package cooxm.devicecontrol.socket;

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
        	
            msg= Message.readFromClient(clientRequest);
           
			//readTest(); 
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

    
    /*private String readFromClient2()  
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


