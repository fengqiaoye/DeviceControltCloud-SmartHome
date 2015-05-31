package cooxm.devicecontrol.control;
/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.device.Device;
import cooxm.devicecontrol.device.DeviceMap;
import cooxm.devicecontrol.device.DeviceState;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.device.ProfileMap;
import cooxm.devicecontrol.device.ProfileSet;
import cooxm.devicecontrol.device.ProfileSetMap;
import cooxm.devicecontrol.device.ProfileTemplate;
import cooxm.devicecontrol.device.Room;
import cooxm.devicecontrol.device.RoomMap;
import cooxm.devicecontrol.device.Trigger;
import cooxm.devicecontrol.device.TriggerMap;
import cooxm.devicecontrol.device.TriggerTemplate;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.socket.SocketClient;
import cooxm.devicecontrol.synchronize.IRFileDownload;
import cooxm.devicecontrol.synchronize.IRMatch2;
import cooxm.devicecontrol.util.MySqlClass;
import redis.clients.jedis.Jedis;

public class LogicControl {	
	
	public static final short COMMAND_START            		   =  0x1600;
	public static final short COMMAND_END            		   =  0x19FF;

	public static final short WARNING_START            		   =  0x2000;
 	public static final short WARNING_END					   =  0x21FF;
	
	public static final short COMMAND_ACK_OFFSET       		   =  0x4000; 
	
    /*** 请求 情景模式命令    @see get_room_profile() */
	private static final short GET_ROOM_PROFILE					=	COMMAND_START+1;	
    /*** 请求 情景模式的回复    @see get_room_profile_ack() */
	private static final short GET_ROOM_PROFILE_ACK     		=   COMMAND_START+1 + COMMAND_ACK_OFFSET;	
	
    /*** 设置 情景模式   @see set_room_profile()  */
	private static final short SET_ROOM_PROFILE					=	COMMAND_START+2;	
    /*** 设置 情景模式的回复   @see set_room_profile_ack()  */
	private static final short SET_ROOM_PROFILE_ACK	    		=	COMMAND_START+2+COMMAND_ACK_OFFSET;

    /*** 删除 情景模式   @see delete_room_profile()  */
	private static final short DELETE_ROOM_PROFILE				=	COMMAND_START+3;	
    /*** 删除 情景模式的回复   @see set_room_profile_ack()  */
	private static final short DELETE_ROOM_PROFILE_ACK	    	=	COMMAND_START+3+COMMAND_ACK_OFFSET;
	
	/*** 中控切换情景模式命令 */
	public static final short SWITCH_ROOM_PROFILE				=	COMMAND_START+4;
	/*** 中控切换情景模式命令 的回复 */
	private static final short SWITCH_ROOM_PROFILE_ACK			=	COMMAND_START+4+COMMAND_ACK_OFFSET;
	
    /*** 请求 一个用户家里所有情景模式    @see get_room_profile() */
	private static final short GET_ALL_PROFILES					=	COMMAND_START+5;	
    /*** 请求一个用户家里所的情景模式 回复    @see get_room_profile_ack() */
	private static final short GET_ALL_PROFILE_ACK     		=   COMMAND_START+5 + COMMAND_ACK_OFFSET;	
	
    /*** 上报一个用户家里所有情景模式   @see set_room_profile()  */
	private static final short SET_ALL_PROFILES					=	COMMAND_START+6;	
    /*** 上报一个用户家里所有情景模式的回复   @see set_room_profile_ack()  */
	private static final short SET_ALL_PROFILE_ACK	    		=	COMMAND_START+6+COMMAND_ACK_OFFSET;
	
	/*** 请求 情景模式集 */	
	private static final short GET_RROFILE_SET					=	COMMAND_START+21;
	/*** 请求 情景模式集 的回复*/	
	private static final short GET_RROFILE_SET_ACK				=	COMMAND_START+21+COMMAND_ACK_OFFSET;
	
	/*** 设置 情景模式集*/
	private static final short SET_RROFILE_SET					=	COMMAND_START+22;
	/*** 设置 情景模式集 的回复*/
	private static final short SET_RROFILE_SET_ACK				=	COMMAND_START+22+COMMAND_ACK_OFFSET;

	/*** 删除 情景模式集*/
	private static final short DELETE_RROFILE_SET				=	COMMAND_START+23;
	/*** 删除 情景模式集 的回复*/
	private static final short DELETE_RROFILE_SET_ACK			=	COMMAND_START+23+COMMAND_ACK_OFFSET;
	
	/*** 情景模式集切换 */
	public  static final short SWITCH_RROFILE_SET				=	COMMAND_START+24;	
	/*** 情景模式集切换 的回复*/
	private static final short SWITCH_RROFILE_SET_ACK			=	COMMAND_START+24+COMMAND_ACK_OFFSET;

	/*** 请求情景模式模板 */
	private static final short GET_RROFILE_TEMPLATE				=	COMMAND_START+25;	
	/*** 请求情景模式模板 的回复*/
	private static final short GET_RROFILE_TEMPLATE_ACK			=	COMMAND_START+25+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发 情景模板 */
	private static final short SET_RROFILE_TEMPLATE				=	COMMAND_START+26;	
	/*** 上报或者下发 情景模板 回复*/
	private static final short SET_RROFILE_TEMPLATE_ACK			=	COMMAND_START+26+COMMAND_ACK_OFFSET;
	
	/*** 请求一个用户家里所有 情景模式集 */	
	private static final short GET_ALL_RROFILE_SET					=	COMMAND_START+27;
	/*** 请求一个用户家里所有 情景模式集 的回复*/	
	private static final short GET_ALL_RROFILE_SET_ACK				=	COMMAND_START+27+COMMAND_ACK_OFFSET;
	
	/*** 上报一个用户家里所有 情景模式集*/
	private static final short SET_ALL_RROFILE_SET					=	COMMAND_START+28;
	/*** 上报一个用户家里所有 情景模式集 的回复*/
	private static final short SET_ALL_RROFILE_SET_ACK				=	COMMAND_START+28+COMMAND_ACK_OFFSET;
	
	/*** 请求一个家电*/
	private static final short GET_ONE_DEVICE				=	COMMAND_START+41;
	/*** 请求家电列表 的回复*/
	private static final short GET_ONE_DEVICE_ACK			=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电*/
	private static final short SET_ONE_DEVICE				=	COMMAND_START+42;
	/*** 设置 家电列表 的回复*/
	private static final short SET_ONE_DEVICE_ACK			=	COMMAND_START+42+COMMAND_ACK_OFFSET;	

	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_DEVICE			=	COMMAND_START+43;
	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_DEVICE_ACK		=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
	/*** 切换某个家电状态*/
	public static final short SWITCH_DEVICE_STATE		    =	COMMAND_START+44;
	/*** 切换某个家电状态 的回复*/
	private static final short SWITCH_DEVICE_STATE_ACK		=	COMMAND_START+44+COMMAND_ACK_OFFSET;
	
	/*** 请求一个用户家里所有家电列表*/
	private static final short GET_ALL_DEVICE				=	COMMAND_START+45;
	/*** 请求一个用户家里所有家电列表 的回复*/
	private static final short GET_ALL_DEVICE_ACK			=	COMMAND_START+45+COMMAND_ACK_OFFSET;	
	
	/*** 上报一个用户家里所有家电列表*/
	private static final short SET_ALL_DEVICE				=	COMMAND_START+46;
	/*** 上报一个用户家里所有家电列表 的回复*/
	private static final short SET_ALL_DEVICE_ACK			=	COMMAND_START+46+COMMAND_ACK_OFFSET;
	
	/*** 请求家电列表*/
	private static final short GET_ONE_ROOM				=	COMMAND_START+51;
	/*** 请求家电列表 的回复*/
	private static final short GET_ONE_ROOM_ACK			=	COMMAND_START+51+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电列表*/
	private static final short SET_ONE_ROOM				=	COMMAND_START+52;
	/*** 设置 家电列表 的回复*/
	private static final short SET_ONE_ROOM_ACK			=	COMMAND_START+52+COMMAND_ACK_OFFSET;	

	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_ROOM			=	COMMAND_START+53;
	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_ROOM_ACK		=	COMMAND_START+53+COMMAND_ACK_OFFSET;
	
	/*** 请求家电列表*/
	private static final short GET_ALL_ROOM				=	COMMAND_START+54;
	/*** 请求家电列表 的回复*/
	private static final short GET_ALL_ROOM_ACK			=	COMMAND_START+54+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电列表*/
	private static final short SET_ALL_ROOM				=	COMMAND_START+55;
	/*** 设置 家电列表 的回复*/
	private static final short SET_ALL_ROOM_ACK			=	COMMAND_START+55+COMMAND_ACK_OFFSET;
	
	/*** 请求触发规则模板 */
	private static final short GET_TRIGGER_TEMPLATE				=	COMMAND_START+61;	
	/*** 请求触发规则模板 的回复*/
	private static final short GET_TRIGGER_TEMPLATE_ACK			=	COMMAND_START+61+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则模板 */
	private static final short SET_TRIGGER_TEMPLATE				=	COMMAND_START+62;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short SET_TRIGGER_TEMPLATE_ACK			=	COMMAND_START+62+COMMAND_ACK_OFFSET;
	
	/*** 请求触发规则模板 */
	private static final short GET_TRIGGER				=	COMMAND_START+71;	
	/*** 请求触发规则模板 的回复*/
	private static final short GET_TRIGGER_ACK			=	COMMAND_START+71+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则模板 */
	private static final short SET_TRIGGER				=	COMMAND_START+72;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short SET_TRIGGER_ACK			=	COMMAND_START+72+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则模板 */
	private static final short DELETE_TRIGGER				=	COMMAND_START+73;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short DELETE_TRIGGER_ACK			=	COMMAND_START+73+COMMAND_ACK_OFFSET;
	
	/**断网重新联网时， 同步 时间表 */
	private static final short SYN_UPDATETIME               =   COMMAND_START+81;
	private static final short SYN_UPDATETIME_ACK			=	COMMAND_START+81+COMMAND_ACK_OFFSET;
	
	/**请求遥控 文件 */
	private static final short DOWNLOAD_INFRARED_FILE       =   COMMAND_START+91;
	private static final short DOWNLOAD_INFRARED_FILE_ACK	=	COMMAND_START+91+COMMAND_ACK_OFFSET;
	
	/**上传学习到的红外码，即这些红外码不在我们的红外码库中*/
	private static final short UPLOAD_INFRARED_LEARN       =   COMMAND_START+92;
	private static final short UPLOAD_INFRARED_LEARN_ACK   =   COMMAND_START+92+COMMAND_ACK_OFFSET;
	
	/**上传的扑捉到红外码值 ，用来识别遥控器的型号*/
	private static final short RECOGNIZE_INFRARED_CODE       =   COMMAND_START+93;
	private static final short RECOGNIZE_INFRARED_CODE_ACK   =   COMMAND_START+93+COMMAND_ACK_OFFSET;
	
    /*** 告警消息   */
	private static final short WARNING_MSG				 	=	WARNING_START+3;
    /*** 告警消息  的回复  */
	private static final short WARNING_MSG_ACK				=	WARNING_START+3+COMMAND_ACK_OFFSET;	
	
	
	/***********************  ERROR CODE :-50000  :  -59999 ************/
	private static final int SUCCESS                  =	0;
	private static final int RECEIVED                 = -50000;
	
	/** 情景模式陈旧*/
	private static final int PROFILE_OBSOLETE         =	-50001;	
	/** 情景模式不存在*/
	private static final int PROFILE_NOT_EXIST        = -50002;	
	
	private static final int PROFILE_SET_OBSOLETE     =	-50003;	
	private static final int PROFILE_SET_NOT_EXIST    = -50004;	
	
	private static final int PROFILE_TEMPLATE_OBSOLETE     =	-50005;	
	private static final int PROFILE_TEMPLATE_NOT_EXIST    = -50006;
	
	private static final int DEVICE_OBSOLETE   	  	  = -50011;
	private static final int DEVICE_NOT_EXIST   	  = -50012;
	private static final int DEVICE_STATE_EMPTY   	  = -50013;
	
	/*** 消息可以识别，但是收件人错误，例如收到自己发送的消息*/
	private static final int COMMAND_NOT_ENCODED   	  = -50021;
	/** 命令超时没有响应*/
	public static final int TIME_OUT		   	      = -50022;
	/**命令号码段不对*/
	public static final int WRONG_COMMAND		   	  = -50023;
	
	/** 红外码库文件不存在*/
	public static final int INFRARED_FILE_NOT_EXIST	  = -50031;
	/** 红外码库文件不存在*/
	public static final int INFRARED_CODE_NOT_RECOGNIZED = -50032;
	
	/** 下载红外码库时 没有告知家电类型*/
	public static final int UNKNOWN_DEVICE_TYPE = -50033;
	
	/** 服务器没有通过认证*/
	public static final int SERVER_NOT_AUTHORIZED	  = -50041;
	
	/** 服务器没有通过认证*/
	public static final int IN_PROCESS_PLEASE_WAIT	  = -50051;
	
	/** JSON格式错误，导致解析失败*/
	public static final int JSON_PARSE_ERROR	     = -50061;
	
	/** 时间戳格式错误，导致解析失败,正确的格式为：yyyy-MM-dd HH:mm:ss*/
	public static final int TIME_PARSE_ERROR	     = -50062;
	
	/** 时间戳格式错误，导致解析失败,正确的格式为：yyyy-MM-dd HH:mm:ss*/
	public static final int SQL_ERROR	     = -50063;
	
	/** 时间戳格式错误，导致解析失败,正确的格式为：yyyy-MM-dd HH:mm:ss*/
	public static final int ROOM_NOT_EXIST	     = -50071;
	
	
	

	

	/***********************   resource needed   ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	static Configure config=null;
	static MySqlClass mysql=null;
	SocketClient msgSock=null;
	Jedis jedis=null;// new Jedis("172.16.35.170", 6379,200);
	public static ProfileMap profileMap =null;
	ProfileSetMap profileSetMap =null;
	DeviceMap deviceMap=null;
	TriggerMap triggerMap=null;
	RoomMap roomMap=null;
	String ir_file_path;//=new File(cf.getValue("ir_file_path"));
	private final static String currentProfile= "currentProfile";
	private final static String currentProfileSet= "currentProfileSet";
	private final static String currentDeviceState= "currentDeviceState";	
	private final static String commandQueue= "commandQueue";
	
    //public LogicControl() {}
    
    public LogicControl(Configure cf) {
    	log.info("Starting logic control module ... ");
    	this.config=cf;
		String mysql_ip			=cf.getValue("mysql_ip");
		String mysql_port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String mysql_database	=cf.getValue("mysql_database");		
		String redis_ip         =cf.getValue("redis_ip");
		int redis_port       	=Integer.parseInt(cf.getValue("redis_port"));	
		String msg_server_IP    =cf.getValue("msg_server_IP");
		int msg_server_port     =Integer.parseInt(cf.getValue("msg_server_port"));
		int cluster_id          =Integer.parseInt(cf.getValue("cluster_id"));
		int server_id           =Integer.parseInt(cf.getValue("server_id"));
		
		ir_file_path=cf.getValue("ir_file_path");
		
		
		mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		this.jedis= new Jedis(redis_ip, redis_port,200);
		try{
//	    	ConnectThread th=new ConnectThread(msg_server_IP, msg_server_port, 1, 6, 201,false);
//	    	th.start();	
			
//			this.msgSock=new SocketClient(msg_server_IP, msg_server_port, cluster_id, 6, 201, false);	
//			new Thread((Runnable) this.msgSock).start();
//			log.info("Successfull connect to msg Server: "+msg_server_IP+":"+msg_server_port);
	 	
			this.profileMap= new ProfileMap(mysql);
			this.profileSetMap= new ProfileSetMap(mysql);
			this.deviceMap=new DeviceMap(mysql);
			this.triggerMap=new TriggerMap(mysql);
			this.roomMap=new RoomMap(mysql);
		} catch (SQLException  e) {
			e.printStackTrace();
		}
		log.info("Initialization of map successful :  profileMap size="+profileMap.size()
				+";profileSetMap size="+profileMap.size()
				+"; deviceMap size="+deviceMap.size()
				+"; roomMap size="+roomMap.size()
				);
		log.info("Initialization of Logic control module finished. ");
	}
    
  public static MySqlClass  getMysql(){
	  if(mysql==null ||mysql.isClosed()){		  
		String mysql_ip			=config.getValue("mysql_ip");
		String mysql_port		=config.getValue("mysql_port");
		String mysql_user		=config.getValue("mysql_user");
		String mysql_password	=config.getValue("mysql_password");
		String mysql_database	=config.getValue("mysql_database");			
		mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
	  }else{
		return mysql;
	  }
	return mysql;
    }
	
	public  void decodeCommand(Message msg) {		
		int commandID=msg.getCommandID();
			
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
			get_room_profile(msg);
			break;
		case SET_ROOM_PROFILE:
			set_room_profile(msg); 
			break;	
		case DELETE_ROOM_PROFILE:
			delete_room_profile(msg);
			break;
		case SWITCH_ROOM_PROFILE:	
			switch_room_profile(msg);
			break;
		case GET_ALL_PROFILES:			
			get_all_profile(msg);
			break;
		case SET_ALL_PROFILES:
			set_all_profile(msg); 
			break;	
		case GET_RROFILE_SET:	
			get_profile_set(msg);
			break;
		case SET_RROFILE_SET:	
			set_profile_set(msg);
			break;	
		case DELETE_RROFILE_SET:
			delete_room_profile(msg);
			break;
		case SWITCH_RROFILE_SET:	
			switch_profile_set(msg);
			break;
		case GET_ALL_RROFILE_SET:	
			get_all_profile_set(msg);
			break;
		case SET_ALL_RROFILE_SET:	
			set_all_profile_set(msg);
			break;
		case GET_RROFILE_TEMPLATE: 	
			get_profile_template(msg);
			break;
		case SET_RROFILE_TEMPLATE:	
			set_profile_template(msg);
			break;	
		case GET_ONE_DEVICE:	
			get_one_device(msg);;
			break;
		case SET_ONE_DEVICE:	
			set_one_device(msg, mysql);;
			break;	
		case DELETE_ONE_DEVICE:
			delete_one_device(msg);
			break;			
		case SWITCH_DEVICE_STATE:	
			switch_device_state(msg);
			break;
		case GET_ALL_DEVICE:	
			get_all_device(msg);;
			break;
		case SET_ALL_DEVICE:	
			set_all_device(msg, mysql);;
			break;	
		case GET_ONE_ROOM:	
			get_one_room(msg);;
			break;
		case SET_ONE_ROOM:	
			set_one_room(msg, mysql);;
			break;	
		case DELETE_ONE_ROOM:
			delete_one_room(msg);
			break;	
		case GET_ALL_ROOM:	
			get_all_room(msg);
			break;
		case SET_ALL_ROOM:	
			set_all_room(msg, mysql);;
			break;
		case WARNING_MSG:	
			warning_msg(msg);
			break;
		case WARNING_MSG_ACK:	
			warning_msg_ack(msg);
			break;
		case GET_TRIGGER_TEMPLATE:	
			get_trigger_template(msg);
			break;
		case SET_TRIGGER_TEMPLATE:	
			set_trigger_template(msg);
			break;	
		case GET_TRIGGER:	
			get_trigger(msg);
			break;
		case SET_TRIGGER:	
			set_trigger(msg);
			break;	
		case DELETE_TRIGGER:
			delete_trigger(msg);
			break;
		case SYN_UPDATETIME:	
			syn_updatetime(msg);
			break;
		case DOWNLOAD_INFRARED_FILE:	
			download_infrared_file(msg);
			break;
		case RECOGNIZE_INFRARED_CODE:
			recognize_infrared_code(msg);
			break;
		default:
			int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().optInt("sender");
			}
			JSONObject json=msg.getJson();
			try {
				json.put("sender",2);
				json.put("receiver",sender); 

				if(msg.isValid()){
					log.info("Valid command receive,but commandID can't be recognized. SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
					json.put("errorCode", LogicControl.COMMAND_NOT_ENCODED);
	            }else{
	            	log.info("Invalid command receive. SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
				    json.put("errorCode", LogicControl.WRONG_COMMAND);          
	            }
			} catch (JSONException e1) {
				e1.printStackTrace();
				try {
					json.put("errorCode",JSON_PARSE_ERROR);
					json.put("errorDescription",e1.getCause().getMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			msg.setCommandID((short) (msg.getCommandID()+ LogicControl.COMMAND_ACK_OFFSET));
			msg.setJson(json);
			try {
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}		
	}
	


	/*** 请求查询情景模式
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   profileID:7654321,
     *   profile: 
     *         {
     *          情景模式的json格式 
     *         }
     * }
     *                      
     */
    public void get_room_profile(Message msg) {
    	Profile profile=null;
    	int ctrolID;
		JSONObject json= new JSONObject();
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileID=msg.getJson().getInt("profileID");

	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
	    	String key=ctrolID+"_"+profileID;
	    	if( (profile= profileMap.get(key))!=null  || (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
	    		json.put("profile", profile.toJsonObj());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_profile ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}
			json.put("sender",2);
			json.put("receiver",sender);  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		msg.setJson(json);
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
    	//System.out.println("get room profile result:"+msg.toString());
    }
     
    /*** 保存或者上传一个情景模式
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该情景模式，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间晚于云端，则将上报的profile保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间早于云端，则需要将云端的情景模式下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的prifile的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "ctrolID":12345677
     *  "profile":
     *   {
			"profileID":123456789,
			"ctrolID":12345677,
			"profileName":"未知情景",
			"profileSetID":12345,
			"profileTemplateID":0,
			"roomID":203,
			"roomType":2,
			"factorList":
			[
				{"factorID":40,"minValue":20,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"2014-12-13 14:15:17","maxValue":1
				}
			],
			"modifyTime":"2014-12-13 14:15:17",
			"createTime":"2014-12-13 14:15:17"
		}
	  }
     * @throws JSONException 
     * @throws ParseException 
	*/
    public void set_room_profile( Message msg)  {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile msgProfile;
		try {
			msgProfile = new Profile(msg.getJson().getJSONObject("profile"));
	    	Profile dbProfile;
	    	int ctrolID=msgProfile.getCtrolID();
	    	int profileID=msgProfile.getProfileID();
	    	Date msgModifyTime=msgProfile.getModifyTime();
	    	String key=ctrolID+"_"+profileID;
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if( (dbProfile=this.profileMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
				json.put("errorCode",PROFILE_OBSOLETE);    	
				log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" profileID:"+profileID+".");
	    	}else { //云端较旧  或者 不存在，则保存
	    		Profile p=this.profileMap.put(key, msgProfile);
				if(p!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}
			}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setJson(json);
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除情景模式
     * <pre>传入的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
           
     */
    public void delete_room_profile(Message msg) {
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileID=msg.getJson().getInt("profileID");
	    	String key=ctrolID+"_"+profileID;
	    	int sender=0;
			if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(profileMap.containsKey(key)){
	    		profileMap.remove(key);
	    		msg.setJson(new JSONObject());
	    		json.put("errorCode", SUCCESS);    		
	    	}else if((Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
	    		Profile.deleteFromDB(mysql, ctrolID, profileID);
	    		json.put("errorCode", SUCCESS);
	    	}else {
				log.error("room_profile not exist ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");			
				//msg.setJson()new JSONObject();
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
    
    /*** 请求切换情景模式,根据命令的发送方有不同的响应方式
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
    *   ctrolID:1234567
    *   roomID: 203
    *   profileID:7654321
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile(final Message msg){
    	JSONObject json=new JSONObject();
    	Message replyMsg=new Message(msg);
    	Profile profile=null;
    	int ctrolID;
    	int sender=0;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileID=msg.getJson().getInt("profileID");

	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);	
			json.put("receiver",sender); 
	    	String key=ctrolID+"_"+profileID;
	    	if((profile= profileMap.get(key))!=null || (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
	    		int roomID=profile.getRoomID();
	    		String command=profile.getCtrolID()+","+msg.getCommandID()+","+profile.getRoomType()+","+profile.getRoomID()+","+profileID;
	    		jedis.publish(commandQueue,command);
	        	String key2=ctrolID+"_currentProfile";
	    		jedis.hset(key2, roomID+"", profile.toJsonObj().toString());
	    		if(sender==0){
	    			json.put("errorCode",SUCCESS);
	    		}else {
	    			TimeOutTread to=new TimeOutTread(1,msg);
	    			to.start();   	
	    			//json.put("errorCode", IN_PROCESS_PLEASE_WAIT);
	    			return;
	    		}
	    	}else {
				log.error("Can't switch room profile,profile doesn't exist. ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}
		replyMsg.setCommandID(SWITCH_ROOM_PROFILE_ACK);
		replyMsg.setJson(json);
		try {
			CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

    	//2015-05-27 收到切换请求暂时不回复，等待中控的回复。
    	/*replyMsg.setCommandID(SWITCH_ROOM_PROFILE_ACK);
    	json.put("sender",2);
    	json.put("receiver",sender);
    	json.put("originalSenderRole", sender);
		replyMsg.setJson(json);
    	CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);*/
    }
    
	/*** 请求查询一个用户家里所有情景模式
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   profileArray: 
     *         [  { 情景模式的json格式      },
     *            { 情景模式的json格式      },
     *            { 情景模式的json格式      },
     *         ]
     * }                 
     */
    public void get_all_profile(Message msg) {
		JSONObject json= new JSONObject();
    	List<Profile> profileList=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);	
			json.put("receiver",sender); 
			profileMap.size();
	    	if( (profileList= profileMap.getProfilesByctrolID(ctrolID)).size()!=0  ){
	    		JSONArray ja=new JSONArray();
	    		for (Profile profile : profileList) {
					ja.put(profile.toJsonObj());
				}
	    		json.put("profileArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_all_profile by ctrolID:"+ctrolID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

 
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        msg.setJson(json);
  		msg.setCommandID(GET_ALL_PROFILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
     
    /*** 保存或者上传一个情景模式
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该情景模式，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间晚于云端，则将上报的profile保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间早于云端，则需要将云端的情景模式下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的prifileArray的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "profileArray":[
     *                 {profile1的jason格式	},
     *                 {profile2的jason格式	},
     *                 ....
     *                 ]
	  }
     * @throws ParseException 
	*/
    public void set_all_profile( Message msg) {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//Profile msgProfile=new Profile(msg.getJson().getJSONObject("profile"));
    	Profile dbProfile;
    	int ctrolID;

		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 

			JSONArray ja=msg.getJson().getJSONArray("profileArray");		
			for (int i=0;i<ja.length();i++) {
				Profile msgProfile = new Profile(ja.getJSONObject(i));
		    	int profileID=msgProfile.getProfileSetID();
		    	String key=ctrolID+"_"+profileID;
		    	Date msgModifyTime=msgProfile.getModifyTime();    
		    	if( (dbProfile=this.profileMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
		    		log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" profileID:"+profileID+".");
					json.put("errorCode",PROFILE_OBSOLETE);    		
		    	}else { //云端较旧  或者 不存在，则保存
		    		Profile p=profileMap.put(key, msgProfile);
		    		if(p!=null){
					   json.put("errorCode",SUCCESS);   
		    		}else{
		    			json.put("errorCode",SQL_ERROR);  
		    			break;
		    		}
				}    		
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setJson(json);
  		msg.setCommandID(SET_ALL_PROFILE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 请求切换情景模式,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ PROFILE_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(1,msg);
		to.start();
    }
    
    /*** 查询情景模式集
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
     *   ctrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50004}
     *   （2）如果查询的情景模式集存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   profileSet: 
     *         {
     *          情景模式集的json格式 
     *         }
     * }              
     */
    public void get_profile_set(Message msg) {
    	JSONObject json=new JSONObject();
    	ProfileSet profileSet=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int profileSetID=msg.getJson().getInt("profileSetID");
	    	String key=ctrolID+"_"+profileSetID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
	    	if((profileSet=profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
	    		json.put("profileSet", profileSet.toJsonObj());
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_profile_set, ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_SET_NOT_EXIST);
	    	}



		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setCommandID( GET_RROFILE_SET_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    

	
	/*** 设置 情景模式集

	 * <pre>Json格式和 设置情景模式 和 {@link cooxm.devicecontrol.control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：

     * { 
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务 
     *  "ctrolID":12345677
     *   profileSet:
     *   {  
     *      情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *      profileArray:[
     *      				{  profile1 },
     *        				{  profile2 }
     *                   ]
     *   }  
     * }
	 * */
	public void set_profile_set(Message msg) {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	ProfileSet msgProfileSet;
		try {
			msgProfileSet = new ProfileSet(msg.getJson().getJSONObject("profileSet"));
	    	ProfileSet dbProfileSet;
	    	int ctrolID=msgProfileSet.getCtrolID();
	    	int profileSetID=msgProfileSet.getProfileSetID();
	    	Date msgModifyTime=msgProfileSet.getModifyTime();
	    	String key=ctrolID+"_"+profileSetID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 
	    	if((dbProfileSet=profileSetMap.get(key))==null &&  dbProfileSet.getModifyTime().before(msgModifyTime)){
	    		ProfileSet ps=profileSetMap.put(key, msgProfileSet);
	    		JSONArray ja=msg.getJson().getJSONObject("profileSet").getJSONArray("profileArray");
	    		for (int i=0;i< ja.length();i++) {
					Profile p=new Profile(ja.getJSONObject(i));
					String key2=ctrolID+"_"+p.getProfileID();
					Profile p2=profileMap.put(key2, p);
					if(p2!=null){
						json.put("errorCode",SUCCESS); 
					}else{
						json.put("errorCode",SQL_ERROR); 
						break;
					}
				}
				if(ps!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}  		
	    	}else if( dbProfileSet.getModifyTime().after(msgModifyTime)){	//云端较新  
	    		log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" profileID:"+profileSetID+".");
				json.put("errorCode",PROFILE_SET_OBSOLETE);    		
	    	}   	


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
  		msg.setCommandID(SET_RROFILE_SET_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 	
	}
	
    /*** 删除情景模式集
     * <pre>传入的json格式为：
     * { 
     *   ctrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_profile_set(Message msg) {
    	JSONObject json=new JSONObject();
    	//Profile profile=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileSetID=msg.getJson().getInt("profileSetID");
	    	String key=ctrolID+"_"+profileSetID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	if(profileSetMap.containsKey(key)){
	    		profileSetMap.remove(key);
	    		json.put("errorCode", SUCCESS);    		
	    	}else if((ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
	    		ProfileSet.deleteProfileSetFromDB(mysql, ctrolID, profileSetID);
	    		json.put("errorCode", SUCCESS);
	    	}else {
				log.error("room_profileSet not exist ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
				json.put("errorCode",PROFILE_SET_NOT_EXIST);
	    	}
	    	msg.setCommandID( DELETE_RROFILE_SET_ACK);
			json.put("sender",2);
			json.put("receiver",sender); 
			msg.setJson(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	

	
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     sender:"control"/"mobile"/"cloud"
	 *     ctrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void switch_profile_set(Message msg){
    	JSONObject json=new JSONObject();
    	Message replyMessage=new Message(msg);
    	ProfileSet profileSet=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int profileSetID=msg.getJson().getInt("profileSetID"); 
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}     	
	    	//String key=ctrolID+"_"+profileSetID;
	    	String key=ctrolID+"_currentProfile";
	    	if((profileSet= profileSetMap.get(key))!=null|| (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
	    		String command=profileSet.getCtrolID()+","+msg.getCommandID()+","+254+","+254+","+profileSetID;
	    		jedis.publish(commandQueue, command);
	    		Profile profile=null;
	    		for (int i = 0; i < profileSet.getProfileList().size(); i++) {
	    			String setKey=ctrolID+"_"+profileSet.getProfileList().get(i);
	        		profile=profileMap.get(setKey); 
	        		if(profile!=null){
	    			 jedis.hset(key, profile.getRoomID()+"", profile.toJsonObj().toString());
	    			  //jedis.hset(currentProfileSet, key, profileSet.toJsonObj().toString());
	        		}else{
	        			json.put("errorCode",PROFILE_NOT_EXIST);  
	        		}
				}
				json.put("sender",2);
				json.put("receiver",sender);
	    		if(sender==0){
		    		json.put("errorCode",SUCCESS);
		    		
	    		}else {
	    			TimeOutTread to=new TimeOutTread(1,msg);
	    			to.start();  	
	    			//json.put("errorCode", IN_PROCESS_PLEASE_WAIT);
	    			return;
	    		}
	    	}else {
				log.error("Can't switch room profileSet,profileSet doesn't exit. ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender);

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		replyMessage.setJson(json);
		replyMessage.setCommandID( SWITCH_RROFILE_SET_ACK);
		try {
			CtrolSocketServer.sendCommandQueue.offer(replyMessage, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}		
	}
	
    /*** 请求切换某个情景模式,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_set_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(1,msg);
		to.start();
    }
	
	/*** 请求查询一个用户家里所有情景模式
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   profileSetArray: 
     *         [  { 情景集的json格式      },
     *            { 情景集的json格式      },
     *            { 情景集的json格式      },
     *         ]
     * }                 
     */
    public void get_all_profile_set(Message msg) {
    	List<ProfileSet> profileSetList=null;
		JSONObject json= new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	//int profileID=msg.getJson().getInt("profileSetID");

	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);	
			json.put("receiver",sender); 
	    	if( (profileSetList= profileSetMap.getProfileSetsByctrolID(ctrolID)).size()!=0  ){
	    		JSONArray ja=new JSONArray();
	    		for (ProfileSet profile : profileSetList) {
					ja.put(profile.toJsonObj());
				}
	    		json.put("profileSetArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_profileSet by ctrolID:"+ctrolID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(GET_ALL_RROFILE_SET_ACK);
    	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
     
    /*** 保存或者上传一个情景模式
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该情景模式，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间晚于云端，则将上报的profile保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间早于云端，则需要将云端的情景模式下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的prifileArray的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "profileSetArray":[
     *                 {profile1的jason格式	},
     *                 {profile2的jason格式	},
     *                 ....
     *                 ]
	  }
     * @throws ParseException 
	*/
    public void set_all_profile_set( Message msg) {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//Profile msgProfile=new Profile(msg.getJson().getJSONObject("profile"));
    	ProfileSet dbProfile;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
			JSONArray ja=msg.getJson().getJSONArray("profileSetArray");		
			for (int i=0;i<ja.length();i++) {
				ProfileSet msgProfile = new ProfileSet(ja.getJSONObject(i));
		    	int profileSetID=msgProfile.getProfileSetID();
		    	Date msgModifyTime=msgProfile.getModifyTime();		
				String key=ctrolID+"_"+profileSetID;
				
		    	if( (dbProfile=this.profileSetMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
		    		log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" profileID:"+profileSetID+".");
					json.put("errorCode",PROFILE_OBSOLETE);    		
		    	}else { //云端较旧  或者 不存在，则保存	    		
		    		ProfileSet p=this.profileSetMap.put(key, msgProfile);
					if(p!=null){
						json.put("errorCode",SUCCESS); 
					}else{
						json.put("errorCode",SQL_ERROR); 
						break;
					}
				}    		
			}    	



		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
  		msg.setCommandID(SET_ALL_RROFILE_SET_ACK);
    	msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
	
    /*** 请求切换情景模式集,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_profile_set_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(1,msg);
		to.start();
    }
    
    /*** 请求查询情景模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   profileTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX_NOT_EXIST}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   profileTemplateID:7654321,
     *   profileTemplate: 
     *         {
     *          情景模式模板的json格式 
     *         }
     * }
     *                      
     */
    public void get_profile_template(Message msg) {
    	JSONObject json=new JSONObject();
    	ProfileTemplate profileTemplat=null;
    	int profileTemplatID;
		try {
			profileTemplatID = msg.getJson().getInt("profileTemplateID");

	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(  (profileTemplat=ProfileTemplate.getFromDB(mysql,  profileTemplatID))!=null){
	    		json.put("profileTemplate", profileTemplat.toJsonObj());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_profile_template, profileTemplatID:"+profileTemplatID+" from Mysql.");
				json.put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);
	    	}

 

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_RROFILE_TEMPLATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    
    /*** 请求情景模板,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void get_profile_template_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(1,msg);
		to.start();
    }
     
    /*** 保存或者上传一个情景模式
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该情景模式，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间晚于云端，则将上报的profile保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的profile的修改时间早于云端，则需要将云端的情景模式下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的prifile的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     * "ctrolID":12345677
     *  profileTemplat:
     *   {
			"profileTemplatID":123456789,
			"ctrolID":12345677,
			"profileName":"未知情景",
			"profileSetID":12345,
			"profileTemplateID":0,
			"roomID":203,
			"roomType":2,
			"factorList":
			[
				{"factorID":40,"minValue":20,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"2014-12-13 14:15:17","maxValue":1
				}
			],
			"modifyTime":"2014-12-13 14:15:17",
			"createTime":"2014-12-13 14:15:17"
		}
	  }
     * @throws ParseException 
	*/
    public void set_profile_template( Message msg) {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	ProfileTemplate msgProfile;
		try {
			msgProfile = new ProfileTemplate(msg.getJson().getJSONObject("profileTemplate"));
	    	ProfileTemplate dbProfile;
	    	int profileTemplatID=msgProfile.getProfileTemplateID();
	    	Date msgModifyTime=msgProfile.getModifyTime();
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}   
			json.put("sender",2);
			json.put("receiver",sender); 
			dbProfile=ProfileTemplate.getFromDB(mysql, profileTemplatID);
	    	if(dbProfile==null || ( dbProfile!=null && dbProfile.getModifyTime().before(msgModifyTime)) ){
				json.put("errorCode",SUCCESS);   
				int x=msgProfile.saveToDB(mysql);
				if(x>0){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}
	    	}else if(dbProfile!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
	    		log.error("Profile_template in Cloud is newer than from profile from user,  profileTemplatID:"+profileTemplatID+".");
				json.put("errorCode",PROFILE_TEMPLATE_OBSOLETE);    		
	    	}  	



		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
		msg.setJson(json);
  		msg.setCommandID(SET_RROFILE_TEMPLATE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 上传或者下发情景模板,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5;  分析服务器：6...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5;  分析服务器：6...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void set_profile_template_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(1,msg);
		to.start();
    }
    
    
	/*** 获取一个设备
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     deviceID:
     *   }
     *   @return List< Device > 加电列表 的json格式
	 * @throws JSONException 
     *   */
	public void get_one_device(Message msg) {
    	JSONObject json=new JSONObject();
    	Device device=new Device();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int deviceID=msg.getJson().getInt("deviceID");
	    	String key=ctrolID+"_"+deviceID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	if( (device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
	    		json.put("device", device.toJsonObj());
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_one_device, ctrolID:"+ctrolID+",deviceID: "+ deviceID+" from deviceMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
     * <pre>传入的json格式为：
     * { 
     *   ctrolID:1234567
     *   sender:0
     *   receiver:0
     *   device:
     *   { 
     *     ...
     *   }
     *   
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
	public void set_one_device(Message msg,MySqlClass mysql) {
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Device msgDevice;
		try {
			msgDevice = new Device(msg.getJson().getJSONObject("device"));
	    	Device dbDevice;
	    	int ctrolID=msgDevice.getCtrolID();
	    	int deviceID=msgDevice.getDeviceID();
	    	Date msgModifyTime=msgDevice.getModifyTime();
	    	String key=ctrolID+"_"+deviceID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if((dbDevice=this.deviceMap.get(key))==null  || dbDevice.modifyTime.before(msgModifyTime) ){	 //不存在或者云端较旧
	    		Device d=this.deviceMap.put(key, msgDevice);
				if(d!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}
	
				String key2=ctrolID+"_roomBind";
	    		int roomID=msgDevice.getRoomID();
	    		String command=msgDevice.getCtrolID()+","+msg.getCommandID()+","+msgDevice.getRoomType()+","+msgDevice.getRoomID()+","+deviceID;
	    		jedis.publish(commandQueue,command);
	    		jedis.hset(key2, deviceID+"", msgDevice.toJsonObj().toString());
	    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ //云端较新  
	    		log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" deviceID:"+deviceID+".");
				json.put("errorCode",DEVICE_OBSOLETE);   
			}



		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
  		msg.setCommandID(SET_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	
    /*** 删除情景模式集
     * <pre>传入的json格式为：
     * { 
     *   ctrolID:1234567
     *   deviceID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_one_device(Message msg){
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int deviceID=msg.getJson().getInt("deviceID");
	    	String key=ctrolID+"_"+deviceID;
	    	int sender=0;
	    	Device device;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 

			if((device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
	    		Device device2=deviceMap.remove(key);
	    		if(device2!=null)
	    		   json.put("errorCode", SUCCESS);    		
	    	}else {
				log.error("room_device not exist ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setCommandID( DELETE_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
	/*** 切换某个家电状态
	 * 	 <pre>例如对应json消息体如下格式 ：
	 *   {sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
     *    receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
	 *    ctrolID:1234567
	 *    roomID: 203
	 *    deviceID:201456135
	 *    deviceType: 填写factorID, 例如501	电视
     *    state:{
     *           设备状态的json格式；
     *          }
     *   }
	 * @throws JSONException 
	 * @throws SQLException */
	public void switch_device_state(Message msg) {
		JSONObject json=new JSONObject();
		Message replyMsg=new Message(msg);
    	Device device=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
			
			//device = new Device(msg.getJson().getJSONObject("device"));
			
	    	int deviceID=msg.getJson().getInt("deviceID");
	    	int deviceType=msg.getJson().getInt("deviceType");
	    	DeviceState state= new DeviceState();
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	json.put("sender",2);
	    	json.put("receiver",sender); 
	    	//String key=ctrolID+"_"+deviceID;
	    	String key=ctrolID+"_currentDeviceState";
	    	if((device= deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
	        	if(msg.getJson().has("state")){
	        		state=new DeviceState(msg.getJson().getJSONObject("state"));
	        		String command=device.getCtrolID()+","+msg.getCommandID()+","+device.getRoomType()+","+device.getRoomID()+","+deviceID+","+deviceType;
	        		jedis.publish(commandQueue, command);
	        		jedis.hset(key, deviceID+"", state.toJson().toString());
	        		//json.put("errorCode",SUCCESS); 	  
	        		if(sender==0){
	        			json.put("errorCode",SUCCESS);   		
	        		}else {
	        			TimeOutTread to=new TimeOutTread(1,msg);
	        			to.start();  
	        			//json.put("errorCode", IN_PROCESS_PLEASE_WAIT);
	        			return;
	        		}
	        	}else{
	        		json.put("errorCode",DEVICE_STATE_EMPTY); 	
	        	}
	
	    	}else {
				log.error("Can't switch room device,device doesn't exit. ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	replyMsg.setCommandID(SWITCH_DEVICE_STATE_ACK);
		replyMsg.setJson(json);
		try {
			CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/*** 获取一个用户家里所有设备
	 * 	 <pre>请求对应json消息体为：
	 *   {
	 *     ctrolID:1234567
     *   }
     *   @return List< Device > 加电列表 的json格式
     *   回复消息对应json消息体为：
     *   deviceArray:[
     *                 {device的json格式}
     *                 ...
     *               ]
     *   
	 * @throws JSONException 
     *   */
	public void get_all_device(Message msg) {
    	JSONObject json=new JSONObject();
    	List<Device> deviceList=new ArrayList<Device>();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	//int deviceID=msg.getJson().getInt("deviceID");
	    	//String key=ctrolID+"_"+deviceID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	if( (deviceList=deviceMap.getApplianceByctrolID(ctrolID)).size()!=0 ){
	    		JSONArray ja=new JSONArray();
	    		for (Device device : deviceList) {
					ja.put(device.toJsonObj());
				}
	    		json.put("deviceArray", ja);
	    		json.put("errorCode",SUCCESS); 
	    	}else {
				log.error("Can't get_one_device, ctrolID:"+ctrolID+" from deviceMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setCommandID( GET_ALL_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个用户家里所有家电
	 *   <pre>请求消息格式：
	 *   {
	 *    ctrolID:1234564
	 *   }
	 * 	 <pre>回复消息JOSN:* 
	 *   {
	 *   ctrolID:1234564
	 *   deviceArray:[
	 *                 {device的json格式}
	 *                 ....
	 *               ]
	 *     
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_all_device(Message msg,MySqlClass mysql) {
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//Device msgDevice=new Device(msg.getJson());
    	Device dbDevice;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	JSONArray ja=msg.getJson().getJSONArray("deviceArray");	
			for (int i=0;i<ja.length();i++) {
				Device msgDevice = new Device(ja.getJSONObject(i));  
		    	int deviceID=msgDevice.getDeviceID();
		    	Date msgModifyTime=msgDevice.getModifyTime();
		    	String key=ctrolID+"_"+deviceID;
		    	if((dbDevice=this.deviceMap.get(key))==null  || ( dbDevice!=null && dbDevice.modifyTime.before(msgModifyTime) ) ){	 //不存在或者云端较旧
		    		this.deviceMap.put(key, msgDevice);
					json.put("errorCode",SUCCESS);
		
					String key2=ctrolID+"_roomBind";
		    		int roomID=msgDevice.getRoomID();
		    		String command=msgDevice.getCtrolID()+","+msg.getCommandID()+","+msgDevice.getRoomType()+","+msgDevice.getRoomID()+","+deviceID;
		    		//jedis.publish(commandQueue,command);
		    		jedis.hset(key2, deviceID+"", msgDevice.toJsonObj().toString());
					//json.put("errorCode",SUCCESS);
		    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ //云端较新  
		    		log.error("device in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" deviceID:"+deviceID+",discard.");
					json.put("errorCode",DEVICE_OBSOLETE);   
				}
			}
	  		msg.setCommandID(SET_ALL_DEVICE_ACK);
			json.put("sender",2);
			json.put("receiver",sender); 
			msg.setJson(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	
	/*** 获取一个房间
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     roomID:101
     *   }
     *   @return List< Device > 加电列表 的jsonArray格式
     *   {
     *    ctrolID:1234567,   
     *	  room: {JSON }
     *   }
     *  
	 * @throws JSONException 
     *   */
	public void get_one_room(Message msg) {
    	JSONObject json=new JSONObject();
    	Room room=new Room();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int roomID=msg.getJson().getInt("roomID");
	    	String key=ctrolID+"_"+roomID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	if(  (room=Room.getRoomHeadFromDB(mysql, ctrolID, roomID))!=null){
	    		json.put("room", room.toJsonObject());
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_one_room, ctrolID:"+ctrolID+"roomID: "+ roomID+" from roomMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}
	    	msg.setCommandID( GET_ONE_ROOM_ACK);
			json.put("sender",2);
			json.put("receiver",sender); 
			msg.setJson(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
	 * 	 <pre>对应的json:* 
	 *   ctrolID:123456789
	 *   room:
	 *   {
	 *     将这个家电的jsonObject格式
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_one_room(Message msg,MySqlClass mysql) {
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Room msgRoom;
		try {
			msgRoom = new Room(msg.getJson().getJSONObject("room"));

	    	Room dbRoom;
	    	int ctrolID=msg.getJson().getInt("ctrolID");
	    	int roomID=msgRoom.getRoomID();
	    	Date msgModifyTime=msgRoom.getModifyTime();
	    	String key=ctrolID+"_"+roomID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	
	    	if((dbRoom=this.roomMap.get(key))==null  || (dbRoom!=null && dbRoom.getModifyTime().before(msgModifyTime) )){	 //不存在或者云端较旧
	    		Room r=this.roomMap.put(key, msgRoom);
	    		if(r!=null){
	    			json.put("errorCode",SUCCESS);
	    		}else{
	    			json.put("errorCode",SQL_ERROR);
	    			log.error("save Room to roomMap failed,room:"+dbRoom.toJsonObject().toString());
	    		}

	
				String key2=ctrolID+"_roomBind";
	    		//int roomID=msgRoom.getRoomID();
	    		String command=msgRoom.getCtrolID()+","+msg.getCommandID()+","+msgRoom.getRoomType()+","+msgRoom.getRoomID()+","+roomID;
	    		jedis.publish(commandQueue,command);
	    		jedis.hset(key2, roomID+"", msgRoom.toJsonObject().toString());
	    	}else if(dbRoom.getModifyTime().after(msgModifyTime)){ //云端较新  
	    		log.error("device in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" roomID:"+roomID+",discard.");
				json.put("errorCode",DEVICE_OBSOLETE);   
			}
	  		msg.setCommandID(SET_ONE_ROOM_ACK);
			json.put("sender",2);
			json.put("receiver",sender); 
			msg.setJson(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	
    /*** 删除情景模式集
     * <pre>传入的json格式为：
     * { 
     *   ctrolID:1234567
     *   roomID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_one_room(Message msg) {
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int roomID=msg.getJson().getInt("roomID");
	    	String key=ctrolID+"_"+roomID;
	    	int sender=0;
	    	Room room=new Room();
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			if((room=roomMap.get(key))!=null || (room=Room.getRoomHeadFromDB(mysql, ctrolID, roomID))!=null){
	    		roomMap.remove(key);
	    		json.put("errorCode", SUCCESS);    		
	    	}else {
				log.error("room_room not exist ctrolID:"+ctrolID+" roomID:"+roomID+" from roomMap or Mysql.");
				json.put("errorCode",ROOM_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setCommandID( DELETE_ONE_ROOM_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    
	/*** 获取一个用户家里所有房间
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
     *   }
     *   @return List< Device > 加电列表 的jsonArray格式
     *   {
     *   ctrolID:1234567,     *   
     *	 roomArray: [
     *               {JSON }
     *              ]
     *   }
	 * @throws JSONException 
     **/
	public void get_all_room(Message msg) {
    	JSONObject json=new JSONObject();
    	List<Room> roomList=new ArrayList<Room>();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}	
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(  (roomList=roomMap.getRoomsByctrolID(ctrolID)).size()!=0){
	    		JSONArray ja=new JSONArray();
	    		for (Room room : roomList) {
					ja.put(room.toJsonObject());
				}
	    		json.put("roomArray", ja);
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_all_room by ctrolID:"+ctrolID+""+" from roomMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	msg.setCommandID( GET_ALL_ROOM_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
	 * 	 <pre>对应的json:* 
	 *   ctrolID:123456789
	 *   roomArray:
	 *   [
	 *     { 家电的jsonObject格式}
	 *   ]
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_all_room(Message msg,MySqlClass mysql) {
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Room dbRoom;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	JSONArray ja=msg.getJson().getJSONArray("roomArray");	
			for (int i=0;i<ja.length();i++) {
		    	Room msgRoom=new Room(ja.getJSONObject(i));
		    	int roomID=msgRoom.getRoomID();
		    	Date msgModifyTime=msgRoom.getModifyTime();
		    	String key=ctrolID+"_"+roomID;
		    	if((dbRoom=this.roomMap.get(key))==null  || dbRoom.getModifyTime().before(msgModifyTime) ){	 //不存在或者云端较旧
		    		Room r=this.roomMap.put(key, msgRoom);
		    		if(r!=null){
				       json.put("errorCode",SUCCESS); 
		    		}else{
		    			json.put("errorCode",SQL_ERROR);
		    		}

					String key2=ctrolID+"_roomBind";
		    		String command=msgRoom.getCtrolID()+","+msg.getCommandID()+","+msgRoom.getRoomType()+","+msgRoom.getRoomID()+","+roomID;
		    		//jedis.publish(commandQueue,command);
		    		jedis.hset(key2, roomID+"", msgRoom.toJsonObject().toString());

		    	}else if(dbRoom.getModifyTime().after(msgModifyTime)){ //云端较新  
		    		log.error("Room in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" roomID:"+roomID+",discard.");
					json.put("errorCode",DEVICE_OBSOLETE);   
				}
			}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		msg.setJson(json);
  		msg.setCommandID(SET_ALL_ROOM_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	

	
	  /*** 告警消息
	  <pre>例如对应json消息体如下格式 ：
	  {
	    "warnContent", "检测你的厨房可燃气体超标，已自动帮你打开厨房的窗户~"  
	    "warnType",  1  	
	    "channel",0 
	    "timeout",30
	    "createTime","2014-12-25 12:13:14"    
	  }
	  */
	public void warning_msg(final Message msg){
		//if(this.msgSock!=null &&  !this.msgSock.sock.isOutputShutdown()  && !this.msgSock.sock.isClosed()){
		//msg.writeBytesToSock2(this.msgSock.sock);	
		JSONObject json=msg.getJson();
			try {
				if(msg.getJson().has("sender")){
					//msg.getJson().getInt("originalSenderRole");
					int senderRole=msg.getJson().getInt("sender");

					json.put("originalSenderRole", senderRole);
					json.put("receiver", 6);
					msg.setJson(json);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				try {
					json.put("errorCode",JSON_PARSE_ERROR);
					json.put("errorDescription",e.getCause().getMessage());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			try {
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
	
	public void warning_msg_ack(final Message msg){
		System.out.println("successfull sending warning message");
	} 	
	
    /*** 请求触发模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发模板不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发模板存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   triggerTemplateID:7654321,
     *   triggerTemplate: 
     *         {
     *          触发模板的json格式 
     *         }
     * }            
     */
    public void get_trigger_template(Message msg) {
    	JSONObject json=new JSONObject();
    	TriggerTemplate trigger=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int triggerID=msg.getJson().getInt("triggerTemplateID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
	    	String key=ctrolID+"_"+triggerID;
	    	if( ( trigger=TriggerTemplate.getFromDB(mysql, triggerID))!=null){
	    		json.put("triggerTemplate", trigger.toJson());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender);  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_TEMPLATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
     
    /*** 保存或者上传一个触发模板
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该触发模板，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的trigger的修改时间晚于云端，则将上报的trigger保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的trigger的修改时间早于云端，则需要将云端的触发模板下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的prifile的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  trigger:
     *   {
			"triggerID":123456789,
			"ctrolID":12345677,
			"triggerName":"未知情景",
			"triggerSetID":12345,
			"triggerTemplateID":0,
			"roomID":203,
			"roomType":2,
			"factorList":
			[
				{"factorID":40,"minValue":20,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"2014-12-13 14:15:17","maxValue":1
				}
			],
			"modifyTime":"2014-12-13 14:15:17",
			"createTime":"2014-12-13 14:15:17"
		}
	  }
     * @throws ParseException 
	*/
    public void set_trigger_template( Message msg) {
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	TriggerTemplate msgTrigger;

		try {
			msgTrigger = new TriggerTemplate(msg.getJson().getJSONObject("triggerTemplate"));
			TriggerTemplate dbTrigger;
	    	int ctrolID=msg.getJson().getInt("ctrolID");
	    	int triggerID=msgTrigger.getTriggerTemplateID();
	    	//Date msgModifyTime=msgTrigger.getModifyTime();

	    	String key=ctrolID+"_"+triggerID;
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			} 
			json.put("sender",2);
			json.put("receiver",sender); 

	    	if( (dbTrigger=TriggerTemplate.getFromDB(mysql, triggerID))==null){
	    		int x=msgTrigger.saveToDB(mysql);
				if(x>0){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}
				//json.put("errorCode",SUCCESS);    		
	    	}/*else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
	    		this.triggerMap.put(key, msgTrigger);
				json.put("errorCode",SUCCESS);   
			} */   	


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 请求触发规则
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发规则不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发规则存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567,
     *   triggerTemplateID:7654321,
     *   triggerTemplate: 
     *         {
     *          触发规则的json格式 
     *         }
     * }
     *                      
     */
    public void get_trigger(Message msg) {
    	JSONObject json=new JSONObject();
    	Trigger trigger=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int triggerID=msg.getJson().getInt("triggerID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
	    	String key=ctrolID+"_"+triggerID;
	    	if( (trigger= triggerMap.get(key))!=null  || (trigger=Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
	    		json.put("trigger", trigger.toJson());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

			json.put("sender",2);
	
			json.put("receiver",sender);  

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
     
    /*** 保存或者上传一个触发规则
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message的json格式:
     *  (1)如果云端不存在该触发规则，直接保存，返回json: {"errorCode":SUCCESS}；
     *  (2)如果上传的触发规则的修改时间晚于云端，则将上报的trigger保存在数据库，返回{"errorCode":SUCCESS}；
     *  (2)如果上传的触发规则的修改时间早于云端，则需要将云端的触发模板下发到 终端（手机、中控）,返回{"errorCode":OBSOLTE_PROFILE}  ；     *         
     *@param message 传入的json格式为： （要上传或者保存的触发规则的json格式）
     * {
     *  "sender":     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  "receiver":   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *  trigger:
     *   {
			"triggerID":123456789,
			"ctrolID":12345677,
			"triggerName":"未知情景",
			"triggerSetID":12345,
			"triggerTemplateID":0,
			"roomID":203,
			"roomType":2,
			"factorList":
			[
				{"factorID":40,"minValue":20,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"operator":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"2014-12-13 14:15:17","maxValue":1
				}
			],
			"modifyTime":"2014-12-13 14:15:17",
			"createTime":"2014-12-13 14:15:17"
		}
	  }
     * @throws ParseException 
	*/
    public void set_trigger( Message msg){
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger;
		try {
			msgTrigger = new Trigger(msg.getJson().getJSONObject("trigger"));

	    	Trigger dbTrigger;
	    	int ctrolID=msg.getJson().getInt("ctrolID");
	    	//int triggerID=msg.getJson().getInt("triggerID");
	    	int triggerID=msgTrigger.getTriggerID();
	    	Date msgModifyTime=msgTrigger.getModifyTime();
	    	String key=ctrolID+"_"+triggerID;
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 		
	    	
	    	if((dbTrigger=this.triggerMap.get(key))==null && (dbTrigger=Trigger.getFromDB(mysql, ctrolID, triggerID))==null){
	    		Trigger t=this.triggerMap.put(key, msgTrigger);
				if(t!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}   		
	    	}else if(  dbTrigger.getModifyTime().after(msgModifyTime)){	//云端较新  
				json.put("errorCode",PROFILE_OBSOLETE);    		
	    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
	    		this.triggerMap.put(key, msgTrigger);
				json.put("errorCode",SUCCESS);   
				}  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.getCause().getMessage());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
			
		}
		msg.setJson(json);
  		msg.setCommandID(SET_TRIGGER_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除触发模板
     * <pre>传入的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   triggerID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}           
     */
    public void delete_trigger(Message msg) {
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int triggerID=msg.getJson().getInt("triggerID");
	    	String key=ctrolID+"_"+triggerID;
	    	int sender=0;
			if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
			}
	    	if(triggerMap.containsKey(key)){
	    		Trigger t=triggerMap.remove(key);
	    		if(t!=null){
	    			json.put("errorCode", SUCCESS);
	    		}else{
	    			json.put("errorCode", SQL_ERROR);
	    		}
	    		    		
	    	}else {
				log.error("room_trigger not exist ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		msg.setJson(json);
    	msg.setCommandID(DELETE_TRIGGER_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    
    
    /*** 同步所有表
     * <pre> 当且仅当用在中控断网重新联网时，中控需要从info_syn_updatetime 表中读取每一个表的更新时间，将晚于这个时间的所有记录(情景模式、家电列表)上报云端；
     *       每一个表的记录，用单独的一个命令上报；
     * @Note 需要注意：在联网情况下，用户每次修改情景、触发、家电等配置时，都要刷新一下info_syn_updatetime 最后同步时间；
     *             在断网时，不能刷新最后同步时间 ；        
     * 请求的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   recordType: 记录对象的类型，例如 profile,device,trigger ...
     *   record：[
                { ...	记录1的json格式			},	
                { ...	记录2的json格式			}
               ]
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1） 同步成功返回0，否则返回           -1
     */
    public void syn_updatetime(Message msg)  {
    	JSONObject json=new JSONObject();
    	String tableName=msg.getJson().optString("recordType");
    	JSONArray ja=null;
    	int sender=0;
	   try {
			if(msg.getJson().has("sender")){
	
					sender=msg.getJson().getInt("sender");
	
				}
	    	switch (tableName) {
			case "profile":
				ja=msg.getJson().getJSONArray("record");
				for (int i=0;i<ja.length();i++) {
					JSONObject jo=ja.getJSONObject(i);
					Profile profile=new Profile(jo);
					profile.saveToDB(mysql);
				}			
				break;
			case "profileSet":
				ja=msg.getJson().getJSONArray("record");
				for (int i=0;i<ja.length();i++) {
					JSONObject jo=ja.getJSONObject(i);
					ProfileSet profile=new ProfileSet(jo);
					profile.saveProfileSetToDB(mysql);
				}			
				break;
			case "device":
				ja=msg.getJson().getJSONArray("record");
				for (int i=0;i<ja.length();i++) {
					JSONObject jo=ja.getJSONObject(i);
					Device device=new Device(jo);
					device.saveToDB(mysql);
				}			
				break;
			case "trigger":
				ja=msg.getJson().getJSONArray("record");
				for (int i=0;i<ja.length();i++) {
					JSONObject jo=ja.getJSONObject(i);
					Trigger trigger=new Trigger(jo);
					trigger.saveToDB(mysql);
				}			
				break;
			default:
				break;
			}

			json.put("sender",2);
			json.put("receiver",sender); 

		} catch (JSONException e) {
			e.printStackTrace();
		}
   	msg.setCommandID(SYN_UPDATETIME_ACK);
	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    }
  
    /*** 下载某一个型号家电的 红外码库文件
     * <pre> ；        
     * 请求的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   applianceType: 填写家电的factorID,
     *   fileID:这一型号的 家电对应的文件名ID
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）若 这一型号的家电的 红外库存在，则返回  红外码库文件的URL 地址；
     *   （2）若 这一型号的家电的 红外库不存在，则返回  NOT_EXIST 
     */
    public void    download_infrared_file(Message msg) {
		JSONObject json= new JSONObject();
    	int sender=0;
		try {
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
			int applianceType;

			applianceType = msg.getJson().getInt("applianceType");

			if(applianceType==541 ||applianceType==501||applianceType==511||applianceType==521||applianceType==601 ||applianceType==591){			
			}else{
				json.put("errorCode", UNKNOWN_DEVICE_TYPE);
			}
			String fileID=msg.getJson().optString("fileID");
			IRFileDownload irDownload=new IRFileDownload(applianceType,fileID);
			String url=irDownload.getURL();		
	

			if(url.equals("") || url==null){
				json.put("errorCode", INFRARED_FILE_NOT_EXIST);
			}else{
				json.put("url", url);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		msg.setJson(json);
    	msg.setCommandID(DOWNLOAD_INFRARED_FILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /*** 上报某一个型号家电的 红外码，让云端来识别
     * <pre> ；        
     * 请求的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   applianceType: 填写家电类型，数值请参考factorID字典表,如果家电类型未知填写:-1
     *   ircode:红外码的16进制字符串格式例如："27,04,00,00,24,00,26,81,FC,01,FC,81,FC,05,F8,C1,0E,1B,C2,00,05,F8,C3,00,70,23,CB,26,01,00,24,8,07,09,00,00,00,00,51,00"
     * @return message 的json格式：
     *   （1）若 这一型号的家电的 红外码识别成功，则返回  这个红外型号对应码库文件的URL 地址；
     *   （2）若 这一型号的家电的 红外识别识别，则返回  NOT_EXIST 
     * @throws JSONException 
     */
    private void recognize_infrared_code(Message msg) {
		JSONObject json= new JSONObject();
    	int sender=0;
		try {
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
			String ircode=msg.getJson().optString("ircode");
			int applianceType;
			applianceType = msg.getJson().getInt("applianceType");

			String applianceTypeStr="";
			switch (applianceType) {
			case 541: //空调
				applianceTypeStr="AC";
				break;
			case 501: //电视
				applianceTypeStr="TV";
				break;
			case 511: //机顶盒
				applianceTypeStr="STB";
				break;
			case 521: //DVD
				applianceTypeStr="DVD";
				break;
			case 601: //电风扇
				applianceTypeStr="FAN";
				break;
			case 591: //空气净化器
				applianceTypeStr="ACL";
				break;			
			default:
				applianceTypeStr="";
				break;
			}
			if(applianceTypeStr!=""){
				this.ir_file_path=this.ir_file_path+"\\"+applianceType+"\\codes";
			}
			
	    	IRMatch2 im=new IRMatch2();
			im.match(new File(ir_file_path), im.getC3(ircode));
			String fileName=im.getTop1();
			if(fileName==null){
				json.put("errorCode", INFRARED_CODE_NOT_RECOGNIZED);
			}else{
				IRFileDownload irDownload=new IRFileDownload(applianceType,fileName);
				String url=irDownload.getURLWithoutExtention();		
	
	
				if(url.equals("") || url==null){
					json.put("errorCode", INFRARED_FILE_NOT_EXIST);
				}else{
					json.put("url", url);
				}			
			}



		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	msg.setCommandID(DOWNLOAD_INFRARED_FILE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
	}
    

	public static void main(String[] args) {		
		Configure cf= new Configure();
		LogicControl lc= new LogicControl(cf);		
		System.out.println("lc="+lc);
	}		
}
