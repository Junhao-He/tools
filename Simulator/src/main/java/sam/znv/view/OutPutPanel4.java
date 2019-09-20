package sam.znv.view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import com.znv.fss.hbaseReadAndWrite.writePhoenixPerson;
import sam.znv.controller.SendController;

import static sam.znv.hbase.HbaseWriteAndRead.getDataRowKey;
import static sam.znv.hbase.HbaseWriteAndRead.writeHbase;

/**
 * Created by 86157 on 2019/8/14.
 */
public class OutPutPanel4 extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel outputIpLabel;
    private JButton sendBtn;
    private JButton sendBtn2;
    private JButton sendBtn3;
    public static int lib_id;
    //定义图片地址
    public static String filepath;

    public OutPutPanel4() {
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
                }
            }
        });
        selectFileBtn.setBounds(160, 20, 100, 30);
        this.add(selectFileBtn);

        JLabel label = new JLabel("指定名单库");
        label.setBounds(40, 60, 100, 30);
        this.add(label);

        JTextField frequencyInput = new JTextField("20");
        frequencyInput.setBounds(160, 60, 50, 30);
        this.add(frequencyInput);

        sendBtn = new JButton("发送布控库");
        sendBtn.setBounds(300, 60, 100, 30);
        this.add(sendBtn);
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lib_id = Integer.parseInt(frequencyInput.getText().trim());
                try{
                    System.out.println("选择的路径："+filepath);
                    writeHbase(filepath,lib_id);
                }catch (Exception e1){
                    e1.printStackTrace();
                }

            }
        });

        //发送通知topic
        sendBtn3 = new JButton("发送通知Topic");
        sendBtn3.setBounds(420, 60, 120, 30);
        this.add(sendBtn3);
        sendBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SendController.sendNoticTopic(frequencyInput.getText().trim());
            }
        });


        //传入lib_id 和 person_id
        JLabel labelAlarm = new JLabel("查询告警库");
        labelAlarm.setBounds(40, 120, 100, 30);
        this.add(labelAlarm);

        JTextField alarmLib = new JTextField("2");
        alarmLib.setBounds(160, 120, 50, 30);
        this.add(alarmLib);

        JLabel labelPersonId = new JLabel("查询人员ID");
        labelPersonId.setBounds(40, 160, 100, 30);
        this.add(labelPersonId);

        JTextField alarmPersonId = new JTextField("7597655675459787609");
        alarmPersonId.setBounds(160, 160, 160, 30);
        this.add(alarmPersonId);

        //数据显示
        JTextArea messageArea = new JTextArea();
        messageArea.setBounds(120, 200, 500, 200);
        messageArea.setLineWrap(false);
        JScrollPane pane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setBounds(40, 200, 500, 200);
        pane.setViewportView(messageArea);
        this.add(pane);


        //查询告警数据 显示到TextArea中
        sendBtn2 = new JButton("查询告警库信息");
        sendBtn2.setBounds(340, 160, 120, 30);
        this.add(sendBtn2);
        sendBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String alarm_Lib = alarmLib.getText().trim();
                String alarm_Person_Id = alarmPersonId.getText().trim();
                try{
                    System.out.println("查询名单库是："+alarm_Lib+"查询的person_id是："+alarm_Person_Id);
                    messageArea.append(getDataRowKey(alarm_Person_Id,alarm_Lib));
                    messageArea.append("\r\n");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
    }
}
