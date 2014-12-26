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
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import device.Profile;
import device.ProfileMap;
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
	
	/*** 中控切换情景模式命令 */
	private static final short CONTROL_SWITCH_ROOM_PROFILE		=	COMMAND_START+3;
	/*** 中控切换情景模式命令 的回复 */
	private static final short CONTROL_SWITCH_ROOM_PROFILE_ACK	=	COMMAND_START+3+COMMAND_ACK_OFFSET;

	
	/*** 中控切换情景模式命令 */
	private static final short MOBILE_SWITCH_ROOM_PROFILE		=	COMMAND_START+4;
	/*** 手机情景模式命令 的回复 */
	private static final short MOBILE_SWITCH_ROOM_PROFILE_ACK	=	COMMAND_START+4+COMMAND_ACK_OFFSET;
	
	
	/*** 请求 情景模式集 */	
	private static final short GET_RROFILE_SET					=	COMMAND_START+21;
	/*** 请求 情景模式集 的回复*/	
	private static final short GET_RROFILE_SET_ACK				=	COMMAND_START+21+COMMAND_ACK_OFFSET;
	
	/*** 设置 情景模式集*/
	private static final short SET_RROFILE_SET					=	COMMAND_START+22;
	/*** 设置 情景模式集 的回复*/
	private static final short SET_RROFILE_SET_ACK				=	COMMAND_START+22+COMMAND_ACK_OFFSET;
	
	/*** 情景模式集切换 */
	private static final short CONTROL_SWITCH_RROFILE_SET		=	COMMAND_START+23;	
	/*** 情景模式集切换 的回复*/
	private static final short CONTROL_SWITCH_RROFILE_SET_ACK	=	COMMAND_START+23+COMMAND_ACK_OFFSET;
	
	/*** 情景模式集切换 */
	private static final short MOBILE_SWITCH_RROFILE_SET		=	COMMAND_START+24;	
	/*** 情景模式集切换 的回复*/
	private static final short MOBILE_SWITCH_RROFILE_SET_ACK	=	COMMAND_START+24+COMMAND_ACK_OFFSET;
	
	/*** 请求家电列表*/
	private static final short GET_APP_LIST						=	COMMAND_START+41;
	/*** 请求家电列表 的回复*/
	private static final short GET_APP_LIST_ACK					=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** 设置 家电列表*/
	private static final short SET_APP_LIST						=	COMMAND_START+42;
	/*** 设置 家电列表 的回复*/
	private static final short SET_APP_LIST_ACK					=	COMMAND_START+42+COMMAND_ACK_OFFSET;	
	
	/*** 切换某个家电状态*/
	private static final short CONTROL_SWITCH_APP_STATE		    		 =	COMMAND_START+43;
	/*** 切换某个家电状态 的回复*/
	private static final short CONTROL_SWITCH_APP_STATE_ACK		    	=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
	/*** 切换某个家电状态*/
	private static final short MOBILE_SWITCH_APP_STATE		    		=	COMMAND_START+43;
	/*** 切换某个家电状态 的回复*/
	private static final short MOBILE_SWITCH_APP_STATE_ACK		    	=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
    /*** 告警消息   */
	private static final short WARNING_MSG				 		=	COMMAND_START+61;
    /*** 告警消息  的回复  */
	private static final short WARNING_MSG_ACK				 	=	COMMAND_START+61+COMMAND_ACK_OFFSET;	
	
	
	/***********************  ERROR CODE :-50000  :  -59999 ************************/
	private static final int SUCCESS                  =	0;
	private static final int PROFILE_OBSOLETE         =	-50001;	
	private static final int PROFILE_NOT_EXIST        = -50002;		

	

	
	static Logger log= Logger.getLogger(LogicControl.class);
	MySqlClass mysql=null;
	ProfileMap profileMap =null;
	
    public LogicControl() {
		// TODO Auto-generated constructor stub
	}
    
    public LogicControl(MySqlClass mysql) {
		this.mysql=mysql;
		try {
			this.profileMap= new ProfileMap(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			
		case SWITCH_ROOM_PROFILE:	
			String plate2=new String("utf-8");
		case GET_RROFILE_SET:	
			String plate3=new String("utf-8");
		case SET_RROFILE_SET:	
			String plate12=new String("utf-8");			
		case SWITCH_RROFILE_SET:	
			String plate4=new String("utf-8");
		case GET_APP_LIST:	
			String plate5=new String("utf-8");
		case SET_APP_LIST:	
			String plate11=new String("utf-8");			
		case SWITCH_APP_STATE:	
			String plate6=new String("utf-8");
		case WARNING_MSG:	
			String plate7=new String("utf-8");
		case EMERGENCY:	
			String plate8=new String("utf-8");
			
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
    	Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	Date jsonModifyTime=sdf.parse(json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileID;
    	if(!profileMap.containsKey(key)){
    		profile=new Profile(json);
    		profile.saveProfileToDB(mysql);
    		return true;
    	}else {
    		profile= profileMap.get(key);
    	}
    	
    	if(profile.modifyTime.after(jsonModifyTime)){
    		return false;    		
    	}else{
    		return true;
    	}
    }
	
    
   /***  中控请求切换情景模式
    * <pre>传入的json格式为：
   * { 
   *   comand:SWITCH_ROOM_PROFILE 
   *   CtrolID:1234567
   *   profileID:7654321
   * }
	 * */
    public void control_switch_room_profile(){
    	
    }
    
    /*** 手机请求切换情景模式
     * <pre>传入的json格式为：
    * { 
    *   comand:SWITCH_ROOM_PROFILE 
    *   CtrolID:1234567
    *   profileID:7654321
    * }
 	* */
    public void mobile_switch_room_profile(){
    	
    }
    
	/*** 请求 情景模式集 
     *json格式和请求为： <pre>
     * { 
     *   GET_RROFILE_SET 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
	 * */	
	public void GET_RROFILE_SET(){
		
	}
	
	/*** 设置 情景模式集
	 * <pre>Json格式和 设置情景模式 和 {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：
     * {  
     *   [  
     *   情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *   ]  
     * }
	 * */
	public void SET_RROFILE_SET(){
		
	}
	
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     comand:SWITCH_RROFILE_SET 
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void SWITCH_RROFILE_SET(){
		
	}
	
	/*** 请求家电列表
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     comand:GET_APP_LIST 
	 *     CtrolID:1234567
     *   }*/
	public void GET_APP_LIST(){
		
	}
	
	
	/*** 设置 家电列表
	 * 	 <pre>对应的：
	 *   {
	 *     将每一个家电转一个json对象，将整个房屋多个家电组成一个json数组
     *   }*/
	public void SET_APP_LIST()
	
	
	/*** 切换某个家电状态
	 * 	 <pre>例如对应json消息体如下格式 ：
	 *   {
	 *     comand:SWITCH_APP_STATE
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }*/
	public void SWITCH_APP_STATE()		  
	
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
	public void warn()				=	COMMAND_START+61;

    

	public static void main(String[] args) {		
		System.out.println(0x32FF+"!");

		


	}	
	
}
