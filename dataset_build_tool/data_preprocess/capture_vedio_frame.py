# -*- coding: utf-8 -*-
"""
视频截帧成像
"""
import cv2
import os
from config.config_operation import ConfigOperation


def opencv_capture(input_file, output_path, frame_interval=5):
    vc = cv2.VideoCapture(input_file)
    c = 0
    num = 0
    if vc.isOpened():
        rval, frame = vc.read()
    else:
        rval = False

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
    opencv_capture(params['vedio.input.path'], params['figure.output.path'],
                   int(params['capture.frame.interval']))

    # opencv_capture('E:\\video\\nanjing7.mp4', "E:\\video\\testx")


