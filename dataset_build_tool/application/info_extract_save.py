# -*-coding: utf-8-*-
"""
提取图片信息并保存
"""
import os
import datetime
from config.config_operation import ConfigOperation
from utils.extraction import extraction_img
from common.file_operation import read_dir, read_files


if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')

    params = ConfigOperation.read_config_file(config_file)
    start = datetime.datetime.now()
    input_path = params['face.detection.output.path']
    output_path = params['face.info.initial.path']
    dir_list = read_dir(input_path)
    failed_images = []
    for file_dir in dir_list:
        current_path = file_dir.split("\\")[-1]
        # print(current_path)
        file_output_dir = os.path.join(output_path, current_path)
        if not os.path.exists(file_output_dir):
            os.makedirs(file_output_dir)
        with open(os.path.join(file_output_dir, "feature_info.txt"), "w") as f:

            files = read_files(file_dir)
            uuid = 0
            enter_time = "2020-03-11 19:30:10"
            for file in files:
                uuid += 1
                img_url = os.path.join(file_dir, file)
                feature = extraction_img(img_url)
                if feature == "":
                    failed_images.append(file)
                    print(file+":特征提取失败，未写入...")
                else:

                    f.write(str(uuid) + "," + img_url + "," + feature + "," + enter_time + ",0" + "\n")
                    print(file+":特征写入成功...")
    end = datetime.datetime.now()
    print("-------------图片信息提取成功，共耗时：{}秒----------".format((end-start).seconds))
    print("特征提取失败的图片：{}".format(failed_images))