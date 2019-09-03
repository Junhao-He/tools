package sam.znv.Function;



import com.alibaba.fastjson.JSONObject;
import sam.znv.kafka.PicAttr;
import sam.znv.utils.JsonObjectType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static sam.znv.feature.FeatureInfo.*;

/**
 * Created by 86157 on 2019/7/24.
 */
public class readPropertiesFss {
    static String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
    static String requestAttrUrl = "http://10.45.157.115:80/verify/attribute/gets";
    static String picturePath = "F:\\picture\\fss_test.jpg";
    static String path= "F:\\kafka_producer.properties";

    public static void main(String[] args) throws Exception {
        readFileFss(path,picturePath);
    }
    /*
    1、读取文件默认里面获取某个值；
    2、没有的话匹配字段类型；
    3、默认值填充；
     */
    public static JSONObject readFileFss(String path,String picPath) throws Exception{
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String[] arrs = null;
        JSONObject jo = new JSONObject();
        JSONObject objectOld = oldObject(picPath);
        while ((line = br.readLine())!=null){
            arrs = line.split("=",-1);
            String jo1 = arrs[0].toString();
            String jo2 = arrs[1];
            if(jo2==null|| jo2.isEmpty()){
                jo.put(jo1,objectOld.get(jo1));
            }else{
                //判断数据类型，并进行转化，判断类型并进行转化
                System.out.println("type:"+JsonObjectType.objectType(jo1));
                switch (JsonObjectType.objectType(jo1)){
                    case "Int" : jo.put(jo1,stringToInt(jo2));
                    case "Float" : jo.put(jo1,stringToFloat(jo2));
                    case "String" : jo.put(jo1,jo2.toString());
                    case "Double" : jo.put(jo1,stringToDoule(jo2));
                    default : jo.put(jo1,jo2.toString());
                }
            }
        }
        return jo;
    }

    public static JSONObject oldObject(String picPath) throws Exception{
        String feature = getFeature(picPath,requestUrl);
        PicAttr picAttr=getPicAttr(picPath, requestAttrUrl);

        System.out.println(feature);
        System.out.println(picAttr);

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

    public static int stringToInt(String str)
    {
        return Integer.parseInt(str);
    }
    public static long stringToLong(String str)
    {
        return Long.parseLong(str);
    }
    public static float stringToFloat(String str)
    {
        return Float.parseFloat(str);
    }
    public static double stringToDoule(String str){
        return Double.parseDouble(str);
    }
}
