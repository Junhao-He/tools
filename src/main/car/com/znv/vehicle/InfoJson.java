package com.znv.vehicle;

public class InfoJson {
	static String uuidJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \r\n" +
            "    \"data\": \"ef51b7a4bbaa11e6b086838b707212fa\"\r\n" +
            "}";
	
	static String subSystemJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \r\n" +
            "    \"data\": [\r\n" +
            "        {\r\n" +
            "            \"subSystemUuid\": \"28311552\", \r\n" +
            "            \"subSystemName\": \"卡口\"\r\n" +
            "        }, \r\n" +
            "        {\r\n" +
            "            \"subSystemUuid\": \"6291456\", \r\n" +
            "            \"subSystemName\": \"停车场\"\r\n" +
            "        }\r\n" +
            "    ]\r\n" +
            "}";
	
	static String evetnTypesJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \n" +
            "    \"data\": [\r\n" +
            "        {\r\n" +
            "            \"subSystemUuid\": \"6291456\", \r\n" +
            "            \"eventType\": 524545, \r\n" +
            "            \"eventTypeName\": \"入场\"\r\n" +
            "        }, \r\n" +
            "        {\r\n" +
            "            \"subSystemUuid\": \"6291456\", \r\n" +
            "            \"eventType\": 524546, \r\n" +
            "            \"eventTypeName\": \"出场\"\r\n" +
            "        }\r\n" +
            "    ]\r\n" +
            "}";
	
	static String MQJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \r\n" +
            "    \"data\": {\r\n" +
            "        \"destination\": \"openapi.pms.topic\", \r\n" +
            "        \"mqURL\": \"127.0.0.1:61616\"\r\n" +
            "    }\r\n" +
            "}";
	
	static String netZoneJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \r\n" +
            "    \"data\": [\r\n" +
            "        {\r\n" +
            "            \"netZoneUuid\": \"2773f64219ad420a8c038fbd04ba23bc\", \r\n" +
            "            \"netZoneName\": \"127.0.0.1\"\r\n" +
            "        }\r\n" +
            "    ]\r\n" +
            "}";
	
	static String kmsIpPortJson = "{\r\n" +
            "    \"errorCode\": 0, \r\n" +
            "    \"errorMessage\": null, \r\n" +
            "    \"data\": {\r\n" +
            "        \"port\": \"8081\", \r\n" +
            "        \"ip\": \"127.0.0.1\"\r\n" +
            "    }\r\n" +
            "}";
	static String parkingInfos = "{\"errorCode\":0,\"errorMessage\":\"获取停车场信息成功！\",\"data\":[{\"parkUuid\":\"e9334b1f1cd9477480af4ff51c6480bc\",\"parkName\":\"admin\",\"totalPlot\":37,\"leftPlot\":27,\"totalFiexdPlot\":0,\"leftFiexdPlot\":0,\"description\":null}]}";
	
	static String plotStatus = "{\"errorCode\":0,\"errorMessage\":\"获取车位信息成功！\",\"data\":{\"pageNo\":1,\"pageSize\":500,\"total\":45,\"list\":[{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"25\",\"plotUuid\":\"b366fa4a77144140b092ba363ac17546\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"35\",\"plotUuid\":\"3d784cbc8d474b38bdfe6aeb7e5487e4\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"40\",\"plotUuid\":\"9167b87120214110a0a899e5333bd057\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"24\",\"plotUuid\":\"2ba024baa4c24bc3944f70ab19d2949f\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"29\",\"plotUuid\":\"61a9056249fe49518a8322db13e32797\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"34\",\"plotUuid\":\"7f827713bed0494f84a642eac4780a2c\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"23\",\"plotUuid\":\"4a4045d063004d7ab9b93bc94b505ee1\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"28\",\"plotUuid\":\"61aa366ec54c4cedbc5e913489ebeabc\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"33\",\"plotUuid\":\"fcdbc392092b4e918c633b4e046b14b9\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"38\",\"plotUuid\":\"2244be33e41048078288fcf9d7828d46\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"22\",\"plotUuid\":\"967f618982124f26907b8fbc73b57c9d\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"27\",\"plotUuid\":\"40fe0de549bc4dbc9df76565aef2d594\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"32\",\"plotUuid\":\"a8356624b2394b1f9cc50ac7a6abbbd5\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"37\",\"plotUuid\":\"2835ceefa28a4f0d85e0c349a6dcbed4\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"21\",\"plotUuid\":\"7cabf9f68927489d98111d01628ca0a1\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"26\",\"plotUuid\":\"0a4110b7c8f048b1bbfa71ccc57e6fd0\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"31\",\"plotUuid\":\"03de4c9ff38148c09597d9313577b65c\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"36\",\"plotUuid\":\"24c119f895b6400eb807da9fbbc0c342\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"5\",\"plotUuid\":\"78a38b70f5664a01ad6ffd3ee2b1f420\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"10\",\"plotUuid\":\"394dc3a7f3264e1aa546f0780d130649\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"15\",\"plotUuid\":\"496fe627673045679e00fd701322040a\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"4\",\"plotUuid\":\"afa301ecdbb54ac49a0ca064806fcc48\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"9\",\"plotUuid\":\"b94931c1253341ffa15dabe2a8f54d1e\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"14\",\"plotUuid\":\"4b761b58fa1446298e0fd6044cc85501\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"3\",\"plotUuid\":\"1650e7d9bb7845349f1878cb101525c7\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"8\",\"plotUuid\":\"24c32afe37144dc6aa9d3d71041ca05e\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"13\",\"plotUuid\":\"2b7ea69b429d4b37bc8637059a3b13ec\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"2\",\"plotUuid\":\"7395cdc017084abea08af217429892b6\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"7\",\"plotUuid\":\"6e095da38d464773a7a8054b400dd1c9\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"12\",\"plotUuid\":\"9bb700828afd4a82930b2723fc45cb94\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"1\",\"plotUuid\":\"22d325f821f243eab82ebc49eeaf2fd1\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"6\",\"plotUuid\":\"304ddf14a7b84fbdaa329046dcc9c9a4\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"11\",\"plotUuid\":\"111185577ef44d269694f44abe3d249d\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"20\",\"plotUuid\":\"23179aee3f1a49aeb3ee06700cc69488\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"30\",\"plotUuid\":\"7cf6858c851b40c1b4b1df5d195b7b9e\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"19\",\"plotUuid\":\"c9828e8c68484e5fba6962dbe271c773\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"18\",\"plotUuid\":\"2c65e6ee2a33444aa2b8c7ae6d86033e\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"17\",\"plotUuid\":\"8071ceaf63424983992addc828fca63a\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"16\",\"plotUuid\":\"c982e3e2930d4e5ebcc9acbf32eed43b\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"39\",\"plotUuid\":\"3d57c5c7f95c45c08f16768f6cb9929c\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"45\",\"plotUuid\":\"3ee1b7e4dbbd4835a84e2192117c1f7a\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"44\",\"plotUuid\":\"cade17954e7d4c37ae3123931fd88d06\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"43\",\"plotUuid\":\"d79202e3423a4312a0d6735ca14bcced\",\"status\":0},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"42\",\"plotUuid\":\"bf7e0751eb8a4b2f9fe6d54d361dd6e4\",\"status\":1},{\"floorName\":\"1f\",\"floorUuid\":\"1b1e6002055e4afa92e5ba775bc2a151\",\"parkName\":\"南码头\",\"parkUuid\":\"df9a1f19ea8e46e09aa48aedc13b35f1\",\"plotNo\":\"41\",\"plotUuid\":\"5fff91081e8046289030833789448c46\",\"status\":1}]}}";
	
}
