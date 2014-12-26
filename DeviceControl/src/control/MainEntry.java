package control;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import socket.CtrolSocketServer;
import socket.Message;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：24 Dec 2014 14:11:29 
 */

public class MainEntry {
	

	
	/*** 
	 * 整个程序包的入口
	 * @param  args[0]: local port which deviceControl program listen at, like 64415
	 * 
	 * @throws InterruptedException  从接收到的消息队列poll出一个消息时，可能会发生异常
	 * */
	public static void main(String[] args) throws InterruptedException {
		
		Config cf = new Config();		
		CtrolSocketServer server=new CtrolSocketServer(Integer.parseInt(cf.getValue("server_port")));
		if(!CtrolSocketServer.receiveCommandQueue.isEmpty()){
			Message msg;

				msg = CtrolSocketServer.receiveCommandQueue.poll(10, TimeUnit.MICROSECONDS);
				if(msg!=null){
					
				}
		}
		
		
		
		
		
		


	}

}
