package device;

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

import org.json.JSONException;
import org.json.JSONObject;

import util.MySqlClass;


/***情景模式集：
 * <p>涉及到多个房间的情景模式，这里只列出多个房间的情景模式ID，具体情景模式定义要根据ID去情景模式里查找</p>*/
public class ProfileSet {
	int CtrolID;
	int profileSetID;
	String profileSetName;
	/***<br>0：完全用户全新创建；<br>1：系统模版ID；*/
	int profileSetTemplateID;
	/*** List of profileID*/
	List<Integer> profileList;	
	Date createTime;
	Date modifyTime;
	ProfileSet currProfileSet;
	static final String  profileSetTable="info_user_st_set";
	
	ProfileSet(){}
	
	 ProfileSet(ProfileSet pc){
		 this.CtrolID=pc.CtrolID;
		 this.profileSetID=pc.profileSetID;
		 this.profileSetName=pc.profileSetName;
		 this.profileSetTemplateID=pc.profileSetTemplateID;
		 this.profileList=pc.profileList;
		 this.createTime=pc.createTime;
		 this.modifyTime=pc.modifyTime;		 
	 }
	 
	public JSONObject toJsonObj(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileSetJson = new JSONObject(); 
        JSONObject profileJson ; 
	    try {
		    profileSetJson.put("deviceSN",        this.CtrolID       );
			profileSetJson.put("profileSetID",         this.profileSetID      );
		    profileSetJson.put("deviceID",        this.profileSetName      );
		    profileSetJson.put("profileSetTemplateID",      this.profileSetTemplateID        );
		    for(Integer profileID: this.profileList){
		    	profileJson= new JSONObject(); 
		    	profileJson.put("profileID",profileID); 
		    	profileSetJson.accumulate("profileList", profileJson);
		    }
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
		String sql="insert into "+profileSetTable
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
				+this.CtrolID+","	
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
	public	static ProfileSet getProfileSetFromDB(MySqlClass mysql,int CtrolID,int profileSetID) throws SQLException
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
					+" where ctr_id="+CtrolID
					+" and userstsetid="+profileSetID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==""||res.length()==0) {
				System.out.println("ERROR:query result is empty: "+sql);
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
			
			profileSet.CtrolID=Integer.parseInt(cells[0]);
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
