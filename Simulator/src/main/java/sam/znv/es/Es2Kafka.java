package sam.znv.es;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import sam.znv.utils.EsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @Author LiuJun
 * @create 2019/12/13 12:51
 */
public class Es2Kafka {

    private static String cluseterName = "lv210.dct-znv.com-es";
    private static String transportHosts = "10.45.154.210";
    private static String index = "person_list_data_n_project_v1_2";
    private static TransportClient client ;
    private static List<JSONObject> datas = new ArrayList<>(1000);
    public static String bootstrap = "10.45.154.217:9092";
    public static String topic = "test-4";
    public static KafkaProducer<String, JSONObject> producer;

    public static void main(String[] args) {
        initKafkaAndEs();
        readEsToKafka();
    }

    private static void initKafkaAndEs() {
        Properties pro = new Properties();
        pro.put("bootstrap.servers", bootstrap);
        pro.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        pro.put("value.serializer","com.znv.kafka.common.KafkaAvroSerializer");
        pro.put("acks","1");
        pro.put("retries","3");
        pro.put("linger.ms","1000");
        producer = new KafkaProducer<>(pro);
        //初始化es連接
        client = EsUtils.getEsTransportClient(cluseterName, transportHosts);
    }

    public static void readEsToKafka(){
        SearchResponse scrollResp = client.prepareSearch(index)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC).setScroll(new TimeValue(60000)).setSize(1000)
                .get();
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
                Map<String, Object> source = hit.getSource();
                JSONObject json = new JSONObject(source);
                datas.add(json);
                if (datas.size() >= 1000) {
                    //数据丢到kafka中
                    writeKafka(datas);
                    System.out.println("---写入文件大小------"+datas.size());
                    datas = new ArrayList<>(1000);
                }
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0);

        if (datas.size() > 0) {
            //数据丢到kafka中
            writeKafka(datas);
            System.out.println("---写入文件大小------"+datas.size());
            datas = new ArrayList<>(1000);
        }

    }

    public static void writeKafka(List<JSONObject> datas){
        for (JSONObject msg: datas) {
            ProducerRecord<String, JSONObject> data = new ProducerRecord(topic, null, msg);
            Future<RecordMetadata> res = producer.send(data, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if(exception == null){
                        //System.out.println("发送成功！！");
                    }else{
                        System.out.println("发送失败！！！");
                    }
                }
            });
        }
    }
}
