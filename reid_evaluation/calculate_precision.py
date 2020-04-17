# coding=utf-8
"""
比对相似度TOP N图片，统计精确性
数据格式：
image_name top2 top5
2_0038_5 0 0
"""


def calculate_pre(file):
    top2p = 0
    top5p = 0
    amount = 0
    with open (file, 'r') as f:
        data = f.readlines()
        for i in range(1, len(data)):

            content = data[i].strip('\n')
            if len(content.split(' ')) > 2:
                content = content.split(' ')
                # print(content)
                top2p += int(content[1])
                top5p += int(content[2])
                amount += 1
    # print("Top 2 precision: {}".format('%.2f%%' % (top2p/amount/2 * 100)))
    # print("Top 5 precision: {}".format('%.2f%%' % (top5p/amount/5 * 100)))
    print("Top 2 precision: {:.2%}".format(top2p/amount/2))
    print("Top 5 precision: {:.2%}".format(top5p/amount/5))


if __name__ == "__main__":
    calculate_pre(r'./picasso_pre.txt')
