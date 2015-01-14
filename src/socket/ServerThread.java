package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import control.LogicControl;


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
        this.clientRequest = s;  
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
        	if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
        		Message outMsg=null;
				try {
					outMsg=CtrolSocketServer.sendCommandQueue.peek();
					String clientIP=getClientIP(outMsg.getServerID());
					if(null!=outMsg && this.clientRequest.getInetAddress().getHostAddress()==clientIP ){
						outMsg = CtrolSocketServer.sendCommandQueue.poll(100, TimeUnit.MICROSECONDS);
						outMsg.writeToSock(clientRequest);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}       		     		
        	}
        	
            try {
				msg= readFromClient();
				//readTest();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			} 
            try {
				CtrolSocketServer.receiveCommandQueue.offer(msg,100, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }   
    } 
    
	public String getClientIP(int clientID){
		return CtrolSocketServer.clientMap.get(clientID);		
	}
    
/*	private void readTest() 
    {  
		byte[] b2=new byte[50]; 
		try {
			clientRequest.getInputStream().read(b2,0,2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("B2: "+b2[0]+","+b2[1]);
		//byte[] b2={b2[0],b2[1]};
		System.out.println("B2 int: "+BytesUtil.getShort(b2) );
		System.out.println("B2 int: "+BytesUtil.bytesToShort(b2) );
		System.out.println("String:"+new String(b2));
		
		byte[] b50=new byte[50]; 
		try {
			//clientRequest.getInputStream().read(b50,0,50);
			System.out.println(new String(b2,"UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    }*/

	private Message readFromClient() throws UnsupportedEncodingException, JSONException    
    {  
		byte[] b23=new byte[23]; 
		Header head=new Header();
		Message msg=new Message();
    	try {
			clientRequest.getInputStream().read(b23,0,23);
			head=new Header(b23);
			head.printHeader();
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress());  
		}
    	
    	
    	byte[] cookie=new byte[head.cookieLen];
    	try {
			clientRequest.getInputStream().read(cookie,0,head.cookieLen);
		} catch (IOException e) {			
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
		}
    	//int cookieInt=BytesUtil.getInt(cookie);
    	String cookieStr=new String(cookie);
    	System.out.println("cookie: "+cookieStr);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
			System.out.println("error:exception happened,connection from "+clientRequest.getInetAddress().getHostAddress() + " has been closed!");
		}  
    	String comString=new String(commnad);
    	System.out.println("command: "+comString);
    	msg=new Message(head, cookieStr, comString);
        return msg; 
    } 
    

    
    
/*    
 private void validateCommand(Message msg) {
    	String command=null; 
    	if(str==null) return;
        command = str.trim().toUpperCase();  
        if (command.equals("HELP"))  
        {  
            // 命令help查询本服务器可接受的命令 
        	output.println("This is DeviseControl server,only following command can be accepted:");
            output.println("SynRoomProfile");  
            output.println("SwitchToRoomProfile");  
            output.println("SynApplianceList");  
            output.println("SwitchApplicanceState");  
        } else if (command.startsWith("QUERY"))  
        { // 命令query  
            output.println("Command Accept !");  
            try {
            	receiveCommandQueue.put(command);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
        // else if …….. //在此可加入服务器的其他指令  
        else if (!command.startsWith("HELP") && !command.startsWith("QUIT")  
                && !command.startsWith("QUERY"))  
        {  
            output.println("Invalid Command ! Please key in HELP to inquire all valid command !");  
        }    	
    } 
    
    
    private String readFromClient2()  
    {        
        try  
        {  
        	str = input.readLine(); 
            return str;  
        }  
        //如果捕捉到异常，表明该Socket对应的客户端已经关闭  
        catch (IOException e)  
        {  
            //删除该Socket。  
            CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress());    //① 
            //System.out.println("error:exception happened,connection from "+clientRequest.getInetAddress().getHostAddress() + " has been closed!");  
        }  
        return null;  
    } */
      
}  


