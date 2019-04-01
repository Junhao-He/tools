package com.znv.netty.tcp.client;

import java.io.File;
import java.io.FileInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 *
 * Created by MaHuiming on 2018/3/12.
 */
public class ChildClientHandlerAdapter extends  ChannelHandlerAdapter {


    private int counter;
    private static int count =0;
    ChildClientHandlerAdapter(){
    	
    }
    
  //定义图片地址，设备ID，进出，进出类型等
    public  String filepath,deviceid="0",inout,type,time;
    ChildClientHandlerAdapter(String filepath,String deviceid,String inout,String type,String time){
    	this.filepath = filepath;
    	this.deviceid = deviceid;
    	this.inout = inout;
    	this.type = type;
    	this.time = time;
    }

    private void send(ChannelHandlerContext ctx) throws Exception {
    	ByteBuf buf = null;
    	//7e7e7e8b280101010808100e142bb2ccb0a2d2cc0000020101002050020201d6d0d0cbc1a6ceac0002ffffff000000000d
        StringBuffer msg = new StringBuffer("7e7e7e8b280101010808100e142bb2ccb0a2d2cc0000020101");
        
        byte[] deviceidbyte = intToBytes2(Integer.parseInt(deviceid,16));
        int length = deviceidbyte.length;
        if(length>4) {
        	length = 4;
        }
        byte[] pic = image2Bytes(filepath);
        String piclen = "000000"+String.valueOf(Integer.parseInt(String.valueOf(pic.length),16));
        piclen = piclen.substring(piclen.length()-6,piclen.length());
        msg.append(piclen);
        msg.append("020201d6d0d0cbc1a6ceac0002ffffff000000000d");
        
        if("进门".equals(inout)) {
        	msg.setCharAt(60, '1');
        }
        if("刷卡开门".equals(inout)) {
        	msg.setCharAt(58, '1');
        }
        byte[] profix = toBytes(msg.toString());
        
        for(int i=length-1; i>=0;i--) {
        	profix[47-(length-1-i)]=deviceidbyte[i];
        }
        //System.out.println("======"+pic.length);
        //System.out.println(profix.length+pic.length);
        buf = Unpooled.buffer(profix.length+pic.length);
        count ++;
        System.out.println("你已经发了"+String.valueOf(count)+"次了！");
        buf.writeBytes(profix);
        buf.writeBytes(pic);
        ctx.writeAndFlush(buf);
        ctx.flush();
        
    }
    /**
     * 向服务端发送消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if(!"-1".equals(time)) {
    		int i = Integer.parseInt(time);
    		while(true) {
    			send(ctx);
    			Thread.sleep(1000*i);	
    		}
    	}
    	if("".equals(filepath) || filepath ==null)
    	{
    		ByteBuf buf = null;
            StringBuffer msg = new StringBuffer("7e7e7e8b280101010808100e142bb2ccb0a2d2cc0000020101002050200201d6d0d0cbc1a6ceac0002ffffff000000000d"); 
            byte[] profix = toBytes(msg.toString());
            buf = Unpooled.buffer(profix.length);
            System.out.println(profix.length);
            buf.writeBytes(profix);
            ctx.writeAndFlush(buf);
            ctx.flush();
            ctx.close(); 
    	}
        send(ctx);
        ctx.close(); 
    }
    
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
    
    private byte[] image2Bytes(String imgSrc) throws Exception
    {
        FileInputStream fin = new FileInputStream(new File(imgSrc));
        //可能溢出,简单起见就不考虑太多,如果太大就要另外想办法，比如一次传入固定长度byte[]
        byte[] bytes  = new byte[fin.available()];
        //将文件内容写入字节数组，提供测试的case
        fin.read(bytes);

        fin.close();
        return bytes;
    }

    
    public static byte[] intToBytes2(int n){
        byte[] b = new byte[4];
       
        for(int i = 0;i < 4;i++)
        {
         b[i]=(byte)(n>>(24-i*8));
      
    } 
    return b;
  } 

    /**
     * 从服务端接收消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("now is :" + body + ";the counter is:" + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	System.out.println("出错了");
        ctx.close();
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    	System.out.println("客户端失去连接");

    }
}
