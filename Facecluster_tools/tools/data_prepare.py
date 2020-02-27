# -*- coding: utf-8 -*-
import re
from util.decode import decode_avro


# 获取kafka数据
def get_kafka_data(consumer, total_offsets):
     result = {}
     count = 0
     for msg in consumer:
        value = decode_avro(msg.value)
        # print(value)
        result[value['uuid']] = value
        count += 1
        if count >= total_offsets:
            return result


def kafka_data_prepare(data, src_data):
    result = []
    for k, v in data.items():
        src_data[k]['img_url'] = v['img_url']
        result.append(src_data[k])
    return result, []


def data_prepare(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['person_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['small_picture_path'])
            data_id[i['person_id']] = url
        else:
            data_id[i['person_id']].append(i['small_picture_path'])
        spp = re.findall(r"\[.*\]", i['small_picture_path'])[0]
        if spp not in data_url:
            id = []
            id.append(i['person_id'])
            data_url[spp] = id
        else:
            data_url[spp].append(i['person_id'])
    return data_id, data_url


def data_prepare_url(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['fused_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['img_url'])
            data_id[i['fused_id']] = url
        else:
            data_id[i['fused_id']].append(i['img_url'])
        # if i['img_url'][17:22] not in data_url:
        # label = i['img_url'].split('\\')[-2]
        try:
            label = i['img_url'].split('\\')[-2]
        except:
            label = i['img_url'].split('/')[-2]
        if label not in data_url: # 53:89 for yc 50:57 for casia
            id = []
            id.append(i['fused_id'])
            data_url[label] = id
        else:
            data_url[label].append(i['fused_id'])
    return data_id, data_url

