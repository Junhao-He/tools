package sam.znv.DeveOps.KafkaTools;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sam.znv.utils.ReadProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @Author LiuJun
 * @create 2019/11/11 11:30
 */
public class PictureToKafka {
    private static String msgType;
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureToKafka.class);
    private static String topic;
    private static KafkaProducer<String, JSONObject> producer;
    private static Properties pro;

    /**
     * 添加kafka参数
     */
    private static void initKafka() {
        pro.put("bootstrap.servers",pro.getProperty("bootstrap.servers"));
        pro.put("key.serializer",pro.getProperty("key.serializer"));
        pro.put("value.serializer",pro.getProperty("value.serializer"));
        pro.put("acks",pro.getProperty("acks"));
        pro.put("retries",pro.getProperty("retries"));
        pro.put("linger.ms",pro.getProperty("linger.ms"));
        producer = new KafkaProducer<>(pro);
    }
    /**
     * 加载配置文件
     * @param path 传入外部参数路径
     * @return
     */
    private static Properties getProperties(String path){
        Properties pro = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            pro.load(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

    private static void sendPic(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            String s[] = f.list();
            for (int i = 0; i < s.length; i++) {
                sendPic(path + File.separator + s[i]);
            }
        } else {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendIdx1(path);
        }
    }

    private static void sendIdx1(String picPath){
        LOGGER.info("*********************"+picPath);
        JSONObject msg = null;
        switch (msgType){
            case "1": msg = FeatureInfo.getFeatureInfo(picPath); break;
            case "2": msg = FeatureInfo.getFeatureInfoFusion_fss_camera(picPath); break;
            case "3": msg = FeatureInfo.getFeatureInfoFusion_community_camera(picPath); break;
            case "4": msg = FeatureInfo.getFeatureInfoFusionDoor(picPath); break;
            default: msg = FeatureInfo.getFeatureInfo(picPath); break;
        }
        if(msg.getOrDefault("feature","").equals("") || msg.getOrDefault("feature","").equals("null")){
            LOGGER.info("--------没有提取到特征，不发送----------");
        }else{
            sendMessage(msg);
        }
    }

    private static void sendMessage(JSONObject msg){
        ProducerRecord<String, JSONObject> data = new ProducerRecord(topic, null, msg);

        Future<RecordMetadata> res = producer.send(data, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null){
                    LOGGER.info("发送成功！！！");
                }else{
                    LOGGER.info("发送失败！！！");
                }
            }
        });
    }

    public static void main(String[] args) {

        pro = ReadProperties.getProperties(args[0]);
        msgType = pro.getProperty("kafka.msgtype");
        topic = pro.getProperty("kafka.topic");
        //设置商汤提取特征地址及camera_id
        String requestUrl = pro.getProperty("requestUrl","http://10.45.157.115:80/verify/feature/gets");
        FeatureInfo.setRequestUrl(requestUrl);
        String requestAttrUrl = pro.getProperty("requestAttrUrl","http://10.45.157.115:80/verify/attribute/gets");
        FeatureInfo.setRequestAttrUrl(requestAttrUrl);
        String cameraId = pro.getProperty("cameraId","32010400001310007002");
        FeatureInfo.setCameraId(cameraId);
        initKafka();
        sendPic(pro.getProperty("picturePath"));

    }
}
