# coding = utf-8 

# @Time : 2020/10/12 16:23
# @Author : 0049003071
# @File : data_prepare.py
# @Software: PyCharm

import configparser
from elasticsearch import Elasticsearch
import hnswlib
import numpy as np
import base64
import struct
import os
import time
import json
import codecs
from fdfs_client.client import Fdfs_client, get_tracker_conf


def get_data_from_es(history_table, host, doc_type, query):
    es = Elasticsearch(host)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [history_table]
    doc = {
        "query": {
            "bool": {
                "must": [{"range": {"enter_time": {"gte": query[0], "lte": query[1],
                                                   "format": "yyyy-MM-dd HH:mm:ss", "time_zone": "+08:00"}}}]}}
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
            message_info = dict()
            message_info['uuid'] = message['uuid']
            message_info['img_url'] = message['fdfs_url']
            message_info['rt_feature'] = message['rt_feature']
            message_info['quality_score'] = message['quality_score']
            message_info['index'] = label_index
            if message['quality_score'] > quality_threshold:
                message_info['index'] = query_index
                searched_data.append(message_info)
                searched_feature.append(base64_to_float(message['rt_feature']))
                query_index += 1
            es_data.append(message_info)
            es_feature.append(base64_to_float(message['rt_feature']))
            label_index += 1
        except:
            print("Data error! {}".format(label_index))

    # es_data += [x['_source'] for x in docs]
    # es_feature += base64_to_float(x['_source']['rt_feature'] for x in docs)

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
                message_info = dict()
                message_info['uuid'] = message['uuid']
                message_info['img_url'] = message['fdfs_url']
                message_info['rt_feature'] = message['rt_feature']
                message_info['quality_score'] = message['quality_score']
                message_info['index'] = label_index
                if message['quality_score'] > quality_threshold:
                    message_info['index'] = query_index
                    searched_data.append(message_info)
                    searched_feature.append(base64_to_float(message['rt_feature']))
                    query_index += 1
                es_data.append(message_info)
                es_feature.append(base64_to_float(message['rt_feature']))
                label_index += 1
            except KeyError:
                print("Field missing!")
            except Exception as e:
                print(e)

        # es_data += [x['_source'] for x in docs]
        # es_feature += base64_to_float(x['_source']['rt_feature'] for x in docs)

    print('Data amount: {}'.format(len(es_data)))
    print('Query data amount:{}'.format(len(searched_data)))
    return es_data, searched_data, es_feature, searched_feature
    # info_dict = {}
    # for i in l:
    #     info_dict[i["img_url"].split('\\')[-1]] = i["rt_feature"]
    # return info_dict


def base64_to_float(kb_feature):
    feature = base64.b64decode(kb_feature)
    float_out = []
    if len(feature) >= 2060:
        for i in range(12, 12 + 512 * 4, 4):
            x = feature[i:i + 4]
            float_out.append(struct.unpack('f', x)[0])
    return float_out


def cos(f1, f2):
    return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))


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
            print('fdfs_dir is illegal!')
    print('Download completed!')


def save2json(dic, filename):
    data = json.dumps(dic)
    try:
        with codecs.open(filename, 'a', 'utf-8') as file:
            file.write(data)
    except:
        print("Failed！")


def save_files(labels, distances, es_data, search_data, thr):
    dir_today = os.getcwd()+'\\'+time.strftime("%Y-%m-%d", time.localtime())
    if not os.path.exists(dir_today):
        os.mkdir(dir_today)
    infos = dict()
    tracker_path = get_tracker_conf('./fdfs_client.conf')
    client = Fdfs_client(tracker_path)
    for index in range(len(labels)):
        label = labels[index]
        distance = distances[index]
        uuid = search_data[index]['uuid']
        infos[uuid] = []
        index_dir = dir_today+'\\'+uuid
        if not os.path.exists(index_dir):
            os.mkdir(index_dir)
        for i in range(len(distance)):
            # ss = time.time()
            if distance[i] < thr:
                try:
                    infos[uuid].append(es_data[label[i]])
                    # print(es_data[label[i]]['uuid'])
                    client.download_to_file(index_dir+'\\'+es_data[label[i]]['uuid']+'.jpg',
                                            bytes(es_data[label[i]]['img_url'], encoding="utf8"))
                except:
                    # print("Downloading error! {}".format(es_data[label[i]]['uuid']))
                    continue
                # finally:
                #     print("Downloading costs: {}s".format(time.time() - ss))
                save2json(infos, dir_today+'\\'+time.strftime("%Y-%m-%d", time.localtime())+'.json')


if __name__ == '__main__':
    s_time = time.time()
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./config.ini")
    history_table = config['ES.org']['history_table']
    host = config['ES.org']['host']
    doc_type = config['ES.org']['type']
    query = ['2020-10-20 06:10:21', '2020-10-30 06:10:21']
    quality_threshold = float(config['INFO.org']['quality_threshold'])
    neighbor_amount = int(config['INFO.org']['neighbor_amount'])
    distance_threshold = float(config['INFO.org']['distance_threshold'])

    es_data, search_data, label_feature, search_feature = get_data_from_es(history_table, host, doc_type, query)
    read_time = time.time()
    print("Reading file costs: {}s".format(read_time - s_time))
    labels, distances = annsearch(search_feature, label_feature, neighbor_amount)
    print(labels[:10])
    print(distances[:10])
    search_time = time.time()
    print("Searching costs: {}s".format(search_time - read_time))
    # print(distances[:10])
    save_files(labels, distances, es_data, search_data, 1)
    download_time = time.time()
    print("Downloading costs: {}s".format(download_time - search_time))






    # print(data1[:10])
    # print(data2[:10])
    # print(len(data2), len(feature2))
    # data11 = np.array([[1, 2, 3, 4, 5],
    #                    [2, 3, 4, 5, 6],
    #                    [1, 3, 5, 7, 9],
    #                    [1, 4, 7, 10, 13]])
    # data22 = np.array([[1, 11, 21, 32, 41],
    #                    [13, 14, 15, 16, 17],
    #                    [4, 5, 6, 7, 8],
    #                    [2, 4, 6, 8, 10]])
    # feature1 = np.array([1,0])
    # feature2 = np.array([[-1,1],
    #                      [1,2],
    #                      [2,3],
    #                      [1,1]])
    # a, b = annsearch(feature1, feature2, neighbor_amount)
    # np.save("./labels.npy", np.array(a, dtype=float))
    # print(a[:5])
    # print(b[:5])

    # ret_download = client.download_to_file('./ttt.jpg', bytes("group1/M01/91/CA/Ci2dpF9fFP-ATbVzAAAgdwHKltc328.jpg", encoding="utf8"))

