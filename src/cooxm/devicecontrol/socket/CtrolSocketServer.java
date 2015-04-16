package cooxm.devicecontrol.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.util.BytesUtil;
import cooxm.devicecontrol.util.MySqlClass;

public class CtrolSocketServer {
	
	private static final short ACK_OFFSET = 0x4000;
	private static final short CMDCODE_RESERVED_FOR_COMMON_BEGIN 		= 0x1100;
	private static final short CMD__Identity_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 1;  //身份标识请求
	private static final short CMD__Identity_ACK 		= CMD__Identity_REQ  + ACK_OFFSET;  //身份标识请求
	private static final short CMD__HEARTBEAT_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 2;  //心跳请求
	private static final short CMD__HEARTBEAT_ACK 		= CMD__HEARTBEAT_REQ  + ACK_OFFSET;  //心跳请求
	/** 服务器没有通过认证*/
	public static final int SERVER_NOT_AUTHORIZED	  = -50041;

	Configure configure=null;
    ServerSocket severSock = null;
    //Socket sock =null;
	static OutputStream output = null;
	
    /** Map   < serverID,Socket > */
	public static Map<Integer,Socket> sockMap= new HashMap<Integer,Socket>();
	
    /*** Map   < serverID,server >     */
	public static Map<Integer,Server> severMap=new HashMap<Integer,Server>();
	
	public static ReceiveCommandQueue receiveCommandQueue;
	public static SendCommandQueue sendCommandQueue;
	public LogicControl lcontrol;
    
	static Logger log =Logger.getLogger(CtrolSocketServer.class);	
	
	/***@param serverPort: 从配置文件中读取: ./conf/control.conf */
	public CtrolSocketServer(Configure configure, LogicControl lcontrol ) {
		log.info("starting device control socket server...");		

		sendCommandQueue=SendCommandQueue.getInstance();
		receiveCommandQueue=ReceiveCommandQueue.getInstance();
        this.configure=configure;
        this.lcontrol=lcontrol;
	}
	
	/** 做服务器时使用*/
	public void listen() throws IOException, Exception  
	{
		int serverPort=Integer.parseInt(configure.getValue("server_port"));
        try{
        	severSock= new ServerSocket(serverPort);
        }
        catch(IOException e)
        {
         	log.error(e);
            System.exit(1);
        }
        while(true)
        {
        	log.info(" Listening at port:"+severSock.getLocalPort()+"...");
        	Socket sock = severSock.accept();
        	 InetAddress clientAdress=sock.getInetAddress();
        	 int count=sockMap.size()+1;
        	log.info(" Accept the "+ count +"th connection from client:"+
        	 clientAdress.getHostAddress()+"("+clientAdress.getHostName()+")"+":"+sock.getPort());  


            ReadThread rt = new ReadThread(sock);    
       		rt.start(); 
       		WriteThread wr=new  WriteThread(sock);
       		wr.start();
       		
       		ProcessThread pt= new  ProcessThread();
       		pt.start();
        }
	}
	
	/** client使用*/
	public void connectTo(String IP, int port,int clusterID,	int serverID,	int serverType){
		
		Socket sock=null;
		try {
			sock = new Socket(IP, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(sock!=null){	
            ReadThread rt = new ReadThread(sock);    
       		rt.start(); 
       		WriteThread wr= new  WriteThread(sock);
       		wr.start();
       		sendAuth(sock,clusterID,serverID,serverType);
       		rt.serverID=serverID;   		

		}		
	}
	
	public class ReadThread extends Thread
	{
		private Socket socket;
		private int serverID=-1;
		
		public ReadThread(Socket client)
		{socket = client;}
		
		public void run()
		{
	        while (!this.socket.isClosed())  
	        { 
	          Message  msg= readFromClient(socket);	          
	            if(msg!=null){
	            	short commandID=msg.getCommandID();
	            	msg.setServerID(this.serverID);
	            	switch (commandID) {
					case CMD__Identity_REQ: //0x1101:
						int serverID=authenrize(msg,this.socket);
						if(serverID>0){
							this.serverID=serverID;
						}
						break;
					case CMD__HEARTBEAT_REQ:  //0x1102
						replyHeartBeat(msg);
						break;
					case CMD__Identity_ACK:	
						int ack_res=-1;
						try {
							ack_res = msg.getJson().getInt("errorCode");
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						if(ack_res==0){
							if(this.serverID>0){
							Server server= getServerInfo(this.serverID);
							severMap.put(this.serverID, server);
							sockMap.put(this.serverID, socket);
							}
						}else{
							log.error("Get authenrize failed: myIP:"+socket.getLocalAddress().getHostAddress()+",myPort:"+socket.getLocalPort()
									+",remoteIP:"+socket.getInetAddress().getHostAddress()+",remotePort:"+socket.getPort());
						}			
						break;
					default:
			            try {
			            	if(msg.isValid()){
			            		boolean falg=CtrolSocketServer.receiveCommandQueue.offer(msg,500, TimeUnit.MICROSECONDS);
			            		System.out.println("size of receiveCommandQueue ="+CtrolSocketServer.receiveCommandQueue.size());
			            	}else{
			            		return;
			            	}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
	            }else{   //socket关闭
	            	
	            	
	            	try {
	            		if(serverID>0){
							log.info("socket hase been closed :"+this.socket.getInetAddress().getHostAddress()+":"+this.socket.getPort()
									+",serverID: "+serverID+",serverType: "+severMap.get(serverID).getServerType());
							sockMap.remove(this.serverID);
							severMap.remove(this.serverID);	
	            		}
						this.socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }	
		}
	}
	
	public class WriteThread extends Thread
	{
		private Socket sock;
		
		public WriteThread(Socket client)
		{sock = client;}
	
		public void run()
		{
	    	while(!this.sock.isClosed())
	    	{
	    		try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
	    		if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
		    		Message outMsg=null;
					try {
						outMsg=CtrolSocketServer.sendCommandQueue.peek();
						if(null!=outMsg){
							int serverID=outMsg.getServerID();	
							 if(serverID>0 && this.sock.equals(sockMap.get(serverID)) ){	
								outMsg = CtrolSocketServer.sendCommandQueue.take();//poll(100, TimeUnit.MICROSECONDS);
								outMsg.writeBytesToSock2(this.sock);
								log.info("Send to  "+sock.getInetAddress().getHostAddress()+":"+sock.getPort()+":"+outMsg.msgToString());	
								System.out.println("size of sendCommandQueue ="+CtrolSocketServer.sendCommandQueue.size());
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 				
		    	}
	    	}
		}
	} 
	
	public class ProcessThread extends Thread
	{
		
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
					log.error(e);
				}
			}
		}
	}
	
	public void sendAuth(Socket sock,int clusterID,int  serverID ,int serverType){
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}					
		Header header=Message.getOneHeaer((short)CMD__Identity_REQ);
		String jsonStr="{\"uiClusterID\":"+clusterID+",\"usServerType\":"+serverType+",\"uiServerID\":"+serverID+"}";
		Message authMsg=new Message(header, "", jsonStr);
		authMsg.writeBytesToSock2(sock);
		log.info("Send Auth "+sock.getInetAddress().getHostAddress()+":"+sock.getPort()+":"+authMsg.msgToString());		
	}
	
	public int getServerID(Message msg) {
		String cookie=msg.getCookie();
		if(null!=cookie && cookie.split("_").length==2 ){
			return Integer.parseInt(cookie.split("_")[0]);
		}else if(msg.getJson().has("uiServerID")){
			try {
				return msg.getJson().getInt("serverID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			return -1;	
	}
	
	public int getServerID(Socket sock) {
		for (Entry<Integer, Socket> entry:sockMap.entrySet()) {
			if(entry.getValue().equals(sock)){
				return entry.getKey();
			}			
		}
		return -1;	
	}
	
	/**回复心跳 */
	public void replyHeartBeat(Message heartBeatMsg){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Message replyMsg=new Message(heartBeatMsg);
		replyMsg.setCommandID((short) (heartBeatMsg.getCommandID()+Message.COMMAND_ACK_OFFSET));
		JSONObject json=new JSONObject();
		try {
			json.put("uiTime", sdf.format(new Date()));
			replyMsg.setJson(json);
			Server server=severMap.get(replyMsg.serverID);
			Socket sock=sockMap.get(replyMsg.serverID);
			if(sock==null ||  server==null){
				log.error("can't find sock from sockMap by serverID:"+replyMsg.serverID+",this message will be disposed.");
				return;
			}

			boolean flag=CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
			System.out.println("HeartBeat "+sock.getInetAddress().getHostAddress()+":"+sock.getPort()
					+",serverID: "+heartBeatMsg.serverID+",serverType: "+server.getServerType()+":"+replyMsg.msgToString());
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	/** 返回serverID*/	
	public int authenrize(Message msg,Socket sock){
		int serverID=-1;
		try {
			
			if(msg.getJson().has("uiServerID")){
				serverID= msg.getJson().getInt("uiServerID");
			}
			if(serverID<0){
				JSONObject json=new JSONObject();
				json.put("errorCode",-1);
				Message replyMsg=new Message(msg);
				replyMsg.setCommandID((short) (msg.getCommandID()+Message.COMMAND_ACK_OFFSET));
				replyMsg.setJson(json);

				CtrolSocketServer.sendCommandQueue.offer(msg,500, TimeUnit.MICROSECONDS);
				log.info("anth faild: "+replyMsg.toString());
				Thread.sleep(5000);
				sock.close();
				return serverID;
			}
			Server server=getServerInfo(serverID);			
			int serverType=msg.getJson().getInt("usServerType");	
			String ss=sock.getInetAddress().getHostAddress();
			if(server!=null && server.getServerType()==serverType ){
				severMap.put(serverID, server);				
				sockMap.put(serverID, sock);
				
				JSONObject json=new JSONObject();
				json.put("errorCode", 0);
				Message replyMsg=new Message(msg);
				replyMsg.setCommandID((short) (msg.getCommandID()+Message.COMMAND_ACK_OFFSET));
				replyMsg.setJson(json);
				replyMsg.setServerID(serverID);
				boolean flag=CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
				System.out.println("Send AuRs "+sock.getInetAddress().getHostAddress()+":"+sock.getPort()
						+",serverID: "+replyMsg.serverID+",serverType: "+server.getServerType()+":"+replyMsg.msgToString());
				return serverID;
			}else{                                //鉴权失败

			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serverID; 	
	}      
		
	public /*synchronized*/  Server getServerInfo(int serverID){
		severMap=new HashMap<Integer, Server>();
		String mysql_ip			=this.configure.getValue("mysql_ip");
		String mysql_port		=this.configure.getValue("mysql_port");
		String mysql_user		=this.configure.getValue("mysql_user");
		String mysql_password	=this.configure.getValue("mysql_password");
		String mysql_database	=this.configure.getValue("mysql_database_main");
		
		MySqlClass mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		
		String sql="select  "
				+" serverid  ,"				
				+"serverip ,"
				+"serverport ,"
				+"servertype ,"
				+"clusterid "
				+ "  from "				
				+"info_server"
				+" where serverid= "
				+serverID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		if(res==null ) {
			log.error("ERROR:exception happened: "+sql);
			return null;
		}else if(res=="") {
			log.error("ERROR:query result is empty: "+sql);
			return null;
		}
		//System.err.println(res);System.err.println(res);
		String[]  cells=res.split(",");
		Server server=new Server(cells[1], Integer.parseInt(cells[2]), Integer.parseInt(cells[3]), Integer.parseInt(cells[4]));
		server.setLastHeartBeatTime(new Date());
		mysql.close();	
		return server;

	}	
	
	public static Message readFromClient(Socket clientRequest) 
    {  
		if(clientRequest==null || clientRequest.isClosed()){
			return null;
		}
		byte[] b23=new byte[23]; 
		Header head=new Header();
		Message msg=new Message();
		int offset=0;
    	try {
    		while(offset<23){
    			int len = clientRequest.getInputStream().read(b23,offset,23-offset);
    			if(len<0){
    				return null;
    			}else{
    				offset=offset+len;
    			}    			
    		}
    		head=new Header(b23);
			//head.printHeader();
		} catch (IOException e) {
			log.error("IOException socket:"+clientRequest.getInetAddress().getHostAddress()+" , socket will be closed.");
			e.printStackTrace();
			return null;			
		}    	
    	
    	byte[] cookie=new byte[head.cookieLen];
    	try {
			clientRequest.getInputStream().read(cookie,0,head.cookieLen);
		} catch (IOException e) {			
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
		}
    	String cookieStr=new String(cookie);
    	//System.out.println(" cookie: "+cookieStr);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
		}  
    	String comString=new String(commnad);
    	if(comString.length()==0){
    		return null;
    	}else{
    		msg=new Message(head, cookieStr, comString);
    	}
    	if(msg.isAuth() && msg.commandID!=4354){
    		log.info(" Recv frm "+clientRequest.getInetAddress().getHostAddress()+":"+clientRequest.getPort()+":"+msg.msgToString());
    	}
        return msg; 
    } 
	


    public static void main(String [] args) throws IOException, Exception      
    {  
    	Configure cf = new Configure();
    	//new CtrolSocketServer(cf).listen();
		//new CtrolSocketServer(cf).connectTo("172.16.35.173", 20190,1,5,200);
    	//initseverMap(cf);    	
    }    
}
