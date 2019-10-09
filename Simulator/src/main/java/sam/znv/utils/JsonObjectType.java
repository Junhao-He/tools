package sam.znv.utils;

import com.alibaba.fastjson.JSONObject;

/*
字段类型
 */
public class JsonObjectType {
    //特征类型获取Object
    public static  String  objectType(String str){
        JSONObject jo = new JSONObject();
        jo.put("camera_id","String");
        jo.put("enter_time","String");
        jo.put("leave_time","String");
        jo.put("duration_time","Int");
        jo.put("gpsx","Float");
        jo.put("gpsy","Float");
        jo.put("task_idx","String");
        jo.put("track_idx","String");
        jo.put("feature","String");
        jo.put("sim_threshold","Float");
        jo.put("similarity","Float");
        jo.put("msg_source","String");
        jo.put("rt_feature","String");
        jo.put("img_width","Int");
        jo.put("img_height","Int");
        jo.put("quality_score","Float");
        jo.put("yaw","Float");
        jo.put("pitch","Float");
        jo.put("roll","Float");
        jo.put("age","Int");
        jo.put("gender","Int");
        jo.put("glass","Int");
        jo.put("mask","Int");
        jo.put("race","Int");
        jo.put("beard","Int");
        jo.put("emotion","Int");
        jo.put("eye_open","Int");
        jo.put("send_idx","Int");
        jo.put("frame_index","Int");
        jo.put("right_pos","Int");
        jo.put("office_id","String");
        jo.put("image_name","String");
        jo.put("top","Int");
        jo.put("mouth_open","Int");
        jo.put("camera_name","String");
        jo.put("big_picture_uuid","String");
        jo.put("glass","Int");
        jo.put("time_stamp","String");
        jo.put("bottom","Int");
        jo.put("camera_type","Int");
        jo.put("left_pos","Int");
        jo.put("office_name","String");
        jo.put("msg_type","String");
        jo.put("frame_time","String");
        jo.put("result_type","Int");
        jo.put("result_idx","String");
        jo.put("img_url","String");
        jo.put("img_mode","String");
        jo.put("img_url_data","String");
        jo.put("right","Int");
        jo.put("left","Int");
        jo.put("isSend","String");
        jo.put("fuse_id","String");
        jo.put("fuse_count","Int");
        jo.put("access_source","String");
        jo.put("uuid","String");
        jo.put("event_id","Long");
        jo.put("person_id","String");
        jo.put("camera_kind","Int");
        jo.put("lib_id","Int");
        jo.put("event_type","Int");
        jo.put("op_time","String");
        jo.put("is_alarm","String");
        jo.put("device_kind","Int");
        jo.put("door_event_id","String");
        jo.put("birth","String");
        jo.put("person_name","String");
        jo.put("control_event_id","String");
        jo.put("event_time","String");
        return jo.get(str).toString();
    }
}
