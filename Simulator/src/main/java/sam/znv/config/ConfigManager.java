package sam.znv.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

/**
 * @Author: ct
 * @Date: 2020/3/4 16:11
 * @Version 1.0
 */
public class ConfigManager {

    public static Properties readResourceFile(String fileName){
        Properties props = new Properties();
        URL url = ConfigManager.class.getClassLoader().getResource(fileName);
        try {
            props.load(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }


    public ConfigManager(){

    }


    public static void main(String[] args){

    }







}
