package com.znv.door;

import javax.swing.JOptionPane;

import com.znv.netty.tcp.client.TcpClient;

public class SendDoorThread extends Thread{
	public static String ip;
	public static String port;
	
	//定义图片地址，设备ID，进出，进出类型等
    public static String filepath,deviceid,inout,type,time;

	public void run() { 
		try{
			System.out.println("我在跑！！");
			TcpClient tcpClient = new TcpClient();
	        tcpClient.connect(ip,Integer.parseInt(port),filepath,deviceid,inout,type,time);	 
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "发送失败", "提示",JOptionPane.WARNING_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, "发送成功", "提示",JOptionPane.WARNING_MESSAGE);
	}  
}
