/**
 * <pre>
 * 标  题: ZKafkaProducer.java.
 * 版权所有: 版权所有(C)2001-2017
 * 公   司: 深圳中兴力维技术有限公司
 * 内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明
 * 其他说明: // 其它内容的说明
 * 完成日期: // 输入完成日期，例：2000年2月25日
 * </pre>
 * <pre>
 * 修改记录1:
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * @version 1.0
 * @author MHm
 */
package sam.znv.kafka;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.util.Strings;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka生产者 不能用0.10的jar包
 * 
 * @author HuangRu
 */
public final class ZKafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKafkaProducer.class);
    private static ZKafkaProducer instance = null;

    private KafkaProducer<String, JSONObject> producer;
    private Properties properties;
    private static String topic = "";
    private ZKafkaProducer() {
        properties = new Properties();

        InputStream fs = null;
        try {
            fs = this.getClass().getClassLoader().getResourceAsStream("kafka_producer.properties");
            properties.load(fs);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        //添加kafka参数
        Properties pro = new Properties();
        pro.put("bootstrap.servers",properties.getProperty("bootstrap.servers"));
        pro.put("key.serializer",properties.getProperty("key.serializer"));
        pro.put("value.serializer",properties.getProperty("value.serializer"));
        pro.put("acks",properties.getProperty("acks"));
        pro.put("retries",properties.getProperty("retries"));
        pro.put("linger.ms",properties.getProperty("linger.ms"));
        producer = new KafkaProducer<>(pro);
    }

    public synchronized static ZKafkaProducer getInstance() {
        if (instance == null) {
           // synchronized (ZKafkaProducer.class)
            {
                if (instance == null) {
                    instance = new ZKafkaProducer();
                }
            }
        }
        return instance;
    }

    /**
     * 发送数据到kafka
     * 
     * @param message 发送的消息
     */
    public void sendMessage(JSONObject message,String noticTopic) {

        if(noticTopic=="noticTopic"){
            topic = properties.getProperty("notic.topic");
        }else{
            topic = properties.getProperty("kafka.topic");
        }

        if (Strings.isEmpty(topic)) {
            return;
        }
        LOGGER.info("******topic*****"+topic+"********message*****"+message);
        ProducerRecord<String, JSONObject> data = new ProducerRecord(topic, null, message);

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
        System.out.println(res.isDone());

    }
    /**
     * 返回打印device_code和mete_id的字符串
     * 
     */
    public String getDebugInfo() {
        String debuginfo = properties.getProperty("debuginfo");
        return debuginfo;
    }
    /**
     * 关闭
     */
    public void close() {
        producer.close();
    }

    /**
     * kafka回调
     * 
     * @author ZNV
     */
    private class KafkaCallback implements Callback {

        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                LOGGER.error("Error sending message: {} ", exception.getMessage());
            }
        }
    }

}
