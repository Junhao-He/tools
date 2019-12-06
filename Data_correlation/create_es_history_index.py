# coding=utf-8
"""
    hjh@2019.11.25
    本地图片发送商汤服务器获取特征写入ES
"""

import requests, json, base64, struct
import os
import numpy as np
from elasticsearch import Elasticsearch,helpers
import uuid
from multiprocessing.dummy import Pool as Pool
import multiprocessing
import random
import time


# 创建要写入的ES表
def create_es_index():
    es = Elasticsearch(ip)
    if not es.indices.exists(index=index):
        mappings = {
            "mappings": {
                "history_data": {
                    "_field_names": {
                        "enabled": "false"
                    },
                    "_all": {
                        "enabled":"false"
                    },
                    "properties": {
                        "gender": {
                            "type": "integer"
                        },
                        "op_time": {
                            "type": "date"
                        },
                        "roll": {
                            "index": "false",
                            "type": "float"
                        },
                        "coarse_id": {
                            "type": "keyword"
                        },
                        "track_idx": {
                            "index": "false",
                            "type": "keyword"
                        },
                        "uuid": {
                            "index": "false",
                            "type": "keyword"
                        },
                        "frame_index": {
                            "index":"false",
                            "type": "integer"
                        },
                        "right_pos": {
                            "index": "false",
                            "type": "integer"
                        },
                        "office_id": {
                            "type": "keyword"
                        },
                        "is_alarm": {
                            "type": "keyword"
                        },
                        "rt_feature": {
                            "type": "feature",
                            "doc_values": "true"
                        },
                        "task_idx": {
                            "index": "false",
                            "type": "keyword"
                        },
                        "lib_id": {
                            "type": "integer"
                        },
                        "top": {
                            "index": "false",
                            "type": "integer"
                        },
                        "mouth_open": {
                            "type": "integer"
                        },
                        "similarity": {
                            "type": "float"
                        },
                        "rowkey": {
                            "type": "keyword"
                        },
                        "fused_id": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "ignore_above": 256,
                                    "type": "keyword"
                                }
                            }
                        },
                        "pitch": {
                            "index": "false",
                            "type": "float"
                        },
                        "camera_name": {
                            "type": "keyword"
                        },
                        "mask": {
                            "type": "integer"
                        },
                        "person_id": {
                            "type": "keyword"
                        },
                        "big_picture_uuid": {
                            "type": "keyword"
                        },
                        "glass": {
                            "type": "integer"
                        },
                        "leave_time": {
                            "type": "date"
                        },
                        "beard": {
                            "type": "integer"
                        },
                        "img_width": {
                            "index": "false",
                            "type": "integer"
                        },
                        "race": {
                            "type": "integer"
                        },
                        "bottom": {
                            "index": "false",
                            "type": "integer"
                        },
                        "img_height": {
                            "index": "false",
                            "type": "integer"
                        },
                        "quality_score": {
                            "type": "float"
                        },
                        "camera_id": {
                            "type": "keyword"
                        },
                        "camera_type": {
                            "type": "integer"
                        },
                        "birth": {
                            "type": "keyword"
                        },
                        "eye_open": {
                            "type": "integer"
                        },
                        "person_name": {
                            "type": "keyword"
                        },
                        "enter_time": {
                            "type": "date"
                        },
                        "left_pos": {
                            "index": "false",
                            "type": "integer"
                        },
                        "yaw": {
                            "index": "false",
                            "type": "float"
                        },
                        "office_name": {
                            "type": "keyword"
                        },
                        "control_event_id": {
                            "type": "keyword"
                        },
                        "emotion": {
                            "type": "integer"
                        },
                        "img_url": {
                            "index": "false",
                            "type": "keyword"
                        },
                        "duration_time": {
                            "type": "long"
                        },
                        "age": {
                            "type": "integer"
                        }
                    }
                }
             }
        }
        print("Index creating success!")
        res = es.indices.create(index=index, body=mappings)
        return res
    else:
        print("Index has already existed!")
        return None


def cos(f1, f2):
    return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))


def get_feature_from_st(pic,http="http://10.45.157.115:80/verify/feature/gets"):
    body = open(pic,'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    # return base64Tofloat(aaa['feature'])
    return aaa['feature']


def base64_to_float(kb_feature):
    feather = base64.decodestring(kb_feature)
    floatout = []
    for i in range(12,12 + 512 * 4, 4):
        x = feather[i:i + 4]
        floatout.append(struct.unpack('f', x)[0])
    return floatout


# 构造数据字段信息
def insert_data(uuid, feature, img_dir, number):
    es = Elasticsearch(ip+":9200")
    action = {
        "enter_time": "2019-11-25T11:11:00.000Z",
        "uuid": str(uuid),
        "camera_id": "64010600001310000066",
        "office_name": "智慧城市人脸布控",
        "img_url": img_dir,
        "big_picture_uuid": str(uuid),
        "person_id": number,
        # "coarse_id": coarse_id,
        "rt_feature": feature
        # "person_id": str(number).zfill(5),
    }
    es.index(index=index, doc_type="history_data", body=action)


def create_test_data():
    t1 = 0
    for person_dir in os.listdir(dir)[:]:
        person_dir = os.path.join(dir, person_dir)
        t2 = 0
        for img_dir in os.listdir(person_dir):
            img_dir = os.path.join(person_dir, img_dir)
            try:
                feature = get_feature_from_st(img_dir)
                feature = str(feature)
                # uu = 'HJH'+str(t1)+str(t2)
                # uu_str = uuid.uuid3(uuid.NAMESPACE_DNS, uu.encode('utf-8'))
                uu_str = uuid.uuid1()
                insert_data(uu_str, feature, img_dir, t1)
                t2 += 1
            except Exception as e:
                print (Exception, ":", e)
                continue
        t1 += 1
        # else:
        #     print('Data is ready!')
        #     break


# 多进程实现数据写入，已废弃
# def create_test_data_multiprocessing():
#     global item
#     global person_dir
#     global actions
#     item = 0
#     for person_dir in os.listdir(dir)[:]:
#         person_dir = os.path.join(dir, person_dir)
#         pool = Pool()
#         pool.map(process_img, os.listdir(person_dir))
#         pool.close()
#         pool.join()
#         item += 1
#
#
# def process_img(img_dir):
#     img_dir = os.path.join(person_dir, img_dir)
#     try:
#         feature = getFeatureST(img_dir)
#         feature = str(feature)
#         # uu = 'HJH'+str(t1)+str(t2)
#         # uu_str = uuid.uuid3(uuid.NAMESPACE_DNS, uu.encode('utf-8'))
#         uu_str = uuid.uuid1()
#         insert_data(uu_str, feature, img_dir, item)
#     except Exception as e:
#         print (Exception, ":", e)


# 多进程写入ES改进版
def create_test_data_multiprocessing2():
    # global person_dir
    # global actions
    pool = Pool(8)
    for i in os.listdir(dir)[:]:
        # print(i)
        pool.apply_async(process_img2, (i,), callback=sum_actions)
    pool.close()
    pool.join()
    es = Elasticsearch(['10.45.154.206'], port=9200)
    print("action_sum: "+str(len(action_sum)))
    helpers.bulk(es, action_sum)


def process_img2(person_dir):
    global error
    person_dir = os.path.join(dir, person_dir)
    actions = []
    for img_dir in os.listdir(person_dir):
        # print(img_dir)
        img_dir = os.path.join(person_dir, img_dir)
        try:
            # action = {}
            feature = get_feature_from_st(img_dir)
            feature = str(feature)
            # uu = 'HJH'+str(t1)+str(t2)
            # uu_str = uuid.uuid3(uuid.NAMESPACE_DNS, uu.encode('utf-8'))
            uu_str = uuid.uuid1()
            t = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())).split(' ')
            action = {
                "_index": index,
                "_type": type,
                "_source": {
                    "enter_time": t[0]+'T'+t[1]+'.000Z',
                    "uuid": str(uu_str),
                    "camera_id": "64010600001310000066",
                    "office_name": "智慧城市人脸布控",
                    "img_url": img_dir,
                    "big_picture_uuid": str(uuid),
                    "person_id": random.randint(0, 10000),
                    # "coarse_id": coarse_id,
                    "rt_feature": feature
                }
            }
            actions.append(action)
        except Exception as e:
            error += 1
            # print(Exception, ":", e)
            print(img_dir)
            continue
    # print("actions: "+str(len(actions)))
    return actions


def sum_actions(actions):
    global action_sum
    print("action_Sum: "+str(len(action_sum)))
    action_sum += actions


if __name__ == '__main__':
    s_time = time.time()
    ip = "10.45.154.206"
    index = "history_fss_data_15w_test_data"
    type = "history_data"
    # dir = "D:\\Dataset\\Asian"
    dir = "D:\\Dataset\\test_dataset_15w"
    action_sum = []
    error = 0
    print(multiprocessing.cpu_count())
    create_es_index()
    create_test_data_multiprocessing2()
    e_time = time.time()
    print("error: "+str(error))
    print("Time cost: {}".format(e_time-s_time))
