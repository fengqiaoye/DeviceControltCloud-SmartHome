package device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created��2014��12��15�� ����4:32:50 
 */

public class RoomMap {
	
	/*** Map<ctrolID_RoomID,Room>*/
	static Map<String, Room> roomMap=new HashMap<String, Room>(); 


	
	public List<Room> getRoomsByCtrolID(int CtrolID){
		List<Room> roomList= new ArrayList<Room>();
		for (Entry<String, Room> entry : roomMap.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==CtrolID){
				roomList.add(entry.getValue());
			}			
		}
		return roomList;
	}
	
	   /*** 
	   * ����MYSQL��ȡ�����б�
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	    */
		RoomMap(MySqlClass mysql) throws SQLException	
		{   Room room= null;//new Room();
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sql2="select  "
			+"ctr_id        ,"
			+"roomid        ,"
			+"roomtype      ,"
			+"description  ,"
			+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
			+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
			+ "  from "				
			+Room.roomIndexTable
			+ ";";
			System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				System.out.println("ERROR:empty query by : "+sql2);
				return ;
			} 
			String[] records=res2.split("\n");
			for(String line:records){			
				room =new Room();
				String[] index=line.split(",");
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
				}			
				RoomMap.roomMap.put(room.CtrolID+"_"+room.roomID, room);		
			}	
		}
		
	
}