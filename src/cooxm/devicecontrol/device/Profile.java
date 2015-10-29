package cooxm.devicecontrol.device;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.log.Log;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.synchronize.IRMatch2;
import cooxm.devicecontrol.util.MySqlClass;
import cooxm.devicecontrol.util.JedisUtil;


/***
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 * @function  一个房间的情景模式，可能包含多个情景因素(家电、环境等)
 * */
public class Profile  {
	
	static Logger log =Logger.getLogger(Profile.class);
	private int profileID;
	private String profileName;
	private int ctrolID;
	private int roomID;
	private int roomType;
	private int profileTemplateID;
	private int profileSetID;
	private List<Factor> factorList;
	private Date createTime;
	private Date modifyTime;
		
	public static final String  profileDetailTable="info_user_room_st_factor";
	public static final String  profileIndexTable="info_user_room_st";	

	public int getProfileTemplateID() {
		return profileTemplateID;
	}
	public void setProfileTemplateID(int profileTemplateID) {
		this.profileTemplateID = profileTemplateID;
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
	public int getProfileID() {
		return profileID;
	}
	public void setProfileID(int profileID) {
		this.profileID = profileID;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public int getProfileSetID() {
		return profileSetID;
	}
	public void setProfileSetID(int profileSetID) {
		this.profileSetID = profileSetID;
	}
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	
	public int getRoomID(){
		return this.roomID;		
	}
	
	public void setRoomID(int roomID){
		this.roomID = roomID;		
	}
	
	public int getRoomType(){
		return this.roomType	;
	}
	
	public void setRoomType(int roomType){
		this.roomType = roomType;
	}
		
	public int getProfileType(){
		return this.profileTemplateID;
	}
	

	public List<Factor> getFactorList() {
		return factorList;
	}

	public void setFactorList(List<Factor> factorList) {
		this.factorList = factorList;
	}
	public Profile (){}


	
	public Profile(ProfileTemplate ptemp,int ctrolID){
		this.profileID = ptemp.getProfileTemplateID()+100;
		this.profileName = ptemp.getProfileTemplateName();
		this.ctrolID = ctrolID;
		this.profileTemplateID = ptemp.getProfileTemplateID();
		this.profileSetID = -1;
		this.factorList=new ArrayList<Factor>();
		List<FactorTemplate> a = ptemp.getFactorTemplateTempList();
		for (int i = 0; i < a.size(); i++) {
			Factor factor1=new Factor(101, a.get(i));
			this.factorList.add(factor1);
		}
		this.createTime = ptemp.getCreateTime();
		this.modifyTime = ptemp.getModifyTime();

	}
	

	
	public Profile(int profileID, String profileName, int ctrolID,
			int profileTemplateID, int profileSetID, List<Factor> factorList,
			Date createTime, Date modifyTime) {
		this.profileID = profileID;
		this.profileName = profileName;
		this.ctrolID = ctrolID;
		this.profileTemplateID = profileTemplateID;
		this.profileSetID = profileSetID;
		this.factorList = factorList;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}
	/**
	 * 将情景模式对象 转换为一个JSONObject 存储
	 * @return JSONObject
	 * */
	public JSONObject toJsonObj(){	
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileJson = new JSONObject();  
        JSONObject factorJson ; //= new JSONObject();  
        try {
		    profileJson.put("profileID",this.profileID);
		    profileJson.put("profileName",this.profileName);
		    profileJson.put("ctrolID",this.ctrolID); 
		    profileJson.put("roomID",this.getRoomID());
		    profileJson.put("roomType",this.getRoomType());
		    profileJson.put("profileTemplateID",getProfileTemplateID());
		    profileJson.put("profileSetID",this.profileSetID);
		    JSONArray ja=new JSONArray();
		    List<Factor> factors = getFactorList();
		    if (null != factors) {
		    	for(Factor factor: factors){
			    	factorJson= new JSONObject(); 
			    	factorJson=factor.toProfileJson();
			    	ja.put(factorJson);
			    	//profileJson.accumulate("factorList",factorJson); 
			    }
			}
		    
		    profileJson.put("factorList",ja); 
		    profileJson.put("createTime",sdf.format(getCreateTime()));
		    profileJson.put("modifyTime",sdf.format(getModifyTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}  		
		return profileJson;
	}
	public Profile (JSONObject profileJson) throws JSONException, ParseException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.profileID=profileJson.getInt("profileID");
			this.profileName=profileJson.getString("profileName");
			this.ctrolID=profileJson.getInt("ctrolID");
			this.roomID=profileJson.getInt("roomID");
			this.roomType=profileJson.getInt("roomType");
			this.profileTemplateID=profileJson.getInt("profileTemplateID");
			this.profileSetID=profileJson.getInt("profileSetID");
			JSONArray factorListJSON= profileJson.getJSONArray("factorList");
			List<Factor> factorList = new ArrayList<Factor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				Factor factor= new Factor();
			  /*factor.factorID=factorJson.getInt("factorID");
				factor.minValue=factorJson.getInt("minValue");
				factor.maxValue=factorJson.getInt("maxValue");
				factor.operator=factorJson.getInt("operator");
				factor.validFlag=factorJson.getInt("validFlag");
				factor.setCreateTime(sdf.parse(factorJson.getString("createTime")) );
				factor.setModifyTime(sdf.parse(factorJson.getString("modifyTime")) );	*/
				factor=Factor.fromProfileJson(factorJson);
				factorList.add(factor);		
			}		
			setFactorList(factorList);
			setCreateTime(sdf.parse(profileJson.optString("createTime")));
			setModifyTime(sdf.parse(profileJson.optString("modifyTime")));	

	}
	

	public Factor getFactor(int factorID){	
		List<Factor> factorList=getFactorList();
		for (int i = 0; i < factorList.size(); i++) {
			if(factorList.get(i).getFactorID()==factorID){
				return factorList.get(i);
			}			
		}
		return null;
	}
	
	/**情景列表为空，返回ture； */
	public boolean isEmpty(){
		if(this.factorList==null||this.factorList.size()==0 ||getCreateTime()==null){			
			return true;
		}		
		return false;		
	}
	
	public boolean isExistInDB(MySqlClass mysql){
		
		return false;
	}

	
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "XXXX");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException
	 * @returns 0 :profile为空；
	 * 			1   ：保存成功
	 * */
	public int saveToDB(MySqlClass mysql){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Factor> factorList=getFactorList();
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql0="delete from "+profileDetailTable+" where userroomstid= " +this.profileID +" and ctr_id="+this.ctrolID+";";
         mysql.query(sql0);
         
		String[] sql=new String[factorList.size()];
		int i=0;
		for (Factor ft:factorList) {
			sql[i]="replace into "+profileDetailTable
					+" (userroomstid  ,"     
					+"ctr_id ,"
					+" deviceid,"
					+"factorid ,"
					+"lower ,"
					+"upper ,"
					+"cmpalg ,"
					+"valid_flag ,"
					+"createtime   ,"
					+"modifytime   "
					+ ")"				
					+"values "
					+ "("
					+this.profileID+","
					+this.ctrolID+","
					+ft.getDeviceID()+","
					+ft.getFactorID()+","
					+ft.getMinValue()+","
					+ft.getMaxValue()+","
					+ft.getOperator()+","
					+ft.getValidFlag()+",'"
					+sdf.format(ft.getCreateTime())+"','"
					+sdf.format(ft.getModifyTime())
					+"')";
			int count=mysql.query(sql[i]);
			//System.out.println(sql[i]);
			i++;	
		}		
		
		
		String sql2="replace into "+profileIndexTable
				+" (userroomstid ," 
				+" userroomstname ," 
				+"ctr_id ,"
				+"roomid ,"
				+"roomtype ,"
				+"sttemplateid ,"
				+"stsetid  ,"
				+"createtime ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.profileID+",'"	
				+this.profileName+"',"	
				+this.ctrolID+","
				+this.roomID+","
				+this.roomType+","
				+getProfileTemplateID()+","
				+this.profileSetID+",'"
				+sdf.format(getCreateTime())+"','"
				+sdf.format(getModifyTime())
				+"')";
		//System.out.println(sql2);	
		int count=mysql.query(sql2);
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

		
		return count;	
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static Profile getFromDBByProfileID(MySqlClass mysql,int ctrolID,int profileID) 
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Profile profile=new Profile();
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			int roomID;
			int roomType;
			
			String sql2="select  "
			+" userroomstid       ,"
			+"userroomstname,"
			+"ctr_id        ,"
			+"roomid        ,"
			+"roomtype      ,"
			+"sttemplateid  ,"
			+"stsetid  ,"			
			+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
			+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
			+ "  from "				
			+profileIndexTable
			+" where ctr_id="+ctrolID
			+" and userroomstid="+profileID
			+ ";";
			//System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			//System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				log.error("ERROR:empty query by : "+sql2);
				return null;
			} else if(res2.split("\n").length!=1){
				log.error("ERROR:Multi profile retrieved from mysql. ");
				return null;
			}else{
				String[] index=res2.split(",");
				profile.profileName=index[1];	
				profile.roomID=Integer.parseInt(index[3]);	
				profile.roomType=Integer.parseInt(index[4]);	
				profile.setProfileTemplateID(Integer.parseInt(index[5])); 
				profile.profileSetID=Integer.parseInt(index[6]);
				try {
					profile.setCreateTime(sdf.parse(index[7]));
					profile.setModifyTime(sdf.parse(index[8]));
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			}
			
			String sql="select "
					+"userroomstid," 
					+"ctr_id,"
					+"deviceid,"
					+"factorid,"
					+"lower,"
					+"upper,"
					+"cmpalg,"
					+"valid_flag,"
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+ctrolID
					+" and userroomstid="+profileID
					+ ";";
			//System.out.println("query:"+sql);
			String res=mysql.select(sql);
			//System.out.println("get from mysql:"+res);
			
			List<Factor> factorList=new ArrayList<Factor>();
			if(res==null || res=="" ) {
				//System.err.println("ERROR:query result is empty: "+sql);
				profile.setFactorList(factorList);				
				return profile;
			}			

			String[] resArray=res.split("\n");
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){				
					ft=new Factor();
					
					ft.setRoomID(profile.roomID);
					ft.setRoomType(profile.roomType);
					ft.setDeviceID(Integer.parseInt(cells[2]));
					ft.setFactorID(Integer.parseInt(cells[3]));
					ft.setMinValue(Integer.parseInt(cells[4]));
					ft.setMaxValue(Integer.parseInt(cells[5]));
					ft.setOperator(Integer.parseInt(cells[6]));
					ft.setValidFlag(Integer.parseInt(cells[7]));
					try {
						ft.setCreateTime(sdf.parse(cells[8]));
						ft.setModifyTime(sdf.parse(cells[9]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
										
					factorList.add(ft);
				profile.setFactorList(factorList);
				profile.profileID=Integer.parseInt(cells[0]);
				profile.ctrolID=Integer.parseInt(cells[1]);		
			}/*else {
				System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ profileDetailTable);
				return null;				
			}*/
		}	
		
	try {
		mysql.conn.commit();
	} catch (SQLException e) {
		e.printStackTrace();
	}			
	return profile;			
	}
	
	   /*** 
	   * 从入MYSQL读取profile
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * 根据模板ID获取对应的profile 
	   */
		public	static Profile getFromDBByTemplateID(MySqlClass mysql,int ctrolID,int roomID,int templateID) throws SQLException
			{
				DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Profile profile=new Profile();
				mysql.conn.setAutoCommit(false);
				
				//int roomID;
				int roomType;
				
				String sql2="select  "
				+" userroomstid       ,"
				+"userroomstname,"
				+"ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"sttemplateid  ,"
				+"stsetid  ,"			
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from "				
				+profileIndexTable
				+" where ctr_id="+ctrolID
				+" and sttemplateid="+templateID
				+" and roomid= "+roomID
				+ ";";
				//System.out.println("query:"+sql2);
				String res2=mysql.select(sql2);
				//System.out.println("get from mysql:\n"+res2);
				if(res2==null|| res2==""){
					System.err.println("ERROR:empty query by : "+sql2);
					return null;
				} else if(res2.split("\n").length!=1){
					log.error("ERROR:Multi profile retrieved from mysql. ");
					return null;
				}else{
					String[] index=res2.split(",");
					profile.profileID=Integer.parseInt(index[0]);
					profile.profileName=index[1];	
					roomID=Integer.parseInt(index[3]);	
					roomType=Integer.parseInt(index[4]);	
					profile.setProfileTemplateID(Integer.parseInt(index[5])); 
					profile.profileSetID=Integer.parseInt(index[6]);
					try {
						profile.setCreateTime(sdf.parse(index[7]));
						profile.setModifyTime(sdf.parse(index[8]));
					} catch (ParseException e) {
						e.printStackTrace();
					}				
				}
				
				String sql="select "
						+"userroomstid," 
						+"ctr_id,"
						+"deviceid,"
						+"factorid,"
						+"lower,"
						+"upper,"
						+"cmpalg,"
						+"valid_flag,"
						+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
						+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
						+ "  from  "				
						+profileDetailTable
						+" where ctr_id="+ctrolID
						+" and userroomstid="+profile.profileID
						+ ";";
				//System.out.println("query:"+sql);
				String res=mysql.select(sql);
				//System.out.println("get from mysql:\n"+res);
				if(res==null || res=="" ) {
					System.err.println("ERROR:query result is empty: "+sql);
					return null;
				}
				String[] resArray=res.split("\n");

				List<Factor> factorList=new ArrayList<Factor>();
				Factor ft=null;
				String[] cells=null;
				for(String line:resArray){
					cells=line.split(",");
					if(cells.length>0){				
						ft=new Factor();	
						ft.setRoomID(roomID);
						ft.setRoomType(roomType);
						ft.setDeviceID(Integer.parseInt(cells[2]));
						ft.setFactorID(Integer.parseInt(cells[3]));
						ft.setMinValue(Integer.parseInt(cells[4]));
						ft.setMaxValue(Integer.parseInt(cells[5]));
						ft.setOperator(Integer.parseInt(cells[6]));
						ft.setValidFlag(Integer.parseInt(cells[7]));
						try {
							ft.setCreateTime(sdf.parse(cells[8]));
							ft.setModifyTime(sdf.parse(cells[9]));
						} catch (ParseException e) {
							e.printStackTrace();
						}											
						factorList.add(ft);
						profile.setFactorList(factorList);
						profile.profileID=Integer.parseInt(cells[0]);
						profile.ctrolID=Integer.parseInt(cells[1]);		
					}else {
						System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ profileDetailTable);
						return null;				
					}
				}
				
			
		mysql.conn.commit();			
		return profile;			
		}
	
   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static int deleteFromDB(MySqlClass mysql,int ctrolID,int profileID) 
		{
			int res2=-1;
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String sql="delete "
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+ctrolID
					+" and userroomstid="+profileID
					+ ";";
			//System.out.println("query:"+sql);
			int res=mysql.query(sql);
			//System.out.println("deleted "+ res + " rows of records from table:"+profileDetailTable);
			if(res<0 ) {
				//System.err.println("ERROR: empty result: "+sql);
				return -1;
			}
			
			String sql2="delete   "
			+ "  from "				
			+profileIndexTable
			+" where ctr_id="+ctrolID
			+" and userroomstid="+profileID
			+ ";";
			//System.out.println("query:"+sql2);
			res2=mysql.query(sql2);
			//System.out.println("deleted "+ res + " rows of records from table:"+profileIndexTable);
			if(res2<0){
				log.error("ERROR:exception happened: "+sql2);
				return -1;
			}
			
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return res2;					
	}
	
	   /*** 
	   * 删除一个用户家里所有profile
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	   */
		public	static int deleteProfileListByctrolID(MySqlClass mysql,int ctrolID) 
			{
				try {
					mysql.conn.setAutoCommit(false);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				String sql="delete "
						+ "  from  "				
						+profileDetailTable
						+" where ctr_id="+ctrolID
						+ ";";
				System.out.println("query:"+sql);
				int res=mysql.query(sql);
				//System.out.println("deleted "+ res + " rows of records from table:"+profileDetailTable);
				if(res<=0 ) {
					System.err.println("ERROR: empty result: "+sql);
					return 0;
				}
				
				String sql2="delete   "
				+ "  from "				
				+profileIndexTable
				+" where ctr_id="+ctrolID
				+ ";";
				System.out.println("query:"+sql2);
				int res2=mysql.query(sql2);
				//System.out.println("deleted "+ res + " rows of records from table:"+profileIndexTable);
				if(res2<0){
					log.error("ERROR:exception happened: "+sql2);
					return 0;
				} 
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
			return 1;			
		}
		
		/*public static void deleteCurrentProfileByRoomIDFromRedis(JedisUtil jedis,int ctrolID,int roomID) throws JSONException, ParseException{
			Map<String, String> profileMap = jedis.hgetAll(LogicControl.currentProfile+ctrolID);
			for (Map.Entry<String, String> entry:profileMap.entrySet()) {
				Profile p=new Profile(new JSONObject(entry.getValue()));
				if(p!=null &&p.getRoomID()==roomID){
					jedis.hdel(LogicControl.currentProfile+ctrolID,roomID+"");
				}				
			}
			
		}*/
		
		public static void deleteFactorByDeviceIDFromRedis(JedisUtil jedis,int ctrolID,int deviceID) throws JSONException, ParseException{
			Map<String, String> profileMap = jedis.hgetAll(LogicControl.currentProfile+ctrolID);
			for (Map.Entry<String, String> entry:profileMap.entrySet()) {
				Profile p=new Profile(new JSONObject(entry.getValue()));
				for (Iterator<Factor> iterator = p.getFactorList().iterator(); iterator.hasNext();) {
					Factor factor = (Factor) iterator.next();
					if(factor.getDeviceID()==deviceID){
						iterator.remove();						
					}
				}
				jedis.hset(LogicControl.currentProfile+ctrolID, p.getRoomID()+"", p.toJsonObj().toString());
			}
			
		}
	
   /*** 
   * 从入MYSQL读取profile的 情景详情
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static List<Factor>  getProFactorsFromDB(MySqlClass mysql,int ctrolID,int profileID,int roomID,int roomType) 
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String sql="select "
					+"userroomstid," 
					+"ctr_id,"
					+"deviceid,"
					+"factorid,"
					+"lower,"
					+"upper,"
					+"cmpalg,"
					+"valid_flag,"
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+ctrolID
					+" and userroomstid="+profileID
					+ ";";
			//System.out.println("query:"+sql);
			String res=mysql.select(sql);
			//System.out.println("get from mysql:\n"+res);
			if(res==null  || res=="") {
				return new ArrayList<Factor>();
			}
			String[] resArray=res.split("\n");
			List<Factor> factorList=null;//new ArrayList<Factor>();
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length==10){				
					ft=new Factor();	
					ft.setRoomID(roomID);
					ft.setRoomType(roomType);
					ft.setDeviceID(Integer.parseInt(cells[2]));
					ft.setFactorID(Integer.parseInt(cells[3]));
					ft.setMinValue(Integer.parseInt(cells[4]));
					ft.setMaxValue(Integer.parseInt(cells[5]));
					ft.setOperator(Integer.parseInt(cells[6]));
					ft.setValidFlag(Integer.parseInt(cells[7]));
					try {
						ft.setCreateTime(sdf.parse(cells[8]));
						ft.setModifyTime(sdf.parse(cells[9]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					factorList=new ArrayList<Factor>();
					factorList.add(ft);					
				}else {
					System.err.println("ERROR:Columns mismatch between class Profile  and table  "+ profileDetailTable);
					return null;				
				}
			}		
		
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
			return factorList;			
		}
	
	   /*** 
	   * 从入MYSQL读取profile的 基本情况
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_user_room_st
	   * @throws SQLException 
	    */
	public	static Profile  getHeadFromDB(MySqlClass mysql,int ctrolID,int profileID) throws SQLException
		{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Profile profile =new Profile();
		String sql2="select  "
				+" userroomstid       ,"
				+"userroomstname,"
				+"ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"sttemplateid  ,"
				+"stsetid  ,"					
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from "				
				+profileIndexTable
				+" where ctr_id="+ctrolID
				+" and userroomstid="+profileID
				+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			log.error("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			profile.profileName=index[1];	
			//profile.roomID=Integer.parseInt(index[3]);	
			//profile.roomType=Integer.parseInt(index[4]);	
			profile.setProfileTemplateID(Integer.parseInt(index[5])); 
			profile.profileSetID=Integer.parseInt(index[6]);
			try {
				profile.setCreateTime(sdf.parse(index[7]));
				profile.setModifyTime(sdf.parse(index[8]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			}		
		
			return profile;		
		}
	
	public Factor getFactorByID(int factorID){
		for (Factor f:this.factorList) {
			if (f.getFactorID()==factorID) {
				return f;
			}
		}
		return null;
		
	}
	
	public static Map<Integer, Set<Integer>> getCurrentProfileTemplateID(JedisUtil jedis,int ctrolID) throws JSONException{
		Map<Integer, Set<Integer>> currentProfile=new HashMap<Integer, Set<Integer>>();
		Map<String, String> x = jedis.hgetAll(LogicControl.currentProfile+ctrolID);
		if(x==null){
			return null;
		}else{
			Iterator<Map.Entry<String, String>> it = x.entrySet().iterator();				
			while( it.hasNext()){
				Map.Entry<String, String> pmap=it.next();
				JSONObject json=new JSONObject(pmap.getValue())	;
				int templateID=json.getInt("profileTemplateID");
				Set<Integer> roomIDSet=currentProfile.get(templateID);
				if(roomIDSet==null){
					roomIDSet=new TreeSet<Integer>() ;
				}
				roomIDSet.add(Integer.parseInt(pmap.getKey()));	
				currentProfile.put(templateID, roomIDSet);
			}
		}
		return currentProfile;
	}
	
	
	public static String getOneProfile(JedisUtil jedis,int ctrolID){
		Map<String, String> x = jedis.hgetAll(LogicControl.currentProfile+ctrolID);
		if(x==null){
			return null;
		}else{
			Iterator<Map.Entry<String, String>> it = x.entrySet().iterator();				
			while( it.hasNext()){
				Map.Entry<String, String> pmap=it.next();
				return pmap.getValue()	;
			}
		}
		return null;
	}
	
	
	public static Profile getCustomerProfile(int ctrolID,int profileTemplateID,int roomID,int roomType){
		Profile p=new Profile();
		p.profileID = 199;
		p.profileName = "手动模式";
		p.ctrolID = ctrolID;
		p.profileTemplateID = profileTemplateID;
		p.profileSetID = 1;
		p.roomID=roomID;
		p.roomType=roomType;
		p.factorList = new ArrayList<Factor>() ;
		p.createTime = new Date();
		p.modifyTime = new Date();
		return p;
		
	}

	
	public static void main(String[] args) throws SQLException, JSONException {
		MySqlClass mysql=new MySqlClass("120.24.81.226","3306","cooxm_device_control", "cooxm", "cooxm");
		Profile p =new Profile();
//		p=Profile.getFromDBByProfileID(mysql, 12345677, 123456789);
//		p.getFactorList().remove(0);
//		p.setProfileName("离家模式111");
//		p.saveToDB(mysql);
		//System.out.println(p.toJsonObj().toString());
	    //JSONObject jo=p.toJsonObj();
		
//		JedisUtil jedis =new JedisUtil("120.24.81.226",6379, 5000);
//		jedis.select(9);
//		Map<Integer, Set<Integer>> x = getCurrentProfileTemplateID(jedis,10002);
//		String x2=getOneProfile(jedis,10002);
		
		
		p=Profile.getFromDBByTemplateID(mysql, 10003, 1000, 2);
		System.out.println(p.getProfileName());
		
	}

}
