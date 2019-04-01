package com.znv.iot;

public class AlarmJson {
	//联通
	
	public static String unicomgas = "{\r\n" + 
			"    \"data\":{ \r\n" + 
			"        \"serialNumber\": \"863703031571560\",\r\n" + 
			"        \"type\": 1,\r\n" + 
			"        \"battery\": 65, \r\n" +
			"        \"reportTime\": \"2018-05-30 23:30:43\",\r\n" + 
			"        \"creater\": \"dmp\",\r\n" + 
			"        \"createDate\": \"2018-06-06 15:55:15\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"gas-monitor\"\r\n" +
			"}";/////
	
	public static String unicombody = "{\r\n" + 
			"    \"data\":{ \r\n" + 
			"        \"serialNumber\": \"863703031571560\",\r\n" + 
			"        \"type\": 1,\r\n" + 
			"        \"battery\": 65, \r\n" +
			"        \"reportTime\": \"2018-05-30 23:30:43\",\r\n" + 
			"        \"creater\": \"dmp\",\r\n" + 
			"        \"createDate\": \"2018-06-06 15:55:15\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"bodydetection-monitor\"\r\n" +
			"}";/////
	
	public static String unicomwaterline = "{\r\n" + 
			"    \"data\":{ \r\n" + 
			"        \"serialNumber\": \"863703031571560\",\r\n" + 
			"        \"type\": 1,\r\n" + 
			"        \"battery\": 65, \r\n" +
			"        \"reportTime\": \"2018-05-30 23:30:43\",\r\n" + 
			"        \"creater\": \"dmp\",\r\n" + 
			"        \"createDate\": \"2018-06-06 15:55:15\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"waterlinedetection-monitor\"\r\n" +
			"}";/////
	
	public static String unicomwaterbox = "{\r\n" + 
			"    \"data\":{ \r\n" + 
			"        \"serialNumber\": \"863703031571560\",\r\n" + 
			"        \"type\": 1,\r\n" + 
			"        \"battery\": 65, \r\n" +
			"        \"reportTime\": \"2018-05-30 23:30:43\",\r\n" + 
			"        \"creater\": \"dmp\",\r\n" + 
			"        \"createDate\": \"2018-06-06 15:55:15\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"waterboxdetection-monitor\"\r\n" +
			"}";/////
	
	public static String unicomfire = "{\r\n"+ 
			"    \"data\":{\r\n" + 
			"        \"id\": 17,\r\n" + 
			"        \"serialNumber\": \"863703036179617\",\r\n" + 
			"        \"dataSn\": \"00000014\",\r\n" + 
			"        \"bizCode\": \"01\",\r\n" + 
			"        \"bizData\": \"06\",\r\n" + 
			"        \"reportTime\": \"2018-05-06 10:46:28\",\r\n" + 
			"        \"updater\": \"dmp\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"smoke-sensor\"\r\n" +
			"}";////
	
	public static String unicommove = "{\r\n"+
			"    \"data\":{\r\n" + 
			"        \"serialNumber\": \r\n" + 
			"        \"863703031571560\",\r\n" + 
			"        \"type\": 1, \r\n" + 
			"        \"battery\": 65, \r\n" + 
			"        \"reportTime\": \"2018-05-30 23:30:43\", \r\n" + 
			"        \"creater\": \"dmp\", \r\n" + 
			"        \"createDate\": \"2018-06-06 15:55:15\"\r\n" + 
			"    },\r\n" + 
			"    \"type\": \"infra-red\"\r\n" +
			"}";//////
	
	public static String unicomdici = "{\r\n"+
			"    \"type\": \"geo-mnt\",\r\n" + 
			"    \"data\":{\r\n" +
			"        \"id\": 14,\r\n" + 
			"        \"serialNumber\": \"863702018031303\",\r\n" + 
			"        \"reportTime\": \"2018-06-06 15:36:15\",\r\n" + 
			"        \"occupiedStatus\": 1,\r\n" + 
			"        \"type\": \"occupancy\",\r\n" + 
			"        \"addr\": \"正方中路\",\r\n" + 
			"        \"lng\": 116.403963,\r\n" + 
			"        \"lat\": 39.915119 \r\n" + 
			"    }\r\n" + 
			"}";////
	
	public static String unicomyijian = "{\r\n"+
			"    \"data\":{\r\n" + 
			"        \"id\": 17,\r\n" +
			"        \"serialNumber\": \"863703036179617\",\r\n" + 
			"        \"dataTime\": \"2018-05-06 10:46:28\",\r\n" + 
			"        \"batteryVoltage\": 3.588,\r\n" + 
			"        \"signalStrength\": -81,\r\n" + 
			"        \"SNR\": 24,\r\n" + 
			"        \"cellid\":\"27DF92\",\r\n" + 
			"        \"updater\": \"dmp\"\r\n" +
			"    },\r\n" + 
			"    \"type\": \"alarm-btn\"\r\n" + 
			"}";////
	
	
	
	//未来
	public static String bstargas = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"煤气告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS05A-L-02\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";
	
	public static String bstarbody = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"人体感应告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS05A-L-02\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";
	
	public static String bstarfire = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"火灾告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS06A-L-02\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";////
	
	public static String bstarwaterline = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"水位告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS06A-L-02\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";////
	
	public static String bstarwaterbox = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"水箱检测告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS06A-L-02\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";////
	
	public static String bstarmove = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"监测区域无活动信号告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS0705A-L\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";//////
	
	public static String bstardici = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"违停告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS08A-L-00\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";////
	
	public static String bstaryijian = "{\r\n" + 
			"    \"alarm_th\": \"\",\r\n" + 
			"    \"alarmdesc\": \"一键求助告警\",\r\n" + 
			"    \"alarmlevel\": \"1\",\r\n" + 
			"    \"alarmtype\": 1,\r\n" + 
			"    \"datavalue\": \"\",\r\n" + 
			"    \"dealmsg\": \"告警\",\r\n" + 
			"    \"deveui\": \"70B3D53AF001C5C7\",\r\n" + 
			"    \"devicelocation\": \"正方中路\",\r\n" + 
			"    \"devtype\": \"SS09A-L-05\",\r\n" + 
			"    \"flag\": 1,\r\n" + 
			"    \"time\": \"2018-04-14 18:43:45\"\r\n" + 
			"}";////
	
	
	//电信
	public static String telecomgas = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"    \"deviceId\": \"004a070001111156\",\r\n" + 
			"    \"deviceName\": \"燃气探测器\",\r\n" + 
			"    \"deviceType\":\"1\",\r\n" + 
			"    \"deviceAddress\":\"正方中路\",\r\n" + 
			"    \"alarmType\":20011,\r\n" + 
			"    \"alarmLevel\":1,\r\n" + 
			"    \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"    \"alarmNo\":\"123123123\",\r\n" + 
			"    \"alarmTitle\":\"煤气告警\",\r\n" + 
			"    \"alarmOccure\":1,\r\n" + 
			"    \"picInfo\":{\r\n" + 
			"    \"pictureUrl\":\"\",\r\n" + 
			"    \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";/////
	
	public static String telecombody = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"    \"deviceId\": \"004a070001111156\",\r\n" + 
			"    \"deviceName\": \"人体感应探测器\",\r\n" + 
			"    \"deviceType\":\"1\",\r\n" + 
			"    \"deviceAddress\":\"正方中路\",\r\n" + 
			"    \"alarmType\":20011,\r\n" + 
			"    \"alarmLevel\":1,\r\n" + 
			"    \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"    \"alarmNo\":\"123123123\",\r\n" + 
			"    \"alarmTitle\":\"人体感应告警\",\r\n" + 
			"    \"alarmOccure\":1,\r\n" + 
			"    \"picInfo\":{\r\n" + 
			"    \"pictureUrl\":\"\",\r\n" + 
			"    \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";/////
	
	public static String telecomwaterline = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"    \"deviceId\": \"004a070001111156\",\r\n" + 
			"    \"deviceName\": \"水位探测器\",\r\n" + 
			"    \"deviceType\":\"1\",\r\n" + 
			"    \"deviceAddress\":\"正方中路\",\r\n" + 
			"    \"alarmType\":20011,\r\n" + 
			"    \"alarmLevel\":1,\r\n" + 
			"    \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"    \"alarmNo\":\"123123123\",\r\n" + 
			"    \"alarmTitle\":\"水位告警\",\r\n" + 
			"    \"alarmOccure\":1,\r\n" + 
			"    \"picInfo\":{\r\n" + 
			"    \"pictureUrl\":\"\",\r\n" + 
			"    \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";/////
	
	public static String telecomwaterbox = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"    \"deviceId\": \"004a070001111156\",\r\n" + 
			"    \"deviceName\": \"水箱检测器\",\r\n" + 
			"    \"deviceType\":\"1\",\r\n" + 
			"    \"deviceAddress\":\"正方中路\",\r\n" + 
			"    \"alarmType\":20011,\r\n" + 
			"    \"alarmLevel\":1,\r\n" + 
			"    \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"    \"alarmNo\":\"123123123\",\r\n" + 
			"    \"alarmTitle\":\"水箱检测告警\",\r\n" + 
			"    \"alarmOccure\":1,\r\n" + 
			"    \"picInfo\":{\r\n" + 
			"    \"pictureUrl\":\"\",\r\n" + 
			"    \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";/////
	
	public static String telecomfire = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"        \"deviceId\": \"004a070001111156\",\r\n" + 
			"        \"deviceName\": \"烟雾探测器\",\r\n" + 
			"        \"deviceType\":\"1\",\r\n" + 
			"        \"deviceAddress\":\"正方中路ַ\",\r\n" + 
			"        \"alarmType\":20003,\r\n" + 
			"        \"alarmLevel\":1,\r\n" + 
			"        \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"        \"alarmNo\":\"123123123\",\r\n" + 
			"        \"alarmTitle\":\"火灾告警\",\r\n" + 
			"        \"alarmOccure\":1,\r\n" + 
			"        \"picInfo\":{\r\n" + 
			"        \"pictureUrl\":\"\",\r\n" + 
			"        \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";////
	
	public static String telecommove = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"        \"deviceId\": \"004a070001111156\",\r\n" + 
			"        \"deviceName\": \"红外探测器\",\r\n" + 
			"        \"deviceType\":\"1\",\r\n" + 
			"        \"deviceAddress\":\"正方中路\",\r\n" + 
			"        \"alarmType\":20020,\r\n" + 
			"        \"alarmLevel\":1,\r\n" + 
			"        \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"        \"alarmNo\":\"123123123\",\r\n" + 
			"        \"alarmTitle\":\"监控区域无活动信号告警\",\r\n" + 
			"        \"alarmOccure\":1,\r\n" + 
			"        \"picInfo\":{\r\n" + 
			"        \"pictureUrl\":\"\",\r\n" + 
			"        \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";//////
	
	public static String telecomdici = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"    \"data\": [{\r\n" + 
			"        \"deviceId\": \"004a070001111156\",\r\n" + 
			"        \"deviceName\": \"违停地磁\",\r\n" + 
			"        \"deviceType\":\"1\",\r\n" + 
			"        \"deviceAddress\":\"正方中路\",\r\n" + 
			"        \"alarmType\":20038,\r\n" + 
			"        \"alarmLevel\":1,\r\n" + 
			"        \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"        \"alarmNo\":\"123123123\",\r\n" + 
			"        \"alarmTitle\":\"违停告警\",\r\n" + 
			"        \"alarmOccure\":1,\r\n" + 
			"        \"picInfo\":{\r\n" + 
			"        \"pictureUrl\":\"\",\r\n" + 
			"        \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";////
	
	public static String telecomyijian = "{\r\n" + 
			"    \"type\": \"alarm\",\r\n" + 
			"        \"data\": [{\r\n" + 
			"        \"deviceId\": \"004a070001111156\",\r\n" + 
			"        \"deviceName\": \"一键求助\",\r\n" + 
			"        \"deviceType\":\"1\",\r\n" + 
			"        \"deviceAddress\":\"正方中路\",\r\n" + 
			"        \"alarmType\":20019,\r\n" + 
			"        \"alarmLevel\":1,\r\n" + 
			"        \"alarmTime\":\"2018-03-06 00:00:00\",\r\n" + 
			"        \"alarmNo\":\"123123123\",\r\n" + 
			"        \"alarmTitle\":\"一键求助告警\",\r\n" + 
			"        \"alarmOccure\":1,\r\n" + 
			"        \"picInfo\":{\r\n" + 
			"        \"pictureUrl\":\"\",\r\n" + 
			"        \"pictureBase64\":\"\"\r\n" + 
			"    },\r\n" + 
			"    \"alarmExt\":{}\r\n" + 
			"    }]\r\n" + 
			"}";////
}
