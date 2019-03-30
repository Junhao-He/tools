package com.znv.netty.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 用于测试 Created by MaHuiming on 2018/3/12.
 */
public class TcpClient {

	public static Channel channel = null;
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public void connect(String host, int port,String filepath,String deviceid,String inout,String type,String time) {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						// 解决TCP粘包/拆包问题
						// arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
						// ByteBuf delimiter =
						// Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes());
						// 1024 是单条消息的最大长度，如果达到该长度后仍然没有找到分隔符就会抛出异常，这点大家要特别注意。delimiter就是我们的分隔符。
						// arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
						// arg0.pipeline().addLast(new StringDecoder());
						arg0.pipeline().addLast(new ChildClientHandlerAdapter(filepath,deviceid,inout,type,time));
					}
				});

		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			channel = future.channel();
			System.out.println("连接服务端成功");
			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						// 解决TCP粘包/拆包问题
						// arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
						// ByteBuf delimiter =
						// Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes());
						// 1024 是单条消息的最大长度，如果达到该长度后仍然没有找到分隔符就会抛出异常，这点大家要特别注意。delimiter就是我们的分隔符。
						// arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
						// arg0.pipeline().addLast(new StringDecoder());
						arg0.pipeline().addLast(new ChildClientHandlerAdapter());
					}
				});

		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			channel = future.channel();
			System.out.println("连接服务端成功");
			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TcpClient tcpClient = new TcpClient();
		tcpClient.connect("127.0.0.1", 8019);
		byte[] end = System.getProperty("line.separator").getBytes();
		System.out.println(end.length);
		for (int i = 0; i < end.length; i++) {
			System.out.println(end[i]);
		}
	}

}
