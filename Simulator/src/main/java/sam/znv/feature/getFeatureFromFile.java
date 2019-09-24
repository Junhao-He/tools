package sam.znv.feature;

import java.io.*;

public class getFeatureFromFile {
    public static void main(String[] args) {
        String file = "F:\\pictureDir\\feature特征";
        String pic = "E:\\cq_data\\[1-209]-7505fc64-be4a-4947-97c7-a799811a34d7\\0b38517728254adda16c386ab6f7e81c.jpg";
        String result = getFeatureFile(pic,file);
        System.out.println("*********result*************"+"\r\n"+result);
    }

    /**
     *
     * @param picPath 传入图片路径
     * @param filePath 传入图片特征路径
     * @return
     */
    public static String getFeatureFile(String picPath,String filePath){
        String[] strings = picPath.split("\\\\");
        String stringFile = strings[strings.length - 1]+".feature";
        File file = new File(filePath);
        File[] tempList = file.listFiles();
        for(int i=0; i< tempList.length; i++){
            if(tempList[i].isFile()){
                if(tempList[i].getName().equals(stringFile)){
                    return readFile(tempList[i]);
                }
            }
            if(tempList[i].isDirectory()){
                for(File filenext :tempList[i].listFiles()){
                    if(filenext.getName().equals(stringFile)){
                        System.out.println("**************匹配成功**************"+filenext.getName());
                        return readFile(filenext);
                    }
                }
            }

        }
        return "";
    }

    /**
     *读取文件内容
     * @param file 传入文件
     * @return
     */
    public static String readFile(File file){
        String encoding = "UTF-8";
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
