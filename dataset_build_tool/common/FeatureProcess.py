# -*- coding:utf-8 -*-
"""
 Created by 0049003121 on 2019/3/15
"""

from sklearn import preprocessing
import numpy as np
import base64
import struct


class FeatureProcess(object):

    @staticmethod
    def feature_fuse(feature, rate, fused_feature):
        """
        :param feature:  特征
        :param rate: 融合率
        :param fused_feature: 融合特征
        :return: 返回融合特征
        """
        fused_feature = rate * np.array(feature) + fused_feature
        return fused_feature

    @staticmethod
    def feature_normalization(feature):
        """
        :param feature: 未规范化前的特征
        :return: 规范化后的特征
        """
        final_feature = preprocessing.normalize([feature], norm="l2")
        return final_feature[0]

    @staticmethod
    def feature_mean(features):
        """
        :param features: 相似特征组
        :return: 返回平均规范化特征
        """
        final_feature = np.mean(features, axis=0)
        return FeatureProcess.feature_normalization(final_feature)

    @staticmethod
    def base64tofloat(kb_feature, dimension=512):
        """
        :param kb_feature: base64编码特征
        :param dimension: 向量维度
        :return: float型的列表
        """
        feature = base64.b64decode(kb_feature)
        float_out = []
        if len(feature) >= (12 + dimension * 4):
            for i in range(12, 12 + dimension * 4, 4):
                x = feature[i:i + 4]
                float_out.append(struct.unpack('f', x)[0])
        return FeatureProcess.feature_normalization(float_out)

    @staticmethod
    def float2byte(f):
        return struct.pack('f', f)

    @staticmethod
    def float2base64(feature, dimension=512):
        """
        :param feature: float型特征
        :param dimension: 特征维度
        :return: 返回2060维编码
        """
        s = ''.encode(encoding='utf-8')
        byte_initial = '7qpXQoleAAAAAgAA'.encode(encoding='utf-8')
        for i in range(dimension):
            s += FeatureProcess.float2byte(feature[i])
        b64_out = byte_initial + base64.b64encode(s)
        return b64_out.decode()

    @staticmethod
    def calc_stsimilarity_to_euclidean(sim):
        stsrc = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57, 1.0]
        stdst = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
        cos=0.0
        if sim<=0:
            return 2
        if sim>=1.0:
            return 0
        for i in range(7):
            if sim>=stdst[i] and sim <=stdst[i+1]:
                cos=stsrc[i]+(stsrc[i+1]-stsrc[i])*(sim-stdst[i])/(stdst[i+1]-stdst[i])
                #print cos
                break
        eucl_dist=np.sqrt(2-2*cos)
        print(cos,eucl_dist)
        return eucl_dist

    @staticmethod
    def calc_euclidean_to_stsimilarity(eucl_dist):
        stsrc = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57, 1.0]
        stdst = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
        if eucl_dist<=0:
            return 1.0
        if eucl_dist>=2.0:
            return 0.0
        cos=1-eucl_dist*eucl_dist/2
        sim=0.0
        for i in range(7):
            if cos>=stsrc[i] and cos <=stsrc[i+1]:
                sim=stdst[i]+(stdst[i+1]-stdst[i])*(cos-stsrc[i])/(stsrc[i+1]-stsrc[i])
                print (stsrc[i],stsrc[i+1],sim)
                break
        print(cos,sim)
        return sim

    @staticmethod
    def normalize(score):
        """
        :param score: 特征余弦相似度计算的结果
        :return: 将得分映射到[0,1]
        """
        src_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57, 1.0]  # 源得分点
        dst_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]  # 目标映射点
        src_len = len(src_points)
        if score <= src_points[0]:
            return 0.0
        elif score >= src_points[src_len - 1]:
            return 1.0
        result = 0.0

        for i in range(1, src_len):
            if score < src_points[i]:
                result = dst_points[i - 1] + (score - src_points[i - 1]) * (dst_points[i] - dst_points[i - 1]) / (
                    src_points[i] - src_points[i - 1])
                break

        return result

    @staticmethod
    def denormalize(similarity_score):
        """
        :param similarity_score: 相似度得分
        :return: 返回余弦相似度
        """
        dst_points = [-1.0, 0.4, 0.42, 0.44, 0.48, 0.53, 0.57, 1.0]  # 目标得分点
        src_points = [0.0, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]  # 源映射点
        if(similarity_score <= src_points[0]):
            return dst_points[0]
        elif(similarity_score >= src_points[-1]):
            return dst_points[-1]

        else:
            result = 0.0
            for i in range(len(src_points)):
                if similarity_score < src_points[i]:
                    result = dst_points[i-1] + (similarity_score - src_points[i - 1]) * (dst_points[i] - dst_points[i - 1]) / (src_points[i] - src_points[i - 1])
                    break

            return result

    @staticmethod
    def cos(f1, f2):
        """
        :param f1: 特征向量1
        :param f2: 特征向量2
        :return: 余弦相似度计算结果
        """
        return np.dot(f1, f2)/(np.linalg.norm(f1)*np.linalg.norm(f2))

