/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：2014年12月15日 下午4:48:54 
 */
package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.MySqlClass;

/**
 * <pre>Map < ctrolID+profileID,Profile >
 * @key ctrolID+profileID字符串
 * @value 对应的情景模式
 */
public class ProfileMap extends HashMap<String, Profile>{

	private static final long serialVersionUID = 1L;
	static Logger log= Logger.getLogger(ProfileMap.class);
	/***Map<ctrolID_profileID,Profile>*/
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
	private static HashMap<String, Profile> getProfileMapFromDB(MySqlClass mysql) throws SQLException		
	{   
		log.info("Start to initialize profileMap....");
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
		//+" where ctr_id="+ctrolID
		//+" and userroomstid="+profileID
		+ ";";
		//System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		//System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} 
		String[] records=res2.split("\n");
		for(String line:records){			
			profile =new Profile();
			String[] index=line.split(",");
			profile.setProfileID(Integer.parseInt(index[0]));
			profile.setProfileName(index[1]);	
			profile.setCtrolID(Integer.parseInt(index[2]) );	
			//profile.setRoomID(Integer.parseInt(index[3]));	
			//profile.setRoomType(Integer.parseInt(index[4]));	
			int roomID=Integer.parseInt(index[3]);
			int roomType=Integer.parseInt(index[4]);
			profile.setProfileTemplateID(Integer.parseInt(index[5])); 
			try {
				profile.setCreateTime(sdf.parse(index[6]));
				profile.setModifyTime(sdf.parse(index[7]));
			} catch (ParseException e) {
				e.printStackTrace();
			}			
			profile.setFactorList(Profile.getProFactorsFromDB(mysql, profile.getCtrolID(), profile.getProfileID(),roomID,roomType ));
			if(!profile.isEmpty())
			profileMap.put(profile.getCtrolID()+"_"+profile.getProfileID(), profile);		
		}
		log.info("Initialize profileMap finished !");
		return profileMap;		
	}
	

	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public Profile put(String key,Profile profile) {
		if(null==this.mysql)
			return null;
		int x=profile.saveToDB(this.mysql)	;
		if(x>0){
			super.put(key, profile);
			return profile;
		}else{
		   return null;	
		}	
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	@Override
	public Profile remove(Object key) {
		Profile profile = super.get(key);
		Profile.deleteFromDB(mysql, profile.getCtrolID(), profile.getProfileID());
		return super.remove(key);
		//return profile;
	}
	

	/*** 获取一个家庭所有情景模式
	 * @param ctrolID
	 * @return  List < Profile > 情景模式列表	 * 
	 * */
	public List<Profile> getProfilesByctrolID(int ctrolID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : this.entrySet()) {
			String ctrolID2=entry.getKey().split("_")[0];
			if(ctrolID2.equals(ctrolID+"")){
				profileList.add(entry.getValue());
			}			
		}
		return profileList;
	}


	/*** 获取一个房间所有情景模式
	 * @param: roomID
	 * @param: ctrolID 
	 * */
	public List<Profile> getProfilesByRoomID(int ctrolID,int roomID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : this.entrySet()) {
			Profile profile=entry.getValue();
			if(profile.getCtrolID()==ctrolID){
				for (Factor factor : profile.getFactorList()) {
					if(factor.getRoomID()==roomID){
						profileList.add(profile);
						break;
					}				
				}
			}else {
				continue;
			}
		
		}
		return profileList;
	}
	
	/*** 获取一个房间一个情景模式
	 * @param: roomID
	 * @param: ctrolID 
	 * */
	public List<Profile> getProfileByRoomIDTemplateID(int ctrolID,int roomID,int templateID){	
		List<Profile> profileList=new ArrayList<Profile>();
		for (Entry<String, Profile> entry : this.entrySet()) {
			Profile profile=entry.getValue();
			if(profile.getCtrolID()==ctrolID){
				for (Factor factor : profile.getFactorList()) {
					if(factor.getRoomID()==roomID){
						profileList.add(profile);
						break;
					}				
				}
			}else {
				continue;
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
