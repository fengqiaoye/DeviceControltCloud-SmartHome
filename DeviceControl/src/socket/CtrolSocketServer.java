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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CtrolSocketServer {

    ServerSocket severSock = null;
	static OutputStream output = null;	
	public static Map<String,Socket> sockMap= new HashMap<String,Socket>();
	public static Map<String,Thread> threadMap= new HashMap<String,Thread>();
	
	
	
	
	public CtrolSocketServer(int localPort) {
		// TODO Auto-generated constructor stub
        try{
        	severSock= new ServerSocket(localPort);
        }
        catch(IOException e)
        {
            System.out.println(e);
            System.exit(1);
        }
	}
	
	public void listen() throws IOException, Exception  
	{
        while(true)
        {
        	Date now = new Date(); 
        	System.out.println(now + " Listenning at port:"+severSock.getLocalPort()+"...");
        	Socket sock = severSock.accept();
        	 InetAddress clientAdress=sock.getInetAddress();
        	System.out.println(now+" Accept connection from client:"+clientAdress.getHostAddress()+"("+clientAdress.getHostName()+")");  
        	sockMap.put(clientAdress.getHostAddress(), sock);
        	
//        	String msg=readMsgFromScok(sock);
//        	System.out.println(msg);

            ServerThread th = new ServerThread(sock);    
       		th.start(); 
       		threadMap.put(clientAdress.getHostAddress(), th);
			//Thread.sleep(200);  
			System.out.println(now+" Started the "+threadMap.size()+"th thread!");
        }
	}
	
    public static void writeMsgToScok(Socket sock,String msg) throws IOException   
    {
    	OutputStreamWriter writer = new OutputStreamWriter(sock.getOutputStream());  
        PrintWriter output = new PrintWriter(writer, true);      	
        output.println(msg); 
    }
    
    
    public static String readMsgFromScok(Socket sock) throws IOException   
    {
    	InputStreamReader reader = new InputStreamReader(sock.getInputStream()); 
    	BufferedReader input=new BufferedReader(reader) ;//  ‰»Î¡˜  
    	String str = input.readLine();
    	return str;
    }

    public static void main(String [] args)  
    {  
        try {
			new CtrolSocketServer(64415).listen();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  
    }
    
}
