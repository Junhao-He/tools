package com.znv.vehicle;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class GetAllFiles {
	
	private List<byte[]> piclist = new ArrayList<>();
	private List<String> pathlist = new ArrayList<>();

	public List<byte[]> getPiclist() {
		return piclist;
	}

	public  List<String> getPathList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		byte[] pic = null;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				
				String fileName = files[i].getName();
				if (files[i].isDirectory()) { // 判断是文件还是文件夹
					getPathList(files[i].getAbsolutePath()); // 获取文件绝对路径
				} else if (fileName.endsWith(".jpg")) { // 判断文件名是否以.jpg结尾
					String strFileName = files[i].getAbsolutePath();					
					try {
						pic = image2Bytes(strFileName);
						piclist.add(pic);
					} catch (Exception e) {
						e.printStackTrace();
					}
			        //String path = client.uploadFile(pic, "jpg");
			        //path = "http://10.45.157.187:80/"+path;
			        //System.out.println(""+i+"::::"+path);
					//pathlist.add(path);
				} else {
					continue;
				}
			}
		}
		return pathlist;
	}

 private byte[] image2Bytes(String imgSrc) throws Exception
    {
        FileInputStream fin = new FileInputStream(new File(imgSrc));
        //可能溢出,简单起见就不考虑太多,如果太大就要另外想办法，比如一次传入固定长度byte[]
        byte[] bytes  = new byte[fin.available()];
        //将文件内容写入字节数组，提供测试的case
        fin.read(bytes);

        fin.close();
        return bytes;
    }

}
