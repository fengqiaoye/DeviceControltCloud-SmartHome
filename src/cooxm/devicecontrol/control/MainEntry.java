package cooxm.devicecontrol.control;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：24 Dec 2014 14:11:29 
 */
import java.io.IOException;
import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.socket.CtrolSocketServer;



public class MainEntry {
	
	static Logger log =Logger.getLogger(MainEntry.class);	
	private static Configure cf =new Configure();
	/***  
	 * 整个程序包的入口
	 * @param  Configure 配置文件
	 * 
	 * @throws InterruptedException  从接收到的消息队列poll出一个消息时，可能会发生异常
	 * */
	public static void main(String[] args)  {
		log.info("Starting from main entry...");		

		try {
			new CtrolSocketServer(cf).listen();
		} catch (IOException e) {
			log.error(e);	
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getMessage());	
			e.printStackTrace();
		}

	}
	
	public static Configure getConfig() {
		return cf;		
	}
}
