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

public class TriggerTemplate {

	//private int ctrolID;
	private int triggerTemplateID;
	
	private List<TriggerTemplateFactor>  triggerTemplateFactorList;
	private List<TriggerTemplateReact>   triggerTemplateReactList;
	
	private static String triggerFactorTable="cfg_regular_template";
	private static String triggerReactTable="cfg_regular_template_react";
	

	public int getTriggerTemplateID() {
		return triggerTemplateID;
	}
	public void setTriggerTemplateID(int triggerTemplateID) {
		this.triggerTemplateID = triggerTemplateID;
	}
	public List<TriggerTemplateFactor> getTriggerTemplateFactorList() {
		return triggerTemplateFactorList;
	}
	public void setTriggerTemplateFactorList(
			List<TriggerTemplateFactor> triggerTemplateFactorList) {
		this.triggerTemplateFactorList = triggerTemplateFactorList;
	}
	public List<TriggerTemplateReact> getTriggerTemplateReactList() {
		return triggerTemplateReactList;
	}
	public void setTriggerTemplateReactList(
			List<TriggerTemplateReact> triggerTemplateReactList) {
		this.triggerTemplateReactList = triggerTemplateReactList;
	}
	public TriggerTemplate(){}
	
	public TriggerTemplate(int triggerTemplateID,
			List<TriggerTemplateFactor> triggerFactorList,
			List<TriggerTemplateReact> triggerReactList) {
		this.triggerTemplateID = triggerTemplateID;
		this.triggerTemplateFactorList = triggerFactorList;
		this.triggerTemplateReactList = triggerReactList;
	}
	
	public  TriggerTemplate fromJsom (JSONObject triggerTemplateJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TriggerTemplate trigger= new TriggerTemplate();
		try {
			trigger.triggerTemplateID=triggerTemplateJson.getInt("triggerTemplateID");
			JSONArray factorListJSON= triggerTemplateJson.getJSONArray("factorList");
			List<TriggerTemplateFactor> factorList = new ArrayList<TriggerTemplateFactor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				TriggerTemplateFactor factor= new TriggerTemplateFactor();
				factor=TriggerTemplateFactor.fromJson(factorJson);
				factorList.add(factor);		
			}		
			trigger.setTriggerTemplateFactorList(factorList);
			
			JSONArray reactListJSON= triggerTemplateJson.getJSONArray("reactList");
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
    		triggerJson.put("triggerTemplateID", getTriggerTemplateID());  
         	for (TriggerTemplateFactor factor:getTriggerTemplateFactorList()) {
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
		for (TriggerTemplateFactor ft:this.triggerTemplateFactorList) {
			String sql="insert into "+triggerFactorTable
					+" (regularid  ,"     
					+"logicalrelation,"
					+"roomtype ,"
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
					+this.triggerTemplateID+",'"
					+ft.getLogicalRelation()+"',"
					+ft.getRoomType()+","
					+ft.getFactorID()+","
					+ft.getOperator()+","
					+ft.getMinValue()+","
					+ft.getMaxValue()+","
					+ft.getAccumilateTime()+","
					+ft.getValidFlag()+",'"
					+sdf.format(ft.getCreateTime())+"','"
					+sdf.format(ft.getModifyTime())
					+"');";
			System.out.println(sql);
			int count=mysql.query(sql);
			if(count>0) System.out.println("insert success"); 	
		}			
	
		for (TriggerTemplateReact react:this.triggerTemplateReactList) {
		String sql2="insert into "+triggerReactTable
				+" (regularid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "
				+ ")"				
				+"values "
				+ "("
				+this.triggerTemplateID+","	
				+react.getReactType()+","	
				+react.getTargetID()+","
				+react.getReactWay()
				+");";
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
	public	static TriggerTemplate getFromDB(MySqlClass mysql,int regularid)
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String sql="select "
					+"regularid  ,"     
					+"logicalrelation ,"
					+"roomtype ,"
					+"factorid ,"
					+"operater ,"
					+"min ,"
					+"max ,"
					+"accumilatetime   ,"
					+"validflag, "
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+triggerFactorTable
					+" where regularid="+regularid
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null || res=="" ) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			TriggerTemplate triggert=new TriggerTemplate();
			List<TriggerTemplateFactor> factorList=new ArrayList<TriggerTemplateFactor>();
			TriggerTemplateFactor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){			
					ft=new TriggerTemplateFactor();	
					ft.setLogicalRelation(cells[1]);
					ft.setRoomType(Integer.parseInt(cells[2]));
					ft.setFactorID(Integer.parseInt(cells[3]));
					ft.setOperator(Integer.parseInt(cells[4]));
					ft.setMinValue(Integer.parseInt(cells[5]));
					ft.setMaxValue(Integer.parseInt(cells[6]));
					ft.setAccumilateTime(Integer.parseInt(cells[7]));
					ft.setValidFlag(Integer.parseInt(cells[8]));
					try {
						ft.setCreateTime(sdf.parse(cells[9]));
						ft.setModifyTime(sdf.parse(cells[10]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					factorList.add(ft);
					triggert.setTriggerTemplateFactorList(factorList);
					triggert.setTriggerTemplateID(Integer.parseInt(cells[0]));
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ triggerFactorTable);
					return null;				
				}
			}			
			
			List<TriggerTemplateReact> triggerReactList=new ArrayList<TriggerTemplateReact>();
			TriggerTemplateReact react=null;
			String sql2="select  "
					+" regularid ," 
					+" reacttype ," 
					+"targetid ,"
					+"reactway "	
					+ " from  "	
					+triggerReactTable
					+" where regularid="+regularid
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
				react.setReactType(Integer.parseInt(array[1]));
				react.setTargetID(Integer.parseInt(array[2]));
				react.setReactWay(Integer.parseInt(array[3]));
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
	
	
	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		
		TriggerTemplate t=getFromDB(mysql, 10005);
		t.saveToDB(mysql);

	}

}
