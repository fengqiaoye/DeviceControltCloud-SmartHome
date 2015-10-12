package cooxm.devicecontrol.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.JsonValidator;
import cooxm.devicecontrol.util.MySqlClass;

public class CtrolSocketServer {
	static Logger log =Logger.getLogger(CtrolSocketServer.class);
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
	private SocketClient msgSock;
	MySqlClass mysql_main;
	
	private static final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    
	
	
	/***@param serverPort: 从配置文件中读取: ./conf/control.conf */
	public CtrolSocketServer(Configure configure /*, LogicControl lcontrol */) {
		log.info("starting device control socket server...");	
		sendCommandQueue=SendCommandQueue.getInstance();
		receiveCommandQueue=ReceiveCommandQueue.getInstance();
        this.configure=configure;
        this.lcontrol=new LogicControl(configure);
        
		String mysql_ip			=this.configure.getValue("mysql_ip");
		String mysql_port		=this.configure.getValue("mysql_port");
		String mysql_user		=this.configure.getValue("mysql_user");
		String mysql_password	=this.configure.getValue("mysql_password");
		String mysql_database	=this.configure.getValue("mysql_database_main");		
		mysql_main=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		log.info("device control socket server started, finish.");	
	}
	
	/** 做服务器时使用*/
	public void listen() throws IOException, Exception  
	{
		int serverPort=Integer.parseInt(configure.getValue("server_port"));
		int cluster_id          =Integer.parseInt(configure.getValue("cluster_id"));
		int server_id           =Integer.parseInt(configure.getValue("server_id"));
        try{
        	severSock= new ServerSocket(serverPort);
        }
        catch(IOException e)
        {
         	log.error(e);
            System.exit(1);
        }
        	
        String msg_server_IP=configure.getValue("msg_server_IP"); //configure.getProperty("msg_server_IP", "172.16.35.173");
        int msg_server_port =Integer.parseInt(configure.getValue("msg_server_port")); 
        log.info("Connecting to msg server,IP:"+msg_server_IP+":"+msg_server_port);
        this.msgSock=new SocketClient(msg_server_IP, msg_server_port, cluster_id,4, server_id, 201,false,false);  
        Thread connectMsgServerThread = new Thread((Runnable) this.msgSock);
        connectMsgServerThread.setName("connectMsgServer");
        connectMsgServerThread.start();   
        
        Thread.sleep(200);
    	if(this.msgSock.sock!=null){
        	sockMap.put(4, this.msgSock.sock);
        	Server server= getServerInfo(4);
        	severMap.put(4, server);
    	}

        ReadThread msgRt = new ReadThread(this.msgSock.sock);   
        msgRt.serverID=4;
        msgRt.setName("mesgServer_ReadThread");
        msgRt.start(); 
        
   		WriteThread msgWr=new  WriteThread(this.msgSock.sock);
   		msgWr.setName("mesgServer_WritThread");
   		msgWr.start(); 
   		
   		ProcessThread pt= new  ProcessThread();
   		pt.setName("Main____ProcessThread");
   		pt.start();
    	
        while(true)
        {
        	log.info(" Listening at port:"+severSock.getLocalPort()+"...");
        	Socket sock = severSock.accept(); 	
        	SocketAddress remoteSock = sock.getRemoteSocketAddress();
        	int count=sockMap.size()+1;
        	log.info(" Accept the "+ count +"th connection from client:"+remoteSock.toString());

            ReadThread rt = new ReadThread(sock); 
            rt.setName(remoteSock.toString().replace("/", "")+"_Rd");
       		rt.start(); 
       		log.info(" Create ReadThread for socket:"+remoteSock.toString()
       				 +",threadID="+rt.getId()+",threadName="+rt.getName()+",state="+rt.getState()); 
       		
       		Thread.sleep(500); 
       		
       		WriteThread wr=new  WriteThread(sock);
       		wr.setName(remoteSock.toString().replace("/", "")+"_Wt");
       		wr.start();
       		log.info(" Create WriteThread for socket:"+remoteSock.toString()
      				 +",threadID="+wr.getId()+",threadName="+wr.getName()+",state="+wr.getState());
       		
//       		ProcessThread pt2= new  ProcessThread();
//       		pt2.start();       		
        }
	}
	
	/** client使用*/
	public void connectTo(String IP, int port,int clusterID,	int serverID,	int serverType){
		
		Socket sock=null;
		try {
			sock = new Socket(IP, port);

			if(sock!=null){	
	            ReadThread rt = new ReadThread(sock);    
	       		rt.start(); 
	       		WriteThread wr= new  WriteThread(sock);
	       		wr.start();
	       		sendAuth(sock,clusterID,serverID,serverType);
	       		rt.serverID=serverID;   		
	
			}	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			sock=null;
			e.printStackTrace();
		}
	}
	
	public class ReadThread extends Thread {

		private Socket clientSocket;

		private int serverID = -1;

		public ReadThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public void run() {
			ExecutorService executorService = Executors.newSingleThreadExecutor();

			while (null != clientSocket && !clientSocket.isClosed()) {
				Message msg = null;
				if (!messageQueue.isEmpty()) {
					msg = messageQueue.poll();
				} else {
					msg = readFromClient(clientSocket);
				}

				try {
					if (clientSocket.getInputStream().available() > 23) {
						executorService.submit(new Runnable() {
							@Override
							public void run() {
								try {
									while (clientSocket.getInputStream().available() > 23) {
										Message msg = readFromClient(clientSocket);
										if (null != msg) {
											messageQueue.add(msg);
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
						});
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (msg != null) {
					short commandID = msg.getCommandID();
					msg.setServerID(serverID);
					switch (commandID) {
					case CMD__Identity_REQ: // 0x1101:
						int authServerID = authenrize(msg, clientSocket);
						if (authServerID > 0) {
							serverID = authServerID;
						}
						break;
					case CMD__HEARTBEAT_REQ: // 0x1102
						replyHeartBeat(msg);
						break;
					case CMD__Identity_ACK:
						int ack_res = -1;
						try {
							ack_res = msg.getJson().getInt("errorCode");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (ack_res == 0 && serverID > 0) {
							if (sockMap.containsKey(serverID)) {
								Socket oldClientSocket = sockMap.get(serverID);
								log.error("Accept a new socket , the same serverID = " + serverID + " from "
										+ clientSocket.getRemoteSocketAddress() + " , the old socket will be closed : "
										+ oldClientSocket.getRemoteSocketAddress());
								try {
									oldClientSocket.close();
									sockMap.remove(serverID);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// replace the old client socket
								sockMap.put(serverID, clientSocket);
							}
							Server server = getServerInfo(serverID);
							severMap.put(serverID, server);
						} else {
							log.error("Get authenrize failed: myIP:" + clientSocket.getLocalSocketAddress()
									+ ",remoteIP:" + clientSocket.getRemoteSocketAddress().toString());
						}
						break;
					default:
						if (!sockMap.containsValue(clientSocket)) {
							log.info("Socket hase been removed from SockMap : "
									+ clientSocket.getRemoteSocketAddress().toString() + " , rverID : " + serverID);
							sockMap.remove(serverID, clientSocket);
							try {
								if (!clientSocket.isClosed()) {
									clientSocket.close();
								}
								log.info("Socket has been closed : " + clientSocket.getRemoteSocketAddress().toString()
										+ " , serverID : " + serverID);
							} catch (IOException e) {
								e.printStackTrace();
							}
							executorService.shutdown();
							return; // 线程终止
						}
						try {
							if (msg.isValid()) {
								receiveCommandQueue.offer(msg, 200, TimeUnit.MICROSECONDS);
								if (CMD__Identity_REQ == msg.getCommandID()) {

								}
							} else {
								return;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
				} else {
					try {
						log.info("socket has been closed :" + clientSocket.getRemoteSocketAddress().toString()
								+ ",serverID: " + serverID);
						boolean result = sockMap.remove(serverID, clientSocket);
						severMap.remove(serverID);
						if (true == result) {
							log.info("Socket has been removed from SockMap : "
									+ clientSocket.getRemoteSocketAddress().toString() + " , serverID : " + serverID);
						}
						if (!clientSocket.isClosed()) {
							clientSocket.close();
						}
						executorService.shutdown();
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			executorService.shutdown();
		}
	}
	
/*	public class ReadThread extends Thread
	{
		private Socket socket;
		private int serverID=-1;
		
		public ReadThread(Socket client)
		{socket = client;
		}
		
		public void run()
		{
			if(this.socket==null){
				return;  //线程终止
			}
	        while (this.socket!=null && !this.socket.isClosed())  
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
						if(ack_res==0 && this.serverID>0){
							if(sockMap.containsKey(this.serverID)){
								Socket oldSock=sockMap.get(this.serverID);
								log.error("Accept a new socket, the same serverID="+this.serverID+"from "+socket.getRemoteSocketAddress()
										+",the old socket will be closed:"+oldSock.getRemoteSocketAddress());
								try {
									oldSock.close();
									oldSock=null;
									sockMap.remove(this.serverID);
								} catch (IOException e) {
									e.printStackTrace();
								}
								sockMap.put(this.serverID, socket);								
							}							
							Server server= getServerInfo(this.serverID);							
							severMap.put(this.serverID, server);


						}else{
							log.error("Get authenrize failed: myIP:"+socket.getLocalSocketAddress()
									+",remoteIP:"+socket.getRemoteSocketAddress().toString());
						}			
						break;
					default:
						if(!sockMap.containsValue(this.socket)){
							log.info("socket hase been removed from SockMap :"+this.socket.getRemoteSocketAddress().toString()+",serverID: "+this.serverID);
							sockMap.remove(this.serverID, this.socket);
							try {
								this.socket.getInputStream().close();
								this.socket.close();
								this.socket=null;
								log.info("socket hase been closed :"+this.socket.getRemoteSocketAddress().toString()+",serverID: "+this.serverID);
							} catch (IOException e) {
								e.printStackTrace();
							}
							return;  //线程终止
						}
			            try {
			            	if(msg.isValid()){
			            		boolean falg=CtrolSocketServer.receiveCommandQueue.offer(msg,200, TimeUnit.MICROSECONDS);
			            		//System.out.println("size of receiveCommandQueue ="+CtrolSocketServer.receiveCommandQueue.size());
			            	}else{
			            		return;
			            	}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
	            }else{   //msg=null,socket关闭     	
	            	try {
					    log.info("socket hase been closed :"+this.socket.getRemoteSocketAddress().toString()+",serverID: "+serverID);
						boolean res=sockMap.remove(this.serverID, this.socket);
						severMap.remove(this.serverID);	
						if(res==true){
							log.info("socket hase been removed from SockMap :"+this.socket.getRemoteSocketAddress().toString()+",serverID: "+serverID);
						}	
						this.socket.getInputStream().close();
						this.socket.close();
						this.socket=null;
	            		Thread.sleep(5);						
						return;
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            }
	        }	
		}
	}*/
	
	public class WriteThread extends Thread
	{
		private Socket sock;
		
		public WriteThread(Socket client)
		{sock = client;}
	
		public  void run()
		{
			if(this.sock==null){
				return;  //线程终止
			}
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	    	while(this.sock!=null && !this.sock.isClosed()  )
	    	{
	    		try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(!sockMap.containsValue(this.sock)){  //sockMap已把当前socket删除
					try {
						log.error("can't find THIS sock in SockMap,THIS sock:"+this.sock.getRemoteSocketAddress()+",socket closed!");
						this.sock.getOutputStream().close();
						this.sock.close();
						this.sock=null;
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					return;
				}
	    		if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
		    		Message outMsg=null;
		    		Socket targetSock=null;
		    		int serverID=-1;
					try {
						
						synchronized(CtrolSocketServer.sendCommandQueue){ //锁定队列
							outMsg=CtrolSocketServer.sendCommandQueue.peek();
							if(null!=outMsg){
								//System.out.println("Thread="+this.getName()+",Size of sendCommandQueue="+sendCommandQueue.size()+",ServerID="+outMsg.getServerID()+",head of queue:"+outMsg.toString());
								
								if(outMsg.commandID>=0x2000 &&  outMsg.commandID<=0x21FF){ //消息服务器
									serverID=4;
								}else{
									 serverID=outMsg.getServerID();	
								}								
								targetSock= sockMap.get(serverID);
								if(targetSock==null || serverID<0){ //找不到serverID对应的socket
									log.error("Message has been disposed:"+outMsg.toString()+",because can't find sock in SockMap,sock:"+this.sock.getRemoteSocketAddress()+",outMsg.serverID="+serverID);
									CtrolSocketServer.sendCommandQueue.remove();
									continue;
								}					

								//if( this.sock.equals(targetSock) ){	
									outMsg = CtrolSocketServer.sendCommandQueue.take();
									if(outMsg.getCommandID()==20738){  //心跳
										log.debug("Start to send HeartBeat "+targetSock.getRemoteSocketAddress().toString()+",serverID: "+outMsg.serverID+",Msg:"+outMsg.toString());
									}
									outMsg.writeBytesToSock2(targetSock);
									if(outMsg.getCommandID()==20738){  //心跳
										log.debug("HeartBeat "+targetSock.getRemoteSocketAddress().toString()+",serverID: "+outMsg.serverID+",Msg:"+outMsg.toString());
									}else{
									    log.debug("Send  to "+targetSock.getRemoteSocketAddress().toString()+":"+outMsg.toString());	
									}

								//}
							}
						}

					} catch (InterruptedException e) {
						//e.printStackTrace();
						log.error("IOException:Socket has been closed,serverID :"+serverID+",sock :"+targetSock.getRemoteSocketAddress().toString());
						log.error(e);
						return;
					} catch (IOException e) {
						//e.printStackTrace();
						try {
							targetSock.close();
							targetSock=null;							
							log.error("IOException:Socket has been closed,serverID :"+serverID+",sock :"+targetSock.getRemoteSocketAddress().toString());
							log.error(e);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						targetSock=null;
						return;
					} 				
		    	}
	    	}
	    	log.error("Write thread terminate, sock=null,"+this.sock.getRemoteSocketAddress());
	    	return;
		}
	} 
	
	public class ProcessThread extends Thread
	{	
		
		public void run()
		{
			while(true){
				Message msg;
				try {
					Thread.sleep(5);
					msg = CtrolSocketServer.receiveCommandQueue.poll(200, TimeUnit.MICROSECONDS);					
					if(msg!=null){
						try {
							lcontrol.decodeCommand(msg);	
						} catch (Exception e) {
							e.printStackTrace(); 
							LogicControl.logException(e);
							
					   		ProcessThread pt= new  ProcessThread(); //创建一个新的线程
					   		pt.setName("ProcessThread");
					   		pt.start();
					   		return;
						}						
					}
				} catch (InterruptedException e) {
					log.error(e);
				}
			}
		}
	}
	
	public void sendAuth(Socket sock,int clusterID,int  serverID ,int serverType) throws IOException{
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}					
		Header header=Message.getOneHeaer((short)CMD__Identity_REQ);
		String jsonStr="{\"uiClusterID\":"+clusterID+",\"usServerType\":"+serverType+",\"uiServerID\":"+serverID+"}";
		Message authMsg=null;
		try {
			authMsg = new Message(header, "", jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		authMsg.writeBytesToSock2(sock);
		log.info("Send Auth "+sock.getRemoteSocketAddress().toString()+":"+authMsg.toString());		
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
			//Server server=severMap.get(replyMsg.serverID);
			Socket sock=sockMap.get(replyMsg.serverID);
			if(sock==null ){
				log.error("can't find sock from sockMap by serverID:"+replyMsg.serverID+",this message will be disposed,cookieID="+heartBeatMsg.getCookie());
				return;
			}else{
				//replyMsg.writeBytesToSock2(sock);
				sendCommandQueue.offer(replyMsg, 200, TimeUnit.MILLISECONDS);
				
			}

			//boolean flag=CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
			
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
				CtrolSocketServer.sendCommandQueue.offer(msg,100, TimeUnit.MICROSECONDS);
				log.info("anth faild: "+replyMsg.toString());

				Thread.sleep(5);
				sock.close();
				sock=null;
				return serverID;
			}
			Server server=getServerInfo(serverID);			
			int serverType=msg.getJson().getInt("usServerType");	
			if(server!=null && server.getServerType()==serverType ){
				severMap.put(serverID, server);	
				sockMap.put(serverID, sock);
			
				printSocketMap();
				
				JSONObject json=new JSONObject();
				json.put("errorCode", 0);
				Message replyMsg=new Message(msg);
				replyMsg.setCommandID((short) (msg.getCommandID()+Message.COMMAND_ACK_OFFSET));
				replyMsg.setJson(json);
				replyMsg.setServerID(serverID);
				Thread.sleep(200);
				boolean flag=CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
				log.debug("Send AuRs "+sock.getRemoteSocketAddress().toString()
						+",serverID: "+replyMsg.serverID+",serverType: "+server.getServerType()+":"+replyMsg.toString());
				return serverID;
			}else{                                //鉴权失败
				log.error("Authenrize failed,serverType not match: "+sock.getRemoteSocketAddress().toString()+":"+sock.getPort()+",serverID: ");
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
	public static void printSocketMap(){
		for (Entry<Integer, Socket> sock : sockMap.entrySet()) {
			log.info("sockMap,ID:"+sock.getKey()+",sock:"+sock.getValue().getRemoteSocketAddress().toString());
		}
	}
	
	public   Server getServerInfo(int serverID){		
		String sql="select  "
				+" serverid  ,"				
				+"serverip ,"
				+"serverport ,"
				+"servertype ,"
				+"clusterid "
				+ "  from "				
				+"info_server"
				+" where serverid= "+serverID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql_main.select(sql);
		if(res==null ) {
			log.error("ERROR:exception happened: "+sql);
			return null;
		}else if(res=="") {
			log.error("ERROR:query result is empty: "+sql);
			return null;
		}
		//System.err.println(res);
		String[]  cells=res.split(",");
		Server server=new Server(cells[1], Integer.parseInt(cells[2]), Integer.parseInt(cells[3]), Integer.parseInt(cells[4]),serverID);
		server.setLastHeartBeatTime(new Date());
		return server;
	}
	
	
	public static Message readFromClient(Socket clientRequest) 
    {  		
		if(clientRequest==null || clientRequest.isClosed()|| !clientRequest.isConnected()){
			return null;
		}
		InputStream in=null;
		try {
			in = clientRequest.getInputStream();
			if (in==null) {
				log.warn("InputStream is null for socket:"+clientRequest.getRemoteSocketAddress().toString());
				return null;
			}
			byte[] b23=new byte[23]; 
			Header head=new Header();
			Message msg=new Message();
			int offset=0;

    		while(offset<23){
    			int len = in.read(b23,offset,23-offset);
    			if(len<0){
    				Thread.sleep(10);
    				len = in.read(b23,offset,23-offset);
    				if(len<0){
	    				log.fatal("read failed from socket,read len <0,"+clientRequest.getRemoteSocketAddress().toString()+",likely the remote host has been closed.");
	    				return null;
    				}
    			}else{
    				offset=offset+len;
    			}    			
    		}
    		byte[] headTag     ={b23[0],b23[1],b23[2],b23[3],b23[4],b23[5]};	
    		String headStr=new String(headTag,"UTF-8");
    		if(headStr.equals("#XRPC#")){
    			head=new Header(b23);
    		}else{
    			log.error("Input stream can't be recognized,message must be started with \"#XRPC#\",socket closed. ");
    			return null;
    		}
			//head.printHeader();  
    		
	    	byte[] cookie=new byte[head.cookieLen];
	    	try {
				clientRequest.getInputStream().read(cookie,0,head.cookieLen);
			} catch (IOException e) {			
				e.printStackTrace();
				
			}
	    	String cookieStr=new String(cookie);
	    	
	    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
	    	String comString=new String(commnad);
	    	if(comString.length()==0 || new JsonValidator().validate(comString)==false ){
	    		if(comString!=null){
	    			log.fatal("Json parse error,header="+head.toJson().toString()+",cookie="+cookieStr+",json="+comString);
	    		}
	    		Thread.sleep(150);
	    		return Message.getEmptyMsg();
	    	}else{
	    		try {
					msg=new Message(head, cookieStr, comString);
				} catch (JSONException e) {
					log.error("Json2 parse error,header="+head.toJson().toString()+",cookie="+cookieStr+",json="+comString);
					e.printStackTrace();
					return null;
				}
	    	}
	    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	//if(msg.isAuth() && msg.commandID!=SocketClient.CMD__HEARTBEAT_REQ){
	    	log.debug("Recv frm "+clientRequest.getRemoteSocketAddress().toString()+":"+msg.toString());
	    	//}
	        return msg;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(" IOException socket:"+clientRequest.getRemoteSocketAddress().toString()+" , socket will be closed.");
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			return null;			
		} catch (InterruptedException e) {
			e.printStackTrace();
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;  
		}
		
    } 
	


    public static void main(String [] args) throws IOException, Exception      
    {  
    	Configure cf= new Configure();
    	LogicControl lc=new LogicControl(cf);
    	new CtrolSocketServer(cf).listen(); 
    	
    	/*JSONObject json;
		json = new JSONObject("{\"sender\":0,\"receiver\":2}");
		Message msg1=new Message((short)0x1635, "1433128078_15", json);
		Message msg2=new Message((short)0x5635, "1433128078_15", json);
		TimeOutMap tm=new TimeOutMap();
		tm.put(msg1.getCookie(), msg1);

		Thread.sleep(10);

		tm.put(msg2.getCookie(), msg2);*/


    }    
}
