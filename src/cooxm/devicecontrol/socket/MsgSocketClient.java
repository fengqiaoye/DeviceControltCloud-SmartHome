package cooxm.devicecontrol.socket;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;



public class MsgSocketClient extends Socket implements Runnable {

	private static final short ACK_OFFSET = 0x4000;
	private static final short CMDCODE_RESERVED_FOR_COMMON_BEGIN 		= 0x1100;
	private static final short CMD__Identity_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 1;  //身份标识请求
	private static final short CMD__Identity_ACK 		= CMD__Identity_REQ  + ACK_OFFSET;  //身份标识请求
	private static final short CMD__HEARTBEAT_REQ 		= CMDCODE_RESERVED_FOR_COMMON_BEGIN + 2;  //心跳请求
	private static final short CMD__HEARTBEAT_ACK 		= CMD__HEARTBEAT_REQ  + ACK_OFFSET;  //心跳请求
		
    
    public static Logger log = Logger.getLogger(CtrolSocketServer.class);  
	/**用户连接的通信套接字*/  
    static Socket sock=null;
    BufferedReader input;
	
    public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public MsgSocketClient(String IP,int port) throws UnknownHostException, IOException  
    {   
    	log.info("starting connect to  message server...");
		InetAddress remoteaddress;
		InetAddress localaddress;
        try  
        { 
			localaddress =InetAddress.getByName("0.0.0.0");// InetAddress.getByName(getLocalIP());	
			remoteaddress = InetAddress.getByName(IP);
			Boolean b=isReachable(localaddress, remoteaddress, port, 5000);
        	if(b){
        		sock = new Socket(IP, port);
    			input=new BufferedReader(new InputStreamReader(sock.getInputStream()));	
    			
        		Header header=Message.getOneHeaer((short)CMD__Identity_REQ);
        		String jsonStr="{\"uiClusterID\":1,\"usServerType\":201,\"uiServerID\":6}";
        		Message authMsg=new Message(header, "", jsonStr);
        		authMsg.writeBytesToSock(sock);
        		System.out.println("Send to MsgServer : "+authMsg.msgToString());
        		//new readThread(sock).run();

        	}else{
        		log.error("Initialize MsgSocketClient failed, during to connetion to remote host "+IP+":"+port+" failed.");
        	}

        } catch (IOException e)  
        {  
            System.out.println(e.getMessage());  
        }
    } 
    
	public static boolean isReachable(InetAddress localInetAddr, InetAddress remoteInetAddr,int port, int timeout) { 		
		boolean isReachable = false; 
		Socket socket = null; 
		try{ 
		 socket = new Socket(); 
		 /**端口号设置为 0 表示在本地挑选一个可用端口进行连接*/
		 SocketAddress localSocketAddr = new InetSocketAddress(localInetAddr, 0); 
		 socket.bind(localSocketAddr); 
		 InetSocketAddress endpointSocketAddr = new InetSocketAddress(remoteInetAddr, port); 
		 socket.connect(endpointSocketAddr, timeout);        
		 log.info("SUCCESS - connected to MsgServer! Local: " + 
				 			localInetAddr.getHostAddress() + " remote: " + 
				 			remoteInetAddr.getHostAddress() + " port:" + port); 
		 isReachable = true; 
		} catch(IOException e) { 
			log.error("FAILRE - CAN not connect! Local: " + 
				 	localInetAddr.getHostAddress() + " remote: " + 
				 	remoteInetAddr.getHostAddress() + " port:" + port); 
		} finally{ 
		 if(socket != null) { 
		 try{ 
		 socket.close(); 
		 } catch(IOException e) { 
			 log.error("Error occurred while closing socket.."); 
		   } 
		 } 
		} 
		return isReachable; 
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
     	   Message msg=CtrolSocketServer.readFromClient(sock);
     	   if(msg!=null){
     		   decodeMsg(msg);
     	   }
        }
   }
	
	public  class ReadThread extends Thread
	{
		private Socket socket;
		
		public ReadThread(Socket client)
		{socket = client;}
		
		public void run()
		{
           while(true){
        	   Message msg=CtrolSocketServer.readFromClient(socket);
        	   decodeMsg(msg);
           }
        }	
	}	
	
	public  void decodeMsg(Message msg){
		short commandID=msg.commandID;
		switch (commandID) {
		case CMD__Identity_ACK:			
			break;
		case CMD__HEARTBEAT_REQ:
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				msg.getJson().put("uiTime", sdf.format(new Date()));
				msg.msgLen=msg.getMsgLength();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			msg.commandID=CMD__HEARTBEAT_ACK;
			msg.writeBytesToSock(MsgSocketClient.sock);
			System.out.println("Send to MsgServer : "+msg.msgToString());
			break;
		default:
			break;
		}
		
	}
   
    public static void main(String [] args)       
    {  

    	try {
			MsgSocketClient msgSock= new MsgSocketClient("172.16.35.174", 10790);
			new Thread(msgSock).start();
			//msgSock.new ReadThread(sock).start();
			
		    System.out.println(" i love you "); 
		    
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   	
    }


}
