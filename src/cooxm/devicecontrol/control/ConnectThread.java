package cooxm.devicecontrol.control;

import java.io.IOException;
import java.net.UnknownHostException;

import cooxm.devicecontrol.socket.SocketClient;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 12, 2015 5:32:19 PM 
 */

public class ConnectThread extends Thread{
	SocketClient client;
	String IP ;
	int port ;
	
	
	ConnectThread(	String IP ,	int port ){
		this.IP=IP;
		this.port=port;	
		
	}
	
	@Override
	public void run(){
		while (true){
			if(this.client==null){
				try {
					this.client=new SocketClient(IP, port);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					try {
						Thread.sleep(30*1000);
						try {
							this.client=new SocketClient(IP, port);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}else{				
				this.client.sendAuth(201, 6);
				new Thread((Runnable) this.client).start();
				break;
			}
		}
	}
	
    public static void main(String [] args)  {
    	ConnectThread th=new ConnectThread("172.16.35.173",10790);
    	th.start();
    	
    	
    	
    }

}
