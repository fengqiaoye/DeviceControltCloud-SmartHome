package cooxm.devicecontrol.device;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：28 Jan 2015 14:24:17 
 */

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

import cooxm.devicecontrol.util.MySqlClass;

public class Trigger {

	private int ctrolID;
	private int triggerID;
	
	private List<TriggerFactor>  triggerFactorList;
	private List<TriggerTemplateReact>   triggerReactList;
	/** 在一天之内触发的次数*/
    int times;
	
	static String triggerFactorInfoTable="info_trigger";
	static String triggerReactInfoTable ="info_trigger_react";

	

	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public int getTriggerID() {
		return triggerID;
	}
	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}
	public List<TriggerFactor> getTriggerFactorList() {
		return triggerFactorList;
	}
	public void setTriggerFactorList(
			List<TriggerFactor> triggerFactorList) {
		this.triggerFactorList = triggerFactorList;
	}
	public List<TriggerTemplateReact> getTriggerTemplateReactList() {
		return triggerReactList;
	}
	public void setTriggerTemplateReactList(
			List<TriggerTemplateReact> triggerReactList) {
		this.triggerReactList = triggerReactList;
	}
	public Trigger(){}
	public Trigger(Trigger trigger){
		this.ctrolID=trigger.ctrolID;		
		this.triggerID = trigger.triggerID;
		this.triggerFactorList = trigger.triggerFactorList;
		this.triggerReactList = trigger.triggerReactList;
	}
	
	public Trigger(int ctrolID,int triggerID,
			List<TriggerFactor> triggerFactorList,
			List<TriggerTemplateReact> triggerReactList) {
		this.ctrolID=ctrolID;		
		this.triggerID = triggerID;
		this.triggerFactorList = triggerFactorList;
		this.triggerReactList = triggerReactList;
	}
	

	/*** 
	 * Save  to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException
	 * @returns 0 :profile为空；
	 * 			1   ：保存成功
	 * */
	public int saveToDB(MySqlClass mysql) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (TriggerFactor ft:this.triggerFactorList) {
			String sql="insert into "+triggerFactorInfoTable
					+" ("
					+ "ctrolid ," 
					+ "triggerid ,"     
					+"logicalrelation ,"
					+"roomtype ,"
					+"roomid ,"
					+"factorid ,"
					+"operater ,"
					+"min ,"
					+"max ,"
					+"accumilatetime   ,"
					+"validflag, "
					+"createtime, "
					+"modifytime "
					+ ")"				
					+"values "
					+ "("
					+this.ctrolID+","
					+this.triggerID+",'"
					+ft.getLogicalRelation()+"',"
					+ft.getRoomType()+","
					+ft.getRoomID()+","
					+ft.getRoomID()+","
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
	
		for (TriggerTemplateReact react:this.triggerReactList) {
		String sql2="insert into "+triggerReactInfoTable
				+" ("
				+ "ctrolid ," 
				+ "triggerid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+","	
				+this.triggerID+","	
				+react.getReactType()+","	
				+react.getTargetID()+","
				+react.getReactWay()
				+")";
			System.out.println(sql2);	
			mysql.query(sql2);
		}
		
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
	public	static Trigger getFromDB(MySqlClass mysql,int ctrolID,int triggerid)
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String sql="select "
					+"ctrolid  ,"    			
					+"triggerid  ,"     
					+"logicalrelation ,"
					+"roomtype ,"
					+"roomid ,"					
					+"factorid ,"
					+"operater ,"
					+"min ,"
					+"max ,"
					+"accumilatetime   ,"
					+"validflag, "
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+triggerFactorInfoTable
					+" where triggerid="+triggerid
					+" and ctrolid="+ctrolID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null || res=="" ) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			Trigger triggert=new Trigger();
			List<TriggerFactor> factorList=new ArrayList<TriggerFactor>();
			TriggerFactor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){			
					ft=new TriggerFactor();					
					ft.setLogicalRelation(cells[2]);					
					ft.setRoomType(Integer.parseInt(cells[3]));
					ft.setRoomType(Integer.parseInt(cells[4]));
					ft.setFactorID(Integer.parseInt(cells[5]));
					ft.setOperator(Integer.parseInt(cells[6]));
					ft.setMinValue(Integer.parseInt(cells[7]));
					ft.setMaxValue(Integer.parseInt(cells[8]));
					ft.setAccumilateTime(Integer.parseInt(cells[9]));
					ft.setValidFlag(Integer.parseInt(cells[10]));
					try {
						ft.setCreateTime(sdf.parse(cells[11]));
						ft.setModifyTime(sdf.parse(cells[12]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					ft.setState(false);
					
					factorList.add(ft);
					triggert.setTriggerFactorList(factorList);
					triggert.setCtrolID(Integer.parseInt(cells[0]));
					triggert.setTriggerID(Integer.parseInt(cells[1]));
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ triggerFactorInfoTable);
					return null;				
				}
			}			
			
			List<TriggerTemplateReact> triggerReactList=new ArrayList<TriggerTemplateReact>();
			TriggerTemplateReact react=null;
			String sql2="select  "
					+"ctrolid  ,"    			
     				+" triggerid ," 
					+" reacttype ," 
					+"targetid ,"
					+"reactway "	
					+ " from  "	
					+triggerReactInfoTable
					+" where triggerid="+triggerid
					+ ";";
			System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				System.err.println("ERROR:empty query by : "+sql2);
				return null;
			} 
			String[] resArray2=res2.split("\n");
			for(String line:resArray2){
				String [] array=line.split(",");
				react=new TriggerTemplateReact();
				//react.setCtrolID(Integer.parseInt(array[0]));
				react.setReactType(Integer.parseInt(array[2]));
				react.setTargetID(Integer.parseInt(array[3]));
				react.setReactWay(Integer.parseInt(array[4]));
				triggerReactList.add(react);
			}	
			triggert.setTriggerTemplateReactList(triggerReactList);
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		 return triggert;			
	}
	

	public static int deleteFromDB(MySqlClass mysql, int ctrolID,	int triggerID) {
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String sql="delete * "
				+ "  from  "				
				+triggerFactorInfoTable
				+" where ctr_id="+ctrolID
				+" and userroomstid="+triggerID
				+ ";";
		int res=mysql.query(sql);
		//System.out.println("deleted "+ res + " rows of records from table:"+triggerFactorInfoTable);
		if(res<=0 ) {
			System.err.println("ERROR: empty result: "+sql);
			return 0;
		}
		
		String sql2="delete *  "
		+ "  from "				
		+triggerReactInfoTable
		+" where ctr_id="+ctrolID
		+" and userroomstid="+triggerID
		+ ";";
		System.out.println("query:"+sql2);
		int res2=mysql.query(sql2);
		//System.out.println("deleted "+ res + " rows of records from table:"+triggerReactInfoTable);
		if(res2<0){
			System.err.println("ERROR:exception happened: "+sql2);
			return 0;
		} 
	try {
		mysql.conn.commit();
	} catch (SQLException e) {
		e.printStackTrace();
	}			
	return 1;		
	}
	
	public static  Trigger fromJson(JSONObject TriggerJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		Trigger trigger=new Trigger();
		try {
			trigger.triggerID=TriggerJson.getInt("triggerID");
			JSONArray factorListJSON= TriggerJson.getJSONArray("factorList");
			List<TriggerFactor> factorList = new ArrayList<TriggerFactor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				TriggerFactor factor= new TriggerFactor();
				factor=TriggerFactor.fromJson(factorJson);
				factorList.add(factor);		
			}		
			trigger.setTriggerFactorList(factorList);
			
			JSONArray reactListJSON= TriggerJson.getJSONArray("reactList");
			List<TriggerTemplateReact> reactList = new ArrayList<TriggerTemplateReact>() ;
			for(int i=0;i<reactListJSON.length();i++){
				JSONObject reactJson=factorListJSON.getJSONObject(i);
				TriggerTemplateReact react= new TriggerTemplateReact();
				react=TriggerTemplateReact.fromJson(reactJson);
				reactList.add(react);		
			}		
			trigger.setTriggerTemplateReactList(reactList);			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return trigger;
	}
	
	public JSONObject toJson() {		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject triggerJson=new JSONObject();
    	JSONObject factorJson;
		try {
    		triggerJson.put("triggerID", getTriggerID());  
         	for (TriggerFactor factor:getTriggerFactorList()) {
		    	factorJson= new JSONObject(); 
		    	factor.toJson();		    	
		    	triggerJson.accumulate("factorList",factorJson);			
			}
         	
         	for (TriggerTemplateReact react:getTriggerTemplateReactList()) {
		    	factorJson= new JSONObject(); 
		    	react.toJson();		    	
		    	triggerJson.accumulate("reactList",factorJson);			
			}     	
        	
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return triggerJson;		
	}
	
	public Date getModifyTime() {
		Date modifyTime=new Date(0);
     	for (TriggerFactor factor:getTriggerFactorList()) {
		   Date  factorModifyTime=factor.getModifyTime();
		   if(factorModifyTime.after(modifyTime)){
			   modifyTime=factorModifyTime;
		   }
		}		
		return modifyTime;
	}
	

	
	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		
		Trigger T=getFromDB(mysql, 1234567,1025);
		T.saveToDB(mysql);

	}


}
