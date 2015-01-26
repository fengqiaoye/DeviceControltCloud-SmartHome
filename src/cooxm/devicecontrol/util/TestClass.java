package cooxm.devicecontrol.util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 09:48:27 
 */
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.socket.Header;
import cooxm.devicecontrol.socket.Message;
import redis.clients.jedis.Jedis;



public class TestClass {
	
	public void jsonDecodeTest() throws JSONException{
		String str = "[{\"id\":\"\",\"num\":\"\",\"dt\":\"2010-07-21T17:29:28\",\"consignee\":\"aaaa\",\"bank\":\"001\",\"ems\":\"0\"}]";
		String str1="{\"student\":[{\"name\":\"leilei\",\"age\":23},{\"name\":\"leilei02\",\"age\":23}]}";
		String str2="{ \"people\":[{\"firstName\":\"Brett\",\"lastName\":\"McLaughlin\",\"email\":\"aaaa\"},{\"firstName\":\"Jason\",\"lastName\":\"Hunter\",\"email\":\"bbbb\"},{\"firstName\":\"Elliotte\",\"lastName\":\"Harold\",\"email\":\"cccc\"},{\"INT\":\"123\",\"BOOL\":\"false\",\"DOUBLE\":\"456.789\"}]}";
		
		System.out.println(new Date());
		for (int i=0;i<10000000;i++)	{
			JSONObject jo = new JSONObject(str2);
			Object ja=jo.get("people");		
		}
		System.out.println(new Date());		
	}
	
	public static void writeObj(Jedis jedis,String key,Object obj){
		ByteArrayOutputStream bos =  new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos =  new ObjectOutputStream(bos);
			oos.writeObject(obj);	
			oos.close();
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte [] byteArray = bos.toByteArray();
		jedis.hset("roomMap".getBytes(), key.getBytes(), byteArray);

		
	}
	
	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
		//反序列化
		bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		  return ois.readObject();
		} catch (Exception e) {
		 
		}
		return null;
		}
	
	public static void getObj(Jedis jedis,String key){
		byte [] bs = jedis.hget("roomMap".getBytes(),key.getBytes());

		ByteArrayInputStream bis =  new ByteArrayInputStream(bs);

		ObjectInputStream inputStream;
		try {
			inputStream = new ObjectInputStream(bis);
			String readObject = (String) inputStream.readObject();

			System. out .println( " read object \t" + readObject.toString());

			inputStream.close();

			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testJson(){
		JSONObject jo=new JSONObject();
		try {
			jo.put("1","2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	static class Test{
		String ID;
		String name;
		Test(){}
		Test(String ID,
		String name){
			this.ID=ID;
			this.name=name;
		}
	}
	
	public static void sendMsgTest(){
    Message msg=new Message().getOneMsg();    	
    	try {
			Socket sock= new Socket("172.16.35.210", 10290);
		 	msg.writeBytesToSock(sock);
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	
	public static void referenceTest(){

		
		Message a = new Message();
		a.cookie="test A";
		System.out.println(a);
		Message b=a ;//new Message(a);
		b.cookie="test B";
		System.out.println(b);
		
		System.out.println(a.cookie);
		System.out.println(b.cookie);
		
		
	}

	public static void printReachableIP(InetAddress remoteAddr, int port){ 
	    String retIP = null; 
	 
	    Enumeration<NetworkInterface> netInterfaces; 
	    try{ 
	      netInterfaces = NetworkInterface.getNetworkInterfaces(); 
	      while(netInterfaces.hasMoreElements()) {    
	          NetworkInterface ni = netInterfaces.nextElement();    
	          Enumeration<InetAddress> localAddrs = ni.getInetAddresses(); 
	          while(localAddrs.hasMoreElements()){ 
	              InetAddress localAddr = localAddrs.nextElement(); 
	              if(isReachable(localAddr, remoteAddr, port, 5000)){ 
	                      retIP = localAddr.getHostAddress(); 
	                      break;        
	              } 
	          } 
	       } 
	    } catch(SocketException e) { 
	        System.out.println(
	    "Error occurred while listing all the local network addresses."); 
	    }    
	    if(retIP == null){ 
	        System.out.println("NULL reachable local IP is found!"); 
	    }else{ 
	        System.out.println("Reachable local IP is found, it is " + retIP); 
	    }        
	 }  
	
	
	public static boolean isReachable(InetAddress localInetAddr, InetAddress remoteInetAddr,int port, int timeout) { 
		
		boolean isReachable = false; 
		Socket socket = null; 
		try{ 
		 socket = new Socket(); 
		 // 端口号设置为 0 表示在本地挑选一个可用端口进行连接
		 SocketAddress localSocketAddr = new InetSocketAddress(localInetAddr, 0); 
		 socket.bind(localSocketAddr); 
		 InetSocketAddress endpointSocketAddr = new InetSocketAddress(remoteInetAddr, port); 
		 socket.connect(endpointSocketAddr, timeout);        
		 System.out.println("SUCCESS - connection established! Local: " + 
				 			localInetAddr.getHostAddress() + " remote: " + 
				 			remoteInetAddr.getHostAddress() + " port" + port); 
		 isReachable = true; 
		} catch(IOException e) { 
		 System.out.println("FAILRE - CAN not connect! Local: " + 
				 	localInetAddr.getHostAddress() + " remote: " + 
				 	remoteInetAddr.getHostAddress() + " port" + port); 
		} finally{ 
		 if(socket != null) { 
		 try{ 
		 socket.close(); 
		 } catch(IOException e) { 
		    System.out.println("Error occurred while closing socket.."); 
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
	

	public static void main(String[] args) {
//		String str1="{\"student\":[{\"name\":\"leilei\",\"age\":23},{\"name\":\"leilei02\",\"age\":23}]}";
//		Jedis jedis=new Jedis("172.16.35.170", 6379,200);
//		 writeObj( jedis,"student",str1);
//		 getObj(jedis,"student");
		 
//		 byte [] bs = jedis.hget("roomMap".getBytes(), "student".getBytes());
//		 String student=(String) unserialize(bs);
//		 System.out.println(student);
		 
//		 testJson();
		
		//referenceTest();
		
	//	sendMsgTest();
		

//		InetAddress remoteaddress;
//		InetAddress localaddress;
//		try {
//			localaddress = InetAddress.getByName(getLocalIP());	
//			remoteaddress = InetAddress.getByName("172.16.35.174");			
//			//printReachableIP(remoteaddress, 6379);
//			Boolean b=isReachable(localaddress, remoteaddress, 6379, 5000);
//			System.out.println(b);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		


	
		
		
	}
}
