package cooxm.devicecontrol.socket;

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

import cooxm.devicecontrol.control.Config;
import cooxm.devicecontrol.util.MySqlClass;

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
	
    /**<pre>
     * Map   < serverID,serverIP >
     * @key clientID
     * @value clientIP
     */
	public static Map<Integer,String> clientMap;
	
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
         	log.error(e);
            System.exit(1);
        }
        initclientMap(config);
	}
	
	public static  void initclientMap(Config cf){
		clientMap=new HashMap<Integer, String>();
		String mysql_ip			=cf.getValue("mysql_ip");
		String mysql_port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String mysql_database	=cf.getValue("mysql_database_main");
		
		MySqlClass mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		
		String sql="select  "
				+" serverid       ,"
				+"serverip "
				+ "  from "				
				+"info_server"
				+ ";";
		System.out.println("query:"+sql);
		String res=mysql.select(sql);
		if(res==null ) {
			log.error("ERROR:exception happened: "+sql);
			return ;
		}else if(res=="") {
			log.error("ERROR:query result is empty: "+sql);
			return ;
		}
		String[] resArray=res.split("\n");
		for(String line:resArray){
			String[]  cells=line.split(",");
		  clientMap.put(Integer.parseInt(cells[0]), cells[1]);
		}
		
		//System.out.println("1");
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
		//new CtrolSocketServer(cf).listen();
    	initclientMap(cf);    	
    }

    
}
