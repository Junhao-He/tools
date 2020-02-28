# -*- coding: utf-8 -*-
"""
ES数据读取
"""
from elasticsearch import Elasticsearch
from elasticsearch import helpers


def get_data_from_es(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [name]
    if query == [] :
        doc = {
            "query": {
                "bool": {
                    # "must": [ { "range": { "enter_time": { "gt": "2018-06-01T11:19:29.000Z","lt": "2018-06-04T11:19:29.000Z"}}}]}}
                    "must": [ { }]}}
        }
    else:
        doc = {
            "query": {
                "bool": {
                    "must": [ { "range": { "enter_time": {"gte": query[0],"lte": query[1],"format":"yyyy-MM-dd HH:mm:ss","time_zone":"+08:00"}}}]}}
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
    # print 'total scroll_size: ', scroll_size)
    l = []
    docs = page1['hits']['hits']
    l += [x['_source'] for x in docs]

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
        l += [x['_source'] for x in docs]

    # global amount
    # # amount = len(l)
    # print('Data amount: ', amount)

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return l


def read_es(name, es_name, doc_type, query=[]):
    es = Elasticsearch(es_name)
    # name = 'fused_src_data_nightowl_ycys_34w_92'
    indices = [name]
    if query == [] :
        doc = {
            "query": {
                "bool": {
                    # "must": [ { "range": { "enter_time": { "gt": "2018-06-01T11:19:29.000Z","lt": "2018-06-04T11:19:29.000Z"}}}]}}
                    "must": [ { }]}}
        }
    else:
        doc = {
            "query": {
                "bool": {
                    #"must": [ { "range": { "enter_time": { "gte": query[0],"lte": query[1],"time_zone":"+08:00"}}}]}}
                    "must": [ { "range": { "enter_time": {"gte": query[0],"lte": query[1],"format":"yyyy-MM-dd HH:mm:ss","time_zone":"+08:00"}}}]}}
        }
    # Initialize the scroll

    scan_result = helpers.scan(es, query=doc, scroll='60m', index=indices)
    l = [x['_source'] for x in scan_result]

    # Start scrolling
    # while scroll_size > 0:
    #     # print ("Scrolling...")
    #     page = es.scroll(scroll_id=sid, scroll='2m')
    #     # Update the scroll ID
    #     sid = page['_scroll_id']
    #     # Get the number of results that we returned in the last scroll
    #     scroll_size = len(page['hits']['hits'])
    #     # print ("scroll size: " + str(scroll_size))
    #     # Do something with the obtained page
    #     docs = page['hits']['hits']
    #     l += [x['_source'] for x in docs]

    print('total docs: ', len(l))

    # file_path = name+'.json'
    # with open(file_path, 'a') as f:
    #     json.dump(l, f, indent=2)
    return l