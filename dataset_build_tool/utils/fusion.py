import os
import numpy as np
from common.FeatureProcess import FeatureProcess
from common.file_operation import read_dir, read_files
from utils.cluster import process_batch_common


# 计算区内融合特征
def compute_inner_fused_feature(fig_path, feature_path, output_path):
    with open(os.path.join(feature_path, 'feature_info.txt')) as f:
        fig_feature_dict = {}
        for line in f:
            info_array = line.split(",")
            fig_name = info_array[1].split("\\")[-1]
            fig_feature_dict[fig_name] = FeatureProcess.base64tofloat(info_array[2].encode("utf-8"))

    dir_list = read_dir(fig_path)

    # 路径-融合特征字典
    path_fused_feature_dict = {}
    for dir_path in dir_list:
        if os.listdir(dir_path):
            file_list = read_files(dir_path)
            feature_list = []
            for file in file_list:
                feature_list.append(fig_feature_dict[file])
            fused_feature = FeatureProcess.feature_mean(feature_list)
            path_fused_feature_dict[dir_path] = FeatureProcess.float2base64(fused_feature)

    if not os.path.exists(output_path):
        os.makedirs(output_path)
    with open(os.path.join(output_path, 'fused_info.txt'), 'w') as f:
        for key, value in path_fused_feature_dict.items():
            f.write(key+','+value+'\n')


# 区内合并检测
def detect_inner_fuse(file, cluster_params, st_threshold):
    with open(file) as f:
        feature_list = []
        src_info = []
        for line in f:
            info_arr = line.split(",")
            src_info.append({"path": info_arr[0]})
            feature_list.append(FeatureProcess.base64tofloat(info_arr[1].encode("utf-8")))
        new_result = process_batch_common((np.array(feature_list), src_info), cluster_params, st_threshold)

    for elem in new_result:
        src_info_list = elem["src_list"]
        if len(src_info_list) > 1:
            print(src_info_list)


# 区间段内再次合并
def detect_between_fuse(first_file, second_file, cluster_params, st_threshold):
    feature_list = []
    src_info = []
    with open(first_file) as f:
        for line in f:
            info_arr = line.split(",")
            src_info.append({"path": info_arr[0]})
            feature_list.append(FeatureProcess.base64tofloat(info_arr[1].encode("utf-8")))

    with open(second_file) as f:
        for line in f:
            info_arr = line.split(",")
            src_info.append({"path": info_arr[0]})
            feature_list.append(FeatureProcess.base64tofloat(info_arr[1].encode("utf-8")))

    new_result = process_batch_common((np.array(feature_list), src_info), cluster_params, st_threshold)
    print("---{} and {} 新融合上的图片集：---".format(first_file, second_file))
    for elem in new_result:
        src_info_list = elem["src_list"]
        if len(src_info_list) > 1:
            print(src_info_list)


# 最终融合检测相似性判断
def detect_final_compare(src_input_path, dst_input_path, cluster_params, st_threshold):
    dir_list = read_dir(src_input_path)
    fig_feature_dict = {}
    # 读取原始特征和图片相对地址
    for dir_path in dir_list:
        with open(os.path.join(dir_path, "feature_info.txt")) as f:
            for line in f:
                info_arr = line.split(",")
                fig_feature_dict[info_arr[1].split("\\")[-1]] = \
                    FeatureProcess.base64tofloat(info_arr[2].encode("utf-8"))

    # 读取每一类的图片，计算其融合特征
    class_path_list = read_dir(dst_input_path)
    fused_feature_list = []
    src_info = []
    for class_path in class_path_list:
        feature_list = []
        files = read_files(class_path)
        for file in files:
            feature_list.append(fig_feature_dict[file])
        fused_feature = FeatureProcess.feature_mean(feature_list)
        src_info.append({"path": class_path})
        fused_feature_list.append(fused_feature)

    new_result = process_batch_common((np.array(fused_feature_list), src_info), cluster_params, st_threshold)
    print("最终结果集上候选融合上的图片集：---")
    for elem in new_result:
        src_info_list = elem["src_list"]
        if len(src_info_list) > 1:
            print(src_info_list)