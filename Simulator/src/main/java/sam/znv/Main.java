package sam.znv;

import sam.znv.view.WindowContainer;

import javax.swing.UIManager;



/**
 * 启动入口
 * 
 * @author YDL
 *
 */
public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // 换肤
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                }
                new WindowContainer().initWindow();
            }
        });
    }

}