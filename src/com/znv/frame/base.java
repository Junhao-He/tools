package com.znv.frame;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.znv.activemq.MqProducer;
import com.znv.door.SendDoorThread;
import com.znv.iot.AlarmJson;
import com.znv.iot.SendIotThread;
//import com.znv.vehicle.CircSendVehicleThread;
import com.znv.vehicle.SendMsg;
import com.znv.vehicle.SendVehicleThread;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class base extends JFrame {
	
    private JTabbedPane tabbedPane;   
    private JPanel panelIot,panelDoor,panelVehicle;
	
	public static boolean iotStopFlag = false;
	public static boolean doorStopFlag = false;
	public static boolean vehicleStopFlag = false;
	public static SendVehicleThread vehicletask = new SendVehicleThread();
//	public static CircSendVehicleThread vehiclecirctask = new CircSendVehicleThread();
	//定义图片地址
    public static String filepath;

	//public static boolean vehicleStopFlag = false;
	
	private String url= "";
	
	private String alarmJson = "";

	//private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					base frame = new base();
					frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public base() {
		setTitle("模拟器");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 702, 510);
		
		JPanel panel = (JPanel) getContentPane();
		//创建选项卡面板对象  
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane); 
		    
		/*
		 * 
		 * --------------------------------Iot面板--------------------------------
		 * 
		 * */ 
        panelIot=new JPanel();  
        panelIot.setLayout(null);	
		
		JTextArea text_Iotip = new JTextArea();
		text_Iotip.setBackground(new Color(255, 255, 255));
		text_Iotip.setForeground(new Color(0, 0, 0));
		text_Iotip.setEditable(false);
		text_Iotip.setText("IP\uFF1A");
		text_Iotip.setBounds(23, 34, 48, 21);
		panelIot.add(text_Iotip);
		
		JTextArea textArea_Iotip = new JTextArea();
		textArea_Iotip.setText("10.45.157.162");
		textArea_Iotip.setBounds(81, 34, 115, 21);
		panelIot.add(textArea_Iotip);
		
		JTextArea text_Iotport = new JTextArea();
		text_Iotport.setEditable(false);
		text_Iotport.setText("\u7AEF\u53E3\uFF1A");
		text_Iotport.setBounds(206, 34, 43, 21);
		panelIot.add(text_Iotport);
		
		JTextArea textArea_Iotport = new JTextArea();
		textArea_Iotport.setText("8081");
		textArea_Iotport.setBounds(259, 34, 73, 21);
		panelIot.add(textArea_Iotport);
		
		JTextArea text_Manufactor = new JTextArea();
		text_Manufactor.setForeground(Color.BLACK);
		text_Manufactor.setEditable(false);
		text_Manufactor.setText("厂家：");
		text_Manufactor.setBounds(23, 94, 48, 22);
		panelIot.add(text_Manufactor);
		
		JTextArea text_AlarmInfo = new JTextArea();
		text_AlarmInfo.setText("告警消息：");
		text_AlarmInfo.setForeground(Color.BLACK);
		text_AlarmInfo.setEditable(false);
		text_AlarmInfo.setBounds(23, 188, 108, 22);
		panelIot.add(text_AlarmInfo);
		
		
		JTextArea text_AlarmType = new JTextArea();
		text_AlarmType.setEditable(false);
		text_AlarmType.setText("告警类型选择：");
		text_AlarmType.setBounds(206, 94, 97, 22);
		panelIot.add(text_AlarmType);
		
		JTextArea textArea_AlarmInfo = new JTextArea();
		textArea_AlarmInfo.setBounds(23, 221, 387, 213);
		panelIot.add(textArea_AlarmInfo);
		JScrollPane js=new JScrollPane(textArea_AlarmInfo);
		//分别设置水平和垂直滚动条自动出现
		js.setHorizontalScrollBarPolicy(
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js.setVerticalScrollBarPolicy(
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setBounds(23, 220, 387, 213);
		panelIot.add(js);
		
		JComboBox comboBox_Manufactor = new JComboBox();
		comboBox_Manufactor.setModel(new DefaultComboBoxModel(new String[] {"未来宽带", "电信", "联通"}));
		comboBox_Manufactor.setSelectedItem("未来宽带");
		comboBox_Manufactor.setBounds(81, 94, 115, 21);
		panelIot.add(comboBox_Manufactor);
		
		JComboBox comboBox_AlarmType = new JComboBox();
		comboBox_AlarmType.setModel(new DefaultComboBoxModel(new String[] 
				{"煤气告警", "活动探测", "一键求助", "人体感应", 
				"火灾告警","水位告警", "水箱检测", "地磁告警"}));
		comboBox_AlarmType.setSelectedItem("煤气告警");
		comboBox_AlarmType.setBounds(313, 94, 97, 21);
		panelIot.add(comboBox_AlarmType);
		
		comboBox_Manufactor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String manufacturer = (String)comboBox_Manufactor.getSelectedItem();
				String alarmType = (String)comboBox_AlarmType.getSelectedItem();
				if(manufacturer.equals("未来宽带")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/bstar/alarmlistener";
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.bstargas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.bstarmove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.bstaryijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.bstarbody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.bstarfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.bstarwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.bstarwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.bstardici;
					}
				}
				else if(manufacturer.equals("电信")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/telecom/alarmlistener";
					
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.telecomgas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.telecommove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.telecomyijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.telecombody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.telecomfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.telecomwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.telecomwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.telecomdici;
					}
				}
				else if(manufacturer.equals("联通")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/unicom/alarmlistener";
					
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.unicomgas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.unicommove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.unicomyijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.unicombody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.unicomfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.unicomwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.unicomwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.unicomdici;
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "厂家类型无法识别", "提示",JOptionPane.WARNING_MESSAGE);
					return;
				}
				textArea_AlarmInfo.setText(alarmJson);
			}
			});

		
		comboBox_AlarmType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String manufacturer = (String)comboBox_Manufactor.getSelectedItem();
				String alarmType = (String)comboBox_AlarmType.getSelectedItem();
				if(manufacturer.equals("未来宽带")){
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.bstargas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.bstarmove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.bstaryijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.bstarbody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.bstarfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.bstarwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.bstarwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.bstardici;
					}
				}
				else if(manufacturer.equals("电信")){
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.telecomgas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.telecommove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.telecomyijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.telecombody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.telecomfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.telecomwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.telecomwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.telecomdici;
					}
				}
				else if(manufacturer.equals("联通")){
					if(alarmType == "煤气告警")
					{
						alarmJson = AlarmJson.unicomgas;
					}	
					else if(alarmType == "活动探测")
					{
						alarmJson = AlarmJson.unicommove;
					}
					else if(alarmType == "一键求助")
					{
						alarmJson = AlarmJson.unicomyijian;
					}
					else if(alarmType == "人体感应")
					{
						alarmJson = AlarmJson.unicombody;
					}
					else if(alarmType == "火灾告警")
					{
						alarmJson = AlarmJson.unicomfire;
					}	
					else if(alarmType == "水位告警")
					{
						alarmJson = AlarmJson.unicomwaterline;
					}
					else if(alarmType == "水箱检测")
					{
						alarmJson = AlarmJson.unicomwaterbox;
					}
					else if(alarmType == "地磁告警")
					{
						alarmJson = AlarmJson.unicomdici;
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "厂家类型无法识别", "提示",JOptionPane.WARNING_MESSAGE);
					return;
				}
				textArea_AlarmInfo.setText(alarmJson);
			}
			});			
		
		JRadioButton rdbtnNewRadioButton_timing = new JRadioButton("定时发送");
		rdbtnNewRadioButton_timing.setBounds(23, 144, 83, 23);
		panelIot.add(rdbtnNewRadioButton_timing);
		
		JTextArea text_Timing = new JTextArea();
		text_Timing.setText("定时时间(秒)：");
		text_Timing.setForeground(Color.BLACK);
		text_Timing.setEditable(false);
		text_Timing.setBounds(109, 144, 117, 22);
		panelIot.add(text_Timing);
		
		JTextArea textArea_Timing = new JTextArea();
		textArea_Timing.setText("1");
		textArea_Timing.setBounds(236, 144, 72, 21);
		panelIot.add(textArea_Timing);
		
		JButton button = new JButton("停止");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				iotStopFlag = true;
			}
		});
		button.setBounds(342, 144, 68, 23);
		panelIot.add(button);
				
		
		JButton btnNewButton = new JButton("\u53D1\u9001");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				iotStopFlag = false;
				boolean isTimeSend = rdbtnNewRadioButton_timing.isSelected();
				
				String manufacturer = (String)comboBox_Manufactor.getSelectedItem();
				if(manufacturer.equals("未来宽带")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/bstar/alarmlistener";
				}
				else if(manufacturer.equals("电信")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/telecom/alarmlistener";
				}
				else if(manufacturer.equals("联通")){
					url = "http://"+textArea_Iotip.getText()+":"+textArea_Iotport.getText()+"/znvr/unicom/alarmlistener";
				}
				
				alarmJson = textArea_AlarmInfo.getText();
				try{
					JSONObject objJson = JSONObject.parseObject(alarmJson);					
				}catch (JSONException e2) {
		            JOptionPane.showMessageDialog(null, "json格式错误", "提示",JOptionPane.WARNING_MESSAGE);
		            return;	
				}
				
				if(isTimeSend){
					String times = textArea_Timing.getText();
					int iTime = Integer.parseInt(times);
					SendIotThread mtask = new SendIotThread(url, alarmJson, iTime, 2);
					mtask.start();
				}
				else{
					SendIotThread mtask = new SendIotThread(url, alarmJson, 0, 1);
					mtask.start();
				}
			}
		});
		btnNewButton.setBounds(342, 34, 68, 23);
		panelIot.add(btnNewButton);
		
		tabbedPane.addTab("iot告警", panelIot);
		
		JTextArea text_iotdevId = new JTextArea();
		text_iotdevId.setText("设备ID:");
		text_iotdevId.setBounds(420, 245, 71, 24);
		panelIot.add(text_iotdevId);
		
		JTextArea textArea_iotdevId = new JTextArea();
		textArea_iotdevId.setBounds(501, 245, 170, 24);
		panelIot.add(textArea_iotdevId);
		
		JTextArea text_iotdevType = new JTextArea();
		text_iotdevType.setText("设备类型：");
		text_iotdevType.setBounds(420, 295, 71, 24);
		panelIot.add(text_iotdevType);
		
		JComboBox comboBox_iotdevType = new JComboBox();
		comboBox_iotdevType.setModel(new DefaultComboBoxModel(new String[] {"GPS设备", "活力探测设备", "煤气检测设备", "烟感设备", "地磁设备"}));
		comboBox_iotdevType.setBounds(501, 296, 115, 21);
		panelIot.add(comboBox_iotdevType);
		
		JTextArea text_iotdevState = new JTextArea();
		text_iotdevState.setText("健康状态：");
		text_iotdevState.setBounds(420, 346, 71, 24);
		panelIot.add(text_iotdevState);
		
		JComboBox comboBox_iotdevState = new JComboBox();
		comboBox_iotdevState.setModel(new DefaultComboBoxModel(new String[] {"健康", "异常"}));
		comboBox_iotdevState.setBounds(500, 347, 116, 21);
		panelIot.add(comboBox_iotdevState);
		
		JButton button_sendIotdevState = new JButton("发送设备健康状态");
		button_sendIotdevState.setBounds(451, 391, 152, 23);
		panelIot.add(button_sendIotdevState);
		
		button_sendIotdevState.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		

        	}
        });
		
		/*
		 * 
		 * --------------------------------人脸门禁面板--------------------------------
		 * 
		 * */
        panelDoor=new JPanel(); 
        tabbedPane.addTab("门禁事件", panelDoor);
        panelDoor.setLayout(null);
        
        JTextArea txtrIp = new JTextArea();
        txtrIp.setEditable(false);
        txtrIp.setText("ip：");
        txtrIp.setBounds(38, 32, 33, 24);
        panelDoor.add(txtrIp);
        
        JTextArea textArea_9 = new JTextArea();
        textArea_9.setText("10.45.157.162");
        textArea_9.setBounds(81, 32, 106, 24);
        panelDoor.add(textArea_9);
        
        JTextArea txtrPort = new JTextArea();
        txtrPort.setEditable(false);
        txtrPort.setText("port：");
        txtrPort.setBounds(197, 32, 43, 24);
        panelDoor.add(txtrPort);
        
        JTextArea textArea_10 = new JTextArea();
        textArea_10.setText("8019");
        textArea_10.setBounds(250, 32, 60, 24);
        panelDoor.add(textArea_10);
        
        JTextArea textArea_13 = new JTextArea();
        textArea_13.setText("选择图片：");
        textArea_13.setEditable(false);
        textArea_13.setBounds(38, 66, 69, 24);
        panelDoor.add(textArea_13);
        
        JTextArea txtrid_1 = new JTextArea();
        txtrid_1.setText("设备ID：");
        txtrid_1.setEditable(false);
        txtrid_1.setBounds(38, 101, 69, 24);
        panelDoor.add(txtrid_1);
        
        JTextArea textArea_14 = new JTextArea();
        textArea_14.setEditable(false);
        textArea_14.setText("门禁进出：");
        textArea_14.setBounds(38, 135, 69, 23);
        panelDoor.add(textArea_14);
        
        JTextArea textArea_15 = new JTextArea();
        textArea_15.setEditable(false);
        textArea_15.setText("门禁进门方式：");
        textArea_15.setBounds(38, 172, 93, 21);
        panelDoor.add(textArea_15);
        
        JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"进门", "出门"}));
		comboBox.setSelectedItem("进门");
        comboBox.setBounds(129, 136, 93, 21);
        panelDoor.add(comboBox);
        
        JComboBox comboBox_3 = new JComboBox();
        comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"人脸识别开门", "刷卡开门"}));
		comboBox_3.setSelectedItem("刷卡开门");
        comboBox_3.setBounds(143, 173, 93, 21);
        panelDoor.add(comboBox_3);
        
        JTextArea textArea_16 = new JTextArea();
        textArea_16.setBounds(129, 101, 135, 24);
        panelDoor.add(textArea_16);
        
        JTextArea txtrs = new JTextArea();
        txtrs.setText("定时发送(秒)：");
        txtrs.setEditable(false);
        txtrs.setBounds(38, 213, 93, 21);
        panelDoor.add(txtrs);
        
        JTextArea textArea_17 = new JTextArea();
        textArea_17.setText("-1");
        textArea_17.setBounds(143, 213, 43, 21);
        panelDoor.add(textArea_17);
        
        JTextArea textArea_18 = new JTextArea();
        textArea_18.setText("同时上报个数：");
        textArea_18.setEditable(false);
        textArea_18.setBounds(197, 213, 98, 24);
        panelDoor.add(textArea_18);
        
        JTextArea textArea_19 = new JTextArea();
        textArea_19.setText("1");
        textArea_19.setBounds(305, 213, 33, 21);
        panelDoor.add(textArea_19);
        
        JButton btnNewButton_1 = new JButton("选择文件");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		JFileChooser chooser = new JFileChooser("./");             //设置选择器 
        		chooser.setMultiSelectionEnabled(true);   //设为多选
        		int returnVal = chooser.showOpenDialog(button);        //是否打开文件选择框
        		System.out.println("returnVal="+returnVal); 
        		if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型 
        			filepath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
        			System.out.println(filepath); 
        			System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径 }
        		}
        	}
        });
        btnNewButton_1.setBounds(129, 66, 93, 23);
        panelDoor.add(btnNewButton_1);
        
        JButton Button_doorStart = new JButton("发送");
        Button_doorStart.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		SendDoorThread.ip = textArea_9.getText();
        		SendDoorThread.port = textArea_10.getText();
        		
        		SendDoorThread.filepath =filepath;
        		SendDoorThread.deviceid = textArea_16.getText();
        		SendDoorThread.inout = (String)comboBox.getSelectedItem();
        		SendDoorThread.type = (String)comboBox_3.getSelectedItem();
        		SendDoorThread.time = textArea_17.getText();
        		int num = Integer.parseInt(textArea_19.getText());
        		for(int i=1; i<num+1; i++) {
        			SendDoorThread doortask = new SendDoorThread();
    				doortask.start();
        		}
        		
        	}
        });
        Button_doorStart.setBounds(38, 259, 93, 23);
        panelDoor.add(Button_doorStart);
        
        JButton btnNewButton_2 = new JButton("单元门未关闭告警发送");
        btnNewButton_2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		SendDoorThread.ip = textArea_9.getText();
        		SendDoorThread.port = textArea_10.getText();	
        		SendDoorThread.filepath ="";
        		SendDoorThread.deviceid = "";
        		SendDoorThread.inout = "";
        		SendDoorThread.type = "";
        		SendDoorThread.time = "";
        		SendDoorThread doortask = new SendDoorThread();
    			doortask.start();
        		
        	}
        });
        btnNewButton_2.setBounds(38, 307, 174, 23);
        panelDoor.add(btnNewButton_2);
        
          
        /*
		 * 
		 * --------------------------------过车事件面板--------------------------------
		 * 
		 * */
        panelVehicle=new JPanel(); 
        tabbedPane.addTab("过车事件", panelVehicle);
        panelVehicle.setLayout(null);
        
        JTextArea txtrPort_1 = new JTextArea();
        txtrPort_1.setText("port：");
        txtrPort_1.setEditable(false);
        txtrPort_1.setBounds(44, 79, 38, 24);
        panelVehicle.add(txtrPort_1);
        
        JTextArea textArea_11 = new JTextArea();
        textArea_11.setText("61616");
        textArea_11.setBounds(104, 79, 50, 24);
        panelVehicle.add(textArea_11);
        
        JTextArea txtrIp_1 = new JTextArea();
        txtrIp_1.setText("ip：");
        txtrIp_1.setEditable(false);
        txtrIp_1.setBounds(44, 33, 38, 24);
        panelVehicle.add(txtrIp_1);
        
        JTextArea textArea_12 = new JTextArea();
        textArea_12.setText("10.45.157.170");
        textArea_12.setBounds(104, 33, 110, 24);
        panelVehicle.add(textArea_12);
        
        JTextArea textArea_8 = new JTextArea();
        textArea_8.setEditable(false);
        textArea_8.setText("车牌：");
        textArea_8.setBounds(44, 121, 50, 24);
        panelVehicle.add(textArea_8);
        
        JTextArea textArea_plateno = new JTextArea();
        textArea_plateno.setText("沪KA9753");
        textArea_plateno.setBounds(104, 121, 98, 24);
        panelVehicle.add(textArea_plateno);
        
        JTextArea txtrid = new JTextArea();
        txtrid.setEditable(false);
        txtrid.setText("卡口id：");
        txtrid.setBounds(44, 165, 50, 24);
        panelVehicle.add(txtrid);
        
        JTextArea textArea_gateid = new JTextArea();
        textArea_gateid.setText("");
        textArea_gateid.setBounds(106, 165, 170, 24);
        panelVehicle.add(textArea_gateid);
        
        JTextArea txtrid_road = new JTextArea();
        txtrid_road.setEditable(false);
        txtrid_road.setText("道闸id:");
        txtrid_road.setBounds(44, 199, 50, 24);
		panelVehicle.add(txtrid_road);
		
		JTextArea textArea_road = new JTextArea();
		textArea_road.setBounds(104, 199, 172, 24);
		panelVehicle.add(textArea_road);
        
        
        JTextArea txtrstate = new JTextArea();
		txtrstate.setEditable(false);
		txtrstate.setText("设备健康状态：");
		txtrstate.setBounds(285, 165, 100, 24);
		panelVehicle.add(txtrstate);

		JComboBox comboBox_rstate = new JComboBox();
		comboBox_rstate.setModel(new DefaultComboBoxModel(new String[] { "正常", "异常" }));
		comboBox_rstate.setSelectedItem("正常");
		comboBox_rstate.setBounds(395, 165, 85, 21);
		panelVehicle.add(comboBox_rstate);

		JTextArea eventtype = new JTextArea();
		eventtype.setEditable(false);
		eventtype.setText("事件类型：");
		eventtype.setBounds(44, 243, 58, 24);
		panelVehicle.add(eventtype);

		JComboBox comboBox_eventtype = new JComboBox();
		comboBox_eventtype.setModel(new DefaultComboBoxModel(new String[] { "进", "出" }));
		comboBox_eventtype.setSelectedItem("进");
		comboBox_eventtype.setBounds(110, 244, 85, 21);
		panelVehicle.add(comboBox_eventtype);
		String defailturl = System.getProperty("user.dir")+"\\pic\\AU3S80.jpg";
		
		
		JTextArea txtInterval = new JTextArea();
		txtInterval.setText("间隔时长：");
		txtInterval.setEditable(false);
		txtInterval.setBounds(40, 315, 70, 23);
		txtInterval.setVisible(false);
		panelVehicle.add(txtInterval);

		JTextArea textArea_Interval = new JTextArea();
		textArea_Interval.setText("1000");
		textArea_Interval.setBounds(120, 315, 50, 23);
		textArea_Interval.setVisible(false);
		panelVehicle.add(textArea_Interval);
		
		JTextArea txttime = new JTextArea();
		txttime.setText("ms");
		txttime.setEditable(false);
		txttime.setBounds(175, 315, 20, 20);
		txttime.setVisible(false);
		panelVehicle.add(txttime);
		
		JCheckBox Check_CircSend = new JCheckBox("循环发送");
		Check_CircSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Check_CircSend.isSelected()) {
					txtInterval.setVisible(true);
					textArea_Interval.setVisible(true);
					txttime.setVisible(true);
				}
				else {
					txtInterval.setVisible(false);
					textArea_Interval.setVisible(false);
					txttime.setVisible(false);
				}
			}
		});
		Check_CircSend.setBounds(40, 285, 100, 23);
		panelVehicle.add(Check_CircSend);
		
		JButton Button_vehicleStart = new JButton("发  送");
		Button_vehicleStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean iscircsend = Check_CircSend.isSelected();
				int intervalTime = Integer.parseInt(textArea_Interval.getText());
				MqProducer.ip = textArea_12.getText();
				MqProducer.port = textArea_11.getText();
				SendMsg.deviceId = textArea_gateid.getText();
				SendMsg.rawId = textArea_road.getText();
				SendMsg.plateNo = textArea_plateno.getText();
				if(comboBox_eventtype.getSelectedIndex() == 0) {
					SendMsg.eventtype = 524545;
				}
				else {
					SendMsg.eventtype = 524546;
				}
//				if(vehicleStopFlag == true) {//当前已在循环发送，再次点击则停止
//					vehiclecirctask.terminate();
//					Button_vehicleStart.setText("发  送");
//					vehicleStopFlag = false;
//					vehiclecirctask = new CircSendVehicleThread();
//					return;
//				}
//
//				if(iscircsend == true) {//循环发送
//					vehiclecirctask = new CircSendVehicleThread();
//					vehiclecirctask.SetCircSendInterval(intervalTime);
//					vehiclecirctask.start();
//					Button_vehicleStart.setText("停止发送");
//					vehicleStopFlag = true;
//				}
//				else {//不需要循环发送
//					vehicletask = new SendVehicleThread();
//					vehicletask.start();
//				}
			}
		});
		Button_vehicleStart.setBounds(40, 400, 93, 23);
		panelVehicle.add(Button_vehicleStart);
		
		
		
		/*
		 * 
		 * --------------------------------警情面板--------------------------------
		 * 
		 * */
		JPanel panelPoliceSituation = new JPanel();
		tabbedPane.addTab("警情", panelPoliceSituation);
		panelPoliceSituation.setLayout(null);
		
		JTextArea txtrip = new JTextArea();
		txtrip.setText("数据库ip:");
		txtrip.setBounds(24, 22, 75, 24);
		panelPoliceSituation.add(txtrip);
		
		JTextArea textArea = new JTextArea();
		textArea.setText("10.45.157.170");
		textArea.setBounds(109, 22, 123, 24);
		panelPoliceSituation.add(textArea);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setText("端口号：");
		textArea_1.setBounds(247, 22, 56, 24);
		panelPoliceSituation.add(textArea_1);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setText("3306");
		textArea_2.setBounds(313, 22, 75, 24);
		panelPoliceSituation.add(textArea_2);
		
		
		/*
		 * 
		 * --------------------------------警力面板--------------------------------
		 * 
		 * */
		JPanel panelPoliceForce = new JPanel();
		tabbedPane.addTab("警力", null, panelPoliceForce, null);
		panelPoliceForce.setLayout(null);
		
		JTextArea text_plateNo = new JTextArea();
		text_plateNo.setEnabled(false);
		text_plateNo.setEditable(false);
		text_plateNo.setText("车牌号：");
		text_plateNo.setBounds(20, 67, 56, 24);
		panelPoliceForce.add(text_plateNo);
		
		JTextArea textArea_plateNo = new JTextArea();
		textArea_plateNo.setEnabled(false);
		textArea_plateNo.setEditable(false);
		textArea_plateNo.setBounds(86, 67, 92, 24);
		panelPoliceForce.add(textArea_plateNo);
		
		JTextArea text_policeNo = new JTextArea();
		text_policeNo.setEditable(false);
		text_policeNo.setText("单兵号：");
		text_policeNo.setBounds(201, 67, 56, 24);
		panelPoliceForce.add(text_policeNo);
		
		JTextArea textArea_policeNo = new JTextArea();
		textArea_policeNo.setBounds(267, 67, 92, 24);
		panelPoliceForce.add(textArea_policeNo);
		
		JTextArea text_policeKind = new JTextArea();
		text_policeKind.setEditable(false);
		text_policeKind.setText("警力类型：");
		text_policeKind.setBounds(20, 20, 69, 24);
		panelPoliceForce.add(text_policeKind);
		
		JComboBox comboBox_policeKind = new JComboBox();
		comboBox_policeKind.setModel(new DefaultComboBoxModel(new String[] {"单兵", "警车"}));
		comboBox_policeKind.setBounds(109, 20, 69, 24);
		comboBox_policeKind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(comboBox_policeKind.getSelectedIndex() == 0) {//警员
					text_plateNo.setEnabled(false);
					textArea_plateNo.setEnabled(false);
					text_policeNo.setEnabled(true);
					textArea_policeNo.setEnabled(true);
					textArea_policeNo.setEditable(true);
				}
				else {//警车
					text_plateNo.setEnabled(true);
					textArea_plateNo.setEnabled(true);
					textArea_plateNo.setEditable(true);
					text_policeNo.setEnabled(false);
					textArea_policeNo.setEnabled(false);
				}
			}
		});
		panelPoliceForce.add(comboBox_policeKind);
		
		JTextArea text_state = new JTextArea();
		text_state.setEditable(false);
		text_state.setText("当前状态：");
		text_state.setBounds(201, 20, 69, 24);
		panelPoliceForce.add(text_state);
		
		JComboBox comboBox_state = new JComboBox();
		comboBox_state.setModel(new DefaultComboBoxModel(new String[] {"在岗", "出警"}));
		comboBox_state.setBounds(290, 20, 69, 23);
		panelPoliceForce.add(comboBox_state);
		
		JTextArea txtrX = new JTextArea();
		txtrX.setEditable(false);
		txtrX.setText(" X：");
		txtrX.setBounds(20, 110, 31, 24);
		panelPoliceForce.add(txtrX);
		
		JTextArea txtrY = new JTextArea();
		txtrY.setEditable(false);
		txtrY.setText(" Y：");
		txtrY.setBounds(201, 110, 31, 24);
		panelPoliceForce.add(txtrY);
		
		JTextArea textArea_X = new JTextArea();
		textArea_X.setText("0.0");
		textArea_X.setBounds(61, 110, 117, 24);
		panelPoliceForce.add(textArea_X);
		
		JTextArea textArea_Y = new JTextArea();
		textArea_Y.setText("0.0");
		textArea_Y.setBounds(242, 110, 117, 24);
		panelPoliceForce.add(textArea_Y);
		
		
		JButton Button_Report = new JButton("上  报");
		Button_Report.setBounds(20, 155, 93, 23);
		panelPoliceForce.add(Button_Report);
	}
}
