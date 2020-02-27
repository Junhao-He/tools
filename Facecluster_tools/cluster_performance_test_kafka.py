# -*- coding: utf-8 -*-
"""
聚类准确性测试主入口-Kafka
"""
from kafkaClient.Consumer import Consumer
from config.config_operation import ConfigOperation
from tools.data_prepare import get_kafka_data, kafka_data_prepare, data_prepare_url
from evaluation.cluster_evaluation_with_label import calculate_pre, calculate_recall, calculate_pre_with_singleton
from evaluation.nmi_calculate import calculate_nmi


if __name__ == '__main__':
    cluster_param = ConfigOperation.read_config_file('./config/cluster.cfg')
    print(cluster_param)
    # 获取源数据信息
    print("*****获取源数据topic:{}信息***".format(cluster_param['kafka.topic.name']))
    consumer_object = Consumer(cluster_param['kafka.bootstrap.servers'], cluster_param['kafka.topic.name'],
                               cluster_param['kafka.group.id'], cluster_param['kafka.auto.offset.reset'],
                               cluster_param['kafka.offset.auto.commit'])
    consumer = consumer_object.get_consumer()
    total = Consumer.get_total_offsets(consumer, cluster_param['kafka.topic.name'])
    data = get_kafka_data(consumer, total)
    consumer.close(False)

    # 获取src信息
    print("*****获取src topic:{}信息***".format(cluster_param['kafka.src.topic.name']))
    consumer_object = Consumer(cluster_param['kafka.bootstrap.servers'], cluster_param['kafka.src.topic.name'],
                               cluster_param['kafka.group.id'], cluster_param['kafka.auto.offset.reset'],
                               cluster_param['kafka.offset.auto.commit'])
    consumer = consumer_object.get_consumer()
    src_total = Consumer.get_total_offsets(consumer, cluster_param['kafka.src.topic.name'])
    src_data = get_kafka_data(consumer, total)
    consumer.close(False)

    result = kafka_data_prepare(data, src_data)

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


