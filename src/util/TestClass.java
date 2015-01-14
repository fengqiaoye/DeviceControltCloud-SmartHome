package util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 09:48:27 
 */
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import socket.Header;
import socket.Message;



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
    	Message msg= new Message();
    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=2;
    	short msgLen=15;
    	short commandID=0x1601;
    	int sequeeceNo=123456;
    	short encType=1; 
    	short cookieLen=4;
    	int reserve=0;
    	
    	JSONObject json=new JSONObject();
    	try {
			json.put("CtrolID", 1234567);
	    	json.put("sender", 1);
	    	json.put("roomID", 103);
	    	json.put("errorCode", -12);
		} catch (JSONException e) {
			e.printStackTrace();
		}


    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
    	msg.header=head;
    	msg.cookie="87654321";
    	msg.json=json;
    	msg.receiveTime=new Date();
    	msg.replyTime=new Date();
    	
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
		
		sendMsgTest();
		
		}
}
