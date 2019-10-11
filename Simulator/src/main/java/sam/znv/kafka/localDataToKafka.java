package sam.znv.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.util.Strings;
import sam.znv.utils.JsonObjectType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by 86157 on 2019/9/6.
 */
public class localDataToKafka {

    /**
     * 加载本地数据发送kafka
     * @bootstrapIp 指定kafka服务器地址及端口号
     * @topic 指定写入数据的topic
     * @path 指定数据所在的路径
     */
    private static KafkaProducer<String, JSONObject> producer;
    private static String topic = "fss-history-n-project-v1-2-production-fusion";
    private static String bootstrapIp = "10.45.154.216:9092";
    private static String path = "F:\\history\\b.txt";

    public static void main(String[] args) {
        loadProperties();
        sendData(path);
    }

    public static void localToKafka(){
        loadProperties();
        sendData(path);
    }

    /**
     * 添加kafka参数
     */
    public static void loadProperties(){
        Properties pro = new Properties();
        pro.put("bootstrap.servers",bootstrapIp);
        pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        pro.put("value.serializer","com.znv.kafka.common.KafkaAvroSerializer");
        pro.put("acks","all");
        pro.put("retries",0);
        pro.put("linger.ms",1);
        producer = new KafkaProducer<>(pro);
    }

    /**
     * 遍历文件夹
     * @param path 图片路径
     */
    public static void sendData(String path){
        File f = new File(path);
        if(f.isDirectory()){
            String[] s = f.list();
            for(int i=0;i<s.length;i++){
                sendData(path+"\\"+s[i]);
            }
        }else{
            try {
                readFile(path);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 读取本地文件，构造特征数据
     * @param filename 传入单个文件路径
     * @throws IOException
     */
    public static void readFile(String filename) throws Exception {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String [] arrs = null;
        String[] temp = null;
        String line = "";
        while ((line= br.readLine())!=null){
            arrs = line.split(",",-1);
            JSONObject jo = new JSONObject();
            for(String arr : arrs){
                if(!arr.trim().isEmpty()){
                    temp = arr.split("=");
                    String jo1 = null;
                    String jo2 = null;
                    if(temp.length < 2){
                       jo1 = temp[0].toString();
                       jo2 = "";
                    }else {
                       jo1 = temp[0].toString();
                       jo2 = temp[1];
                    }
                    //判断数据类型，并进行转化，判断类型并进行转化
                    //System.out.println("----------jo1--------"+jo1);
                    switch (JsonObjectType.objectType(jo1)){
                        case "Int" : jo.put(jo1,stringToInt(jo2));break;
                        case "Float" : jo.put(jo1,stringToFloat(jo2));break;
                        case "String" : jo.put(jo1,jo2.toString());break;
                        case "Double" : jo.put(jo1,stringToDoule(jo2));break;
                        case "Long" : jo.put(jo1,stringToLong(jo2));break;
                        default : jo.put(jo1,jo2.toString());break;
                    }
                }
            }
            sendMessage(jo,topic);
            System.out.println("*******arr**********"+jo);
        }
    }

    /**
     * 发送数据到kafka
     * @param message 发送的消息
     */
    public static void sendMessage(JSONObject message,String topic) throws Exception {

        if (Strings.isEmpty(topic)) {
            return;
        }

        ProducerRecord<String, JSONObject> data = new ProducerRecord(topic, null, message);

        Future<RecordMetadata> res = producer.send(data, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null){
                    System.out.println("发送成功！！！");
                }else{
                    System.out.println("发送失败！！！");
                }
            }
        });
        Thread.sleep(1);

    }

    public static int stringToInt(String str)
    {
        try {
            return Integer.parseInt(str);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public static void setPath(String path) {
        localDataToKafka.path = path;
    }

    public static void setTopic(String topic) {
        localDataToKafka.topic = topic;
    }

    public static void setBootstrapIp(String bootstrapIp) {
        localDataToKafka.bootstrapIp = bootstrapIp;
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
