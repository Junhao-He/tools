package sam.znv.feature;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.PropertiesUtil;
import sam.znv.kafka.PicAttr;
import sam.znv.utils.GetDataFeature;
import sam.znv.utils.PictureUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.abs;
import static sam.znv.feature.FeatureInfoCommunity.getUUID;

public class FeatureInfo {
    //242特征
    static String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
    static String requestAttrUrl = "http://10.45.157.115:80/verify/attribute/gets";
    //245特征
    //static String requestUrl = "http://10.45.154.193:80/verify/feature/gets";
    //static String requestAttrUrl = "http://10.45.154.193:80/verify/attribute/gets";
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

    public static JSONObject getFeatureInfoTest(String picPath){
        JSONObject jo = new JSONObject();
        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);

        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        jo.put("frameIndex","748");
        jo.put("taskIdx", "1555917855202");
        jo.put("sendIdx", 2);
        jo.put("trackIdx", trackid);
        jo.put("msgSource", "camera");
        jo.put("smallPictureUrl", "http://10.45.152.148:9008/GetSmallPic?6e556154a7f14093b1c535a4807a6ff5");
        jo.put("bigPictureUrl", "http://10.45.152.148:9008/GetPictureUrl/fastdfs_6e556154a7f14093b1c535a4807a6ff5");
        jo.put("dwFaceScore", 95.0);
        jo.put("simThreshold", 0.93);
        jo.put("bigPicturePath", "fastdfs_6e556154a7f14093b1c535a4807a6ff5");
        jo.put("roll", 0.0);
        jo.put("rtFeature",feature);
        jo.put("personAge", 25);
        jo.put("isStranger", 1);
        jo.put("occurNumStranger", 1);
        jo.put("customStrangerFlag", 1);
        jo.put("personGender", 0);
        jo.put("personLib", 10000);
        jo.put("personId", "4c453651-dc32-46ec-bb4e-d0e64fda911b0");
        jo.put("timeStranger", "2019-06-06 15,22,30");
        jo.put("recgUuid", "AWsrquzI2CnlbI_nSnDb");
        jo.put("msgType", "faceRecgResult");
        jo.put("gpsX", 0.0);
        jo.put("gpsY", 0.0);
        jo.put("eventId", "5803");
        jo.put("leave_time", leavetime);
        jo.put("durationTime", 1);
        jo.put("eventTime", entertime);
        jo.put("eventType", 2001);
        jo.put("enter_time", "2019-06-06 12,19,39");
        jo.put("mouthOpen", 0.0);
        jo.put("glass", 0);
        jo.put("emotion", 0);
        jo.put("gender", 0);
        jo.put("race", 0);
        jo.put("beard", 1);
        jo.put("age", 25);
        jo.put("smile", 1);
        jo.put("mask", 0);
        jo.put("eyeOpen", 0);
        jo.put("encoderId", "11000000001110000055");
        jo.put("cameraKind", 3);
        jo.put("cameraId", "null");
        jo.put("camera_id", "32010100001310009103");
        jo.put("channel", "1");
        jo.put("cameraName", "211_camera");
        jo.put("cameraType", 30001);
        jo.put("cameraAddress", "");
        jo.put("opTime", entertime);
        jo.put("isAlarm", 0);

        return jo;
    }

    /**
     * 1.32版本door数据
     * @param picPath 单个图片路径
     * @return
     */
    public static JSONObject getFeatureInfoFusionDoor(String picPath){
        JSONObject jo = new JSONObject();
        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);
        long unboundedLong = new Random().nextLong();
        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        jo.put("access_source","community");
        jo.put("top",0);
        jo.put("face_pass_num","");
        jo.put("small_picture_url", "http,//10.45.152.148,9008/GetSmallPic?6e556154a7f14093b1c535a4807a6ff5");
        jo.put("big_picture_url","");
        jo.put("office_id","");
        jo.put("event_type", 2001);
        jo.put("gpsz", 0.0f);
        jo.put("glass", 0);
        jo.put("img_width",110);
        jo.put("beard", 1);
        jo.put("bottom",0);
        jo.put("img_height",120);
        jo.put("camera_type", 30001);
        jo.put("face_pass_num","");
        jo.put("event_desc","");
        jo.put("report_time",entertime);
        jo.put("sim_threshold",0.92f);
        jo.put("msg_type","fss-analysis-n-project-v1-2");
        jo.put("gpsx", 0.0f);
        jo.put("house_address","");
        jo.put("gpsy", 0.0f);
        jo.put("gender", 0);
        jo.put("camera_address","");
        jo.put("right_pos",0);
        jo.put("feature",feature);
        jo.put("camera_kind", 3);
        jo.put("mouth_open", 0);
        jo.put("camera_name", "211_camera");
        jo.put("direction","");
        jo.put("mask", 0);
        jo.put("leave_time", leavetime);
        jo.put("camera_id", "32011500001310000002");
        jo.put("eye_open", 0);
        jo.put("event_time", entertime);
        jo.put("left_pos",0);
        jo.put("office_name","");
        jo.put("event_id", abs(unboundedLong));
        jo.put("emotion", 0);
        jo.put("small_picture_path","");
        jo.put("duration_time",1);
        jo.put("big_picture_path","");
        jo.put("enter_time", entertime);
        jo.put("age", 25);
        jo.put("msg_source", "door");
        jo.put("race", 0);
        jo.put("smile", 1);
        jo.put("quality_score",1.0f);
        return jo;
    }
    /**
     * 1.32版本access_source = community 中 camera数据
     * @param picPath 单个图片路径
     * @return
     */
    public static JSONObject getFeatureInfoFusion_community_camera(String picPath){

        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);
        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();

        JSONObject jo = new JSONObject();
        String uuid = getUUID();
        jo.put("access_source","community");
        jo.put("gpsx",0.0f);
        jo.put("gpsx",0.0f);
        jo.put("top",0);
        jo.put("leave_time",leavetime);
        jo.put("event_id",5801);
        jo.put("duration_time",1);
        jo.put("event_time",entertime);
        jo.put("enter_time",entertime);
        jo.put("event_type",2001);
        jo.put("msg_type","faceSnapEvent");
        jo.put("small_picture_url","http://10.45.152.148:9008/GetSmallPic?"+uuid);
        jo.put("big_picture_url","http://10.45.152.148:9008/GetPictureUrl/fastdfs_"+uuid);
        jo.put("sim_threshold",0.93f);
        jo.put("big_picture_path","fastdfs_"+uuid);
        jo.put("roll",0.0f);
        jo.put("feature",feature);
        jo.put("mouth_open",0);
        jo.put("glass",picAttr.getEyeglass());
        jo.put("gender",picAttr.getGender());
        jo.put("race",picAttr.getRace());
        jo.put("beard",picAttr.getBeard());
        jo.put("smile",1);
        jo.put("emotion",picAttr.getSmile());
        jo.put("age",picAttr.getAge().intValue());
        jo.put("mask",picAttr.getMask());
        jo.put("eye_open",picAttr.getEyeOpen());
        jo.put("frame_index","748");
        jo.put("task_idx","1555917855202");
        jo.put("send_idx",2);
        jo.put("track_idx",trackid);
        jo.put("msg_source","camera");
        jo.put("encoder_id","11000000001110000055");
        jo.put("camera_kind",3);
        jo.put("camera_id","32011500001310000002");
        jo.put("channel","1");
        jo.put("camera_name","211_camera");
        jo.put("camera_type",30001);
        jo.put("camera_address","");
        jo.put("quality_score",1.0f);
        jo.put("yaw",0.0f);
        jo.put("pitch",0.0f);

        return jo;
    }
    /**
     * 1.32版本access_source = fss 中 camera数据
     * @param picPath 单个图片路径
     * @return
     */
    public static JSONObject getFeatureInfoFusion_fss_camera(String picPath){

        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);
        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        JSONObject jo = new JSONObject();
        jo.put("access_source","fss");
        jo.put("msg_source","camera");
        jo.put("gender",picAttr.getGender());
        jo.put("roll",0.0f);
        jo.put("task_idx","f11081621a3211e9b173801844e9b5f4");
        jo.put("track_idx",trackid);
        jo.put("frame_index","748");
        jo.put("gpsx",0.0f);
        jo.put("gpsy",0.0f);
        jo.put("right_pos",0);
        jo.put("feature",feature);
        jo.put("camera_name","");
        jo.put("camera_id","32010100001310009103");
        jo.put("enter_time",entertime);
        jo.put("leave_time",leavetime);
        jo.put("duration_time",1);
        jo.put("sim_threshold",0.93f);
        jo.put("img_width",125);
        jo.put("img_height",220);
        jo.put("quality_score",1.0f);
        jo.put("yaw",0.0f);
        jo.put("pitch",0.0f);

        jo.put("age",picAttr.getAge().intValue());
        jo.put("gender",picAttr.getGender());
        jo.put("glass",picAttr.getEyeglass());
        jo.put("mask",picAttr.getMask());
        jo.put("race",picAttr.getRace());
        jo.put("beard",picAttr.getBeard());
        jo.put("emotion",picAttr.getSmile());
        jo.put("eye_open",picAttr.getEyeOpen());
        jo.put("top",0);

        jo.put("send_idx",1);


        return jo;

    }
    /**
     * 黑人算法字段模拟
     * @param picPath 单个图片路径
     * @return
     */
    public static JSONObject getFeatureInfoFusionBlackMan(String picPath){

        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);
        System.out.println(feature);
        System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        JSONObject jo = new JSONObject();
        jo.put("msg_source","camera");
        jo.put("gender",picAttr.getGender());
        jo.put("roll",0.0f);
        jo.put("task_idx","f11081621a3211e9b173801844e9b5f4");
        jo.put("track_idx",trackid);
        jo.put("frame_index","748");
        jo.put("gpsx",0.0f);
        jo.put("gpsy",0.0f);
        jo.put("right_pos",0);
        jo.put("feature",feature);
        jo.put("camera_name","");
        jo.put("camera_id","32010100001310009103");
        jo.put("enter_time",entertime);
        jo.put("leave_time",leavetime);
        jo.put("duration_time",1);
        jo.put("sim_threshold",0.93f);
        jo.put("img_width",125);
        jo.put("img_height",220);
        jo.put("quality_score",1.0f);
        jo.put("yaw",0.0f);
        jo.put("pitch",0.0f);

        jo.put("age",picAttr.getAge().intValue());
        jo.put("gender",picAttr.getGender());
        jo.put("glass",picAttr.getEyeglass());
        jo.put("mask",picAttr.getMask());
        jo.put("race",picAttr.getRace());
        jo.put("beard",picAttr.getBeard());
        jo.put("emotion",picAttr.getSmile());
        jo.put("eye_open",picAttr.getEyeOpen());
        jo.put("top",0);

        jo.put("send_idx",1);


        return jo;

    }
    public static JSONObject getFeatureInfo(String picPath){

        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);

        //System.out.println(feature);
        //System.out.println(picAttr);

        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        ArrayList<String> cameraIds = new ArrayList<>();
        int id = new Random().nextInt(5);
        JSONObject jo = new JSONObject();
        cameraIds.add("32011500001310000002");
        cameraIds.add("32011500001310000001");
        cameraIds.add("32010400001310007002");
        cameraIds.add("32010400001310007002");
        cameraIds.add("32010400001310007002");
        cameraIds.add("110000010022");
        cameraIds.add("32010400001110005002");
        cameraIds.add("32010400001110007052");
        cameraIds.add("000000010001");
        cameraIds.add("000000010023");
        cameraIds.add("000000010002");
        cameraIds.add("32010400001110007002");
        cameraIds.add("32011500001310000002");
        cameraIds.add("32011500001310000002");
        cameraIds.add("32011500001310000002");

//        jo.put("camera_id",cameraIds.get(id%15));
        jo.put("camera_id","32011500001310000004");
        jo.put("enter_time",entertime);
        jo.put("leave_time",leavetime);
        jo.put("duration_time",1);
        jo.put("gpsx",0.0f);
        jo.put("gpsy",0.0f);
        jo.put("task_idx","f11081621a3211e9b173801844e9b5f4");
        jo.put("track_idx",trackid);
        jo.put("feature",feature);
        jo.put("sim_threshold",0.92f);
        jo.put("img_width",125);
        jo.put("img_height",220);
        jo.put("quality_score",1.0f);
        jo.put("yaw",0.0f);
        jo.put("pitch",0.0f);
        jo.put("roll",0.0f);
        jo.put("msg_source", "camera");
        jo.put("age",picAttr.getAge().intValue());
        jo.put("gender",picAttr.getGender());
        jo.put("glass",picAttr.getEyeglass());
        jo.put("mask",picAttr.getMask());
        jo.put("race",picAttr.getRace());
        jo.put("beard",picAttr.getBeard());
        jo.put("emotion",picAttr.getSmile());
        jo.put("eye_open",picAttr.getEyeOpen());

        jo.put("send_idx",1);

        return jo;

    }

    /**
     * 聚类字段模拟
     * @param picPath 传入图片路径
     * @return
     */
    public static JSONObject getFeatureInfoCluster(String picPath){
        String feature = getFeature(picPath,requestUrl);
        //本地文件获取特征
        //String file = "F:\\pictureDir\\feature特征";
        //String feature = getFeatureFile(picPath,file);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);
        System.out.println(feature);
        System.out.println(picAttr);
        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String entertime = enterleave.get(0);
        String leavetime = enterleave.get(1);
        String trackid = getTrackIdx();
        JSONObject jo = new JSONObject();
        String uuid = getUUID();
        jo.put("uuid",uuid);
        jo.put("feature",feature);
        jo.put("enter_time",entertime);
        jo.put("leave_time",leavetime);
        jo.put("event_id",5801);
        jo.put("event_time",entertime);
        jo.put("img_url",picPath);
        jo.put("event_type",2001);
        jo.put("msg_type","faceSnapEvent");
        jo.put("big_picture_uuid","http://10.45.152.148:9008/GetPictureUrl/fastdfs_"+uuid);
        jo.put("roll",0.0f);
        jo.put("frame_index","748");
        jo.put("task_idx","1555917855202");
        jo.put("send_idx",2);
        jo.put("track_idx",trackid);
        jo.put("msg_source","camera");
        jo.put("camera_kind",3);
        jo.put("camera_id","32011500001310000002");
        jo.put("camera_name","211_camera");
        jo.put("camera_type",30001);
        jo.put("office_id","");
        jo.put("office_name","");
        jo.put("right_pos",400);
        jo.put("left_pos",400);
        jo.put("img_width",200);
        jo.put("img_height",100);
        jo.put("similarity",0.92f);
        jo.put("person_id",uuid);
        jo.put("lib_id",3);
        return jo;
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
    /**
     * 获取单张图片的feature
     * @param picPath 图片路径
     * @param requestUrl 请求的地址
     * @return
     */
    public static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).getOrDefault("feature","").toString();
//        byte[] _feature = Base64.getDecoder().decode(feature);
        return feature;
    }
    /**
     * 获取图片对应的属性
     * @param picPath 图片路径
     * @param requestUrl 请求的地址
     * @return
     */
    public static PicAttr getPicAttr(String picPath,String requestUrl){
        byte[] data = PictureUtils.image2byte(picPath);
        String dd = GetDataFeature.getFeature("zs",data,requestUrl);
        System.out.println(dd);
        PicAttr picAttr = JSONObject.parseObject(dd, PicAttr.class);
        return picAttr;
    }
    /**
     * 批量获取特征
     * @param picPath 图片路径
     * @param requestUrl 请求的地址
     */
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
    /**
     * 构造通知topic消息
     * @param lib_id 传入更改的lib_id
     * @return
     */
    public  static JSONObject noticTopicMsg(String lib_id) {
        Properties properties = loadFromResource("kafka_producer.properties");
        String table_name = properties.getProperty("table_name");
        JSONObject jo = new JSONObject();
        ArrayList<String> enterleave=getEnterAndLeaveTime1();
        String send_time = enterleave.get(0);
        jo.put("send_time",send_time);
        jo.put("reference_id","");
        jo.put("primary_id",lib_id);
        jo.put("msg_type","Dfss-BlackListChange-n-project-v1-2");
        jo.put("table_name",table_name);
        return jo;
    }
    public static Properties loadFromResource(String filename) {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
            prop.load(in);
        } catch (Exception e){
            e.printStackTrace();
        }
        return prop;
    }
}
