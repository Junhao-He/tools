package sam.znv;

import com.znv.door.SendDoorThread;
import com.znv.vehicle.SendVehicleThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class base extends JFrame {
	
    private JTabbedPane tabbedPane;   
    private JPanel panelDoor;
	
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
					sam.znv.base frame = new base();
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
		    



		JButton button = new JButton("停止");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iotStopFlag = true;
			}
		});
		button.setBounds(342, 144, 68, 23);




		/*
		 *
		 * --------------------------------人脸门禁面板--------------------------------
		 *
		 * */
        panelDoor=new JPanel();
        tabbedPane.addTab("one to kafka", panelDoor);
        panelDoor.setLayout(null);

        JTextArea textArea_13 = new JTextArea();
        textArea_13.setText("选择图片：");
        textArea_13.setEditable(false);
        textArea_13.setBounds(38, 32, 69, 24);
        panelDoor.add(textArea_13);

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
        btnNewButton_1.setBounds(129, 32, 93, 23);
        panelDoor.add(btnNewButton_1);

        JButton Button_doorStart = new JButton("发送");
        Button_doorStart.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		SendDoorThread.filepath =filepath;
        	}
        });
        Button_doorStart.setBounds(38, 259, 93, 23);
        panelDoor.add(Button_doorStart);
	}
}
