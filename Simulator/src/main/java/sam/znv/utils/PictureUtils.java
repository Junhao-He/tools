package sam.znv.utils;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;

public class PictureUtils {

	/**
	 * 将二进制图片数据保存到本地
	 * 
	 * @param image
	 *            图片的二进制数据
//	 * @param count
	 *            用于作为图片名称
	 * @param filePath
	 *            保存图片的路径
	 * @return 保存的图片的绝对路径名
	 */
	public static String savePicture(byte[] image, String filename, String filePath) {
		String fileName = filePath + "/" + filename + ".jpg";
		try {
			OutputStream outStr = new FileOutputStream(fileName);
			InputStream inStr = new ByteArrayInputStream(image);

			byte[] b = new byte[1024];
			int nRead = 0;
			while ((nRead = inStr.read(b)) != -1) {
				outStr.write(b, 0, nRead);
			}
			outStr.flush();
			outStr.close();
			inStr.close();

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException !");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * 将本地图片转为二进制数据
	 * 
	 * @param path
	 *            要转化的图片的绝对路径
	 * @return 转化后的二进制数据
	 */
	public static byte[] image2byte(String path) {
		byte[] data = null;
		FileImageInputStream input = null;
		try {
			input = new FileImageInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		} catch (FileNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
		return data;
	}

	public static void movePic(String srcPicPath, String destPicPath) {
		File srcFile = new File(srcPicPath);

		byte[] imageData = PictureUtils.image2byte(srcPicPath);
		PictureUtils.savePicture(imageData, srcFile.getName().replace(".jpg", ""), destPicPath);
		// srcFile.delete();
	}

	public static void main(String[] args) {
		String path = "E:/test/3.jpg";
		String path2 = "E:/test/test1";
		File file = new File(path2);
		if (!file.exists()) {
			file.mkdir();
		}
		byte[] image = image2byte(path);
		savePicture(image, "1", path2);

	}
}
