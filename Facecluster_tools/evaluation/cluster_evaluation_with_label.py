#coding:utf-8
"""
Created by hjh@2019.12.04
Modified by lf 2020/02/26
"""

from __future__ import division
import re
import os
import shutil
# import sys
# import configparser
# from evaluation.nmi_calculate import calculate_nmi
# from esClient.ESReader import get_data_from_es


def data_prepare(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['person_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['small_picture_path'])
            data_id[i['person_id']] = url
        else:
            data_id[i['person_id']].append(i['small_picture_path'])
        spp = re.findall(r"\[.*\]", i['small_picture_path'])[0]
        if spp not in data_url:
            id = []
            id.append(i['person_id'])
            data_url[spp] = id
        else:
            data_url[spp].append(i['person_id'])
    return data_id, data_url


def data_prepare_personid(result):
    data_fid = {}
    data_pid = {}
    for i in result[0]:
        if i['fused_id'] not in data_fid:
            id = []
            # url.append(i['small_picture_path'][-15:-8])
            id.append(i['person_id'])
            data_fid[i['fused_id']] = id
        else:
            data_fid[i['fused_id']].append(i['person_id'])
        if i['person_id'] not in data_pid:
            fid = []
            fid.append(i['fused_id'])
            data_pid[i['person_id']] = fid
        else:
            data_pid[i['person_id']].append(i['fused_id'])
    print(len(data_fid), len(data_pid))
    return data_fid, data_pid


def data_prepare_url(result):
    data_id = {}
    data_url = {}
    for i in result[0]:
        if i['fused_id'] not in data_id:
            url = []
            # url.append(i['small_picture_path'][-15:-8])
            url.append(i['img_url'])
            data_id[i['fused_id']] = url
        else:
            data_id[i['fused_id']].append(i['img_url'])
        # if i['img_url'][17:22] not in data_url:
        # label = i['img_url'].split('\\')[-2]
        try:
            label = i['img_url'].split('\\')[-2] 
        except:
            label = i['img_url'].split('/')[-2]
        if label not in data_url: # 53:89 for yc 50:57 for casia
            id = []
            id.append(i['fused_id'])
            data_url[label] = id
        else:
            data_url[label].append(i['fused_id'])
    return data_id, data_url


def calculate_recall(data):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    for i in data:
        if len(set(data[i])) == 1:
            pure_sum += len(data[i])
            pure_cluster += 1
            # print i)
        sum += len(data[i])
    recall = pure_sum/sum
    print("pure_recall: {}; sum: {}".format(pure_cluster, len(data)))
    print("pure_sum: {}; sum: {}".format(pure_sum, sum))
    return recall


def calculate_pre(data):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    sum_all = 0
    for key in data:
        # print data[key][0])
        if len(data[key][0]) != 5:
            serials = []
            for i in data[key]:
                # serial = re.findall(r"\[.*\]", i)[0]
                # serial = i.split('\\')[-2]
                try:
                    serial = i.split('\\')[-2] 
                except:
                    serial = i.split('/')[-2]
                serials.append(serial)
            de_serials = list(set(serials))
            # print de_serials)
            if len(serials) > 1:
                if len(de_serials) == 1 :
                    pure_sum += len(serials)
                    pure_cluster += 1
                sum += len(serials)
            sum_all += len(serials)
        else:
            if len(data[key]) > 1:
                if len(set(data[key])) == 1:
                    pure_sum += len(data[key])
                    pure_cluster += 1
                    # print i)
                sum += len(data[key])
            sum_all += len(data[key])
    pre = pure_sum/sum
    print("pure_pre: {}; sum: {}".format(pure_cluster, len(data)))
    print("pure_sum: {}; sum: {}".format(pure_sum, sum))
    return pre


def calculate_pre_with_singleton(data, amount):
    pure_sum = 0
    pure_cluster = 0
    sum = 0
    sum_all = 0
    for key in data:
        # print data[key][0])
        if len(data[key][0]) != 5:
            serials = []
            for i in data[key]:
                # serial = re.findall(r"\[.*\]", i)[0]
                try:
                    serial = i.split('\\')[-2]
                except:
                    serial = i.split('/')[-2]
                serials.append(serial)
            de_serials = list(set(serials))
            # print de_serials)
            if len(serials) > 1:
                if len(de_serials) == 1 :
                    pure_sum += len(serials)
                    pure_cluster += 1
                sum += len(serials)
            sum_all += len(serials)
        else:
            if len(data[key]) > 1:
                if len(set(data[key])) == 1:
                    pure_sum += len(data[key])
                    pure_cluster += 1
                    # print i)
                sum += len(data[key])
            sum_all += len(data[key])
    # pre = pure_sum/sum
    # print "pure_pre: {}; sum: {}".format(pure_cluster, len(data)))
    # print "pure_sum: {}; sum: {}".format(pure_sum, sum))
    pre_plus = pure_sum/amount
    return pre_plus


def save_img(data, dst_file):
    for key in data:
        if len(data[key]) > 1:
            for i in data[key]:
                if not os.path.isfile(i):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    #分离文件名和路径
                    imgpath1 = i.split('\\')[-2]
                    imgpath2 = i.split('\\')[-1]
                    fpath = dst_file+key+'\\'+imgpath1
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                #创建路径
                    shutil.copyfile(i, dst_file+key+'\\'+imgpath1+'\\'+imgpath2)      #复制文件
                    # print "copy %s -> %s"%(srcfile, dstfile))
    print("Saving images is complete!")


def save_img2(data, dst_file, src_file):
    # local_dir = 'C:\\Users\\0049003071\\Desktop\\fsdownload\\chongq-190314-171738 2019-03-13~2019-03-14\\cq_data\\'
    for key in data:
        if len(data[key]) > 1:
            for i in data[key]:
                # print i[72:])
                # i = src_file + i[90:]
                i = src_file + i[90:]
                if not os.path.isfile(i):
                    print("%s not exist!"%(i))
                else:
                    # fpath, fname = os.path.split(dstfile)    #分离文件名和路径
                    imgpath1 = i.split('\\')[-2]
                    imgpath2 = i.split('\\')[-1]
                    fpath = dst_file+key
                    if not os.path.exists(fpath):
                        os.makedirs(fpath)                #创建路径

                    shutil.copyfile(i, dst_file+key+'\\'+imgpath2)      #复制文件
                    # print "copy %s -> %s"%(srcfile, dstfile))
    print("Saving images is complete!")


def save_multiple_imgs(data, dst_file):
    for key in data:
        if len(data[key]) > 1:
            labels = []
            for item in data[key]:
                labels.append(item.split('\\')[-2])
            labels = set(labels)
            if len(labels) > 1:
                for i in data[key]:
                    if not os.path.isfile(i):
                        print("%s not exist!"%(i))
                    else:
                        # fpath, fname = os.path.split(dstfile)    # 分离文件名和路径
                        img_path1 = i.split('\\')[-2]
                        img_path2 = i.split('\\')[-1]
                        fpath = dst_file+key+'\\'+img_path1
                        if not os.path.exists(fpath):
                            os.makedirs(fpath)                # 创建路径
                        shutil.copyfile(i, dst_file+key+'\\'+img_path1+'\\'+img_path2)      # 复制文件
    print("Saving images is complete!")

# if __name__ == '__main__':
#     # thr = '0.92'
#     sys.path.insert(0, '/home/hjh/py')
#     config = configparser.ConfigParser()
#     print("- Load config file")
#     config.read("./cluster_evaluation_with_label_config.ini")
#     print(config['ES.org']['fused_src_table'])
#     result = get_data_from_es(config['ES.org']['fused_src_table'], config['ES.org']['host'], config['ES.org']['type'])
#     amount = len(result[0])
#     data_id, data_url = data_prepare_url(result)
#
#     precision = calculate_pre(data_id)
#     recall = calculate_recall(data_url)
#     print('Precision: {:.2%}'.format(precision))
#     print('Recall {:.2%}'.format(recall))
#     print('F1 Score: {:.2%}'.format(precision*recall*2/(precision+recall)))
#     print('Pre+: {:.2%}'.format(calculate_pre_with_singleton(data_id, amount)))
#     print('NMI: {:.2%}'.format(calculate_nmi(result)))
