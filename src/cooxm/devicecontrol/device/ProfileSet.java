package cooxm.devicecontrol.device;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午2:48:17 
 */
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.util.MySqlClass;
import cooxm.devicecontrol.util.JedisUtil;


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
	 
	public ProfileSet (JSONObject profileSetJson) throws JSONException, ParseException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.ctrolID=profileSetJson.getInt("ctrolID");
		this.profileSetID=profileSetJson.getInt("profileSetID");
		this.profileSetName=profileSetJson.getString("profileSetName");
		this.profileSetTemplateID=profileSetJson.getInt("profileSetTemplateID");
		
		JSONArray profileArray= profileSetJson.getJSONArray("profileArray");
		List<Integer> profileList = new ArrayList<Integer>() ;
		for(int i=0;i<profileArray.length();i++){
			//2015-06-01 和李鹏协商更改
			//JSONObject profileJson=profileListJSON.getJSONObject(i);
			//Integer profileID= profileJson.getInt("profileID");
			//Profile profile=new Profile(profileJson);
			//LogicControl.profileMap.put(this.ctrolID+"_"+profileID, profile);
			Integer profileID=profileArray.getInt(i);
			profileList.add(profileID);		
		}		
		this.profileList=profileList;
		this.createTime=sdf.parse(profileSetJson.getString("createTime"));
		this.modifyTime=sdf.parse(profileSetJson.getString("createTime"));	

	}
	
	public static JSONObject toJson(List<Profile> profileList){
	
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileSetJson = new JSONObject(); 
	    JSONArray ja=new JSONArray();
        for (Iterator iterator = profileList.iterator(); iterator.hasNext();) {
	    	    try {
				Profile profile = (Profile) iterator.next();
			    profileSetJson.put("ctrolID",        profile.getCtrolID()       );
				profileSetJson.put("profileSetID",         profile.getProfileTemplateID()      );
			    profileSetJson.put("profileSetName",        profile.getProfileName()      );
			    profileSetJson.put("profileSetTemplateID",       profile.getProfileTemplateID()     );
	
		    	ja.put(profile.getProfileID());
	
	
			    profileSetJson.put("profileArray", ja);
			    profileSetJson.put("createTime",sdf.format(profile.getCreateTime()));
			    profileSetJson.put("modifyTime",sdf.format(profile.getModifyTime()));
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
	    
	    return profileSetJson;
	}
	 
	public JSONObject toJsonObj(){
	
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileSetJson = new JSONObject(); 
        //JSONObject profileJson ; 
	    try {
		    profileSetJson.put("ctrolID",        this.ctrolID       );
			profileSetJson.put("profileSetID",         this.profileSetID      );
		    profileSetJson.put("profileSetName",        this.profileSetName      );
		    profileSetJson.put("profileSetTemplateID",      this.profileSetTemplateID        );
		    JSONArray ja=new JSONArray();
		    for(Integer profileID: this.profileList){
		    	//Profile profile=Profile.getFromDBByProfileID(mysql, ctrolID, profileID);//从数据库获取
		    	//profileSetJson.accumulate("profileArray", profile.toJsonObj());
		    	
		    	// 2015-06-01 和李鹏商定，不再包含情景细节；
		    	/*Profile profile=LogicControl.profileMap.get(ctrolID+"_"+profileID);
		    	ja.put(profile.toJsonObj());*/
		    	ja.put(profileID);

		    }
		    profileSetJson.put("profileArray", ja);
		    profileSetJson.put("createTime",sdf.format(this.createTime));
		    profileSetJson.put("modifyTime",sdf.format(this.createTime));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return profileSetJson;
	}
	 
	 
	public ProfileSet generateProfileSet(List<Integer> profileList){
		ProfileSet pc=new  ProfileSet();
		 pc.profileSetID=(int)Math.random()*65530+4;
		 pc.profileList=profileList;
		 Date now =new Date();
		 pc.createTime=now;
		 pc.modifyTime=now;			 
		 return pc;
	 }
	
	 public boolean isEmpty() {
		if(this.profileList==null||this.profileList.size()  ==0 ||this.createTime==null ||this.modifyTime==null){			
			return true;
		}		
		return false;		
	}
	 
	 
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException 
	 * */
	public int saveProfileSetToDB(MySqlClass mysql) {
		if(this.isEmpty()){
			//System.out.println("ERROR:object is empty,can't save to mysql"+this.ctrolID+",profileID="+this.profileSetID);
			//return -1;
			this.profileList.add(-1);  //因为info_user_st_set表中userstid是主键，不能为空
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int resultCount=0;
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		deleteProfileSetFromDB(mysql, this.ctrolID, this.profileSetID); //先清空原有数据
		
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
		//System.out.println(sql);		
		resultCount+=mysql.query(sql);
	
	}
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				mysql.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}	
		
		return resultCount;	
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	/*public	static ProfileSet getProfileSetFromDB(MySqlClass mysql,int ctrolID,int profileSetID) 
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			//System.out.println("query:"+sql);
			String res=mysql.select(sql);
			//System.out.println("get from mysql:\n"+res);
			if(res==null||res.length()==0) {
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
				if(profileID!=-1){  //因为info_user_st_set表中 userstid=-1是人为添加，不是真正的profile；
				    profileIDList.add(profileID);
				}
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

			return profileSet;
		}*/
	
	
     //  --------------------------------2015-07-28  profileSet表不再存在，从profile表中读取  -----------------------
	   /*** 
	   * 根据 模板ID 来查找 情景集
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	 * @throws ParseException 
	   */
	/*public	static ProfileSet getProfileSetByTemplateID(MySqlClass mysql,int ctrolID,int templateID) throws SQLException
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
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res);
		if(res==null||res.length()==0) {
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
			if(profileID!=-1){  //因为info_user_st_set表中 userstid=-1是人为添加，不是真正的profile；
				profileIDList.add(profileID);
			}
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
	}*/
	
    //  -------------------------------- profileSet表不再存在，从profile表中读取  -----------------------
	public	static ProfileSet getProfileSetByTemplateID(MySqlClass mysql,int ctrolID,int templateID) throws SQLException, ParseException
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);
		String sql="select "
				+" userroomstid       ,"
				+"userroomstname,"
				+"ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"sttemplateid  ,"
				+"stsetid  ,"			
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+Profile.profileIndexTable
				+" where ctr_id="+ctrolID
				+" and sttemplateid="+templateID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res);
		if(res==null||res.length()==0) {
			System.err.println("ERROR:query result is empty: "+sql);
			return null;
		}
		String[] resArray=res.split("\n");
		ProfileSet profileSet=new ProfileSet();
		List<Integer> profileIDList=new ArrayList<Integer>();
		Integer profileID=null;
		String[] cells=null;
		Date modifyTime=new Date(0);
		for(String line:resArray){
			cells=line.split(",");
			profileID=new Integer(Integer.parseInt(cells[0]));	
			if(profileID!=-1){  //因为info_user_st_set表中 userstid=-1是人为添加，不是真正的profile；
				profileIDList.add(profileID);
			}
			modifyTime=sdf.parse(cells[8]).after(modifyTime)?sdf.parse(cells[8]):modifyTime;
		}
		profileSet.profileList=profileIDList;
		
		profileSet.ctrolID=Integer.parseInt(cells[2]);
		profileSet.profileSetID=Integer.parseInt(cells[5]);  //就是模板ID
		profileSet.profileSetName=cells[1];
		profileSet.profileSetTemplateID=Integer.parseInt(cells[5]);
		try {
			profileSet.createTime=sdf.parse(cells[7]);
			profileSet.modifyTime=modifyTime;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mysql.conn.commit();
		return profileSet;
	}
	
  /*** 
   * 从入MYSQL删除一个profileSet
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static int deleteProfileSetFromDB(MySqlClass mysql,int ctrolID,int profileSetID) 
	{
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql="delete   "
				+ "  from  "				
				+profileSetTable
				+" where ctr_id="+ctrolID
				+" and userstsetid="+profileSetID
				+ ";";
		//System.out.println("query:"+sql);
		int res=mysql.query(sql);
		if(res<=0) {
			//System.out.println("ERROR:query result is empty: "+sql);
			return 0;
		}				
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	  /*** 
	   * 从入MYSQL删除profileSet说包含的一个情景ID
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	   */
		public static int deleteProfileSetDetail(MySqlClass mysql,int ctrolID,int profileSetID,int profileID) 
		{
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String sql="delete   "
					+ "  from  "				
					+profileSetTable
					+" where ctr_id="+ctrolID
					+" and userstsetid="+profileSetID
					+" and userstid=" +profileID
					+ ";";
			//System.out.println("query:"+sql);
			int res=mysql.query(sql);
			if(res<=0) {
				System.out.println("ERROR:query result is empty: "+sql);
				return 0;
			}				
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return res;
		}
		
	public static void deleteProfileSetByRoomIDFromRedis(JedisUtil jedis,int ctrolID,int roomID) throws JSONException, ParseException{
		Map<String, String> profileSetMap = jedis.hgetAll(LogicControl.currentProfileSet+ctrolID);
		for (Map.Entry<String, String> entry:profileSetMap.entrySet()) {
			ProfileSet p=new ProfileSet(new JSONObject(entry.getValue()));
			for (int i = 0; i < p.getProfileList().size(); i++) {
				int profileID=p.getProfileList().get(i);
				Profile profile=LogicControl.profileMap.get(ctrolID+"_"+profileID);
				if(profile!=null && profile.getRoomID()==roomID){
					p.getProfileList().remove((Object)profileID);
					jedis.hset(LogicControl.currentProfileSet+ctrolID, p.getProfileSetID()+"", p.toJsonObj().toString());
				}				
			}
			
		}
		
	}
		
			
	public static void main(String[] args) throws SQLException, ParseException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("120.24.81.226","3306","cooxm_device_control", "cooxm", "cooxm");
		ProfileSet p =new ProfileSet();
		p=ProfileSet.getProfileSetByTemplateID(mysql, 10002, 2);
		//p=ProfileSet.getProfileSetFromDB(mysql, 12345677, 12345);
		//p.profileSetID++;
		
		//p.saveProfileSetToDB(mysql);
	}


}
