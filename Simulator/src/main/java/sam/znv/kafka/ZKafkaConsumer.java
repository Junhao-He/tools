/**
 * <pre>
 * 标  题: ZKafkaConsumer.java.
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

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * kafka消费者 不能用0.10的jar包
 * 
 * @author HuangRu
 */
public final class ZKafkaConsumer {
    Logger logger = LoggerFactory.getLogger(ZKafkaConsumer.class);
    private static ZKafkaConsumer instance = null;

    private KafkaConsumer<String, String> consumer;
    private Properties properties;

    private ZKafkaConsumer() {
        properties = new Properties();

        InputStream fs = null;
        try {
            fs = this.getClass().getClassLoader().getResourceAsStream("kafka_consumer.properties");
            properties.load(fs);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("yltest"));
    }

    public synchronized static ZKafkaConsumer getInstance() {
        if (instance == null) {
           // synchronized (ZKafkaProducer.class)
            {
                if (instance == null) {
                    instance = new ZKafkaConsumer();
                }
            }
        }
        return instance;
    }

    /**
     * 拉数据从kafka
     * 
     *
     */
    public ConsumerRecords<String, String> receiveMessage() {

        System.out.println("press button");

        String topic = properties.getProperty("kafka.topic");

        if (Strings.isEmpty(topic)) {
            return null;
        }
//        if (Strings.isEmpty(message)) {
//            return;
//        }

        /*ThreadPoolUtils.exec(new Runnable() {
            @Override
            public void run() {
                //Logger.L.debug("send alarm to kafka:" + message);
                producer.send(new ProducerRecord<String, String>(topic, message), new KafkaCallback());
            }
        });*/
        ConsumerRecords<String, String> records = consumer.poll(1000);

        return records;

//        producer.send(new ProducerRecord<String, String>(topic, message), new KafkaCallback());
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
        consumer.close();
    }

    /**
     * kafka回调
     * 
     * @author ZNV
     */
    private class KafkaCallback implements Callback {

        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                logger.error("Error sending message: {} ", exception.getMessage());
            }
        }
    }

}
