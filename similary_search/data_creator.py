# coding = utf-8 

# @Time : 2020/10/21 15:23
# @Author : 0049003071
# @File : data_creator.py
# @Software: PyCharm

from fdfs_client.client import Fdfs_client, get_tracker_conf
import os
import glob
import json
import codecs


def upload_fdfs_with_label(pic_dir):
    fdfs_dict = {}
    faceid_dict = {}
    tracker_path = get_tracker_conf('./fdfs_client.conf')
    client = Fdfs_client(tracker_path)
    picture_list = get_file_list(pic_dir, [])
    print('Picture number: {}'.format(len(picture_list)))
    for i in picture_list:
        # print(i)
        ret = client.upload_by_filename(i)
        fdfs_dict[i] = str(ret['Remote file_id'], encoding='utf-8')
        faceid_dict[i] = i.split('\\')[-2]
    return fdfs_dict, faceid_dict


def upload_fdfs(pic_dir):
    fdfs_dict = {}
    tracker_path = get_tracker_conf('./fdfs_client.conf')
    client = Fdfs_client(tracker_path)
    picture_list = glob.glob(pic_dir + '\\*.jpg')
    print('Picture number: {}'.format(len(picture_list)))
    for i in picture_list:
        # print(i)
        ret = client.upload_by_filename(i)
        fdfs_dict[i] = str(ret['Remote file_id'], encoding='utf-8')
    return fdfs_dict


# 遍历文件夹获得文件名list
def get_file_list(dir, file_list):
    # newDir = dir
    if os.path.isfile(dir):
        file_list.append(dir)
    elif os.path.isdir(dir):
        for s in os.listdir(dir):
            # if s == "xxx":
                # continue
            new_dir = os.path.join(dir, s)
            get_file_list(new_dir, file_list)
    return file_list


def save2json(dic, filename):
    data = json.dumps(dic)
    try:
        if not os.path.exists(os.getcwd()+'\\'+filename):
            with codecs.open(os.getcwd()+'\\'+filename, 'a', 'utf-8') as file:
                file.write(data)
        else:
            os.remove(os.getcwd()+'\\'+filename)
    except:
        print("Failed！")


if __name__ == '__main__':
    img2fdfs, face2fdfs = upload_fdfs_with_label(r'D:\Dataset\Chongq')
    save2json(img2fdfs, "cqimg2fdfs.json")
    save2json(face2fdfs, "cqface2fdfs.json")