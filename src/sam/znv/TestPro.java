package sam.znv;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import sam.znv.utils.GetDataFeature;
import sam.znv.utils.PictureUtils;

public class TestPro {
    public static void main(String[] args) throws IOException {
        sendIdx1("D:\\Users\\User\\Desktop\\pic\\dd.jpg");
    }

    public static void sendIdx1(String picPath){
        System.out.println("*********************"+picPath);
//        String picPath = "D:\\pictures\\zs.jpg";
        String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
        String feature = getFeature(picPath,requestUrl);
        System.out.println(feature);
        String entertime = getEnterAndLeaveTime1().get(0);
        String leavetime = getEnterAndLeaveTime1().get(1);
        String trackid = getTrackIdx();


        JSONObject jo = new JSONObject();
        jo.put("camera_id","32011500001310000002");
        jo.put("enter_time",entertime);
        jo.put("leave_time",leavetime);
        jo.put("duration_time",1);
        jo.put("gpsx",0.0f);
        jo.put("gpsy",0.0f);
        jo.put("task_idx","f11081621a3211e9b173801844e9b5f4");
        jo.put("track_idx",trackid);
        jo.put("feature",feature);
        jo.put("sim_threshold",0.93f);
        jo.put("rt_feature",feature);
        jo.put("img_width",125);
        jo.put("img_height",220);
        jo.put("quality_score",1.0f);
        jo.put("yaw",0.0f);
        jo.put("pitch",0.0f);
        jo.put("roll",0.0f);
        jo.put("age",22);
        jo.put("gender",0);
        jo.put("glass",0);
        jo.put("mask",0);
        jo.put("race",0);
        jo.put("beard",0);
        jo.put("emotion",0);
        jo.put("eye_open",0);
        jo.put("send_idx",1);
        ZKafkaProducter.getInstance().sendMessage("peim.alarm.topic", jo);

    }
    public static void sendIdx2(){

    }

    public static String getTrackIdx(){
        Date now = new Date();
        long ts = now.getTime();
        return ts+"";
    }

    public static String[] getEnterAndLeaveTime(){
        String dt[] = new String[2];
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String et = dateFormat.format(now);
        long later = now.getTime()+3000;
        Date dd = new Date(later);
        String lt = dateFormat.format(dd);
        dt[0] = et;
        dt[1] = lt;
        return dt;
    }

    public static ArrayList<String> getEnterAndLeaveTime1(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String entertime = df.format(date);
        long time=date.getTime()+20000; //这是毫秒数
        String leavetime = df.format(time);
        ArrayList<String> tt = new ArrayList<>();
        tt.add(entertime);
        tt.add(leavetime);
        return tt;
    }

    public static String featureProcess(){
        String picPath = "D:\\pictures\\zs.jpg";
        String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
        String requestUrl2 = "http://10.45.157.115:9001/verify/attribute/gets";
        String requestUrl3 = "http://10.45.157.115:80/verify/feature/batchGet";
        return getFeature(picPath,requestUrl);
//        getPicAttr(picPath,requestUrl2);
//        getBatchFeature(picPath,requestUrl3);
    }
    //获取单张图片的feature
    public static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).get("feature").toString();
//        byte[] _feature = Base64.getDecoder().decode(feature);
        return feature;
    }
    //获取图片对应的属性
    public static void getPicAttr(String picPath,String requestUrl){
        byte[] data = PictureUtils.image2byte(picPath);
        String dd = GetDataFeature.getFeature("zs",data,requestUrl);
        System.out.println(dd);
    }
    //批量获取特征
    public static void getBatchFeature(String picPath,String requestUrl){
        byte[] data = PictureUtils.image2byte(picPath);
        JSONObject jo = new JSONObject();
        jo.put("image_name","zs.jpg");
        jo.put("image_data",data);
        List<JSONObject> person=new ArrayList<>();
        person.add(jo);
        JSONArray ja = GetDataFeature.getBatchFeature2(person,requestUrl);
        System.out.println(ja.getString(0));
    }
}
