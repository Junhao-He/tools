package sam.znv.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import sam.znv.feature.FeatureInfo;
import sam.znv.kafka.PicAttr;
import sam.znv.kafka.ZKafkaProducter;
import sam.znv.utils.GetDataFeature;
import sam.znv.utils.PictureUtils;

public class SendController {

    //todo:  config
    static String dstTopic = "kafka.topic";

    public static void main(String[] args) throws IOException {
        sendIdx1("D:\\Users\\User\\Desktop\\pic\\dd.jpg");
    }

    public static void sendIdx1(String picPath){
        System.out.println("*********************"+picPath);

        JSONObject msg= FeatureInfo.getFeatureInfo(picPath);
        ZKafkaProducter.getInstance().sendMessage(msg);

    }
    public static void sendIdx2(){

    }

}
