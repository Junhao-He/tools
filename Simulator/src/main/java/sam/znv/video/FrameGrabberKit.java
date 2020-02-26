package sam.znv.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/** 参考blog: https://blog.csdn.net/keizhige/article/details/103817067
 * auther: LiuJun
 * date: 2020/2/17 13:45.
 */
public class FrameGrabberKit {

    private static final Logger logger = LoggerFactory.getLogger(FrameGrabberKit.class);

    public static void main(String[] args) {
        //传入两个路径：视频文件路径、图片文件夹所在路径;
        //将图片发送商汤，商汤提取特征正常，则将图片保存下来，写入一个文件夹下;
        getVedioImg("D:\\video\\20200217_120349.mp4", "D:\\img", 10);
    }

    /**
     * 截取视频图片
     * @param videoPath 传入视频的路径
     * @param imgPath 传入图片存放路径
     * @param rate 设置提取帧的频率，正常情况下 10为1s
     */
    public static void getVedioImg(String videoPath, String imgPath, int rate){
        File videoFile = new File(videoPath);
        if(!videoFile.exists()) return;
        File imgFile = new File(imgPath);
        if (!imgFile.isDirectory()){
            imgFile.mkdirs();
        }
        System.out.println("路径----"+imgFile.toString());
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
                    doExecuteFrame(frame,flag, imgFile.toString());
                }
                flag++;
            }
            grabber.close();
            grabber.stop();
        }catch(Exception e){
            logger.error("获取视频图片失败！", e);
        }
    }

    public static void doExecuteFrame(Frame f, int index, String picturePath) throws Exception {
        if (null == f || null == f.image) return;
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(f);
        String newFileName = picturePath +File.separator + index + ".jpeg";
        ImageIO.write(bufferedImage, "jpeg", new File(newFileName));
    }

}

