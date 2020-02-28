# coding = utf - 8
"""
    hjh@2019.11.06
    python实现最近邻比对查找，并与FAISS及FLANN结果比对
"""

import numpy as np
import time
from ..tools import euclidean


def find_index(dist, dis, k_neighbors):
    for i in range(k_neighbors-1):
        if dis >= dist[k_neighbors-2-i]:
            return k_neighbors-1-i
    return 0


def nn_search(data, k_neighbors):
    result_dis = {}
    result_ind = {}
    for i in range(len(data)):
        dis = []
        index = []
        # print(i)
        for j in range(len(data)):
            if len(dis) < k_neighbors:
                dis.append(euclidean(data[i], data[j]))
                if len(dis) == k_neighbors:
                    # print(dis)
                    index = list(np.argsort(dis))
                    dis.sort()
                    # print(dis)
                    # print(index)
            else:
                item = euclidean(data[i], data[j])
                if item < dis[k_neighbors-1] :
                    ind = find_index(dis, item, k_neighbors)
                    dis.insert(ind, item)
                    dis = dis[:k_neighbors]
                    index.insert(ind, j)
                    index = index[:k_neighbors]
        result_dis[i] = dis
        result_ind[i] = index
    # return result_dis, result_ind
    return result_ind


def get_data(data):
    data = data.split('\n')
    result = []
    # print(len(data))
    for i in data[:50]:
        i = i.split('|')
        # print(i)
        i = i[1]
        i = i.split(' ')
        item = []
        for j in i:
            item.append(float(j))
        result.append(item)
    return np.array(result)


def deal_with_data(out_file):
    result = {}
    sign1 = False
    sign2 = False
    data = []
    faiss_index = {}
    flann_index = {}
    with open(out_file, 'r', encoding='UTF-8') as file:
        for i in file.readlines():
            if "Data amount" in i:
                data_count = int(i.split(' ')[-1])
                data_and_index = {}
                result[data_count] = data_and_index
                data = []
                faiss_index = {}
                flann_index = {}
                data_and_index["data"] = data
                data_and_index["faiss_index"] = faiss_index
                data_and_index["flann_index"] = flann_index
            if "Faiss Data" in i:
                sign1 = not sign1
            if sign1 and '|' in i:
                i = i.split('|')
                # print(i)
                i = i[1]
                i = i.split(' ')
                item = []
                for j in i:
                    try:
                        item.append(float(j))
                    except:
                        continue
                data.append(item)
            if "Distances" in i:
                sign2 = not sign2
            if sign2 and '|' in i and '.' not in i:
                i = i.split('|')
                ind = i[0].strip('\t')
                # print(ind)
                i = i[1]
                i = i.split(' ')
                item = []
                for j in i:
                    try:
                        item.append(int(j))
                    except:
                        continue
                faiss_index[int(ind)] = item
            if "Time using of Faiss" in i:
                sign2 = not sign2
            if "(" in i:
                i = i[1:-2]
                i = i.split(',')
                try:
                    flann_index[int(i[1])].append(int(i[0]))
                except:
                    flann_index[int(i[1])] = []
                    flann_index[int(i[1])].append(int(i[0]))
    # for key in result:
    #     print(result[key]["flann_index"])
    return result


def algorithm_comparison(result):
    for key in result:
        faiss_differ = 0
        flann_differ = 0
        print("Data amount:{}".format(int(key)))
        start_time = time.time()
        sums = int(key)*k_neighbors
        # for i in result[key]["data"]:
        #     print(i)
        data = np.array(result[key]["data"])
        faiss_index = result[key]["faiss_index"]
        flann_index = result[key]["flann_index"]
        re_ind = nn_search(data, k_neighbors)
        # print(re_ind)
        for key2 in re_ind:
            faiss_differ += k_neighbors-len(set(re_ind[key2]).intersection(set(faiss_index[key2])))
            flann_differ += k_neighbors-len(set(re_ind[key2]).intersection(set(flann_index[key2])))
        end_time = time.time()
        print("Difference of Faiss:%.2f " % (faiss_differ/sums))
        print("Difference of Flann:%.2f " % (flann_differ/sums))
        print("运行时间:%.2f秒" % (end_time-start_time))


def time_statistics(file):
    with open(file, 'r', encoding='UTF-8') as f:
        for i in f.readlines():
            if 'amount' in i:
                print(i)
            if 'time' in i or 'Time' in i:
                print(i)


def algorithm_compare():
    global k_neighbors
    k_neighbors = 5
    file_dir = "./out5_5_3.txt"
    # time_statistics(file_dir)
    data_processed = deal_with_data(file_dir)
    algorithm_comparison(data_processed)


if __name__ == '__main__' :
    algorithm_compare()

    # k_neighbors = 5
    # file_dir = "C:\\Users\\0049003071\\Desktop\\hjh_temp\\faiss4j_poject\\out512_5_1.txt"
    # data_processed = deal_with_data(file_dir)
    # algorithm_comparison(data_processed)
    # file_dir = "C:\\Users\\0049003071\\Desktop\\hjh_temp\\faiss4j_poject\\out256_5.txt"
    # time_statistics(file_dir)