package sam.znv.controller;

import java.io.*;

import com.alibaba.fastjson.JSONObject;
import sam.znv.Function.readPropertiesFss;
import sam.znv.feature.FeatureInfo;
import sam.znv.kafka.ZKafkaProducer;

public class SendController {



    public static void main(String[] args) throws IOException {
//        sendIdx1("D:\\Users\\User\\Desktop\\pic\\dd.jpg",1,1);
        String path = "D:\\pictures\\real";
        File f = new File(path);
        String s[] = f.list();
        System.out.println("***********send start!************");
        for (int i = 0; i < s.length; i++) {
            sendIdx1(path + "\\" + s[i],i,1);
        }
        System.out.println("***********send all end success!*************");
    }

    public static void sendIdx1(String picPath, int sendcount, int countpersencond){
        System.out.println("*********************"+picPath);

        JSONObject msg= FeatureInfo.getFeatureInfo(picPath);
        ZKafkaProducer.getInstance().sendMessage(msg,"");

    }

    public static void sendIdx2(String picPath,String propertiesPath) throws Exception{
        System.out.println("*********picPath************"+picPath);
        System.out.println("**********propertiesPath***********"+propertiesPath);
        JSONObject msg= readPropertiesFss.readFileFss(propertiesPath,picPath);
        ZKafkaProducer.getInstance().sendMessage(msg,"");
    }

    //通知topic构造数据
    public static void sendNoticTopic(String lib_id) {
        System.out.println("**********lib_id******"+lib_id);
        JSONObject msg = FeatureInfo.noticTopicMsg(lib_id);
        ZKafkaProducer.getInstance().sendMessage(msg,"noticTopic");
    }
    public static void sendByFeature(String feature){
        System.out.println("*********************"+feature);
        JSONObject msg= FeatureInfo.constructMsg(feature);
        ZKafkaProducer.getInstance().sendMessage(msg,"");
    }

}
