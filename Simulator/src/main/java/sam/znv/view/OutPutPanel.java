package sam.znv.view;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import sam.znv.controller.SendController;
import sam.znv.kafka.ZKafkaConsumer;
import sam.znv.kafka.ZKafkaProducer;

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
    private int countPerSecond = 0;
    private int sendCount = 0;

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

        JLabel labelFrequency = new JLabel("发送频率（次/秒）：");
        labelFrequency.setBounds(40, 100, 120, 30);
        this.add(labelFrequency);

        JTextField frequencyInput = new JTextField("0");
        frequencyInput.setBounds(160, 100, 50, 30);
        this.add(frequencyInput);

        sendBtn = new JButton("发送");
        sendBtn.setBounds(280, 20, 100, 30);
        this.add(sendBtn);
        sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                //SendController.sendIdx1(filepath,1,1);
                System.out.println("发送：" + filepath);
                countPerSecond = Integer.parseInt(frequencyInput.getText());
                sendCount = 0;
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
        featureSendBtn.setBounds(280, 100, 100, 30);
        this.add(featureSendBtn);
        featureSendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SendController.sendByFeature(featureInput.getText());
            }
        });

        JTextArea messageArea = new JTextArea();
        messageArea.setBounds(40, 180, 300, 100);
        messageArea.setLineWrap(false);
        JScrollPane pane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setBounds(40, 180, 300, 100);
        pane.setViewportView(messageArea);
        this.add(pane);

        JButton receiveBtn = new JButton("读取告警");
        receiveBtn.setBounds(40, 140, 100, 30);
        this.add(receiveBtn);
        receiveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConsumerRecords<String, String> records = ZKafkaConsumer.getInstance().receiveMessage();
                for(ConsumerRecord<String, String> record : records)
                {
                    messageArea.append(record.value()+"\n");
                }
            }
        });
    }

    private void sendPic(String path)
    {
        System.out.println("sendCount:"+sendCount);
        System.out.println("countPerSecond:"+countPerSecond);
        File f = new File(path);
        if (f.isDirectory()) {
            String s[] = f.list();
            for (int i = 0; i < s.length; i++) {
                sendPic(path + "\\" + s[i]);
            }
        } else {
            if(countPerSecond>0&&sendCount>=countPerSecond)
            {
                sendCount =  0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendCount += 1;
            SendController.sendIdx1(path,1,1);
        }
    }
}
