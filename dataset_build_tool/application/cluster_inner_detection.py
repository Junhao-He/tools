"""
区内混入和一人分多类检测
Created by 0049003121 2020/03/25
"""
import os
from common.file_operation import read_dir, read_files, random_select_face, detect_romove_file
from config.config_operation import ConfigOperation
from utils.fusion import compute_inner_fused_feature, detect_inner_fuse


# 包含该现象则表明肯定混入，目前一种规则（图片特性），后续可添加各种属性检测
def detect_misclassification(input_path):
    if not os.path.exists(input_path):
        print("该路径不存在，请重新输入...:")
        exit()
    else:
        dir_list = read_dir(input_path)
        for path in dir_list:
            info_id = {}
            file_list = read_files(path)
            for file in file_list:
                pic_id = file.split('-')[-2]
                if pic_id in info_id:
                    print(path+":存在图片混入现象...")
                    break
                else:
                    info_id[pic_id] = 1


# 检测可能的混入现象
def detect_possible_misclassification(input_path, delta=200):
    if not os.path.exists(input_path):
        print("该路径不存在，请重新输入...:")
        exit()
    else:
        dir_list = read_dir(input_path)
        for path in dir_list:
            info_id = []
            file_list = read_files(path)
            for file in file_list:
                pic_id = int(file.split('-')[-2])
                info_id.append(pic_id)
                min_value = min(info_id)
                max_value = max(info_id)
                if max_value > min_value + delta:
                    print("可能存在误分类现象的为：{}".format(path))
                    break


if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')
    params = ConfigOperation.read_config_file(config_file)
    print("-----------1.区内异常检测---------------------")
    input_path = params['face.cluster.inner.output.path']
    dir_list = [os.path.join(path, "img") for path in read_dir(input_path)]
    for path in dir_list:
        detect_misclassification(path)
    for path in dir_list:
        detect_possible_misclassification(path, 300)

    print("\n-----------2.人工辅助扫描图片，确保无混入------------\n")
    print("------------3.计算每类融合特征-------------------------\n")
    for path in dir_list:
        current_path_id = path.split("\\")[-2]
        feature_info_path = os.path.join(params['face.info.initial.path'], current_path_id)

        compute_inner_fused_feature(path, feature_info_path, os.path.join(input_path, current_path_id,
                                                                          "info"))
    print(".........特征融合信息计算完成......")
    print("\n-----------4.降低阈值，聚类筛选出候选队列，人工辅助判别------------")
    thresholds = [0.92, 0.85, 0.80]
    dir_list_info = [os.path.join(path, "info") for path in read_dir(input_path)]
    cluster_config_file = os.path.join(current_path, '..\\config\\ann.cfg')
    cluster_params = ConfigOperation.read_config_file(cluster_config_file)
    cluster_params['ann.cluster.n_neighbors'] = int(cluster_params['ann.cluster.n_neighbors'])
    cluster_params['ann.cluster.trees'] = int(cluster_params['ann.cluster.trees'])
    for dir_path in dir_list_info:
        print("路径：{}".format(dir_path))
        for threshold in thresholds:
            print("阈值为：{}".format(threshold))
            detect_inner_fuse(os.path.join(dir_path, "fused_info.txt"), cluster_params, threshold)

    print("\n-----------5.随机选择每类中的一张图片，按属性划分到不同文件夹-------")
    random_select_face("E:\\video\\inner_cluster\\1\\img", "E:\\video\\inner_cluster\\1\\result_one")
    # 可根据属性再次划分到文件

    print("\n-----------6.删除4个及以下的文件--------------------")
    detect_romove_file("E:\\cluster_result\\6\\img")

    print("\n------------7.计算每类融合特征-------------------------")
    compute_inner_fused_feature("E:\\video\\inner_cluster\\1\\img", "E:\\video\\face_info\\1",
                                "E:\\inner_cluster\\1\\info")


