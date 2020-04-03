# -*- coding: utf-8 -*-
"""
配置文件加载
"""
from configparser import ConfigParser
import json


class ConfigOperation(object):
    @staticmethod
    def read_config_file(file_path):
        cp = ConfigParser()
        cp.read(file_path, encoding='utf-8')
        section = cp.sections()[0]

        # 得到该section的所有键值对列表
        section_dict = {}
        section_list = cp.items(section)
        for i in section_list:
            section_dict[i[0]] = i[1]

        return section_dict

    @staticmethod
    def write_config_file(file_path, write_param_dict):
        cp = ConfigParser()
        cp.read(file_path, encoding='utf-8')
        section = cp.sections()[0]
        for key, value in write_param_dict.items():
            cp.set(section, key, value)
        with open(file_path, 'w') as f:
            cp.write(f)

    @staticmethod
    def read_json_file(file_path):
        with open(file_path, 'r') as f:
            load_dict = json.load(f)

        return load_dict

    @staticmethod
    def write_json_file(file_path, data):
        with open(file_path, 'w') as f:
            json.dump(data, f, indent=2)  # indent显示多行

if __name__ == '__main__':
    # import os
    # current_path = os.getcwd()

    # print(current_path)
    params = ConfigOperation.read_config_file('task.cfg')
    print(params['time.interval.range'])
    # ConfigOperation.write_config_file('task.cfg', {'time.interval.range':'90'})