package com.znv.vehicle;


import com.znv.hk8700.protobuf.EmuServerDevEvent;
import com.znv.hk8700.protobuf.EventDis;
import com.znv.hk8700.protobuf.PmsEvent;
import com.znv.utils.DateUtil;



public class SendMsg {
	
	//private List<String> gateId = Arrays.asList("31010100226010100009","31010400316000000015");
	
	public static String deviceId="31010400316000000015";
	public static String plateNo="苏A987B4";
	public static String picurl = "";
	public static int eventtype = 0;
	public static String rawId = "";
	public byte[] SendPms(){		
		
		EmuServerDevEvent.CEmuEvent.Builder cEmuEventBuilder = EmuServerDevEvent.CEmuEvent.newBuilder();
		
		cEmuEventBuilder.setEventCmd(2);
		cEmuEventBuilder.setParkName("力维停车场");
		cEmuEventBuilder.setParkIndex("12");
  	    cEmuEventBuilder.setGateName("南门");

  	    cEmuEventBuilder.setGateIndex(deviceId);
  	    cEmuEventBuilder.setRoadwayName("车道1");

  	    cEmuEventBuilder.setRoadwayIndex(rawId);
  	    cEmuEventBuilder.setVehicleType(1);
  	    
  	    cEmuEventBuilder.setPlateType(1);
  	   
  	    cEmuEventBuilder.setVehicleColor(1);
  	    cEmuEventBuilder.setPlateColor(1);

  	    cEmuEventBuilder.setPlateNo(plateNo);
  	    cEmuEventBuilder.setCardNo("34345");

  	    cEmuEventBuilder.setEventIndex("456");

  	    cEmuEventBuilder.setRoadwayType(1);


  	    cEmuEventBuilder.setMainLogo(2);

  	    cEmuEventBuilder.setSubLogo(5);
  	    cEmuEventBuilder.setSubModel(7);

  	    cEmuEventBuilder.setPlateBelieve(27);

  	    cEmuEventBuilder.setAlarmCar(1);
  	    	    
		EmuServerDevEvent.CEmuPicUrl.Builder cEmuPicUrlBuilder = EmuServerDevEvent.CEmuPicUrl.newBuilder();

		
		cEmuPicUrlBuilder.setVehiclePicUrl(picurl);
		cEmuPicUrlBuilder.setPlatePicUrl(picurl);
		
		cEmuEventBuilder.setPicUrl(cEmuPicUrlBuilder);

		PmsEvent.MsgPmsEvent.Builder msgPmsEventBuilder = PmsEvent.MsgPmsEvent.newBuilder();
		msgPmsEventBuilder.setPmsEvent(cEmuEventBuilder);
		PmsEvent.MsgPmsEvent msgPmsEvent = msgPmsEventBuilder.build();
		
		EventDis.CommEventLog.Builder commEventLogBuilder = EventDis.CommEventLog.newBuilder();
		commEventLogBuilder.setLogId("123");
		commEventLogBuilder.setEventState(0);
		commEventLogBuilder.setEventType(eventtype);
		commEventLogBuilder.setStopTime(DateUtil.getStringDate());
		commEventLogBuilder.setExtInfo(msgPmsEvent.toByteString());
		EventDis.CommEventLog commEventLog = commEventLogBuilder.build();
        byte[] content = commEventLog.toByteArray();
                
        return content;
    }
}
