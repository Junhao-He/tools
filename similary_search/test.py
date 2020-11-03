# coding = utf-8 

# @Time : 2020/10/30 14:33
# @Author : 0049003071
# @File : test.py
# @Software: PyCharm

from elasticsearch import Elasticsearch


def get_data_from_ES(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    indices = [name]
    if query == [] :
        doc = {
            "_source": ['uuid', 'img_url', 'rt_feature', 'quality_score'],
                "query": {
                    "bool": {
                        # "must": [ { "range": { "enter_time": { "gt": "2018-06-01T11:19:29.000Z","lt": "2018-06-04T11:19:29.000Z"}}}]}}
                        "must": [ { }]}}
        }
    else:
        doc = {
            "_source": ['uuid', 'img_url', 'rt_feature', 'quality_score'],
                "query": {
                    "bool": {
                        "must": [ { "range": { "enter_time": {"gte": query[0],"lte": query[1],
                                                              "format":"yyyy-MM-dd HH:mm:ss", "time_zone":"+08:00"}}}]}}
        }
    # Initialize the scroll
    page1 = es.search(
        index=','.join(indices),
        doc_type=doc_type,
        scroll='2m',
        # search_type='scan',
        sort='_doc',
        size=1000,
        body=doc
    )
    sid = page1['_scroll_id']
    scroll_size = page1['hits']['total']
    print('total scroll_size: ', scroll_size)
    es_data = []
    docs = page1['hits']['hits']
    es_data += [x['_source'] for x in docs]

    # Start scrolling
    while scroll_size > 0:
        # print ("Scrolling...")
        page = es.scroll(scroll_id=sid, scroll='2m')
        # Update the scroll ID
        sid = page['_scroll_id']
        # Get the number of results that we returned in the last scroll
        scroll_size = len(page['hits']['hits'])
        # print ("scroll size: " + str(scroll_size))
        # Do something with the obtained page
        docs = page['hits']['hits']
        es_data += [x['_source'] for x in docs]

    print('total docs: ', len(es_data))

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return es_data


if __name__ == '__main__':
    # data = get_data_from_ES('history_fss_data_cq_similary_search', '10.45.154.218', 'history_data')
    # print(data[0])
    import time
    time.time()
    print(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(1604364860564)))