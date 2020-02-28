# coding = utf - 8
"""
    hjh@2019.12.02
    对数据集进行进一步过滤，减少一人分多类的情况，优化数据集。
"""

import os
import numpy as np
from elasticsearch import Elasticsearch
import shutil
import configparser
from ..tools import cos, standardize
from ..tools import base64_to_float


def get_data_from_es(history_table, host, doc_type):
    es = Elasticsearch(host)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [history_table]
    doc = {
        "query": {
            "bool": {
                "must": [ { }]}}
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
    l = []
    docs = page1['hits']['hits']
    l += [x['_source'] for x in docs]

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
        l += [x['_source'] for x in docs]

    global amount
    amount = len(l)
    print('Data amount: ', amount)
    info_dict = {}
    for i in l:
        info_dict[i["img_url"].split('\\')[-1]] = i["rt_feature"]
    return info_dict


def feature_compare(feature_dict):
    white_list = []
    for fused_id in feature_dict:
        if len(feature_dict[fused_id]) == 2:
            if cos(feature_dict[fused_id][0], feature_dict[fused_id][1]) > 0.57:
                white_list.append(fused_id)
        else:
            false_count = 0
            for i in range(len(feature_dict[fused_id])):
                for j in range(i):
                    if cos(feature_dict[fused_id][0], feature_dict[fused_id][1]) <= 0.57:
                        false_count += 1
            if false_count < len(feature_dict[fused_id])-1:
                white_list.append(fused_id)
    print("White_list: {}".format(len(white_list)))
    return white_list


def img_dataset_merge(id_list, uuid_dict):
    for id in os.listdir(src_dir)[:]:
        id_dir = os.path.join(src_dir, id)
        f_id = list(filter(lambda k: id in uuid_dict[k], uuid_dict))
        if len(f_id) > 0 and f_id[0] in id_list:
            print("Merge!")
            for i in os.listdir(id_dir):
                i_dir = os.path.join(id_dir, i)
                if not os.path.isfile(i_dir):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    # 分离文件名和路径
                    # img_path1 = i_dir.split('\\')[-2]
                    img_path2 = i_dir.split('\\')[-1]
                    fpath = dst_dir+'\\'+f_id[0]
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                # 创建路径
                    shutil.copyfile(i_dir, dst_dir+'\\'+f_id[0]+'\\'+img_path2)      # 复制文件
        else:
            for i in os.listdir(id_dir):
                i_dir = os.path.join(id_dir, i)
                if not os.path.isfile(i_dir):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    # 分离文件名和路径
                    img_path1 = i_dir.split('\\')[-2]
                    img_path2 = i_dir.split('\\')[-1]
                    fpath = dst_dir+'\\'+img_path1
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                # 创建路径
                    shutil.copyfile(i_dir, dst_dir+'\\'+img_path1+'\\'+img_path2)      # 复制文件


# 根据聚类结果对fused_id包含多个label的情况进行过滤，找出一人分多类的情况
def find_same_person_with_multi_fusedid(dir):
    person_dict = {}
    feature_dict = {}
    info_dict = get_data_from_es(config['ES.org']['history_table'], config['ES.org']['host'],config['ES.org']['type'])
    # for key in info_dict:
    #     print(key, info_dict[key])
    cc = 0
    for fused_id in os.listdir(dir)[:]:
        cc += 1
        person_dict[fused_id] = []
        feature_dict[fused_id] = []
        fused_dir = os.path.join(dir, fused_id)
        print(cc)
        for uuid in os.listdir(fused_dir):
            img_list = []
            uuid_dir = os.path.join(fused_dir, uuid)
            for img in os.listdir(uuid_dir):
                img_dir = os.path.join(uuid_dir, img)
                # img_feature = get_feature_form_st(img_dir)
                # try:
                img_feature = base64_to_float(info_dict[img_dir.split('\\')[-1]])
                img_list.append(img_feature)
                # except:
                #     print("Image not exist!")
                # print(img_dir)

            standardized_feature = standardize(np.array(img_list).mean(axis=0)).tolist()
            feature_dict[fused_id].append(standardized_feature)
            person_dict[fused_id].append(uuid)
    # print(feature_compare(feature_dict))
    img_dataset_merge(feature_compare(feature_dict), person_dict)


def dataset_filter():
    global config, src_dir, dst_dir
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./dataset_filter_config.ini")
    input_dir = config['INFO.org']['input_dir']
    src_dir = config['INFO.org']['src_dir']
    dst_dir = config['INFO.org']['dst_dir']
    find_same_person_with_multi_fusedid(input_dir)


if __name__ == '__main__':
    dataset_filter()
    # config = configparser.ConfigParser()
    # print("- Load config file")
    # config.read("./dataset_filter_config.ini")
    #
    # input_dir = config['INFO.org']['input_dir']
    # src_dir = config['INFO.org']['src_dir']
    # dst_dir = config['INFO.org']['dst_dir']
    # find_same_person_with_multi_fusedid(input_dir)