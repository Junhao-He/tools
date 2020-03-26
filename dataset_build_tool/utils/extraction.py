# -*- coding: utf-8 -*-
"""
提取特征接口....
"""
import requests
import json


def extraction_img(pic, http="http://10.45.157.115:80/verify/feature/gets"):
    body = open(pic, 'rb')
    files = {'imageData': body}
    reply = requests.post(http, files=files)
    aaa = json.loads(reply.text)
    if aaa['result'] == 'success':
        return aaa['feature']
    else:
        # print("特征提取失败")
        return ""

extraction_img("E:\\face_output\\1\\nanjing7.mp4-000001-1.jpg")

