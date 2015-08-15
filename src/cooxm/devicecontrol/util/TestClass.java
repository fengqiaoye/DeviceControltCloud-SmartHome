package cooxm.devicecontrol.util;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.device.TriggerFactor;
import cooxm.devicecontrol.socket.Header;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.socket.SocketClient;
import redis.clients.jedis.Jedis;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 09:48:27 
 */
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
		 	msg.writeBytesToSock2(sock);
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	
	public static void referenceTest(){

		
		Message a = new Message();
		a.setCookie("test A");
		System.out.println(a);
		Message b=a ;//new Message(a);
		b.setCookie("test B");
		System.out.println(b);
		
		System.out.println(a.getCookie());
		System.out.println(b.getCookie());
		
		
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
	
	public static void testPredicate(){
		List<Integer> strList=new ArrayList<Integer>();
		strList.add(5);
		strList.add(1001);
		strList.add(1002);
		final Predicate< Integer> filter=new Predicate<Integer>() {
			@Override
			public boolean test(Integer t) {
				if(t>4){
				    return true;
				}else{
					return false;
				}
			}				
		};		
		strList.removeIf(filter);
		System.out.println(strList.size()+strList.toString());
		
	}
	
	void inetAddress(){
		InetAddress remoteaddress;
		InetAddress localaddress;
		try {
			localaddress = InetAddress.getByName(getLocalIP());	
			remoteaddress = InetAddress.getByName("172.16.35.174");			
			//printReachableIP(remoteaddress, 6379);
			Boolean b=isReachable(localaddress, remoteaddress, 6379, 5000);
			System.out.println(b);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	static void btye2str(){
		byte[] b=new byte[30];
		b[0]=0x0;
		b[1]=0x0   ;
		b[2]=0x5f  ;
		b[3]=0x23  ;
		b[4]=(byte) 0xe4  ;
		b[5]=0x11  ;
		b[6]=0x8   ;
		b[7]=0x2   ;
		b[8]=0x50  ;
		b[9]=0x2   ;
		b[10]=0x9  ;
		b[11]=0x2  ;
		b[12]=(byte) 0xbe ;
		b[13]=0x6  ;
		b[14]=0x4  ;
		b[15]=0x0  ;
		b[16]=(byte) 0xae ;
		b[17]=0x51 ;
		b[18]=(byte) 0xff ;
		b[19]=0x0  ;
		b[20]=0x0  ;
		b[21]=0x0  ;
		b[22]=0x0  ;
		b[23]=0x0  ;
		b[24]=0x0  ;
		b[25]=0x0  ;
		b[26]=0x0  ;
		b[27]=0x0  ;
		b[28]=0x0  ;
		b[29]=0x0  ;
		
		String x=bytesToHexString(b);
		System.out.print(x + " ");
		
//		for(int i : b) {
//			System.out.print(i + " ");
//		}
		
	}
	
	public static String toHexString(String s) 
	{ 
		String str=""; 
		for (int i=0;i<s.length();i++) 
		{ 
			int ch = (int)s.charAt(i); 
			String s4 = Integer.toHexString(ch); 
			str = str + s4; 
		} 
		return str; 
	} 
	
	/**
	 *  Convert byte[] to hex string
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if(src==null||src.length<=0){
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}   
			stringBuilder.append(hv);
		}   
		return stringBuilder.toString();   
	}
	 
	/**
	 * Convert hex string to byte[]
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}
	
	/**
	 * Convert char to byte
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}
	
	
	public static void TimeTest(){
		for(int i=0;i<100;i++)
		{
			long time=System.currentTimeMillis();
			long second=time/1000;
			int  milsecond=(int) (time%1000);
			System.out.println(time+", "+second+", "+ milsecond);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	static class A{
		public int a;
		public String b;
		public A(int a,String b){
			this.a=a;
			this.b=b;
		}
	}
	
	public static void testRefrence(){

		Map<Integer, A> map=new HashMap<Integer, A>();
		A a1=new A(1, "a");
		//A a2=new A(2, "b");
		map.put(1, a1);
		A a3=map.get(1);
		changeA3(a3,map);
		
	}
	
	public static void test(){
		double x=Math.log10(2088/0.1)*20;
		System.out.println(x);
	}
	public static void changeA3(A sa,Map<Integer, A> map ){
		sa.b="aaaaaaaaaaaaaaa";
		System.out.println(map.get(1).b);
		
	}
	
	public static void testDecimal(){
		DecimalFormat    df   = new DecimalFormat("######0.00");   

		double d1 = 3.23566 ; 
		double d3 = 2.0;
		String x = df.format(d1); 
		String y = df.format(d3); 
		
		BigDecimal   b   =   new   BigDecimal(d1); 
		BigDecimal c = b.setScale(2,  BigDecimal.ROUND_HALF_UP);
		System.out.println(c);
	}

	

	public static void main(String[] args) throws UnknownHostException, IOException {
//		String str1="{\"student\":[{\"name\":\"leilei\",\"age\":23},{\"name\":\"leilei02\",\"age\":23}]}";
//		Jedis jedis=new Jedis("172.16.35.170", 6379,5000);
//		jedis.select(9);
//		 writeObj( jedis,"student",str1);
//		 getObj(jedis,"student");
		 
//		 byte [] bs = jedis.hget("roomMap".getBytes(), "student".getBytes());
//		 String student=(String) unserialize(bs);
//		 System.out.println(student);
		 
//		 testJson();
		
		//referenceTest();
		
	//	sendMsgTest();	
		
		
		
//		testPredicate();
		
//		btye2str();
		

	//	testRefrence();
		
		//test();
		
		//testDecimal();
		
		int c = Calendar.HOUR_OF_DAY;
		int a=Calendar.getInstance().get(c);
		System.out.println(a);
		


	
		
		
	}
}
