package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import control.Config;

public class CtrolSocketServer {

    ServerSocket severSock = null;
	static OutputStream output = null;
	
    /**
     * Map   < IP,Socket >
     * @key IP
     * @value Socket
     */
	public static Map<String,Socket> sockMap= new HashMap<String,Socket>();
	
    /**
     * Map   < IP,thread >
     * @key IP
     * @value thread
     */
	public static Map<String,Thread> threadMap;//= new HashMap<String,Thread>();
	
   // public static BlockingQueue<Message> receiveCommandQueue;
   // public static BlockingQueue<Message> sendCommandQueue;
	public static ReceiveCommandQueue receiveCommandQueue;
	public static SendCommandQueue sendCommandQueue;
    
	static Logger log =Logger.getLogger(CtrolSocketServer.class);	
	
	
	/***@param serverPort: 从配置文件中读取: ./conf/control.conf */
	public CtrolSocketServer(Config config) {
		log.info("starting device control socket server...");
		threadMap= new HashMap<String,Thread>();
		
		int serverPort=Integer.parseInt(config.getValue("server_port"));
		//int max_send_msg_queue=Integer.parseInt(config.getValue("max_send_msg_queue"));
		//int max_recv_msg_queue=Integer.parseInt(config.getValue("max_recv_msg_queue"));
		//sendCommandQueue= new ArrayBlockingQueue<Message>(max_send_msg_queue);
		//receiveCommandQueue= new ArrayBlockingQueue<Message>(max_recv_msg_queue);
		sendCommandQueue=SendCommandQueue.getInstance();
		receiveCommandQueue=ReceiveCommandQueue.getInstance();
        try{
        	severSock= new ServerSocket(serverPort);
        }
        catch(IOException e)
        {
            //System.out.println(e);
        	log.error(e);
            System.exit(1);
        }
	}
	
	public void listen() throws IOException, Exception  
	{
        while(true)
        {
        	log.info(" Listening at port:"+severSock.getLocalPort()+"...");
        	Socket sock = severSock.accept();
        	 InetAddress clientAdress=sock.getInetAddress();
        	log.info(" Accept connection from client:"+clientAdress.getHostAddress()+"("+clientAdress.getHostName()+")");  
        	sockMap.put(clientAdress.getHostAddress(), sock);

            ServerThread th = new ServerThread(sock);    
       		th.start(); 
       		threadMap.put(clientAdress.getHostAddress(), th);
			//Thread.sleep(200);  
       		log.info(" Started the "+threadMap.size()+"th thread!");
        }
	}
	
    public static void writeMsgToScok(Socket sock,String msg) throws IOException   
    {
    	OutputStreamWriter writer = new OutputStreamWriter(sock.getOutputStream());  
        PrintWriter output = new PrintWriter(writer, true);      	
        output.println(msg); 
    }
    
    
    public static String readLineFromScok(Socket sock) throws IOException   
    {
    	InputStreamReader reader = new InputStreamReader(sock.getInputStream()); 
    	BufferedReader input=new BufferedReader(reader) ;// 输入流  
    	String str = input.readLine();
    	return str;
    }

    public static void main(String [] args) throws IOException, Exception      
    {  
    	Config cf = new Config();
		new CtrolSocketServer(cf).listen();
    }

    
}
