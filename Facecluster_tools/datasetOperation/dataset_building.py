# coding = utf - 8
"""
    hjh@2019.10.10
    根据银川人脸数据构建用于测试的标准数据集
"""
import os
from urllib import request
import numpy as np
import base64
import struct
import requests
import json
import shutil
import re
import configparser
from ..esClient import get_data_from_es


# 根据ES数据，将聚类结果以FusedID为键存入字典
def get_list(data, feature_dict):
    url_list = {}
    fea_list = {}
    fused_id_list = {}
    feature_list = {}
    cluster_num = 0
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
        if len(fea_list[i]) > 1:
            cluster_num += 1
    return url_list, fea_list, cluster_num


def get_feature_of_url(data):
    d = {}
    for i in data:
        d[i['img_url']] = i['feature']
    return d


def get_feature_and_url(fused_data, src_data):
    d_f = {}
    d_url = {}
    fused_id = []
    for i in fused_data[0]:
        fused_id.append(i['fused_id'])
    for i in src_data:
        if i['fused_id'] in fused_id:
            if i['fused_id'] not in d_f:
                feature_list = []
                url_list = []
                feature_list.append(i['feature'])
                url_list.append(i['img_url'])
                d_f[i['fused_id']] = feature_list
                d_url[i['fused_id']] = url_list
            else:
                d_f[i['fused_id']].append(i['feature'])
                d_url[i['fused_id']].append(i['img_url'])
    return d_f, d_url


# 从120:8089把图像存在本地以供人工校验
def save_img(dir, url_list, d):
    path_header = "./cluster_result_"+d+"/"
    path = path_header+dir
    if not os.path.exists(path):
        os.makedirs(path)
    for i in url_list:
        img_url = "http://10.45.157.120:8098/"+i[:2]+'/'+i[2:4]+'/'+i+'.jpg'
        request.urlretrieve(img_url,path+'/'+i+'.jpg')


def save_img2(dir, url_list):
    path = "D:\\Test_result\\imgs_"+data_count+"\\"+dir
    if not os.path.exists(path):
        os.makedirs(path)
    for i in url_list:
        pic_name = re.findall(r"\\([0-9a-z]{32}\.jpg)", i)[0]
        shutil.copyfile(i, path+'\\'+pic_name)


def normalize(score):
    # src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.58, 1.0]
    src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57,1.0]
    # dst_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.85, 0.95, 1.0]
    dst_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
    src_len = len(src_points)
    if score <= src_points[0]:
        return 0.0
    elif score >= src_points[src_len - 1]:
        return 1.0
    result = 0.0
    for i in range(1, src_len):
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


def pure_fused(feature_list, th, url_dict):
    pure_list = []
    url_selected = {}
    for key in feature_list:
        fea_list = []
        for i in feature_list[key]:
            fea_list.append(base64Tofloat(i.encode(encoding='utf-8')))
        if len(fea_list) == 2:
            if cos(fea_list[0], fea_list[1]) >= th:
                pure_list.append(key)
        elif len(fea_list) > 2:
            n = len(fea_list)
            false_count = 0
            for i in range(n):
                for j in range(n):
                    if i < j:
                        # if cos(fea_list[i],fea_list[j])<0.656:
                        if cos(fea_list[i], fea_list[j]) < th:
                            false_count += 1
                if false_count > n-1:
                    break
            if false_count < n-1:
                pure_list.append(key)
    for i in pure_list:
        if i in url_dict:
            url_selected[i] = url_dict[i]
    return url_selected


def pre_cal(pure, interested, data_amount, cluster_amount):
    precision = pure/interested*(1-abs(cluster_amount-0.46*data_amount)/data_amount)
    return '{:.2%}'.format(precision)


# 聚类结果图片按簇存入本地文件夹
def save_imgs(cluster, data_count):
    # with open(cluster_url,'a')as f:
    #     json.dump(cluster, f, indent=2)
    for key in cluster:
        if len(cluster[key]) > 1:
            save_img2(key, cluster[key])


def build_dataset():
    global data_count
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./dataset_filter_config.ini")

    thr = config['INFO.org']['thr']
    index_name1 = config['ES.org']['index1']
    index_name2 = config['ES.org']['index2']
    ip = config['ES.org']['host']
    result = get_data_from_es(index_name1, ip, 'fused', ['1', '1000'])
    fea_result, data_count = get_data_from_es(index_name2, ip, 'fused')
    # print(data_count)
    feature_dict, url_dict = get_feature_and_url(result, fea_result)
    feature, cluster_num = feature_dict, len(feature_dict)

    # 精确性计算，异常FusedID存入本地文件，最后一个参数选择精确性计算方案
    url_selected = pure_fused(feature, thr[0], url_dict)
    print("The num of cluster selected: {}".format(len(url_selected)))
    # 聚类结果图片按簇存入本地文件夹，不用可注释
    save_imgs(url_selected, data_count)


if __name__=='__main__':
    build_dataset()