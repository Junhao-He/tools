# coding:utf-8
"""
    hjh@2019.3.8
    从ES读取源数据及聚类结果，对聚类结果按阈值计算相应精确性
    聚类结果按簇保存源图片至本地（可选）
    PS：精确性计算方案1：簇中出现相似性小于阈值的图片对则视该簇为错误分类；
        精确性计算方案2：簇中图片数为2，计算同方案1；图片数为N（N>2）时，相似性小于阈值的图片对多于N-1对时才视为错误分类；
"""
import random
import os
from urllib import request
import numpy as np
import base64
import struct
import requests
import time
import json
from elasticsearch import Elasticsearch
import configparser
from multiprocessing.dummy import Pool as Pool
import multiprocessing


def get_data_from_ES(name, doc_type, query=[]):
    es = Elasticsearch(config['ES.org']['host'])
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
                    # "must": [ { "range": { "enter_time": { "gte": query[0],"lte": query[1],"time_zone":"+08:00"}}}]}}
                    "must": [{"range": {"enter_time": {"gte": query[0],"lte": query[1],"format":"yyyy-MM-dd HH:mm:ss","time_zone":"+08:00"}}}]}}
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
        docs = page['hits']['hits']
        l += [x['_source'] for x in docs]

    print('total docs: ', len(l))

    # ES数据写入json文件，暂时不用
    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    # return l, str(len(l)//10000)+'w'
    return l


# 根据ES数据，将聚类结果以FusedID为键存入字典
def get_list(data, feature_dict):
    url_list = {}
    fea_list = {}
    fused_id_list = {}
    feature_list = {}
    cluster_num = 0
    for i in data:
        if i['fused_id'] not in fused_id_list:
            url = []
            url.append(i['img_url'])
            fea_url = []
            fea_url.append(feature_dict[i['img_url']])
            fused_id_list[i['fused_id']] = url
            feature_list[i['fused_id']] =fea_url
        else:
            fused_id_list[i['fused_id']].append(i['img_url'])
            feature_list[i['fused_id']].append(feature_dict[i['img_url']])
    for i in fused_id_list:
        url_list[i] = list(set(fused_id_list[i]))
    for i in feature_list:
        fea_list[i] = list(set(feature_list[i]))
        if len(fea_list[i]) > 1:
            cluster_num += 1
    return url_list, fea_list, cluster_num


def get_feature_of_url(data):
    d = {}
    for i in data:
        d[i['img_url']] = i['rt_feature']
    return d


# 从120:8089把图像存在本地以供人工校验
def save_img(dir, url_list):
    path_header = "./cluster_result/"
    path = path_header+dir
    if not os.path.exists(path):
        os.makedirs(path)
    for i in url_list:
        img_url = "http://10.45.157.120:8098/"+i[:2]+'/'+i[2:4]+'/'+i+'.jpg'
        request.urlretrieve(img_url,path+'/'+i+'.jpg')


def normalize(score):
    # src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.58, 1.0]
    src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57,1.0]
    # dst_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.85, 0.95, 1.0]
    dst_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
    srcLen = len(src_points)
    if score <= src_points[0]:
        return 0.0
    elif score >= src_points[srcLen - 1]:
        return 1.0
    result = 0.0
    for i in range(1, srcLen):
        if score < src_points[i]:
            result = dst_points[i - 1] + (score - src_points[i - 1]) * (dst_points[i] - dst_points[i - 1]) / (
                src_points[i] - src_points[i - 1])
            break
    return result


def cos(f1, f2):
    return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))


def getFeatureST(pic, http="http://10.45.157.115:9001/verify/feature/gets"):
    body = open(pic, 'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    feature = base64Tofloat(aaa['feature']) #.encode(encoding = 'utf-8')
    # print(feature)
    return feature
    # return aaa['feature']


def base64Tobyte(codes):
    try:
        fea = base64.decodestring(codes)
    except:
        fea = base64.b64decode(codes)
    return fea


def base64Tofloat(kb_feature):
    # feather = base64.b16decode(kb_feature)
    feature = base64.b64decode(kb_feature)
    float_out = []
    if len(feature) >= 2060:
        for i in range(12,12 + 512 * 4, 4):
            x = feature[i:i + 4]
            float_out.append(struct.unpack('f', x)[0])
    return float_out


# 太慢已废弃 调用商汤服务器即时计算特征
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
                fea_list.append(y)
            except:
                print('error')
                continue
        c_time = time.time()
        print((c_time-s_time))
        if len(fea_list) == 2:
            if cos(fea_list[0],fea_list[1]) >= 0.656:
                pure += 1
            else:
                print(cos(fea_list[0],fea_list[1]))
        else:
            break_flag = True
            n = len(fea_list)
            for i in range(n):
                for j in range(n):
                    if (i < j):
                        if cos(fea_list[i],fea_list[j]) < 0.656:
                            break_flag = False
                            print(cos(fea_list[i], fea_list[j]))
                            break
                if not break_flag:
                    break
            if break_flag:
                pure += 1
        p_time = time.time()
        print((p_time-c_time))
        sum += 1
        print(sum)
    return pure, sum


# 计算相似性（Precision） 将小于阈值的结果存入json
def calculate_similarity(feature_list, th, sign):
    sum = 0
    one_count = 0
    pure = 0
    outlier = {}
    sign = 2
    if sign == 1:
        for key in feature_list:
            fea_list = []
            for i in feature_list[key]:
                fea_list.append(base64Tofloat(i.encode(encoding='utf-8')))
            if len(fea_list) == 1:
                one_count += 1
            elif len(fea_list) == 2:
                if cos(fea_list[0],fea_list[1]) >= th:
                    pure += 1
            else:
                break_flag = True
                n = len(fea_list)
                for i in range(n):
                    for j in range(n):
                        if(i < j):
                            # if cos(fea_list[i], fea_list[j])<0.656:
                            if cos(fea_list[i], fea_list[j]) < th:
                                break_flag = False
                                # print(key)
                                # print(cos(fea_list[i],fea_list[j]))
                                outlier[key] = cos(fea_list[i], fea_list[j])
                                break
                    if not break_flag:
                        break
                if break_flag:
                    pure += 1
            sum += 1
    else:
        for key in feature_list:
            fea_list = []
            for i in feature_list[key]:
                fea_list.append(base64Tofloat(i.encode(encoding='utf-8')))
            if len(fea_list) == 1:
                # print(key)
                one_count += 1
                sum += 1
            elif len(fea_list) == 2:
                sum += 2
                if cos(fea_list[0], fea_list[1]) >= th:
                    pure += 2
            else:
                n = len(fea_list)
                sum += n
                false_count = 0
                simi = 0
                for i in range(n):
                    for j in range(n):
                        if (i < j):
                            # if cos(fea_list[i],fea_list[j])<0.656:
                            if cos(fea_list[i],fea_list[j]) < th:
                                false_count += 1
                                simi += cos(fea_list[i], fea_list[j])
                if false_count > sign-1:
                    outlier[key] = simi/false_count
                else:
                    pure += n
        # print(sum)
    return pure, sum, one_count, outlier


# 计算加权相似性（Precision） 将小于阈值的结果存入json
def calculate_similarity_with_weight(feature_list, th, sign, model=0):
    sum = 0
    pure = 0
    outlier = {}
    # print("Length of feature_list:"+str(len(feature_list)))
    for key in feature_list:
        fea_list = []
        for i in feature_list[key]:
            fea_list.append(base64Tofloat(i.encode(encoding='utf-8')))
        if model == 0:
            if len(fea_list) == 2:
                sum += 2
                if cos(fea_list[0], fea_list[1]) >= th:
                    pure += 2
            elif len(fea_list) > 2:
                n = len(fea_list)
                sum += n
                false_count = 0
                simi = 0
                for i in range(n):
                    for j in range(n):
                        if i < j:
                            # if cos(fea_list[i],fea_list[j])<0.656:
                            if cos(fea_list[i], fea_list[j]) < th:
                                false_count += 1
                                simi += cos(fea_list[i], fea_list[j])
                    if false_count > sign-1:
                        break
                if false_count > sign-1:
                    outlier[key] = simi/false_count
                else:
                    pure += n
        elif model == 1:
            if len(fea_list) == 2:
                sum += 1
                if cos(fea_list[0], fea_list[1]) >= th:
                    pure += 1
            elif len(fea_list) > 2:
                n = len(fea_list)
                sum += 1
                false_count = 0
                simi = 0
                for i in range(n):
                    for j in range(n):
                        if i < j:
                            # if cos(fea_list[i],fea_list[j])<0.656:
                            if cos(fea_list[i], fea_list[j]) < th:
                                false_count += 1
                                simi += cos(fea_list[i], fea_list[j])
                    if false_count > sign-1:
                        break
                if false_count > sign-1:
                    outlier[key] = simi/false_count
                else:
                    pure += 1
    return pure, sum, outlier


# 精确性计算，异常FusedID存入本地文件
def save_results1(feature, thr, data_count, sign):
    ones = 0
    alls = 0
    print("**********Result:***********")
    with open('ycys_'+data_count+'_result.json', 'w') as f:
        f.write('Threshold'+'\t'+'Pure_cluster_num'+'\t'+'Precision'+'\n')
        for i in thr:
            th = i
            p, s, o, outlier = calculate_similarity(feature, th, sign)
            ones = o
            alls = s
            print(th,p)
            f.write(str(th)+'\t'+str(p)+'\t'+str(p/(alls-ones)))
            f.write('\n')
            file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json', 'w')
            file.write(json.dumps(outlier))
            file.close()
        print("Alls:{} Ones:{} Data_Interested:{}".format(alls, ones, alls-ones))
        f.write('Data_count:'+str(alls)+'\n')
        f.write('Cluster_num_beyond_one:'+str(alls-ones))
    # return alls,ones


def save_results2(feature, thr, sign):
    print("sign= {}".format(sign))
    if sign > 0:
        alls = 0
        pre = {}
        print("**********Result:***********")
        for i in thr:
            th = i
            p, s, outlier = calculate_similarity_with_weight(feature, th, sign)
            alls = s
            pre[i] = p
            # print(th,p)
            # url_list = get_mistaken_url_list(result, url_dict, outlier)
            # save_imgs(url_list, str(i))
            # file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json','w')
            # file.write(json.dumps(outlier))
            # file.close()
        sorted(pre.keys())
        return alls, pre
    else:
        alls = 0
        ones = 0
        pre = {}
        print("**********Result:***********")
        for i in thr:
            th = i
            p, s, o, outlier = calculate_similarity(feature, th, 0)
            alls = s
            ones = o
            pre[i] = p
            print(th, p)
            # url_list = get_mistaken_url_list(result, url_dict, outlier)
            # save_imgs(url_list, str(i))
            # file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json','w')
            # file.write(json.dumps(outlier))
            # file.close()
        print("Alls:{} Ones:{} Data_Interested:{}".format(alls, ones, alls-ones))
        # print('data_interested: {}'.format(alls))
        sorted(pre.keys())
        return alls-ones, pre


def save_results3(feature, thr, sign):
    print("sign= {}".format(sign))
    if sign > 0:
        alls = 0
        pre = {}
        print("**********Result:***********")
        for i in thr:
            th = i
            p, s, outlier = calculate_similarity_with_weight(feature, th, sign, 2)
            alls = s
            pre[i] = p
            # print(th,p)
            # url_list = get_mistaken_url_list(result, url_dict, outlier)
            # save_imgs(url_list, str(i))
            # file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json','w')
            # file.write(json.dumps(outlier))
            # file.close()
        sorted(pre.keys())
        return alls, pre
    else:
        alls = 0
        ones = 0
        pre = {}
        print("**********Result:***********")
        for i in thr:
            th = i
            p, s, o, outlier = calculate_similarity(feature, th, 0)
            alls = s
            ones = o
            pre[i] = p
            print(th, p)
            # url_list = get_mistaken_url_list(result, url_dict, outlier)
            # save_imgs(url_list, str(i))
            # file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json','w')
            # file.write(json.dumps(outlier))
            # file.close()
        print("Alls:{} Ones:{} Data_Interested:{}".format(alls, ones, alls-ones))
        # print('data_interested: {}'.format(alls))
        sorted(pre.keys())
        return alls-ones, pre


def pre_cal(pure, interested, data_amount, cluster_amount):
    precision = pure/interested*(1-abs(cluster_amount-0.46*data_amount)/data_amount)
    return '{:.2%}'.format(precision)


# 聚类结果图片按簇存入本地文件夹
def save_imgs(cluster):
    # with open(cluster_url,'a')as f:
    #     json.dump(cluster, f, indent=2)
    for key in cluster:
        if len(cluster[key]) > 2:
            save_img(key, cluster[key])


def get_pictures_by_request(host, url_dict, dir):
    if not os.path.exists(dir):
        os.makedirs(dir)
    for key in url_dict:
        s = time.time()
        if len(url_dict[key]) > 1:
            os.makedirs(dir+key)
            for i in url_dict[key]:
                http = r"http://"+host+r":9008/GetSmallPic?"+i
                try:
                    # s1 = time.time()
                    reply = requests.get(http)
                    with open(dir+key+'/'+i+'.jpg', 'wb') as f:
                        f.write(reply.content)
                    # s2 = time.time()
                    # print("******Save img: "+str(s2 -s1))
                except Exception as e:
                    print(e)
        e = time.time()
        print("Time cost: " + str(e - s))


def get_pictures_by_request_multiprocessing(host, url_dict, dir):
    if not os.path.exists(dir):
        os.makedirs(dir)
    # pool = Pool(multiprocessing.cpu_count())
    pool = Pool(1024)
    for key in url_dict:
        s_t = time.time()
        pool.apply(get_pic, (key,))
        e_t = time.time()
        print("Time cost: "+str(e_t - s_t))


def get_pic(key):
    if len(cluster[key]) > 1:
        os.makedirs(config['PIC.org']['dir']+key)
        for i in cluster[key]:
            http = r"http://"+config['PIC.org']['host']+r":9008/GetSmallPic?"+i
            try:
                s1 = time.time()
                reply = requests.get(http)
                with open(config['PIC.org']['dir']+key+'/'+i+'.jpg', 'wb') as f:
                    f.write(reply.content)
                s2 = time.time()
                print("****Sava img: "+str(s2 - s1))
            except Exception as e:
                print(e)


if __name__ == '__main__':
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./cluster_evaluation_HI_config.ini")

    result = get_data_from_ES(config['ES.org']['history_table'], 'history_data')
    print(len(result))
    # fea_result, data_count = get_data_from_ES(config['ES.org']['history_table'], 'history_data', eval(config['INFO.org']['time_stamp']))
    # print(data_count)
    feature_dict = get_feature_of_url(result)
    cluster, feature, cluster_num = get_list(result, feature_dict)

    # 精确性计算，异常FusedID存入本地文件，最后一个参数选择精确性计算方案

    # save_results1(feature, thr, data_count, formula)
    # print(feature[random.sample(feature.keys(), 1)[0]])
    interested, pre_result = save_results2(feature, eval(config['INFO.org']['thr']), int(config['INFO.org']['formula']))

    if int(config['INFO.org']['formula']) == 0:
        for key in pre_result:
            print(key, pre_result[key], pre_cal(pre_result[key], interested, len(result), len(feature)))
        print('cluster: {}  cluster_interested: {}'.format(len(feature), cluster_num))
    else:
        for key in pre_result:
            print(key, pre_result[key], pre_cal(pre_result[key], interested, len(result), len(feature)))
        print('data: {}  data_interested: {}'.format(len(result), interested))
        print('cluster: {}  cluster_interested: {}'.format(len(feature), cluster_num))

    # 聚类结果图片按簇存入本地文件夹，不用可注释
    # save_imgs(cluster)
    get_pictures_by_request(config['PIC.org']['host'], cluster, config['PIC.org']['dir'])
    # get_pictures_by_request_multiprocessing(config['PIC.org']['host'], cluster, config['PIC.org']['dir'])
