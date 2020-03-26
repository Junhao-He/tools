# -*-coding: utf-8-*-
"""
文件读取或文件夹读取
"""
import os
import math
import shutil
import datetime
import random


def read_files(input_path):
    l = []
    for root, dirs, files in os.walk(input_path):
        for file in files:
            if file.endswith('.jpg'):
                # nums.append(int(file.split('-')[1].split('.')[0]))
                l.append(file)
    return l


# 返回文件夹下的所有目录
def read_dir(input_path):
    lsdir = os.listdir(input_path)
    dirs = [os.path.join(input_path, i) for i in lsdir if os.path.isdir(os.path.join(input_path, i))]
    return dirs


# 获取文件块数及尺寸（ffmpeg方式切割）
def split_block(data, time_delta, frame_v=10):
    block_size = time_delta * frame_v
    length = len(data)
    n_block = math.ceil(length / block_size)
    return n_block, block_size


# 将文件分割到不同的文件夹中（即按时间划分）
def split_img(input_path, output_path):
    l = read_files(input_path)
    # 切割成多少块
    img_dict = {}
    n_block, block_size = split_block(l, 60)
    for item in l:
        # 计算每张图片属于哪个新块
        id = math.ceil(int(item.split('-')[1].split('.')[0]) / block_size)
        img_dict[item] = str(id)

    for k, v in img_dict.items():
        new_path = os.path.join(output_path, v)
        if not os.path.exists(new_path):
            os.makedirs(new_path)
        shutil.copy(input_path + "\\" + k, new_path)
    return img_dict


# 获取文件块数及尺寸（opencv方式）
def split_opencv_block(data, time_delta, frame_interval=10, frame_num_per_second=24):
    block_size = math.ceil(frame_num_per_second / frame_interval * time_delta)
    # print(block_size)
    length = len(data)
    n_block = math.ceil(length / block_size)
    return n_block, block_size


# 将文件分割到不同的文件夹中（即按时间划分）
def split_opencv_img(input_path, output_path, time_delta, frame_interval=10,
                     frame_num_per_second=24):
    print("-----文件切块开始------")
    start = datetime.datetime.now()
    files = read_files(input_path)
    # 切割成多少块
    img_dict = {}
    n_block, block_size = split_opencv_block(files, time_delta, frame_interval, frame_num_per_second)
    # print(block_size)
    for item in files:
        # 计算每张图片属于哪个新块
        id = math.ceil(int(item.split('-')[1].split('.')[0]) / block_size)
        img_dict[item] = str(id)

    for k, v in img_dict.items():
        new_path = os.path.join(output_path, v)
        if not os.path.exists(new_path):
            os.makedirs(new_path)
        shutil.copy(input_path + "\\" + k, new_path)
    end = datetime.datetime.now()
    print("----文件切块完成,耗时:{}秒------".format((end - start).seconds))
    return img_dict


# 检测小于等于n张图片的文件夹，并将其删除
def detect_romove_file(path, n=4):
    if not os.path.exists(path):
        print("该路径不存在，请重新输入...:")
        exit()
    else:
        dir_list = read_dir(path)
        for dir_path in dir_list:
            if not os.listdir(dir_path):
                os.rmdir(dir_path)
                print("删除文件夹：{}".format(dir_path))
            else:
                file_list = read_files(dir_path)
                if len(file_list) <= n:
                    for name in file_list:
                        del_file = os.path.join(dir_path, name)
                        os.remove(del_file)
                        print("remove {} successfully.".format(del_file))
                    os.rmdir(dir_path)
                    print("删除文件夹：{}".format(dir_path))


# 每个文件夹挑选图片，并重命名到一个文件夹中进行检测
def random_select_face(path, new_path):
    new_face_list = []
    if not os.path.exists(path):
        print("该路径不存在，请重新输入...:")
        exit()
    else:
        dir_list = read_dir(path)
        for dir_path in dir_list:
            if os.listdir(dir_path):
                file_list = read_files(dir_path)
                id_face = random.choice(file_list)
                current_dir = dir_path.split("\\")[-1]
                if not os.path.exists(new_path):
                    os.makedirs(new_path)
                shutil.copy(os.path.join(dir_path, id_face), new_path + "\\" +current_dir + ".jpg")


# 选择出最终人脸
def random_select_final_face(path, new_path):
    if not os.path.exists(path):
        print("该路径不存在，请重新输入...:")
        exit()
    else:
        dir_list = read_dir(path)
        for dir_path in dir_list:
            if os.listdir(dir_path):
                file_list = read_files(dir_path)
                id_face = random.choice(file_list)
                current_dir = dir_path.split("\\")[-1]
                if not os.path.exists(new_path):
                    os.makedirs(new_path)
                shutil.copy(os.path.join(dir_path, id_face), new_path + "\\" +current_dir + ".jpg")


# 所有图片迁移到一个文件夹
def transfer_merge(input_path, current_out_dir):
    dir_list = read_dir(input_path)
    fig_num_dict = {}
    for dir_path in dir_list:
        sub_dir_list = read_dir(os.path.join(dir_path, 'img'))
        for sub_dir in sub_dir_list:
            num = len(read_files(sub_dir))
            if num in fig_num_dict:
                fig_num_dict[num].append(sub_dir)
            else:
                fig_num_dict[num] = [sub_dir]

    final_dir = os.path.join(input_path, current_out_dir)
    if not os.path.exists(final_dir):
        os.makedirs(final_dir)
    for nums, dir_list in fig_num_dict.items():
        for i, dir_addr in enumerate(dir_list):
            new_path = os.path.join(final_dir, str(nums)+'_'+str(i+1)) if i > 0 else os.path.join(final_dir, str(nums))
            os.makedirs(new_path)
            files = read_files(dir_addr)
            for file in files:
                shutil.copy(os.path.join(dir_addr, file), new_path)

# 图片输入路径
# input_path = 'E:\\video\\test_01'

# 图片输出路径
# output_path = "E:\\img_output"
# file_dict = split_img(input_path, output_path)
# print(len(file_dict))
# read_files(output_path)
# dirs = read_dir(output_path)
# print(dirs)
