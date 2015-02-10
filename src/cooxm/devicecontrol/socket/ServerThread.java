package cooxm.devicecontrol.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.LogicControl;


public class ServerThread extends Thread  {
    Socket clientRequest;// 用户连接的通信套接字  
    BufferedReader input;// 输入流  
    PrintWriter output;// 输出流  
    Message msg = null;
    String str = null;  

    
    public static Logger log = Logger.getLogger(CtrolSocketServer.class);   
    
    
  
    // serverThread的构造器  
    public ServerThread(Socket s)  
    {  
        clientRequest = s;  
        // 接收receiveServer传来的套接字 

        OutputStreamWriter writer;  
        InputStreamReader reader;  
        try  
        { // 初始化输入、输出流              
            writer = new OutputStreamWriter(clientRequest.getOutputStream());   
            output = new PrintWriter(writer, true); 
            reader = new InputStreamReader(clientRequest.getInputStream(),"utf-8");  
            input = new BufferedReader(reader); 
            //dataOut=new DataOutputStream(clientRequest.getOutputStream());
        } catch (IOException e)  
        {  
            System.out.println(e.getMessage());  
        }  
        output.println("Welcome to DeviceControl server!"); 
        output.flush();
        // 客户机连接欢迎词  
    }  
  
    @Override 
    public void run()  
    { // 线程的执行方法  

        while (true)  
        { 
            msg= Message.readFromClient(clientRequest);
            if(msg!=null){
            	switch (msg.getCommandID()) {
				case 0x1101:
					authenrize(msg);
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

            }
        	if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
        		Message outMsg=null;
				try {
					outMsg=CtrolSocketServer.sendCommandQueue.peek();
					String clientIP=getClientIP(outMsg.getServerID());
					if(null!=outMsg && clientRequest.getInetAddress().getHostAddress()==clientIP ){
						
						outMsg = CtrolSocketServer.sendCommandQueue.poll(100, TimeUnit.MICROSECONDS);
						outMsg.writeToSock(clientRequest);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 				
        	}
        	

        }   
    } 
    
	public String getClientIP(int clientID){
		Server server= CtrolSocketServer.clientMap.get(clientID);
		return server.getServerIP();
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
			CtrolSocketServer.receiveCommandQueue.offer(msg,100, TimeUnit.MICROSECONDS);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public void authenrize(Message msg){
		try {
			int serverID = msg.getJson().getInt("uiServerID");
			int serverType=msg.getJson().getInt("usServerType");	
			if(CtrolSocketServer.clientMap.get(serverID).getServerType()==serverType){
				JSONObject json=new JSONObject();
				json.put("errorCode", 0);
				Message replyMsg=new Message(msg);
				replyMsg.setCommandID((short) (msg.getCommandID()+Message.COMMAND_ACK_OFFSET));
				replyMsg.setJson(json);
				System.out.println("Auth  : "+replyMsg.msgToString());
				CtrolSocketServer.receiveCommandQueue.offer(msg,100, TimeUnit.MICROSECONDS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}      
}  


