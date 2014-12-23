package device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.util.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import util.MySqlClass;

public class Profile {
	
	int profileID;
	String profileName;
	int CtrolID;
	int roomID;
	int roomType;
	int profileTemplateID;
	int profileSetID;
	List<Factor> factorList;
	Date createTime;
	Date modifyTime;
	
	static final String  profileDetailTable="info_user_room_st_factor";
	static final String  profileIndexTable="info_user_room_st";
	

	Profile (){}
	Profile (Profile pf){
		this.profileID=pf.profileID;
		this.profileName=pf.profileName;
		this.profileID=pf.CtrolID;
		this.CtrolID=pf.CtrolID;
		this.roomID=pf.roomID;
		this.roomType=pf.roomType;
		this.profileTemplateID=pf.profileTemplateID;
		this.profileSetID=pf.profileSetID;
		this.factorList=pf.factorList;
		this.createTime=pf.createTime;
		this.modifyTime=pf.modifyTime;		
	}
	
	public Factor getFactor(int factorID){		
		for (int i = 0; i < this.factorList.size(); i++) {
			if(this.factorList.get(i).factorID==factorID){
				return this.factorList.get(i);
			}			
		}
		return null;
	}
	
	
	public boolean isEmpty(){
		if(this.factorList==null||this.createTime==null ||this.modifyTime==null){			
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
	public int saveProfileToDB(MySqlClass mysql) throws SQLException{
		if(this.isEmpty()){
			System.out.println("ERROR:object is empty,can't save to mysql");
			return -1;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);
		int resultCount=0;
		for (Factor ft:this.factorList) {
			String sql="insert into "+profileDetailTable
					+" (userroomstid       ,"     
					+"ctr_id        ,"
					+"factorid        ,"
					+"lower      ,"
					+"upper         ,"
					+"cmpalg         ,"
					+"valid_flag     ,"
					+"createtime   ,"
					+"modifytime   "
					+ ")"				
					+"values "
					+ "("
					+this.profileID+","
					+this.CtrolID+","
					+ft.factorID+","
					+ft.minValue+","
					+ft.maxValue+","
					+ft.compareWay+","
					+ft.validFlag+",'"
					+sdf.format(ft.createTime)+"','"
					+sdf.format(ft.modifyTime)
					+"')";
			System.out.println(sql);
			int count=mysql.query(sql);
			if(count>0) System.out.println("insert success");
			resultCount+=count; 	
		}
		
		
		
		
		String sql="insert into "+profileIndexTable
				+" (userroomstid       ," 
				+" userroomstname       ," 
				+"ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"sttemplateid         ,"
				+"stsetid         ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.profileID+",'"	
				+this.profileName+"',"	
				+this.CtrolID+","
				+this.roomID+","
				+this.roomType+","
				+0+","
				+this.profileSetID+",'"
				+sdf.format(this.createTime)+"','"
				+sdf.format(this.modifyTime)
				+"')";
		System.out.println(sql);		
		resultCount+= mysql.query(sql);			
		mysql.conn.commit();
		
		
		return resultCount;	
	}

	   /*** 
	   * 从入MYSQL读取profile
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	    */
	public	static Profile getOneProfileFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
		    String tablename="info_user_room_st_factor";
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
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
					+tablename
					+" where ctr_id="+CtrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null ) {
				System.out.println("ERROR:exception happened: "+sql);
				return null;
			}else if(res=="") {
				System.out.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			Profile profile=null;
			List<Factor> factorList=null;//new ArrayList<Factor>();
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length==9){				
					ft=new Factor();			
					ft.factorID=Integer.parseInt(cells[3]);
					//ft.factorType=Integer.parseInt(cells[1]);
					//ft.factorName=cells[2];
					ft.minValue=Integer.parseInt(cells[3]);
					ft.maxValue=Integer.parseInt(cells[4]);
					ft.compareWay=Integer.parseInt(cells[5]);
					ft.validFlag=Boolean.parseBoolean(cells[6]);
					try {
						ft.createTime=sdf.parse(cells[7]);
						ft.modifyTime=sdf.parse(cells[8]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					profile=new Profile();
					factorList=new ArrayList<Factor>();
					factorList.add(ft);
					profile.factorList=factorList;
					profile.profileID=Integer.parseInt(cells[0]);
					profile.CtrolID=Integer.parseInt(cells[1]);		
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ tablename);
					return null;				
				}
			}
			
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
			+" where ctr_id="+CtrolID
			+" and userroomstid="+profileID
			+ ";";
			System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				System.out.println("ERROR:empty query by : "+sql2);
				return null;
			} else if(res2.split("\n").length!=1){
				System.out.println("ERROR:Multi profile retrieved from mysql. ");
				return null;
			}else{
				String[] index=res2.split(",");
				profile.profileName=index[1];	
				profile.roomID=Integer.parseInt(index[3]);	
				profile.roomType=Integer.parseInt(index[4]);	
				profile.profileTemplateID=Integer.parseInt(index[5]); 
				profile.profileSetID=Integer.parseInt(index[6]);
				try {
					profile.createTime=sdf.parse(index[7]);
					profile.modifyTime=sdf.parse(index[8]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}		
	mysql.conn.commit();			
			return profile;			
		}
	
	
	   /*** 
	   * 从入MYSQL读取profile的 情景详情
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	    */
	public	static List<Factor>  getProFactorsFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
		    String tablename="info_user_room_st_factor";
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
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
					+tablename
					+" where ctr_id="+CtrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null ) {
				System.out.println("ERROR:exception happened: "+sql);
				return null;
			}else if(res=="") {
				System.out.println("ERROR:query result is empty: "+sql);
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
					ft.factorID=Integer.parseInt(cells[3]);
					//ft.factorType=Integer.parseInt(cells[1]);
					//ft.factorName=cells[2];
					ft.minValue=Integer.parseInt(cells[3]);
					ft.maxValue=Integer.parseInt(cells[4]);
					ft.compareWay=Integer.parseInt(cells[5]);
					ft.validFlag=Boolean.parseBoolean(cells[6]);
					try {
						ft.createTime=sdf.parse(cells[7]);
						ft.modifyTime=sdf.parse(cells[8]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					factorList=new ArrayList<Factor>();
					factorList.add(ft);					
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ tablename);
					return null;				
				}
			}		
		
			mysql.conn.commit();			
			return factorList;			
		}
	
	   /*** 
	   * 从入MYSQL读取profile的 基本情况
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st
	   * @throws SQLException 
	    */
	public	static Profile  getProfileHeadFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
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
				+" where ctr_id="+CtrolID
				+" and userroomstid="+profileID
				+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			System.out.println("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			profile.profileName=index[1];	
			profile.roomID=Integer.parseInt(index[3]);	
			profile.roomType=Integer.parseInt(index[4]);	
			profile.profileTemplateID=Integer.parseInt(index[5]); 
			profile.profileSetID=Integer.parseInt(index[6]);
			try {
				profile.createTime=sdf.parse(index[7]);
				profile.modifyTime=sdf.parse(index[8]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}		
		
			return profile;		
		}
	
	
	/*public boolean SynProfile(MySqlClass mysql,Profile srcProfile) throws SQLException{
		
		Profile target=Profile.getProfileHeadFromDB(mysql, srcProfile.CtrolID, srcProfile.profileID);
		
		if(target.modifyTime.before(srcProfile.modifyTime)){ //mysql表的时间比较旧，则保存上报的profile
			if(srcProfile.saveProfileToDB(mysql)>0){
				return true;
			}else
				return false;
		}else { //mysql表的时间的时间比较新，则下发mysql的profile                                              
			
		}
		
		
		
	}*/

	
	
	
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		Profile p =new Profile();
		p=Profile.getOneProfileFromDB(mysql, 12345677, 123456789);
		p.profileID++;
		
		try {
			p.saveProfileToDB(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
