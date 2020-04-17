# coding = utf-8

# import base64
#

#
# f2 = "/fv4/gD/A/8A//7+/PwAAP79/P38/wAA/Pv+AAEA/v79////Bg8A//79AgP+//39AwQCAAMF/v8AAP/+AAD/AAD+/v4AAAD//v7/AP/////9//7//v77/wD//vz9///+AgQAAAAAAAEJAQD/AAICAAD+/P39/wEA/Pz7/wEH/v39//38CgYEAQIA//7+//7//vz5/f39/v79///+//8AAP4AAAD7/v38AP/9////+/z7/wAA///9/////fv5/v39/P38/gEK/wAEAv79AwYFAAD/AP/9//8A/v79/wD+BwgWCwoKA//7/v38BwT+/wD/AP/+/////wD9/v38AAD9/wD+/v//AP/+/fv7/vz6/v4BAf/9/f39/wAA/f0AAAMFAP/+//79AAMEAgEBAAEAAP//AAADAgIB/gAAAAAA+/r4/fz6Av//AP79/f37/v39AQABAP7+/v7+//7+/v35/v//+/fz+/v6AP/8//7+/vv6/f3+AAD8/v37AAAAAAAAAAH+/wAA//7/AAME/Pz8//38/f39//7+//78//79AAEAAP8B/wEKBggFAAD+/////Pz8//8A+/n6/gD++/v8/wAAAAUEAgIAAgMCAAEA/f3+AP///f79/wAA/gAAAP8A/Pn6/v7+/v78/wAC//7/AAAA/QAA//8AAwT8/wAABf37/v38/v/+//7+/fz8//38/Pz9//79+/r5/fz7AAEA//38/Pn4/f79AAD/AAAE/v/+AP/+//77/gD////9/wAA/gAAAAAA/Pz9///9CQgHAgAA//77/v39/gEFAP79/f38/wAA/v38////AgMDAP//BAf5/f39/Pz7//79//78///+////AP//AAUPBQD+/f39//////7+//7++/n2/Pr5/wADAQD//v39//3+/f36/gEE/v/9AAkH/gD+/wD/AAECAP/+/wAAAP39//7+AAD+BQIAAP79//8CAf79DxQbCggI/Pv7/vv6/vz7/v7//v/7/v39AAMEAgMEAgD///37AQECAgYG/P34/f7//fz7/v39AP///////wABAP/+/P39///+/f7/AP/9///9///+/v7+///+/v78/v7+///+AAEFBQgFAQAA+/3+/////Pr4/f//BQEAAP78/P39//38/f3+AP7+//39AP7+AQD/AP/+AAD/AP/9/f38/v///v4AAP///v38/v38AP8AAP/+/v37/v/++/z9//79AAAAAAAA/gEEAggL/wH8/wAA/wEBAAD//v38///+//38///+/fz9//3//P37/v7+/v8AAQIH/wAAAAIA//7+///9//78//7+BQEEAv/+//8AAP7+BgUDAgQE/P37/v38AAAAAP/+/f39//79BAD+AP7+/v/+AP///v39/wACAAMCAwsK/wIEAf/+/P76/f//AQD9/wAAAQULBf7+/v/9///+/fz9/wD//v7+AP//AAMGBAkI/fz7/v79/QAHAgAA/v38/v8BAAEJAwH///77/v79/fz+AP79/Pz6/f7+/v8AAAEB/Pz7/v39AAAAAAMC/v39///9/f38//7/+/r6/v/9//4AAPz7/fv7//8A//78/wEC//79AAIDCwr+///+/P37/v78/QABAP79/v39//8A/fz7/wUM/f38//7+AP/8/vz6+/v7//38AP8AAAUI/v7+//8A/f7/AP/+AAD+AAIA+/v4/f4A/Pv6/v8AAP/8/wEB/wD+/wAC/fz6/v3+BAUBAAID/v7/AP79/Pv9//79AAAAAAAA/P0AAP//+fn6/v39/gEHAwsLAQQQBQIA/v//AP/9/gD///79//77/v7+/v4BAQIE////AAD/AQMAAAD//Pz9/wAD//79///9AP7+//7+/wAAAQD+/f37/v/+AgD5/f79/v7/AAAD/fz8/v7+/Pr6/v38/f39///+/f//AP/+/v4AAP/+/f37/v79/v78/v39/f79AAD//f7+//////7+//38//8CAQD+/v8AAAAAAAD///7///3+AP/+AwcOBQIB/fv8//7+/fz3/Pz7"
# # print(feature)
# print(len(feature))
# print(len(f2))

# coding = utf-8

import requests
import json
import base64
import cv2
import os
import glob
from time import sleep
import struct


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
        sleep(10)
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


def qst_video_quest(http='http://10.45.154.155:20280/rest/taskManage/addVideoObjextTask'):
    json_content = {"serialnumber": "201710243",
                    "type": "objext",
                    "url": "http://10.45.154.218:8000/20171024软创车库3.mp4",
                    "param": ""}
    r = requests.post(http, json=json_content)
    print(r._content)


# 调用Picasso接口行人位置检测
def picasso_struct_info(img_url, http='http://10.45.154.218:9010/verify/comdet/detect'):
    content = {"imageData": open(img_url, 'rb')}
    reply = requests.post(http, files=content)
    response = json.loads(reply.text)
    return response['data'][0]['result'], len(response['data'][0]['result'])


def pic2base64(url):
    with open(url, 'rb') as f:
        base64_data = base64.b64encode(f.read())
        return base64_data.decode()


def change_image(img_url):
    image = cv2.imread(img_url)
    # img_url = img_url.split('.')
    # new_url = img_url[0]+'_cv.'+img_url[1]
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
        person_img_url = r'D:\Dataset\ReID_test\small_pic_qst'+'\\test_'+img_url[0].split('\\')[-1]+'_'+str(count)+'.jpg'
        # print(person_img_url)
        cv2.imwrite(person_img_url, person_img)
        cv2.rectangle(image_copy, (location['x'], location['y']), (location['x']+location['w'],
                                                                   location['y']+location['h']), (255, 0, 0), 2)
        count += 1
    for item in struct_info_picasso:
        if item['type'] == 1200:
            location = item['rect']
            # print(location)
            if location[0] > 0:
                person_img = image[location[1]: location[3],  location[0]: location[2]]
            else:
                person_img = image[location[1]: location[3],  0: location[2]]
            person_img_url = r'D:\Dataset\ReID_test\small_pic_picasso'+'\\test_'+img_url[0].split('\\')[-1]+'_'+str(count)+'.jpg'
            cv2.imwrite(person_img_url, person_img)
            cv2.rectangle(image_copy, (location[0],location[1]), (location[2], location[3]),
                                                                 (0, 0, 255), 2)
            count += 1
    cv2.imwrite(url_structed, image_copy)


def picture_process(file_dir):
    picture_list = glob.glob(file_dir)
    print(len(picture_list))
    for i in picture_list:
        # print(i)
        result_qst, num_qst = get_qst_struct_info(i)
        # print(num_qst)
        result_picasso, num_picasso = get_picasso_struct_info(i)
        # print(result_picasso)
        mark_image(i, result_qst, result_picasso)
    print("Processing completed")
    # return picture_list


def get_qst_feature(img_url, http='http://10.45.154.155:20280/rest/feature/structPicture'):
    base64_picture = pic2base64(img_url)
    json_content = {'objtype': '1', "picture": base64_picture}
    try:
        r = requests.post(http, json=json_content)
        response = json.loads(r.text)['objexts']
        print("Success1")
        return response
    except:
        print("error!")

def save_qst_info(file_dir):
    feature_dict = {}
    picture_list = glob.glob(file_dir)
    print(picture_list[0].split('\\')[-1].split('.')[0])
    for i in picture_list:
        img_url = i.split('\\')[-1].split('.')[0]
        result_qst, num_qst = get_qst_struct_info(i)
        if len(result_qst) == 1:
            feature_dict[img_url] = result_qst[0]['features']['featureData']
    save_file = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\qst_feature_dict.json'
    # print(save_file)
    with open(save_file, 'a') as f:
        f.write(json.dumps(feature_dict))


def qst_search(pic_byte, serialnumbers, http='http://10.45.154.155:20280/rest/feature/search'):
    json_content = {"objtype": "1",
                    "picture": pic_byte,
                    "serialnumbers": serialnumbers}
    r = requests.post(http, json=json_content)
    print(r._content)


def base64_to_float(kb_feature):
    feature = base64.b64decode(kb_feature)
    # print(len(feature))
    float_out = []
    for i in range(0, 256, 4):
        x = feature[i:i + 4]
        float_out.append(struct.unpack('f', x)[0])
    return float_out


def picasso_face_info(img_url, http='http://10.45.154.218:9010/verify/face/infos'):
    content = {"imageData": open(img_url, 'rb')}
    reply = requests.post(http, files=content)
    response = json.loads(reply.text)
    return response
    # return response['data'][0]['result'], len(response['data'][0]['result'])

if __name__ == '__main__':
    img_url = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\pic_file\zrf1.jpg'
    print(picasso_face_info(img_url))



    # img_url = r'D:\pycharmProjects\facecluster_tools_hjh\reid_evaluation\mp.jpg'
    # feature = get_qst_feature(img_url)
    # print(feature)
    # print(feature[0]['features']['featureData'])
    #
    # print(len(base64.b64decode(feature[0]['features']['featureData'])))
    # f = "hgBDY4z2PGESu1eDegACeDSohPLr94NbzOL3D+Exm2Rt8SYZoQrFIqBtiTwMZRVD7F4DzE2qG8XosvUPVGWrQAthoBrhDrNiuzznAtfaInyM4K1k6cqabRWDCIgbqwdczOCr1T0YlMQPBgJIuqqvgB4DmfVtPpwpKVheSDQiXmOGO6jRpGzvLg7sDuXqrGaV/0MMxkwlAK03DIvWQrO2c62rhSL7sFGnrA2jeRmU9Cb0IxNeEOKO1QdJJvhdbtpjK5T7Z7pKWIcudVZvvsFQAutEnb/uBI7huLXJ5EM8+zoAcQFxywdzuuQZQIM+S/ok691dpZUqjTMefe7p5eFsVA=="
    #
    # ffeature = base64.b64decode(f)
    # print(len(ffeature))


    # result_qst, num_qst = get_qst_struct_info(img_url)
    # print(result_qst)
    # result_picasso, num_picasso = get_detected_picture(img_url)
    # # print(result_picasso)
    # mark_image(img_url, result_qst, result_picasso)

    # qst_video_quest()

    # source_dir = r'D:\Dataset\20171024_3'
    # dir = source_dir + '\\*.jpg'
    # picture_process(dir)


    # dir = r'D:\Dataset\ReID_test\small_pic_picasso'+ '\\*.jpg'
    # save_qst_info(dir)