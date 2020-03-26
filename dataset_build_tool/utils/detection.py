# -*-coding: utf-8-*-
"""
检测图片中的所有人脸图片
"""
from common.file_operation import read_dir, read_files
import os
from PIL import Image
import requests
import cv2
import datetime


# 商汤检测人脸
def detection_st_face(img_name, server_url= "http://10.45.157.115:80/verify/face/detectAndQuality", threshold=30):
    """人脸检测"""
    with open(img_name, "rb") as f:
        data = {"imageData": f}
        r = requests.post(server_url, files=data)
    # 解析post的返回结果
    result = eval(r.text)
    r.close()
    if  result['result'] != 'success':
        print("{}:检测失败.....".format(img_name))
        return [], result['result']
    else:
        size_list = []
        for elem in result["data"]:
            if elem['quality_score'] > threshold:
                size_list.append(elem['rect'])
        return size_list, result['result']


# 技术中心全量解析
def detection_jishuzhongxin_face(img_name, server_url="http://10.45.154.64:9010/verify/face/detect", threshold=0.99):
    """人脸检测"""
    with open(img_name, "rb") as f:
        data = {"imageData": f}
        r = requests.post(server_url, files=data)
    # 解析post的返回结果
    result = eval(r.text)
    r.close()
    if result['result'] != 'success':
        print("{}:检测失败.....".format(img_name))
        return [], result['result']
    else:
        size_list = []
        for elem in result["data"]:
            for item in elem['result']:
                if item['quality'] > threshold:
                    size_list.append(item['rect'])

        return size_list, result['result']


# Pillow保存图片
def crop_save_face(img_path, output_path, size = [0, 0, 0, 0], left_top_size=30, right_bottom_size=30):
    """保存人脸图片"""
    img = Image.open(img_path)
    width, height = img.size
    left = 0 if size[0] - left_top_size < 0 else size[0] - left_top_size
    top = 0 if size[1] - left_top_size < 0 else size[1] - left_top_size
    right = width if size[2] + right_bottom_size > width else size[2] + right_bottom_size
    bottom = height if size[3] + right_bottom_size > height else size[3] + right_bottom_size
    cropped = img.crop((left, top, right, bottom))
    cropped.save(output_path)


# cv保存图片
def cv_save_face(img_path, output_path, size = [0, 0, 0, 0], left_top_size=30, right_bottom_size=30):
    img = cv2.imread(img_path)
    left = 0 if size[0] - left_top_size < 0 else size[0] - left_top_size
    top = 0 if size[1] - left_top_size < 0 else size[1] - left_top_size
    right = img.shape[1] if size[2] + right_bottom_size > img.shape[1] else size[2] + right_bottom_size
    bottom = img.shape[0] if size[3] + right_bottom_size > img.shape[0] else size[3] + right_bottom_size
    cv2.imwrite(output_path, img[top:bottom, left:right])


# 检测人脸并保存
def detection_output(input_path, output_path, algo_type='st'):
    print("----------------人脸检测并保存服务开始--------------")
    start = datetime.datetime.now()
    dir_list = read_dir(input_path)
    for file_dir in dir_list:
        current_path = file_dir.split("\\")[-1]
        # print(current_path)
        files = read_files(file_dir)
        file_output_path = os.path.join(output_path, current_path)

        # 创建目录路径
        if not os.path.exists(file_output_path):
            print("创建输出目录{}...".format(file_output_path))
            os.makedirs(file_output_path)
        for file in files:
            absolute_path = os.path.join(file_dir, file)
            multi_face_list, state = detection_st_face(absolute_path) if algo_type == 'st' \
                else detection_jishuzhongxin_face(absolute_path)
            # 每张图片中的人脸
            num = 1
            for face_img_size in multi_face_list:
                img_output_path = os.path.join(file_output_path, file[:-4]+"-"+str(num)+file[-4:])
                # 保存在输出路径中按指定顺序存储
                cv_save_face(absolute_path, img_output_path, face_img_size)
                num += 1
            print("{}检测保存完成...".format(file))
    end = datetime.datetime.now()
    print("----------------人脸检测并保存服务完成，共耗时{}秒--------------".format((end-start).seconds))


if __name__ == "__main__":
    # input_path = "E:\\img_output"
    # output_path = "E:\\face_output"
    # detection_output(input_path, output_path)
    server_url = "http://10.45.157.115:80/verify/face/detectAndQuality"
    server_url_2 = "http://10.45.154.64:9010/verify/comdet/detect"
    # size_list, state = detection_st_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg")
    size_list, state = detection_jishuzhongxin_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", threshold=0.99)
    size_list_01, state_01 = detection_st_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", threshold=30)
    id = 1
    for si in size_list:
        # 保存方式测试
        cv_save_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", "E:\\test_img_1\\{}.jpg".format(str(id)),
                       si, 30, 30)
        crop_save_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", "E:\\test_img_11\\{}.jpg".format(str(id)),
                       si, 30, 30)
        id += 1
    # 检测方法测试
    for si in size_list_01:
        cv_save_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", "E:\\test_img_111\\{}.jpg".format(str(id)),
                       si, 30, 30)
        crop_save_face("E:\\img_output\\1\\nanjing7.mp4-000152.jpg", "E:\\test_img_1111\\{}.jpg".format(str(id)),
                       si, 30, 30)
        id += 1
    print(size_list)
    print(state)



