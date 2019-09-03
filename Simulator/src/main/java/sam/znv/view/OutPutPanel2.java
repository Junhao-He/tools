package sam.znv.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static sam.znv.Function.getFeature.getFeaturePath;

/**
 * Created by 86157 on 2019/7/11.
 */
public class OutPutPanel2 extends JPanel {

    private static final long serialVersionUID = 1L;
    private JLabel outputIpLabel;
    private JButton sendBtn;

    //定义图片地址
    public static String filepath;
    public static String filePathFeature;

    public OutPutPanel2() {
        super();
        this.setBackground(Color.white);
        this.setLayout(null);

        outputIpLabel = new JLabel("选择图片或文件夹");
        outputIpLabel.setBounds(40, 20, 100, 30);
        this.add(outputIpLabel);

        JButton selectFileBtn = new JButton("选择文件");
        selectFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser("./");             //设置选择器
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);   //设为多选
                int returnVal = chooser.showOpenDialog(null);        //是否打开文件选择框
                System.out.println("returnVal="+returnVal);
                if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型
                    filepath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
                    System.out.println(filepath);
                    System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径 }
                }
            }
        });
        selectFileBtn.setBounds(160, 20, 100, 30);
        this.add(selectFileBtn);


        JLabel label = new JLabel("选择特征存储位置");
        label.setBounds(40, 60, 100, 30);
        this.add(label);


        JButton selectFeatureFile = new JButton("选择文件");
        selectFeatureFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser("./");             //设置选择器
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);   //设为多选
                int returnVal = chooser.showOpenDialog(null);        //是否打开文件选择框
                System.out.println("returnVal="+returnVal);
                if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型
                    filePathFeature = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
                    System.out.println(filepath);
                    System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径 }
                }
            }
        });
        selectFeatureFile.setBounds(160, 60, 100, 30);
        this.add(selectFeatureFile);

        sendBtn = new JButton("提取特征");
        sendBtn.setBounds(300, 60, 100, 30);
        this.add(sendBtn);
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //SendController.sendIdx1(filepath,1,1);
                System.out.println("提取图片特征存储位置：" + filePathFeature);
                getFeaturePath(filepath,filePathFeature);
            }
        });
    }
}
