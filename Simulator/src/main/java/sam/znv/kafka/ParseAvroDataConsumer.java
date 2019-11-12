package sam.znv.kafka;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 86157 on 2019/9/3.
 */

public class ParseAvroDataConsumer {

    /**
     * 模拟消费者，消费kafka中的avro格式数据，将数据写入本地
     * @path 数据写入的路径
     * @bootstrapIp kafka服务器地址及端口号
     * @topic 消费的kafka
     */
    private static FileOutputStream fos = null;
    private static ObjectOutputStream oos = null;
    private static String path = "F:\\TestFusionRealData230\\a.txt";
    private static String bootstrapIp = "10.45.152.230:9092";
    private static String topic = "fss-analysis-n-project-v1-2-production";
    private static RandomAccessFile out_r;

    public static void main(String[] args) {
        parseAvroData(topic);
    }

    /**
     * 加载配置参数
     * @return
     */
    public static Properties loadProperties(){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapIp);
        props.put("group.id", "test-11");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("auto.offset.reset","earliest");  //latest, earliest, none
        return props;
    }

    /**
     * 拉取指定topic数据
     * @param topic 拉取topic名字
     */
    public static void parseAvroData(String topic){
        Properties props = loadProperties();
        // 定义consumer
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));

        //解析avro格式数据
        String schema="{\"type\":\"map\",\"values\":[\"null\",\"int\",\"long\",\"float\",\"double\",\"string\",\"boolean\",\"bytes\"]}";
        Schema parse = new Schema.Parser().parse(schema);
        SpecificDatumReader<HashMap<Utf8, Object>> reader = new SpecificDatumReader<>(parse);

        while (true){
            // 读取数据，读取超时时间为100ms
            ConsumerRecords<String, byte[]> records = consumer.poll(1000);
            for (ConsumerRecord<String, byte[]> record : records){
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
                HashMap<Utf8, Object> hashMap = new HashMap<>();
                HashMap<Utf8, Object> read = null;
                try {
                    read = reader.read(hashMap, decoder);
                    StringBuilder sb = new StringBuilder();
                    for(Map.Entry<Utf8, Object> entry:read.entrySet()){
                        sb.append(entry.getKey()+"="+entry.getValue()+",");
                    }
                    appendData2File(sb.toString(),path);

                    System.out.println("----------------------------"+read);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void parseJson(String topic){
        Properties props = loadProperties();
        // 定义consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records){
                System.out.println("*******value**********"+record.value().toString());
            }
        }
    }
    /**
     *topic中数据写入本地某个文件（追加）
     *@param dataString  接收的数据
     */
    public static void appendData2File(String dataString,String filename){
        OutputStreamWriter out = null;
        FileOutputStream fos = null;
        try{
            File file = new File(filename);
            if(!file.getParentFile().isDirectory()){
                file.getParentFile().mkdirs();
            }

            if(!file.exists()){
                file.createNewFile();
            }

            File newFile = renameFile(filename);
            //追加写入文件，使用UTF-8格式
            fos = new FileOutputStream(newFile, true);
            out = new OutputStreamWriter(fos,"UTF-8");
            out.write(dataString);
            out.write("\r\n");
        }catch (Exception e){
        }finally {
            try {
                if(null!=out){
                    out.flush();
                    out.close();
                }
            }catch (IOException e){}

        }
    }

    public static File renameFile(String filename){
        File file = new File(filename);
        long fileSize = getFileSize(file);
        //文件达到一定大小生成新文件
        if(fileSize/1048576L > 10){
            System.out.println("文件开始创建...");
            String data = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String str1 = file.getParent() + "\\";
            String str2 = str1.concat(data);
            String nameDate = str2 + ".txt"; // 定义路径
            path = nameDate;
            return new File (nameDate);
        }else{
            return file;
        }
    }

    // 创建新文件以当前时间命名，重新赋值输出流
    public void createFile(File file) throws IOException {
        Date date = new Date(); // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 定义文件名格式
        String formatDate = sdf.format(date); // 把当前时间以定义的格式 格式化
        String str1 = file.getParent() + "\\";
        String str2 = str1.concat(formatDate);
        String nameDate = str2 + ".txt"; // 定义路径
        out_r = new RandomAccessFile(nameDate, "rw"); // 获得写入目标文件
    }


    /**
     * 获取文件大小
     * @param file 路径
     * @return
     */
    public static long getFileSize(File file){
        FileChannel fc = null;
        try {
            if(file.exists() && file.isFile()){
                FileInputStream fis = new FileInputStream(file);
                fc = fis.getChannel();
                return fc.size();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static void writeFile(HashMap<String,String> data) throws Exception{
        File file = new File(path);
        fos = new FileOutputStream(file);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        oos.flush();
        oos.close();
    }

    public static void setPath(String path) {
        ParseAvroDataConsumer.path = path;
    }

    public static void setBootstrapIp(String bootstrapIp) {
        ParseAvroDataConsumer.bootstrapIp = bootstrapIp;
    }
}