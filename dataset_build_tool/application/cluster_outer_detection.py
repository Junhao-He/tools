"""
区间一人分多类检测
Created by 0049003121 2020/03/26
"""
import os
from common.file_operation import read_dir, read_files, random_select_final_face, detect_romove_file, transfer_merge
from config.config_operation import ConfigOperation
from utils.fusion import detect_final_compare, detect_between_fuse


if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')
    params = ConfigOperation.read_config_file(config_file)
    print("----------------1.类间两两聚类--------------------")
    input_path = params["face.cluster.inner.output.path"]
    dir_list = read_dir(input_path)
    length = len(dir_list) - 1
    new_tuple_list = []
    for elem in range(length):
        new_tuple_list.append((dir_list[elem], dir_list[elem + 1]))

    cluster_config_file = os.path.join(current_path, '..\\config\\ann.cfg')
    cluster_params = ConfigOperation.read_config_file(cluster_config_file)
    cluster_params['ann.cluster.n_neighbors'] = int(cluster_params['ann.cluster.n_neighbors'])
    cluster_params['ann.cluster.trees'] = int(cluster_params['ann.cluster.trees'])

    thresholds = [0.92, 0.85, 0.80]

    for i in new_tuple_list:
        print("\n....{}之间合并聚类....".format(i))
        for threshold in thresholds:
            print("--当前阈值：{}--".format(threshold))
            detect_between_fuse(os.path.join(i[0], 'info', 'fused_info.txt'),
                                os.path.join(i[1], 'info', 'fused_info.txt'), cluster_params, threshold)

    print("\n----------------2.人工筛选合并--------------------")

    print("\n----------------3.合并所有类别到一个文件夹中,清理空文件夹------------------")
    # 迁移到一个文件中
    transfer_merge(input_path, "final_result")
    # 清理空文件夹
    detect_romove_file(os.path.join(input_path, "final_result"))

    print("----------------4.计算最新融合特征,再次聚类合并筛选合并---------------------")
    for threshold in thresholds:
        print("当前阈值为:{}".format(threshold))
        detect_final_compare(params['face.info.initial.path'], os.path.join(input_path, "final_result"),
                             cluster_params, threshold)

    print("----------------5.清理归档---------------------------------")
    # 随机选出一张人脸,针对随机挑选的人脸划分属性信息
    random_select_final_face(os.path.join(input_path, "final_result"), os.path.join(input_path, "result_one"))
    # 删除空文件夹
    detect_romove_file(os.path.join(input_path, "final_result"), 1)
    # 清理全部模糊的

    dir_list = read_dir(os.path.join(input_path, "result_one"))
    # gender_list = ['M' if i.split("\\")[-1] is '男' else 'F' for i in dir_list]
    person_gender_dict = {}
    for id, elem in enumerate(dir_list):
       files = read_files(elem)
       for file in files:
           person_gender_dict[file.split('.')[0]] = 'M' if dir_list[id].split('\\')[-1] == '男' else 'F'

    person_list_path = read_dir(os.path.join(input_path, "final_result"))
    no = 0
    full_info_list = []
    for person_path in person_list_path:
        img_list = read_files(person_path)
        current_path = person_path.split("\\")[-1]
        gender_id = person_gender_dict[current_path]
        for img in img_list:
            full_info_list.append((str(no), gender_id, os.path.join(person_path, img)
                                   , current_path))
            no += 1

    with open(os.path.join(input_path, "person_info.txt"), "w") as f:
        f.write("id,gender,img_url,class_no"+"\n")
        for elem in full_info_list:
            f.write(",".join(i for i in elem) + "\n")