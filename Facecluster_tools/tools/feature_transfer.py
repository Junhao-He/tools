import requests
import base64
import numpy as np
import struct
import json


def getFeatureST(pic, http="http://10.45.157.115:9001/verify/feature/gets"):
    body = open(pic, 'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    feature = base64Tofloat(aaa['feature'])#.encode(encoding = 'utf-8')
    # print feature)
    return feature
    # return aaa['feature']


def base64Tofloat(kb_feature):
    # feather = base64.b16decode(kb_feature)
    feature = base64.b64decode(kb_feature)
    floatout = []
    if len(feature) >= 2060:
        for i in range(12, 12 + 512 * 4, 4):
            x = feature[i:i + 4]
            floatout.append(struct.unpack('f', x)[0])
    return floatout


def get_feature_from_st(pic, http="http://10.45.157.115:80/verify/feature/gets"):
    body = open(pic,'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    # return base64Tofloat(aaa['feature'])
    return aaa['feature']


def base64_to_float(kb_feature):
    feature = base64.b64decode(kb_feature)
    float_out = []
    if len(feature) >= 2060:
        for i in range(12, 12 + 512 * 4, 4):
            x = feature[i:i + 4]
            float_out.append(struct.unpack('f', x)[0])
    return float_out


def base64_to_byte(codes):
    try:
        fea = base64.decodestring(codes)
    except:
        fea = base64.b64decode(codes)
    return fea