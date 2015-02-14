package cooxm.devicecontrol.control;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：24 Dec 2014 14:11:29 
 */
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;


public class MainEntry {
	
	static Logger log =Logger.getLogger(MainEntry.class);	
	private static Configure cf ;
	/*** 
	 * 整个程序包的入口
	 * @param  Configure 配置文件
	 * 
	 * @throws InterruptedException  从接收到的消息队列poll出一个消息时，可能会发生异常
	 * */
	public static void main(String[] args)  {
		log.info("Starting from main entry...");		
		cf = new Configure();	
		LogicControl lcontrol=new LogicControl(cf);

			try {
				new CtrolSocketServer(cf).listen();
			} catch (IOException e) {
				log.error(e);				
			} catch (Exception e) {
				log.error(e);	
			}

		if(!CtrolSocketServer.receiveCommandQueue.isEmpty()){
			Message msg;
			try {
				msg = CtrolSocketServer.receiveCommandQueue.poll(200, TimeUnit.MICROSECONDS);
				if(msg!=null){
					lcontrol.decodeCommand(msg);					
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}
	
	public static Configure getConfig() {
		return cf;		
	}
}
