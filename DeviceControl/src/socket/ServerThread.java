package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.json.JSONException;

import util.BytesUtil;


public class ServerThread extends Thread  {
    Socket clientRequest;// 用户连接的通信套接字  
    BufferedReader input;// 输入流  
    PrintWriter output;// 输出流  
    ObjectOutputStream os;
    Message msg = null;
    String str = null;  
    public static BlockingQueue<Message> receiveCommandQueue= new ArrayBlockingQueue<Message>(10000);
    public static BlockingQueue<Message> sendCommandQueue= new ArrayBlockingQueue<Message>(10000);
    
    public static Logger LOG = Logger.getLogger(ServerThread.class);   
    
    
  
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
            reader = new InputStreamReader(this.clientRequest.getInputStream(),"utf-8");  
            input = new BufferedReader(reader); 
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
        	if(!sendCommandQueue.isEmpty()){
        		Message outMsg=null;
				try {
					outMsg = sendCommandQueue.poll(200, TimeUnit.MICROSECONDS);
					os.writeObject(outMsg);
					writeToClient(outMsg.MessageToString());  

				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}       		     		
        	}
        	
            try {
            	msg= readFromClient();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
			//System.out.println("message received from client:"+msgLine);  
			if(msg.isValid()){
				if(receiveCommandQueue.offer(msg))
				receiveCommandQueue.add(msg);
				sendCommandQueue.add(msg);
			}else{
				System.out.println("Error: Cant't add Message to Receive Message Queue ! please confirm the queue have enough capacity!");
			}
  
        }   
    } 
    


	private Message readFromClient() throws UnsupportedEncodingException, JSONException    
    {  
		byte[] b23=new byte[23]; 
		Header head=new Header();
		Message msg=new Message();
    	try {
			clientRequest.getInputStream().read(b23,0,23);
			head=new Header(b23);
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
    	int cookieInt=BytesUtil.getInt(cookie);
    	System.out.println("cookie: "+cookieInt);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
		}  
    	String comString=new String(commnad);
    	System.out.println("command: "+comString);
    	msg=new Message(head, cookieInt, comString);
        return msg; 
    } 
    

    
	private void writeToClient(String command)  
    {  
        output.println(command);            
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


