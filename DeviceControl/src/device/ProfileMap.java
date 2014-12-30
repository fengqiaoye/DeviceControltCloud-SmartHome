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








import org.apache.log4j.Logger;

import util.BytesUtil;
import util.MySqlClass;

/**
 * <pre>Map < CtrolID+profileID,Profile >
 * @key CtrolID+profileID字符串
 * @value 对应的情景模式
 */
public class ProfileMap extends HashMap<String, Profile>{

	private static final long serialVersionUID = 1L;

	/***Map<CtrolID+profileID,Profile>*/
	//public static Map<String, Profile> profileMap=new HashMap<String, Profile>();  
	//static final String  profileIndexTable="info_user_room_st";
	MySqlClass mysql;
	
	
	ProfileMap(){}
	ProfileMap(Map<String, Profile> profileMap){
		super(profileMap);		
	}
	
	public ProfileMap(MySqlClass mysql) throws SQLException{
		super(getProfileMapFromDB(mysql));
		this.mysql=mysql;
	}
	
   /*** 
   * 从入MYSQL读取情景模式列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
    */
	public static HashMap<String, Profile> getProfileMapFromDB(MySqlClass mysql) throws SQLException		
	{   
		HashMap<String, Profile> profileMap=new HashMap<String, Profile>();
		Profile profile= null;//new Profile();
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
			return null;
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
			profileMap.put(profile.CtrolID+"_"+profile.profileID, profile);		
		}
		return profileMap;		
	}
	

	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	public Profile put(String key,Profile profile) {
		if(null==this.mysql)
			return null;
		try {
			profile.saveProfileToDB(this.mysql)	;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		this.put(key, profile);
		return profile;		
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	public Profile remove(String key,Profile profile) {
		if(null==this.mysql)
			return null;
		try {
			Profile.deleteProfileFromDB(mysql, profile.CtrolID, profile.profileID);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		this.remove(key);
		return profile;
	}
	

	/*** 获取一个家庭所有情景模式
	 * @param CtrolID
	 * @return  List < Profile > 情景模式列表	 * 
	 * */
	public List<Profile> getProfilesByCtrolID(int CtrolID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : this.entrySet()) {
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
		for (Entry<String, Profile> entry : this.entrySet()) {
			if(entry.getValue().roomID==roomID){
				profileList.add(entry.getValue());
			}			
		}
		return profileList;
	}
	


	public static void main(String[] args) throws SQLException {
		
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		ProfileMap pm=new ProfileMap(mysql);
		System.out.println(pm.size());

		
//		ProfileMap pm=new ProfileMap();
//		Profile p =new Profile();
//		pm.put("test", p);
//		ProfileMap pm2=new ProfileMap(pm);
//		System.out.println(pm2.keySet().toString());
	}
	
	

}
