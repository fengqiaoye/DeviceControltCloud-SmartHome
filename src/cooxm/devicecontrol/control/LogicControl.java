﻿package cooxm.devicecontrol.control;
/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





import cooxm.devicecontrol.constant.Role;
import cooxm.devicecontrol.constant.WarnID;
import cooxm.devicecontrol.device.Device;
import cooxm.devicecontrol.device.DeviceMap;
import cooxm.devicecontrol.device.DeviceState;
import cooxm.devicecontrol.device.EnviromentState;
import cooxm.devicecontrol.device.Factor;
import cooxm.devicecontrol.device.FactorDict;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.device.ProfileMap;
import cooxm.devicecontrol.device.ProfileSet;
import cooxm.devicecontrol.device.ProfileTemplate;
import cooxm.devicecontrol.device.Room;
import cooxm.devicecontrol.device.RoomMap;
import cooxm.devicecontrol.device.RoomTypeDic;
import cooxm.devicecontrol.device.Trigger;
import cooxm.devicecontrol.device.TriggerMap;
import cooxm.devicecontrol.device.TriggerTemplate;
import cooxm.devicecontrol.device.TriggerTemplateMap;
import cooxm.devicecontrol.device.Warn;
import cooxm.devicecontrol.encyco.TuringCatAesCrypto;
import cooxm.devicecontrol.security.MonitorStatus;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.socket.SocketClient;
import cooxm.devicecontrol.synchronize.IRFileDownload;
import cooxm.devicecontrol.synchronize.IRMatch2;
import cooxm.devicecontrol.util.MySqlClass;
import cooxm.devicecontrol.util.StringUtility;
import cooxm.devicecontrol.util.JedisUtil;

public class LogicControl {	
	
	static long cookieNo = ((System.currentTimeMillis()/1000)%(24*3600))*10000;
	
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
	private static final short GET_ALL_PROFILE_ACK     		    =   COMMAND_START+5 + COMMAND_ACK_OFFSET;	
	
    /*** 上报一个用户家里所有情景模式   @see set_room_profile()  */
	private static final short SET_ALL_PROFILES					=	COMMAND_START+6;	
    /*** 上报一个用户家里所有情景模式的回复   @see set_room_profile_ack()  */
	private static final short SET_ALL_PROFILE_ACK	    		=	COMMAND_START+6+COMMAND_ACK_OFFSET;
	
    /*** 请求 一个用户家里所有情景模式列表，不含情景细节     */
	private static final short GET_PROFILE_LIST					=	COMMAND_START+7;	
    /*** 请求一个用户家里所的情景模式 回复    */
	private static final short GET_PROFILE_LIST_ACK     		=   COMMAND_START+7 + COMMAND_ACK_OFFSET;
	
    /*** 请求 一个用户家里所有情景模式列表，不含情景细节     */
	private static final short SET_PROFILE_LIST					=	COMMAND_START+8;	
    /*** 请求一个用户家里所的情景模式 回复    */
	private static final short SET_PROFILE_LIST_ACK     		=   COMMAND_START+8 + COMMAND_ACK_OFFSET;
	
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
	
	/*** 请求一个用户家里所有 情景模式集列表 */	
	private static final short GET_RROFILE_SET_LIST					=	COMMAND_START+29;
	/*** 请求一个用户家里所有 情景模式集 的回复*/	
	private static final short GET_RROFILE_SET_LIST_ACK				=	COMMAND_START+29+COMMAND_ACK_OFFSET;
	
	/*** 请求所有的情景模式模板 ，包含详情*/
	private static final short GET_ALL_RROFILE_TEMPLATE				=	COMMAND_START+30;	
	/*** 请求情景模式模板 的回复*/
	private static final short GET_ALL_RROFILE_TEMPLATE_ACK			=	COMMAND_START+30+COMMAND_ACK_OFFSET;
	
	/*** 请求所有情景模式模板 的列表的ID*/
	private static final short GET_RROFILE_TEMPLATE_LIST			=	COMMAND_START+31;	
	/*** 请求情景模式模板 的回复*/
	private static final short GET_RROFILE_TEMPLATE_LIST_ACK		=	COMMAND_START+31+COMMAND_ACK_OFFSET;
	
	/*** 请求家庭当前所处的情景模式*/
	private static final short GET_ACTIVE_RROFILE					=	COMMAND_START+32;	
	/*** 请求家庭当前所处的情景模式 的ack*/
	private static final short GET_ACTIVE_RROFILE_ACK				=	COMMAND_START+32+COMMAND_ACK_OFFSET;
	
	
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
	
	/*** 请求一个用户家里所有家电列表*/
	private static final short GET_DEVICE_LIST				=	COMMAND_START+47;
	/*** 请求一个用户家里所有家电列表 的回复*/
	private static final short GET_DEVICE_LIST_ACK			=	COMMAND_START+47+COMMAND_ACK_OFFSET;
	
	/*** 切换某个家电状态*/
	public static final short SWITCH_SIMPLE_DEVICE_STATE	=	COMMAND_START+48;
	/*** 切换某个家电状态 的回复*/
	private static final short SWITCH_SIMPLE_DEVICE_STATE_ACK		=	COMMAND_START+48+COMMAND_ACK_OFFSET;
	
	/*** 请求一个用户家里所有家电列表*/
	private static final short SET_DEVICE_LIST				=	COMMAND_START+49;
	/*** 请求一个用户家里所有家电列表 的回复*/
	private static final short SET_DEVICE_LIST_ACK			=	COMMAND_START+49+COMMAND_ACK_OFFSET;
	
	/*** 请求设备字典*/
	private static final short GET_DEVICE_DICTIONARY				=	COMMAND_START+50;
	/*** 请求设备字典ack*/
	private static final short GET_DEVICE_DICTIONARY_ACK			=	COMMAND_START+50+COMMAND_ACK_OFFSET;
	
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
	
	/*** 请求房间列表*/
	private static final short GET_ROOM_LIST				=	COMMAND_START+56;
	/*** 请求房间列表 的回复*/
	private static final short GET_ROOM_LIST_ACK			=	COMMAND_START+56+COMMAND_ACK_OFFSET;	
	
	/*** 请求触发规则模板 */
	private static final short GET_TRIGGER_TEMPLATE				=	COMMAND_START+61;	
	/*** 请求触发规则模板 的回复*/
	private static final short GET_TRIGGER_TEMPLATE_ACK			=	COMMAND_START+61+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则模板 */
	private static final short SET_TRIGGER_TEMPLATE				=	COMMAND_START+62;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short SET_TRIGGER_TEMPLATE_ACK			=	COMMAND_START+62+COMMAND_ACK_OFFSET;
	
	/*** 请求触发规则 */
	private static final short GET_TRIGGER				=	COMMAND_START+71;	
	/*** 请求触发规则模板 的回复*/
	private static final short GET_TRIGGER_ACK			=	COMMAND_START+71+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则 */
	private static final short SET_TRIGGER				=	COMMAND_START+72;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short SET_TRIGGER_ACK			=	COMMAND_START+72+COMMAND_ACK_OFFSET;
	
	/*** 上报或者下发触发规则模板 */
	private static final short DELETE_TRIGGER				=	COMMAND_START+73;	
	/*** 上报或者下发 触发规则 模板回复*/
	private static final short DELETE_TRIGGER_ACK			=	COMMAND_START+73+COMMAND_ACK_OFFSET;
	
	/*** 请求触发模板列表 */
	private static final short GET_TRIGGER_TEMPLATE_LIST				=	COMMAND_START+74;	
	/*** 请求触发模板列表回复*/
	private static final short GET_TRIGGER_TEMPLATE_LIST_ACK			=	COMMAND_START+74+COMMAND_ACK_OFFSET;
	
	/*** 请求触发模板的头部 或者索引，而不包含规则详细细节 */
	private static final short GET_TRIGGER_TEMPLATE_HEADER				=	COMMAND_START+75;	
	/*** 请求触发模板回复*/
	private static final short GET_TRIGGER_TEMPLATE_HEADER_ACK			=	COMMAND_START+75+COMMAND_ACK_OFFSET;
	
	
	/*** 请求触发规则的头部 或者索引，而不包含规则详细细节 */
	private static final short GET_TRIGGER_HEADER				=	COMMAND_START+76;	
	/*** 请求触发规则回复*/
	private static final short GET_TRIGGER_HEADER_ACK			=	COMMAND_START+76+COMMAND_ACK_OFFSET;
	
	/*** 上报触发规则头部 或者索引，而不包含规则详细细节 */
	private static final short SET_TRIGGER_HEADER				=	COMMAND_START+77;	
	/*** 上报触发规则头部回复*/
	private static final short SET_TRIGGER_HEADER_ACK			=	COMMAND_START+77+COMMAND_ACK_OFFSET;	
	
	
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
	
	/**上传的扑捉到红外码值 ，用来识别遥控器的型号*/
	private static final short UPLOAD_REMOTE_CONTROL_OPERATION       =   COMMAND_START+94;
	private static final short UPLOAD_REMOTE_CONTROL_OPERATION_ACK   =   COMMAND_START+94+COMMAND_ACK_OFFSET;
	
	/**上传的扑捉到红外码值 ，用来识别遥控器的型号*/
	private static final short GET_ENVIROMENT_STATE       =   COMMAND_START+101;
	private static final short GET_ENVIROMENT_STATE_ACK   =   COMMAND_START+101+COMMAND_ACK_OFFSET;
	
	/**空的消息，什么事情也不做*/
	public static final short DO_NOTHING			      =   COMMAND_START+111;
	
    /*** 取消告警消息   */
	private static final short REPORT_CONTROL_MSG		    =	COMMAND_START+102;
    /*** 取消告警消息  的回复  */
	private static final short REPORT_CONTROL_MSG_ACK		=	COMMAND_START+102+COMMAND_ACK_OFFSET;
	
    /*** 请求语言   */
	private static final short GET_LANGUAGE		    	=	COMMAND_START+103;
    /*** 请求语言ACK   */
	private static final short GET_LANGUAGE_ACK		    =	COMMAND_START+103+COMMAND_ACK_OFFSET;
	
    /*** 设置语言 */
	private static final short SET_LANGUAGE		  	 	=	COMMAND_START+104;
    /*** 设置语言ack */
	private static final short SET_LANGUAGE_ACK		    =	COMMAND_START+104+COMMAND_ACK_OFFSET;
	
    /*** 请求布防状态 */
	private static final short GET_MONITOR_STATUS		  	 	=	COMMAND_START+105;
    /*** 请求布防状态 */
	private static final short GET_MONITOR_STATUS_ACK		    =	COMMAND_START+105+COMMAND_ACK_OFFSET;
	
    /*** 上报布防状态 */
	private static final short SET_MONITOR_STATUS		  	 	=	COMMAND_START+106;
    /*** 上报布防状态 */
	private static final short SET_MONITOR_STATUS_ACK		    =	COMMAND_START+106+COMMAND_ACK_OFFSET;
	
	

	
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
	
	/** profileSet陈旧，即上报的profileSet比云端陈旧 */
	private static final int PROFILE_SET_OBSOLETE     =	-50003;	
	private static final int PROFILE_SET_NOT_EXIST    = -50004;	
	
	/** profileTEMPLATE陈旧，即上报的profileTEMPLATE比云端陈旧 */
	private static final int PROFILE_TEMPLATE_OBSOLETE     =	-50005;	
	private static final int PROFILE_TEMPLATE_NOT_EXIST    = -50006;

	/** 上报的情景模式因素列表为空*/
	private static final int PROFILE_FACTORLIST_IS_EMPTY    = -50007;
	/** 情景集所包含的情景模式不存在*/
	private static final int PROFILESET_PROFILELIST_IS_INCONTACT    = -50008;
	
	/** 该用户不存在 当前正在运行的情景模式*/
	private static final int ACTIVE_PROFILE_NOT_EXIST    = -50009;
	
	
	
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
	/** 红外码无法识别*/
	public static final int INFRARED_CODE_NOT_RECOGNIZED = -50032;
	
	/** 下载红外码库时 没有告知家电类型*/
	public static final int UNKNOWN_DEVICE_TYPE          = -50033;
	
	/** 服务器没有通过认证*/
	public static final int SERVER_NOT_AUTHORIZED	  = -50041;
	
	/** 服务器没有通过认证*/
	public static final int IN_PROCESS_PLEASE_WAIT	  = -50051;
	
	/** JSON格式错误，导致解析失败*/
	public static final int JSON_PARSE_ERROR	     = -50061;
	
	/** 时间戳格式错误，导致解析失败,正确的格式为：yyyy-MM-dd HH:mm:ss*/
	public static final int TIME_PARSE_ERROR	     = -50062;
	
	/** 云后台SQL发生错误*/
	public static final int SQL_ERROR	     		= -50063;
	
	/**  房间不存在*/
	public static final int ROOM_NOT_EXIST	     	= -50071;
	
	
	/** 房间陈旧，云端比较新*/
	public static final int ROOM_OBSOLETE	     	= -50072;
	
	
	/**  用户家里 环境因素不存在*/
	public static final int HOUSE_STATE_NOT_EXIST = -50081;
	
	/**  情景模式不存在或者MYSQL读取错误*/
	public static final int TRIGGER_TEMPLATE_NOT_EXIST 	= -50091;
	
	public static final int TRIGGER_NOT_EXIST 			= -50092;
	
	public static final int TRIGGER_OBSOLETE 			= -50093;	
	
	public static final int LANGUAGE_NOT_EXIST 			= -50094;
	
	public static final int MONINTOR_STATUS_NOT_EXIST 	= -50095;

	/***********************   resource needed   ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	static Configure config=null;
	static MySqlClass mysql=null;
	SocketClient msgSock=null;
	JedisUtil jedis=null; 
	public static ProfileMap profileMap =null;
	//public static ProfileSetMap profileSetMap =null;
	public static DeviceMap deviceMap=null;
	TriggerMap triggerMap=null;
	TriggerTemplateMap  triggerTemplateMap=null;
	RoomMap roomMap=null;
	Map<Integer,ProfileTemplate> profileTemplateList=null;
	TimeOutMap to;
	String ir_file_dir;//=new File(cf.getValue("ir_file_path"));
	IRMatch2 im=new IRMatch2();
	List<FactorDict> factorDicList;
	public final static String currentProfile		= "currentProfile:";
	public final static String currentProfileSet	= "currentProfileSet:";
	public final static String currentDeviceState	= "currentDeviceState:";	
	public final static String roomBind				= "roomBind:";	
	public final static String roomList				= "roomList:";
	public final static String triggerState			= "triggerState:";
	public final static String houseState			= "houseState:";
	public final static String language				= "language:";
	public final static String monitorStatus		= "monitorStatus:";
    
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
		
		ir_file_dir=cf.getValue("ir_file_path");		
		mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		//this.jedis= new JedisUtil(redis_ip, redis_port,5000);	
		this.jedis=new JedisUtil(redis_ip, redis_port,9);
		jedis.select(9);
		try{
	 	
			this.profileMap= new ProfileMap(mysql);
			//this.profileSetMap= new ProfileSetMap(mysql);
			this.deviceMap=new DeviceMap(mysql,jedis);
			this.profileTemplateList=ProfileTemplate.getAllFromDB(mysql);
			this.triggerTemplateMap=new  TriggerTemplateMap(mysql);
			this.roomMap=new RoomMap(mysql,jedis);
			factorDicList=FactorDict.getDeviceDictList(mysql);
			this.to=new TimeOutMap();			
			Thread timerMapTread=new Thread(to);
			timerMapTread.setName("timerMapTread");
			timerMapTread.start();
			
		} catch (SQLException  e) {
			e.printStackTrace(); logException(e);
			logException(e);
		}
		log.info("Initialization of map successful :  profileMap size="+profileMap.size()
				//+";profileSetMap size="+profileSetMap.size()
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
		case SWITCH_ROOM_PROFILE_ACK:	
			switch_room_profile_ack(msg);
			break;
		case GET_ALL_PROFILES:			
			get_all_profile(msg);
			break;
		case SET_ALL_PROFILES:
			set_all_profile(msg); 
			break;	
		case GET_PROFILE_LIST:			
			get_profile_list(msg);
			break;
		case SET_PROFILE_LIST:			
			set_profile_list(msg);
			break;
		case GET_RROFILE_SET:	
			get_profile_set(msg);
			break;
		case SET_RROFILE_SET:	
			set_profile_set(msg);
			break;	
		case DELETE_RROFILE_SET:
			delete_profile_set(msg);
			break;
		case SWITCH_RROFILE_SET:	
			switch_profile_set2(msg);  //2015-07-11 和文辉协定采用新的切换逻辑：云端根据模板ID找到情景集  
			break;
		case SWITCH_RROFILE_SET_ACK:	
			switch_profile_set_ack(msg);
			break;
		case GET_ALL_RROFILE_SET:	
			get_all_profile_set(msg);
			break;
		case SET_ALL_RROFILE_SET:	
			set_all_profile_set(msg);
			break;
		case GET_RROFILE_SET_LIST:	
			get_profile_set_list(msg);
			break;
		case GET_RROFILE_TEMPLATE: 	
			get_profile_template(msg);
			break;
		case SET_RROFILE_TEMPLATE:	
			set_profile_template(msg);
			break;	
		case GET_ALL_RROFILE_TEMPLATE: 	
			get_all_profile_template(msg);
			break;			
		case GET_RROFILE_TEMPLATE_LIST: 	
			get_profile_template_list(msg);
			break;
		case GET_ACTIVE_RROFILE:
			get_active_profile(msg);
			break;
		case GET_ONE_DEVICE:	
			get_one_device(msg);
			break;
		case SET_ONE_DEVICE:	
			set_one_device(msg, mysql);
			break;	
		case DELETE_ONE_DEVICE:
			delete_one_device(msg);
			break;			
		case SWITCH_DEVICE_STATE:			
		case SWITCH_SIMPLE_DEVICE_STATE:
			switch_device_state(msg);
			break;
		case SWITCH_DEVICE_STATE_ACK:			
		case SWITCH_SIMPLE_DEVICE_STATE_ACK:
			switch_device_state_ack(msg);
			break;
			
		case GET_ALL_DEVICE:	
			get_all_device(msg);
			break;
		case SET_ALL_DEVICE:	
			set_all_device(msg, mysql);
			break;
		case GET_DEVICE_LIST:	
			get_device_list(msg);
			break;
		case SET_DEVICE_LIST:	
			set_device_list(msg);
			break;
		case GET_DEVICE_DICTIONARY:	
			get_device_dictionary(msg);
			break;	
		case GET_ONE_ROOM:	
			get_one_room(msg);
			break;
		case SET_ONE_ROOM:	
			set_one_room(msg, mysql);
			break;	
		case DELETE_ONE_ROOM:
			delete_one_room(msg);
			break;	
		case GET_ALL_ROOM:	
			get_all_room(msg);
			break;
		case SET_ALL_ROOM:	
			set_all_room(msg, mysql);
			break;
		case GET_ROOM_LIST:	
			get_room_list(msg);
			break;
		case REPORT_CONTROL_MSG:	
			report_control_msg(msg);
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
		case GET_TRIGGER_TEMPLATE_LIST:
			get_trigger_templat_list(msg);
			break;
		case GET_TRIGGER_TEMPLATE_HEADER:
			get_trigger_template_header(msg);
			break;
		case GET_TRIGGER_HEADER:
			get_trigger_header(msg);
			break;
		case SET_TRIGGER_HEADER:
			set_trigger_header(msg);
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
		case UPLOAD_REMOTE_CONTROL_OPERATION:
			upload_remote_control_operation(msg);
			break;	
		case GET_ENVIROMENT_STATE:
			get_enviroment_state(msg);
			break;
		case GET_LANGUAGE:	
			get_language(msg);
			break;
		case GET_LANGUAGE_ACK:	
			get_language_ack(msg);
			break;
		case WARNING_MSG:	
			warning_msg(msg);
			break;
		case WARNING_MSG_ACK:	
			warning_msg_ack(msg);
			break;
		case DO_NOTHING:
			break;
		case GET_MONITOR_STATUS:	
			get_monitor_status(msg);
			break;
		case GET_MONITOR_STATUS_ACK:	
			get_monitor_status_ack(msg);
			break;
		case SET_MONITOR_STATUS:	
			set_monitor_status(msg);
			break;
		case SET_MONITOR_STATUS_ACK:	
			set_monitor_status_ack(msg);
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
					json.put("errorDescription",e1.toString());
				} catch (JSONException e) {
					e.printStackTrace(); logException(e);
					logException(e);
				}
				
			}
			if(msg.getCommandID()<=0x4000){
			   msg.setCommandID((short) (msg.getCommandID()+ LogicControl.COMMAND_ACK_OFFSET));
			}
			msg.setJson(json);
			try {
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace(); logException(e);
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
	    	if( (profile= profileMap.get(key))!=null  /*|| (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null*/){
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
		msg.setJson(json);
    	msg.setCommandID(GET_ROOM_PROFILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender);
			
			//if(msgProfile.getFactorList()==null || msgProfile.getFactorList().size()==0){
			//	json.put("errorCode",PROFILE_FACTORLIST_IS_EMPTY); 
			//}else{
	    	Profile dbProfile;
	    	int ctrolID=msgProfile.getCtrolID();
	    	int profileID=msgProfile.getProfileID();
	    	Date msgModifyTime=msgProfile.getModifyTime();
	    	String key=ctrolID+"_"+profileID;
	    	
	    	if( (dbProfile=this.profileMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
				json.put("errorCode",PROFILE_OBSOLETE);    	
				log.error("Profile in Cloud is newer than from user, ctrolID:"+ctrolID+" profileID:"+profileID+",cookieID="+msg.getCookie());
	    	}else { //云端较旧  或者 不存在，则保存
	    		Profile p=this.profileMap.put(key, msgProfile);
				if(p!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}
			}
	    	/*Profile pr = this.profileMap.remove(key);
    		Profile p=this.profileMap.put(key, msgProfile);
			if(p!=null){
				json.put("errorCode",SUCCESS); 
			}else{
				json.put("errorCode",SQL_ERROR); 
			}*/
			//}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (ParseException e) {
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace(); logException(e);
		}
    	msg.setJson(json);
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    		Profile p=profileMap.get(key);
	    		profileMap.remove(key);
	    		json.put("errorCode", SUCCESS);  
	    		jedis.hdel(currentProfile+ctrolID, p.getRoomID()+"");
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}

		}
    	msg.setCommandID(DELETE_ROOM_PROFILE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	DateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmssSSS");
    	JSONObject json=new JSONObject();
    	Message replyMsg=new Message(msg);
    	Profile profile=null;
    	int ctrolID;
    	int sender=0;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileID=msg.getJson().getInt("profileID");
	    	int roomID=  msg.getJson().getInt("roomID");
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);	
			json.put("receiver",sender);
	    	String key=ctrolID+"_"+profileID;
			profile=profileMap.getProfileByRoomIDTemplateID(ctrolID, roomID, profileID);  //注意此处profileID 的值是templateID
	    	if(profile!=null /*|| (profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID))!=null*/){				
	    		for (Factor factor : profile.getFactorList()) {   //将情景中家电的每一个开关记录到redis中
	    			String key2=currentDeviceState+ctrolID;
	    			JSONObject stateJson= new JSONObject();
	    			stateJson.put("sender", Role.PROFILE_SWITCH);
	    			stateJson.put("time", sdf.format(new Date()));
	    			//stateJson.put("tempID", profileID);  //情景模板ID
					stateJson.put("devType", factor.getFactorID());
					String stateStr=null;
					if (factor.getFactorID()==541) {
						try {
							stateStr=jedis.hget(key2, factor.getDeviceID()+"");
						} catch (Exception e) {
							stateStr=null;
							e.printStackTrace();
						}
						
						DeviceState state=null;
						int onOff=factor.getMinValue()>0 ? 1: 0;
						if (stateStr!=null) {
							JSONObject jsonState=new JSONObject(stateStr);
							if (jsonState.has("state")) {
								state=new DeviceState(jsonState.getJSONObject("state"));
								state.setOnOff(factor.getMinValue());
							}else {
								state=new DeviceState(onOff, 0, 0, 0, factor.getMinValue(), 0, factor.getMinValue());
							}							
						}else{
							state=new DeviceState(onOff, 0, 0, 0, factor.getMinValue(), 0, factor.getMinValue());
						}
						stateJson.put("state", state.toJson());
						try{
							jedis.hset(key2, factor.getDeviceID()+"", stateJson.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						}else{
						int keyType=factor.getMinValue()==1?501:502;
						stateJson.put("keyType", keyType);
						jedis.hset(key2, factor.getDeviceID()+"", stateJson.toString());
					}
				}
	    		
	    		if (profileID==2) {      // 观影模式关窗帘
	    			List<Device> devList=deviceMap.getDevicesByroomIDDevType(ctrolID, roomID, 421);
	    			double value=EnviromentState.getFactorStateByRoomIDfactorID(ctrolID, roomID, 2501, jedis);  //光强度
	    			Calendar calender = Calendar.getInstance();
	    			int time = calender.get(Calendar.HOUR_OF_DAY)*100+calender.get(Calendar.MINUTE);
	    			if ( time<800 || time>1800 ||( time<=1800 && time>=800 && value>=600)) {  //晚6点之后进入观影模式，直接关窗帘; 或者白天根据光照判断
	    				for (int i = 0; i < devList.size(); i++) {
	    					JSONObject json2=new JSONObject();
							json2.put("ctrolID", ctrolID);
							json2.put("roomID", roomID);
							json2.put("deviceID", devList.get(i).getDeviceID());
							json2.put("deviceType", 421);
							json2.put("sender",2);
							json2.put("receiver",0); 
							json2.put("keyType", 502);							
		    				Message msg3=new Message((short) (LogicControl.SWITCH_SIMPLE_DEVICE_STATE), (cookieNo++) +"_2",json2 );
		    				msg3.setServerID(1);
		    				CtrolSocketServer.sendCommandQueue.offer(msg3, 100, TimeUnit.MILLISECONDS);//转发给中控
						}
	    			}	    			
				}/*else if (profileID==4) { //观影模式关结束，切到居家模式，打开窗帘，窗帘归位
	    			List<Device> devList=deviceMap.getDevicesByroomIDDevType(ctrolID, roomID, 421);
	    			//double value=EnviromentState.getFactorStateByRoomIDfactorID(ctrolID, roomID, 2501, jedis);  //光强度
	    			Calendar calender = Calendar.getInstance();
	    			int time = calender.get(Calendar.HOUR_OF_DAY)*100+calender.get(Calendar.MINUTE);
	    			if (time>800 || time<1800 ) {  //晚6点之后进入观影模式，直接关窗; 或者白天根据光照判断
	    				for (int i = 0; i < devList.size(); i++) {
	    					JSONObject json2=new JSONObject();
							json2.put("ctrolID", ctrolID);
							json2.put("roomID", roomID);
							json2.put("deviceID", devList.get(i).getDeviceID());
							json2.put("deviceType", 421);
							json2.put("sender",2);
							json2.put("receiver",0); 
							json2.put("keyType", 501); //打开
							//long cookieNo = ((System.currentTimeMillis()/1000)%(24*3600))*10000;
		    				Message msg3=new Message((short) (LogicControl.SWITCH_SIMPLE_DEVICE_STATE), (cookieNo++)+"_2",json2 );
		    				msg3.setServerID(1);
		    				CtrolSocketServer.sendCommandQueue.offer(msg3, 100, TimeUnit.MILLISECONDS);//转发给中控
						}
	    			}
				}*/
	    		Device.batchSwitchRoleByRoomID(jedis, ctrolID, Role.PROFILE_SWITCH, roomID);
	    		
	        	String key3=LogicControl.currentProfile+ctrolID;
	        	JSONObject pjSON=profile.toJsonObj();
	        	pjSON.put("switchTime", sdf.format(new Date()));
	    		jedis.hset(key3, roomID+"", pjSON.toString());
				
	    		if(sender==0  /*||profile.isEmpty()*/){   //来自中控，或者情景详情为空
	    			json.put("errorCode",SUCCESS);
	    		}else {
	    			to.put(msg.getCookie(),new Message(msg));
	    			
	    			Message msg2=new Message(msg);
	    			msg2.setServerID(1);           //将主服务器接收此消息
        			msg2.getJson().put("receiver",0); 
        			msg2.getJson().put("sender",2); 
        			msg2.setJson(msg2.getJson());
	    			CtrolSocketServer.sendCommandQueue.offer(msg2, 100, TimeUnit.MILLISECONDS);	    			  	
	    			return;
	    		}
	    	}else {
				log.error("Can't switch room profile,profile doesn't exist. ctrolID:"+ctrolID+" profileID:"+profileID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}


		} catch (Exception e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}
		replyMsg.setCommandID(SWITCH_ROOM_PROFILE_ACK);
		replyMsg.setJson(json);
		//if(sender!=0){
			try {
				CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		//}
	}
    
    /*** 请求切换情景模式,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_ack(final Message msg){
		to.put(msg.getCookie(),msg);
//		TimeOutTread to=new TimeOutTread(3,msg);
//		to.start();
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
        msg.setJson(json);
  		msg.setCommandID(GET_ALL_PROFILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
			List<String> msgProfileList=new ArrayList<String>();
	/*先保存*/for (int i=0;i<ja.length();i++) {
				Profile msgProfile = new Profile(ja.getJSONObject(i));
		    	int profileID=msgProfile.getProfileID();
		    	String key=ctrolID+"_"+profileID;
		    	msgProfileList.add(key);

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
	/*删除云端存在但是上报不存在的*/
	      List<Profile> dbProfileList=profileMap.getProfilesByctrolID(ctrolID);
	      for (Iterator iterator = dbProfileList.iterator(); iterator.hasNext();) {
			Profile profile = (Profile) iterator.next();
			String key=profile.getCtrolID()+"_"+profile.getProfileID();
			if(!msgProfileList.contains(key)){  //不存在则删除
				profileMap.remove(key);
			}			
		}

		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setJson(json);
  		msg.setCommandID(SET_ALL_PROFILE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}     	
    }
    
	/*** 请求查询一个用户家里所有情景模式列表，不包含情景详细细节
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
     *         [  { profileID:123,modifytime:2015-06-01 12:13:14      },
     *            { profileID:124,modifytime:2015-06-01 12:13:14      },
     *            { profileID:125,modifytime:2015-06-01 12:13:14      },
     *         ]
     * }                 
     */
    public void get_profile_list(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

			profileList= profileMap.getProfilesByctrolID(ctrolID);	
			int i=0;
	    	if( profileList.size()!=0  ){
	    		JSONArray ja=new JSONArray();
	    		if(msg.getJson().has("count")){
	    			int count=msg.getJson().getInt("count");
	    			int offset=msg.getJson().getInt("offset");
		    		for (Profile profile : profileList) {	 
		    			if ( i >= offset && i < offset+count) {
			    			JSONObject jo=new JSONObject();
			    			jo.put("profileID", profile.getProfileID());
			    			jo.put("modifyTime", profile.getModifyTime().getTime()/1000); //sdf.format(profile.getModifyTime())
							ja.put(jo);	
						}else if (i>=offset+count){
							break;
						}
		    			i++;    			
					}
		    		json.put("offset", i);
		    		if(i>=profileList.size()){
		    			json.put("end", 1);
		    		}else{
		    			json.put("end", 0);
		    		}
	    		}else{
	    			for (Profile profile : profileList) {	
		    			JSONObject jo=new JSONObject();
		    			jo.put("profileID", profile.getProfileID());
		    			jo.put("modifyTime", profile.getModifyTime().getTime()/1000); //sdf.format(profile.getModifyTime())
						ja.put(jo);
	    			}
	    		}

	    		json.put("profileArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
	    		json.put("end", 1);
	    		json.put("offset", 0);
				log.error("Can't get profile list by ctrolID:"+ctrolID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_NOT_EXIST);
	    	}
 
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
        msg.setJson(json);
  		msg.setCommandID(GET_PROFILE_LIST_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    
	/*** 获取一个用户家里所有设备
	 * 	 <pre>请求对应json消息体为：
	 *   {
	 *     sender:0
	 *     receiver:2
	 *     ctrolID:1234567
	 *     profileArray:
	 *     [ 
	 *        {profileID:101,modifyTime:"2015-07-07 12:13:14"},
	 *        {profileID:102,modifyTime:"2015-07-07 12:13:14"},
	 *     ]
	 *     
     *   }
     *   @return List< Device > 加电列表 的json格式
     *   回复消息对应json消息体为：
     *   {
     *                 errorcode:0                 
         }
     *   
	 * @throws JSONException 
     *   */
	public void set_profile_list(Message msg) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	List<Profile> profileList=new ArrayList<Profile>();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
			JSONArray ja =msg.getJson().getJSONArray("profileArray");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 	    	
			List<Integer> msgProfileList=new ArrayList<Integer>();
			if(ja!=null){				
				for (int i = 0; i < ja.length(); i++) {
					Integer profileID=ja.getJSONObject(i).getInt("profileID");
					msgProfileList.add(profileID);
				}				
			}
	    	if( (profileList=profileMap.getProfilesByctrolID(ctrolID)).size()!=0 ){
	    		for (Profile profile : profileList) {
	    			if(!msgProfileList.contains(profile.getProfileID())){
	    				profileMap.remove(ctrolID+"_"+profile.getProfileID());
	    			}
				}	    		
	    	}
	    	json.put("errorCode",SUCCESS ); 
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( SET_PROFILE_LIST_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg,100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}	
	}

    

    
    /*** 查询情景模式集
     * <pre>传入的json格式为：
     * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
     *   ctrolID:1234567
     *   //profileSetID:7654321
     *   profileTemplateID:3    1睡眠  ，2观影，3离家， 4居家
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
    	List<Profile> profileList;
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
			
	    	if((profileList=profileMap.getProfileSetByTemplateID(ctrolID, profileSetID))!=null ){
	    		
	    		json.put("profileSet", ProfileSet.toJson(profileList));
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_profile_set, ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_SET_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_RROFILE_SET_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    

	
	/*** 设置 情景模式集

	 * <pre>Json格式和 设置情景模式 和 {@link cooxm.devicecontrol.cooxm.devicecontrol.control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：

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
	    	json.put("ctrolID",ctrolID);
			json.put("sender",2);
			json.put("receiver",sender); 

			//2015-06-01 和李鹏商量，商定profileSet分房间发送,set里面不再包含profile任何细节
    		if(msgProfileSet.getProfileList()==null){
    			json.put("errorCode",PROFILESET_PROFILELIST_IS_INCONTACT); 
    		}else{
    			List<Integer> profileIDs=msgProfileSet.getProfileList();
    			boolean contact_flag=true;
	    		for (int i = 0; i < profileIDs.size(); i++) {
					String tmpKey=msgProfileSet.getCtrolID()+"_"+profileIDs.get(i);
					if(!profileMap.containsKey(tmpKey)){
						json.put("errorCode",PROFILESET_PROFILELIST_IS_INCONTACT);
						contact_flag=false;
					}
				}
	    		if(contact_flag==true){
	    			json.put("errorCode",SUCCESS); 
		    		//ProfileSet ps=profileSetMap.put(key, msgProfileSet);
					/*JSONArray ja=msg.getJson().getJSONObject("profileSet").getJSONArray("profileArray");
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
						json.put("errorCode",PROFILESET_PROFILELIST_IS_INCONTACT); 
					}*/
	    		}
	    	 }
  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (ParseException e) {
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace(); logException(e);
		}
  		msg.setCommandID(SET_RROFILE_SET_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		} 	
	}
	
	//  --------------------  profileSet表 的数据表不再适用  -------------
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
    /*	JSONObject json=new JSONObject();
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
	    		ProfileSet ps=profileSetMap.remove(key);
	    		if(ps!=null){
	    		     json.put("errorCode", SUCCESS);
	    		}else{
	    			json.put("errorCode", SQL_ERROR);
	    		}
	    		json.put("errorCode", SUCCESS);    		
	    	}else if(ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID)!=null){
	    		ProfileSet.deleteProfileSetFromDB(mysql, ctrolID, profileSetID);
	    	}else{
				log.error("room_profileSet not exist ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
				json.put("errorCode",PROFILE_SET_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( DELETE_RROFILE_SET_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}   */ 	
    }
	

  /* -------------------------------------------2015-07-11 和文辉协定，情景集不再上报所包含的子情景ID	--------------------------------------------
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     sender:"control"/"mobile"/"cloud"
	 *     ctrolID:1234567
	 *     profileSetID:7654321
	 *     
     *   }
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
			json.put("sender",2);
			json.put("receiver",sender);
	    	String key2=ctrolID+"_"+profileSetID;
	    	String key=LogicControl.currentProfile+ctrolID;
	    	String key1=currentProfileSet+ctrolID;
	    	if((profileSet= profileSetMap.get(key2))!=null|| (profileSet=ProfileSet.getProfileSetFromDB(mysql, ctrolID, profileSetID))!=null){
	    		jedis.hset(key1, profileSet.getProfileSetID()+"", profileSet.toJsonObj().toString());
	    		Profile profile=null;
	    		boolean all_profile_is_empty=true;
	    		for (int i = 0; i < profileSet.getProfileList().size(); i++) {
	    			String setKey=ctrolID+"_"+profileSet.getProfileList().get(i);
	        		profile=profileMap.get(setKey); 
	        		if(profile!=null){
	    			 jedis.hset(key, profile.getRoomID()+"", profile.toJsonObj().toString());
	    			 all_profile_is_empty = all_profile_is_empty && profile.isEmpty(); //判断是否所有的情景的详情都为空
	        		}else{
	        			json.put("errorCode",NOT_EXIST);  
	        		}
				}
	    		if(sender==0 ||profileSet.isEmpty() ||all_profile_is_empty){ 
		    		json.put("errorCode",SUCCESS);		    		
	    		}else {
	    			msg.getJson().put("receiver",0);
	    			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
	    			to.put(msg.getCookie(),msg);
	    			return;  //这里先返回，等待超时线程去处理
	    		}
	    	}else {
				log.error("Can't switch room profileSet,profileSet doesn't exit. ctrolID:"+ctrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
				json.put("errorCode",NOT_EXIST);
	    	}

		} catch (Exception e) {
			e.printStackTrace(); logException(e);
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
	------------------------------------------------  end : 2015-07-11 和文辉协定，情景集不再上报所包含的子情景ID   ---------------------------*/
    
	
    
    
    /*------------------------------------------------ 2015-07-11 和文辉协定，以下为新的切换逻辑：云端根据模板ID找到情景集      ---------------------------*/
    /*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     sender:"control"/"mobile"/"cloud"
	 *     ctrolID:1234567
	 *     profileSetID:7654321
	 *     
     *   }*/
	public void switch_profile_set2(Message msg){  
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	Message replyMessage=new Message(msg);
    	Profile profile2=null;
    	int ctrolID;
    	int sender=0;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int profileSetID=msg.getJson().getInt("profileSetID"); 

	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}  
			json.put("sender",2);
			List<Profile> profileList=profileMap.getProfileSetByTemplateID(ctrolID, profileSetID);
			//boolean all_profile_is_empty=true;
			if(profileList!=null){
				for (Profile profile : profileList) {
		    		if(profile.getProfileSetID()>=1 ||profileSetID==5 ){  //如果该情景和 情景集联动，setID==1 或者 是手动模式
		        	 JSONObject pjSON=profile.toJsonObj();
		        	 pjSON.put("switchTime", sdf.format(new Date()));
	    			 jedis.hset(currentProfile+ctrolID, profile.getRoomID()+"", pjSON.toString());
	    			 //all_profile_is_empty = false; //判断是否所有的情景都不联动 
	    			 
	 	    		for (Factor factor : profile.getFactorList()) {   //将情景中家电的每一个开关记录到redis中
		    			String key2=currentDeviceState+ctrolID;
		    			JSONObject stateJson= new JSONObject();
		    			stateJson.put("sender", Role.PROFILE_SWITCH);
		    			stateJson.put("time", sdf.format(new Date()));
		    			//stateJson.put("tempID", profile.getProfileTemplateID());
						stateJson.put("devType", factor.getFactorID());
		    			
						if (factor.getFactorID()==541) {
							String stateStr=jedis.hget(key2, factor.getDeviceID()+"");
							DeviceState state=null;
							int onOff=factor.getMinValue()>0 ? 1: 0;
							if (stateStr!=null) {
								JSONObject jsonState=new JSONObject(stateStr);
								if (jsonState.has("state")) {
									state=new DeviceState(jsonState.getJSONObject("state"));
									state.setOnOff(factor.getMinValue());
								}else {
									state=new DeviceState(onOff, 0, 0, 0, factor.getMinValue(), 0, factor.getMinValue());
								}							
							}else{
								state=new DeviceState(onOff, 0, 0, 0, factor.getMinValue(), 0, factor.getMinValue());
							}
							stateJson.put("state", state.toJson());
							jedis.hset(key2, factor.getDeviceID()+"", stateJson.toString());  //标记操作家电的是 7
						}else{
							int keyType=factor.getMinValue()==1?501:502;
							stateJson.put("keyType", keyType);
							jedis.hset(key2, factor.getDeviceID()+"", stateJson.toString());
						}
					}
	    			Device.batchSwitchRole(jedis, ctrolID, Role.PROFILE_SWITCH); //将加电的操作者批量改为7
	    			
	 	    		if (profile.getProfileTemplateID()==2) {      // 观影模式关窗帘
		    			List<Device> devList=deviceMap.getDevicesByroomIDDevType(ctrolID, profile.getRoomID(), 421);
		    			double value=EnviromentState.getFactorStateByRoomIDfactorID(ctrolID, profile.getRoomID(), 2501, jedis);  //光强度
		    			Calendar calender = Calendar.getInstance();
		    			int time = calender.get(Calendar.HOUR_OF_DAY)*100+calender.get(Calendar.MINUTE);
		    			if(time<800 || time>1800 ||( time<=1800 && time>=800 && value>=600)){  //晚6点之后进入观影模式，直接关窗; 或者白天根据光照判断
		    				for (int i = 0; i < devList.size(); i++) {
		    					JSONObject json3=new JSONObject();
								json3.put("ctrolID", ctrolID);
								json3.put("roomID", profile.getRoomID());
								json3.put("deviceID", devList.get(i).getDeviceID());
								json3.put("deviceType", 421);
								json3.put("sender",2);
								json3.put("receiver",0); 
								json3.put("keyType", 502);
								//long cookieNo = ((System.currentTimeMillis()/1000)%(24*3600))*10000;
			    				Message msg3=new Message((short) (LogicControl.SWITCH_RROFILE_SET_ACK), (cookieNo++)+"_2",json3 );
			    				msg3.setServerID(1);
			    				CtrolSocketServer.sendCommandQueue.offer(msg3, 100, TimeUnit.MILLISECONDS);//转发给中控
							}
		    			}	    			
					}/*else if (profile.getProfileTemplateID()==4) { //观影模式关结束，切到居家模式，打开窗帘
		    			List<Device> devList=deviceMap.getDevicesByroomIDDevType(ctrolID, profile.getRoomID(), 421);
		    			//double value=EnviromentState.getFactorStateByRoomIDfactorID(ctrolID, roomID, 2501, jedis);  //光强度
		    			Calendar calender = Calendar.getInstance();
		    			int time = calender.get(Calendar.HOUR_OF_DAY)*100+calender.get(Calendar.MINUTE);
		    			if (time>800 || time<1800 ) {  //晚6点之后进入观影模式，直接关窗; 或者白天根据光照判断
		    				for (int i = 0; i < devList.size(); i++) {
		    					JSONObject json2=new JSONObject();
								json2.put("ctrolID", ctrolID);
								json2.put("roomID", profile.getRoomID());
								json2.put("deviceID", devList.get(i).getDeviceID());
								json2.put("deviceType", 421);
								json2.put("sender",2);
								json2.put("receiver",0); 
								json2.put("keyType", 501); //打开
								//long cookieNo = ((System.currentTimeMillis()/1000)%(24*3600))*10000;
			    				Message msg3=new Message((short) (LogicControl.SWITCH_SIMPLE_DEVICE_STATE), (cookieNo++)+"_2",json2 );
			    				msg3.setServerID(1);
			    				CtrolSocketServer.sendCommandQueue.offer(msg3, 100, TimeUnit.MILLISECONDS);//转发给中控
							}
		    			}
					}*/
	    			 
		    		}

				}				
	    		if(/*all_profile_is_empty ||*/sender==0){  //如果所有的情景都是空的，或者命令来自中控，则直接回复成功
		    		json.put("errorCode",SUCCESS);	
					json.put("receiver",sender); 	//原路返回				
	    		}else {   //命令来自手机且不为空，则需要转给中控来执行
	    			to.put(msg.getCookie(),new Message(msg));
	    			
	    			Message msg2=new Message(msg);
	    			msg2.setServerID(1);           //将主服务器接收此消息
        			msg2.getJson().put("receiver",0); 
        			msg2.getJson().put("sender",2); 
        			msg2.setJson(msg2.getJson());
	    			CtrolSocketServer.sendCommandQueue.offer(msg2, 100, TimeUnit.MILLISECONDS);//转发给中控

	    			return;  //这里先返回，等待超时线程去处理
	    		}
			}else{
	    		json.put("errorCode",PROFILE_SET_NOT_EXIST);	
				json.put("receiver",sender); 	//原路返回					
			}			

		} catch (Exception e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		replyMessage.setJson(json);
		replyMessage.setCommandID( SWITCH_RROFILE_SET_ACK);
		//if(sender!=0){
			try {
				CtrolSocketServer.sendCommandQueue.offer(replyMessage, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}	
		//}

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
    public void switch_profile_set_ack(final Message msg){
//		TimeOutTread to=new TimeOutTread(3,msg);
//		to.start();
		to.put(msg.getCookie(),msg);
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
    	/*List<ProfileSet> profileSetList=null;
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
				json.put("errorCode",NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(GET_ALL_RROFILE_SET_ACK);
    	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}  */  	
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
    	/*JSONObject json=new JSONObject();
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
			List<String> msgProfileList=new ArrayList<String>();
			
			for (int i=0;i<ja.length();i++) {
				ProfileSet msgProfile = new ProfileSet(ja.getJSONObject(i));
		    	int profileSetID=msgProfile.getProfileSetID();
		    	Date msgModifyTime=msgProfile.getModifyTime();		
				String key=ctrolID+"_"+profileSetID;
				msgProfileList.add(key);
				
		    	if( (dbProfile=this.profileSetMap.get(key))!=null && dbProfile.getModifyTime().after(msgModifyTime)){	//云端较新  
		    		log.error("Profile in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" profileID:"+profileSetID+".");
					json.put("errorCode",OBSOLETE);    		
		    	}else { //云端较旧  或者 不存在，则保存	    		
		    		ProfileSet p=this.profileSetMap.put(key, msgProfile);
					if(p!=null){
						json.put("errorCode",SUCCESS); 
					}else{
						json.put("errorCode",PROFILESET_PROFILELIST_IS_INCONTACT); 
						break;
					}
				}    		
			}  
			
			//删除云端存在但是上报不存在的
		      List<ProfileSet> dbProfileList=profileSetMap.getProfileSetsByctrolID(ctrolID);
		      for (Iterator iterator = dbProfileList.iterator(); iterator.hasNext();) {
		    	  ProfileSet profile = (ProfileSet) iterator.next();
				String key=profile.getCtrolID()+"_"+profile.getProfileSetID();
				if(!msgProfileList.contains(key)){  //上报不存在则删除
					profileSetMap.remove(key);
				}			
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
  		msg.setCommandID(SET_ALL_RROFILE_SET_ACK);
    	msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}  */   	
    }
    
	/*** 请求查询一个用户家里所有情景模式列表，不含情景详细信息
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
     *         [  { profileSetID:123，modifyTime: 2015-06-01 12:13:14     },
     *            { profileSetID:123，modifyTime: 2015-06-01 12:13:14     },
     *            { profileSetID:123，modifyTime: 2015-06-01 12:13:14      },
     *         ]
     * }                 
     */
    public void get_profile_set_list(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	List<ProfileSet> profileSetList=null;
		JSONObject json= new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);	
			json.put("receiver",sender);  
			JSONArray ja=new JSONArray();
			for (int i = 1; i <= 5; i++) {
				List<Profile> profileList=profileMap.getProfileSetByTemplateID(ctrolID, i);  //获取情景集
				if(profileList!=null){
					Date modifyDate=new Date(0);
					for (Iterator iterator = profileList.iterator(); iterator
							.hasNext();) {
						Profile profile = (Profile) iterator.next();
						if (profile.getModifyTime().after(modifyDate)) {  //取较晚的时间
							modifyDate=profile.getModifyTime();
						}						
					}					
	    			JSONObject jo=new JSONObject();
	    			jo.put("profileSetID", i);
	    			jo.put("modifyTime", modifyDate.getTime()/1000);
					ja.put(jo);
				}				
			}
	    	/*if( (profileSetList= profileSetMap.getProfileSetsByctrolID(ctrolID)).size()!=0  ){
	    		JSONArray ja=new JSONArray();
	    		for (ProfileSet profile : profileSetList) {
	    			JSONObject jo=new JSONObject();
	    			jo.put("profileSetID", profile.getProfileSetID());
	    			jo.put("modifyTime", sdf.format(profile.getModifyTime()));
					ja.put(jo);
				}
	    		json.put("profileSetArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_profileSet by ctrolID:"+ctrolID+" from profileMap or Mysql.");
				json.put("errorCode",PROFILE_SET_NOT_EXIST);
	    	}*/
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(GET_RROFILE_SET_LIST_ACK);
    	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_RROFILE_TEMPLATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
		TimeOutTread to=new TimeOutTread(3,msg);
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
		msg.setJson(json);
  		msg.setCommandID(SET_RROFILE_TEMPLATE_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
		TimeOutTread to=new TimeOutTread(3,msg);
		to.start();
    }
    
    /*** 请求查询所有的情景模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
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

     *   profileTemplateArray: 
     *        [
     *          {情景模式模板1的json格式 },
     *          {情景模式模板2的json格式 }
     *        ]
     * }                    
     */
    public void get_all_profile_template(Message msg) {
    	JSONObject json=new JSONObject();
    	Map<Integer,ProfileTemplate> profileTemplatList=null;

		try {
	    	//int ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(  (profileTemplatList=ProfileTemplate.getAllFromDB(mysql))!=null){
    			JSONArray ja=new JSONArray();
	    		for (Integer key:profileTemplatList.keySet()) {
	    			ProfileTemplate pt=profileTemplatList.get(key);
	    			if(pt!=null){
	    				ja.put(pt.toJsonObj());
	    			}
				}
	    		json.put("profileTemplateArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
				json.put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		} catch (SQLException e) {			
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",SQL_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_ALL_RROFILE_TEMPLATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    /*** 请求查询所有的情景模板
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
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

     *   profileTemplateArray: 
     *        [
     *          {情景模式模板1的json格式 },
     *          {情景模式模板2的json格式 }
     *        ]
     * }                    
     */
    public void get_profile_template_list(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	//Map<Integer,ProfileTemplate> profileTemplatList=null;

		try {
	    	//int ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(  this.profileTemplateList!=null){  //(profileTemplatList=ProfileTemplate.getAllFromDB(mysql))!=null
    			JSONArray ja=new JSONArray();
	    		for (Integer key:this.profileTemplateList.keySet()) {
	    			ProfileTemplate pt=profileTemplateList.get(key);
	    			if(pt!=null){
	    				JSONObject jo=new JSONObject();
	    				jo.put("profileTemplateID",pt.getProfileTemplateID());
	    				jo.put("name",pt.getProfileTemplateName());
	    				jo.put("modifyTime",pt.getModifyTime().getTime()/1000);//sdf.format(pt.getModifyTime())
	    				//ja.put(pt.toJsonObj());
	    				ja.put(jo);
	    			}
				}
	    		json.put("profileTemplateArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
				json.put("errorCode",PROFILE_TEMPLATE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_RROFILE_TEMPLATE_LIST_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    /*** 请求查询当前正在生效的情景模式,返回每个房间所处的情景模式，如果每个房间的模式ID都相同，说明是全家模式，若不相同则为混合模式-1；例如三个房间，模板ID都是2，代表全家观影模式
     * <pre>传入的json格式为：
     * { 
     *   sender:    0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:  0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式存在，则返回:
     *  { 
     *   ctrolID:123456
     *   sender:    2
     *   receiver:  1
     *   activeProfile:
     *   [
     *    {roomID:101,profileID:11,profileTemplateID:2},
		  {roomID:201,profileID:12,profileTemplateID:2}
     * }                    
     */
    public void get_active_profile(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
		try {
	    	int ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
			Map<String, String> activeProfileMap = jedis.hgetAll(LogicControl.currentProfile+ctrolID);
	    	if(  activeProfileMap.size()>0){
    			JSONArray ja=new JSONArray();
	    		for (Map.Entry<String, String> entry:activeProfileMap.entrySet()) {
	    			Profile p=new Profile(new JSONObject(entry.getValue())); 
	    			if(p!=null){
	    				JSONObject jo=new JSONObject();
	    				jo.put("roomID",p.getRoomID());
	    				jo.put("profileID",p.getProfileID());
	    				jo.put("profileTemplateID",p.getProfileTemplateID());
	    				ja.put(jo);
	    			}
				}
	    		if(ja.length()>0){
		    		json.put("activeProfile", ja);
		    		json.put("errorCode",SUCCESS);
	    		}
	    	}else {
				json.put("errorCode",ACTIVE_PROFILE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		} catch (ParseException e) {
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace(); logException(e);
		}
    	msg.setCommandID( GET_ACTIVE_RROFILE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	if( (device=deviceMap.get(key))!=null /*|| (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null*/){
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID( GET_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
     *   changeFlag: 0 损坏状态没有改变； 1：损坏状态有改变
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
	public void set_one_device(Message msg,MySqlClass mysql) {
		JSONObject json= new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
    	Device msgDevice;
		try {
			msgDevice = new Device(msg.getJson().getJSONObject("device"));
			int changeFlag=msg.getJson().optInt("changeFlag");
	    	Device dbDevice;
	    	int ctrolID=msgDevice.getCtrolID();
	    	int deviceID=msgDevice.getDeviceID();
	    	int roomID=msgDevice.getRoomID();
	    	Date msgModifyTime=msgDevice.getModifyTime();
	    	String key=ctrolID+"_"+deviceID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender" ); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 
			dbDevice=this.deviceMap.get(key);
	    	if((dbDevice)==null  || (dbDevice!=null && dbDevice.modifyTime.before(msgModifyTime))){	 //不存在或者云端较旧
	    		Device d=this.deviceMap.put(key, msgDevice);
				if(d!=null){
					json.put("errorCode",SUCCESS); 
				}else{
					json.put("errorCode",SQL_ERROR); 
				}	
				String key2=LogicControl.roomBind+ctrolID;
	    		jedis.hset(key2, deviceID+"", msgDevice.toJsonObj().toString());

	    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ //云端较新  
	    		log.error("device in Cloud is newer than from user, ctrolID:"+ctrolID+" deviceID:"+deviceID+",cookie="+msg.getCookie());
				json.put("errorCode",DEVICE_OBSOLETE);   
			}
	    	
	    	if(changeFlag==1){
	    		JSONObject json2= new JSONObject();
	    		int warnType=0;
	    		if(msgDevice.getState()==1){        //由坏变好了
	    			warnType=WarnID.SENSOR_ONLINE;//3017;
	    		}else if(msgDevice.getState()==0){  //由好变坏
	    			warnType=WarnID.SENSOR_OFFLINE;//3016;
	    		}	  
	    		Room room=roomMap.get(ctrolID+"_"+roomID);
	    		if(room!=null){
		    		//String msgContent=room.getRoomName()+"的"+msgDevice.getDeviceName();
	    			JSONObject msgContent=new JSONObject();
	    			msgContent.put("roomName", room.getRoomName());
		    		Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2, warnType, 0, sender,msgContent.toString());
					json2.put("originalSenderRole", sender);
					json2.put("sender", 2);			
									
					json2.put("ctrolID", ctrolID);
					json2.put("warn", warn.toJsonObject());
					//msg.setJson(json2);
					//msg.setCommandID(WARNING_MSG);
					Message msg2=new Message(WARNING_MSG, cookieNo++ +"_2", json2);
					try {
						CtrolSocketServer.sendCommandQueue.offer(msg2, 100, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace(); logException(e);
					}					
	    		}	    		
	    	}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (ParseException e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e.printStackTrace(); logException(e);
			}
		}
  		msg.setCommandID(SET_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    		jedis.hdel(LogicControl.roomBind+ctrolID,device.getDeviceID()+"");
	    		jedis.hdel(LogicControl.currentDeviceState+ctrolID,device.getDeviceID()+"");
	    	}else {
				log.error("room_device not exist ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( DELETE_ONE_DEVICE_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json=new JSONObject();
		Message replyMsg=new Message(msg);
    	Device device=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");			
	    	int deviceID=msg.getJson().getInt("deviceID");
	    	int deviceType=msg.getJson().getInt("deviceType");
	    	int roomID=msg.getJson().optInt("roomID");
	    	DeviceState newState= new DeviceState();
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}

	    	json.put("sender",2);
	    	json.put("receiver",sender);
	    	String key=ctrolID+"_"+deviceID;
	    	if((device= deviceMap.get(key))!=null /*|| (device=Device.getOneDeviceFromDB(mysql, ctrolID, deviceID))!=null*/){
				json.put("switchTime",sdf.format(new Date()));
				String key2=currentDeviceState+ctrolID;
        		JSONObject stateJson=new JSONObject();
        		stateJson.put("time", sdf.format(new Date()));
        		stateJson.put("sender", sender);
				stateJson.put("devType", device.getDeviceType());
				

    			String oldStateStr=jedis.hget(key2, deviceID+"");   //之前的 设备状态    			
        		
        		int keyType=-1;
        		switch (deviceType) {
				case 541: //空调
		        	if(msg.getJson().has("state")){    //air conditional空调
		        		newState=new DeviceState(msg.getJson().getJSONObject("state"));
		        		DeviceState oldState=null;
		    			if (oldStateStr!=null && oldStateStr.contains("stable") ) {
		    				JSONObject deviceS=new JSONObject(oldStateStr);		        		
	        				oldState=new DeviceState(deviceS.getJSONObject("state"));
	        				int oldstable =oldState.getStable();
	        				oldState.replaceAdd(newState);
	        				if(sender!=0 && sender!=1 && sender!=8 ){  //不是中控（来自分析服务器）	stable保持旧的值	        					
	        					oldState.setStable(oldstable);	
	        				}
						}else{
							oldState=newState;
						}
	        			stateJson.put("state", oldState.toJson());  		
		        		jedis.hset(key2, deviceID+"", stateJson.toString());
		        	}
					break;
				case 151: //图灵猫门锁	
				case 311: //图灵猫门锁	
					keyType=msg.getJson().getInt("keyType");
	        		stateJson.put("keyType", keyType);
	        		jedis.hset(key2, deviceID+"",stateJson.toString()); 
	        		
        			if(keyType==501){      //门锁打开短信通知
        				JSONObject json2=new JSONObject();
        				json2.put("sender", 6);	
        				json2.put("sender", 1);
        				json2.put("ctrolID", ctrolID);
    	    			JSONObject msgContent=new JSONObject();
    	    			Room room=roomMap.get(ctrolID+"_"+roomID);
    	    			if (room!=null) {
    	    				msgContent.put("roomName", room.getRoomName());
						}else{
							String roomName=RoomTypeDic.getRoomTypeName(RoomTypeDic.getRoomType(roomID));	
							msgContent.put("roomName", roomName);
						}    	    			
        				Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2, 1151, 0, sender,msgContent.toString());  
        				json2.put("warn", warn.toJsonObject());
        				Message msg3=new Message(msg);
    					msg3.setJson(json2);
    					msg3.setCommandID(WARNING_MSG);
            			CtrolSocketServer.sendCommandQueue.offer(msg3, 100, TimeUnit.MILLISECONDS);	       			
      			
        			}
					break;
				case 131: //智能插座
					keyType=msg.getJson().getInt("keyType");
	        		stateJson.put("keyType", keyType);
	        		jedis.hset(key2, deviceID+"",stateJson.toString()); 
	        		if( keyType==502){         //智能插座关闭，则关闭相关家电
	        			Device plug=deviceMap.get(ctrolID+"_"+deviceID);
	        			if(plug!=null){
	        				int relatedDevID=plug.getRelatedDevType();  //智能插座关联的设备
	        				String devStateStr=jedis.hget(key2, relatedDevID+"");
	        				if(devStateStr!=null){
	        					JSONObject jsonState=new JSONObject(devStateStr);
	        					if(jsonState.has("state")){  //空调
	        						DeviceState airState=new DeviceState(jsonState);
	        						airState.setOnOff(1);    // 1代表空调关闭
	        						jsonState.put("state", airState.toJson());	        						
	        					}else{                      //其他家电 关闭
	        						jsonState.put("keyType", 502);
	        					}
	        					jsonState.put("time", sdf.format(new Date()));
	        					jsonState.put("sender", sender);
	        					jsonState.put("devType", jsonState.optInt("devType"));
	        					jedis.hset(key2, relatedDevID+"",jsonState.toString());       //改变jedis状态
	        				}	        				
	        			}
	        		}
					break;
				default:
					keyType=msg.getJson().getInt("keyType");
	        		stateJson.put("keyType", keyType);
	        		jedis.hset(key2, deviceID+"",stateJson.toString()); 
					break;					
				}
        		
        		if(sender!=0){
	    			to.put(msg.getCookie(),new Message(msg));  //放入超时队列
	    			
        			Message msg2=new Message(msg);
        			msg2.setServerID(1); //转给主服务器
        			msg2.getJson().put("receiver",0); 
        			msg2.getJson().put("sender",2);       
        			msg2.setJson(msg2.getJson());
        			CtrolSocketServer.sendCommandQueue.offer(msg2, 100, TimeUnit.MILLISECONDS);
        			return;	
        		}else{
        			json.put("errorCode",SUCCESS);
        		}
	
	    	}else {
				log.error("Can't switch device,device doesn't exit. ctrolID:"+ctrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}
			if (deviceType==541) {
				replyMsg.setCommandID(SWITCH_DEVICE_STATE_ACK);
			}else{
				replyMsg.setCommandID(SWITCH_SIMPLE_DEVICE_STATE_ACK);
			}	    	
			replyMsg.setJson(json);
			//if(sender!=0){
				try {
					CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace(); logException(e);
				}
			//}
		} catch (JSONException e1) {
			e1.printStackTrace(); logException(e1);
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		} catch (ParseException e) {
			e.printStackTrace();logException(e);
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace(); logException(e1);
			}
		}

	}
	
    /*** 请求切换家电,返回值
     * <pre>传入的json格式为：
    * { 
     *   sender:    中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:4; ...
     *   receiver:  中控:0;  手机:1;  设备控制服务器:2;  web:3;  主服务:4;  消息服务:5; ...
         errorCode: SUCCESS/ NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_device_state_ack(final Message msg){
//		TimeOutTread to=new TimeOutTread(3,msg);
//		to.start();
		to.put(msg.getCookie(),msg);
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_ALL_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}	
	}
	
	
	/*** 设置 一个用户家里所有家电,以上报的为准，如果云端存在，而上报中没有，则从云端删除
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
			json.put("sender",2);
			json.put("receiver",sender); 
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	JSONArray ja=msg.getJson().getJSONArray("deviceArray");	
	    	List<String> msgDeviceList=new ArrayList<>();
			for (int i=0;i<ja.length();i++) {
				Device msgDevice = new Device(ja.getJSONObject(i));  
		    	int deviceID=msgDevice.getDeviceID();
		    	Date msgModifyTime=msgDevice.getModifyTime();
		    	String key=ctrolID+"_"+deviceID;

		    	if((dbDevice=this.deviceMap.get(key))==null  || ( dbDevice!=null && dbDevice.modifyTime.before(msgModifyTime) ) ){	 //不存在或者云端较旧
		    		this.deviceMap.put(key, msgDevice);
		    		msgDeviceList.add(key);
					json.put("errorCode",SUCCESS);		
					String key2=LogicControl.roomBind+ctrolID;
		    		jedis.hset(key2, deviceID+"", msgDevice.toJsonObj().toString());
		    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ //云端较新  
		    		log.error("device in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" deviceID:"+deviceID+",discard.");
					json.put("errorCode",DEVICE_OBSOLETE);   
				}
			}
			
			/*删除云端存在但是上报不存在的*/
		      List<Device> dbDeviceList=deviceMap.getDevicesByctrolID(ctrolID);
		      for (Iterator iterator = dbDeviceList.iterator(); iterator.hasNext();) {
		    	  Device device = (Device) iterator.next();
				String key=device.getCtrolID()+"_"+device.getDeviceID();
				if(!msgDeviceList.contains(key)){  //不存在则删除
					deviceMap.remove(key);
					//jedis.hdel(LogicControl.roomBind+ctrolID,device.getDeviceID()+"");
					jedis.hdel(LogicControl.currentDeviceState+ctrolID,device.getDeviceID()+"");
				}			
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (ParseException e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}			
		}
  		msg.setCommandID(SET_ALL_DEVICE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	public void get_device_list(Message msg) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	List<Device> deviceList=new ArrayList<Device>();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 
	    	if( (deviceList=deviceMap.getDevicesByctrolID(ctrolID)).size()!=0 ){
	    		JSONArray ja=new JSONArray();
	    		for (Device device : deviceList) {
	    			JSONObject jo=new JSONObject();
	    			jo.put("deviceID", device.getDeviceID());
	    			jo.put("modifyTime", device.getModifyTime().getTime()/1000);//sdf.format(device.getModifyTime())
					ja.put(jo);
				}
	    		json.put("deviceArray", ja);
	    		json.put("errorCode",SUCCESS); 
	    	}else {
				log.error("DEVICE_NOT_EXIST, ctrolID:"+ctrolID+" from deviceMap or Mysql,sender:"+sender);
				json.put("errorCode",DEVICE_NOT_EXIST);
	    	}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_DEVICE_LIST_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}	
	}
	
	/*** 获取一个用户家里所有设备id
	 * 	 <pre>请求对应json消息体为：
	 *   {
	 *     sender:0
	 *     receiver:2
	 *     ctrolID:1234567
	 *     deviceArray:
	 *     [ 
	 *        {deviceID:101,modifyTime:"2015-07-07 12:13:14"},
	 *        {deviceID:102,modifyTime:"2015-07-07 12:13:14"},
	 *     ]
	 *     
     *   }
     *   @return List< Device > 加电列表 的json格式
     *   回复消息对应json消息体为：
         {
     *                 errorcode:0
     *                 
         }
     *   
	 * @throws JSONException 
     *   */
	public void set_device_list(Message msg) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	List<Device> deviceList=new ArrayList<Device>();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
			JSONArray ja =msg.getJson().getJSONArray("deviceArray");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 
	    	
			List<Integer> msgDeviceList=new ArrayList<Integer>();
			if(ja!=null){				
				for (int i = 0; i < ja.length(); i++) {
					Integer deviceID=ja.getJSONObject(i).getInt("deviceID");
					msgDeviceList.add(deviceID);
				}				
			}
	    	if( (deviceList=deviceMap.getDevicesByctrolID(ctrolID)).size()!=0 ){
	    		for (Device device : deviceList) {
	    			if(!msgDeviceList.contains(device.getDeviceID())){  //上报不存在，而数据存在，则删除
	    				deviceMap.remove(ctrolID+"_"+device.getDeviceID());
	    				//jedis.hdel(LogicControl.roomBind+ctrolID,device.getDeviceID()+"");  //redis也删除
	    				jedis.hdel(LogicControl.currentDeviceState+ctrolID,device.getDeviceID()+"");  //redis也删除
	    			}
				}
	    		json.put("errorCode",SUCCESS); 
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( SET_DEVICE_LIST_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}	
	}
	
	/** 获取家电字典
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     sender:0
	 *     receiver:2
     *   }
     *   @return language 的jsonArray格式
     *   {
     *    errorCode:0,   
     *	  deviceDictionary: [ deviceDictionary Json列表 ]
     *   }
     *  
	 * @throws JSONException 
     */
	public void get_device_dictionary(Message msg){
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
			//List<FactorDict> dictionary=FactorDict.getDeviceDictList(mysql);
	    	if(factorDicList.size()!=0 ){
	    		JSONArray array=new JSONArray();
	    		for (FactorDict factorDict : factorDicList) {
					array.put(factorDict.toJson());
				}
				json.put("deviceDictArray", array);  		
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't language, ctrolID:"+ctrolID);
				json.put("errorCode",DEVICE_NOT_EXIST)   ;
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_DEVICE_DICTIONARY_ACK); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}		
	}
	
	/** 获取一个房间
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
     */
	public void get_one_room(Message msg) {
    	JSONObject json=new JSONObject();
    	Room room=null;
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");

	    	int roomID=msg.getJson().getInt("roomID");
	    	String key=ctrolID+"_"+roomID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
	    	if((room=roomMap.get(key))!=null /*||  (room=Room.getRoomHeadFromDB(mysql, ctrolID, roomID))!=null*/){
	    		json.put("room", room.toJsonObject());
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_one_room, ctrolID:"+ctrolID+"roomID: "+ roomID+" from roomMap or Mysql.");
				json.put("errorCode",ROOM_NOT_EXIST);
	    	}
	    	msg.setCommandID( GET_ONE_ROOM_ACK);
 
			msg.setJson(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_ONE_ROOM_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	int roomType=msgRoom.getRoomType();
	    	Date msgModifyTime=msgRoom.getModifyTime();
	    	String key=ctrolID+"_"+roomID;
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
	    	if((dbRoom=this.roomMap.get(key))==null  || (dbRoom!=null && dbRoom.getModifyTime().before(msgModifyTime) )){	 //不存在或者云端较旧
	    		Room r=this.roomMap.put(key, msgRoom);
	    		String jedisKey=LogicControl.roomList+ctrolID;
	    		jedis.hset(jedisKey, roomID+"", msgRoom.toJsonObject().toString());
	    		//自动给新创建的房间设置当前 情景模式为手动模式
	    		Map<Integer, Set<Integer>> currentProfileMap=Profile.getCurrentProfileTemplateID(jedis, ctrolID);
	    		if(currentProfileMap!=null && currentProfileMap.size()==1){
	    			Iterator<Integer> it = currentProfileMap.keySet().iterator();
	    			int profileTemplateID=it.next();
	    			Profile p= Profile.getCustomerProfile(ctrolID, profileTemplateID, roomID, roomType);
		        	 JSONObject pjSON=p.toJsonObj();
		        	 pjSON.put("time", sdf.format(new Date()));
	    			 jedis.hset(currentProfile+ctrolID, p.getRoomID()+"", pjSON.toString());
	    			 jedis.hset(LogicControl.currentProfile+ctrolID, roomID+"", pjSON.toString());
	    		}else{
	    			Profile p= Profile.getCustomerProfile(ctrolID, 5, roomID, roomType);
		        	 JSONObject pjSON=p.toJsonObj();
		        	 pjSON.put("time", sdf.format(new Date()));
	    			 jedis.hset(currentProfile+ctrolID, p.getRoomID()+"", pjSON.toString());
	    			jedis.hset(LogicControl.currentProfile+ctrolID, roomID+"", pjSON.toString());
	    		}
	    		
	    		if(r!=null){
	    			json.put("errorCode",SUCCESS);
	    		}else{
	    			json.put("errorCode",SQL_ERROR);
	    			log.error("save Room to roomMap failed,room:"+dbRoom.toJsonObject().toString());
	    		}	
	    	}else if(dbRoom.getModifyTime().after(msgModifyTime)){ //云端较新  
	    		log.error("device in Cloud is newer than from profile from user, ctrolID:"+ctrolID+" roomID:"+roomID+",discard.");
				json.put("errorCode",ROOM_OBSOLETE);   
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
  		msg.setCommandID(SET_ONE_ROOM_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	Room room=null;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender); 
			if((room=roomMap.get(key))!=null || (room=Room.getRoomHeadFromDB(mysql, ctrolID, roomID))!=null){
	    		roomMap.remove(key);                                //删数据库
	    		jedis.hdel(LogicControl.roomList+ctrolID,roomID+"");//删Redis  
	    		
	    		jedis.hdel(currentProfile+ctrolID, roomID+"");
	    		
	    		json.put("errorCode", SUCCESS);    		
	    	}else {
				log.error("room_room not exist ctrolID:"+ctrolID+" roomID:"+roomID+" from roomMap or Mysql.");
				json.put("errorCode",ROOM_NOT_EXIST);
	    	}
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( DELETE_ONE_ROOM_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
				json.put("errorCode",ROOM_NOT_EXIST);
	    	}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_ALL_ROOM_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	List<String> msgRoomList=new ArrayList<>();
			for (int i=0;i<ja.length();i++) {
		    	Room msgRoom=new Room(ja.getJSONObject(i));
		    	int roomID=msgRoom.getRoomID();
		    	Date msgModifyTime=msgRoom.getModifyTime();
		    	String key=ctrolID+"_"+roomID;
		    	msgRoomList.add(key);
		    	if((dbRoom=this.roomMap.get(key))==null  || dbRoom.getModifyTime().before(msgModifyTime) ){	 //不存在或者云端较旧
		    		Room r=this.roomMap.put(key, msgRoom);
		    		jedis.hset(LogicControl.roomList+ctrolID,roomID+"",msgRoom.toJsonObject().toString());
		    		if(r!=null){
				       json.put("errorCode",SUCCESS); 
		    		}else{
		    			json.put("errorCode",SQL_ERROR);
		    		}

		    	}else if(dbRoom.getModifyTime().after(msgModifyTime)){ //云端较新  
		    		log.error("Room in Cloud is newer than from user, ctrolID:"+ctrolID+" roomID:"+roomID+",cookieID="+msg.getCookie()+"discard.");
					json.put("errorCode",ROOM_OBSOLETE);   
				}
			}
			
			/*删除云端存在但是上报不存在的*/
		      List<Room> dbRoomList=roomMap.getRoomsByctrolID(ctrolID);
		      for (Iterator iterator = dbRoomList.iterator(); iterator.hasNext();) {
		    	  Room room = (Room) iterator.next();
				String key=room.getCtrolID()+"_"+room.getRoomID();
				if(!msgRoomList.contains(key)){  //不存在则删除
					roomMap.remove(key);
					jedis.hdel(LogicControl.roomList+ctrolID,room.getRoomID()+"");
					//jedis.hdel(currentProfile+ctrolID, room.getRoomID()+"");
				}			
			}


		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
		msg.setJson(json);
  		msg.setCommandID(SET_ALL_ROOM_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		} 		
	}
	
	/*** 获取一个用户家里所有房间
	 * 	 <pre>对应json消息体为：
	 *   { 
	 *     sender:
	 *     receiver:
	 *     ctrolID:1234567
     *   }
     *   @return List< Device > 加电列表 的jsonArray格式
     *   {
     *   ctrolID:1234567,     *   
     *	 roomArray: [
     *               {roomID:1, modifyTime:2015-06-01 12:13:14 }
     *              ]
     *   }
	 * @throws JSONException 
     **/
	public void get_room_list(Message msg) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	    			JSONObject jo=new JSONObject();
	    			jo.put("roomID", room.getRoomID());
	    			jo.put("modifyTime", room.getModifyTime().getTime()/1000);//sdf.format(room.getModifyTime())
					ja.put(jo);
				}
	    		json.put("roomArray", ja);
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't get_all_room by ctrolID:"+ctrolID+""+" from roomMap or Mysql,sender:"+sender);
				json.put("errorCode",ROOM_NOT_EXIST);
	    	}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_ROOM_LIST_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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

		JSONObject json=msg.getJson();		
			try {
				if(msg.getJson().has("sender")){
					int senderRole=msg.getJson().getInt("sender");
					json.put("originalSenderRole", senderRole);
					json.put("sender", 6);
					msg.setJson(json);
				}

			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
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
				e.printStackTrace(); logException(e);
			}
		} 
	
	public void warning_msg_ack(final Message msg){
		//System.out.println("successfull sending warning message");
		
	} 	
	
	  /*** 取消告警消息、上报告警消息 等中控产生的消息
	  <pre> 请求json消息体如下格式 ：
	  {
		ctrolID:10003
		businessType:  1:有害气体告警；     2火警；3漏水；4 闯入告警      ；
		               501：取消有害气体；2取消火警；3取消漏水；4取消创入告警；
	  }
	  */
	public void report_control_msg(final Message msg){
		JSONObject json=msg.getJson();
		Message replyMsg=new Message(msg);
		JSONObject replyjson=new JSONObject();	
		replyMsg.setCommandID(REPORT_CONTROL_MSG_ACK);
		try {
			replyjson.put("errorCode", 0);
			replyMsg.setJson(replyjson);
			try {
				CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace(); logException(e);
			}
			
			int senderRole=0;
			if(msg.getJson().has("sender")){
				senderRole=msg.getJson().getInt("sender");
				json.put("originalSenderRole", senderRole);
			}
			json.put("sender", 6);				
			int ctrolID=msg.getJson().getInt("ctrolID");
			int warnType=msg.getJson().getInt("businessType");
			Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2, warnType, 0, senderRole, new JSONObject().put("roomName", "全家").toString());
			
			json.put("ctrolID", ctrolID);
			json.put("warn", warn.toJsonObject());
		} catch (JSONException e) {
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		msg.setJson(json);
		msg.setCommandID(WARNING_MSG);
		try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
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
				json.put("errorCode",TRIGGER_TEMPLATE_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender);  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_TEMPLATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
  		msg.setCommandID(SET_ROOM_PROFILE_ACK);
		msg.setJson(json);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
	    	if(  (trigger=Trigger.getFromDB(mysql, ctrolID, triggerID))!=null){
	    		json.put("trigger", trigger.toJson());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
				json.put("errorCode",TRIGGER_NOT_EXIST);
	    	}

			json.put("sender",2);
	
			json.put("receiver",sender);  

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
				json.put("errorCode",TRIGGER_OBSOLETE);    		
	    	}else if(  dbTrigger.getModifyTime().before(msgModifyTime)){ //云端较旧，则保存
	    		this.triggerMap.put(key, msgTrigger);
				json.put("errorCode",SUCCESS);   
				}  
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
			
		}
		msg.setJson(json);
  		msg.setCommandID(SET_TRIGGER_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
				json.put("errorCode",TRIGGER_NOT_EXIST);
	    	}

			json.put("sender",2);
			json.put("receiver",sender); 
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
		msg.setJson(json);
    	msg.setCommandID(DELETE_TRIGGER_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    /*** 请求触发规则列表
     * <pre>传入的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":NOT_EXIST}           
     */
    public void get_trigger_templat_list(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json= new JSONObject();
    	//int ctrolID=-1;
		try {
			//ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);	
			json.put("receiver",sender);
			int count=msg.getJson().optInt("count",-1);
			int offset=msg.getJson().getInt("offset");
			int i=0;
	    	if( triggerTemplateMap.size()!=0  ){
	    		JSONArray ja=new JSONArray();
	    		if ( count>0) {
		    		for (Entry<Integer, TriggerTemplate> entry:triggerTemplateMap.entrySet()) {
		    			if (i>=offset && i<offset+count){
		    				ja.put( entry.getValue().toJsonHeader());
		    			}else if (i>=offset+count){
		    				break;
		    			}
		    			i++;
		    		}	
		    		json.put("offset", i);
		    		if(i>=triggerTemplateMap.size()){
		    			json.put("end", 1);
		    		}else{
		    			json.put("end", 0);
		    		}
				}else {
		    		for (Entry<Integer, TriggerTemplate> entry:triggerTemplateMap.entrySet()) {
		    			ja.put( entry.getValue().toJsonHeader());
		    		}
				}
	    		json.put("triggerTemplateArray", ja);
	    		json.put("errorCode",SUCCESS);
	    	}else {
	    		json.put("end", 1);
	    		json.put("offset", offset);
				log.error("can't get trigger template from mysql,please check cfg_trigger_template_header.");
				json.put("errorCode",TRIGGER_TEMPLATE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_TEMPLATE_LIST_ACK);
    	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    /*** 请求触发规则列表的头部
     * <pre>传入的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":NOT_EXIST}           
     */
    public void get_trigger_template_header(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json= new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
            int triggerID=msg.getJson().getInt("triggerTemplateID");
	    	int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);	
			json.put("receiver",sender); 
			TriggerTemplate trigger=null;
			if( (trigger= triggerTemplateMap.get(triggerID))!=null  ){
	    		json.put("trigger", trigger.toJsonHeader());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("can't get trigger template from mysql,please check cfg_trigger_template_header.");
				json.put("errorCode",TRIGGER_TEMPLATE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_TEMPLATE_LIST_ACK);
    	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}    	
    }
    
    /*** 请求触发规则的头部/索引
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
    public void get_trigger_header(Message msg) {
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
			json.put("sender",2);			
			json.put("receiver",sender); 
	    	String key=ctrolID+"_"+triggerID;
	    	if(  (trigger=Trigger.getHeaderFromDB(mysql, ctrolID, triggerID))!=null){
	    		json.put("trigger", trigger.toJsonHeader());
	    		json.put("errorCode",SUCCESS);
	    	}else {
				log.error("Can't get_room_trigger ctrolID:"+ctrolID+" triggerID:"+triggerID+" from triggerMap or Mysql.");
				json.put("errorCode",TRIGGER_TEMPLATE_NOT_EXIST);
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
		}
    	msg.setCommandID(GET_TRIGGER_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
    public void set_trigger_header( Message msg){
    	JSONObject json=new JSONObject();
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Trigger msgTrigger;
		try {
			msgTrigger =  Trigger.initTriggerHeader(msg.getJson().getJSONObject("trigger"));

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
	    	//if((dbTrigger=Trigger.getHeaderFromDB(mysql, ctrolID, triggerID))==null  || (dbTrigger!=null && dbTrigger.getModifyTime().before(msgModifyTime)) ){
    		jedis.hset(LogicControl.triggerState+ctrolID, triggerID+"", msgTrigger.toJsonHeader().toString());
			int t=msgTrigger.saveHeaderToDB(mysql);
			if(t>0){
				json.put("errorCode",SUCCESS); 
			}else{
				json.put("errorCode",SQL_ERROR); 
			}   		

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e1.printStackTrace();
			}
			
		}
		msg.setJson(json);
  		msg.setCommandID(SET_TRIGGER_HEADER_ACK);
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
					Device device=null;
					try {
						device = new Device(jo);
						
					} catch (ParseException e) {
						
						e.printStackTrace(); logException(e);
						break;
					}
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
			e.printStackTrace(); logException(e);
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e.getCause().getMessage());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		} catch (ParseException e) {
			try {
				json.put("errorCode",TIME_PARSE_ERROR);
				json.put("errorDescription",e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace(); logException(e);
		}
   	msg.setCommandID(SYN_UPDATETIME_ACK);
	msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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

			if(applianceType==541 ||applianceType==501||applianceType==511||applianceType==521||applianceType==601 ||applianceType==591 ||applianceType==521){			
			}else{
				json.put("errorCode", UNKNOWN_DEVICE_TYPE);
			}
			String fileID=msg.getJson().optString("fileID");
			IRFileDownload irDownload=new IRFileDownload(applianceType,fileID);
			String url=irDownload.getEncycroURL();
	

			if(url.equals("") || url==null){
				json.put("errorCode", INFRARED_FILE_NOT_EXIST);
			}else{
				json.put("url", url);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
		msg.setJson(json);
    	msg.setCommandID(DOWNLOAD_INFRARED_FILE_ACK);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
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
    public  void recognize_infrared_code(Message msg) {
 
		JSONObject json= new JSONObject();

    	int sender=0;
		try {
			int ctrolID=msg.getJson().getInt("ctrolID");
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender); 
			String ircode=msg.getJson().optString("ircode");
			int applianceType=-1;
			applianceType = msg.getJson().getInt("applianceType");
		   	im.init(ircode, applianceType);  //初始化
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
			case 522: //DVD
				applianceTypeStr="DVD";
				break;
			case 601: //电风扇
				applianceTypeStr="FAN";
				break;
			case 591: //空气净化器
				applianceTypeStr="ACL";
				break;
			case 521: //视频盒子
				applianceTypeStr="IPTV";
				break;	
			default:
				applianceTypeStr="";
				break;
			}
			String filePath2=null;
			String ir_file_path=this.config.getValue("ir_file_path");
			String ir_file_ip=this.config.getValue("ir_file_ip");
			if(applianceTypeStr!=""){
				filePath2=ir_file_path+"/"+applianceTypeStr+"/codes";
			}else{
				filePath2=ir_file_path;
			}
			System.out.println("input Infrared filePATH="+filePath2);
			String C3code=im.getC3(ircode);
			if(C3code.equals("") || C3code==null){
				json.put("errorCode", INFRARED_CODE_NOT_RECOGNIZED);
			}
			im.match(/*new File(filePath2),*/ C3code);
			String res=im.getTop1();
			String fileName=null;
			String score=null;
			if(res==null){                   //没有匹配到，则匹配C3code的子串
	    		if(C3code.charAt(C3code.length()-1)!=','){
	    			C3code=C3code+",";
	    		}
				//String tempSubStr=new StringUtility().getLongestSubStr(C3code);  //寻找最长重复子串
	    		String tempSubStr=C3code;  
	    		String subStr=null;
	    		while(tempSubStr!=null){
	    			 subStr=tempSubStr;
	    			 tempSubStr=new StringUtility().getLongestSubStr(tempSubStr);//寻找最长重复子串  			 
	    		}
				if(subStr!=null){
					int count=StringUtility.getSubCount_2(C3code,subStr);  // 重复子串 重复的次数
					for (int j = count-1; j >=1; j--) {
						String C2code2=StringUtility.getNthDuplicateStr(subStr,j);      //子串重复j次
						im.match(/*new File(filePath2),*/ C2code2);
						res=im.getTop1();
						if(res!=null)           //找到了 退出
							break;
					}
				}	
				//将C3code重复2-3次  2015-10-22
				if (new StringUtility().getLongestSubStr(C3code)==null) {  //没有重复字串
					String multiC3code=C3code;
					for (int i = 2; i < 4; i++) {
						multiC3code=multiC3code+C3code;
						im.match( multiC3code);
						res=im.getTop1();
						if(res!=null)           //找到了 退出
							break;					
					}
				}
			}
			if (res!=null) {
				String [] result=res.split("\\|");//[0];
				if(result!=null && result.length==2 ){
					fileName=result[0];
					score=result[1];					
					im.clear();
					System.out.println("C3code="+C3code+"  the most high score is "+fileName+",score="+score);
				}
			}else{                           //还是没有匹配到
				json.put("errorCode", INFRARED_CODE_NOT_RECOGNIZED);
				IRMatch2.saveUnknownCode(mysql, ctrolID, applianceType, applianceTypeStr, ircode);
				log.error("InfraRed code recognize failed,deviceType="+applianceTypeStr+",code="+ircode );
			}

			if(fileName==null){
				json.put("errorCode", INFRARED_CODE_NOT_RECOGNIZED);
			}else{				
				int pos=fileName.lastIndexOf('\\');
				if(pos<0){
					pos=fileName.lastIndexOf('/');
				}
				String fid=fileName.substring(pos+1,fileName.length()-4);
				
				int pos2=fileName.indexOf('\\');
				if(pos2<0){
					pos2=fileName.indexOf('/');
				}				
				String deviceTypeStr=fileName.substring(0,pos2);
				int deviceType=-1;
				switch (deviceTypeStr) {
				case "AC": //空调
					deviceType=541;
					break;
				case "TV": //电视
					deviceType=501;
					break;
				case "STB": //机顶盒
					deviceType=511;
					break;
				case "DVD": //DVD
					deviceType=621;
					break;
				case "FAN": //电风扇
					deviceType=601;
					break;
				case "ACL": //空气净化器
					deviceType=591;
					break;	
				case "IPTV": // 电视盒子
					deviceType=521;
					break;	
				default:
					deviceType=-1;
					break;
				}
				
				json.put("fid", fid);
				json.put("deviceType", deviceType);
				json.put("deviceTypeName", deviceTypeStr);
				json.put("bitDifference", Integer.parseInt(score));
				String url="keyfiles6/"+deviceTypeStr+"/codes/"+fid;
				if(url.equals("") || url==null){
					json.put("errorCode", INFRARED_FILE_NOT_EXIST);
				}else{
					TuringCatAesCrypto crypto = new TuringCatAesCrypto();
					crypto.setToken("token_key");
					int pos3=ir_file_path.lastIndexOf('\\');
					if(pos3<0){
						pos3=ir_file_path.lastIndexOf('/');
					}
					String fid3=ir_file_path.substring(pos3+1,ir_file_path.length());
					String encrypted = crypto.encrypt(fid3+"/"+fileName);
					url=ir_file_ip+encrypted;
					json.put("url", url);
					json.put("errorCode", SUCCESS);
				}			
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
		}
    	msg.setCommandID(RECOGNIZE_INFRARED_CODE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
    	
	}
    
    /** 上报遥控器的操作
     * <pre> 
     *  请求的json：
     *  { ctrolID:12564
     *    roomType: 2
     *    roomID:201
     *    deviceID:268654
     *    deviceType: 541
     *    key: KEY_ON = 501;           KEY_OFF = 502;  
     *   }  
     * */
    public  void upload_remote_control_operation(Message msg) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json=new JSONObject();
		Message replyMsg =new Message(msg); 
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");			
	    	int roomType=msg.getJson().getInt("roomType");
	    	int roomID=msg.getJson().getInt("roomID");
	    	int deviceID=msg.getJson().getInt("deviceID");
	    	int deviceType=msg.getJson().getInt("deviceType");
	    	int key=msg.getJson().getInt("key");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
	    	json.put("sender",2);
	    	json.put("receiver",sender); 
	    	Device device=deviceMap.get(ctrolID+"_"+deviceID);
	    	if(device!=null){
	    		if(key==501){
	    			key=1;
	    		}else if(key==502){
	    			key=0;
	    		}
		    	String operation=deviceType+","+sdf.format(new Date())+","+ctrolID+","+ deviceID+","+roomType+","+roomID+","+device.getWall()+","+key;
	    		//jedis.publish("profileOperation", operation);
	    	}
    		json.put("errorCode",SUCCESS); 
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	replyMsg.setCommandID(UPLOAD_REMOTE_CONTROL_OPERATION_ACK);
		replyMsg.setJson(json);
		try {
			CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
    	
    }
    
    /*** 请求用户家里一个房间的 环境数据，如温度
     * <pre> ；        
     * 请求的json格式为：
     * { 
     *   sender:     0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   receiver:   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务
     *   ctrolID:1234567
     *   roomID: 
     *  }
     * @return message 的json格式：
     *   （1）若 这个房间的数据环境存在，则返回  这个房间的环境数据errorcode=0；
     *   （2）若 这个房间的环境数据不存在，则返回 样板间的环境数据。
     *   {
     *   sender:     2
     *   receiver:   0
     *   ctrolID:1234567
     *   enviromentState:
     *     {
     *      {temprature:25,level:5},
     *      {lux:300,level:5},
     *     }     *   
     *   } 
     * @throws JSONException 
     */
    public  void  get_enviroment_state(Message msg){
		JSONObject json= new JSONObject();
    	int sender=0;
		try {
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().getInt("sender");
			}
			json.put("sender",2);
			json.put("receiver",sender);
			int roomID  = msg.getJson().getInt("roomID");
			int ctrolID = msg.getJson().getInt("ctrolID");
			
		    json.put("roomID",roomID);
		    
    	    String stateStr=this.jedis.hget("houseState:"+ctrolID,roomID+"");
    	    if(stateStr!=null){
    	    	JSONObject jo=new JSONObject(stateStr); 
    	    	json.put("houseState", jo);
    	    }else{
    	    	
    	    	json.put("errorCode", HOUSE_STATE_NOT_EXIST);
    	    }
     	} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			
		}
    	msg.setCommandID(GET_ENVIROMENT_STATE_ACK);
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
    	
    }
    
	/** 获取一个房间
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     sender:0
	 *     receiver:2
     *   }
     *   @return language 的jsonArray格式
     *   {
     *    ctrolID:1234567,   
     *	  languageid:22
     *   }
     *  
	 * @throws JSONException 
     */
	public void get_language(Message msg){
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
			Language lan=Language.getLanguage(mysql, ctrolID);
	    	if(lan!=null ){
				json.put("ctrolID", lan.ctrolID);
				json.put("languageID", lan.languageID);
				json.put("modifyTime", lan.modifyTime);	    		
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				log.error("Can't language, ctrolID:"+ctrolID);
				json.put("errorCode",LANGUAGE_NOT_EXIST)   ;
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_LANGUAGE_ACK); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}		
	}
	
	public void get_language_ack(Message msg){
		
	}
    
	/** 获取一个房间
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     sender:0
	 *     receiver:2
     *   }
     *   @return language 的jsonArray格式
     *   {
     *    ctrolID:1234567,   
     *	  languageid:22
     *   }
     *  
	 * @throws JSONException 
     */
	public void set_language(Message msg){
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
			int languageID=msg.getJson().getInt("languageID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
			Language lan=new Language(ctrolID, languageID, new Date());
			jedis.hset(LogicControl.language,ctrolID+"", languageID+"");
    		int res=lan.saveLanguage(mysql);
	    	if(res>0 ){	    		
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				json.put("errorCode",SQL_ERROR)   ;
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( SET_LANGUAGE_ACK); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}		
	}
	public void set_language_ack(Message msg){
		
	}
	
	/** 获取布防状态
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     ctrolID:1234567
	 *     sender:0    
	 *     receiver:2
     *   }
     *   @return language 的jsonArray格式
     *   {
     *    errorCode:0   
     *	  		monitorStatus:    0:没有布防； 1：正在输入密码(60秒输入时间);  2:布防  
     *    		time:"2015-10-26 12:13:14"
     *    		sender:最后一次操作布防的角色   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务； 7 情景模式； 8遥控器；
     *   }
     *  
	 * @throws JSONException 
     */
	public void get_monitor_status(Message msg){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=new JSONObject();
    	int ctrolID;
		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	int sender=0;
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
			String str=jedis.hget(LogicControl.monitorStatus,ctrolID+"");			
	    	if(str!=null ){	
	    		MonitorStatus status=new MonitorStatus(new JSONObject(str));
	    		json.put("monitorStatus",status.getMonitorStatus()); 
	    		json.put("time",sdf.format(status.getTime())); 
	    		json.put("errorCode",SUCCESS);   
	    	}else {
				json.put("errorCode",SQL_ERROR)   ;
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		}
    	msg.setCommandID( GET_MONITOR_STATUS_ACK); 
		msg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
	}
	public void get_monitor_status_ack(Message msg){
		
	}
	/** 上报布防状态
	 * 	 * 	 <pre>对应json消息体为：
     *   {
	 *     sender:0    
	 *     receiver:2
     *    status:{
     *	  		monitorStatus:    0:没有布防； 1：正在输入密码(60秒输入时间);  2:布防  
     *    		time:"2015-10-26 12:13:14"
     *    		sender:最后一次操作布防的角色   0: 中控;1: 手机 ; 2:设备控制服务器; 3:web端; 4 :主服务; 5:	分析服务; 6:消息服务； 7 情景模式； 8遥控器；
     *    	   }
     *   }
     *   
	 *   @return  的json格式
	 *   {
	 *     sender:0    
	 *     receiver:2
	 *     errorCode:0
     *   }  
	 * @throws JSONException 
     */
	public void set_monitor_status(Message msg){
		Message replyMsg=new Message(msg);
    	JSONObject json=new JSONObject();
    	int ctrolID;
    	int sender=0;

		try {
			ctrolID = msg.getJson().getInt("ctrolID");
	    	if(msg.getJson().has("sender")){
	    		sender=msg.getJson().getInt("sender"); 
	    	}
			json.put("sender",2);
			json.put("receiver",sender);
			//JSONObject jsonTemp=msg.getJson().getJSONObject("status");
			MonitorStatus status=new MonitorStatus(msg.getJson());
	    	if(status!=null ){	
				jedis.hset(LogicControl.monitorStatus,ctrolID+"",status.toJson().toString()); 	
        		switch (status.getMonitorStatus()) {
				case 0:  //没有布防	
					status.stopMonitor();
					break;
				case 1:  //正在输入密码
					status.enterPassword();
					break;
				case 2:  //布防成功，发送通知
					status.SendWarnMsg(WarnID.START_MONITOR);
					break;
				default:
					break;
				}

        		if(sender==Role.PHONE){
        			msg.setTimeOut(60);
        			to.put(msg.getCookie(),msg);
        			
            		Message msg2=new Message(msg);            		
        			msg2.getJson().put("receiver",0);
        			msg2.setServerID(1); //转给主服务器        
        			msg2.getJson().put("sender",2);       
        			msg2.setJson(msg2.getJson());
        			CtrolSocketServer.sendCommandQueue.offer(msg2, 100, TimeUnit.MILLISECONDS);
        			return;	
        		}else if(sender==Role.CONTROL){
        			json.put("errorCode",SUCCESS);
        		}else{
        			log.error("unknow Sender:"+sender+",ctrolID="+ctrolID);
        		}
	    	}
		} catch (JSONException e1) {
			e1.printStackTrace();
			try {
				json.put("errorCode",JSON_PARSE_ERROR);
				json.put("errorDescription",e1.toString());
			} catch (JSONException e) {
				e.printStackTrace(); logException(e);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		replyMsg.setCommandID( SET_MONITOR_STATUS_ACK); 
		replyMsg.setJson(json);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(replyMsg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace(); logException(e);
		}
		
	}
	public void set_monitor_status_ack(Message msg){
		
	}
	
    public static void logException(Exception e){
    	   StringWriter sw = new StringWriter();
    	   e.printStackTrace(new PrintWriter(sw, true));
    	   String str = sw.toString();
    	   log.error ( "Exception:" + str ) ;
    }
    


	
}
