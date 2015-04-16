package cooxm.devicecontrol.socket;

import java.util.concurrent.TimeUnit;

import cooxm.devicecontrol.control.LogicControl;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 14, 2015 12:00:03 PM 
 */

public class ProcessThread extends Thread
{
	LogicControl lcontrol;
	public ProcessThread(LogicControl lcontrol){
		this.lcontrol=lcontrol;
	}
	
	public void run()
	{
		while(true){
			Message msg;
			try {
				Thread.sleep(10);
				msg = CtrolSocketServer.receiveCommandQueue.poll(200, TimeUnit.MICROSECONDS);
				if(msg!=null){
					lcontrol.decodeCommand(msg);					
				}
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}
	
	
	
}
