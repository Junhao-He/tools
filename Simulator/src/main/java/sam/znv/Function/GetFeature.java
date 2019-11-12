package sam.znv.Function;


import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import com.alibaba.fastjson.JSON;
import sam.znv.utils.GetDataFeature;

/**
 * Created by 86157 on 2019/7/24.
 * 输出特征路径地址，命名为：原文件名.feature
 * 保存上一路径格式
 */
public class GetFeature {

    static String requestUrl = "http://10.45.157.115:80/verify/feature/gets";
    //图片输入路径
    static String intPutPath = "F:\\picture\\[1-178]-7505fc64-be4a-4947-97c7-a799811a34d7";
    //特征输出路径
    static String outPutPath = "E:\\feature1";

    public static void main(String[] args){
        getFeaturePath(intPutPath,outPutPath);
    }
    public static void getFeaturePath(String intPutPath,String outPutPath){
        File f = new File(intPutPath);
        if (f.isDirectory()) {
            String s[] = f.list();
            for (int i = 0; i < s.length; i++) {
                getFeaturePath(intPutPath + "\\" + s[i],outPutPath);
            }
        } else {
            try {
                getOnePictureFeature(intPutPath,outPutPath);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void getOnePictureFeature(String picPath,String outPutPath) throws IOException {

        String feature = getFeature(picPath,requestUrl);
        File outPut = new File(outPutPath);
        outPut.mkdirs();
        File picturePath = new File(picPath);
        String name = picturePath.getName();
        //创建父节点文件夹
        String parent = picturePath.getParentFile().getName();
        new File(outPut+"\\"+parent).mkdirs();
        //创建文件路径
        String featurePath = outPut+"\\"+parent+"\\"+name+".feature";
        File file =new File(featurePath);
        if(file.exists()){
            file.delete();
        }else {
            file.createNewFile();
        }
        Writer out =new FileWriter(file);
        out.write(feature+"\r\n");
        out.close();
    }

    public static String getFeature(String picPath,String requestUrl){
        String data = GetDataFeature.getImageFeature(picPath,requestUrl);
        String feature = JSON.parseObject(data).getOrDefault("feature","").toString();
        return feature;
    }
}
