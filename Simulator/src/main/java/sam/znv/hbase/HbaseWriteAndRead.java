package sam.znv.hbase;

import com.alibaba.fastjson.JSON;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.util.PropertiesUtil;
import sam.znv.utils.GetDataFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import sam.znv.utils.SaltingUtil;

/**
 * Created by 86157 on 2019/8/20.
 */
public class HbaseWriteAndRead {

    private static Properties properties = loadFromResource("phoenix.properties");
    private static final String stUrl = properties.getProperty("sensetimeUrl");
    //连接集群
    public static Connection initHbase() throws IOException {
        //创建连接
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum",properties.getProperty("hbase_url"));
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    public static void main(String[] args) throws IOException {

//        DataBean dataBean = writeOneData("E:\\czl.JPG", 2);
//        try {
//            inserData(dataBean);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        String result = getDataRowKey("2019-09-18 19:08:57", "AW1ED55z632kzP1m0JIY");
        System.out.println(result);
    }

    public static void writeHbase(String path,int libId) throws IOException{
        File f = new File(path);
        if(f.isDirectory()){
            String[] lists = f.list();
            for(String list : lists){
                writeHbase(path+"\\"+list,libId);
            }
        }else{
            DataBean dataBean = writeOneData(path,libId);
            inserData(dataBean);
        }
    }

    /**
     * 构造数据
     * @param picturePath 传入单张图片地址
     * @param libId 库id
     * @return
     */
    public static DataBean writeOneData(String picturePath,int libId){
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

    /**
     * 插入数据
     * @param data 插入单条数据
     * @throws IOException
     */
    public static void inserData( DataBean data) throws IOException{
        String tableName = properties.getProperty("HbaseTablePersonList");
        TableName tablename = TableName.valueOf(tableName);
        //rowkey的获取
        byte[] salt2 = new byte[1];
        byte[] byteKey = Bytes.add((Bytes.toBytes(data.getLIB_ID())), Bytes.toBytes(data.getPERSON_ID()));
        salt2[0] = SaltingUtil.getSaltingByte(byteKey, 0, byteKey.length, 24);
        Put put = new Put(Bytes.add(salt2, byteKey));

        put.addColumn("ATTR".getBytes(),"CONTROL_START_TIME".getBytes(), data.getCONTROL_START_TIME().getBytes());
        put.addColumn("ATTR".getBytes(),"CONTROL_END_TIME".getBytes(), data.getCONTROL_END_TIME().getBytes());
        put.addColumn("ATTR".getBytes(),"FLAG".getBytes(), Bytes.toBytes(data.getFLAG()));
        put.addColumn("ATTR".getBytes(),"BIRTH".getBytes(), Bytes.toBytes(data.getBIRTH()));
        put.addColumn("ATTR".getBytes(),"PERSON_NAME".getBytes(), Bytes.toBytes(data.getPERSON_NAME()));
        put.addColumn("ATTR".getBytes(),"CONTROL_EVENT_ID".getBytes(), Bytes.toBytes(data.getCONTROL_EVENT_ID()));
        put.addColumn("ATTR".getBytes(),"SEX".getBytes(), Bytes.toBytes(data.getSEX()));
        put.addColumn("FEATURE".getBytes(),"FEATURE".getBytes(), data.getFEATURE());
        put.addColumn("ATTR".getBytes(),"PERSONLIB_TYPE".getBytes(), Bytes.toBytes(data.getPERSONLIB_TYPE()));
        put.addColumn("ATTR".getBytes(),"IS_DEL".getBytes(), Bytes.toBytes(data.getIS_DEL()));
        System.out.println("**********PERSON_ID**********"+data.getPERSON_ID());
        HTable table =(HTable) initHbase().getTable(tablename);
        table.setAutoFlush(true,true);
        table.put(put);
        table.close();

    }

    /**
     *根据rowkey进行查询数据
     * @param opTime 查询人员时间
     * @param uuid 查询uuid
     * @return
     * @throws IOException
     */
    public static String getDataRowKey(String opTime,String uuid) throws IOException{
        //创建连接
        String tableName = properties.getProperty("HbaseTableAlarm");
        //HbaseTablePersonList  HbaseTableAlarm
        HTable table =(HTable) initHbase().getTable(TableName.valueOf(tableName));
        //rowkey的获取

        /*
        byte[] salt2 = new byte[1];
        byte[] byteKey = Bytes.add((Bytes.toBytes(opTime)), (Bytes.toBytes(uuid)));
        salt2[0] = SaltingUtil.getSaltingByte(byteKey, 0, byteKey.length, 24);
        Put put = new Put(Bytes.add(salt2, byteKey));
        Get get = new Get(Bytes.add(salt2, byteKey));
        */
        byte[] salt2 = new byte[1];
        //byte[] byteKey = Bytes.add((Bytes.toBytes(opTime)), Bytes.toBytes(uuid));
        byte[] byteKey = Bytes.add((Bytes.toBytes(Integer.parseInt(uuid))), (Bytes.toBytes(opTime)));
        salt2[0] = SaltingUtil.getSaltingByte(byteKey, 0, byteKey.length, 24);
        Put put = new Put(Bytes.add(salt2, byteKey));
        Get get = new Get(Bytes.add(salt2, byteKey));
        StringBuffer sb = new StringBuffer();
        if(!get.isCheckExistenceOnly()){
            Result result = table.get(get);
            if(result.isEmpty()){
                return null;
            }else{
                sb.append("OP_TIME:{"+opTime+"}  ");
                sb.append("UUID:{"+uuid+"}  ");
                for(Cell cell: result.rawCells()){
                    String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    if (colName.equals("PERSON_NAME")){
                        sb.append("PERSON_NAME:{"+value+"}  ");
                    }
                    if(colName.equals("CONTROL_END_TIME")){
                        sb.append("CONTROL_END_TIME:{"+value+"}");
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).getOrDefault("feature","").toString();
        return feature;
    }
    public static ArrayList<String> getEnterAndLeaveTime1(){
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

    public static Properties loadFromResource(String filename) {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
