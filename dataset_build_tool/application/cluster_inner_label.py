"""
区内聚类标注
Created by 0049003121 2020/03/25
"""
import datetime
import os
import shutil
import numpy as np
from common.FeatureProcess import FeatureProcess
from config.config_operation import ConfigOperation
from utils.cluster import process_batch_common
from common.file_operation import read_dir, read_files


def read_feature_info(input_path, file_name="feature_info.txt"):
    file = os.path.join(input_path, file_name)
    if not os.path.exists(file):
        print("输入文件路径不存在或输入错误,请重新输入...")
        exit(0)
    else:
        with open(file, "r") as f:
            data = []
            feature_list = []
            for line in f:
                elems = line.split(",")
                data.append({"uuid": elems[0], "img_url": elems[1].replace("\\", "\\\\"), "feature": elems[2],
                             "enter_time": elems[3], "coarse_id": elems[4], "region": elems[1].split("\\")[-2]})
                # print(len(FeatureProcess.base64tofloat(elems[2].encode("utf-8"))))
                feature_list.append(FeatureProcess.base64tofloat(elems[2].encode("utf-8")))
            return data, feature_list

if __name__ == '__main__':
    print("--------------聚类内部标注---------------")
    start = datetime.datetime.now()
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')
    params = ConfigOperation.read_config_file(config_file)
    cluster_config_file = os.path.join(current_path, '..\\config\\ann.cfg')
    cluster_params = ConfigOperation.read_config_file(cluster_config_file)
    cluster_params['ann.cluster.n_neighbors'] = int(cluster_params['ann.cluster.n_neighbors'])
    cluster_params['ann.cluster.trees'] = int(cluster_params['ann.cluster.trees'])
    st_threshold = float(params['st_threshold'])
    dir_list = read_dir(params['face.info.initial.path'])
    output_path = params['face.cluster.inner.output.path']

    print(st_threshold)
    # 目录列表
    for path in dir_list:
        src_list, feature_list = read_feature_info(path)
        cluster_info = process_batch_common((np.array(feature_list), src_list), cluster_params, st_threshold)

        num = 0
        fuseInfo_new_path = os.path.join(output_path, path.split("\\")[-1] + "\\" + "info")
        if not os.path.exists(fuseInfo_new_path):
            os.makedirs(fuseInfo_new_path)

        # f = open(fuseInfo_new_path + "\\fused_info.txt", 'w')
        for cluster_elem in cluster_info:
            num += 1

            src_info = cluster_elem["src_list"]

            length = len(src_info)
            print(str(num)+":"+str(length))
            doc_id = str(length) + "_" + str(num)
            fused_feature = cluster_elem["fused_feature"]

            img_new_path = os.path.join(output_path, path.split("\\")[-1] + "\\" + "img\\"+doc_id)

            if not os.path.exists(img_new_path):
                os.makedirs(img_new_path)
            for i in src_info:
                shutil.copy(i['img_url'], img_new_path)

            # f.write("{},{},{},{}\n".format(doc_id, img_new_path.replace("\\", "\\\\"), length,
            #                              FeatureProcess.float2base64(fused_feature)))

    end = datetime.datetime.now()
    print("-------------聚类内部标注结束，共耗时：{}秒-------------------------".format((end-start).seconds))



