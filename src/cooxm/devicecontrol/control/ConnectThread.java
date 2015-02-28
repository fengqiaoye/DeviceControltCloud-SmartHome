package cooxm.devicecontrol.control;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.socket.SocketClient;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 12, 2015 5:32:19 PM 
 */

public class ConnectThread extends Thread{
	static Logger log= Logger.getLogger(ConnectThread.class);
	SocketClient client;
	String IP ;
	int port ;
	int clusterID;
	int serverID;
	int serverType;
	
	
	ConnectThread(	String IP ,	int port ,	int clusterID,	int serverID,	int serverType){
		this.IP=IP;
		this.port=port;	
		this.clusterID=clusterID;
		this.serverID=serverID;
		this.serverType=serverType;		
	}
	
	@Override
	public void run(){
		while (true){
			if(this.client==null){
				try {
					this.client=new SocketClient(IP, port,clusterID,serverID,serverType);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					try {
						Thread.sleep(60*1000);
						System.out.println("Failiar:connect to "+IP+":"+port+" failed ,Waiting for 60 seconds to reconnect...");
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}else{				
				this.client.sendAuth(this.clusterID,this.serverID,this.serverType);
				new Thread((Runnable) this.client).start();
				log.info("successful connected to :"+IP+":"+port+",serverID:"+this.serverID+",serverType:"+this.serverType);
				break;
			}
		}
	}
	
    public static void main(String [] args)  {
    	ConnectThread th=new ConnectThread("172.16.35.173",20190,1,5,200);
    	th.start();   	
    	
    	
    }

}
