package cooxm.devicecontrol.device;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.MySqlClass;

public class ProfileSetMap extends HashMap<String, ProfileSet> {
	static Logger log= Logger.getLogger(ProfileSetMap.class);
	private static final long serialVersionUID = 1L;
	///***Map<ctrolID_profileID,Profile>*/
	//static Map<String, ProfileSet> profileSetMap=new HashMap<String, ProfileSet>();  
	//static final String  profileIndexTable="info_user_room_st";
	MySqlClass mysql;
	
	public ProfileSetMap(){}
	public ProfileSetMap(Map<String, ProfileSet> profileSetMap){
		super(profileSetMap);	
	}
	
	public ProfileSetMap(MySqlClass mysql) throws SQLException{
		super(getProfileSetMapFromDB(mysql));
	}

   /*** 
   * 从入MYSQL读取情景模式集列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_set
   * @throws SQLException 
    */
	public static HashMap<String, ProfileSet> getProfileSetMapFromDB(MySqlClass mysql) throws SQLException	
	{   
		log.info("Start to initialize profileSetMap....");
		HashMap<String, ProfileSet> profileSetMap=new HashMap<String, ProfileSet>();
		ProfileSet profileSet=null; 		
	    List<Integer> profileIDList=new ArrayList<Integer>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select "
				+" ctr_id       ," 
				+" userstsetid       ," 
				+"stsetname        ,"
				+"settemplateid        ,"
				+"userstid      ,"
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+ProfileSet.profileSetTable
				+ ";";
		//System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		//System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return  null;
		} 
		Integer profileID=null;
		String[] records=res2.split("\n");
		for(String line:records){			
			profileSet =new ProfileSet();
			String[] cells=line.split(",");			
			profileSet.ctrolID=Integer.parseInt(cells[0]);
			profileSet.profileSetID=Integer.parseInt(cells[1]);
			profileSet.profileSetName=cells[2];
			profileSet.profileSetTemplateID=Integer.parseInt(cells[3]);
			profileID=new Integer(Integer.parseInt(cells[4]));	
			try {
				profileSet.createTime=sdf.parse(cells[5]);
				profileSet.modifyTime=sdf.parse(cells[6]);
			} catch (ParseException e) {
				e.printStackTrace();
			}				
			profileIDList.add(profileID);
		}
		profileSet.profileList=profileIDList;
		
		if(!profileSet.isEmpty())
		profileSetMap.put(profileSet.ctrolID+"_"+profileSet.profileSetID, profileSet);
		log.info("Initialize profileSetMap finished !");
		return profileSetMap;
	}

	/*** 获取一个家庭所有情景模式
	 * @param: roomID	 * 
	 * */
	public List<ProfileSet> getProfileSetsByctrolID(int ctrolID){	
		List<ProfileSet> profileList=new ArrayList<ProfileSet>();
		for (Entry<String, ProfileSet> entry : this.entrySet()) {
			if(entry.getKey().split("_")[0]==ctrolID+""){
				profileList.add(entry.getValue());
			}			
		}
		return profileList;
	}

	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public ProfileSet put(String key,ProfileSet profileSet) {
		if(null==this.mysql)
			return null;
		int x=profileSet.saveProfileSetToDB(this.mysql)	;
		if(x>0){
			 super.put(key, profileSet);
			 return profileSet;
		}else{
		   return null;	
		}
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	@Override
	public ProfileSet remove(Object ctrolID_profileSetID) {
		ProfileSet profileSet=this.get(ctrolID_profileSetID);
		ProfileSet.deleteProfileSetFromDB(mysql, profileSet.ctrolID, profileSet.profileSetID);
		return super.remove(ctrolID_profileSetID);
		//return profileSet;
	}



	public static void main(String[] args) throws SQLException {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		ProfileSetMap pm=new ProfileSetMap(mysql);
		System.out.println(pm.size());
	}
	
	

}

