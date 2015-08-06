package cooxm.devicecontrol.control;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import cooxm.devicecontrol.socket.SocketClient;



/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 12, 2015 5:32:19 PM 
 */

public class ConnectThread extends Thread{
	static Logger log= Logger.getLogger(ConnectThread.class);
	public SocketClient client;
	String IP ;
	int port ;
	int clusterID;
	int serverID;
	int serverType;
	boolean actFlag;
	
	
	public ConnectThread(	String IP ,	int port ,	int clusterID,	int serverID,	int serverType,boolean actFlag){
		this.IP=IP;
		this.port=port;	
		this.clusterID=clusterID;
		this.serverID=serverID;
		this.serverType=serverType;		
		this.actFlag=actFlag;
	}
	
	@Override
	public void run(){
		while (true){
			if(this.client==null){
				this.client=new SocketClient(IP, port,clusterID,serverID,serverType,false,false);
			}else{				
				try {
					this.client.sendAuth(this.clusterID,this.serverID,this.serverType);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					this.client=null;
				}
				new Thread((Runnable) this.client).start();
				log.info("successful connected to :"+IP+":"+port+",serverID:"+this.serverID+",serverType:"+this.serverType);
				break;
			}
		}
	}
	
    public static void main(String [] args)  {
    	ConnectThread th=new ConnectThread("172.16.35.16",20190,1,5,200,false);
    	th.start();   	
    	
    	System.out.println("11");
    	
    	
    }

}
