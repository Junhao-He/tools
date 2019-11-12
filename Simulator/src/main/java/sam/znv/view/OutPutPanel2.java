package sam.znv.view;

import sam.znv.kafka.LocalDataToKafka;
import sam.znv.kafka.ParseAvroDataConsumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static sam.znv.Function.GetFeature.getFeaturePath;

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

    //定义topic数据写入存储位置
    public static String filePathTopic;

    //定义topic数据读取存储地址
    public static String fileReadTopicPath;
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

        /**
         * 拉去指定topic数据
         * 设置 路径，服务器地址，topic
         */
        JLabel ipAddrText = new JLabel("指定服务器");
        ipAddrText.setBounds(40, 140, 100, 30);
        this.add(ipAddrText);

        JTextField  ipAddr = new JTextField("10.45.154.210");
        ipAddr.setBounds(160, 140, 120, 30);
        this.add(ipAddr);

        JLabel pollTopicText = new JLabel("指定拉取topic");
        pollTopicText.setBounds(40, 180, 100, 30);
        this.add(pollTopicText);

        JTextField pollTopic = new JTextField("fss-history-n-project-v1-2-production");
        pollTopic.setBounds(160, 180, 220, 30);
        this.add(pollTopic);

        JLabel storeDataText = new JLabel("选择存储地址");
        storeDataText.setBounds(40, 220, 100, 30);
        this.add(storeDataText);

        JButton selectFileStoreTopicData = new JButton("选择文件");
        selectFileStoreTopicData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser("./");             //设置选择器
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);   //设为多选
                int returnVal = chooser.showOpenDialog(null);        //是否打开文件选择框
                System.out.println("returnVal="+returnVal);
                if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型
                    filePathTopic = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
                    System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径 }
                }
            }
        });
        selectFileStoreTopicData.setBounds(160, 220, 100, 30);
        this.add(selectFileStoreTopicData);

        JButton sendBtnTopic = new JButton("拉取topic数据");
        sendBtnTopic.setBounds(380, 220, 140, 30);
        this.add(sendBtnTopic);
        sendBtnTopic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("topic数据选择存储位置："+filePathTopic);
                //设置ip地址
                ParseAvroDataConsumer.setBootstrapIp(ipAddr.getText().trim()+":9092");
                //设置拉取路径
                ParseAvroDataConsumer.setPath(filePathTopic+"\\a.txt");
                ParseAvroDataConsumer.parseAvroData(pollTopic.getText().trim());
            }
        });

        /**
         * 本地数据 写入指定服务器topic中
         * 指定服务器，数据地址，topic
         */
        JLabel ipAddrWriteText = new JLabel("指定服务器");
        ipAddrWriteText.setBounds(40, 270, 100, 30);
        this.add(ipAddrWriteText);

        JTextField  ipAddrWrite = new JTextField("10.45.154.210");
        ipAddrWrite.setBounds(160, 270, 120, 30);
        this.add(ipAddrWrite);

        JLabel pushTopicText = new JLabel("指定写入topic");
        pushTopicText.setBounds(40, 310, 100, 30);
        this.add(pushTopicText);

        JTextField pushTopic = new JTextField("fss-history-n-project-v1-2-production");
        pushTopic.setBounds(160, 310, 220, 30);
        this.add(pushTopic);

        JLabel selectDataText = new JLabel("选择数据地址");
        selectDataText.setBounds(40, 350, 100, 30);
        this.add(selectDataText);

        JButton selectFileStoreTopicData2 = new JButton("选择文件");
        selectFileStoreTopicData2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser("./");             //设置选择器
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setMultiSelectionEnabled(true);   //设为多选
                int returnVal = chooser.showOpenDialog(null);        //是否打开文件选择框
                System.out.println("returnVal="+returnVal);
                if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型
                    fileReadTopicPath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
                }
            }
        });
        selectFileStoreTopicData2.setBounds(160, 350, 100, 30);
        this.add(selectFileStoreTopicData2);

        JButton writeBtnTopic = new JButton("写入topic数据");
        writeBtnTopic.setBounds(380, 350, 140, 30);
        this.add(writeBtnTopic);
        writeBtnTopic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("选择topic数据存储位置："+fileReadTopicPath);
                //设置ip地址
                LocalDataToKafka.setBootstrapIp(ipAddrWrite.getText().trim()+":9092");
                //设置读取路径
                LocalDataToKafka.setPath(fileReadTopicPath);
                //设置拉取topic
                LocalDataToKafka.setTopic(pushTopic.getText().trim());
                LocalDataToKafka.localToKafka();
            }
        });

    }
}
