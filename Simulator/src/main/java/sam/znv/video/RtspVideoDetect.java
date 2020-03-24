package sam.znv.video;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import org.junit.Test;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * @Author LiuJun
 * @create 2020/3/23 16:00
 */
public class RtspVideoDetect {
    private static final Logger logger = LoggerFactory.getLogger(RtspVideoDetect.class);

    static {
        System.load(System.getProperty("user.dir") + "\\opencv_java340-x64.dll");
    }
    public static void main(String[] args) {
        String rtspPath = "rtsp://admin:Znv123456@10.45.148.31:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1";
        String bigImgPath = "D:\\doc44";
        String smallImgPath = "D:\\img";

        try {
            rtspDetect(rtspPath,bigImgPath,smallImgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rtspDetect(String file, String bigPicturePath, String smallPicturePath) throws Exception{
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
        grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
        // 一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
        //grabber.setImageWidth(960);
        //grabber.setImageHeight(540);
        logger.info("grabber start");
        grabber.start();
        int count = 0;
        while (true){
            Frame frame = grabber.grabImage();
            count ++;
            if(count %2 == 0){
                FrameGrabberKit.doExecuteFrame(frame,count,bigPicturePath,smallPicturePath);
                logger.info("frame message: "+frame);
            }
        }
    }

    /**
    @Test
    public void testzc() throws FrameGrabber.Exception {
        //String file = "rtsp://192.168.2.38:5554/2";
        String file = "rtsp://admin:Znv123456@10.45.148.31:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1";
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
        grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
        // 一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
        //grabber.setImageWidth(960);
        //grabber.setImageHeight(540);
        System.out.println("grabber start");
        grabber.start();
        CanvasFrame canvasFrame = new CanvasFrame("sdsaf");
        canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvasFrame.setAlwaysOnTop(true);
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        // OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        int count = 0;
        while (true){
            Frame frame = grabber.grabImage();
            opencv_core.Mat mat = converter.convertToMat(frame);
            canvasFrame.showImage(frame);
            count ++ ;
            System.out.println("--------count-------"+count);
        }

    }
    */
}
