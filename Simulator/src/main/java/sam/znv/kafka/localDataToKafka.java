package sam.znv.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import sam.znv.utils.JsonObjectType;

import java.io.BufferedReader;
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
    private KafkaProducer<String, JSONObject> producer;
    private static String topic = "test-1";
    private static String bootstrapIp = "10.45.157.112:9092";
    private static String path = "D:\\data\\f.txt";

    //添加kafka参数
    @Before
    public void loadProperties(){
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
     * 读取本地文件，构造特征数据
     */
    @Test
    public void readFile() throws IOException {
        FileReader fr = new FileReader(path);
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
                    String jo1 = temp[0].toString();
                    String jo2 = temp[1];
                    //判断数据类型，并进行转化，判断类型并进行转化
                    switch (JsonObjectType.objectType(jo1)){
                        case "Int" : jo.put(jo1,stringToInt(jo2));break;
                        case "Float" : jo.put(jo1,stringToFloat(jo2));break;
                        case "String" : jo.put(jo1,jo2.toString());break;
                        case "Double" : jo.put(jo1,stringToDoule(jo2));break;
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
    public void sendMessage(JSONObject message,String topic) {

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
