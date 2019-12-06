# coding = utf - 8
"""
    hjh@2019.6.26
    从ES读取源数据及聚类结果，对聚类结果按阈值计算相应查全率
    此版本适用于社区陌生人聚类，融合ID及数据路径位于同一张src表
"""

from elasticsearch import Elasticsearch
from elasticsearch import helpers
import json
import re
import os
import shutil
import time
import requests
import base64
import numpy as np
import struct
import configparser
from nmi_calculate import calculate_nmi


def get_data_from_es(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [name]
    if query == [] :
        doc = {
            "query": {
                "bool": {
                    # "must": [ { "range": { "enter_time": { "gt": "2018-06-01T11:19:29.000Z","lt": "2018-06-04T11:19:29.000Z"}}}]}}
                    "must": [ { }]}}
        }
    else:
        doc = {
            "query": {
                "bool": {
                    #"must": [ { "range": { "enter_time": { "gte": query[0],"lte": query[1],"time_zone":"+08:00"}}}]}}
                    "must": [ { "range": { "enter_time": {"gte": query[0],"lte": query[1],"format":"yyyy-MM-dd HH:mm:ss","time_zone":"+08:00"}}}]}}
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

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return l, str(len(l)//1000)+'w'


def read_es(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [name]
    if query == [] :
        doc = {
            "query": {
                "bool": {
                    # "must": [ { "range": { "enter_time": { "gt": "2018-06-01T11:19:29.000Z","lt": "2018-06-04T11:19:29.000Z"}}}]}}
                    "must": [ { }]}}
        }
    else:
        doc = {
            "query": {
                "bool": {
                    #"must": [ { "range": { "enter_time": { "gte": query[0],"lte": query[1],"time_zone":"+08:00"}}}]}}
                    "must": [ { "range": { "enter_time": {"gte": query[0],"lte": query[1],"format":"yyyy-MM-dd HH:mm:ss","time_zone":"+08:00"}}}]}}
        }
    # Initialize the scroll

    scan_result = helpers.scan(es, query=doc, scroll='60m', index=indices)
    l = [x['_source'] for x in scan_result]

    # Start scrolling
    # while scroll_size > 0:
    #     # print ("Scrolling...")
    #     page = es.scroll(scroll_id=sid, scroll='2m')
    #     # Update the scroll ID
    #     sid = page['_scroll_id']
    #     # Get the number of results that we returned in the last scroll
    #     scroll_size = len(page['hits']['hits'])
    #     # print ("scroll size: " + str(scroll_size))
    #     # Do something with the obtained page
    #     docs = page['hits']['hits']
    #     l += [x['_source'] for x in docs]

    print('total docs: ', len(l))

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return l, str(len(l)//1000)+'w'


def data_prepare(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['person_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['small_picture_path'])
            data_id[i['person_id']] = url
        else:
            data_id[i['person_id']].append(i['small_picture_path'])
        spp = re.findall(r"\[.*\]", i['small_picture_path'])[0]
        if spp not in data_url:
            id = []
            id.append(i['person_id'])
            data_url[spp] = id
        else:
            data_url[spp].append(i['person_id'])
    return data_id, data_url


def data_prepare_personid(result):
    data_fid = {}
    data_pid = {}
    for i in result[0]:
        if i['fused_id'] not in data_fid:
            id = []
            # url.append(i['small_picture_path'][-15:-8])
            id.append(i['person_id'])
            data_fid[i['fused_id']] = id
        else:
            data_fid[i['fused_id']].append(i['person_id'])
        if i['person_id'] not in data_pid:
            fid = []
            fid.append(i['fused_id'])
            data_pid[i['person_id']] = fid
        else:
            data_pid[i['person_id']].append(i['fused_id'])
    print(len(data_fid), len(data_pid))
    return data_fid, data_pid


def data_prepare_url(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['fused_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['img_url'])
            data_id[i['fused_id']] = url
        else:
            data_id[i['fused_id']].append(i['img_url'])
        # if i['img_url'][17:22] not in data_url:
        label = i['img_url'].split('\\')[-2]
        if label not in data_url: # 53:89 for yc 50:57 for casia
            id = []
            id.append(i['fused_id'])
            data_url[label] = id
        else:
            data_url[label].append(i['fused_id'])
    return data_id, data_url


def calculate_recall(data):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    for i in data:
        if len(set(data[i])) == 1:
            pure_sum += len(data[i])
            pure_cluster += 1
            # print(i)
        sum += len(data[i])
    recall = pure_sum/sum
    print("pure_recall: {}; sum: {}".format(pure_cluster, len(data)))
    print("pure_sum: {}; sum: {}".format(pure_sum, sum))
    return recall


def cos(f1, f2):
    return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))


def getFeatureST(pic, http="http://10.45.157.115:9001/verify/feature/gets"):
    body = open(pic, 'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    feature = base64Tofloat(aaa['feature'])#.encode(encoding = 'utf-8')
    # print(feature)
    return feature
    # return aaa['feature']


def base64Tofloat(kb_feature):
    # feather = base64.b16decode(kb_feature)
    feature = base64.b64decode(kb_feature)
    floatout = []
    if len(feature) >= 2060:
        for i in range(12, 12 + 512 * 4, 4):
            x = feature[i:i + 4]
            floatout.append(struct.unpack('f', x)[0])
    return floatout


def calculate_feature_and_similarity(path):
    sum = 0
    pure = 0
    for x in os.listdir(path)[:]:
        print(x)
        s_time = time.time()
        print(s_time)
        x = os.path.join(path, x)
        fea_list = []

        for y in os.listdir(x):
            y = os.path.join(x, y)
            try:
                y = getFeatureST(y)
                # print(y)
                fea_list.append(y)
            except :
                print ('error')
                continue
        c_time = time.time()
        print((c_time-s_time))
        if len(fea_list)==2:
            if cos(fea_list[0], fea_list[1]) >=0.656:
                pure += 1
            else:
                print(cos(fea_list[0], fea_list[1]))
        else:
            break_flag = True
            n = len(fea_list)
            for i in range(n):
                for j in range(n):
                    if (i < j):
                        if cos(fea_list[i], fea_list[j])<0.656:
                            break_flag = False
                            print(cos(fea_list[i], fea_list[j]))
                            break
                if break_flag == False:
                    break
            if  break_flag == True:
                pure += 1
        p_time = time.time()
        print((p_time-c_time))
        sum += 1
        print(sum)
    return pure, sum


def calculate_pre(data):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    sum_all = 0
    for key in data:
        # print(data[key][0])
        if len(data[key][0]) != 5:
            serials = []
            for i in data[key]:
                # serial = re.findall(r"\[.*\]", i)[0]
                serial = i.split('\\')[-2]
                serials.append(serial)
            de_serials = list(set(serials))
            # print(de_serials)
            if len(serials) > 1:
                if len(de_serials) == 1 :
                    pure_sum += len(serials)
                    pure_cluster += 1
                sum += len(serials)
            sum_all += len(serials)
        else:
            if len(data[key]) > 1:
                if len(set(data[key])) == 1:
                    pure_sum += len(data[key])
                    pure_cluster += 1
                    # print(i)
                sum += len(data[key])
            sum_all += len(data[key])
    pre = pure_sum/sum
    print("pure_pre: {}; sum: {}".format(pure_cluster, len(data)))
    print("pure_sum: {}; sum: {}".format(pure_sum, sum))
    return pre


def calculate_pre_with_singleton(data, amount):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    sum_all = 0
    for key in data:
        # print(data[key][0])
        if len(data[key][0]) != 5:
            serials = []
            for i in data[key]:
                # serial = re.findall(r"\[.*\]", i)[0]
                serial = i.split('\\')[-2]
                serials.append(serial)
            de_serials = list(set(serials))
            # print(de_serials)
            if len(serials) > 1:
                if len(de_serials) == 1 :
                    pure_sum += len(serials)
                    pure_cluster += 1
                sum += len(serials)
            sum_all += len(serials)
        else:
            if len(data[key]) > 1:
                if len(set(data[key])) == 1:
                    pure_sum += len(data[key])
                    pure_cluster += 1
                    # print(i)
                sum += len(data[key])
            sum_all += len(data[key])
    # pre = pure_sum/sum
    # print("pure_pre: {}; sum: {}".format(pure_cluster, len(data)))
    # print("pure_sum: {}; sum: {}".format(pure_sum, sum))
    pre_plus = pure_sum/amount
    return pre_plus


def save_img(data, dst_file):
    for key in data:
        if len(data[key]) > 1:
            for i in data[key]:
                if not os.path.isfile(i):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    #分离文件名和路径
                    imgpath1 = i.split('\\')[-2]
                    imgpath2 = i.split('\\')[-1]
                    fpath = dst_file+key+'\\'+imgpath1
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                #创建路径
                    shutil.copyfile(i, dst_file+key+'\\'+imgpath1+'\\'+imgpath2)      #复制文件
                    # print("copy %s -> %s"%(srcfile, dstfile))
    print("Saving images is complete!")


def save_img2(data, dst_file, src_file):
    # local_dir = 'C:\\Users\\0049003071\\Desktop\\fsdownload\\chongq-190314-171738 2019-03-13~2019-03-14\\cq_data\\'
    for key in data:
        if len(data[key]) > 1:
            for i in data[key]:
                # print(i[72:])
                # i = src_file + i[90:]
                i = src_file + i[90:]
                if not os.path.isfile(i):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    #分离文件名和路径
                    imgpath1 = i.split('\\')[-2]
                    imgpath2 = i.split('\\')[-1]
                    fpath = dst_file+key
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                #创建路径

                    shutil.copyfile(i, dst_file+key+'\\'+imgpath2)      #复制文件
                    # print("copy %s -> %s"%(srcfile, dstfile))
    print("Saving images is complete!")


def save_multiple_imgs(data, dst_file):
    for key in data:
        if len(data[key]) > 1:
            labels = []
            for item in data[key]:
                labels.append(item.split('\\')[-2])
            labels = set(labels)
            if len(labels) > 1:
                for i in data[key]:
                    if not os.path.isfile(i):
                        print("%s not exist!"%(i))
                    else:
                        # fpath, fname = os.path.split(dstfile)    # 分离文件名和路径
                        img_path1 = i.split('\\')[-2]
                        img_path2 = i.split('\\')[-1]
                        fpath = dst_file+key+'\\'+img_path1
                        if not os.path.exists(fpath):
                            os.makedirs(fpath)                # 创建路径
                        shutil.copyfile(i, dst_file+key+'\\'+img_path1+'\\'+img_path2)      # 复制文件
    print("Saving images is complete!")

if __name__ == '__main__':
    # thr = '0.92'
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./cluster_evaluation_with_label_config.ini")
    print(config['ES.org']['fused_src_table'])
    result = get_data_from_es(config['ES.org']['fused_src_table'], config['ES.org']['host'], config['ES.org']['type'])
    # result = get_data_from_ES("fused_src_data_cq_lf_1", "10.45.157.120", 'fused')
    # data_id, data_url = data_prepare_url(result)


    # json_url = 'recall_' + config['ES.org']['fused_src_table'].split('_')[-2]+"_" +\
    #            config['ES.org']['fused_src_table'].split('_')[-1] + '.json'
    # json_id = 'pre_' + thr + '.json'
    # with open(json_url, 'w') as f:
    #     json.dump(data_url, f, indent=2)
    # with open(json_id, 'w') as f:
    #     json.dump(data_id, f, indent=2)

    dst_dir = "D:\\Test_result\\"+config['ES.org']['fused_src_table'].split('_')[-3]+"_" +\
              config['ES.org']['fused_src_table'].split('_')[-2]+"_" + config['ES.org']['fused_src_table'].split('_')[-1] + "\\"
    # save_multiple_imgs(data_id, dst_dir)
    # save_img(data_id, dst_dir)

    # print('threshold：'+config['INFO.org']['thr'])
    # precision = calculate_pre(data_id)
    #
    # recall = calculate_recall(data_url)
    # print('Precision: {:.2%}'.format(precision))
    # print('Recall {:.2%}'.format(recall))
    # print('F1 Score: {:.2%}'.format(precision*recall*2/(precision+recall)))
    # print('Pre+: {:.2%}'.format(calculate_pre_with_singleton(data_id, amount)))

    print('NMI: {:.2%}'.format(calculate_nmi(result)))
