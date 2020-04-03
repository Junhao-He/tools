# -*- coding: utf-8 -*-
"""
按时间范围划分
"""
from config.config_operation import ConfigOperation
from common.file_operation import split_opencv_img
from data_preprocess.capture_video_frame import read_video_frame_rate
import os


if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')
    params = ConfigOperation.read_config_file(config_file)
    # 图片输入路径
    input_path = params['figure.output.path']

    # 图片输出路径
    output_path = params['figure.range.path']
    # 每个文件夹多长时间范围(单位秒)
    time_interval = int(params['time.interval.range'])
    # 视频帧率
    frame_rate = read_video_frame_rate(params['video.input.path'])
    # 帧间隔
    frame_interval = int(params['capture.frame.interval'])
    # 拷贝到指定文件夹中
    split_opencv_img(input_path, output_path, time_interval, frame_interval, frame_rate)
