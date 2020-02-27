# -*- coding: utf-8 -*-
"""
avro格式编码
"""
import avro.schema
from avro.io import BinaryEncoder, DatumWriter
from io import BytesIO
from avro.datafile import DataFileWriter
import os
# from util.decode import decode_avro

current_path = os.path.dirname(__file__)
schema = avro.schema.Parse(open(current_path+'/../config/user.avsc').read())
dw = DatumWriter(writer_schema=schema)


def encode_avro(data):
    bio = BytesIO()
    binary_encoder = BinaryEncoder(bio)
    dw.write(data, binary_encoder)
    return bio.getvalue()


# 写入指定文件
def encode_avro_output_file(data, file):
    with DataFileWriter(open(file, "wb"),DatumWriter(), schema) as f:
        for elem in data:
            f.append(elem)


# d_list = [{"name": "Alyssa", "favorite_number": 256, "feature": "1"},
#           {"name": "Zhangsan", "favorite_number": 257, "feature": "2"}]
#
# encode_avro_output_file(d_list, "users.avro")
#
# d0 = {"name": "Alyssa", "favorite_number": 256, "feature": ""}
#
# encode_d0 = encode_avro(d0)
# print(encode_d0)
#
#
# decode_d0 = decode_avro(encode_d0)
# print(decode_d0)