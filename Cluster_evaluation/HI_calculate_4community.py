#coding = utf - 8
"""
    hjh@2019.6.25
    从ES读取源数据及聚类结果，对聚类结果按阈值计算相应精确性
    聚类结果按簇保存源图片至本地（可选）
    此版本适用于社区陌生人聚类，融合ID及特征位于同一张表
"""
import json
import os
from urllib import request
import numpy as np
import base64
import struct
import requests
import time
import json
from elasticsearch import Elasticsearch


def get_data_from_ES(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    #name = 'fused_src_data_nightowl_ycys_34w_92'
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
    print('total scroll_size: ', scroll_size)
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

    print('total docs: ', len(l))

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return l, str(len(l)//1000)+'w'


# 根据ES数据，将聚类结果以FusedID为键存入字典
def get_list(data, feature_dict):
    url_list = {}
    fea_list = {}
    fused_id_list = {}
    feature_list = {}
    for i in data[0]:
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
    return url_list, fea_list


def get_personid_list(data):
    feature_list = {}
    for i in data[0]:
        if i['person_id'] not in feature_list:
            feature = []
            feature.append(i['rt_feature'])
            feature_list[i['person_id']] = feature
        else:
            feature_list[i['person_id']].append(i['rt_feature'])
    fea_list = {}
    for i in feature_list:
        fea_list[i] = list(set(feature_list[i]))
    return fea_list

def get_feature_of_url(data):
    d = {}
    for i in data:
        d[i['img_url']] = i['rt_feature']
    return d


# 从120:8089把图像存在本地以供人工校验
def save_img(dir, url_list, d):
    path_header = "./cluster_result_"+d+"/"
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


def getFeatureST(pic,http="http://10.45.157.115:9001/verify/feature/gets"):
    body = open(pic,'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    feature = base64Tofloat(aaa['feature'])# .encode(encoding = 'utf-8')
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
    feather = base64.b64decode(kb_feature)
    floatout = []
    if len(feather)>=2060:
        for i in range(12,12 + 512 * 4, 4):
            x = feather[i:i + 4]
            floatout.append(struct.unpack('f', x)[0])
    return floatout


# 太慢，已废弃 这个是调用商汤服务器即时计算特征
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
                #print(y)
                fea_list.append(y)
            except :
                print ('error')
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
                        if cos(fea_list[i],fea_list[j])<0.656:
                            break_flag = False
                            print(cos(fea_list[i],fea_list[j]))
                            break
                if break_flag == False:
                    break
            if  break_flag == True:
                pure += 1
        p_time = time.time()
        print((p_time-c_time))
        sum += 1
        print(sum)
    return pure,sum


# 计算相似性（Precision） 将小于阈值的结果存入json
def calculate_similarity(feature_list, th, sign):
    sum = 0
    one_count = 0
    pure = 0
    outlier = {}
    if sign == 1:
        for key in feature_list:
            fea_list = []
            for i in feature_list[key]:
                # print(base64Tofloat(i.encode(encoding='utf-8')))
                fea_list.append(base64Tofloat(i.encode(encoding='utf-8')))
            if len(fea_list) == 1:
                # print(key)
                one_count += 1
            elif len(fea_list)==2:
                if cos(fea_list[0],fea_list[1]) >=th:
                    pure += 1
            else:
                break_flag = True
                n = len(fea_list)
                for i in range(n):
                    for j in range(n):
                        if( i<j):
                            # if cos(fea_list[i],fea_list[j])<0.656:
                            if cos(fea_list[i],fea_list[j])<th:
                                break_flag = False
                                # print(key)
                                # print(cos(fea_list[i],fea_list[j]))
                                outlier[key] = cos(fea_list[i],fea_list[j])
                                break
                    if not break_flag:
                        break
                if break_flag:
                    pure += 1
            sum += 1
    elif sign == 2:
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
                if cos(fea_list[0],fea_list[1]) >= th:
                    pure += 2
            else:
                break_flag = True
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
                                simi += cos(fea_list[i],fea_list[j])
                if false_count > sign-1:
                    outlier[key] = simi/false_count
                else:
                    pure += n
        # print(sum)
    return pure,sum,one_count,outlier


# 精确性计算，异常FusedID存入本地文件
def save_results(feature, thr, data_count, sign):
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
            file = open('ycys_'+data_count+'_outlier_'+str(th)+'.json','w')
            file.write(json.dumps(outlier))
            file.close()
        print("Alls:{} Ones:{} Data_Interested:{}".format(alls, ones, alls-ones))
        f.write('Data_count:'+str(alls)+'\n')
        f.write('Cluster_num_beyond_one:'+str(alls-ones))
    # return alls,ones


# 聚类结果图片按簇存入本地文件夹
def save_imgs(cluster, data_count):
    # with open(cluster_url,'a')as f:
    #     json.dump(cluster, f, indent=2)
    for key in cluster:
        if len(cluster[key])>2:
            save_img(key,cluster[key],data_count)


if __name__ == '__main__':
    # 输入
    # data_count = '34w'
    formula = 1
    timestamp = ["2018-06-09 12:00:00", "2018-06-10 12:00:00"]
    thr = [0.656, 0.613, 0.57, 0.53, 0.52, 0.48]
    # thr = [0.656]

    # index_name1 = "fused_src_data_nightowl_0415_kmeans"
    # index_name2 = "fss_history_yc_without_nullfeature"
    #
    # result = get_data_from_ES("10.45.157.120", index_name1, 'fused')
    # fea_result, data_count = get_data_from_ES("10.45.157.120", index_name2, 'history_data', timestamp)
    # # print(data_count)
    # feature_dict = get_feature_of_url(fea_result)
    # cluster, feature = get_list(result, feature_dict)
    # # 精确性计算，异常FusedID存入本地文件，最后一个参数选择精确性计算方案
    # save_results(feature, thr, data_count, formula)
    # # 聚类结果图片按簇存入本地文件夹，不用可注释
    # # save_imgs(cluster, data_count)

    index_name = "history_data_sq_hi"
    data_count = 'sq'
    result = get_data_from_ES(index_name, "10.45.152.155", 'history_data')
    # print(result[0][0])
    feature = get_personid_list(result)
    save_results(feature, thr, data_count, formula)