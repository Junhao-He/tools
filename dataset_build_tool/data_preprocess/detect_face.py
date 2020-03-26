# -*-coding: utf-8-*-
"""
检测图片图片中的人脸并保存
"""
import os
from config.config_operation import ConfigOperation
from utils.detection import detection_output


if __name__ == '__main__':

    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')

    params = ConfigOperation.read_config_file(config_file)

    input_path = params['figure.range.path']
    output_path = params['face.detection.output.path']
    algo_type = params['face.detection.algo']
    detection_output(input_path, output_path, algo_type)
