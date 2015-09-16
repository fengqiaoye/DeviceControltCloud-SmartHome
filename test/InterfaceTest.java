



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
	static String setdeviceStr = "{\"receiver\":2,\"sender\":0,\"changeFlag\":1,\"device\":{\"deviceType\":2201,\"remoteControl\":0,\"ctrolID\":10000,\"type\":0,\"deviceID\":2,\"deviceSN\":\"6a-00-00-00-00-00\",\"deviceName\":\"图灵猫传感器\",\"roomID\":1000,\"modifyTime\":\"2015-09-14 10:35:12\",\"createTime\":\"2015-09-14 10:29:31\",\"relatedDevID\":-1,\"state\":0,\"wall\":5,\"roomType\":1}}";

	static String getDeviceStr="{\"deviceID\":2,\"ctrolID\":10003}";
	static String getRoomStr="{\"roomID\":1000,\"ctrolID\":0}";
	static String getHouseStateStr="{\"roomID\":2000,\"ctrolID\":40008}";
	static String get_profile_template_list="{\"ctrolID\":40004}";
	static String get_profile_list="{\"ctrolID\":40008}";
	static String set_profile_list="{\"receiver\":2,\"sender\":0,\"profileArray\":[{\"modifyTime\":1441008197,\"profileID\":1},{\"modifyTime\":1441008197,\"profileID\":2},{\"modifyTime\":1441008197,\"profileID\":3},{\"modifyTime\":1441008197,\"profileID\":4}],\"ctrolID\":10006}";
	
	static String recognize_ir_code="{\"ircode\":\"33,04,00,00,24,00,26,82,50,01,f8,82,54,06,08,c1,11,56,c2,00,10,f1,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,19,c1,11,55,c2,00,10,f0,c3,00,30,4d,b2,de,21,07,f8,00\",\"applianceType\":541,\"ctrolID\":0}";
	
	static String get_active_profile="{\"receiver\":2,\"sender\":1,\"ctrolID\":10004}";
	static String switch_device_state="{\"deviceType\":421,\"receiver\":0,\"sender\":1,\"ctrolID\":10005,\"keyType\":501,\"deviceID\":6,\"roomID\":1001}";
	static String switch_device_state2="{\"deviceType\":421,\"receiver\":0,\"sender\":5,\"ctrolID\":10003,\"keyType\":501,\"deviceID\":13,\"roomID\":1000}";
    static String set_device_list="{\"deviceArray\":[{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":1},{\"modifyTime\":\"2015-07-20 11:45:37\","
    		+ "\"deviceID\":2},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":3},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":4},{\"modifyTime\":\"2015-07-20 11:45:37\",\"deviceID\":5}],\"receiver\":2,\"sender\":0,\"ctrolID\":10005}";
    static String warn_msg="{\"warn\":{\"severity\":2,\"msgContent\":\"warn\",\"createTime\":1439558074,\"warnType\":2,\"opType\":14,\"channel\":3,\"ctrolID\":10000,\"madeFrom\":2,\"target\":3,\"timeOut\":0},\"receiver\":0,\"originalSenderRole\":5,\"sender\":6,\"ctrolID\":10005}";
    static String warn_msg2="{\"warn\":{\"severity\":2,\"msgContent\":\"warn\",\"createTime\":1439803702,\"warnType\":2,\"opType\":13,\"channel\":3,\"ctrolID\":10015,\"madeFrom\":2,\"target\":3,\"timeOut\":0},\"receiver\":0,\"originalSenderRole\":5,\"sender\":6,\"ctrolID\":10015}";
    static String get_trigger_template_list="{\"offset\":0,\"receiver\":2,\"sender\":1,\"count\":20,\"ctrolID\":10000}";
    static String switch_room_profile="{\"receiver\":0,\"sender\":5,\"profileID\":2,\"ctrolID\":10003,\"roomID\":1000}";
    static String switch_profileSet="{\"receiver\":0,\"sender\":5,\"profileSetID\":2,\"ctrolID\":10013}";
    static String get_room_list="{\"receiver\":2,\"sender\":1,\"ctrolID\":10003}";
    
    static String download_ir_file="{\"receiver\":2,\"sender\":1,\"ctrolID\":10003,\"applianceType\":501,\"fileID\":214}";
    static String heartBeat="{\"uiTime\":1441932101}";
    
    public static void main(String[] args) throws JSONException, InterruptedException, UnknownHostException, IOException   { 

		sock = new Socket("172.16.45.30",20190);//120.24.81.23
		SocketClient.sendAuth(sock, 1,8,103);
		count=1;
		
		
		//System.out.println("successs");
		
		

	   //get_room_profile_test();

		

//	   set_room_profile_test();
//
//
//		
//	   get_one_room_test();
//
//		
//    	get_houseState_test();
//		
//
//		
//		
		//get_one_device_test();
//
//
//
	    
		
		//get_active_profile();
		// heartBeat() ;
		//get_profile_list();
		//set_profile_list();
		//set_device_list();
		
		//warn_msg();
		//download_ir_file();
		//recognize_ir_code();
		

		//set_one_device_test();

		//get_trigger_template_list();
			//recognize_ir_code();

		
		switch_room_profile();

		//get_profile_template_list();
		
		//switch_device_state();
		
		//switch_profileSet();
		
		//get_room_list();
		
		
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
	
	public static void set_profile_list() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(set_profile_list);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1608, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	
	public static void get_trigger_template_list() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(get_trigger_template_list);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x164a, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void switch_room_profile() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(switch_room_profile);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1604, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(20*1000);
	}
	
	public static void download_ir_file() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(download_ir_file);
		json.put("sender", 0) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x165b, System.currentTimeMillis()
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
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
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
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
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
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1620, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(100*1000);
	}
	
	public static void heartBeat() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(heartBeat);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1102, System.currentTimeMillis()
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
		JSONObject json=new JSONObject(switch_device_state2);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1630, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(100*1000);
	}
	
	public static void switch_profileSet() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(switch_profileSet);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1618, System.currentTimeMillis()
				/ 1000 % 86400 * 10000 + count + "_15", json);
		msg1.writeBytesToSock2(sock);
		System.out.println("Send:"+msg1.toString());

		Message msg = CtrolSocketServer.readFromClient(sock);
		if (msg != null)
			//System.out.println("Recv:"+msg.toString());		
		Thread.sleep(100*1000);
	}
	
	public static void get_room_list() throws JSONException, UnknownHostException, IOException, InterruptedException {
		count++;
		JSONObject json=new JSONObject(get_room_list);
		//json.put("sender", 1) ; //      "sender":    中控:0 ; 手机:1 ; 设备控制服务器:2;
		//json.put("receiver", 2) ;
		Message msg1 = new Message((short) 0x1638, System.currentTimeMillis()
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
		Message msg1 = new Message((short) 0x162a, System.currentTimeMillis()
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
		JSONObject json=new JSONObject(warn_msg2);
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
	
	
		
    private static void get_profile_template_list() throws JSONException, InterruptedException, IOException
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
