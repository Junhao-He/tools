package sam.znv.feature;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import sam.znv.kafka.PicAttr;
import sam.znv.utils.GetDataFeature;
import sam.znv.utils.PictureUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FeatureInfoCommunity {
    static String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
    static String requestAttrUrl = "http://10.45.157.115:80/verify/attribute/gets";

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

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
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

    public static JSONObject getFeatureInfo(String picPath){

        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);

//        String feature = getFeatureFile("D:\\sentPicture\\feature\\",picPath);
        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();




        JSONObject jo = new JSONObject();
        String uuid = getUUID();
        JSONObject gpsInfo = new JSONObject();
        gpsInfo.put("gpsX",0.0f);
        gpsInfo.put("gpsY",0.0f);
        jo.put("gpsInfo",gpsInfo);

        JSONObject eventInfo = new JSONObject();
        eventInfo.put("leave_time",leavetime);
        eventInfo.put("eventId",5803);  //error"580370466884028041"
        eventInfo.put("durationTime",1);
        eventInfo.put("eventTime",entertime);
        eventInfo.put("enter_time",entertime);
        eventInfo.put("eventType",2001);
        jo.put("eventInfo",eventInfo);

        jo.put("msgType","faceSnapEvent");

        JSONObject picInfo = new JSONObject();
        picInfo.put("smallPictureUrl","http://10.45.152.148:9008/GetSmallPic?"+uuid);
        picInfo.put("bigPictureUrl","http://10.45.152.148:9008/GetPictureUrl/fastdfs_"+uuid);
        picInfo.put("dwFaceScore",95.0);
        picInfo.put("simThreshold",0.93f);
        picInfo.put("bigPicturePath","fastdfs_"+uuid);
        picInfo.put("roll",0.0f);
        picInfo.put("rtFeature",feature);
        jo.put("picInfo",picInfo);

        JSONObject humanFeature = new JSONObject();
        humanFeature.put("mouthOpen",0.0f);
        humanFeature.put("glass",picAttr.getEyeglass());
        humanFeature.put("gender",picAttr.getGender());
        humanFeature.put("race",picAttr.getRace());
        humanFeature.put("beard",picAttr.getBeard());
        humanFeature.put("smile",1);
        humanFeature.put("emotion",picAttr.getSmile());
        humanFeature.put("age",picAttr.getAge().intValue());
        humanFeature.put("mask",picAttr.getMask());
        humanFeature.put("eyeOpen",picAttr.getEyeOpen());
        jo.put("humanFeature",humanFeature);

        JSONObject taskInfo = new JSONObject();
        taskInfo.put("frameIndex","748");
        taskInfo.put("taskIdx","1555917855202");
        taskInfo.put("sendIdx",2);
        taskInfo.put("trackIdx",trackid);
        jo.put("taskInfo",taskInfo);

        jo.put("msgSource","camera");

        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("encoderId","11000000001110000055");
        deviceInfo.put("cameraKind",3);
        deviceInfo.put("camera_id","32011500001310000002");
        deviceInfo.put("channel","1");
        deviceInfo.put("cameraName","211_camera");
        deviceInfo.put("cameraType",30001);
        deviceInfo.put("cameraAddress","");
        jo.put("deviceInfo",deviceInfo);

        return jo;

    }
    public static String getFeatureFile(String featurePath,String picturePath){
        //D:\sentPicture\picture\11.jpg
        String jpgName = picturePath.split("\\\\")[picturePath.split("\\\\").length-1];  //11.jpg
        String pictureName = jpgName+".feature";
        String feature = getFile(featurePath,pictureName);
        return feature;
    }
    public static String getFile(String featurePath,String pictureName){
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(featurePath);
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
                fileList.add(fileIndex);
            }
        }
        String lastName = null;
        String feature = null;
        for(File fileString:fileList){
            lastName = fileString.getName().split("\\\\")[fileString.getName().split("\\\\").length-1];
            if(lastName.equals(pictureName)){
                feature = readToString(fileString.toString());
                break;
            }
        }
        return feature;
    }

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject constructMsg(String feature)
    {
        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
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

        jo.put("age",30);
        jo.put("gender",2);
        jo.put("glass",0);
        jo.put("mask",0);
        jo.put("race",0);
        jo.put("beard",0);
        jo.put("emotion",1);
        jo.put("eye_open",1);

        jo.put("send_idx",1);

        return jo;

    }
    //获取单张图片的feature
    public static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).get("feature").toString();
//        byte[] _feature = Base64.getDecoder().decode(feature);
        return feature;
    }
    //获取图片对应的属性
    public static PicAttr getPicAttr(String picPath,String requestUrl){
        byte[] data = PictureUtils.image2byte(picPath);
        String dd = GetDataFeature.getFeature("zs",data,requestUrl);
        System.out.println(dd);
        PicAttr picAttr = JSONObject.parseObject(dd, PicAttr.class);
        return picAttr;
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
