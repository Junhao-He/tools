package sam.znv.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import java.util.UUID;

import static org.opencv.imgcodecs.Imgcodecs.imdecode;
import static sam.znv.config.ConfigManager.readResourceFile;
import static sam.znv.video.DetectFace.getPath;

/** 参考blog: https://blog.csdn.net/keizhige/article/details/103817067
 * auther: LiuJun
 * date: 2020/2/17 13:45.
 */
public class FrameGrabberKit {

    private static final Logger logger = LoggerFactory.getLogger(FrameGrabberKit.class);

    //大图图片写入文件地址
    private static String bigPicturePathInit;
    //小图图片写入文件地址
    private static String smallPicturePathInit;
    //视频路径
    private static String videoPath;
    //是否是Picasso算法
    private static String isPicasso;
    private static String urlPicasso;
    private static String urlST;

    static {
        Properties pro = readResourceFile("detectVideo.properties");
        bigPicturePathInit = pro.getOrDefault("bigPicturePathInit", "D:\\img").toString();
        smallPicturePathInit = pro.getOrDefault("smallPicturePathInit","D:\\doc44").toString();
        try {
            videoPath = new String(pro.getProperty("videoPath").getBytes("ISO-8859-1"), "utf-8");
        }catch ( Exception e){
            logger.error(e.getMessage());
        }
        isPicasso = pro.getOrDefault("isPicasso", "yes").toString();
        urlPicasso = pro.getOrDefault("urlPicasso", "http://10.45.154.218:9010/verify/face/detect").toString();
        urlST = pro.getOrDefault("urlST", "http://10.45.154.129/verify/face/detectAndQuality").toString();
        System.load(System.getProperty("user.dir") + "\\opencv_java340-x64.dll");
    }

    public static void main(String[] args) {


        getVediosImg(videoPath, bigPicturePathInit, smallPicturePathInit, 10);

    }

    public static void getVediosImg(String videoPath, String imgBigPath, String imgSmallPath, int rat){
        File file = new File(videoPath);
        if(! file.exists()){
            logger.error("传入视频地址无效");
            return;
        }
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f :files) {
                getVedioImg(f.getPath(), imgBigPath, imgSmallPath, rat);
            }
        }else{
            getVedioImg(videoPath, imgBigPath, imgSmallPath, rat);
        }
    }

    /**
     * 截取视频图片
     * @param videoPath 传入视频的路径
     * @param imgBigPath 传入图片存放路径
     * @param rate 设置提取帧的频率，正常情况下 10为1s
     */
    public static void getVedioImg(String videoPath, String imgBigPath, String imgSmallPath, int rate){
        File videoFile = new File(videoPath);
        if(!videoFile.exists()) {
            logger.info("视频路径不存在！！！");
            return;
        }

        //为每一个视频创建一个文件夹 大图一个文件夹 小图一个文件夹
        String[] sp = videoPath.split("\\\\");
        String pictureName = sp[sp.length - 1].split("\\.")[0];
        String imgPathVideo = imgBigPath + File.separator + pictureName;
        File imgPathVideoFile = new File(imgPathVideo);
        if (!imgPathVideoFile.isDirectory()) imgPathVideoFile.mkdirs();
        String imgSmallVideo = imgSmallPath + File.separator + pictureName;
        if (!new File(imgSmallVideo).isDirectory()) new File(imgSmallVideo).mkdirs();

        logger.info("图片路径----" + imgPathVideo);
        try{
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();
            int ftp = grabber.getLengthInFrames();
            int flag=0;
            Frame frame = null;
            while (flag <= ftp) {
                //获取帧
                frame = grabber.grabImage();
                //设置截取的频率
                if(flag % rate ==0){
                    doExecuteFrame(frame,flag, imgPathVideo, imgSmallVideo);
                }
                flag++;
            }
            grabber.close();
            grabber.stop();
        }catch(Exception e){
            logger.error("获取视频图片失败！", e);
        }
    }

    public static void doExecuteFrame(Frame f, int index, String bigPicturePath, String smallPicturePath) throws Exception {
        if (null == f || null == f.image) return;
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(f);
        String newNameBigPicture = bigPicturePath +File.separator + index + ".jpg";
        bufferedImageToByte(bufferedImage, newNameBigPicture, smallPicturePath);
    }

    /**
     * 将BufferedImage转换为byte[]
     * @param image
     * @return
     */
    public static void bufferedImageToByte(BufferedImage image, String newBigPictureName, String smallPicturePath){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            ImageIO.write(image, "jpg", baos);//写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            if(isPicasso.toLowerCase().equals("yes")){
                JSONObject data = getData(urlPicasso, bytes);
                parseOnePictureDataPicasso(data, bytes, newBigPictureName,smallPicturePath);
            }else{
                JSONObject data = getData(urlST, bytes);
                parseOnePictureDataST(data, bytes, newBigPictureName, smallPicturePath);
            }
        } catch (IOException e) {
            logger.error("提示:",e);
        }
    }

    public static void parseOnePictureDataPicasso(JSONObject urlResult, byte[] pictureByte, String picturePath, String smallPicturePath){
        if(urlResult.get("result").equals("error")){
            return;
        }
        JSONArray data = urlResult.getJSONArray("data");
        JSONArray result = (JSONArray) data.getJSONObject(0).get("result");
        if(result.size() != 0){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(picturePath);
                fileOutputStream.write(pictureByte);
            }catch (Exception e){
                logger.error("写入大图出错："+e.getMessage());
            }
            Mat mat = convertByteToMat(pictureByte);
            for(int i=0; i< result.size();i++){
                JSONArray rect = (JSONArray) result.getJSONObject(i).get("rect");
                int rect0 = (int) rect.get(0) < 0 ? 0 : (int) rect.get(0);
                int rect1= (int) rect.get(1) < 0 ? 0 : (int) rect.get(1);
                int rect2= ((int)rect.get(2) > mat.cols()) ? mat.cols():(int) rect.get(2);
                int rect3= ((int)rect.get(3) > mat.rows()) ? mat.rows():(int) rect.get(3);

                Rect rectPicture = new Rect(rect0, rect1, (rect2-rect0), (rect3-rect1));
                Size size = new Size((rect2-rect0), (rect3-rect1));
                String outFile = getPath(picturePath, smallPicturePath, i);
                Mat matNew = new Mat();
                Mat sub = mat.submat(rectPicture);
                Imgproc.resize(sub, matNew, size);// 将人脸进行截图并保存
                Imgcodecs.imwrite(outFile, matNew);
                logger.info(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
            }
        }else{
            logger.info(String.format("未识别人脸图片： %s", picturePath));
        }
    }

    public static void parseOnePictureDataST(JSONObject urlResult, byte[] pictureByte, String picturePath, String smallPicturePath){
        if(urlResult.get("result").equals("error")){
            return;
        }
        JSONArray result = urlResult.getJSONArray("data");
        if(result.size() != 0){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(picturePath);
                fileOutputStream.write(pictureByte);
            }catch (Exception e){
                logger.error("写入大图出错："+e.getMessage());
            }
            Mat mat = convertByteToMat(pictureByte);
            for(int i=0; i< result.size();i++){
                JSONArray rect = (JSONArray) result.getJSONObject(i).get("rect");
                int rect0 = (int) rect.get(0) < 0 ? 0 : (int) rect.get(0);
                int rect1= (int) rect.get(1) < 0 ? 0 : (int) rect.get(1);
                int rect2= ((int)rect.get(2) > mat.cols()) ? mat.cols():(int) rect.get(2);
                int rect3= ((int)rect.get(3) > mat.rows()) ? mat.rows():(int) rect.get(3);
                System.out.println("rect0: "+rect0 + "rect1: " + rect1 + "rect2: " + rect2 + "rect3: "+rect3 + "cols and rows" + mat.cols() + "--" + mat.rows());
                Rect rectPicture = new Rect(rect0, rect1, (rect2-rect0), (rect3-rect1));
                Size size = new Size((rect2-rect0), (rect3-rect1));
                String outFile = getPath(picturePath, smallPicturePath, i);
                Mat matNew = new Mat();
                Mat sub = mat.submat(rectPicture);
                Imgproc.resize(sub, matNew, size);// 将人脸进行截图并保存
                Imgcodecs.imwrite(outFile, matNew);
                logger.info(String.format("图片裁切成功，裁切后图片文件为： %s", outFile));
            }
        }else{
            logger.info(String.format("未识别人脸图片： %s", picturePath));
        }
    }


    private static Mat convertByteToMat(byte[] bytes){
        return imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
    }

    private static JSONObject getData(String url, byte[] imageData) {
        String result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody bab = new ByteArrayBody(imageData, UUID.randomUUID().toString());
        HttpEntity entity = MultipartEntityBuilder.create().addPart("imageData", bab).build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        HttpEntity resEntity = null;
        try {
            response = client.execute(httpPost);
            resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
            result = "{\"result\":\"-1\"}";
        } finally {
            httpPost.releaseConnection();
        }
        return JSON.parseObject(result);
    }

}

