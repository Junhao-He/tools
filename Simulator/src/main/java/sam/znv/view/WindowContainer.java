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
    OutPutPanel outputPanel = new OutPutPanel(); // OutPut TAB


    public void initWindow() {
        JFrame frame = new JFrame();     
        frame.setTitle("模拟器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setBounds(200, 200, 490, 350);
        frame.setLocationRelativeTo(null); // 居中
        frame.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.add(outputPanel,"ONE TO KAFKA", 0);

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
