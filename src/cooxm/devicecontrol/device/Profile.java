package cooxm.devicecontrol.device;


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import cooxm.devicecontrol.util.MySqlClass;


/***
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 * @function  一个房间的情景模式，可能包含多个情景因素(家电、环境等)
 * */
public class Profile  {
	
	private int profileID;
	private String profileName;
	private int ctrolID;
	//private int roomID;
	//private int roomType;
	private int profileTemplateID;
	private int profileSetID;
	private List<Factor> factorList;
	private Date createTime;
	private Date modifyTime;
		
	static final String  profileDetailTable="info_user_room_st_factor";
	static final String  profileIndexTable="info_user_room_st";	

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
	

	public List<Factor> getFactorList() {
		return factorList;
	}

	public void setFactorList(List<Factor> factorList) {
		this.factorList = factorList;
	}
	public Profile (){}

	public Profile (JSONObject profileJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.profileID=profileJson.getInt("profileID");
			this.profileName=profileJson.getString("profileName");
			this.ctrolID=profileJson.getInt("ctrolID");
			//this.roomID=profileJson.getInt("roomID");
			//this.roomType=profileJson.getInt("roomType");
			//this.profileTemplateID=profileJson.getInt("profileTemplateID");
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
			setModifyTime(sdf.parse(profileJson.optString("createTime")));	
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
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
		    for(Factor factor: getFactorList()){
		    	factorJson= new JSONObject(); 
		    	/*factorJson.put("factorID", factor.factorID);
		    	factorJson.put("minValue", factor.minValue);
		    	factorJson.put("maxValue", factor.maxValue);
		    	factorJson.put("operator", factor.operator);
		    	factorJson.put("validFlag", factor.validFlag);
		    	factorJson.put("createTime", sdf.format(factor.getCreateTime()));
		    	factorJson.put("modifyTime", sdf.format(factor.getModifyTime()));*/
		    	factorJson=factor.toProfileJson();		    	
		    	profileJson.accumulate("factorList",factorJson); 
		    }
		    
		    profileJson.put("createTime",sdf.format(getCreateTime()));
		    profileJson.put("modifyTime",sdf.format(getModifyTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}  		
		return profileJson;
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
	
	
	public boolean isEmpty(){
		if(getFactorList()==null||getCreateTime()==null){			
			return true;
		}		
		return false;		
	}
	
	public boolean isExistInDB(MySqlClass mysql){
		
		return false;
	}

	
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "XXXX");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException
	 * @returns 0 :profile为空；
	 * 			1   ：保存成功
	 * */
	public int saveToDB(MySqlClass mysql){
		if(this.isEmpty()){
			System.out.println("ERROR:object is empty,can't save to mysql");
			return 0;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Factor> factorList=getFactorList();
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (Factor ft:factorList) {
			String sql="replace into "+profileDetailTable
					+" (userroomstid  ,"     
					+"ctr_id ,"
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
					+ft.getFactorID()+","
					+ft.getMinValue()+","
					+ft.getMaxValue()+","
					+ft.getOperator()+","
					+ft.getValidFlag()+",'"
					+sdf.format(ft.getCreateTime())+"','"
					+sdf.format(ft.getModifyTime())
					+"')";
			System.out.println(sql);
			int count=mysql.query(sql);
			if(count>0) System.out.println("insert success"); 	
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
				+factorList.get(0).getRoomID()+","
				+factorList.get(0).getRoomType()+","
				+getProfileTemplateID()+","
				+this.profileSetID+",'"
				+sdf.format(getCreateTime())+"','"
				+sdf.format(getModifyTime())
				+"')";
		System.out.println(sql2);	
		mysql.query(sql2);
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return 1;	
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static Profile getFromDB(MySqlClass mysql,int ctrolID,int profileID) throws SQLException
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Profile profile=new Profile();
			mysql.conn.setAutoCommit(false);
			
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
			System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				System.err.println("ERROR:empty query by : "+sql2);
				return null;
			} else if(res2.split("\n").length!=1){
				System.err.println("ERROR:Multi profile retrieved from mysql. ");
				return null;
			}else{
				String[] index=res2.split(",");
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
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
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
					ft.setFactorID(Integer.parseInt(cells[2]));
					ft.setMinValue(Integer.parseInt(cells[3]));
					ft.setMaxValue(Integer.parseInt(cells[4]));
					ft.setOperator(Integer.parseInt(cells[5]));
					ft.setValidFlag(Integer.parseInt(cells[6]));
					try {
						ft.setCreateTime(sdf.parse(cells[7]));
						ft.setModifyTime(sdf.parse(cells[8]));
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
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static int deleteFromDB(MySqlClass mysql,int ctrolID,int profileID) throws SQLException
		{
			mysql.conn.setAutoCommit(false);
			String sql="delete * "
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+ctrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			int res=mysql.query(sql);
			System.out.println("deleted "+ res + " rows of records from table:"+profileDetailTable);
			if(res<=0 ) {
				System.err.println("ERROR: empty result: "+sql);
				return 0;
			}
			
			String sql2="delete *  "
			+ "  from "				
			+profileIndexTable
			+" where ctr_id="+ctrolID
			+" and userroomstid="+profileID
			+ ";";
			System.out.println("query:"+sql2);
			int res2=mysql.query(sql2);
			System.out.println("deleted "+ res + " rows of records from table:"+profileIndexTable);
			if(res2<0){
				System.err.println("ERROR:exception happened: "+sql2);
				return 0;
			} 
		mysql.conn.commit();			
		return 1;			
	}
	
	
   /*** 
   * 从入MYSQL读取profile的 情景详情
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
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
			if(res==null ) {
				System.err.println("ERROR:exception happened: "+sql);
				return null;
			}else if(res=="") {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			List<Factor> factorList=null;//new ArrayList<Factor>();
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length==9){				
					ft=new Factor();	
					ft.setRoomID(roomID);
					ft.setRoomType(roomType);
					ft.setFactorID(Integer.parseInt(cells[3]));
					ft.setMinValue(Integer.parseInt(cells[3]));
					ft.setMaxValue(Integer.parseInt(cells[4]));
					ft.setOperator(Integer.parseInt(cells[5]));
					ft.setValidFlag(Integer.parseInt(cells[6]));
					try {
						ft.setCreateTime(sdf.parse(cells[7]));
						ft.setModifyTime(sdf.parse(cells[8]));
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
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
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
			System.err.println("ERROR:Multi profile retrieved from mysql. ");
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
	
	public int getRoomID(){
		return this.factorList.get(0).getRoomID();		
	}
	
	public int getRoomType(){
		return this.factorList.get(0).getRoomType()	;
	}
		
	public int getProfileType(){
		return this.profileTemplateID;
	}
	
	public static void main(String[] args) throws SQLException, JSONException {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		Profile p =new Profile();
		p=Profile.getFromDB(mysql, 12345677, 123456789);
		System.out.println(p.toJsonObj().toString());
	    //JSONObject jo=p.toJsonObj();		
		
		Jedis jedis=new Jedis("172.16.35.170", 6379);
		jedis.set(p.profileID+"",p.toJsonObj().toString());
		System.out.println(jedis.get(p.profileID+""));
		jedis.hgetAll("key");

		//p.profileID+=2;		
		//p.saveToDB(mysql);
		
	}

}
