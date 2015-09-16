package cooxm.devicecontrol.device;
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

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.*;
import redis.clients.jedis.Jedis;


/** 
 * 整个家庭家电、传感器列表 
 */
public class Device {	
	int deviceID;
	int ctrolID;
	String deviceName;
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
	
	/***0：传感器   ;	1：家电*/
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
	
	/** 1.设备是好的；  0，设备是坏的 */
	int state;
	/** 1 可以遥控，0 不可以遥控*/
	int remoteControl ;
	
	public static final String deviceBindTable="info_user_room_bind";
	
	
	public int getRemoteControl() {
		return remoteControl;
	}
	public void setRemoteControl(int remoteControl) {
		this.remoteControl = remoteControl;
	}
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
	

	
	public String getDeviceName() {
		return deviceName;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public Device(){}
	public Device(
			int ctrolID,
			String deviceName,
			int deviceID,
			String deviceSN,
			int deviceType,
			int type,
			int roomID, 
			int roomType,
			int wall,
			int relatedDevType, 
			Date createTime,
			Date modifyTime,
			int state,
			int remoteControl 
			) {		
		this.deviceID              =   deviceID      ; 
		this.deviceName            =   deviceName;
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
		this.state=state;
		this.remoteControl=remoteControl;
	}
	

	
	public Device (JSONObject deviceJson) throws JSONException, ParseException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.deviceID=deviceJson.getInt("deviceID");
			this.deviceSN=deviceJson.getString("deviceSN");
			this.deviceName=deviceJson.getString("deviceName");
			this.ctrolID=deviceJson.getInt("ctrolID");	
			this.roomID=deviceJson.getInt("roomID");
			this.roomType=deviceJson.getInt("roomType");
			this.deviceType=deviceJson.getInt("deviceType");
			this.type=deviceJson.getInt("type");
			this.wall=deviceJson.getInt("wall");
			if (deviceJson.has("relatedDevID")) {
				this.relatedDevType=deviceJson.getInt("relatedDevID");
			}else{
				this.relatedDevType=deviceJson.getInt("relatedDevType");
			}			
			this.createTime=sdf.parse(deviceJson.getString("createTime"));
			this.modifyTime=sdf.parse(deviceJson.getString("modifyTime"));	
			if(deviceJson.has("state")){
				this.state=deviceJson.getInt("state");
			}else{
				this.state=1;
			}
			
			if(deviceJson.has("remoteControl")){
				this.remoteControl=deviceJson.getInt("remoteControl");
			}else{
				this.remoteControl=1;
			}
	}
	
	public JSONObject toJsonObj(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject deviceJson = new JSONObject();  
	    try {
			deviceJson.put("ctrolID",         this.ctrolID      );
		    deviceJson.put("deviceID",        this.deviceID      );
		    deviceJson.put("deviceName",        this.deviceName       );
		    deviceJson.put("deviceSN",        this.deviceSN       );
		    deviceJson.put("roomID",          this.roomID        );
		    deviceJson.put("roomType",        this.roomType        );
		    deviceJson.put("deviceType",      this.deviceType    );
		    deviceJson.put("type",            this.type          );
		    deviceJson.put("wall",            this.wall          );
		    deviceJson.put("relatedDevType",  this.relatedDevType);
		    deviceJson.put("createTime",      sdf.format(this.createTime    ));
		    deviceJson.put("modifyTime",      sdf.format(this.modifyTime    ));
		    deviceJson.put("state",      this.state);
		    deviceJson.put("remoteControl", remoteControl);
		} catch (JSONException e) {
			e.printStackTrace();
		}	    
	    return deviceJson;
	}
	

	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	 * table Name:info_user_room_bind
	 */
	public int saveToDB(MySqlClass mysql){
		String tablename="info_user_room_bind";
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="replace into "+tablename
				+" (ctr_id       ,"  
				+"name,"
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomtype       ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime,   "
				+"state,   "
				+"remoteControl "
				+ ")"				
				+"values "
				+ "("
				+ctrolID+",'"
				+deviceName+"',"
				+deviceID+",'"
				+deviceSN+"',"
				+deviceType+","
				+type+","
				+roomType+","
				+roomID+","
				+wall+","
				+relatedDevType+",'"
				+sdf.format(createTime)+"','"
				+sdf.format(createTime)	+"',"
				+state+","
				+remoteControl
				+ ");";
		//System.out.println(sql);
		return mysql.query(sql);		
	}

	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
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
				+"modifytime,   "
				+"state ,  "
				+"remoteControl "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and devid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res2=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			//System.err.println("ERROR:empty query by : "+sql);
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
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
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
				+"modifytime,   "
				+"state,   "
				+" remoteControl "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
//				+" and deviceid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res2=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res2);
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
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public static int DeleteOneDeviceFromDB(MySqlClass mysql,int ctrolID,int deviceID ){
		String sql="delete   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and devid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		int res2=mysql.query(sql);
		//System.out.println("deleted "+ res2 + " rows of recodes");
		if(res2<=0){
			System.err.println("ERROR:  "+sql);
			return 0;
		} 
		return 1;
	}
	
	public static void deleteDeviceByRoomIDFromRedis(Jedis jedis,int ctrolID,int roomID) throws JSONException, ParseException{
		Map<String, String> DeviceMap = jedis.hgetAll(LogicControl.roomBind+ctrolID);
		for (Map.Entry<String, String> entry:DeviceMap.entrySet()) {
			Device p=new Device(new JSONObject(entry.getValue()));
			if(p.getRoomID()==roomID && p.getCtrolID()==ctrolID){
				jedis.del(LogicControl.roomBind+ctrolID);
			}				
		}		
	}
	
	public static void deleteDeviceStateByRoomIDFromRedis(Jedis jedis,int ctrolID,int roomID) throws JSONException, ParseException{
		Map<String, String> DeviceMap = jedis.hgetAll(LogicControl.currentDeviceState+ctrolID);
		for (Map.Entry<String, String> entry:DeviceMap.entrySet()) {
			Device p=new Device(new JSONObject(entry.getValue()));
			if(p.getRoomID()==roomID && p.getCtrolID()==ctrolID){
				jedis.del(LogicControl.roomBind+ctrolID);
			}				
		}		
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
				+"modifytime,   "
				+"state,   "
				+"remoteControl "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+ctrolID
				+" and deviceid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res);
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
	
	public static  List<Device> getDeviceFromRedisByType(Jedis jedis, int ctrolID,int roomID,int deviceType){
		List<Device> deviceList=new ArrayList<Device>();
		Set<String> deviceIDSet = jedis.hkeys(LogicControl.roomBind+ctrolID);
		if(deviceIDSet.size()==0){
			return null;
		}
		for (String deviceID:deviceIDSet) {
			Device device=new Device();
			String s=jedis.hget(LogicControl.roomBind+ctrolID, deviceID);
			if(s==null){
				return null;
			}
			try {
				device = new Device(new JSONObject(s));
				if (device.getDeviceType()==deviceType && device.getRoomID()==roomID) {
					deviceList.add(device);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return deviceList;
	}
	
	
	
	
	  public static void main(String[] args) throws SQLException{
		  MySqlClass mysql=new MySqlClass("120.24.81.226","3306","cooxm_device_control", "cooxm", "cooxm");
		  Jedis jedis= new Jedis("120.24.81.226", 6379,5000);
		  jedis.select(9);
		  
		  /*Date date=new Date();
		  int ctrolID=40008;
		  Device dev=new Device(ctrolID , "电视",1234567891 , "XJFGOD847X" ,      541 ,    0 ,  2,  101 ,    1, 0,date,date);
		  int count=dev.saveToDB(mysql);
		  dev.saveToDB(mysql);		  
		  Device d= getOneDeviceFromDB(mysql, 40008, 1234567891);
		  


		  jedis.hset(LogicControl.roomBind+ctrolID, 1234567891+"", dev.toJsonObj().toString());
		  System.out.println("Query OK");*/
		  
		  
			String x="{\"deviceID\":1,\"deviceSN\":\"abc\",\"deviceName\":\"fdfe\",\"deviceType\":\"541\",\"type\":\"1\",\"state\":\"1\",\"relatedDevType\":\"20\",\"wall\":\"1\",\"modifyTime\":\"2015-06-30 17:56:28\",\"createTime\":\"2015-06-30 17:56:28\",\"ctrolID\":40004,\"roomID\":3000,\"roomName\":\"客厅\",\"roomType\":2}";
	        JSONObject j=null;
			try {
				j = new JSONObject(x);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Device d=null;
			try {
				d = new Device(j);
			} catch (JSONException | ParseException e) {
				e.printStackTrace();
			}
			System.out.println(d.getRoomID());
			//d.saveToDB(mysql);
			Device c = getOneDeviceFromDB(mysql, 10003, 1);
		  
		  

		  
		

	  }
	

}
