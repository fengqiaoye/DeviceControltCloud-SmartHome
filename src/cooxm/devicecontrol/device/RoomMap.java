package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午4:32:50 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.MySqlClass;
import redis.clients.jedis.Jedis;


/*** Map< ctrolID_RoomID,Room >*/
public class RoomMap  extends HashMap<String, Room>{
	static Logger log= Logger.getLogger(RoomMap.class);
	private static final long serialVersionUID = 1L;
	MySqlClass mysql;
	Jedis jedis;

	RoomMap(){}
	RoomMap(Map<String, Room> roomMap){
		super(roomMap);		
	}
	
	public RoomMap(MySqlClass mysql,Jedis jedis) throws SQLException{
		super(getRoomMapFromDB(mysql));
		this.mysql=mysql;
		this.jedis=jedis;
		this.jedis.select(9);
	}
	
	public List<Room> getRoomsByctrolID(int ctrolID){
		List<Room> roomList= new ArrayList<Room>();
		for (Entry<String, Room> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==ctrolID){
				roomList.add(entry.getValue());
			}			
		}
		return roomList;
	}
	
   /*** 
   * 从入MYSQL读取房间列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static HashMap<String, Room> getRoomMapFromDB(MySqlClass mysql) throws SQLException	
	{   log.info("Start to initialize roomMap....");
	    HashMap<String, Room> roomMap=new HashMap<String, Room>(); 
	    Room room= null;//new Room();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"ctr_id        ,"
		+"roomid        ,"
		+"roomtype   ,"
		+"roomname,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+Room.roomIndexTable
		+ ";";
//		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
//		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} 
		String[] records=res2.split("\n");
		for(String line:records){			
			room =new Room();
			String[] index=line.split(",");
			room.ctrolID=Integer.parseInt(index[0]);	
			room.roomID=Integer.parseInt(index[1]);
			room.roomType=Integer.parseInt(index[2]);	
			room.roomName=index[3]; 
			try {
				room.createTime=sdf.parse(index[4]);
				room.setModifyTime(sdf.parse(index[5]));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			roomMap.put(room.ctrolID+"_"+room.roomID, room);		
		}	
		log.info("Initialize deviceMap finished !");
		return roomMap;
	}
	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public Room put(String key,Room room) {
		if(null==this.mysql)
			return null;
		int x=0;
		try {
		  x=	room.saveRoomToDB(this.mysql)	;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(x>0){
			 super.put(key, room);
			 return room;
		}else{
		   return null;	
		}		
	}	
	
	/**
	 *<pre>重写父类的方法，当向这个map删除一个房间时，自动把这个房间从数据库删除;
	 * 同时删除该房间的情景模式、情景模式集、设备。
	 *  */
	@Override
	public Room remove(Object key) {
		if(null==this.mysql)
			return null;
		Room room =super.get(key);
		if(room!=null){
			Room.deleteRoomFromDB(mysql, room.ctrolID, room.roomID);
			
			//同时删除房间的情景模式，家电列表
			try {
				//ProfileSet.deleteProfileSetByRoomIDFromRedis(this.jedis, room.ctrolID, room.roomID);//删Redis
				//LogicControl.profileSetMap.deleteProfileByRoomID(room.ctrolID, room.roomID);     //删数据库
				
				Device.deleteDeviceByRoomIDFromRedis(this.jedis, room.ctrolID, room.roomID);
				LogicControl.deviceMap.deleteDevicesByroomID(room.ctrolID, room.roomID);
				
				Profile.deleteFactorByDeviceIDFromRedis(this.jedis, room.ctrolID, room.roomID);
				LogicControl.profileMap.deleteProfilesByRoomID(room.ctrolID, room.roomID);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return super.remove(key);
	}
	
	public static void main (String[] args) throws SQLException{
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
		Jedis jedis=new Jedis("172.16.35.170", 6379,5000);
		jedis.select(9);
		RoomMap p = new RoomMap(mysql,jedis);
		
		List<Room> x = p.getRoomsByctrolID(40008);
		
		System.out.println(x.size());
	}
		
	
}
