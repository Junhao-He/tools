package sam.znv.kafka;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;

/**
 * Created by 86157 on 2019/9/3.
 */
/*
模拟消费者，消费avro格式数据，并进行判断输出IS_Alarm="1"的消息
 */
public class parseAvroDataConsumer {


    public static void main(String[] args) {
        Properties props = new Properties();
        // 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put("bootstrap.servers", "10.45.157.112:9092");
        // 制定consumer group
        props.put("group.id", "test-2");
        // 是否自动确认offset
        props.put("enable.auto.commit", "true");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        //指定消费者组
        //1-1 换组重复消费
        props.put("auto.offset.reset","earliest");  //latest, earliest, none

        // 定义consumer
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("fss-analysis-n-project-v1-2-production-lg"));

        //解析avro格式数据
        String schema="{\"type\":\"map\",\"values\":[\"null\",\"int\",\"long\",\"float\",\"double\",\"string\",\"boolean\",\"bytes\"]}";
        Schema parse = new Schema.Parser().parse(schema);
        SpecificDatumReader<HashMap<Utf8, Object>> reader = new SpecificDatumReader<>(parse);

        // 读取数据，读取超时时间为100ms
        ArrayList<HashMap<Utf8, Object>> arrays = new ArrayList<>();
        ConsumerRecords<String, byte[]> records = consumer.poll(1000);
        for (ConsumerRecord<String, byte[]> record : records){
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
            HashMap<Utf8, Object> hashMap = new HashMap<>();
            HashMap<Utf8, Object> read = null;
            try {
                read = reader.read(hashMap, decoder);
                arrays.add(read);
                System.out.println("----------------------------"+read);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
