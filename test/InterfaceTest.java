



import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.naming.InitialContext;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.util.JSON;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.socket.SocketClient;

/**
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：May 24, 2015 4:37:16 PM
 */

public class InterfaceTest {	

    private static Socket sock;
    private static int count;
    
	static String getProfileStr="{\"profileID\":123456789,\"ctrolID\":12345677}";
	static String setProfileStr = "{\"profileName\":\"未知情景1\",\"factorList\":[{\"factorID\":20,\"minValue\":25,\"modifyTime\":\"2015-12-13 14:15:16\","
			+ "\"validFlag\":1,\"createTime\":\"2015-06-13 14:15:16\",\"maxValue\":28,\"roomID\":203,\"roomType\":2,\"operator\":0}],\"profileTemplateID\":0,"
			+ "\"modifyTime\":\"2015-06-13 14:15:17\",\"createTime\":\"2015-12-13 14:15:16\",\"profileID\":123456789,\"profileSetID\":12345,\"ctrolID\":12345677,\"roomID\":203,\"roomType\":2}";
	static String setdeviceStr = "{\"deviceType\":541,\"modifyTime\":\"2015-05-08 17:36:59\",\"createTime\":\"2015-06-08 17:36:59\",\"relatedDevType\":0,"
			+ "\"ctrolID\":123456789,\"type\":0,\"deviceID\":1234567890,\"deviceSN\":\"XJFGOD847X\",\"wall\":1,\"roomID\":2,\"roomType\":101}";
	static String getDeviceStr="{\"deviceID\":1234567890,\"ctrolID\":123456789}";
    

	public static void main(String[] args) throws JSONException, InterruptedException   { 
		try {
			sock = new Socket("172.16.45.30",20190);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SocketClient.sendAuth(sock, 2,15,201);
		count=1;
		
		JSONObject json1=new JSONObject(getProfileStr);
		json1=new JSONObject(setProfileStr);
		json1=new JSONObject(getDeviceStr);
		json1=new JSONObject(setdeviceStr);
		
		System.out.println("successs");
		
		
		try {
			get_room_profile_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			set_room_profile_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			switch_room_profile_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			get_one_device_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			set_one_device_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			switch_one_device_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread.sleep(10*1000);
		
	}
    
	public static void get_room_profile_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getProfileStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 5633, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void set_room_profile_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject();
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 1) ;
		json.put("profile",new JSONObject(setProfileStr));
		Message msg1 = new Message((short) 5634, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void switch_room_profile_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(setProfileStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 1) ;
		//json.put("profile",setProfileStr);
		Message msg1 = new Message((short) 5636, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(60*1000);
	}
	
	public static void get_one_device_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getDeviceStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 1) ;
		//json.put("profile",setProfileStr);
		Message msg1 = new Message((short) 5673, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void set_one_device_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject();
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 1) ;
		json.put("device",new JSONObject(setdeviceStr));
		Message msg1 = new Message((short) 5674, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void switch_one_device_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getDeviceStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 1) ;
		//json.put("profile",setProfileStr);
		Message msg1 = new Message((short) 5675, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(60*1000);
	}
	
	


	
	public void match_chinese(){
		

	}
	
}
