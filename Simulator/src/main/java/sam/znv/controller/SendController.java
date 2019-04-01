package sam.znv.controller;

import java.io.*;

import com.alibaba.fastjson.JSONObject;
import sam.znv.feature.FeatureInfo;
import sam.znv.kafka.ZKafkaProducer;

public class SendController {



    public static void main(String[] args) throws IOException {
        sendIdx1("D:\\Users\\User\\Desktop\\pic\\dd.jpg",1,1);
    }

    public static void sendIdx1(String picPath, int sendcount, int countpersencond){
        System.out.println("*********************"+picPath);

        JSONObject msg= FeatureInfo.getFeatureInfo(picPath);
        ZKafkaProducer.getInstance().sendMessage(msg);

    }
    public static void sendIdx2(){

    }

}
