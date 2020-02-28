# coding=utf-8
"""
    hjh@2020.02.27
    相似度距离计算、聚类指标计算
"""
import numpy as np
from sklearn import metrics


def cos(f1, f2):
    return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))


def euclidean(f1, f2):
    return np.square(np.linalg.norm(f1-f2))


def standardize(x):
    return (x - np.mean(x))/(np.std(x))


def normalize(score):
    # src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.58, 1.0]
    src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57, 1.0]
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


def calculate_nmi(data):
    label_true = []
    label_pred = []
    fused_list = []
    url_list = []
    count_f = 0
    count_u = 0
    for i in data[0]:
        if i['fused_id'] not in fused_list:
            fused_list.append(i['fused_id'])
            label_pred.append(count_f)
            count_f += 1
        else:
            label_pred.append(fused_list.index(i['fused_id']))
        url = i['img_url'].split('\\')[-2]
        if url not in url_list:
            url_list.append(url)
            label_true.append(count_u)
            count_u += 1
        else:
            label_true.append(url_list.index(url))
    return metrics.normalized_mutual_info_score(label_true, label_pred)
