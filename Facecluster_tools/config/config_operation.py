# -*- coding: utf-8 -*-
"""
 Created By 0049003121 on 2019/3/19
"""
from configparser import ConfigParser
import json


class ConfigOperation(object):
    @staticmethod
    def read_config_file(file_path):
        cp = ConfigParser()
        cp.read(file_path)
        section = cp.sections()[0]

        # 得到该section的所有键值对列表
        section_dict = {}
        section_list = cp.items(section)
        for i in section_list:
            section_dict[i[0]] = i[1]

        return section_dict

    @staticmethod
    def read_json_file(file_path):
        with open(file_path, 'r') as f:
            load_dict = json.load(f)

        return load_dict

    @staticmethod
    def write_json_file(file_path, data):
        with open(file_path, 'w') as f:
            json.dump(data, f, indent=2)  # indent显示多行



