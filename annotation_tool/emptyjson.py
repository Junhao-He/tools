import cv2
import numpy as np
import matplotlib.pyplot as plt
import base64
import json
import os
import copy
import argparse


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('img', type=str, help='img file dir')
    parser.add_argument('json', type=str, help='dst json file')

    args = parser.parse_args()

    path = args.img
    if not (path.endswith('/') or path.endswith('\\')):
        path = path + '/'
    if args.json == "":
        jsonpath = path
    else:
        jsonpath = args.json
    for file in os.listdir(path):
        if ".jpg" not in file:
            continue
        img = cv2.imread(path + file)
        if img is None:
            continue
        height, width, channels = img.shape

        labelItem = {}
        labelItem["version"] = str(3)+"."+ str(16.7)
        labelItem["fillColor"] = [255, 0, 0, 128]
        labelItem["flags"] = {}
        with open(path + file, 'rb') as f:
            base64_data = base64.b64encode(f.read())
            img_encode = base64_data.decode()
        labelItem["imageData"] = img_encode
        labelItem["imageHeight"] = height
        labelItem["imagePath"] = file
        labelItem["imageWidth"] = width
        labelItem["lineColor"] = [0, 255, 0, 128]

        shapes = []

        labelItem["shapes"] = shapes
        if not (jsonpath.endswith('/') or jsonpath.endswith('\\')):
            jsonpath = jsonpath + '/'
        with open(jsonpath + file.replace('.jpg', '.json'), 'w') as file:
            json.dump(labelItem, file, indent=1)
    print("Down!")