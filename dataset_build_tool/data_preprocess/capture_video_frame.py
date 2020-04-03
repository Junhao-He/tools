# -*- coding: utf-8 -*-
"""
视频截帧成像
"""
import cv2
import os
import math
from config.config_operation import ConfigOperation


def opencv_capture(input_file, output_path, frame_interval=5, config_file = ''):
    vc = cv2.VideoCapture(input_file)
    c = 0
    num = 0
    if vc.isOpened():
        rval, frame = vc.read()
    else:
        rval = False

    major_ver, minor_ver, subminor_ver = cv2.__version__.split('.')
    fps = vc.get(cv2.cv.CV_CAP_PROP_FPS) if int(major_ver) < 3 else vc.get(cv2.CAP_PROP_FPS)

    while rval:
        rval , frame = vc.read()
        if not os.path.exists(output_path):
            os.makedirs(output_path)
        if c % frame_interval == 0:
            num += 1
            cv2.imwrite(os.path.join(output_path, "img-"+str(num) + '.jpg'), frame)
        c = c + 1
        cv2.waitKey(1)

    vc.release()
    actual_rate = int(math.ceil(fps))
    # props = {'video.frame.rate': str(actual_rate)}
    # if config_file != '':
    #     ConfigOperation.write_config_file(config_file, props)
    return actual_rate


# 获取视频帧率
def read_video_frame_rate(input_file):
    vc = cv2.VideoCapture(input_file)
    major_ver, minor_ver, subminor_ver = cv2.__version__.split('.')
    # 版本号判断
    fps = vc.get(cv2.cv.CV_CAP_PROP_FPS) if int(major_ver) < 3 else vc.get(cv2.CAP_PROP_FPS)
    vc.release()
    actual_rate = int(math.ceil(fps))
    return actual_rate


# def ffmpeg_capture(in_file, frame_num, output_path):
#     import ffmpeg
#     out, err = (ffmpeg.input(in_file, ss=0)
#                 .output(os.path.join(output_path, 'image-%06d.jpg'), vf='fps=fps={}/1'.format(frame_num),
#                         f='image2')
#                 .run(capture_stdout=True)
#                 )
#     return out


# ffmpeg_capture("E:\\video\\nanjing7.mp4", 5, "E:/video/testx")
if __name__ == '__main__':
    current_path = os.getcwd()
    config_file = os.path.join(current_path, '..\\config\\task.cfg')

    params = ConfigOperation.read_config_file(config_file)
    print(params)
    opencv_capture(params['video.input.path'], params['figure.output.path'],
                   int(params['capture.frame.interval']), config_file=config_file)

    # opencv_capture('E:\\video\\nanjing7.mp4', "E:\\video\\testx")


