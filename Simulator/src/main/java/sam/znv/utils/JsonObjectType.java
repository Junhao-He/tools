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
        return jo.get(str).toString();
    }
}
