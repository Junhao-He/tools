# coding = utf-8 

# @Time : 2020/10/28 15:35
# @Author : 0049003071
# @File : similar_search.py
# @Software: PyCharm

import configparser
from elasticsearch import Elasticsearch
import hnswlib
import numpy as np
import logging
import base64
import struct
import os
import time
import datetime
import json
import codecs
from fdfs_client.client import Fdfs_client, get_tracker_conf


def get_data_from_es(history_table, host, doc_type, query):
    """根据时间读取es数据
    Arg:
        history_table (:string), 数据表名
        host (:list), 数据所在host
        doc_type (:string), index对应type
        query (:list), 时间查询条件
    Return:
        es_data: 全量数据
        searched_data: 满足质量分阈值待比对数据
        es_feature: 全量特征
        searched_feature: 满足质量分阈值待比对特征
    """
    try:
        try:
            es = Elasticsearch(host)
        except Exception as e:
            # print(e)
            logging.error(e)
        indices = [history_table]
        doc = {
            "_source": ['uuid', 'img_url', 'rt_feature', 'quality_score'],
            "query": {
                "bool": {
                    "must": [{"range": {"enter_time": {"gte": query[0], "lte": query[1],
                                                       "format": "yyyy-MM-dd HH:mm:ss", "time_zone": "+08:00"}}}
                             # {"range": {"quality_score": {"gte": quality_threshold}}}
                             ]
                }
            }
        }

        # Initialize the scroll
        page1 = es.search(
            index=','.join(indices),
            doc_type=doc_type,
            scroll='2m',
            # search_type='scan',
            sort='_doc',
            size=1000,
            body=doc
        )
        sid = page1['_scroll_id']
        scroll_size = page1['hits']['total']
        # print('total scroll_size: ', scroll_size)

        es_data = []
        es_feature = []
        searched_data = []
        searched_feature = []
        docs = page1['hits']['hits']
        label_index = 0
        query_index = 0
        for message in docs:
            try:
                message = message['_source']
                if message['quality_score'] > quality_threshold1 or \
                        (quality_threshold2 < message['quality_score'] < 1):
                    # message_info['index'] = query_index
                    searched_data.append(message)
                    searched_feature.append(base64_to_float(message['rt_feature']))
                    query_index += 1
                es_data.append(message)
                es_feature.append(base64_to_float(message['rt_feature']))
                label_index += 1
            except:
                # print("Data error! {}".format(label_index))
                logging.error("Data error! {}".format(label_index))
        # Start scrolling
        while scroll_size > 0:
            # print ("Scrolling...")
            page = es.scroll(scroll_id=sid, scroll='2m')
            # Update the scroll ID
            sid = page['_scroll_id']
            # Get the number of results that we returned in the last scroll
            scroll_size = len(page['hits']['hits'])
            # print ("scroll size: " + str(scroll_size))
            # Do something with the obtained page
            docs = page['hits']['hits']
            for message in docs:
                try:
                    message = message['_source']
                    if message['quality_score'] > quality_threshold1 or \
                            (quality_threshold2 < message['quality_score'] < 1):
                        # message_info['index'] = query_index
                        searched_data.append(message)
                        searched_feature.append(base64_to_float(message['rt_feature']))
                        query_index += 1
                    es_data.append(message)
                    es_feature.append(base64_to_float(message['rt_feature']))
                    label_index += 1
                except KeyError as e:
                    # print("Field missing!")
                    logging.error(e)
                except Exception as e:
                    # print(e)
                    logging.error(e)

        # print('Data amount: {}'.format(len(es_data)))
        logging.info('Data amount: {}'.format(len(es_data)))
        # print('Query data amount:{}'.format(len(searched_data)))
        logging.info('Query data amount:{}'.format(len(searched_data)))
        return es_data, searched_data, es_feature, searched_feature
    except ConnectionError as e:
        logging.error(e)
        # print("Error in downloading data!")


def base64_to_float(kb_feature):
    # 二进制特征转化为float矩阵
    feature = base64.b64decode(kb_feature)
    float_out = []
    if len(feature) >= 2060:
        for i in range(12, 12 + 512 * 4, 4):
            x = feature[i:i + 4]
            float_out.append(struct.unpack('f', x)[0])
    return float_out


def cos(f1, f2):
    # 计算向量余弦相似度
    return np.dot(f1, f2) / (np.linalg.norm(f1) * np.linalg.norm(f2))


def annsearch(data, data_labels, neighbor_amount):
    """利用HNSW进行近似最近邻搜索
    Arg:
        data (:list), 待比对向量列表
        data_labels (:list), 检索库向量列表
    Return:
        labels: 相似向量索引
        distances: 相似评分
    """
    data1 = np.array(data, dtype=np.float)
    data_labels1 = np.array(data_labels, dtype=np.float)
    embed_len, embed_dim = data_labels1.shape
    raw_labels = np.arange(embed_len)
    ann = hnswlib.Index(space='cosine', dim=embed_dim)
    ann.init_index(max_elements=embed_len, ef_construction=200, M=16)
    ann.add_items(data_labels1, raw_labels)
    ann.set_ef(100)
    labels, distances = ann.knn_query(data1, k=neighbor_amount)
    return labels, distances


def download_fdfs(dst_dir_list, fdfs_dir_list):
    """下载fdfs文件至本地
    Arg:
        dst_dir_list (:list), fdfs文件保存至本地对应文件名list
        fdfs_dir_list (:list), fdfs文件list
    """
    tracker_path = get_tracker_conf('./fdfs_client.conf')
    client = Fdfs_client(tracker_path)
    for i in fdfs_dir_list:
        fdfs_dir = i
        dst_dir = dst_dir_list[i]
        if type(fdfs_dir) == bytes:
            client.download_to_file(dst_dir, fdfs_dir)
        elif type(fdfs_dir) == str:
            client.download_to_file(dst_dir, bytes(fdfs_dir, encoding='utf-8'))
        else:
            logging.warning('fdfs_dir is illegal!')
            # print('fdfs_dir is illegal!')
    # print('Download completed!')
    logging.info('Download completed!')


def save2json(dic, filename):
    """保存字典变量至本地
    Arg:
        dic (:dict), 待保存字典数据
        filename (:string), 保存文件名称
    """
    data = json.dumps(dic)
    try:
        with codecs.open(filename, 'a', 'utf-8') as file:
            file.write(data)
    except:
        logging.error("Failed!")
        # print("Failed！")


def save_files(labels, distances, es_data, search_data, thr, dst_dir):
    """保存相似搜索结果图片及信息至本地
    Arg:
        labels (:list), 相似向量索引
        distances (:list), 相似向量距离
        es_data (:list), 全量数据
        search_data (:list), 待比对数据
        thr (:float), 相似距离过滤阈值
        dst_dir (:string), 存储路径
    Return:
        labels: 相似向量索引
        distances: 相似评分
    """
    dir_today = dst_dir + '/' + time.strftime("%Y-%m-%d", time.localtime())
    if not os.path.exists(dir_today):
        os.mkdir(dir_today)
    infos = dict()
    tracker_path = get_tracker_conf('./fdfs_client.conf')
    client = Fdfs_client(tracker_path)
    for index in range(len(labels)):
        if index % 100 == 0:
            logging.info("Downloading files :{}".format(index))
            print("Downloading files :{}".format(index))
        label = labels[index]
        distance = distances[index]
        uuid = search_data[index]['uuid']
        infos[uuid] = []
        index_dir = dir_today + '/' + uuid
        if not os.path.exists(index_dir):
            os.mkdir(index_dir)
        for i in range(len(distance)):
            # ss = time.time()
            if distance[i] < thr:
                try:
                    infos[uuid].append(es_data[label[i]])
                    # print(es_data[label[i]]['uuid'])
                    client.download_to_file(index_dir + '/' + es_data[label[i]]['uuid'] + '.jpg',
                                            bytes(es_data[label[i]]['img_url'][10:], encoding="utf8"))
                except:
                    # print("Downloading error! {}".format(es_data[label[i]]['uuid']))
                    continue
                # finally:
                #     print("Downloading costs: {}s".format(time.time() - ss))
    save2json(infos, dir_today + '/' + time.strftime("%Y-%m-%d", time.localtime()) + '.json')


def get_date_today():
    today = datetime.date.today()
    yesterday = today - datetime.timedelta(days=1)
    return [str(yesterday) + ' 00:00:00', str(today) + ' 00:00:00']


if __name__ == '__main__':
    s_time = time.time()

    LOG_FORMAT = "%(asctime)s %(name)s %(levelname)s %(message)s "  # 配置输出日志格式
    DATE_FORMAT = '%Y-%m-%d  %H:%M:%S'  # 配置输出时间的格式
    if not os.path.exists("./log"):
        os.mkdir("./log")
    logging.basicConfig(level=logging.INFO,
                        format=LOG_FORMAT,
                        datefmt=DATE_FORMAT,
                        filename=r"./log/similarity_search.log"
                        )

    config = configparser.ConfigParser()
    # print("- Load config file")
    logging.info("- Load config file")
    config.read("./config.ini")
    history_table = config['ES.org']['history_table']
    host = config['ES.org']['host']
    doc_type = config['ES.org']['type']
    query = ['2020-10-20 06:10:21', '2020-10-30 06:10:21']
    # query = get_date_today()
    quality_threshold1 = float(config['INFO.org']['quality_threshold_1'])
    quality_threshold2 = float(config['INFO.org']['quality_threshold_2'])
    neighbor_amount = int(config['INFO.org']['neighbor_amount'])
    distance_threshold = float(config['INFO.org']['distance_threshold'])
    dst_dir = config['INFO.org']['dst_dir']

    es_data, search_data, label_feature, search_feature = get_data_from_es(history_table, host, doc_type, query)
    read_time = time.time()
    # print("Reading file costs: {}s".format(read_time - s_time))
    logging.info("Reading file costs: {}s".format(read_time - s_time))
    if len(search_data) > 0:
        labels, distances = annsearch(search_feature, label_feature, neighbor_amount)
        # print(labels[:10])
        # print(distances[:10])
        search_time = time.time()
        # print("Searching costs: {}s".format(search_time - read_time))
        logging.info("Searching costs: {}s".format(search_time - read_time))
        # print(distances[:10])
        save_files(labels, distances, es_data, search_data, distance_threshold, dst_dir)
        download_time = time.time()
        # print("Downloading costs: {}s".format(download_time - search_time))
        logging.info("Downloading costs: {}s".format(download_time - search_time))
    else:
        logging.warning("No data get!!!")
        # print("No data get!!!")
