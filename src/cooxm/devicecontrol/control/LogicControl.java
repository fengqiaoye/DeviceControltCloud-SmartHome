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
import java.util.Date;
import java.util.Iterator;
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
	
	/*** 请求家电列表*/
	private static final short GET_ONE_DEVICE				=	COMMAND_START+41;
	/*** 请求家电列表 的回复*/
	private static final short GET_ONE_DEVICE_ACK			=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电列表*/
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
	
	/** 服务器没有通过认证*/
	public static final int SERVER_NOT_AUTHORIZED	  = -50041;
	

	

	/***********************   resource needed   ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	static Configure config=null;
	static MySqlClass mysql=null;
	SocketClient msgSock=null;
	Jedis jedis=null;// new Jedis("172.16.35.170", 6379,200);
	ProfileMap profileMap =null;
	ProfileSetMap profileSetMap =null;
	DeviceMap deviceMap=null;
	TriggerMap triggerMap=null;
	RoomMap roomMap=null;
	File ir_file;//=new File(cf.getValue("ir_file_path"));
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
		
		ir_file=new File(cf.getValue("ir_file_path"));
		
		
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
		log.info("Initialization of map successful :  profileMap,size="+profileMap.size()
				+";profileSetMap size="+profileMap.size()
				+"; deviceMap, size="+deviceMap.size()
				+"; roomMap, size="+roomMap.size()
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
	
	public  void decodeCommand(Message msg){		
		int commandID=msg.getCommandID();
			
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
			try {
				get_room_profile(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ROOM_PROFILE:
			try {
				set_room_profile(msg);
			} catch (JSONException | SQLException | ParseException e) {
				e.printStackTrace();
			} 
			break;	
		case DELETE_ROOM_PROFILE:
			try {
				delete_room_profile(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case SWITCH_ROOM_PROFILE:	
			try {
				switch_room_profile(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case GET_RROFILE_SET:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_RROFILE_SET:	
			try {
				set_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_RROFILE_SET:
			try {
				delete_room_profile(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case SWITCH_RROFILE_SET:	
			try {
				switch_profile_set(msg);
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			break;
		case GET_RROFILE_TEMPLATE: 	
			try {
				get_profile_template(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_RROFILE_TEMPLATE:	
			try {
				set_profile_template(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;	
		case GET_ONE_DEVICE:	
			try {
				get_one_device(msg);;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case SET_ONE_DEVICE:	
			try {
				set_one_device(msg, mysql);;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_ONE_DEVICE:
			try {
				delete_one_device(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;			
		case SWITCH_DEVICE_STATE:	
			try {
				switch_device_state(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_ONE_ROOM:	
			try {
				get_one_room(msg);;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case SET_ONE_ROOM:	
			try {
				set_one_room(msg, mysql);;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_ONE_ROOM:
			try {
				delete_one_room(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;	
		case WARNING_MSG:	
			warning_msg(msg);
			break;
		case WARNING_MSG_ACK:	
			warning_msg_ack(msg);
			break;
		case GET_TRIGGER_TEMPLATE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_TRIGGER_TEMPLATE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;	
		case GET_TRIGGER:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_TRIGGER:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_TRIGGER:
			try {
				delete_one_device(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case SYN_UPDATETIME:	
			try {
				syn_updatetime(msg);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case DOWNLOAD_INFRARED_FILE:	
			try {
				download_infrared_file(msg);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case RECOGNIZE_INFRARED_CODE:
			try {
				recognize_infrared_code(msg);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
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
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			if(msg.isValid()){
				log.info("Valid command receive,but commandID can't be recognized. SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
				try {
					json.put("errorCode", LogicControl.COMMAND_NOT_ENCODED);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }else{
            	log.info("Invalid command receive. SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
            	try {
					json.put("errorCode", LogicControl.WRONG_COMMAND);
				} catch (JSONException  e) {
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
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   profileID:7654321,
     *   profile: 
     *         {
     *          情景模式的json格式 
     *         }
     * }
     *                      
     */
    public void get_room_profile(Message msg) throws JSONException, SQLException{
    	Profile profile=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
		JSONObject json= new JSONObject();
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
    	String key=ctrolID+"_"+profileID;
    	if( (profile= profileMap.get(key))!=null  || (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
    		json.put("profile", profile.toJsonObj());
    		json.put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_profile ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		json.put("sender",2);

		json.put("receiver",sender);  
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
     *@param message 传入的json格式为： （要上传或者保存的prifile的json格式）
     * {
     *  "senderRole":    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  profile:
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
     * @throws ParseException 
	*/
    public void set_room_profile( Message msg) throws JSONException, SQLException, ParseException{
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile msgProfile=new Profile(msg.getJson().getJSONObject("profile"));
    	Profile dbProfile;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+profileID;
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
    	if( (dbProfile=this.profileMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
			json.put("errorCode",PROFILE_OBSOLETE);    		
    	}else { //云端较旧  或者 不存在，则保存
    		this.profileMap.put(key, msgProfile);
			
			json.put("errorCode",SUCCESS);   
		}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
    	msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除情景模式
     * <pre>传入的json格式为：
     * { 
     *   senderRole:    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
           
     */
    public void delete_room_profile(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
    	String key=ctrolID+"_"+profileID;
    	int sender=0;
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
    	if(profileMap.containsKey(key)){
    		profileMap.remove(key);
    		msg.setJson(new JSONObject());
    		json.put("errorCode", SUCCESS);    		
    	}else if((Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
    		Profile.deleteFromDB(mysql, ctrolID, profileID);
    		json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profile not exist ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			//msg.setJson()new JSONObject();
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);

		json.put("sender",2);
		json.put("receiver",sender); 
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
    public void switch_room_profile(final Message msg)throws JSONException, SQLException, InterruptedException{
    	JSONObject json=new JSONObject();
    	Message replyMsg=new Message(msg);
    	Profile profile=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	String key=ctrolID+"_currentProfile";
    	if((profile= profileMap.get(key))!=null || (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null){
    		int roomID=profile.getRoomID();
    		String command=profile.getCtrolID()+","+msg.getCommandID()+","+profile.getRoomType()+","+profile.getRoomID()+","+profileID;
    		jedis.publish(commandQueue,command);
    		jedis.hset(key, roomID+"", profile.toJsonObj().toString());
    		if(sender==0){
    			json.put("errorCode",SUCCESS);
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();   			
    		}
    	}else {
			log.warn("Can't switch room profile,profile doesn't exist. ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(SWITCH_ROOM_PROFILE_ACK);
    	json.put("sender",2);
    	json.put("receiver",sender);
    	json.put("originalSenderRole", sender);
		replyMsg.setJson(new JSONObject());
    	CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
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
		TimeOutTread to=new TimeOutTread(10,msg);
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
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   profileSetID:7654321,
     *   profile: 
     *         {
     *          情景模式集的json格式 
     *         }
     * }              
     */
    public void get_profile_set(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	ProfileSet profileSet=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileSetID=msg.getJson().getInt("profileSetID");
    	String key=ctrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	if((profileSet=profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
    		json.put("profileSet", profileSet.toJsonObj());
    		json.put("errorCode",SUCCESS);   
    	}else {
			log.warn("Can't get_profile_set, ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
			json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.setCommandID( GET_RROFILE_SET_ACK);
		json.put("sender",2);
		json.put("receiver",sender);
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
     *  "senderRole":    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 设备控制服务器:2; 
     *   profileSet:
     *     {  
     *      情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *     }  
     * }
	 * */
	public void set_profile_set(Message msg) throws JSONException, SQLException, ParseException{
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	ProfileSet msgProfileSet=new ProfileSet(msg.getJson().getJSONObject("profileSet"));
    	ProfileSet dbProfileSet;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileSetID=msg.getJson().getInt("profileSetID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	
    	if((dbProfileSet=profileSetMap.get(key))==null && (dbProfileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))==null ){
    		profileSetMap.put(key, msgProfileSet);
			json.put("errorCode",SUCCESS);     		
    	}else if( dbProfileSet.getModifyTime().after(msgModifyTime)){	//云端较新  
			json.put("errorCode",PROFILE_SET_OBSOLETE);    		
    	}else if( dbProfileSet.getModifyTime().before(msgModifyTime)){
    		profileSetMap.put(key, msgProfileSet);
			json.put("errorCode",SUCCESS);   		
    	}    	
  		msg.setCommandID(SET_RROFILE_SET_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
    public void delete_profile_set(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	//Profile profile=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
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
			log.warn("room_profileSet not exist ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.setCommandID( DELETE_RROFILE_SET_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	

	
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     ctrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void switch_profile_set(Message msg)throws JSONException, SQLException, InterruptedException{
    	JSONObject json=new JSONObject();
    	ProfileSet profileSet=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileSetID=msg.getJson().getInt("profileSetID"); 
    	int sender=0;
    	if(json.has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}     	
    	//String key=ctrolID+"_"+profileSetID;
    	String key=ctrolID+"_currentProfile";
    	if((profileSet= profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
    		String command=profileSet.getCtrolID()+","+msg.getCommandID()+","+254+","+254+","+profileSetID;
    		jedis.publish(commandQueue, command);
    		Profile profile=null;
    		for (int i = 0; i < profileSet.getProfileList().size(); i++) {
        		profile=profileMap.get(profileSet.getProfileList().get(i));        		
    			jedis.hset(key, profile.getRoomID()+"", profile.toJsonObj().toString());
    			//jedis.hset(currentProfileSet, key, profileSet.toJsonObj().toString());
			}
    		if(sender==0){
	    		json.put("errorCode",SUCCESS);  	    		
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();  				
    		}
    	}else {
			log.warn("Can't switch room profileSet,profileSet doesn't exit. ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID( SWITCH_RROFILE_SET_ACK);
		json.put("sender",2);
		json.put("receiver",sender);
		msg.setJson(json);
		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		
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
		TimeOutTread to=new TimeOutTread(10,msg);
		to.start();
    }
    
    /*** 请求查询情景模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   profileTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX_NOT_EXIST}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   profileTemplateID:7654321,
     *   profileTemplate: 
     *         {
     *          情景模式模板的json格式 
     *         }
     * }
     *                      
     */
    public void get_profile_template(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	ProfileTemplate profileTemplat=null;
    	int profileTemplatID=msg.getJson().getInt("profileTemplateID");
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
    	if(  (profileTemplat=ProfileTemplate.getFromDB(mysql,  profileTemplatID))!=null){
    		json.put("profileTemplate", profileTemplat.toJsonObj());
    		json.put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_profile_template, profileTemplatID:"+profileTemplatID+" from Mysql.");
			json.put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);
    	}
    	msg.setCommandID( GET_RROFILE_TEMPLATE_ACK);
		json.put("sender",2);
		json.put("receiver",sender);  
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
		TimeOutTread to=new TimeOutTread(10,msg);
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
     *  "senderRole":    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 设备控制服务器:2;
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
    public void set_profile_template( Message msg) throws JSONException, SQLException, ParseException{
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//ProfileTemplate msgProfile=new ProfileTemplate(msg.getJson().getJSONObject("profileTemplate"));
    	ProfileTemplate dbProfile;
    	int profileTemplatID=msg.getJson().getInt("profileTemplatID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}    	
    	if((dbProfile=ProfileTemplate.getFromDB(mysql, profileTemplatID))==null){
			json.put("errorCode",SUCCESS);     		
    	}else if(  dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
			json.put("errorCode",PROFILE_TEMPLATE_OBSOLETE);    		
    	}else if(  dbProfile.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
			json.put("errorCode",SUCCESS);   
		}    	
  		msg.setCommandID(SET_RROFILE_TEMPLATE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
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
		TimeOutTread to=new TimeOutTread(10,msg);
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
	public void get_one_device(Message msg) throws JSONException{
    	JSONObject json=new JSONObject();
    	Device device=new Device();
    	int ctrolID=msg.getJson().getInt("ctrolID");
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
			log.warn("Can't get_one_device, ctrolID:"+ctrolID+"deviceID: "+ deviceID+" from deviceMap or Mysql.");
			json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( GET_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
	 * 	 <pre>对应的jsonArray:* 
	 *   {
	 *     将这个家电的jsonObject格式
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_one_device(Message msg,MySqlClass mysql) throws JSONException, ParseException{
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Device msgDevice=new Device(msg.getJson());
    	Device dbDevice;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+deviceID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	
    	if((dbDevice=this.deviceMap.get(key))==null  || dbDevice.modifyTime.before(msgModifyTime) ){	 //不存在或者云端较旧
    		this.deviceMap.put(key, msgDevice);
			json.put("errorCode",SUCCESS);  

			String key2=ctrolID+"_roomBind";
    		int roomID=msgDevice.getRoomID();
    		String command=msgDevice.getCtrolID()+","+msg.getCommandID()+","+msgDevice.getRoomType()+","+msgDevice.getRoomID()+","+deviceID;
    		jedis.publish(commandQueue,command);
    		jedis.hset(key2, deviceID+"", msgDevice.toJsonObj().toString());
			json.put("errorCode",SUCCESS);
    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ //云端较新  
			json.put("errorCode",DEVICE_OBSOLETE);   
		}
  		msg.setCommandID(SET_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
    public void delete_one_device(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	String key=ctrolID+"_"+deviceID;
    	int sender=0;
    	Device device=new Device();
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
		if((device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
    		deviceMap.remove(key);
    		json.put("errorCode", SUCCESS);    		
    	}else {
			log.warn("room_device not exist ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( DELETE_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
	public void switch_device_state(Message msg) throws JSONException, SQLException{
		JSONObject json=new JSONObject();
    	Device device=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	int deviceType=msg.getJson().getInt("deviceType");
    	DeviceState state= new DeviceState();
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	//String key=ctrolID+"_"+deviceID;
    	String key=ctrolID+"_currentDeviceState";
    	if((device= deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
        	if(msg.getJson().has("state")){
        		state=new DeviceState(msg.getJson().getJSONObject("state"));
        		String command=device.getCtrolID()+","+msg.getCommandID()+","+device.getRoomType()+","+device.getRoomID()+","+deviceID+","+deviceType;
        		jedis.publish(commandQueue, command);
        		jedis.hset(key, deviceID+"", state.toJson().toString());
        		json.put("errorCode",SUCCESS); 	  
        		if(sender==0){
    	    		  		
        		}else {
        			TimeOutTread to=new TimeOutTread(10,msg);
        			to.start();   			
        		}
        	}else{
        		json.put("errorCode",DEVICE_STATE_EMPTY); 	
        	}

    	}else {
			log.warn("Can't switch room device,device doesn't exit. ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(SWITCH_DEVICE_STATE_ACK);
    	json.put("sender",2);
    	json.put("receiver",sender); 
		msg.setJson(json);
		try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	public void get_one_room(Message msg) throws JSONException{
    	JSONObject json=new JSONObject();
    	Room room=new Room();
    	int ctrolID=msg.getJson().getInt("ctrolID");
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
			log.warn("Can't get_one_room, ctrolID:"+ctrolID+"roomID: "+ roomID+" from roomMap or Mysql.");
			json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( GET_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
	 * 	 <pre>对应的jsonArray:* 
	 *   {
	 *     将这个家电的jsonObject格式
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_one_room(Message msg,MySqlClass mysql) throws JSONException, ParseException{
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Room msgRoom=new Room(msg.getJson());
    	Room dbRoom;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int roomID=msg.getJson().getInt("roomID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+roomID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	
    	if((dbRoom=this.roomMap.get(key))==null  || dbRoom.getModifyTime().before(msgModifyTime) ){	 //不存在或者云端较旧
    		this.roomMap.put(key, msgRoom);
			json.put("errorCode",SUCCESS);  

			String key2=ctrolID+"_roomBind";
    		//int roomID=msgRoom.getRoomID();
    		String command=msgRoom.getCtrolID()+","+msg.getCommandID()+","+msgRoom.getRoomType()+","+msgRoom.getRoomID()+","+roomID;
    		jedis.publish(commandQueue,command);
    		jedis.hset(key2, roomID+"", msgRoom.toJsonObject().toString());
			json.put("errorCode",SUCCESS);
    	}else if(dbRoom.getModifyTime().after(msgModifyTime)){ //云端较新  
			json.put("errorCode",DEVICE_OBSOLETE);   
		}
  		msg.setCommandID(SET_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
     *   roomID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_one_room(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	int ctrolID=msg.getJson().getInt("ctrolID");
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
			log.warn("room_room not exist ctrolID:"+ctrolID+" roomID:"+roomID+" from roomMap or Mysql.");
			json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( DELETE_ONE_DEVICE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
    /*** 请求切换某个家电状态,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_set_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(10,msg);
		to.start();
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
			try {
				if(msg.getJson().has("sender")){
					//msg.getJson().getInt("originalSenderRole");
					int senderRole=msg.getJson().getInt("sender");
					JSONObject json=msg.getJson();
					json.put("originalSenderRole", senderRole);
					json.put("receiver", 6);
					msg.setJson(json);
				}
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	
	public void warning_msg_ack(final Message msg){
		System.out.println("successfull sending warning message");
	} 	
	
    /*** 请求触发模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发模板不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发模板存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   triggerTemplateID:7654321,
     *   triggerTemplate: 
     *         {
     *          触发模板的json格式 
     *         }
     * }            
     */
    public void get_trigger_template(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	Trigger trigger=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
    	String key=ctrolID+"_"+triggerID;
    	if( (trigger= triggerMap.get(key))!=null  || (trigger=Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
    		json.put("triggerTemplate", trigger.toJson());
    		json.put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		json.put("sender",2);
		json.put("receiver",sender);  
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
     *  "senderRole":    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 设备控制服务器:2;
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
    public void set_trigger_template( Message msg) throws JSONException, SQLException, ParseException{
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger=new Trigger(msg.getJson().getJSONObject("triggerTemplate"));
    	Trigger dbTrigger;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}    	
    	if((dbTrigger=this.triggerMap.get(key))==null && (dbTrigger=Trigger.getFromDB(mysql, ctrolID, triggerID))==null){
    		this.triggerMap.put(key, msgTrigger);
			json.put("errorCode",SUCCESS);    		
    	}else if(  dbTrigger.getModifyTime().after(msgModifyTime)){	//云端较新  
			json.put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
    		this.triggerMap.put(key, msgTrigger);
			json.put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发规则不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发规则存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 设备控制服务器:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   triggerTemplateID:7654321,
     *   triggerTemplate: 
     *         {
     *          触发规则的json格式 
     *         }
     * }
     *                      
     */
    public void get_trigger(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	Trigger trigger=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
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
			log.warn("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		json.put("sender",2);

		json.put("receiver",sender);  
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
     *  "senderRole":    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 设备控制服务器:2;
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
    public void set_trigger( Message msg) throws JSONException, SQLException, ParseException{
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger=new Trigger(msg.getJson().getJSONObject("trigger"));
    	Trigger dbTrigger;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
    	
    	if((dbTrigger=this.triggerMap.get(key))==null && (dbTrigger=Trigger.getFromDB(mysql, ctrolID, triggerID))==null){
    		this.triggerMap.put(key, msgTrigger);
			json.put("errorCode",SUCCESS);    		
    	}else if(  dbTrigger.getModifyTime().after(msgModifyTime)){	//云端较新  
			json.put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
    		this.triggerMap.put(key, msgTrigger);
			json.put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		json.put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		json.put("receiver",sender); 
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除触发模板
     * <pre>传入的json格式为：
     * { 
     *   senderRole:    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   ctrolID:1234567
     *   triggerID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}           
     */
    public void delete_trigger(Message msg) throws JSONException, SQLException{
    	JSONObject json=new JSONObject();
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
    	if(triggerMap.containsKey(key)){
    		triggerMap.remove(key);
    		json.put("errorCode", SUCCESS);    		
    	}else if((Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
    		Trigger.deleteFromDB(mysql, ctrolID, triggerID);
    		json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_trigger not exist ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
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
     *   senderRole:    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 设备控制服务器:2;
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
    public void syn_updatetime(Message msg) throws JSONException {
    	JSONObject json=new JSONObject();
    	String tableName=msg.getJson().optString("recordType");
    	JSONArray ja=null;
    	int sender=0;
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
				try {
					profile.saveProfileSetToDB(mysql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
    	msg.setCommandID(SYN_UPDATETIME_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
     *   senderRole:    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   ctrolID:1234567
     *   applianceType: 填写家电的factorID,
     *   fileID:这一型号的 家电对应的文件名ID
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）若 这一型号的家电的 红外库存在，则返回  红外码库文件的URL 地址；
     *   （2）若 这一型号的家电的 红外库不存在，则返回  NOT_EXIST 
     */
    public void    download_infrared_file(Message msg) throws JSONException{
		JSONObject json= new JSONObject();
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
		String fileID=msg.getJson().optString("fileID");
		IRFileDownload irDownload=new IRFileDownload(fileID);
		String url=irDownload.getURL();		

    	msg.setCommandID(DOWNLOAD_INFRARED_FILE_ACK);
		if(url.equals("") || url==null){
			json.put("errorCode", INFRARED_FILE_NOT_EXIST);
		}else{
			json.put("url", url);
		}
		json.put("sender",2);
		json.put("receiver",sender); 
		msg.setJson(json);
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
     *   senderRole:    中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 设备控制服务器:2;
     *   ctrolID:1234567
     *   applianceType: 填写家电的factorID,
     *   ircode:红外码的16进制字符串格式例如："27,04,00,00,24,00,26,81,FC,01,FC,81,FC,05,F8,C1,0E,1B,C2,00,05,F8,C3,00,70,23,CB,26,01,00,24,8,07,09,00,00,00,00,51,00"
     * @return message 的json格式：
     *   （1）若 这一型号的家电的 红外码识别成功，则返回  这个红外型号对应码库文件的URL 地址；
     *   （2）若 这一型号的家电的 红外识别识别，则返回  NOT_EXIST 
     * @throws JSONException 
     */
    private void recognize_infrared_code(Message msg) throws JSONException {
		JSONObject json= new JSONObject();
    	int sender=0;
		if(msg.getJson().has("sender")){
			   sender=msg.getJson().getInt("sender");
		}
		String ircode=msg.getJson().optString("ircode");
    	IRMatch2 im=new IRMatch2();
		im.match(this.ir_file, im.getC3(ircode));
		String fileName=im.getTop1();
		if(fileName==null){
			json.put("errorCode", INFRARED_CODE_NOT_RECOGNIZED);
		}else{
			IRFileDownload irDownload=new IRFileDownload(fileName.split(",")[1]);
			String url=irDownload.getURLWithoutExtention();		


			if(url.equals("") || url==null){
				json.put("errorCode", INFRARED_FILE_NOT_EXIST);
			}else{
				json.put("url", url);
			}			
		}
    	msg.setCommandID(DOWNLOAD_INFRARED_FILE_ACK);
		json.put("sender",2);
		json.put("receiver",sender); 
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
