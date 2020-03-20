package sam.znv.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static sam.znv.utils.GetDataFeature.getImageFeature;

/**
 * @Author: liujun
 * @Data: 2020/3/9 14:48
 **/
public class DetectFace {
    private static final String urlPicasso = "http://10.45.154.64:9010/verify/face/detect";
    private static final String urlST = "http://10.45.154.129/verify/face/detectAndQuality";
    public static void main(String[] args) {
        System.load(System.getProperty("user.dir") + "\\opencv_java340-x64.dll");
        detectPictureFace("D:\\img", "D:\\doc44", false);
    }

    public static void detectPictureFace(String pictureDocumentPath, String targetPath, Boolean isPicasso){
        File file = new File(targetPath);
        if(! file.isDirectory()) file.mkdirs();
        File picDoc = new File(pictureDocumentPath);
        if(picDoc.isDirectory()){
            File[] files = picDoc.listFiles();
            for (File file2 : files) {
                if(isPicasso){
                    parseOnePictureDataPicasso(file2.toString(), targetPath);
                }else{
                    parseOnePictureDataST(file2.toString(), targetPath);
                }
            }
        }else{
            if(isPicasso){
                parseOnePictureDataPicasso(pictureDocumentPath, targetPath);
            }else{
                parseOnePictureDataST(pictureDocumentPath, targetPath);
            }
        }
    }

    public static void parseOnePictureDataST(String picturePath, String targetPath){
        JSONObject urlResult = JSON.parseObject(getImageFeature(picturePath, urlST));
        if(urlResult.get("result").equals("error")){
            return;
        }
        JSONArray result = urlResult.getJSONArray("data");
        //JSONArray result = (JSONArray) data.getJSONObject(0).get("result");
        if(result.size() != 0){
            Mat mat = imread(picturePath);
            for(int i=0; i< result.size();i++){
                JSONArray rect = (JSONArray) result.getJSONObject(i).get("rect");
                int rect0= (int) rect.get(0);
                int rect1= (int) rect.get(1);
                int rect2= (int) rect.get(2);
                int rect3= (int) rect.get(3);
                Rect rectPicture = new Rect(rect0, rect1, (rect2-rect0), (rect3-rect1));
                Size size = new Size((rect2-rect0), (rect3-rect1));
                String outFile = getPath(picturePath, targetPath, i);
                Mat matNew = new Mat();
                Mat sub = mat.submat(rectPicture);
                Imgproc.resize(sub, matNew, size);// 将人脸进行截图并保存
                Imgcodecs.imwrite(outFile, matNew);
                System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
            }
        }else{
            System.out.println(String.format("未识别人脸图片： %s", picturePath));
        }
    }


    public static void parseOnePictureDataPicasso(String picturePath, String targetPath){
        JSONObject urlResult = JSON.parseObject(getImageFeature(picturePath, urlPicasso));
        if(urlResult.get("result").equals("error")){
            return;
        }
        JSONArray data = urlResult.getJSONArray("data");
        JSONArray result = (JSONArray) data.getJSONObject(0).get("result");
        if(result.size() != 0){
            Mat mat = imread(picturePath);
            for(int i=0; i< result.size();i++){
                JSONArray rect = (JSONArray) result.getJSONObject(i).get("rect");
                //System.out.println("cols:"+mat.cols() + "rows:"+mat.rows());
                int rect0= (int) rect.get(0);
                int rect1= (int) rect.get(1);
                int rect2= (int) rect.get(2);
                int rect3= (int) rect.get(3);
                Rect rectPicture = new Rect(rect0, rect1, (rect2-rect0), (rect3-rect1));
                Size size = new Size((rect2-rect0), (rect3-rect1));
                String outFile = getPath(picturePath, targetPath, i);
                Mat matNew = new Mat();
                Mat sub = mat.submat(rectPicture);
                Imgproc.resize(sub, matNew, size);// 将人脸进行截图并保存
                Imgcodecs.imwrite(outFile, matNew);
                System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
            }
        }else{
            System.out.println(String.format("未识别人脸图片： %s", picturePath));
        }
    }

    public static String getPath(String picturePath, String targetPath, int i){
        String[] sp = picturePath.split("\\\\");
        String pictureName = sp[sp.length - 1].split("\\.")[0];
        return targetPath +File.separator + pictureName+ "-" + i +".jpg";
    }

    /**
     *
     public static void imageCut(String imagePath, String outFile, int posX, int posY, int width, int height) {
     // 原始图像
     Mat image = Imgcodecs.imread(imagePath);
     // 截取的区域：参数,坐标X,坐标Y,截图宽度,截图长度
     Rect rect = new Rect(posX, posY, width, height);
     // 两句效果一样
     Mat sub = image.submat(rect); // Mat sub = new Mat(image,rect);
     Mat mat = new Mat();
     Size size = new Size(width, height);
     Imgproc.resize(sub, mat, size);// 将人脸进行截图并保存
     Imgcodecs.imwrite(outFile, mat);
     System.out.println(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
     }
     */
}
