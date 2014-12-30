package control;
/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import device.Device;
import device.DeviceMap;
import device.Profile;
import device.ProfileMap;
import device.ProfileSet;
import device.ProfileSetMap;
import redis.clients.jedis.Jedis;
import socket.CtrolSocketServer;
import socket.Message;
import util.MySqlClass;


public class LogicControl {	
	
	private static final short COMMAND_START            =  0x1600;
	private static final short COMMAND_ACK_OFFSET       =  0x4000; 
	
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

	
	/*** 请求家电列表*/
	private static final short GET_ONE_DEVICE				=	COMMAND_START+41;
	/*** 请求家电列表 的回复*/
	private static final short GET_ONE_DEVICE_ACK			=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电列表*/
	private static final short SET_ONE_DEVICE				=	COMMAND_START+42;
	/*** 设置 家电列表 的回复*/
	private static final short SET_ONE_DEVICE_ACK			=	COMMAND_START+42+COMMAND_ACK_OFFSET;	

	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_DEVICE				=	COMMAND_START+43;
	/*** 删除某一个 家电*/
	private static final short DELETE_ONE_DEVICE_ACK			=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
	/*** 切换某个家电状态*/
	private static final short SWITCH_DEVICE_STATE		    =	COMMAND_START+44;
	/*** 切换某个家电状态 的回复*/
	private static final short SWITCH_DEVICE_STATE_ACK		=	COMMAND_START+44+COMMAND_ACK_OFFSET;
		
    /*** 告警消息   */
	private static final short WARNING_MSG				 		=	COMMAND_START+61;
    /*** 告警消息  的回复  */
	private static final short WARNING_MSG_ACK				 	=	COMMAND_START+61+COMMAND_ACK_OFFSET;	
	
	
	/***********************  ERROR CODE :-50000  :  -59999 ************************/
	private static final int SUCCESS                  =	0;
	
	private static final int PROFILE_OBSOLETE         =	-50001;	
	private static final int PROFILE_NOT_EXIST        = -50002;		
	private static final int PROFILE_SET_OBSOLETE     =	-50003;	
	private static final int PROFILE_SET_NOT_EXIST    = -50004;	
	
	private static final int DEVICE_OBSOLETE   	  = -50011;
	private static final int DEVICE_NOT_EXIST   	  = -50012;

	

	/***********************  resource needed ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	MySqlClass mysql=null;
	ProfileMap profileMap =null;
	ProfileSetMap profileSetMap =null;
	DeviceMap deviceMap=null;
	Jedis jedis= new Jedis("172.16.35.170", 6379,200);
	
    public LogicControl() {
		// TODO Auto-generated constructor stub
	}
    
    public LogicControl(MySqlClass mysql) {
		this.mysql=mysql;
		try {
			this.profileMap= new ProfileMap(mysql);
			this.profileSetMap= new ProfileSetMap(mysql);
			this.deviceMap=new DeviceMap(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
			
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
			try {
				get_room_profile(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ROOM_PROFILE:
			try {
				set_room_profile(msg,mysql);
			} catch (JSONException | SQLException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
		case DELETE_ROOM_PROFILE:
			try {
				delete_room_profile(msg,mysql);
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case SWITCH_ROOM_PROFILE:	
			try {
				switch_room_profile(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case GET_RROFILE_SET:	
			try {
				get_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_RROFILE_SET:	
			try {
				set_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
		case DELETE_RROFILE_SET:
			try {
				delete_room_profile(msg,mysql);
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case SWITCH_RROFILE_SET:	
			switch_profile_set(msg,mysql);
			break;
		case GET_ONE_DEVICE:	
			try {
				get_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ONE_DEVICE:	
			try {
				get_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_ONE_DEVICE:
			try {
				delete_one_device(msg,mysql);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;			
		case SWITCH_DEVICE_STATE:	
			try {
				get_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case WARNING_MSG:	
			try {
				get_profile_set(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;			
		}		
	}
	
    /*** 从Map或者 MYSQL查询情景模式
     * <pre>传入的json格式为：
     * { 
     *   comand:GET_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void get_room_profile(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	String key=CtrolID+"_"+profileID;
    	if(profileMap.containsKey(key)){
    		profile= profileMap.get(key);
    		msg.json=profile.toJsonObj();
    	}else if(( profile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		msg.json=profile.toJsonObj();;
    	}else {
			log.warn("Can't get_room_profile CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID+= GET_ROOM_PROFILE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		"profileID":123456789,
		"CtrolID":12345677,
		"profileName":"未知情景",
		"profileSetID":12345,
		"profileTemplateID":0,
		"roomID":203,
		"roomType":2,
		"factorList":
		[
			{"factorID":40,"minValue":20,"compareWay":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
			"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
			},

			{"factorID":60,"minValue":1,"compareWay":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
			"createTime":"2014-12-13 14:15:17","maxValue":1
			}
		],
		"modifyTime":"2014-12-13 14:15:17",
		"createTime":"2014-12-13 14:15:17"
	  }
     * @throws ParseException 
	*/
    public void set_room_profile(Message msg,MySqlClass mysql) throws JSONException, SQLException, ParseException{
    	JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile profile=new Profile(msg.json);
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	Date jsonModifyTime=sdf.parse(json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileID;
    	
    	if( this.profileMap.containsKey(key) && this.profileMap.get(key).modifyTime.after(jsonModifyTime)){	//云端较新  
			msg.json=null;
			msg.json.put("errorCode",PROFILE_OBSOLETE);    		
    	}else{ //云端较旧，则保存
    		this.profileMap.put(key, profile);
			msg.json=null;
			msg.json.put("errorCode",SUCCESS);   
			}    	
  		msg.header.commandID=SET_ROOM_PROFILE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** 删除情景模式
     * <pre>传入的json格式为：
     * { 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_room_profile(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	//Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	String key=CtrolID+"_"+profileID;
    	if(profileMap.containsKey(key)){
    		profileMap.remove(key);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);    		
    	}else if((Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		Profile.deleteProfileFromDB(mysql, CtrolID, profileID);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profile not exist CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID+= DELETE_ROOM_PROFILE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
	
    
    /*** 请求切换情景模式,根据命令的发送方有不同的响应方式
     * <pre>传入的json格式为：
    * { 
    *   senderRole:"control"/"mobile"/"cloud"
    *   CtrolID:1234567
    *   profileID:7654321
    * }
 	* */
    public void switch_room_profile(Message msg,MySqlClass mysql)throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	String key=CtrolID+"_"+profileID;
    	if(profileMap.containsKey(key)){
    		profile= profileMap.get(key);
    	}else if(( profile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){    		
    	}else {
			log.warn("Can't get_room_profile CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
			msg.header.commandID=SWITCH_ROOM_PROFILE_ACK;
	    	try {
				CtrolSocketServer.sendCommandQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	return;
    	}    	    	
    }
    
    /*** 查询情景模式集
     * <pre>传入的json格式为：
     * { 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50004}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void get_profile_set(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	ProfileSet profileSet=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileSetID=json.getInt("profileSetID");
    	String key=CtrolID+"_"+profileSetID;
    	if(profileSetMap.containsKey(key)){
    		profileSet= profileSetMap.get(key);
    		msg.json=profileSet.toJsonObj();
    	}else if(( profileSet=ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))!=null){
    		msg.json=profileSet.toJsonObj();;
    	}else {
			log.warn("Can't get_profile_set, CtrolID:"+CtrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.header.commandID+=  GET_RROFILE_SET_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    

	
	/*** 设置 情景模式集
	 * <pre>Json格式和 设置情景模式 和 {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：
     * {  
     *   [  
     *   情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *   ]  
     * }
	 * */
	public void set_profile_set(Message msg,MySqlClass mysql) throws JSONException, SQLException, ParseException{
    	JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	ProfileSet profileSet=new ProfileSet(msg.json);
    	int CtrolID=json.getInt("CtrolID");
    	int profileSetID=json.getInt("profileSetID");
    	Date jsonModifyTime=sdf.parse(json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileSetID;
    	
    	if( profileSetMap.containsKey(key) && profileSetMap.get(key).modifyTime.after(jsonModifyTime)){	//云端较新  
			msg.json=null;
			msg.json.put("errorCode",PROFILE_SET_OBSOLETE);    		
    	}else{
    		profileSetMap.put(key, profileSet);
			msg.json=null;
			msg.json.put("errorCode",SUCCESS);   		
    	}    	
  		msg.header.commandID= SET_RROFILE_SET_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 	
	}
	
    /*** 删除情景模式集
     * <pre>传入的json格式为：
     * { 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_profile_set(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	//Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileSetID=json.getInt("profileSetID");
    	String key=CtrolID+"_"+profileSetID;
    	if(profileSetMap.containsKey(key)){
    		profileSetMap.remove(key);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);    		
    	}else if((ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))!=null){
    		ProfileSet.deleteProfileSetFromDB(mysql, CtrolID, profileSetID);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profileSet not exist CtrolID:"+CtrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.header.commandID+= DELETE_RROFILE_SET_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
	

	
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void switch_profile_set(Message msg,MySqlClass mysql){
		
	}
	
	/*** 获取一个设备
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     CtrolID:1234567
	 *     deviceID:
     *   }
     *   @return List< Device > 加电列表 的json格式
	 * @throws JSONException 
     *   */
	public void get_one_device(Message msg,MySqlClass mysql) throws JSONException{
    	JSONObject json=msg.json;
    	Device device=new Device();
    	int CtrolID=json.getInt("CtrolID");
    	int deviceID=json.getInt("deviceID");
    	String key=CtrolID+"_"+deviceID;
    	if(deviceMap.containsKey(key)){
    		device=deviceMap.get(key);
    		msg.json=null;
    		msg.json=device.toJsonObj();
    	}else if(null!= (device=Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))){
    		msg.json=null;
    		msg.json=device.toJsonObj();   		
    	}else {
			log.warn("Can't get_one_device, CtrolID:"+CtrolID+"deviceID: "+ deviceID+" from deviceMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.header.commandID+=  GET_ONE_DEVICE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	/*** 设置 一个家电
	 * 	 <pre>对应的jsonArray：	 * 
	 *   {
	 *     将这个家电的jsonObject格式
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_one_device(Message msg,MySqlClass mysql) throws JSONException, ParseException{
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	JSONObject json=msg.json;
    	Device device=new Device(msg.json);
    	int CtrolID=json.getInt("CtrolID");
    	int deviceID=json.getInt("deviceID");
    	Date jsonModifyTime=sdf.parse(json.getString("modifyTime"));
    	String key=CtrolID+"_"+deviceID;
    	
    	if( this.deviceMap.containsKey(key) && this.deviceMap.get(key).modifyTime.after(jsonModifyTime)){	//云端较新  
			msg.json=null;
			msg.json.put("errorCode",DEVICE_OBSOLETE);    		
    	}else{ //云端较旧，则保存
    		this.deviceMap.put(key, device);
			msg.json=null;
			msg.json.put("errorCode",SUCCESS);   
			}    	
  		msg.header.commandID=SET_ONE_DEVICE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	
    /*** 删除情景模式集
     * <pre>传入的json格式为：
     * { 
     *   CtrolID:1234567
     *   deviceID:7654321
     * }
     * @throws JSONException 
     * @return message 的json格式：
     *   （1）如果查询的情景模式不存在，返回jason： {"errorCode":-50002}
     *   （2）如果查询的情景模式存在，则返回情景模式的json格式                  
     */
    public void delete_one_device(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	int CtrolID=json.getInt("CtrolID");
    	int deviceID=json.getInt("deviceID");
    	String key=CtrolID+"_"+deviceID;
    	if(deviceMap.containsKey(key)){
    		deviceMap.remove(key);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);    		
    	}else if((Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))!=null){
    		Device.DeleteOneDeviceFromDB(mysql, CtrolID, deviceID);
    		msg.json=null;
    		msg.json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_device not exist CtrolID:"+CtrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.header.commandID+= DELETE_RROFILE_SET_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
	
	/*** 切换某个家电状态
	 * 	 <pre>例如对应json消息体如下格式 ：
	 *   {
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }*/
	public void SWITCH_APP_STATE(Message msg,MySqlClass mysql){
		
	}
	
  /*** 告警消息
  <pre>例如对应json消息体如下格式 ：
  {
    "warnID",  123456      
    "warnName","厨房漏气"      
    "warnContent", "检测你的厨房可燃气体超标，已自动帮你打开厨房的窗户~"  
    "type",  1  	
    "channel",0      
    "createTime","2014-12-25 12:13:14"    
    "modifyTime","2014-12-25 12:13:14"  
  }
  */
	public void warn(){
		
	}

    

	public static void main(String[] args) {		
		System.out.println(0x32FF+"!");

		


	}	
	
}
