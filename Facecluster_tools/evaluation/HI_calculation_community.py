# coding = utf - 8
"""
    hjh@2019.6.25
    从ES读取源数据及聚类结果，对聚类结果按阈值计算相应精确性
    聚类结果按簇保存源图片至本地（可选）
    此版本适用于社区陌生人聚类，融合ID及特征位于同一张表
"""
import os
from urllib import request
import json
from ..esClient import get_data_from_es
from ..tools import base64_to_float, cos


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
                fea_list.append(base64_to_float(i.encode(encoding='utf-8')))
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
                fea_list.append(base64_to_float(i.encode(encoding='utf-8')))
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
        if len(cluster[key]) > 2:
            save_img(key, cluster[key],data_count)


if __name__ == '__main__':
    # 输入
    # data_count = '34w'
    formula = 1
    timestamp = ["2018-06-09 12:00:00", "2018-06-10 12:00:00"]
    thr = [0.656, 0.613, 0.57, 0.53, 0.52, 0.48]
    # thr = [0.656]

    index_name = "history_data_sq_hi"
    data_count = 'sq'
    result = get_data_from_es(index_name, "10.45.152.155", 'history_data')
    # print(result[0][0])
    feature = get_personid_list(result)
    save_results(feature, thr, data_count, formula)