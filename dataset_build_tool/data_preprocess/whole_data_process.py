# -*- coding: utf-8 -*-
"""
视频截帧、按时间范围划分保存并检测人脸(预处理一体化)
"""
import os
from config.config_operation import ConfigOperation
from data_preprocess.capture_video_frame import opencv_capture
from common.file_operation import split_opencv_img
from utils.detection import detection_output


if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')
    params = ConfigOperation.read_config_file(config_file)
    print("----1、视频分帧-----")
    # 视频帧率
    actual_frame_rate = opencv_capture(params['video.input.path'], params['figure.output.path'],
                   int(params['capture.frame.interval']))

    print("----2、按时间范围保存----")
    # 图片输入路径
    input_path = params['figure.output.path']
    # 图片输出路径
    output_path = params['figure.range.path']
    # 每个文件夹多长时间范围(单位秒)
    time_interval = int(params['time.interval.range'])
    # 帧间隔
    frame_interval = int(params['capture.frame.interval'])
    # 拷贝到指定文件夹中
    split_opencv_img(input_path, output_path, time_interval, frame_interval, actual_frame_rate)

    print("----3、人脸检测和保存----")
    detection_output_path = params['face.detection.output.path']
    detection_input_path = output_path
    algo_type = params['face.detection.algo']
    detection_output(detection_input_path, detection_output_path, algo_type)

