package sam.znv.view;

import sam.znv.controller.SendController;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

/**
 * 数据配置信息
 * 
 * @author YDL
 *
 */
public class OutPutPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JLabel outputIpLabel;
    private JButton sendBtn;
	//定义图片地址
    public static String filepath;

    public OutPutPanel() {
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
        
        sendBtn = new JButton("发送");
        sendBtn.setBounds(280, 20, 100, 30);
        this.add(sendBtn);
        sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                //SendController.sendIdx1(filepath,1,1);
                System.out.println("发送：" + filepath);
                sendPic(filepath);
			}
		});

        JLabel labelFeature = new JLabel("输入特征：");
        labelFeature.setBounds(40, 60, 100, 30);
        this.add(labelFeature);

        JTextField featureInput = new JTextField();
        featureInput.setBounds(100, 60, 160, 30);
        this.add(featureInput);

        JButton featureSendBtn = new JButton("发送特征");
        featureSendBtn.setBounds(280, 60, 100, 30);
        this.add(featureSendBtn);
        featureSendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SendController.sendByFeature(featureInput.getText());
            }
        });

    }

    private void sendPic(String path)
    {
        File f = new File(path);
        if (f.isDirectory()) {
            String s[] = f.list();
            for (int i = 0; i < s.length; i++) {
                sendPic(path + "\\" + s[i]);
            }
        } else {
            SendController.sendIdx1(path,1,1);
        }
    }
}
