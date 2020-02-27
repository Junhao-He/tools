# -*- coding: utf-8 -*-
"""
聚类准确性测试主入口-ES
Created by lf
"""
from esClient.ESReader import get_data_from_es
from evaluation.nmi_calculate import calculate_nmi
from config.config_operation import ConfigOperation
from evaluation.cluster_evaluation_with_label import data_prepare_url, calculate_pre, calculate_recall, calculate_pre_with_singleton


if __name__ == '__main__':
    # thr = '0.92'
    print("- Load config file")
    cluster_param = ConfigOperation.read_config_file('./config/cluster.cfg')
    print(cluster_param['es.index.name'])
    result = get_data_from_es(cluster_param['es.index.name'], cluster_param['es.server.ip'], cluster_param['es.index.type'])
    amount = len(result[0])
    print('Data amount: ', amount)
    data_id, data_url = data_prepare_url(result)

    precision = calculate_pre(data_id)
    recall = calculate_recall(data_url)
    print('Precision: {:.2%}'.format(precision))
    print('Recall {:.2%}'.format(recall))
    print('F1 Score: {:.2%}'.format(precision*recall*2/(precision+recall)))
    print('Pre+: {:.2%}'.format(calculate_pre_with_singleton(data_id, amount)))
    print('NMI: {:.2%}'.format(calculate_nmi(result)))
