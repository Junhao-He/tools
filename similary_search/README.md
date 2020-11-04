1、执行命令运行工具
python similarity_search.py

2、相关参数配置见config.ini
[DEFAULT]
[ES.org]
;ES相关配置 
;数据索引对应host 
host = 10.45.154.161
;ES数据表名称
history_table = vpa_history_data_v1_00_001-0
;ES表type
type = history_data
[INFO.org]
;图片质量过滤阈值(可能存在[0,1] [0,100]两种质量分体系)
quality_threshold_1 = 55
quality_threshold_2 = 0.7
;图片相似度阈值
distance_threshold = 0.8
;图片存放目录 默认当前文件夹
dst_dir = ./
;最近邻搜索数目
neighbor_amount = 10

3、fdfs配置见fdfs_client.conf
（注：无其他要求只需修改tracker_server）
# connect timeout in seconds
# default value is 30s
connect_timeout=0.1

# network timeout in seconds
# default value is 30s
network_timeout=30

# the base path to store log files
base_path=/fastdfs/tracker

# tracker_server can ocur more than once, and tracker_server format is
# "host:port", host can be hostname or ip address
tracker_server=10.45.154.218:22122
# tracker_server=10.72.76.39:22122

#standard log level as syslog, case insensitive, value list:
### emerg for emergency
### alert
### crit for critical
### error
### warn for warning
### notice
### info
### debug
log_level=info

# if use connection pool
# default value is false
# since V4.05
use_connection_pool = false

# connections whose the idle time exceeds this time will be closed
# unit: second
# default value is 3600
# since V4.05
connection_pool_max_idle_time = 200

# if load FastDFS parameters from tracker server
# since V4.05
# default value is false
load_fdfs_parameters_from_tracker=false

# if use storage ID instead of IP address
# same as tracker.conf
# valid only when load_fdfs_parameters_from_tracker is false
# default value is false
# since V4.05
use_storage_id = false

# specify storage ids filename, can use relative or absolute path
# same as tracker.conf
# valid only when load_fdfs_parameters_from_tracker is false
# since V4.05
storage_ids_filename = storage_ids.conf


#HTTP settings
http.tracker_server_port=8080

#use "#include" directive to include HTTP other settiongs
##include http.conf
