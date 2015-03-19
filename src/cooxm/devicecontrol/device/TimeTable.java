package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 16, 2015 4:50:15 PM 
 */

public class TimeTable {
	
	List<TimeRecord> timeRecList;
	public List<TimeRecord> getTimeRecList() {
		return timeRecList;
	}
	public void setTimeRecList(List<TimeRecord> timeRecList) {
		this.timeRecList = timeRecList;
	}
	
	public TimeTable(List<TimeRecord> timeRecList) {
		this.timeRecList = timeRecList;
	}
	public TimeTable(JSONObject json){
		JSONArray jsonArray;
		try {
			jsonArray = json.getJSONArray("timeTable");
			for (int i=0; i<jsonArray.length();i++) {	
				TimeRecord rec=new TimeRecord(jsonArray.getJSONObject(i));
				this.timeRecList.add(rec);
				
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	
	public JSONObject toJson(){
		JSONObject json=new JSONObject();
		for (TimeRecord rec :this.timeRecList) {			
			try {
				json.accumulate("timeTable", rec.toJson());
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		
		return json;		
	}
	
	public boolean isEmpty(){
		if(this.timeRecList==null ){
			System.err.println("ERROR:Object is empty");
			return true;
		}
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
			for(TimeRecord rec:this.timeRecList){
				rec.saveToDB(mysql);
			}
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return 1;	
	}
	
	public TimeTable fromDB(MySqlClass mysql){
		List<TimeRecord> timeTable =new ArrayList<TimeRecord>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
				+TimeRecord.info_syn_updatetime
				;
		System.out.println(sql);
		String res=mysql.select(sql);
		if(res==null|| res==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null;
		} else{
			String[] line=res.split("\n");
			for(int i=0;i<line.length;i++){
				String[] index=line[i].split(",");
				TimeRecord rec;
				try {
					rec = new TimeRecord(Integer.parseInt(index[0]),
							               index[1],sdf.parse(index[2]),Integer.parseInt(index[3]));
					timeTable.add(rec);
				} catch (NumberFormatException | ParseException e) {
					e.printStackTrace();
				}
			}
		}
	
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return new TimeTable(timeRecList);				
	}
	
	

	public static void main(String[] args) {		
		

	}

}
