package sam.znv.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author LiuJun
 * @create 2019/11/12 9:39
 */
public class ReadProperties {

    /**
     * 加载配置文件
     * @param path 传入外部参数路径
     * @return
     */
    public static Properties getProperties(String path){
        Properties pro = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            pro.load(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

}
