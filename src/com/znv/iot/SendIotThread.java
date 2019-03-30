package com.znv.iot;

import javax.swing.JOptionPane;
import com.znv.frame.*;
public class SendIotThread extends Thread { 
	private String url;
	private String alarmJson;
	public int time;
	public int flag;
	
	public SendIotThread(String iurl,String ialarmJson,int itime,int iflag) {
		url = iurl;
		alarmJson = ialarmJson;
    	time = itime;
    	flag = iflag;
	}

	public void run() { 
		long sTime = time*1000;

		if(1 == flag){
			String ret = httpclient.doPost(url, alarmJson);
			JOptionPane.showMessageDialog(null, ret, "提示",JOptionPane.WARNING_MESSAGE);
		}
		else{
			do{
				try {
					String ret = httpclient.doPost(url, alarmJson);
					JOptionPane.showMessageDialog(null, ret, "提示",JOptionPane.WARNING_MESSAGE);			    
		            Thread.sleep(sTime);
		        } catch (InterruptedException e) {
		            e.printStackTrace(); 
		        } 
			}while(!base.iotStopFlag);
		}
	}  
}