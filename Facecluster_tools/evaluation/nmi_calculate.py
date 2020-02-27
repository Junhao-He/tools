# coding:utf-8
"""
    hjh@2019.12.03
    计算聚类结果的互信息指标
"""
from sklearn import metrics


def calculate_nmi(data):
    label_true = []
    label_pred = []
    fused_list = []
    url_list = []
    count_f = 0
    count_u = 0
    for i in data[0]:
        if i['fused_id'] not in fused_list:
            fused_list.append(i['fused_id'])
            label_pred.append(count_f)
            count_f += 1
        else:
            label_pred.append(fused_list.index(i['fused_id']))
        try:
            url = i['img_url'].split('\\')[-2]
        except:
            url = i['img_url'].split('/')[-2]
        if url not in url_list:
            url_list.append(url)
            label_true.append(count_u)
            count_u += 1
        else:
            label_true.append(url_list.index(url))
    return metrics.normalized_mutual_info_score(label_true, label_pred)

if __name__ == "__main__":
    data = [[{"fused_id": '11', "img_url": '1\\1\\1'}, {"fused_id": '11', "img_url": '1\\1\\1'},
             {"fused_id": '11', "img_url": '1\\1\\1'}, {"fused_id": '11', "img_url": '2\\2\\2'},
             {"fused_id": '11', "img_url": '2\\2\\2'}, {"fused_id": '22', "img_url": '1\\1\\1'},
             {"fused_id": '22', "img_url": '1\\1\\1'}, {"fused_id": '22', "img_url": '2\\2\\2'},
             {"fused_id": '22', "img_url": '2\\3\\2'}, {"fused_id": '22', "img_url": '2\\3\\2'},
             {"fused_id": '33', "img_url": '2\\3\\2'}, {"fused_id": '33', "img_url": '2\\3\\2'},
             {"fused_id": '33', "img_url": '2\\3\\2'}, {"fused_id": '33', "img_url": '2\\3\\2'},
             {"fused_id": '33', "img_url": '2\\2\\2'}, {"fused_id": '33', "img_url": '2\\2\\2'},
             {"fused_id": '44', "img_url": '2\\4\\2'}, {"fused_id": '44', "img_url": '2\\4\\2'},
             {"fused_id": '55', "img_url": '2\\5\\2'}]]
    # print(calculate_nmi(data))
