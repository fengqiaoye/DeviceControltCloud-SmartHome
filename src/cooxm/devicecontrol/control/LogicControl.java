package cooxm.devicecontrol.control;
/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.device.Device;
import cooxm.devicecontrol.device.DeviceMap;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.device.ProfileMap;
import cooxm.devicecontrol.device.ProfileSet;
import cooxm.devicecontrol.device.ProfileSetMap;
import cooxm.devicecontrol.device.ProfileTemplate;
import cooxm.devicecontrol.device.RoomMap;
import cooxm.devicecontrol.device.Trigger;
import cooxm.devicecontrol.device.TriggerMap;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.socket.MsgSocketClient;
import cooxm.devicecontrol.util.MySqlClass;
import redis.clients.jedis.Jedis;

public class LogicControl {	
	
	public static final short COMMAND_START            		   =  0x1600;
	public static final short COMMAND_ACK_OFFSET       		   =  0x4000; 
	public static final short WARNING_START            		   =  0x2000;	
	
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
	private static final short SWITCH_ROOM_PROFILE				=	COMMAND_START+4;
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
	private static final short SWITCH_RROFILE_SET				=	COMMAND_START+24;	
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
	private static final short SWITCH_DEVICE_STATE		    =	COMMAND_START+44;
	/*** 切换某个家电状态 的回复*/
	private static final short SWITCH_DEVICE_STATE_ACK		=	COMMAND_START+44+COMMAND_ACK_OFFSET;
	
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
	
	/*** 消息可以识别，但是收件人错误，例如收到自己发送的消息*/
	private static final int COMMAND_NOT_ENCODED   	  = -50021;
	/** 命令超时没有响应*/
	public static final int TIME_OUT		   	      = -50022;
	/**命令号码段不对*/
	public static final int WRONG_COMMAND		   	  = -50023;
	

	

	/***********************   resource needed   ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	static MySqlClass mysql=null;
	Socket msgSock=null;
	Jedis jedis=null;// new Jedis("172.16.35.170", 6379,200);
	ProfileMap profileMap =null;
	ProfileSetMap profileSetMap =null;
	DeviceMap deviceMap=null;
	TriggerMap triggerMap=null;
	RoomMap roomMap=null;
	private final static String currentProfile= "currentProfile";
	private final static String currentProfileSet= "currentProfileSet";
	private final static String currentDeviceState= "currentDeviceState";	
	private final static String commandQueue= "commandQueue";
	
    //public LogicControl() {}
    
    public LogicControl(Config cf) {
    	log.info("Starting logic control module ... ");
    	
		String mysql_ip			=cf.getValue("mysql_ip");
		String mysql_port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String mysql_database	=cf.getValue("mysql_database");		
		String redis_ip         =cf.getValue("redis_ip");
		int redis_port       	=Integer.parseInt(cf.getValue("redis_port"));	
		String msg_server_IP=cf.getValue("msg_server_ip");
		int msg_server_port =Integer.parseInt(cf.getValue("msg_server_port"));
		
		
		mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		this.jedis= new Jedis(redis_ip, redis_port,200);
		try {
			this.msgSock=new MsgSocketClient(msg_server_IP, msg_server_port);
			new Thread((Runnable) this.msgSock).start();
			this.profileMap= new ProfileMap(mysql);
			this.profileSetMap= new ProfileSetMap(mysql);
			this.deviceMap=new DeviceMap(mysql);
			this.triggerMap=new TriggerMap(mysql);
			this.roomMap=new RoomMap(mysql);
		} catch (SQLException  e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	  return mysql;    	
    }
	
	public void decodeCommand(Message msg){		
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
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ONE_DEVICE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
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
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case WARNING_MSG:	
			send_warning_msg(msg);
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
		default:
			int sender=0;
			if(msg.getJson().has("sender")){
				   try {
					sender=msg.getJson().getInt("sender");
	    			msg.getJson().put("sender",2);
	    			msg.getJson().put("receiver",sender); 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(msg.isValid()){
				log.info("Valid command receive,but commandID not encoded.SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
				try {
					msg.getJson().put("errorCode", LogicControl.COMMAND_NOT_ENCODED);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }else{
            	log.info("Invalid command receive. SequeeceID:"+msg.getCookie()+" command ID :"+msg.getCommandID());
            	try {
					msg.getJson().put("errorCode", LogicControl.WRONG_COMMAND);
				} catch (JSONException  e) {
					e.printStackTrace();
				}            
            }
			msg.setCommandID((short) (msg.getCommandID()+ LogicControl.COMMAND_ACK_OFFSET));
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
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
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
    	int sender=0;
    	String key=ctrolID+"_"+profileID;
    	if( (profile= profileMap.get(key))!=null  || (profile=Profile.getFromDB(mysql, ctrolID, profileID))!=null){
    		msg.getJson().put("profile", profile.toJsonObj());
    		msg.getJson().put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_profile ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender);  
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
     *  "senderRole":    中控:0 ; 手机:1 ; 云:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 云:2;
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
    	//JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile msgProfile=new Profile(msg.getJson().getJSONObject("profile"));
    	Profile dbProfile;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+profileID;
    	int sender=0;
    	
    	if((dbProfile=this.profileMap.get(key))==null && (dbProfile=Profile.getFromDB(mysql, ctrolID, profileID))==null){
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);    		
    	}else if(  dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
			msg.getJson().put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbProfile.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
    		this.profileMap.put(key, msgProfile);
			msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除情景模式
     * <pre>传入的json格式为：
     * { 
     *   senderRole:    中控:0 ; 手机:1 ; 云:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 云:2;
     *   ctrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
           
     */
    public void delete_room_profile(Message msg) throws JSONException, SQLException{
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
    		msg.getJson().put("errorCode", SUCCESS);    		
    	}else if((Profile.getFromDB(mysql, ctrolID, profileID))!=null){
    		Profile.deleteFromDB(mysql, ctrolID, profileID);
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profile not exist ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			//msg.setJson()new JSONObject();
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);

		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
    
    /*** 请求切换情景模式,根据命令的发送方有不同的响应方式
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
    *   ctrolID:1234567
    *   roomID: 203
    *   profileID:7654321
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile(final Message msg)throws JSONException, SQLException, InterruptedException{
    	Message replyMsg=new Message(msg);
    	Profile profile=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileID=msg.getJson().getInt("profileID");
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	String key=ctrolID+"_"+profileID;
    	if((profile= profileMap.get(key))!=null || (profile=Profile.getFromDB(mysql, ctrolID, profileID))!=null){
    		jedis.publish(commandQueue, profile.toJsonObj().toString());
    		jedis.hset(currentProfile, key, profile.toJsonObj().toString());
    		if(sender==0){
    			replyMsg.setJson(new JSONObject());
    			replyMsg.getJson().put("errorCode",SUCCESS);
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();   			
    		}
    	}else {
			log.warn("Can't switch room profile,profile doesn't exit. ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			replyMsg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(SWITCH_ROOM_PROFILE_ACK);
    	replyMsg.getJson().put("sender",2);
    	replyMsg.getJson().put("receiver",0);
    	CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
    }
    
    /*** 请求切换情景模式,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
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
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
     *   ctrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50004}
     *   （2）如果查询的情景模式集存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   profileSetID:7654321,
     *   profile: 
     *         {
     *          情景模式集的json格式 
     *         }
     * }              
     */
    public void get_profile_set(Message msg) throws JSONException, SQLException{
    	//JSONObject json=msg.json;
    	ProfileSet profileSet=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileSetID=msg.getJson().getInt("profileSetID");
    	String key=ctrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	if((profileSet=profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
    		msg.getJson().put("profileSet", profileSet.toJsonObj());
    		msg.getJson().put("errorCode",SUCCESS);   
    	}else {
			log.warn("Can't get_profile_set, ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.setCommandID( GET_RROFILE_SET_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    

	
	/*** 设置 情景模式集

	 * <pre>Json格式和 设置情景模式 和 {@link cooxm.devicecontrol.control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：

     * { 
     *  "senderRole":    中控:0 ; 手机:1 ; 云:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 云:2; 
     *   profileSet:
     *     {  
     *      情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *     }  
     * }
	 * */
	public void set_profile_set(Message msg) throws JSONException, SQLException, ParseException{
    	//JSONObject json=msg.json;
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
			msg.getJson().put("errorCode",PROFILE_SET_NOT_EXIST);     		
    	}else if( dbProfileSet.modifyTime.after(msgModifyTime)){	//云端较新  
			msg.getJson().put("errorCode",PROFILE_SET_OBSOLETE);    		
    	}else if( dbProfileSet.modifyTime.before(msgModifyTime)){
    		profileSetMap.put(key, msgProfileSet);
			msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS);   		
    	}    	
  		msg.setCommandID(SET_RROFILE_SET_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
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
    	//JSONObject json=msg.json;
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
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);    		
    	}else if((ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
    		ProfileSet.deleteProfileSetFromDB(mysql, ctrolID, profileSetID);
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profileSet not exist ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.setCommandID( DELETE_RROFILE_SET_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
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
    	Message replymsg=new Message(msg);
    	JSONObject json=msg.getJson();
    	ProfileSet profileSet=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int profileSetID=msg.getJson().getInt("profileSetID"); 
    	int sender=0;
    	if(json.has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}     	
    	String key=ctrolID+"_"+profileSetID;
    	if((profileSet= profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
    		jedis.publish(commandQueue, profileSet.toJsonObj().toString());
    		jedis.hset(currentProfileSet, key, profileSet.toJsonObj().toString());
    		if(sender==0){
	    		replymsg.setJson(new JSONObject());
	    		replymsg.getJson().put("errorCode",SUCCESS);
  	    		
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();  				
    		}
    	}else {
			log.warn("Can't switch room profileSet,profileSet doesn't exit. ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			replymsg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID( SWITCH_RROFILE_SET_ACK);
		replymsg.getJson().put("sender",2);
		replymsg.getJson().put("receiver",sender);
		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		
	}
	
    /*** 请求切换情景模式集,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
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
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   profileTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode": XXXX_NOT_EXIST}
     *   （2）如果查询的情景模式存在，则返回:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
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
    	ProfileTemplate profileTemplat=null;
    	int profileTemplatID=msg.getJson().getInt("profileTemplateID");
    	int sender=0;
    	if(  (profileTemplat=ProfileTemplate.getFromDB(mysql,  profileTemplatID))!=null){
    		msg.getJson().put("profileTemplate", profileTemplat.toJsonObj());
    		msg.getJson().put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_profile_template, profileTemplatID:"+profileTemplatID+" from Mysql.");
			msg.getJson().put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);
    	}
    	msg.setCommandID( GET_RROFILE_TEMPLATE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender);  
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    
    /*** 请求情景模板,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
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
     *  "senderRole":    中控:0 ; 手机:1 ; 云:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 云:2;
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
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//ProfileTemplate msgProfile=new ProfileTemplate(msg.getJson().getJSONObject("profileTemplate"));
    	ProfileTemplate dbProfile;
    	int profileTemplatID=msg.getJson().getInt("profileTemplatID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	int sender=0;
    	
    	if((dbProfile=ProfileTemplate.getFromDB(mysql, profileTemplatID))==null){
			msg.getJson().put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);    		
    	}else if(  dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
			msg.getJson().put("errorCode",PROFILE_TEMPLATE_OBSOLETE);    		
    	}else if(  dbProfile.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
			msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_RROFILE_TEMPLATE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 上传或者下发情景模板,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
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
    	//JSONObject json=msg.json;
    	Device device=new Device();
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	String key=ctrolID+"_"+deviceID;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	if( (device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
    		msg.getJson().put("device", device.toJsonObj());
    		msg.getJson().put("errorCode",SUCCESS);   
    	}else {
			log.warn("Can't get_one_device, ctrolID:"+ctrolID+"deviceID: "+ deviceID+" from deviceMap or Mysql.");
			msg.getJson().put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( GET_ONE_DEVICE_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
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
    	
    	if((dbDevice=this.deviceMap.get(key))==null  && (dbDevice=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))==null ){	
    		msg.getJson().put("errorCode",DEVICE_NOT_EXIST);    		
    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ ////云端较新  
			msg.getJson().put("errorCode",DEVICE_OBSOLETE);   
		}else if (dbDevice.modifyTime.before(msgModifyTime)){ //云端较旧
    		this.deviceMap.put(key, msgDevice);
    		msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS); 
		}
  		msg.setCommandID(SET_ONE_DEVICE_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
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
    	//JSONObject json=msg.json;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	String key=ctrolID+"_"+deviceID;
    	Device device=null;
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	if((device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
    		deviceMap.remove(key);
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);    		
    	}else {
			log.warn("room_device not exist ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			msg.getJson().put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.setCommandID( DELETE_ONE_DEVICE_ACK);
		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
	/*** 切换某个家电状态
	 * 	 <pre>例如对应json消息体如下格式 ：
	 *   {
         sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     ctrolID:1234567
	 *     deviceID:7654321
     *   }
	 * @throws JSONException 
	 * @throws SQLException */
	public void switch_app_state(Message msg) throws JSONException, SQLException{
	   	Message replymsg=new Message(msg);
    	Device device=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int deviceID=msg.getJson().getInt("deviceID");
    	int sender=0;
    	if(msg.getJson().has("sender")){
    		sender=msg.getJson().getInt("sender"); 
    	}
    	String key=ctrolID+"_"+deviceID;
    	if((device= deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null){
    		jedis.publish(commandQueue, device.toJsonObj().toString());
    		jedis.hset(currentDeviceState, key, device.toJsonObj().toString());
    		if(sender==0){
	    		replymsg.setJson(new JSONObject());
	    		replymsg.getJson().put("errorCode",SUCCESS); 	    		
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();   			
    		}
    	}else {
			log.warn("Can't switch room device,device doesn't exit. ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			replymsg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(SWITCH_DEVICE_STATE_ACK);
    	replymsg.getJson().put("sender",2);
    	replymsg.getJson().put("receiver",sender); 
		try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
    /*** 请求切换某个家电状态,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  云:2;  web:3;  主服务:4;  消息服务:5; ...
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
	public void send_warning_msg(final Message msg){
		if(this.msgSock!=null &&  !this.msgSock.isOutputShutdown()  && !this.msgSock.isClosed())
		msg.writeBytesToSock(this.msgSock);		
	}   
	
    /*** 请求触发模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发模板不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发模板存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567,
     *   triggerTemplateID:7654321,
     *   triggerTemplate: 
     *         {
     *          触发模板的json格式 
     *         }
     * }
     *                      
     */
    public void get_trigger_template(Message msg) throws JSONException, SQLException{
    	Trigger trigger=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	int sender=0;
    	String key=ctrolID+"_"+triggerID;
    	if( (trigger= triggerMap.get(key))!=null  || (trigger=Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
    		msg.getJson().put("triggerTemplate", trigger.toJson());
    		msg.getJson().put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender);  
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
     *  "senderRole":    中控:0 ; 手机:1 ; 云:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 云:2;
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
    	//JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger=Trigger.fromJson(msg.getJson().getJSONObject("triggerTemplate"));
    	Trigger dbTrigger;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
    	
    	if((dbTrigger=this.triggerMap.get(key))==null && (dbTrigger=Trigger.getFromDB(mysql, ctrolID, triggerID))==null){
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);    		
    	}else if(  dbTrigger.getModifyTime().after(msgModifyTime)){	//云端较新  
			msg.getJson().put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
    		this.triggerMap.put(key, msgTrigger);
			msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 请求触发规则
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   ctrolID:1234567
     *   triggerTemplateID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的触发规则不存在，返回jason： {"errorCode": XXXX}
     *   （2）如果查询的触发规则存在，则返回:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
     *   receiver:  中控:0 ; 手机:1 ; 云:2; 3:主服务; 4 消息服务; ...
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
    	Trigger trigger=null;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	int sender=0;
    	String key=ctrolID+"_"+triggerID;
    	if( (trigger= triggerMap.get(key))!=null  || (trigger=Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
    		msg.getJson().put("trigger", trigger.toJson());
    		msg.getJson().put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender);  
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
     *  "senderRole":    中控:0 ; 手机:1 ; 云:2;
     *  "receiverRole":  中控:0 ; 手机:1 ; 云:2;
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
    	//JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger=Trigger.fromJson(msg.getJson().getJSONObject("trigger"));
    	Trigger dbTrigger;
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	Date msgModifyTime=sdf.parse(msg.getJson().getString("modifyTime"));
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
    	
    	if((dbTrigger=this.triggerMap.get(key))==null && (dbTrigger=Trigger.getFromDB(mysql, ctrolID, triggerID))==null){
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);    		
    	}else if(  dbTrigger.getModifyTime().after(msgModifyTime)){	//云端较新  
			msg.getJson().put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
    		this.triggerMap.put(key, msgTrigger);
			msg.setJson(new JSONObject());
			msg.getJson().put("errorCode",SUCCESS);   
			}    	
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		msg.getJson().put("sender",2);
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
		msg.getJson().put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除触发模板
     * <pre>传入的json格式为：
     * { 
     *   senderRole:    中控:0 ; 手机:1 ; 云:2;
     *   receiverRole:  中控:0 ; 手机:1 ; 云:2;
     *   ctrolID:1234567
     *   triggerID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
           
     */
    public void delete_trigger(Message msg) throws JSONException, SQLException{
    	int ctrolID=msg.getJson().getInt("ctrolID");
    	int triggerID=msg.getJson().getInt("triggerID");
    	String key=ctrolID+"_"+triggerID;
    	int sender=0;
		if(msg.getJson().has("sender")){
		   sender=msg.getJson().getInt("sender");
		}
    	if(triggerMap.containsKey(key)){
    		triggerMap.remove(key);
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);    		
    	}else if((Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
    		Trigger.deleteFromDB(mysql, ctrolID, triggerID);
    		msg.setJson(new JSONObject());
    		msg.getJson().put("errorCode", SUCCESS);
    	}else {
			log.warn("room_trigger not exist ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
			msg.getJson().put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);

		msg.getJson().put("sender",2);
		msg.getJson().put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    

	public static void main(String[] args) {
		Config cf= new Config();
		LogicControl lc= new LogicControl(cf);
	}		
}
