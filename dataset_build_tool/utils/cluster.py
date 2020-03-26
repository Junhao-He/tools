# -*- coding: utf-8 -*-
"""
自聚类工具
"""
import uuid
from datetime import datetime
from common.FeatureProcess import FeatureProcess
import numpy as np
import pytz


# st_similarity = 0.92
# threshold = FeatureProcess.calc_stsimilarity_to_euclidean(st_similarity)


def build_index(dataset, testset, cluster_params):
    import pyflann
    """
    Takes a dataset, returns the "n" nearest neighbors
    """
    # Initialize FLANN
    pyflann.set_distance_type(distance_type='euclidean')
    flann = pyflann.FLANN()
    params = {}
    algorithm_type = cluster_params['ann.cluster.algorithm']
    if algorithm_type == 'kmeans':
        params = flann.build_index(dataset,
                                   algorithm=algorithm_type,
                                   branching=int(cluster_params['ann.cluster.branching']),
                                   iterations=int(cluster_params['ann.cluster.iterations']),
                                   centers_init=cluster_params['ann.cluster.centers_init'],
                                   cb_index=float(cluster_params['ann.cluster.cb_index'])
                                   )

    elif algorithm_type == 'kdtree':
        params = flann.build_index(dataset,
                                   algorithm=algorithm_type,
                                   trees=int(cluster_params['ann.cluster.trees'])
                                   )
    else:
        print("暂不支持kd树和kmeans外的其它参数配置")
        exit(0)

    # print params
    nearest_neighbors, dists = flann.nn_index(testset,
                                              int(cluster_params['ann.cluster.n_neighbors']),
                                              checks=params['checks'])
    print("flann索引建立成功...")
    return nearest_neighbors, dists


def process_batch_common(batch_data, cluster_params, st_threshold):

        threshold = FeatureProcess.calc_stsimilarity_to_euclidean(st_threshold)
        # 批数据自融合
        print("Begin time of process batch:{}".format(datetime.now().strftime("%Y-%m-%d %H:%M:%S")))
        batch_feature_array, batch_info_array = batch_data
        descriptor_matrix = batch_feature_array

        # clusters = clustering.cluster(descriptor_matrix, cluster_params=cluster_params, thresh=[0.2, 0.4, 1, 6, 7, 8])
        nearest_neighbors, dists = build_index(descriptor_matrix, descriptor_matrix, cluster_params = cluster_params)
        clusters = process_batch_index_result(nearest_neighbors, dists, threshold)
        feature_info_with_fuse = []
        # for i, cluster in enumerate(clusters[-1]["clusters"]):
        for key, cluster in clusters.items():
            fused_feature = FeatureProcess.feature_mean([batch_feature_array[int(x)] for x in list(cluster)])
            fused_id = str(uuid.uuid4())
            fused_count = len(cluster)
            tz = pytz.timezone('Asia/Shanghai')
            fused_time = datetime.now(tz)
            src_info = []
            for x in list(cluster):
                src_info.append(batch_info_array[int(x)])
            feature_info_with_fuse.append({"fused_id":fused_id, "fused_feature": fused_feature, "src_list": src_info,
                                           "fused_count": fused_count, "fused_time": fused_time})

        print("End time of process batch:{}".format(datetime.now().strftime("%Y-%m-%d %H:%M:%S")))
        return feature_info_with_fuse


def remove_dict_elem(dict, ele):
        # 控制键不存在时删除报错问题
        if ele in dict:
            dict.pop(ele)
        return dict


def process_batch_index_result(nearest_neighbors, dists, threshold):
        """process batch data cluster index"""
        # nearest_neighbors[np.where(dists > threshold)] = 0x3f3f3f3f
        nn_lookup = {}
        for i in range(nearest_neighbors.shape[0]):
            nn_lookup[i] = nearest_neighbors[i, :][np.where(dists[i, :] <= threshold)]
        result_set = {}
        src_fused_map = {}
        for key, values in nn_lookup.items():
            for value in values:
                new_member_set = set()
                if (key in src_fused_map) or (value in src_fused_map):
                    # Key或value已经有归属的情况
                    old_target_id = 0x3f3f3f3f
                    new_target_id = 0x3f3f3f3f
                    if key in src_fused_map:
                        if value in src_fused_map:
                            old_target_id = src_fused_map[value]
                        new_target_id = src_fused_map[key]
                    else:
                        new_target_id = src_fused_map[value]

                    # data._1,data._2及oldtarget，排除掉newtarget(避免newtarget与key或value重复)
                    merge_id_set = set()
                    merge_id_set.add(key)
                    merge_id_set.add(value)
                    if old_target_id != 0x3f3f3f3f :
                        merge_id_set.add(old_target_id) #将旧门派掌门取出进行合并
                    merge_id_set.discard(new_target_id)

                    # 针对merge_id_set里的每个成员，将其member合并到newtarget中
                    # 并从groupMapSet中移除原先的门派，修改src_fused_map的value值
                    for id in merge_id_set:
                        if id in result_set:
                            id_member = result_set[id]
                            new_member_set.add(id)
                            new_member_set.update(id_member)

                            # 原归属id的成员在src_fused_map中的数据改变其归属
                            for one in id_member:
                                src_fused_map[one] = new_target_id
                            src_fused_map[id] = new_target_id

                            # 元素已合并，去除result里的原有门派
                            result_set = remove_dict_elem(result_set, id)
                        else:
                            new_member_set.add(id)
                            src_fused_map[id] = new_target_id

                    # 加上new_target_id的原有成员(如果存在的话)
                    if new_target_id in result_set:
                        new_member_set.update(result_set[new_target_id])

                    result_set[new_target_id] = new_member_set

                else:
                    # key与value均未有归属，设定key为掌门
                    new_target_id = key
                    if key in result_set:
                        new_member_set = result_set[key]
                    if value in result_set:
                        id_member = result_set[value]
                        new_member_set.update(id_member)
                        # 原归属value的全部改为key
                        for ele in id_member:
                            src_fused_map[ele] = new_target_id

                    new_member_set.add(value)

                    result_set[new_target_id] = new_member_set

                    if key != value:
                        result_set = remove_dict_elem(result_set, value)
                        src_fused_map[value] = new_target_id

        return result_set
