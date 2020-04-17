# coding = utf-8

import requests
import json
import base64
import cv2
import os
import glob
from time import sleep
import configparser


# 调用千视通接口获得图片结构化信息
def get_qst_struct_info(img_url, http='http://10.45.154.155:20280/rest/feature/structPicture'):
    base64_picture = pic2base64(img_url)
    json_content = {'objtype': '1', "picture": base64_picture}
    try:
        r = requests.post(http, json=json_content)
        response = json.loads(r.text)['objexts']
        print("Success1")
        return response, len(response)
    except requests.exceptions.ProxyError:
        sleep(5)
        r = requests.post(http, json=json_content)
        response = json.loads(r.text)['objexts']
        print("Success2")
        return response, len(response)
    except KeyError:
        img_changed = change_image(img_url)
        r_changed = requests.post(http,
                              json={'objtype': '1', "picture": pic2base64(img_changed)})
        try:
            response_changed = json.loads(r_changed.text)['objexts']
            print("Success3")
            return response_changed, len(response_changed)
        except:
            print("Error 2!")
            return [], 0
    except Exception as e:
        print(e)
        return [], 0


# 添加qst视频分析任务
def qst_video_quest(http='http://10.45.154.155:20280/rest/taskManage/addVideoObjextTask'):
    json_content = {"serialnumber": "201710243",
                    "type": "objext",
                    "url": "http://10.45.154.218:8000/20171024软创车库3.mp4",
                    "param": ""}
    r = requests.post(http, json=json_content)
    print(r._content)


# 调用Picasso接口行人位置检测
def get_picasso_struct_info(img_url, http='http://10.45.154.218:9010/verify/comdet/detect'):
    content = {"imageData": open(img_url, 'rb')}
    reply = requests.post(http, files=content)
    response = json.loads(reply.text)
    if response['result'] == "success":
        # print(response)
        return response['data'][0]['result'], len(response['data'][0]['result'])
    else:
        print('Picasso failure')
        return [], 0



# 图片转base64
def pic2base64(url):
    with open(url, 'rb') as f:
        base64_data = base64.b64encode(f.read())
        return base64_data.decode()


def change_image(img_url):
    image = cv2.imread(img_url)
    cv2.imwrite(img_url, image)
    return img_url


# 根据结构化信息标记大图，截取小图
def mark_image(img_url, struc_info_qst, struct_info_picasso):
    count = 0
    print(img_url)
    image = cv2.imread(img_url)
    # cv2.imshow('aa', image)
    # cv2.waitKey(0)
    image_copy = image.copy()
    img_url = img_url.split('.')
    url_structed = img_url[0]+'_marked.'+img_url[1]
    if os.path.exists(url_structed):
        os.remove(url_structed)
    for item in struc_info_qst:
        location = item['snapshot']['boundingBox']
        # print(location)
        person_img = image[location['y']:location['y']+location['h'], location['x']:location['x']+location['w']]
        person_img_url = ".\\ReID_test\\small_pic_qst"+'\\test_'+img_url[0].split('\\')[-1]+'_'+str(count)+'.jpg'
        # print(person_img_url)
        cv2.imwrite(person_img_url, person_img)
        cv2.rectangle(image_copy, (location['x'], location['y']), (location['x']+location['w'],
                                                                   location['y']+location['h']), (255, 0, 0), 2)
        count += 1
    if not struct_info_picasso == []:
        for item in struct_info_picasso:
            if item['type'] == 1200: # 1200为人体
                location = item['rect']
                # print(location)
                if location[0] > 0:
                    person_img = image[location[1]: location[3],  location[0]: location[2]]
                else:
                    person_img = image[location[1]: location[3], 0: location[2]]
                person_img_url = '.\\ReID_test\\small_pic_picasso'+'\\test_'+img_url[0].split('\\')[-1]+'_'+str(count)+'.jpg'
                cv2.imwrite(person_img_url, person_img)
                cv2.rectangle(image_copy, (location[0],location[1]), (location[2], location[3]),
                                                                     (0, 0, 255), 2)
                count += 1
    cv2.imwrite(url_structed, image_copy)


# 标记功能入口，标记qst,picasso检出人体
def picture_process(pic_dir):
    picture_list = glob.glob(pic_dir)
    print("Num of pictures:{}".format(len(picture_list)))
    for i in picture_list:
        # print(i)
        result_qst, num_qst = get_qst_struct_info(i)
        # print(num_qst)
        result_picasso, num_picasso = get_picasso_struct_info(i)
        # print(result_picasso)
        mark_image(i, result_qst, result_picasso)
    print("Processing completed")
    # return picture_list


# 调用接口提取qst特征，已被get_qst_struct_info函数代替
def get_qst_feature(img_url, http='http://10.45.154.155:20280/rest/feature/structPicture'):
    base64_picture = pic2base64(img_url)
    json_content = {'objtype': '1', "picture": base64_picture}
    try:
        r = requests.post(http, json=json_content)
        response = json.loads(r.text)['objexts']
        # print("Success1")
        return response
    except Exception as e:
        print("error!")


# 提取qst人体特征保存本地
def save_qst_feature(pic_dir, save_file='./qst_feature_dict2.json'):
    feature_dict = {}
    picture_list = glob.glob(pic_dir)
    print(len(picture_list))
    for i in picture_list:
        print(i)
        img_url = i.split('\\')[-1].split('.')[0]
        result_qst, num_qst = get_qst_struct_info(i)
        if len(result_qst) == 1:
            feature_dict[img_url] = result_qst[0]['features']['featureData']
    # save_file = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\qst_feature_dict2.json'
    # print(save_file)
    with open(save_file, 'a') as f:
        f.write(json.dumps(feature_dict))


# 调用qst以图搜图接口
def qst_search(pic_byte, serialnumbers, http='http://10.45.154.155:20280/rest/feature/search'):
    json_content = {"objtype": "1",
                    "picture": pic_byte,
                    "serialnumbers": serialnumbers}
    r = requests.post(http, json=json_content)
    print(r._content)


# 程序主入口
def main():
    config = configparser.ConfigParser()
    print("- Load config file")
    config.read("./compare_structure_info.ini")
    if config['MARK.org']['mark_sign'] == '1':
        print('mark')
        dir = config['MARK.org']['source_dir'] + '\\*.jpg'
        print(dir)
        picture_process(dir)
    if config['SAVE.org']['save_sign'] == '1':
        print('save')
        dir = config['SAVE.org']['source_dir'] + '\\*.jpg'
        save_qst_feature(dir)


if __name__ == '__main__':
    main()
    # img_url = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\FD_103_514271.jpg'
    # result_qst, num_qst = get_qst_struct_info(img_url)
    # print(result_qst)
    # result_picasso, num_picasso = get_detected_picture(img_url)
    # # print(result_picasso)
    # mark_image(img_url, result_qst, result_picasso)


    # source_dir = r'D:\Dataset\20171024_3'
    # dir = source_dir + '\\*.jpg'
    # picture_process(dir)


    # img_url = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\znv_test.jpg'
    # img_url = r'D:\Dataset\ReID_test\small_pic_qst\2_0075_0.jpg'
    #
    # qst_search(pic2base64(img_url), "201710242")
