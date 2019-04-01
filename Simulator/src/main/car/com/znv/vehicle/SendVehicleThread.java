package com.znv.vehicle;

import javax.jms.Destination;
import javax.swing.JOptionPane;

import org.apache.activemq.command.ActiveMQTopic;
import com.znv.activemq.MqProducer;
import com.znv.vehicle.SendMsg;

public class SendVehicleThread extends Thread {
	MqProducer producer = new MqProducer();
	Destination destination = new ActiveMQTopic("openapi.pms.topic");
	SendMsg s = new SendMsg();

	public void run() {
		try {
			producer.sendMessage(destination, s.SendPms());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "发送失败", "提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, "发送成功", "提示", JOptionPane.WARNING_MESSAGE);
	}
}