package sam.znv.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import sam.znv.hbase.DataBean;
import sam.znv.lopq.CoarseClassify;
import sam.znv.lopq.LOPQModel;
import sam.znv.utils.DateUtils;
import sam.znv.utils.EsUtils;
import sam.znv.utils.GetDataFeature;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import org.elasticsearch.client.transport.TransportClient;

/**
 * 将图片转化，构造字段发往es名单库中
 */
public class PictureToES {

    /**
     * @clusterName 设置es集群名字
     * @transportHosts 设置连接地址
     * @index 索引
     * @type 类型
     * @path 图片对应的路径
     */
    private static final String stUrl = "http://10.45.157.115:80/verify/feature/gets";
    private static String cluseterName = "face.dct-znv.com-es";
    private static String transportHosts = "10.45.157.112";
    //如果是单索引：person_list_data_n_project_v1_2，多索引：person_list_data_n_project_v1_2- ;
    private static String index = "person_list_data_n_project_v1_2_liujun";
    private static TransportClient client ;
    private static BulkRequestBuilder bulkRequest;

    private static String type = "person_list";
    private static String path = "D:\\流处理标准测试\\face";
    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static int sendCount = 0;
    public static void main(String[] args) throws IOException {

        //初始化es連接
        client = EsUtils.getEsTransportClient(cluseterName, transportHosts);
        bulkRequest = client.prepareBulk();
        // 加载粗分类模型
        LOPQModel.loadProto(PictureToES.class.getResourceAsStream("/lopq/lopq_model_V1.0_D512_C36.lopq"));
        writeES(path,3);
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
                writeES(path+"\\"+list,libId);
            }
        }else{
            DataBean dataBean = writeOneData(path,libId);
            inserData(dataBean);
            sendCount +=1;
            System.out.println("发送第"+sendCount);
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
        json.put("control_start_time", DateUtils.getDateOfString(data.getCONTROL_START_TIME(),DATE_FORMAT_DATETIME));
        json.put("control_end_time",DateUtils.getDateOfString(data.getCONTROL_END_TIME(),DATE_FORMAT_DATETIME));
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
}
