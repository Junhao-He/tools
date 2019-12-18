package sam.znv.DeveOps.EsTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import sam.znv.hbase.DataBean;
import sam.znv.lopq.CoarseClassify;
import sam.znv.lopq.LOPQModel;
import sam.znv.utils.GetDataFeature;
import sam.znv.utils.ReadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author LiuJun
 * @create 2019/11/26 16:22
 */
public class PictureToEs {
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureToEs.class);
    private static Properties pro;
    private static String stUrl;
    private static String cluseterName;
    private static String transportHosts;
    private static String index;
    private static String type;
    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static TransportClient client;
    private static BulkRequestBuilder bulkRequest;
    private static int sendCount = 0;

    public static void initEs(){
        stUrl = pro.getProperty("sensetimeUrl");
        cluseterName = pro.getProperty("es_clusterName");
        transportHosts = pro.getProperty("transportHosts");
        index = pro.getProperty("index");
        type = pro.getProperty("type");
        client = getEsTransportClient(cluseterName, transportHosts);
        bulkRequest = client.prepareBulk();
    }

    /**
     * @param path 传入路径
     * @param libId 传入libId
     * @throws IOException
     */
    public static void writeES(String path,int libId) throws IOException {
        File f = new File(path);
        if(f.isDirectory()){
            String[] lists = f.list();
            for(String list : lists){
                writeES(path+File.separator+list,libId);
            }
        }else{
            DataBean dataBean = writeOneData(path,libId);
            inserData(dataBean);
            sendCount +=1;
            LOGGER.info("发送第"+sendCount+"条");
            LOGGER.info(""+dataBean);
        }
    }

    /**
     * 插入数据
     * @param data 插入单条数据
     * @throws IOException
     */
    public static void inserData( DataBean data) throws IOException{

        JSONObject json = new JSONObject();
        json.put("lib_id",data.getLIB_ID());
        json.put("person_id",data.getPERSON_ID());
        json.put("control_start_time",getDateOfString(data.getCONTROL_START_TIME(),DATE_FORMAT_DATETIME));
        json.put("control_end_time",getDateOfString(data.getCONTROL_END_TIME(),DATE_FORMAT_DATETIME));
        json.put("flag",data.getFLAG());
        json.put("birth",data.getBIRTH());
        json.put("person_name",data.getPERSON_NAME());
        json.put("control_event_id",data.getCONTROL_EVENT_ID());
        json.put("sex",data.getSEX());
        json.put("feature",data.getFEATURE());
        json.put("personlib_type",data.getPERSONLIB_TYPE());
        json.put("is_del",data.getIS_DEL());
        String docId = json.getString("lib_id") + json.getString("person_id");
        if(index.endsWith("-")){
            //加载coarse_id
            String classify = CoarseClassify.getClassify(data.getFEATURE());
            json.put("coarse_id",classify);
            String indexMulit = index.split("-")[0]+"-"+classify;
            bulkRequest.add(client.prepareIndex(indexMulit,type,docId).setSource(json));
        }else{
            bulkRequest.add(client.prepareIndex(index,type,docId).setSource(json));
        }
        bulkRequest.get();
    }

    /**
     * 构造数据
     * @param picturePath 传入单张图片地址
     * @param libId 库id
     * @return
     */
    private static DataBean writeOneData(String picturePath,int libId){
        //获取图片特征
        String feature = getFeature(picturePath,stUrl);
        byte[] featureBytes = Base64.getDecoder().decode(feature);
        String fileName = new File(picturePath).getName();
        ArrayList<String> enterAndLeaveTime1 = getEnterAndLeaveTime1();
        int LIB_ID = libId;
        String PERSON_ID = Math.abs(new Random().nextLong())+"";
        String CONTROL_START_TIME = enterAndLeaveTime1.get(0);
        String CONTROL_END_TIME = enterAndLeaveTime1.get(1);
        int FLAG = 1;
        String BIRTH = enterAndLeaveTime1.get(0);
        String PERSON_NAME = fileName;
        String CONTROL_EVENT_ID = Math.abs(new Random().nextLong())+"";
        int SEX = 0;
        byte[] FEATURE = featureBytes;
        int PERSONLIB_TYPE = 1;
        String IS_DEL = "0";
        DataBean dataBean = new DataBean(LIB_ID, PERSON_ID, CONTROL_START_TIME, CONTROL_END_TIME, FLAG, BIRTH, PERSON_NAME, CONTROL_EVENT_ID, SEX, FEATURE, PERSONLIB_TYPE, IS_DEL);
        return dataBean;
    }

    private static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).getOrDefault("feature","").toString();
        return feature;
    }

    /**
     * String转成Date
     * @param date
     * @param format
     * @return
     */
    public static Date getDateOfString(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
        }
        return null;
    }

    public static TransportClient getEsTransportClient(String clusterName, String... transportHosts) {
        TransportClient client = null;
        try {
            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            TransportAddress[] addresses = new TransportAddress[transportHosts.length];
            int i = 0;
            for (String host : transportHosts) {
                host = host.replaceAll("http://", "");
                String[] inet = host.split(":");
                addresses[i++] = new InetSocketTransportAddress(InetAddress.getByName(inet[0]), 9300);
            }
            client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
        return client;
    }

    private static ArrayList<String> getEnterAndLeaveTime1(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String entertime = df.format(date);
        long time=date.getTime()-1000*60*60*24*30; //这是毫秒数
        String leavetime = df.format(time);
        ArrayList<String> tt = new ArrayList<>();
        tt.add(entertime);
        tt.add(leavetime);
        return tt;
    }

    public static void main(String[] args) {

        initEs();
        // 加载粗分类模型
        try {
            LOPQModel.loadProto(PictureToEs.class.getResourceAsStream("/lopq/lopq_model_V1.0_D512_C36.lopq"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pro = ReadProperties.getProperties(args[0]);
        int lib = Integer.parseInt(pro.getProperty("lib"));
        try {
            writeES(pro.getProperty("path"),lib);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
