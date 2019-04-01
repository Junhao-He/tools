package com.znv.activemq;

//import javax.jms.Connection;

//import javax.jms.ConnectionFactory;
//import javax.jms.DeliveryMode;
//import javax.jms.Destination;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//import org.apache.activemq.ActiveMQConnection;
//import org.apache.activemq.ActiveMQConnectionFactory;
//
//public class MqProducer {
//    private static final int SEND_NUMBER = 5;
//    // ConnectionFactory ：连接工厂，JMS 用它创建连接
//    ConnectionFactory connectionFactory;
//    // Connection ：JMS 客户端到JMS Provider 的连接
//    Connection connection = null;
//    // Session： 一个发送或接收消息的线程
//    Session session;
//    // Destination ：消息的目的地;消息发送给谁.
//    Destination destination;
//    // MessageProducer：消息发送者
//    MessageProducer producer;
//    public void init() {
//       
//        // TextMessage message;
//        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
//        connectionFactory = new ActiveMQConnectionFactory(
//                ActiveMQConnection.DEFAULT_USER,
//                ActiveMQConnection.DEFAULT_PASSWORD,
//                "tcp://127.0.0.1:61616");
//        try {
//            // 构造从工厂得到连接对象
//            connection = connectionFactory.createConnection();
//            // 启动
//            connection.start();
//            // 获取操作连接
//            session = connection.createSession(Boolean.TRUE,
//                    Session.AUTO_ACKNOWLEDGE);
//            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
//            destination = session.createQueue("FirstQueue");
//            // 得到消息生成者【发送者】
//            producer = session.createProducer(destination);
//            // 设置不持久化，此处学习，实际根据项目决定
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//            // 构造消息，此处写死，项目就是参数，或者方法获取
//            sendMessage(session, producer);
//            session.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != connection)
//                    connection.close();
//            } catch (Throwable ignore) {
//            }
//        }
//    }
//
//    public static void sendMessage(TextMessage message)
//            throws Exception {
//        for (int i = 1; i <= SEND_NUMBER; i++) {
//            TextMessage message = session
//                    .createbyteMessage("ActiveMq 发送的消息" + i);
//            // 发送消息到目的地方
//            System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);
//            producer.send(message);
//        }
//    }
//}

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MqProducer {
	public static String ip;
	public static String port; 

	private String USER = ActiveMQConnection.DEFAULT_USER;
	private String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
	private String URL = "failover:(tcp://"+ip+":"+port+")?timeout=2000";

	private Destination destination = null;
	private Connection conn = null;
	private Session session = null;
	private MessageProducer producer = null;

	// 初始化
	private void initialize() throws JMSException, Exception {
		// 连接工厂
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
		conn = connectionFactory.createConnection();
		// 事务性会话，自动确认消息
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 消息的目的地（Queue/Topic）
		destination = session.createTopic("openapi.pms.topic");
		// destination = session.createTopic(SUBJECT);
		// 消息的提供者（生产者）
		producer = session.createProducer(destination);
		// 不持久化消息
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}

	public void sendMessage(Destination destination, final byte[] message) throws JMSException, Exception {
		initialize();
		// 连接到JMS提供者（服务器）
		conn.start();		
		// 发送字节消息
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes(message);
		producer.send(msg);
		close();
	}

	// 关闭连接
	public void close() throws JMSException {
		if (producer != null)
			producer.close();
		if (session != null)
			session.close();
		if (conn != null)
			conn.close();
	}

}