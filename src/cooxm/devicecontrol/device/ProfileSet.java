﻿package cooxm.devicecontrol.device;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午2:48:17 
 */
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.util.MySqlClass;


/***情景模式集：
 * <p>涉及到多个房间的情景模式，这里只列出多个房间的情景模式ID，具体情景模式定义要根据ID去情景模式里查找</p>*/
public class ProfileSet {
	int ctrolID;
	int profileSetID;
	String profileSetName;
	/***<br> 全家模式的模板ID */
	int profileSetTemplateID;
	
	/*** 所包含的情景模式ID 列表*/
	List<Integer> profileList;	
	Date createTime;
	Date modifyTime;
	//ProfileSet currProfileSet;
	static final String  profileSetTable="info_user_st_set";	
	
	public int getCtrolID() {
		return ctrolID;
	}

	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}

	public int getProfileSetID() {
		return profileSetID;
	}

	public void setProfileSetID(int profileSetID) {
		this.profileSetID = profileSetID;
	}

	public String getProfileSetName() {
		return profileSetName;
	}

	public void setProfileSetName(String profileSetName) {
		this.profileSetName = profileSetName;
	}

	public int getProfileSetTemplateID() {
		return profileSetTemplateID;
	}

	public void setProfileSetTemplateID(int profileSetTemplateID) {
		this.profileSetTemplateID = profileSetTemplateID;
	}

	public List<Integer> getProfileList() {
		return profileList;
	}

	public void setProfileList(List<Integer> profileList) {
		this.profileList = profileList;
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



	ProfileSet(){}
	
	 ProfileSet(ProfileSet pc){
		 this.ctrolID=pc.ctrolID;
		 this.profileSetID=pc.profileSetID;
		 this.profileSetName=pc.profileSetName;
		 this.profileSetTemplateID=pc.profileSetTemplateID;
		 this.profileList=pc.profileList;
		 this.createTime=pc.createTime;
		 this.modifyTime=pc.modifyTime;		 
	 }
	 
	public ProfileSet (JSONObject profileSetJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.ctrolID=profileSetJson.getInt("ctrolID");
			this.profileSetID=profileSetJson.getInt("profileSetID");
			this.profileSetName=profileSetJson.getString("profileSetName");
			this.profileSetTemplateID=profileSetJson.getInt("profileSetTemplateID");
			
			JSONArray profileListJSON= profileSetJson.getJSONArray("profileList");
			List<Integer> profileList = new ArrayList<Integer>() ;
			for(int i=0;i<profileListJSON.length();i++){
				JSONObject profileJson=profileListJSON.getJSONObject(i);
				Integer profileID= profileJson.getInt("profileID");	
				profileList.add(profileID);		
				
				Profile profile=new Profile(profileJson);
				LogicControl.profileMap.put(this.ctrolID+"_"+profileID, profile);
			}		
			this.profileList=profileList;
			this.createTime=sdf.parse(profileSetJson.getString("createTime"));
			this.modifyTime=sdf.parse(profileSetJson.getString("createTime"));	
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public JSONObject toJsonObj(){
//		Configure cf=MainEntry.getConfig();
//		String mysql_ip			=cf.getValue("mysql_ip");
//		String mysql_port		=cf.getValue("mysql_port");
//		String mysql_user		=cf.getValue("mysql_user");
//		String mysql_password	=cf.getValue("mysql_password");
//		String mysql_database	=cf.getValue("mysql_database");	
//		MySqlClass mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileSetJson = new JSONObject(); 
        //JSONObject profileJson ; 
	    try {
		    profileSetJson.put("deviceSN",        this.ctrolID       );
			profileSetJson.put("profileSetID",         this.profileSetID      );
		    profileSetJson.put("profileSetName",        this.profileSetName      );
		    profileSetJson.put("profileSetTemplateID",      this.profileSetTemplateID        );
		    JSONArray ja=new JSONArray();
		    for(Integer profileID: this.profileList){
		    	//Profile profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID);//从数据库获取
		    	Profile profile=LogicControl.profileMap.get(ctrolID+"_"+profileID);
		    	ja.put(profile.toJsonObj());
		    	//profileSetJson.accumulate("profileArray", profile.toJsonObj());
		    }
		    profileSetJson.accumulate("profileArray", ja);
		    profileSetJson.put("createTime",sdf.format(this.createTime));
		    profileSetJson.put("modifyTime",sdf.format(this.createTime));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return profileSetJson;
	}
	 
	 
	 ProfileSet generateProfileSet(List<Integer> profileList){
		ProfileSet pc=new  ProfileSet();
		 pc.profileSetID=(int)Math.random()*65530+4;
		 pc.profileList=profileList;
		 Date now =new Date();
		 pc.createTime=now;
		 pc.modifyTime=now;			 
		 return pc;
	 }
	
	 public boolean isEmpty() {
		if(this.profileList==null||this.createTime==null ||this.modifyTime==null){			
			return true;
		}		
		return false;		
	}
	 
	 
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException 
	 * */
	public int saveProfileSetToDB(MySqlClass mysql) throws SQLException{
		if(this.isEmpty()){
			System.out.println("ERROR:object is empty,can't save to mysql");
			return -1;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int resultCount=0;
		mysql.conn.setAutoCommit(false);		
		for (Integer profileID:this.profileList) {
		String sql="replace into "+profileSetTable
				+" (ctr_id       ," 
				+" userstsetid       ," 
				+"stsetname        ,"
				+"settemplateid        ,"
				+"userstid      ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+","	
				+this.profileSetID+",'"	
				+this.profileSetName+"',"
				+this.profileSetTemplateID+","
				+profileID+",'"
				+sdf.format(this.createTime)+"','"
				+sdf.format(this.modifyTime)
				+"')";
		System.out.println(sql);		
		resultCount+=mysql.query(sql);
		}
		mysql.conn.commit();		
		
		return resultCount;	
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static ProfileSet getProfileSetFromDB(MySqlClass mysql,int ctrolID,int profileSetID) throws SQLException
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
			String sql="select "
					+" ctr_id       ," 
					+" userstsetid       ," 
					+"stsetname        ,"
					+"settemplateid        ,"
					+"userstid      ,"
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileSetTable
					+" where ctr_id="+ctrolID
					+" and userstsetid="+profileSetID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==""||res.length()==0) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			ProfileSet profileSet=new ProfileSet();
			List<Integer> profileIDList=new ArrayList<Integer>();
			Integer profileID=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				profileID=new Integer(Integer.parseInt(cells[4]));				
				profileIDList.add(profileID);
			}
			profileSet.profileList=profileIDList;
			
			profileSet.ctrolID=Integer.parseInt(cells[0]);
			profileSet.profileSetID=Integer.parseInt(cells[1]);
			profileSet.profileSetName=cells[2];
			profileSet.profileSetTemplateID=Integer.parseInt(cells[3]);
			try {
				profileSet.createTime=sdf.parse(cells[5]);
				profileSet.modifyTime=sdf.parse(cells[6]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mysql.conn.commit();
			return profileSet;
		}
	
	   /*** 
	   * 根据 模板ID 来查找 情景集
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	   */
	public	static ProfileSet getProfileSetByTemplateID(MySqlClass mysql,int ctrolID,int templateID) throws SQLException
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);
		String sql="select "
				+" ctr_id       ," 
				+" userstsetid       ," 
				+"stsetname        ,"
				+"settemplateid        ,"
				+"userstid      ,"
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+profileSetTable
				+" where ctr_id="+ctrolID
				+" and settemplateid="+templateID
				+ ";";
		System.out.println("query:"+sql);
		String res=mysql.select(sql);
		System.out.println("get from mysql:\n"+res);
		if(res==""||res.length()==0) {
			System.err.println("ERROR:query result is empty: "+sql);
			return null;
		}
		String[] resArray=res.split("\n");
		ProfileSet profileSet=new ProfileSet();
		List<Integer> profileIDList=new ArrayList<Integer>();
		Integer profileID=null;
		String[] cells=null;
		for(String line:resArray){
			cells=line.split(",");
			profileID=new Integer(Integer.parseInt(cells[4]));				
			profileIDList.add(profileID);
		}
		profileSet.profileList=profileIDList;
		
		profileSet.ctrolID=Integer.parseInt(cells[0]);
		profileSet.profileSetID=Integer.parseInt(cells[1]);
		profileSet.profileSetName=cells[2];
		profileSet.profileSetTemplateID=Integer.parseInt(cells[3]);
		try {
			profileSet.createTime=sdf.parse(cells[5]);
			profileSet.modifyTime=sdf.parse(cells[6]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysql.conn.commit();
		return profileSet;
	}
	
  /*** 
   * 从入MYSQL删除一个profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static int deleteProfileSetFromDB(MySqlClass mysql,int ctrolID,int profileSetID) throws SQLException
	{
		mysql.conn.setAutoCommit(false);
		String sql="delte *  "
				+ "  from  "				
				+profileSetTable
				+" where ctr_id="+ctrolID
				+" and userstsetid="+profileSetID
				+ ";";
		System.out.println("query:"+sql);
		int res=mysql.query(sql);
		if(res<=0) {
			System.out.println("ERROR:query result is empty: "+sql);
			return 0;
		}				
		mysql.conn.commit();
		return 1;
	}
		
			
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		ProfileSet p =new ProfileSet();
		p=ProfileSet.getProfileSetFromDB(mysql, 12345677, 12345);
		p.profileSetID++;
		
		try {
			p.saveProfileSetToDB(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
