package cooxm.devicecontrol.synchronize;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 16, 2015 5:16:53 PM 
 */

public class TimeRecord{
	public static final String info_syn_updatetime="info_syn_updatetime";
	int ctrolID;
	String tableName;
	Date updateTime;
	int  lastSynRole;
	
	
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getLastSynRole() {
		return lastSynRole;
	}
	public void setLastSynRole(int lastSynRole) {
		this.lastSynRole = lastSynRole;
	}
	
	public TimeRecord(int ctrolID, String tableName, Date updateTime,
			int lastSynRole) {
		this.ctrolID = ctrolID;
		this.tableName = tableName;
		this.updateTime = updateTime;
		this.lastSynRole = lastSynRole;
	}
	public JSONObject toJson(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json=null; 
		try {
			json =new JSONObject();
			json.put("tableName", tableName);
			json.put("updateTime", sdf.format(updateTime));
			json.put("lastSynRole", lastSynRole);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return json;		
	}
	
	public TimeRecord (JSONObject json){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.tableName=json.optString("tableName");
		try {
			this.updateTime=sdf.parse(json.optString("updateTime"));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		this.lastSynRole=json.optInt("lastSynRole");		
	}
	
	public TimeRecord() {
	}
	public boolean isEmpty(){
		if(this.tableName==null|| this.updateTime==null )
			return true;
		return false;
		
	}
	
	/*** 
	 * Save Time Record info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @throws SQLException
	 * @returns 0 :保存失败；
	 * 			1   ：保存成功
	 * */
	public int saveToDB(MySqlClass mysql){
		if(this.isEmpty()){
			System.out.println("ERROR:object is empty,can't save to mysql");
			return 0;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql="insert into "+info_syn_updatetime
				+" (ctrolID  ,"     
				+"tablename ,"
				+"updateTime ,"
				+"lastSynRole "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+",'"
				+this.tableName+"','"
				+sdf.format(this.updateTime)+"',"
				+this.lastSynRole
				+")";
		System.out.println(sql);
		int count=mysql.query(sql);
//		if(count>0) System.out.println("insert success"); 		
	
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return 1;	
	}
	
	public static TimeRecord fromDB(MySqlClass mysql,int ctrolID,String tableName){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeRecord rec= new TimeRecord();
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql="select  "
				+" (ctrolID  ,"     
				+"tablename ,"
				+"date_format(updateTime,'%Y-%m-%d %H:%i:%S') ,"
				+"lastSynRole "
				+ ") "				
				+" from  "
				+info_syn_updatetime
				+"where ctrolID=" + ctrolID
				+"and tableName=" + tableName
				;
		System.out.println(sql);
		String res2=mysql.select(sql);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null;
		} else if(res2.split("\n").length!=1){
			System.err.println("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			rec.ctrolID=Integer.parseInt(index[0]);
			rec.tableName=index[1];
			try {
				rec.updateTime=sdf.parse(index[2]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			rec.lastSynRole=Integer.parseInt(index[3]);
		}
	
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return rec;		
	}
	
	
}
