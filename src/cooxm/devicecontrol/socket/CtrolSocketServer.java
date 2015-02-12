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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.Config;
import cooxm.devicecontrol.util.MySqlClass;

public class CtrolSocketServer {
	Config config=null;
    ServerSocket severSock = null;
	static OutputStream output = null;
	
    /** Map   < serverID,Socket > */
	public static Map<Integer,Socket> sockMap= new HashMap<Integer,Socket>();
	
    /** Map   < IP,thread >  */
	//public static Map<String,Thread> threadMap;//= new HashMap<String,Thread>();
	
    /*** Map   < serverID,server >     */
	public static Map<Integer,Server> severMap=new HashMap<Integer,Server>();
	
   // public static BlockingQueue<Message> receiveCommandQueue;
   // public static BlockingQueue<Message> sendCommandQueue;
	public static ReceiveCommandQueue receiveCommandQueue;
	public static SendCommandQueue sendCommandQueue;
    
	static Logger log =Logger.getLogger(CtrolSocketServer.class);	
	
	/***@param serverPort: 从配置文件中读取: ./conf/control.conf */
	public CtrolSocketServer(Config config) {
		log.info("starting device control socket server...");
		
		int serverPort=Integer.parseInt(config.getValue("server_port"));
		sendCommandQueue=SendCommandQueue.getInstance();
		receiveCommandQueue=ReceiveCommandQueue.getInstance();
        try{
        	severSock= new ServerSocket(serverPort);
        }
        catch(IOException e)
        {
         	log.error(e);
            System.exit(1);
        }
        this.config=config;
	}
	

	public void listen() throws IOException, Exception  
	{
        while(true)
        {
        	log.info(" Listening at port:"+severSock.getLocalPort()+"...");
        	Socket sock = severSock.accept();
        	 InetAddress clientAdress=sock.getInetAddress();
        	 int count=severMap.size()+1;
        	log.info(" Accept the "+ count +"th connection from client:"+clientAdress.getHostAddress()+"("+clientAdress.getHostName()+")");  


            ReadThread rt = new ReadThread(sock);    
       		rt.start(); 
       		WriteThread wr=new  WriteThread(sock);
       		wr.start();
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
	            	msg.setServerID(serverID);
	            	switch (msg.getCommandID()) {
					case 0x1101:
						int serverID=authenrize(msg,this.socket);
						if(serverID>0){
							this.serverID=serverID;
						}
						break;
					case 0x1102:
						replyHeartBeat(msg);
						break;
					default:
			            try {
							CtrolSocketServer.receiveCommandQueue.offer(msg,100, TimeUnit.MICROSECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
	            }else{   //socket关闭
	            	try {
						
						sockMap.remove(this.serverID);
						severMap.remove(this.serverID);
						this.socket.close();
						log.info("socket closed :"+this.socket.getInetAddress().getHostAddress()+",serverID: "+serverID);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }	
		}
	}
	
	public class WriteThread extends Thread
	{
		private Socket socket;
		
		public WriteThread(Socket client)
		{socket = client;}
	
		public void run()
		{
	    	while(!this.socket.isClosed())
	    	{
	    		if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
		    		Message outMsg=null;
					try {
						outMsg=CtrolSocketServer.sendCommandQueue.peek();
						if(null!=outMsg){
							int serverID=outMsg.getServerID();	
							//System.out.println("write Thread: size of sockMap: "+sockMap.size());
							 if(serverID>0 && null!=sockMap.get(serverID)){							
								outMsg = CtrolSocketServer.sendCommandQueue.take();//poll(100, TimeUnit.MICROSECONDS);
								System.out.println("write Thread: size of sendCommandQueue: "+CtrolSocketServer.sendCommandQueue.size()+"\nSend  :"+outMsg.msgToString());
								outMsg.writeBytesToSock(sockMap.get(serverID));
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 				
		    	}
	    	}
		}
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
	
	/**回复心跳 */
	public void replyHeartBeat(Message heartBeatMsg){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Message replyMsg=new Message(heartBeatMsg);
		replyMsg.setCommandID((short) (heartBeatMsg.getCommandID()+Message.COMMAND_ACK_OFFSET));
		JSONObject json=new JSONObject();
		try {
			json.put("uiTime", sdf.format(new Date()));
			replyMsg.setJson(json);
			System.out.println("Beat  : "+replyMsg.msgToString());
			CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	/** 返回serverID*/	
	public  int authenrize(Message msg,Socket sock){
		try {
			int serverID=-1;
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
				Thread.sleep(5000);
				sock.close();
				return serverID;
			}
			Server server=getServerInfo(serverID);			
			int serverType=msg.getJson().getInt("usServerType");	
			String ss=sock.getInetAddress().getHostAddress();
			if(server!=null && server.getServerType()==serverType && server.getServerIP().equals(ss)){
				severMap.put(serverID, server);				
				sockMap.put(serverID, sock);
				
				JSONObject json=new JSONObject();
				json.put("errorCode", 0);
				Message replyMsg=new Message(msg);
				replyMsg.setCommandID((short) (msg.getCommandID()+Message.COMMAND_ACK_OFFSET));
				replyMsg.setJson(json);
				replyMsg.setServerID(serverID);
				CtrolSocketServer.sendCommandQueue.offer(replyMsg,100, TimeUnit.MICROSECONDS);
				System.out.println("size of sendCommandQueue: "+CtrolSocketServer.sendCommandQueue.size()+"serverID:"+replyMsg.getServerID());

				System.out.println("read  Thread: size of sockMap: "+sockMap.size());
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
		return -1; 	
	}      
		
	public synchronized  Server getServerInfo(int serverID){
		severMap=new HashMap<Integer, Server>();
		String mysql_ip			=this.config.getValue("mysql_ip");
		String mysql_port		=this.config.getValue("mysql_port");
		String mysql_user		=this.config.getValue("mysql_user");
		String mysql_password	=this.config.getValue("mysql_password");
		String mysql_database	=this.config.getValue("mysql_database_main");
		
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
			//server.setLastHeartBeatTime(new Date());
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
    	try {
			int len = clientRequest.getInputStream().read(b23,0,23);
			if(len >0){
				//inputsream.read(b23,0,23);
			   head=new Header(b23);
			}else{
				log.error("Read Null from socket:"+clientRequest.getInetAddress().getHostAddress());
				return null;
			}
			//head.printHeader();
		} catch (IOException e) {
			log.error("IOException socket:"+clientRequest.getInetAddress().getHostAddress()+" , socket will be closed.");
			e.printStackTrace();
//			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress());  
//			log.error("socket:"+clientRequest.getInetAddress().getHostAddress()+" has been removed from sockmap.");
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
    	//System.out.print(" cookie: "+cookieStr);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
			System.out.println("error:exception happened,connection from "+clientRequest.getInetAddress().getHostAddress() + " has been closed!");
		}  
    	String comString=new String(commnad);
    	msg=new Message(head, cookieStr, comString);
    	System.out.println("Recv  : "+msg.msgToString());
        return msg; 
    } 


    public static void main(String [] args) throws IOException, Exception      
    {  
    	Config cf = new Config();
		new CtrolSocketServer(cf).listen();
    	//initseverMap(cf);    	
    }    
}
