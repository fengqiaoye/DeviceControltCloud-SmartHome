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
	
	/** 情景模板ID，表示这个触发规则只在改情景模式下生效 */
	private int profileID;
	
	/**显示中控上的触发名字 */
	private String triggerName;
	private String description;
	
	/**是否抽象，1.显示中控->设置->功能设置； 0.在情景模式设置中. */
	private int isAbstract;
   /**有效标记，1.有效；  0 无效 */
	private int validFlag;
	
	private Date	  createTime   ;
	private Date	  modifyTime   ;
	
	private List<TriggerFactor>  triggerFactorList;
	private List<TriggerTemplateReact>   triggerReactList;
	/** 在一天之内触发的次数*/
    int times;
	
	static String triggerFactorInfoTable="info_trigger";
	static String triggerHeaderInfoTable ="info_trigger_header";
	static String triggerReactInfoTable ="info_trigger_react";
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
	public int getProfileID() {
		return profileID;
	}
	public void setProfileID(int profileID) {
		this.profileID = profileID;
	}
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIsAbstract() {
		return isAbstract;
	}
	public void setIsAbstract(int isAbstract) {
		this.isAbstract = isAbstract;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public List<TriggerFactor> getTriggerFactorList() {
		return triggerFactorList;
	}
	public void setTriggerFactorList(List<TriggerFactor> triggerFactorList) {
		this.triggerFactorList = triggerFactorList;
	}
	public List<TriggerTemplateReact> getTriggerReactList() {
		return triggerReactList;
	}
	public void setTriggerReactList(List<TriggerTemplateReact> triggerReactList) {
		this.triggerReactList = triggerReactList;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public int getValidFlag() {
		return validFlag;
	}
	public void setValidFlag(int validFlag) {
		this.validFlag = validFlag;
	}
	
	public Date getModifyTime() {
		return modifyTime;
	}
	public Trigger(){}

	public Trigger(Trigger trigger) {
		this.ctrolID = trigger.ctrolID;
		this.triggerID = trigger.triggerID;
		this.profileID = trigger.profileID;
		this.triggerName = trigger.triggerName;
		this.description = trigger.description;
		this.isAbstract = trigger.isAbstract;
		this.validFlag = trigger.validFlag;
		this.createTime = trigger.createTime;
		this.modifyTime = trigger.modifyTime;
		this.triggerFactorList = trigger.triggerFactorList;
		this.triggerReactList = trigger.triggerReactList;
	}

	

	public Trigger(int ctrolID, int triggerID, int profileID,
			String triggerName, String description, int isAbstract,
			int validFlag, Date createTime, Date modifyTime,
			List<TriggerFactor> triggerFactorList,
			List<TriggerTemplateReact> triggerReactList) {
		this.ctrolID = ctrolID;
		this.triggerID = triggerID;
		this.profileID = profileID;
		this.triggerName = triggerName;
		this.description = description;
		this.isAbstract = isAbstract;
		this.validFlag = validFlag;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
		this.triggerFactorList = triggerFactorList;
		this.triggerReactList = triggerReactList;
	}
	
	
	public int saveHeaderToDB(MySqlClass mysql) {		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql0="replace into "+triggerHeaderInfoTable
				+" ("
				+ "ctr_id ," 
				+ "triggerid ,"     
				+"sttemplateid ,"
				+"triggername ,"
				+"description ,"
				+"isabstract,"
				+"valid_flag, "
				+"createtime, "
				+"modifytime "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+","
				+this.triggerID+","
				+this.profileID+",'"
				+this.triggerName+"','"
				+this.description+"',"
				+this.isAbstract+","
				+this.getValidFlag()+",'"
				+sdf.format(this.getCreateTime())+"','"
				+sdf.format(this.getModifyTime())
				+"');";
		//System.out.println(sql0);
		int count0=mysql.query(sql0);
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return 1;
	}
	
	/*** 
	 * Save  to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
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
		String sql0="replace into "+triggerHeaderInfoTable
				+" ("
				+ "ctr_id ," 
				+ "triggerid ,"     
				+"sttemplateid ,"
				+"triggername ,"
				+"description ,"
				+"isabstract,"
				+"validflag, "
				+"createtime, "
				+"modifytime "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+","
				+this.triggerID+","
				+this.profileID+","
				+this.triggerName+"','"
				+this.description+"',"
				+this.isAbstract+
				+this.getValidFlag()+",'"
				+sdf.format(this.getCreateTime())+"','"
				+sdf.format(this.getModifyTime())
				+"')";
		int count0=mysql.query(sql0);
		
		for (TriggerFactor ft:this.triggerFactorList) {
			String sql="replace into "+triggerFactorInfoTable
					+" ("
					+ "ctrolid ," 
					+ "triggerid ,"     
					+"logicalrelation ,"
					+"roomtype ,"
					+"roomid ,"
					+"factorid ,"
					+"operator ,"
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
			//System.out.println(sql);
			int count=mysql.query(sql);
			//if(count>0) System.out.println("insert success"); 
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
		}			
	
		for (TriggerTemplateReact react:this.triggerReactList) {
		String sql2="replace into "+triggerReactInfoTable
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
			try {
				mysql.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}		
		return 1;	
	}
	
	public	static Trigger getHeaderFromDB(MySqlClass mysql,int ctrolID,int triggerid)
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Trigger trigger=new Trigger();
		String sql="select "
				+"ctr_id  ,"    			
				+"triggerid  ," 
				+"sttemplateid ," 
				+"triggername  ," 
				+"description  ,"
				+"isabstract   ," 
				+"valid_flag   ,"
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+triggerHeaderInfoTable
				+" where triggerid="+triggerid
				+" and ctr_id="+ctrolID
				+ ";";
		System.out.println(sql);
		String res=mysql.select(sql);
		if(res==null ||res==""){
			return null;
		}
		String[] cells=res.split(",");
		trigger.setCtrolID(Integer.parseInt(cells[0]));	
		trigger.setTriggerID(Integer.parseInt(cells[1]));	
		trigger.setProfileID(Integer.parseInt(cells[2]));	
		trigger.setTriggerName(cells[3]);	
		trigger.setDescription(cells[4]);
		trigger.setIsAbstract(Integer.parseInt(cells[5]));
		trigger.setValidFlag(Integer.parseInt(cells[6]));
		try {
			trigger.setCreateTime(sdf.parse(cells[7]));
			trigger.setModifyTime(sdf.parse(cells[8]));
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		return trigger;
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
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
					+"operator ,"
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
			triggert.setTriggerReactList(triggerReactList);
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
		String sql="delete  "
				+ "  from  "				
				+triggerFactorInfoTable
				+" where ctrolid="+ctrolID
				+" and triggerid="+triggerID
				+ ";";
		int res=mysql.query(sql);
		//System.out.println("deleted "+ res + " rows of records from table:"+triggerFactorInfoTable);
		if(res<=0 ) {
			System.err.println("ERROR: empty result: "+sql);
			return 0;
		}
		
		String sql2="delete   "
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
	
	public  Trigger(JSONObject TriggerJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		//Trigger trigger=new Trigger();
		try {
			this.triggerID=TriggerJson.getInt("triggerID");
			this.ctrolID=TriggerJson.getInt("ctrolID");
			this.profileID=TriggerJson.getInt("profileID");
			this.triggerName=TriggerJson.getString("triggerName");
			this.description=TriggerJson.getString("description");
			this.isAbstract=TriggerJson.getInt("isAbstract");
			this.validFlag=TriggerJson.getInt("validflag");
			try {
				this.setCreateTime(sdf.parse(TriggerJson.getString("createTime")));
				this.setModifyTime(sdf.parse(TriggerJson.getString("modifyTime")) );
			} catch (ParseException e) {
				e.printStackTrace();
			}

			
			JSONArray factorListJSON= TriggerJson.getJSONArray("factorList");
			List<TriggerFactor> factorList = new ArrayList<TriggerFactor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				TriggerFactor factor= new TriggerFactor();
				factor=TriggerFactor.fromJson(factorJson);
				factorList.add(factor);		
			}		
			this.setTriggerFactorList(factorList);
			
			JSONArray reactListJSON= TriggerJson.getJSONArray("reactList");
			List<TriggerTemplateReact> reactList = new ArrayList<TriggerTemplateReact>() ;
			for(int i=0;i<reactListJSON.length();i++){
				JSONObject reactJson=reactListJSON.getJSONObject(i);
				TriggerTemplateReact react= new TriggerTemplateReact();
				react=TriggerTemplateReact.fromJson(reactJson);
				reactList.add(react);		
			}		
			this.setTriggerReactList(reactList);		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//return trigger;
	}
	
	public JSONObject toJson() {		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject triggerJson=new JSONObject();
    	JSONObject factorJson;
		try {
			triggerJson.put("ctrolID", getCtrolID()); 
    		triggerJson.put("triggerID", getTriggerID());
    		triggerJson.put("profileID", getProfileID());
    		triggerJson.put("triggerName", getTriggerName());
    		triggerJson.put("description", getDescription());
    		triggerJson.put("isAbstract", getIsAbstract());
    		triggerJson.put("validFlag", getValidFlag());
    		triggerJson.put("createTime", sdf.format(getCreateTime()));
    		triggerJson.put("modifyTime", sdf.format(getModifyTime()));
    		
    		JSONArray ja=new JSONArray();
         	for (TriggerFactor factor:getTriggerFactorList()) {
		    	//factorJson= new JSONObject(); 
		    		
		    	ja.put(factor.toJson());
		    	//triggerJson.accumulate("factorList",factorJson);			
			}
         	triggerJson.put("factorList",ja);	
         	
         	JSONArray jb=new JSONArray();
         	for (TriggerTemplateReact react:getTriggerReactList()) {
		    	//factorJson= new JSONObject(); 
		    	jb.put(react.toJson());
		    	//triggerJson.accumulate("reactList",factorJson);			
			}     	
         	triggerJson.put("reactList",jb);	
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return triggerJson;		
	}
	
	/**用json初始化Trigger 索引信息 */
	public static Trigger  initTriggerHeader (JSONObject json){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Trigger trigger= new Trigger();
		try {
			trigger.ctrolID=json.getInt("ctrolID");
			trigger.triggerID=json.getInt("triggerID");
			trigger.profileID=json.getInt("profileID");
			trigger.triggerName=json.getString("triggerName");
			trigger.description=json.getString("description");
			trigger.isAbstract=json.getInt("isAbstract");
			trigger.validFlag=json.getInt("validFlag");
			trigger.setCreateTime(sdf.parse(json.getString("createTime")));
			trigger.setModifyTime(sdf.parse(json.getString("modifyTime")) );
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return trigger;
	}
	/**TriggerTemplate 头部信息打包成json */
	public JSONObject toJsonHeader() {		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject triggerJson=new JSONObject();
		try {
			triggerJson.put("ctrolID", getCtrolID());  
    		triggerJson.put("triggerID", getTriggerID());  
    		triggerJson.put("profileID", getProfileID());
    		triggerJson.put("triggerName", getTriggerName());
    		triggerJson.put("description", getDescription());
    		triggerJson.put("isAbstract", getIsAbstract());
    		triggerJson.put("validFlag", getValidFlag());
    		triggerJson.put("createTime", sdf.format(getCreateTime()));
    		triggerJson.put("modifyTime", sdf.format(getModifyTime()));  	
        	
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return triggerJson;		
	}
	
	/*public Date getModifyTime() {
		Date modifyTime=new Date(0);
     	for (TriggerFactor factor:getTriggerFactorList()) {
		   Date  factorModifyTime=factor.getModifyTime();
		   if(factorModifyTime.after(modifyTime)){
			   modifyTime=factorModifyTime;
		   }
		}		
		return modifyTime;
	}*/
	

	
	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
		
//		Trigger T=getFromDB(mysql, 1234567,1025);
//		T.saveToDB(mysql);
//		
//		deleteFromDB(mysql, 1234567, 1025);
		
		Trigger T=getHeaderFromDB(mysql, 40004,102);
		
		T.saveHeaderToDB(mysql);

	}


}
