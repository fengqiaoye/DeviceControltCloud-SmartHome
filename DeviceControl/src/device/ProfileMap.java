/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */
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
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午4:48:54 
 */





import util.MySqlClass;

/**
 * @author Chen Guanghua (richard@cooxm.com)
 *
 */
public class ProfileMap {
	
	/***Map<CtrolID+profileID,Profile>*/
	static Map<String, Profile> profileMap=new HashMap<String, Profile>();  
	//static final String  profileIndexTable="info_user_room_st";
	
	
	ProfileMap(){}
	ProfileMap(Map<String, Profile> profileMap){
		ProfileMap.profileMap=profileMap;	
	}
	
   /*** 
   * 从入MYSQL读取情景模式列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
    */
	ProfileMap(MySqlClass mysql) throws SQLException	
	{   Profile profile= null;//new Profile();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+" userroomstid       ,"
		+"userroomstname,"
		+"ctr_id        ,"
		+"roomid        ,"
		+"roomtype      ,"
		+"sttemplateid  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+Profile.profileIndexTable
		//+" where ctr_id="+CtrolID
		//+" and userroomstid="+profileID
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
			profile =new Profile();
			String[] index=line.split(",");
			profile.profileID=Integer.parseInt(index[0]);
			profile.profileName=index[1];	
			profile.CtrolID=Integer.parseInt(index[2]);	
			profile.roomID=Integer.parseInt(index[3]);	
			profile.roomType=Integer.parseInt(index[4]);	
			profile.profileTemplateID=Integer.parseInt(index[5]); 
			try {
				profile.createTime=sdf.parse(index[6]);
				profile.modifyTime=sdf.parse(index[7]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			profile.factorList=Profile.getProFactorsFromDB(mysql, profile.CtrolID, profile.profileID);
			if(!profile.isEmpty())
			ProfileMap.profileMap.put(profile.CtrolID+"_"+profile.profileID, profile);		
		}	
	}

	/*** 获取一个家庭所有情景模式
	 * @param: roomID	 * 
	 * */
	public List<Profile> getProfilesByCtrolID(int CtrolID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : profileMap.entrySet()) {
			if(entry.getKey().split("_")[0]==CtrolID+""){
				profileList.add(entry.getValue());
			}			
		}
		return profileList;
	}


	/*** 获取一个房间所有情景模式
	 * @param: roomID
	 * @param: CtrolID 
	 * */
	public List<Profile> getProfilesByRoomID(int roomID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : profileMap.entrySet()) {
			if(entry.getValue().roomID==roomID){
				profileList.add(entry.getValue());
			}			
		}
		return profileList;
	}
	


	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		ProfileMap pm=new ProfileMap(mysql);
		System.out.println(ProfileMap.profileMap.size());
	}
	
	

}
