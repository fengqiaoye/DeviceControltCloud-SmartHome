package cooxm.devicecontrol.socket;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;



public class MsgSocketClient {
	/**用户连接的通信套接字*/  
    Socket sock;
    BufferedReader input;
    PrintWriter output;
    Message msg = null;
    String str = null; 
    public static BlockingQueue<Message> sendMsgQueue;
    
    public static Logger log = Logger.getLogger(CtrolSocketServer.class);   
	
    public MsgSocketClient(String IP,int port)  
    {  
    	log.info("starting connect to  message server...");
        OutputStreamWriter writer;  
        InputStreamReader reader;  
        try  
        { 
        	this.sock = new Socket(IP, port);
            writer = new OutputStreamWriter(sock.getOutputStream());   
            output = new PrintWriter(writer, true); 
            reader = new InputStreamReader(sock.getInputStream(),"utf-8");  
            input = new BufferedReader(reader); 
        } catch (IOException e)  
        {  
            System.out.println(e.getMessage());  
        }
        sendMsgQueue= new ArrayBlockingQueue<Message>(1000);
        System.out.println("Initialize MsgSocketClient finished !");
    } 
    
    public static void main(String [] args)       
    {  
    	MsgSocketClient msgSock= new MsgSocketClient("172.16.35.174", 14290);
   	
    }

}
