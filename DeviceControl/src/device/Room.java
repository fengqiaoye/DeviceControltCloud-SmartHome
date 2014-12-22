package device;

import java.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import util.MySqlClass;
import  org.apache.log4j.*;


public class Room {
	int roomID;
	String roomName;
	int CtrolID;
	int roomType; //1：客厅；	2：卧室	3：厨房	4：卫生间
	List<Profile> profileList;
	List<Device> deviceList;
	Date createTime;
	Date modifyTime;
	
	Profile currProfile;
	private static final Logger logger = Logger.getLogger("global");
	
	static final String roomIndexTable = "info_user_room";

	public Room(){}
	public Room(Room room) {
		this.roomID       =     room.roomID      ;  
		this.roomName      =    room.roomName    ;
		this.CtrolID      =     room.CtrolID     ;
		this.roomType      =    room.roomType    ;
		this.currProfile      = room.currProfile ;
		this.profileList      = room.profileList ;
		this.deviceList      =  room.deviceList  ;
		this.createTime      =  room.createTime  ;
		this.modifyTime      =  room.modifyTime  ;
	}
	
	public Profile getProfileByProfileID(int profileID){		
		for (int i = 0; i < this.profileList.size(); i++) {
			if(this.profileList.get(i).profileID==profileID){
				return this.profileList.get(i);
			}			
		}
		return null;
	}
	
	public Device getDeviceByDeviceID(int deviceID){		
		for (int i = 0; i < this.deviceList.size(); i++) {
			if(this.deviceList.get(i).deviceID==deviceID){
				return this.deviceList.get(i);
			}			
		}
		return null;
	}
	
	/***@deviceType
	0-10：保留
	10：灯
	20：电视
	40: 空调
	60：窗户
	80：窗帘
	90：暖器

	1010:声感器
	1020:光感器
	1030：温感器
	1040：湿感器
	1050：声光温湿四合一传感器
	1051:四合一声感
	1052：四合一光感
	1053：四合一温感
	1054：四合一湿感
	1060:PM2.5检测器
	1070:有害气体检测器
	1080:智能插座

	2040:射频发射器
	2050:红外发射器
	*/
	public List<Device> getDevicesByDeviceType(int deviceType){	
		List<Device> deviceList =new ArrayList<Device> ();
		for (Device d:deviceList) {
			if(d.deviceType==deviceType){
				deviceList.add(d);
			}			
		}
		return deviceList;
	}
	
	/*** @param： type
	 * 0：家电   ;	1：传感器*/
	public List<Device> getDevicesByType(int type){	
		List<Device> deviceList =new ArrayList<Device> ();
		for (Device d:deviceList)  {
			if(d.type==type){
				deviceList.add(d);
			}			
		}
		return deviceList;
	}
	
	
	
	
	public void switchToProfile(Profile profile){
		this.currProfile=profile;		
	}
	
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:		    MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table roomIndexTable :  info_user_room
	 * @throws SQLException 
	 * */
	public boolean saveRoomIndexToDB(MySqlClass mysql) throws SQLException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String sql="insert into "+roomIndexTable
				+" (ctr_id       ," 
				+" roomid       ," 
				+"roomtype        ,"
				+"description        ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.CtrolID+","	
				+this.roomID+","	
				+this.roomType+",'"
				+this.roomName+"','"
				+sdf.format(this.createTime)+"','"
				+sdf.format(this.modifyTime)
				+"');";
		logger.info(sql);
		if(mysql.query(sql)!=-1){
		  logger.info("insert success!");
		  return true;
		}
		return false;
	}
	
   /*** 
   * 从入MYSQL读取room的 基本情况
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st
   * @throws SQLException 
   * @throws IOException 
   */
	public static Room  getRoomHeadFromDB(MySqlClass mysql,int CtrolID,int roomID) throws SQLException, IOException
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Room room =new Room();
		String sql2="select  "
				+" ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"description  ,"
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from "				
				+roomIndexTable
				+" where ctr_id="+CtrolID
				+" and roomid="+roomID
				+ ";";
		//logger.info("query:"+sql2);
		logger.info("query:"+sql2);
		String res2=mysql.select(sql2);
		logger.info("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			logger.info("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			logger.info("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			room.CtrolID=Integer.parseInt(index[0]);	
			room.roomID=Integer.parseInt(index[1]);	
			room.roomType=Integer.parseInt(index[2]);
			room.roomName=index[3];
			try {
				room.createTime=sdf.parse(index[4]);
				room.modifyTime=sdf.parse(index[5]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//logger.log.error(e,e.printStackTrace());
				//logger.error(e.getMessage(),e); 
			}
		}		
	
		return room;		
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		Room room =new Room();
		room=Room.getRoomHeadFromDB(mysql, 12345677, 201);
		room.roomID++;
		
		try {
			room.saveRoomIndexToDB(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//logger.log.error(e.printStackTrace());
			//logger.error(e.getMessage(),e); 
		}
	}

}
