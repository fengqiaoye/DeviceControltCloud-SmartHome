﻿package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import cooxm.devicecontrol.util.*;


/** 
 * 整个家庭家电、传感器列表 
 */
public class Device {	
	int deviceID;
	int ctrolID;
	String deviceSN;
	int roomType;
	int roomID;
	/***deviceType
	101	图灵猫中控
	111	图灵猫旋钮
	121	图灵猫CO探测
	131	图灵猫插座
	141	图灵猫插座棒
	201	烟雾探测器
	211	漏水探测器
	221	门磁
	231	其他探测器
	241	中继器
	251	门铃
	301	主机遥控器
	311	智能门锁
	321	报警器
	331	漏水机械手
	341	天然气机械手
	401	无线遥控灯
	411	遥控窗
	421	遥控窗帘
	501	电视
	511	机顶盒
	521	视频盒子
	531	音响
	541	空调
	551	冰箱
	561	热水器
	571	灯
	581	取暖器
	591	空气净化器
	601	电风扇
	611	饮水机
	801	电饭煲
	811	豆浆机
	821	电烤箱
	831	电水壶
	841	微波炉
	1001	扫地机器人
	1011	擦窗机
	1021	拖地机
	1031	镜面加热
	1041	智能马桶
	1051	加香机
	1061	投影仪
	1071	投影幕
	1081	自动演奏钢琴
	1091	除湿机
	1101	加湿器
	1111	洗衣机
	1121	美发器
	1131	遥控车门
	1141	其他家电
	2201	五合一传感器
	2211	红外发射器
	2221	射频发射器
	*/
	int deviceType;
	
	/***0：家电   ;	1：传感器*/
	int type;
	/***
	 （顺时针方向）
	1：黄
	2:黄蓝
	3：蓝
	4：蓝绿
	5: 绿
	6：红绿
	7：红
	8：洪荒
	9：中间,与墙壁无关
	 */
	int wall; 	
	
	/***relatedDevType定义参见deviceType, 代表没有关联*/
	int relatedDevType; 
	Date createTime;
	public Date modifyTime;
	
	DeviceState state;
	
	public static final String deviceBindTable="info_user_room_bind";
	
	
	public int getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public String getDeviceSN() {
		return deviceSN;
	}
	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}
	public int getRoomID() {
		return roomID;
	}
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	
	public int getRoomType() {
		return roomType;
	}
	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getWall() {
		return wall;
	}
	public void setWall(int wall) {
		this.wall = wall;
	}
	public int getRelatedDevType() {
		return relatedDevType;
	}
	public void setRelatedDevType(int relatedDevType) {
		this.relatedDevType = relatedDevType;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	
	public Device(){}
	public Device(
			int ctrolID,
			int deviceID,
			String deviceSN,
			int deviceType,
			int type,
			int roomID, 
			int roomType,
			int wall,
			int relatedDevType, 
			Date createTime,
			Date modifyTime
			) {		
		this.deviceID              =   deviceID      ;    
		this.deviceSN              = deviceSN    ;
		this.ctrolID              =    ctrolID       ;
		this.roomID              =     roomID        ;
		this.roomType			 = roomType;
		this.deviceType              = deviceType    ;
		this.type              =       type          ;
		this.wall              =       wall          ;
		this.relatedDevType          = relatedDevType;
		this.createTime              = createTime    ;
		this.modifyTime              = modifyTime    ;
		this.state=null;
	}
	

	
	public Device (JSONObject deviceJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.deviceID=deviceJson.getInt("deviceID");
			this.deviceSN=deviceJson.getString("deviceSN");
			this.ctrolID=deviceJson.getInt("ctrolID");	
			this.roomID=deviceJson.getInt("roomID");
			this.roomType=deviceJson.getInt("roomType");
			this.deviceType=deviceJson.getInt("deviceType");
			this.type=deviceJson.getInt("type");
			this.wall=deviceJson.getInt("wall");
			this.relatedDevType=deviceJson.getInt("relatedDevType");
			this.createTime=sdf.parse(deviceJson.getString("createTime"));
			this.modifyTime=sdf.parse(deviceJson.getString("createTime"));	
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject toJsonObj(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject deviceJson = new JSONObject();  
	    try {
			deviceJson.put("ctrolID",         this.ctrolID      );
		    deviceJson.put("deviceID",        this.deviceID      );
		    deviceJson.put("deviceSN",        this.deviceSN       );
		    deviceJson.put("roomID",          this.roomID        );
		    deviceJson.put("roomType",        this.roomType        );
		    deviceJson.put("deviceType",      this.deviceType    );
		    deviceJson.put("type",            this.type          );
		    deviceJson.put("wall",            this.wall          );
		    deviceJson.put("relatedDevType",  this.relatedDevType);
		    deviceJson.put("createTime",      sdf.format(this.createTime    ));
		    deviceJson.put("modifyTime",      sdf.format(this.modifyTime    ));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return deviceJson;
	}
	
	/*** 
	 * get device name:
	 * @return deviceName : a string name of a device depends on device ID
	 * <br> deviceType: <br>	
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
	public String getDeviceName(){
		switch (this.deviceID) {
		case 10:
			return "light";
		case 20:
			return "tv";
		case 40:
			return "aircon";
		case 60:
			return "window";
		case 80:
			return "curtain";
		case 90:
			return "heating";
		case 1010:
			return "noicesensor";
		case 1020:
			return "lightsensor";
		case 1030:
			return "thermometer";
		case 1040:
			return "humidity";
		case 1050:
			return "fourinone";
		case 1051:
			return "fourinone-noice";
		case 1052:
			return "fourinone-light";
		case 1053:
			return "fourinone-thermometer";
		case 1054:
			return "fourinone-humidity";
		case 1060:
			return "pm25";
		case 1070:
			return "poisongas";
		case 1080:
			return "powpoint";
		case 2040:
			return "rfid";
		case 2050:
			return "infrared";
		default:
			return  "unknown";
		} 		
	}
	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 */
	public int saveToDB(MySqlClass mysql){
		String tablename="info_user_room_bind";
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="replace into "+tablename
				+" (ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomtype       ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+ctrolID+","
				+deviceID+",'"
				+deviceSN+"',"
				+deviceType+","
				+type+","
				+roomType+","
				+roomID+","
				+wall+","
				+relatedDevType+",'"
				+sdf.format(createTime)+"','"
				+sdf.format(createTime)
				+"')";
		//System.out.println(sql);
		return mysql.query(sql);		
	}

	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public static Device getOneDeviceFromDB(MySqlClass mysql,int ctrolID,int deviceID ){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Device device=new Device();
		String sql="select  "
				+" ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomtype       ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and devid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res2=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null;
		} else if(res2.split("\n").length!=1){
			System.err.println("ERROR:Multi device retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			device.ctrolID=Integer.parseInt(index[0]);	
			device.deviceID=Integer.parseInt(index[1]);	
			device.deviceSN=index[2];
			device.deviceType=Integer.parseInt(index[3]);
			device.type=Integer.parseInt(index[4]);
			device.roomType=Integer.parseInt(index[5]);
			device.roomID=Integer.parseInt(index[6]);
			device.wall=Integer.parseInt(index[7]);
			device.relatedDevType=Integer.parseInt(index[8]);
			try {
				device.createTime=sdf.parse(index[9]);
				device.modifyTime=sdf.parse(index[10]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return device;
	}
	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public List<Device> getDevicesByctrolID(MySqlClass mysql,int ctrolID ){
		List<Device> deviceList=null;
		Device device=null;
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String sql="select  "
				+" ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomtype       ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
//				+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		String res2=mysql.select(sql);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null;
		} else{
			String[] res3 =res2.split("\n");
			deviceList=new ArrayList<Device>();
			for(String line: res3){				
				String[] index=line.split(",");
				device= new Device();
				device.ctrolID=Integer.parseInt(index[0]);	
				device.deviceID=Integer.parseInt(index[1]);	
				device.deviceSN=index[2];
				device.deviceType=Integer.parseInt(index[3]);
				device.type=Integer.parseInt(index[4]);
				device.roomType=Integer.parseInt(index[5]);
				device.roomID=Integer.parseInt(index[6]);
				device.wall=Integer.parseInt(index[7]);
				device.relatedDevType=Integer.parseInt(index[8]);
				try {
					device.createTime=sdf.parse(index[9]);
					device.modifyTime=sdf.parse(index[10]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				deviceList.add(device);
			}
		}
		return deviceList;
	}
	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public static int DeleteOneDeviceFromDB(MySqlClass mysql,int ctrolID,int deviceID ){
		String sql="delete  * "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		int res2=mysql.query(sql);
		System.out.println("deleted "+ res2 + "rows of recodes");
		if(res2<=0){
			System.err.println("ERROR:  "+sql);
			return 0;
		} 
		return 1;
	}
	
	
	public List<Device> getDevicesByRoomIDFromDB(MySqlClass mysql,int ctrolID,int deviceID ){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Device device=new Device();
		String sql="select  "
				+" ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		String res=mysql.select(sql);
		System.out.println("get from mysql:\n"+res);
		if(res==null|| res==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null;
		} 
		
		String[] resArray=res.split("\n");
		List<Device> deviceList=null;//new ArrayList<Factor>();
		Device device=new Device();
		for(String line:resArray){
			String[] index=line.split(",");
			device.ctrolID=Integer.parseInt(index[0]);	
			device.deviceID=Integer.parseInt(index[1]);	
			device.deviceSN=index[2];
			device.deviceType=Integer.parseInt(index[3]);
			device.type=Integer.parseInt(index[4]);
			device.roomType=Integer.parseInt(index[5]);
			device.roomID=Integer.parseInt(index[6]);
			device.wall=Integer.parseInt(index[7]);
			device.relatedDevType=Integer.parseInt(index[8]);
			try {
				device.createTime=sdf.parse(index[9]);
				device.modifyTime=sdf.parse(index[10]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deviceList=new ArrayList<Device>();
			deviceList.add(device);
		}

		return deviceList;
	}
	
	
	
	
	  public static void main(String[] args) throws SQLException{
		  //MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		  Date date=new Date();
//		  int count=dev.saveToDB(mysql);		
//		  System.out.println("Query OK,  "+count+" row affected.");
		  int ctrolID=1256787;
		  Jedis jedis= new Jedis("120.24.81.226", 6379);
		  Device dev=new Device(ctrolID , 1256786 , "XJFGOD847X" ,      571 ,    0 ,  2,  101 ,    1, 0,date,date);
		  jedis.hset(ctrolID+"_roomBind", 101+"", dev.toJsonObj().toString());
		  System.out.println("Query OK");
		  
		  

		  
		

	  }
	

}
