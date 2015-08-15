package cooxm.devicecontrol.socket;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;



public class SocketClient /*extends Socket*/ implements Runnable {

	private static final short ACK_OFFSET = 0x4000;
	private static final short CMDCODE_RESERVED_FOR_COMMON_BEGIN 		= 0x1100;
	static final short CMD__Identity_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 1;  //身份标识请求
	static final short CMD__Identity_ACK 		= CMD__Identity_REQ  + ACK_OFFSET;  //身份标识请求
	static final short CMD__HEARTBEAT_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 2;  //心跳请求
	static final short CMD__HEARTBEAT_ACK 		= CMD__HEARTBEAT_REQ  + ACK_OFFSET;  //心跳请求
		
    
    public static Logger log = Logger.getLogger(SocketClient.class);  
	/**用户连接的通信套接字*/  
    public Socket sock=null;
    BufferedReader input;
    String IP;
    int port;
	int clusterID;
	int targetServerID;
	int serverID;
	int serverType;
	boolean actFlag; //除了重连是否还需要做其他事情
	boolean initFlag;
	boolean sendHeartBeatFlag;
	
	/**@actFlag 用来标记这个socket连接成功后是否需要readFromClient()读取数据等操作； true：需要
	 *  */
	public SocketClient(String IP,int port,int clusterID,	int targetServerID,int myserverID,	int serverType,boolean actFlag,boolean sendHeartBeatFlag) 
    {   //super(IP,port);
		this.IP=IP;
        this.port=port;
		this.clusterID=clusterID;
		this.serverID=myserverID;
		this.targetServerID=targetServerID;
		
		this.serverType=serverType;	
		this.actFlag=actFlag;
		this.initFlag=false;
		this.sendHeartBeatFlag=sendHeartBeatFlag;
		
    } 
	
	public void sendAuth(int clusterID,int  serverID ,int serverType) throws JSONException, IOException{					
		Header header=Message.getOneHeaer((short)CMD__Identity_REQ);
		String jsonStr="{\"uiClusterID\":"+clusterID+",\"usServerType\":"+serverType+",\"uiServerID\":"+serverID+"}";
		String cookie=System.currentTimeMillis()/1000+"_"+serverID;
		Message authMsg=null;
		authMsg = new Message(header, cookie, jsonStr);
		authMsg.writeBytesToSock2(this.sock);
		log.info("Send Auth "+this.sock.getInetAddress().getHostAddress()+":"+this.sock.getPort()+":"+authMsg.toString());	
		
		 Message msg=CtrolSocketServer.readFromClient(sock);
		 if(msg!=null){
			int errorCode= msg.getJson().optInt("errorCode");
			if(errorCode==0){
				CtrolSocketServer.sockMap.put(targetServerID,sock);
				CtrolSocketServer.printSocketMap();			
				log.info("succefull connect to  server,IP:"+this.IP+" port: "+this.port);
			}else{
				log.info("Auth failed to:"+this.IP+" port: "+this.port+"by auth info:"+jsonStr);
			}
		 }
	}
	
	
	public static void sendAuth(Socket sock,int clusterID,int  serverID ,int serverType) throws JSONException, IOException{					
		Header header=Message.getOneHeaer((short)CMD__Identity_REQ);
		String jsonStr="{\"uiClusterID\":"+clusterID+",\"usServerType\":"+serverType+",\"uiServerID\":"+serverID+"}";
		String cookie=System.currentTimeMillis()/1000+"_"+serverID;
		Message authMsg=null;

			authMsg = new Message(header, cookie, jsonStr);

		authMsg.writeBytesToSock2(sock);
		System.out.println("Send Auth "+sock.getRemoteSocketAddress().toString()+":"+authMsg.toString());	
		
		 Message msg=CtrolSocketServer.readFromClient(sock);
		 if(msg!=null){
			int errorCode= msg.getJson().optInt("errorCode");
			if(errorCode==0){
//				CtrolSocketServer.sockMap.put(targetServerID,sock);
//				CtrolSocketServer.printSocketMap();
				log.info("succefull connect to   server,IP:"+sock.getRemoteSocketAddress().toString());
			}else{
				log.info("Auth failed to:"+sock.getRemoteSocketAddress().toString()+"by auth info:"+jsonStr);
			}
		 }
	}


	
	public static String getLocalIP(){
		InetAddress addr;
		String ip=null;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress().toString();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
	

	@Override
	public void run() {
        while(true){        	
	    	try {
				if(this.sock==null ||this.sock.isClosed()|| !this.sock.isConnected()){
					this.sock=new Socket(IP,port);
					sendAuth(this.clusterID, this.serverID, this.serverType); 
					if(sendHeartBeatFlag==true){
						heartBeat();
					}
				}  
				Thread.sleep(100);
				if (actFlag==true) {   //做一些事情
			  	   Message msg=CtrolSocketServer.readFromClient(this.sock);
			  	   if(msg!=null){
						decodeMsg(msg);
			  	   }else{
						log.info("socket closed:"+this.sock.getRemoteSocketAddress());
						this.sock.close();
						this.sock=null;
						Thread.sleep(5*1000);
			  	   }
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				log.info("socket closed:waiting for 5 seconds .Reconnect to  message server,IP:"+this.IP+" port: "+this.port);  
					this.sock=null ;
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
   }
	
	
	public  void decodeMsg(Message msg) throws JSONException, IOException{
		short commandID=msg.commandID;
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		switch (commandID) {
		case CMD__Identity_ACK:	
			int ack_res=-1;
			try {
				ack_res = msg.getJson().getInt("errorCode");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if(ack_res==0){
				CtrolSocketServer.sockMap.put(targetServerID,sock);
				CtrolSocketServer.printSocketMap();
				//System.out.println("Recv from "+sock.getInetAddress().getHostAddress()+":"+sock.getPort()+":"+msg.msgToString());
			}else{
				log.error("Get authenrize failed: myIP:"+this.sock.getLocalSocketAddress().toString()
						+",remoteIP:"+this.IP+",remotePort:"+this.port
						+"Auth:"
						+"{\"uiClusterID\":"+this.clusterID+",\"usServerType\":"+serverType+",\"uiServerID\":"+serverID+"}");
				sendAuth(clusterID, commandID, ack_res);
			}			
			break;
		case CMD__HEARTBEAT_REQ:
			
			try {
				msg.getJson().put("uiTime", sdf.format(new Date()));
				msg.msgLen=msg.getMsgLength();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			msg.commandID=CMD__HEARTBEAT_ACK;
			msg.writeBytesToSock2(this.sock);
			System.out.println("HeartBeat "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
			break;
		case CMD__HEARTBEAT_ACK:

			System.out.println("Heart ACK "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
			break;
		default:
			if(msg.getJson().has("errorCode")){
				int errorCode=msg.getJson().getInt("errorCode");
				if(errorCode==0){
					log.info("Success: "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
				}else{
					log.info("Faild : "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
				}
			}else{
				log.info("Recv frm "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
			}
			
			break;
		}		
	}
	
	/**每分钟发送一次心跳，无回复则关闭socket */
	private void heartBeat() throws IOException{
        // 启动心跳线程  
        Timer heartBeatTimer = new Timer();  
        TimerTask heartBeatTask = new TimerTask() {    
	        @Override  
	        public void run() {  
	            try {
					sendOrder();
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}  
	        }  
        };  
        heartBeatTimer.schedule(heartBeatTask, 1*60*1000, 5*60*1000); 		
	}
	
	private void sendOrder() throws JSONException, IOException{  
		long time=new Date().getTime()/1000;
    	JSONObject json=new JSONObject();
    	json.put("uiTime", time);
    	String cookie=new Date().getTime()+"_"+this.serverID;
    	Message msg=new Message(CMD__HEARTBEAT_REQ, cookie, json);
        msg.writeBytesToSock2(sock); 	
        log.info("HeartBeat "+this.sock.getRemoteSocketAddress()+":"+msg.toString());
		try {
			this.sock.setSoTimeout(10000);
			Message msgAck=CtrolSocketServer.readFromClient(this.sock);
			log.info("Heart ACK "+this.sock.getRemoteSocketAddress()+":"+msgAck.toString());
			this.sock.setSoTimeout(0);
		} catch (Exception e) {
			log.info("Receive heartBeat timeOut, socket "+this.sock.getRemoteSocketAddress()+" closed, reconnecting ...");
			this.sock.close();
			this.sock=null;

		}

		
	}  
	
	
	public void start(SocketClient msgSock){
	    while (true) {
			if(this==null ||this.sock.isClosed()){

			}else{
				new Thread(msgSock).start();
			}
		} 
	}
	
   
    public static void main(String [] args) throws UnknownHostException, IOException, InterruptedException       
    {  
		SocketClient msgSock= new SocketClient("120.24.81.23", 20190,1,6,5,200,false,true);
		if(msgSock!=null){
	    	//msgSock.sendAuth(1,5,200);
			new Thread(msgSock).start();


		}
		
		//Message msg1=new Message(commandID, cookie, json)

//		Message msg=CtrolSocketServer.readFromClient(msgSock.sock);
//		if(msg!=null)
//		System.out.println(msg.toString());
//       Message msg2=Message.getOneMsg();
//       msg2.writeBytesToSock2(msgSock.sock);
//       
//       Thread.sleep(20000);
//    	
//    	Socket sock =new Socket("172.16.35.173", 20190);
//      Message msg3=Message.getOneMsg();
//      msg3.writeBytesToSock2(msgSock.sock);
//      
//      Thread.sleep(20000);
      
      

      
    	

   	
    }


}
