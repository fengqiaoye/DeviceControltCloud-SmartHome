



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
	static String setdeviceStr = "{\"device\":{\"deviceType\":501,\"modifyTime\":\"2015-07-20 11:54:18\",\"createTime\":\"2015-07-20 11:54:18\",\"relatedDevType\":0,\"ctrolID\":10002,"
			+ "\"type\":1,\"deviceName\":\"电视\",\"deviceID\":1,\"wall\":7,\"deviceSN\":\"\",\"roomType\":1,\"roomID\":1000}}";

	static String getDeviceStr="{\"deviceID\":1234567890,\"ctrolID\":123456789}";
	static String getRoomStr="{\"roomID\":1000,\"ctrolID\":0}";
	static String getHouseStateStr="{\"roomID\":2000,\"ctrolID\":40008}";
	static String get_profile_template_list="{\"ctrolID\":40004}";
	static String get_profile_list="{\"ctrolID\":40008}";
	
	static String recognize_ir_code="{\"ircode\":\"33,04,00,00,24,00,26,82,4d,01,fa,82,51,06,0d,c1,11,52,c2,00,10,f4,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,1e,c1,11,51,c2,00,10,f2,c3,00,30,4d,b2,de,21,07,f8,00\",\"applianceType\":541,\"ctrolID\":0}";
	
	static String get_active_profile="{\"ctrolID\":40008}";
	static String switch_device_state="{\"deviceType\":541,\"receiver\":0,\"sender\":5,\"state\":{\"mode\":1,\"tempreature\":-1,\"modifyTime\":\"2015-07-16 11:40:46\",\"windDirection\":0,\"windSpeed\":-1,\"onOff\":0},\"ctrolID\":40006,\"deviceID\":7,\"roomID\":1000}";
    static String set_device_list="{\"deviceArray\":[{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":1},{\"modifyTime\":\"2015-07-20 11:45:37\","
    		+ "\"deviceID\":2},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":3},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":4},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":5}],\"receiver\":2,\"sender\":0,\"ctrolID\":10005}";
    static String warn_msg="{\"warn\":{\"severity\":2,\"msgContent\":\"warn\",\"createTime\":1439558074,\"warnType\":2,\"opType\":1,\"channel\":3,\"ctrolID\":10002,\"madeFrom\":2,\"target\":3,\"timeOut\":0},\"receiver\":0,\"originalSenderRole\":5,\"sender\":6,\"ctrolID\":10002}";
    public static void main(String[] args) throws JSONException, InterruptedException, UnknownHostException, IOException   { 

		sock = new Socket("172.16.45.30",20190);//120.24.81.23
		SocketClient.sendAuth(sock, 1,6,201);
		count=1;
		
		
		/*JSONObject json1=new JSONObject(getProfileStr);
		json1=new JSONObject(setProfileStr);
		json1=new JSONObject(getDeviceStr);
		json1=new JSONObject(setdeviceStr);*/
		
		//System.out.println("successs");
		
		
		/*try {
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
			get_one_room_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
//		try {
//			get_houseState_test();
//		} catch (JSONException | InterruptedException e) {
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		

		
		
		/*try {
			get_one_device_test();
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
		}*/
		
		//get_active_profile();
		//get_profile_list();
		
		//set_device_list();
		
		//warn_msg();
		recognize_ir_code();
		
		/*try {
			set_one_device_test();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			recognize_ir_code();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		if(sock==null || sock.isClosed()|| !sock.isConnected()){
			sock = new Socket("172.16.35.173",20190);
			SocketClient.sendAuth(sock, 2,15,201);
		}
		
		try {
			get_profile_template_list();
		} catch (JSONException | InterruptedException e) {
			e.printStackTrace();
		}*/
		//switch_device_state();
		Thread.sleep(100*1000);
		
	}
	
	public static void get_profile_list() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(get_profile_list);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1607, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void recognize_ir_code() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(recognize_ir_code);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x165d, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", recognize_ir_code);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
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
	
	public static void get_houseState_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getHouseStateStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1665, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void get_active_profile() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(get_active_profile);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1620, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(100*1000);
	}
	
	public static void switch_device_state() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(switch_device_state);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x162c, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(100*1000);
	}
	
	
	
	public static void get_one_room_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getRoomStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 5683, System.currentTimeMillis()
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
		json.put("receiver",2) ;
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
		//JSONObject json=new JSONObject(setProfileStr);
		JSONObject json=new JSONObject();
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		json.put("ctrolID", 12345677);
		json.put("profileID", 123456789);
		
		//json.put("profile",setProfileStr);
		Message msg1 = new Message((short) 5636, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());
        while(true){
			Message msg = CtrolSocketServer.readFromClient(sock);
			if (msg != null)
				System.out.println("Recv:"+msg.toString());	
        }
		//Thread.sleep(60*1000);
	}
	
	public static void get_one_device_test() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(getDeviceStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
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
		JSONObject json=new JSONObject(setdeviceStr);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		//json.put("device",);
		Message msg1 = new Message((short) 5674, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	
	public static void set_device_list() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(set_device_list);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		//json.put("device",);
		Message msg1 = new Message((short) 0x1631, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void warn_msg() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(warn_msg);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		//json.put("device",);
		Message msg1 = new Message((short) 0x2003, System.currentTimeMillis()
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
		json.put("receiver", 2) ;
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
	
    private static void get_profile_template_list() throws JSONException, InterruptedException
    {
		count++;
		JSONObject json=new JSONObject(get_profile_template_list);
		json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		//json.put("profile",setProfileStr);
		Message msg1 = new Message((short) 0x161f, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(60*1000);
    }
	
    private static int BitReverse(int j, int nu)
    {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++)
        {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }
    



	
	public void match_chinese(){
		

	}
	
}
