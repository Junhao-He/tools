package sam.znv.DeveOps.HbaseTools;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sam.znv.hbase.DataBean;
import sam.znv.utils.ReadProperties;
import sam.znv.utils.SaltingUtil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static sam.znv.hbase.HbaseWriteAndRead.writeOneData;

/**
 * @Author LiuJun
 * @create 2019/11/12 9:35
 */
public class PictureToHbase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PictureToHbase.class);
    private static Properties pro;

    //连接集群
    public static Connection initHbase() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum",pro.getProperty("hbase_url"));
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    public static void writeHbase(String path,int libId) throws IOException{
        File f = new File(path);
        if(f.isDirectory()){
            String[] lists = f.list();
            for(String list : lists){
                writeHbase(path+File.separator+list,libId);
            }
        }else{
            DataBean dataBean = writeOneData(path,libId);
            inserData(dataBean);
        }
    }

    /**
     * 插入数据
     * @param data 插入单条数据
     * @throws IOException
     */
    private static void inserData( DataBean data) throws IOException{
        String tableName = pro.getProperty("HbaseTablePersonList");
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
        HTable table =(HTable) initHbase().getTable(tablename);
        table.setAutoFlush(true,true);
        table.put(put);
        table.close();
        LOGGER.info("**********PERSON_ID**********"+data.getPERSON_ID());
    }

    public static void main(String[] args) {
        pro = ReadProperties.getProperties(args[0]);
        int lib = Integer.parseInt(pro.getProperty("Lib"));
        String path = pro.getProperty("Path");
        try {
            writeHbase(path,lib);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
