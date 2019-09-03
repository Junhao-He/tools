package sam.znv.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;


/**
 * 
 * @author YDL
 *
 */
public class WindowContainer {
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    //ONE TO KAFKA
    OutPutPanel outputPanel = new OutPutPanel();
    //PICTURE TO FEATURE
    OutPutPanel2 outPutPanel2 = new OutPutPanel2();
    //PROPERTIES TO KAFKA
    OutPutPanel3 outPutPanel3 = new OutPutPanel3();
    //writeHbase
    OutPutPanel4 outPutPanel4 = new OutPutPanel4();


    public void initWindow() {
        JFrame frame = new JFrame();     
        frame.setTitle("模拟器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        //改变图片大小
        frame.setBounds(300, 300, 600, 500);
        frame.setLocationRelativeTo(null); // 居中
        frame.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("ONE TO KAFKA",outputPanel);
        tabbedPane.addTab("PICTURE TO FEATURE",outPutPanel2);
        tabbedPane.addTab("PROPERTIES TO KAFKA",outPutPanel3);
        tabbedPane.addTab("WRITE TO HBASE",outPutPanel4);

        frame.setVisible(true);
    }



//    public void restartSys() {
//        String path = FileUtil.getFilePath();
//        path = path.replace("deploy_icap", "run").substring(1) + "restart.bat";
//        String cmd = "cmd /c start " + path;
//        try {
//            Process ps = Runtime.getRuntime().exec(cmd);
//            ps.waitFor();
//        } catch (Exception e) {
//        }
//    }
}
