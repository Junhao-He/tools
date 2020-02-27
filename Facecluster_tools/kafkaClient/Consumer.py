# -*- coding: utf-8 -*-

"""
created by lf 2020/02/24
Kafka消费者
"""
from kafka import KafkaConsumer
from kafka.common import TopicPartition


class Consumer(object):
    def __init__(self, bootstrap_servers, topic, group_id, offset_reset, auto_commit):
        self.__bootstrap_servers = bootstrap_servers
        self.__topic = topic
        self.__group_id = group_id
        self.__offset_reset = offset_reset
        self.__auto_commit = auto_commit == 'True'

    def get_consumer(self):
        return KafkaConsumer(self.__topic,
                             bootstrap_servers=self.__bootstrap_servers,
                             group_id=self.__group_id,
                             auto_offset_reset=self.__offset_reset,
                             enable_auto_commit=self.__auto_commit)

    @staticmethod
    def get_total_offsets(consumer, topic):
        partition_array = [TopicPartition(topic=topic, partition=i) for i in
          consumer.partitions_for_topic(topic)]

        offsets = consumer.end_offsets(partition_array)
        sum_offsets = sum([i for i in offsets.values()])
        return sum_offsets