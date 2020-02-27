# -*- coding: utf-8 -*-
"""
avro格式解码
"""
import avro.schema
from avro.io import DatumReader, BinaryDecoder
from avro.datafile import DataFileReader
from io import BytesIO
import os

# os.path.dirname: 获取当前路径
current_path = os.path.dirname(__file__)
schema = avro.schema.Parse(open(current_path+'/../config/user.avsc').read())
datum_reader = DatumReader(schema)


# 解析avro数据字段
def decode_avro(data):
    decoder = BinaryDecoder(BytesIO(data))
    new_data = datum_reader.read(decoder)
    return new_data


# 解析avro_file文件数据
def decode_avro_file(file):
    data = []
    with DataFileReader(open(file, "rb"), datum_reader) as reader:
        for user in reader:
            data.append(user)
    return data