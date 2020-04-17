# coding = utf-8

import requests
import json
import base64
import cv2
import os
import glob
from time import sleep
import struct
import shutil
import numpy as np


# 调用接口提取Picasso特征
def picasso_feature(img_url, http='http://10.45.154.218:9010/trajectory/reid/feature'):
    content = {"imageData": open(img_url, 'rb')}
    reply = requests.post(http, files=content)
    # print(reply._content)
    response = json.loads(reply.text)
    # print(response)
    return response['data'][0]['name'], response['data'][0]['result']


# 特征转换
def base64_to_float(kb_feature):
    feature = base64.b64decode(kb_feature)
    # print(len(feature))
    float_out = []
    for i in range(12, 2060, 4):
        x = feature[i:i + 4]
        float_out.append(struct.unpack('<f', x)[0])
    return float_out


def get_feature_array(file_dir):
    fail_num = 0
    feature_dict = {}
    picture_list = glob.glob(file_dir)
    print(len(picture_list))
    for i in picture_list:
        name, result = picasso_feature(i)
        if result:
            feature_dict[name] = result[0]
        else:
            fail_num += 1
    with open('./feature_dict_picasso.txt', 'a') as f:
        f.write(json.dumps(feature_dict))
    print("Number of failures: {}".format(fail_num))
    print("Processing completed")


# 比对查找最近邻
def nn_search(data, f_array, thr):
    result_dis = {}
    result_ind = {}
    for key in data:
        dis = []
        index = []
        if data[key]['quality'] > thr:
            print(key)
            for j in range(len(f_array)):
                if len(dis) < k_neighbors:
                    dis.append(euclidean(base64_to_float(data[key]['feature']), f_array[j]))
                    if len(dis) == k_neighbors:
                        index = list(np.argsort(dis))
                        dis.sort()
                else:
                    item = euclidean(base64_to_float(data[key]['feature']), f_array[j])
                    if item < dis[k_neighbors-1]:
                        ind = find_index(dis, item, k_neighbors)
                        dis.insert(ind, item)
                        dis = dis[:k_neighbors]
                        index.insert(ind, j)
                        index = index[:k_neighbors]
            result_dis[key] = dis
            result_ind[key] = index
            # print(dis)
    return result_dis, result_ind
    # return result_ind


def find_index(dist, dis, k_neighbors):
    for i in range(k_neighbors-1):
        if dis >= dist[k_neighbors-2-i]:
            return k_neighbors-1-i
    return 0


# 计算特征欧式距离
def euclidean(f1, f2):
    return np.square(np.linalg.norm(np.array(f1)-np.array(f2)))


# 获取picasso特征
def picasso_compare(feature, feature_list, http='http://10.45.154.218:9010/verify/feature/comparison'):
    content = {"feature1": feature, "feature2": feature_list}
    reply = requests.post(http, data=content)
    print(reply._content)
    response = json.loads(reply.text)
    # print(response)
    return response


# 保存Picasso比对结果
def save_picasso_compare_results(label):
    data = json.load(open('./feature_dict_picasso.txt'))
    name_array = []
    feature_array = []
    name_dict = {}
    for key in data:
        name_array.append(key)
        feature_array.append(base64_to_float(data[key]['feature']))
    print(len(feature_array))
    dis_dict, index_dict = nn_search(data, feature_array, 0.9)
    for key in index_dict:
        name_list = []
        for i in index_dict[key]:
            name_list.append(name_array[i])
        name_dict[key] = name_list
    with open('./distance_dict_'+label+'.json', 'a') as f:
        f.write(json.dumps(dis_dict))
    with open('./name_dict_'+label+'.json', 'a') as f:
        f.write(json.dumps(name_dict))


# 保存qst人体图片对比结果到本地
def save_qst_compare_results(data_dir):
    distance_dict_qst = {}
    name_dict_qst = {}
    with open (data_dir, 'r') as f:
        distance_matrix = f.readlines()
        image_array = distance_matrix[0].split('\t')[:-1]
        print(len(image_array))
        count = 0
        for i in distance_matrix[1:]:
            print(count)
            image_index = []
            distance_index = []
            distance_array = np.array(list(map(float, i.split('\t')[:-1])))
            index = distance_array.argsort()[::-1]
            index = index[:k_neighbors]
            # print(index)
            for j in index:
                image_index.append(image_array[j]+'.jpg')
                distance_index.append(distance_array[j])
            # print(image_index)
            # print(distance_array)
            name_dict_qst[image_array[count]+'.jpg'] = image_index
            # print(image_index[0])
            distance_dict_qst[image_array[count]+'.jpg'] = distance_index
            count += 1
    # print(name_dict_qst)
    # print(distance_dict_qst)
    save_file = './name_dict_qst.json'
    print(len(name_dict_qst))
    with open(save_file, 'a') as f:
        f.write(json.dumps(name_dict_qst))
    save_file = './distance_dict_qst.json'
    with open(save_file, 'a') as f:
        f.write(json.dumps(distance_dict_qst))


# 拷贝图片
def save_image(key, image_name):
    src_dir = ".\\ReID_test\\small_pic_picasso"+'\\'+image_name
    dst_dir = ".\\ReID_test\\"+label+'_search2\\'+key[:-4]+'\\'+image_name
    shutil.copyfile(src_dir, dst_dir)
    # sleep(1)


# 保存比对结果图片至本地
def save_images(name_dict):
    with open ('./picasso_pre.txt', 'r') as f:
        file = f.readlines()
        test_name = []
        for i in file:
            test_name.append(i.split(' ')[0])
    # print(test_name)
    if not os._exists(".\\ReID_test") :
        os.mkdir('.\\ReID_test')
    if not os._exists(".\\ReID_test\\"+label+"_search2"):
        os.mkdir(".\\ReID_test\\"+label+"_search2")
    for key in name_dict:
        if key[:-4] in test_name:
            os.mkdir(".\\ReID_test\\"+label+"_search2\\"+key[:-4])
            print(key)
            print(name_dict[key])
            for i in name_dict[key]:
                sleep(1)
                save_image(key, i)


def main():
    global k_neighbors, label
    k_neighbors = 6
    label = 'qst'


if __name__ == '__main__':
    main()
    # qst_distance_file = './qst_distance_matrix.txt'
    # save_qst_compare_results(qst_distance_file)


    # source_dir = r'D:\Dataset\ReID_test\small_pic_picasso'
    # dir = source_dir + '\\*.jpg'
    # get_feature_array(dir)

    # save_picasso_compare_results(label)
    #
    # data = json.load(open('./name_dict_'+label+'.json'))
    # save_images(data)

    # data = json.load(open('./feature_dict_qst.txt'))
    # feature_qst = {}
    # features = []
    # for key in data:
    #     features.append(data[key]['feature'])
    # feature_qst['features'] = features
    # for i in features:
    #     picasso_compare(i, json.dumps(feature_qst))

