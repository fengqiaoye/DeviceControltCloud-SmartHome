package cooxm.devicecontrol.device;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：28 Jan 2015 14:24:17 
 * 触发规则存数据库时分三部分：
 *  1.头部，索引
 *  2.触发规则详情；
 *  3.响应方式详情
 */

import java.awt.print.Printable;
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

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.util.MySqlClass;

public class TriggerTemplate {

	//private int ctrolID;
	private int triggerTemplateID;
	
	/** <pre>规则生效所依赖的情景模式，如果在任意模式下生效，则为254。ID如下：
	1	睡眠模式
	2	观影模式
	3	离家模式
	4	居家模式
	254  任意情景模式
    */
	private int profileTemplateID;
	
	/**显示中控上的触发名字 */
	private String triggerName;
	private String description;
	
	/**是否抽象，1.显示中控->设置->功能设置； 0.在情景模式设置中. */
	private int isAbstract;
	
	private List<TriggerTemplateFactor>  triggerTemplateFactorList;
	private List<TriggerTemplateReact>   triggerTemplateReactList;
	
	private Date	  createTime   ;
	private Date	  modifyTime   ;
	

	static String triggerFactorTable="cfg_trigger_template";
	static String triggerHeaderTable="cfg_trigger_template_header";
	static String triggerReactTable ="cfg_trigger_template_react";
	

	public int getTriggerTemplateID() {
		return triggerTemplateID;
	}
	public void setTriggerTemplateID(int triggerTemplateID) {
		this.triggerTemplateID = triggerTemplateID;
	}
	public int getProfileTemplateID() {
		return profileTemplateID;
	}
	public void setProfileTemplateID(int profileTemplateID) {
		this.profileTemplateID = profileTemplateID;
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
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public TriggerTemplate(){}
	
	public TriggerTemplate(TriggerTemplate trigger) {
		this.triggerTemplateID = trigger.triggerTemplateID;
		this.profileTemplateID =  trigger.profileTemplateID;
		this.triggerName =  trigger.triggerName;
		this.description =  trigger.description;
		this.isAbstract =  trigger.isAbstract;
		this.triggerTemplateFactorList =  trigger.triggerTemplateFactorList;
		this.triggerTemplateReactList =  trigger.triggerTemplateReactList;
		this.createTime =  trigger.createTime;
		this.modifyTime =  trigger.modifyTime;
	}
	
	public TriggerTemplate(int triggerTemplateID, int profileTemplateID,
			String triggerName, String description, int isAbstract,
			List<TriggerTemplateFactor> triggerTemplateFactorList,
			List<TriggerTemplateReact> triggerTemplateReactList,
			Date createTime, Date modifyTime) {
		this.triggerTemplateID = triggerTemplateID;
		this.profileTemplateID = profileTemplateID;
		this.triggerName = triggerName;
		this.description = description;
		this.isAbstract = isAbstract;
		this.triggerTemplateFactorList = triggerTemplateFactorList;
		this.triggerTemplateReactList = triggerTemplateReactList;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}
	
	/**用json初始化TriggerTemplate 索引信息 */
	public TriggerTemplate  initTriggerTemplateHeader (JSONObject triggerTemplateJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TriggerTemplate trigger= new TriggerTemplate();
		try {
			trigger.triggerTemplateID=triggerTemplateJson.getInt("triggerTemplateID");
			trigger.profileTemplateID=triggerTemplateJson.getInt("profileTemplateID");
			trigger.triggerName=triggerTemplateJson.getString("tiggerName");
			trigger.description=triggerTemplateJson.getString("description");
			trigger.isAbstract=triggerTemplateJson.getInt("isAbstract");
			trigger.setCreateTime(sdf.parse(triggerTemplateJson.getString("createTime")));
			trigger.setModifyTime(sdf.parse(triggerTemplateJson.getString("modifyTime")) );
			
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
    	JSONObject factorJson;
		try {
    		triggerJson.put("triggerTemplateID", getTriggerTemplateID());  
    		triggerJson.put("profileTemplateID", getProfileTemplateID());
    		triggerJson.put("triggerName", getTriggerName());
    		triggerJson.put("description", getDescription());
    		triggerJson.put("isAbstract", getIsAbstract());
    		triggerJson.put("createTime", sdf.format(getCreateTime()));
    		triggerJson.put("modifyTime", sdf.format(getModifyTime()));  	
        	
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return triggerJson;		
	}
	
	public  TriggerTemplate (JSONObject triggerTemplateJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//TriggerTemplate trigger= new TriggerTemplate();
		try {
			this.triggerTemplateID=triggerTemplateJson.getInt("triggerTemplateID");
			this.profileTemplateID=triggerTemplateJson.getInt("profileTemplateID");
			this.triggerName=triggerTemplateJson.getString("tiggerName");
			this.description=triggerTemplateJson.getString("description");
			this.isAbstract=triggerTemplateJson.getInt("isAbstract");
			this.setCreateTime(sdf.parse(triggerTemplateJson.getString("createTime")));
			this.setModifyTime(sdf.parse(triggerTemplateJson.getString("modifyTime")) );
			
			JSONArray factorListJSON= triggerTemplateJson.getJSONArray("factorList");
			List<TriggerTemplateFactor> factorList = new ArrayList<TriggerTemplateFactor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				TriggerTemplateFactor factor= new TriggerTemplateFactor();
				factor=TriggerTemplateFactor.fromJson(factorJson);
				factorList.add(factor);		
			}		
			this.setTriggerTemplateFactorList(factorList);
			
			JSONArray reactListJSON= triggerTemplateJson.getJSONArray("reactList");
			List<TriggerTemplateReact> reactList = new ArrayList<TriggerTemplateReact>() ;
			for(int i=0;i<reactListJSON.length();i++){
				JSONObject reactJson=reactListJSON.getJSONObject(i);
				TriggerTemplateReact react= new TriggerTemplateReact();
				react=TriggerTemplateReact.fromJson(reactJson);
				reactList.add(react);		
			}		
			this.setTriggerTemplateReactList(reactList);			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJson() {		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject triggerJson=new JSONObject();
    	JSONObject factorJson;
		try {
    		triggerJson.put("triggerTemplateID", getTriggerTemplateID());  
    		triggerJson.put("profileTemplateID", getProfileTemplateID());
    		triggerJson.put("triggerName", getTriggerName());
    		triggerJson.put("description", getDescription());
    		triggerJson.put("isAbstract", getIsAbstract());
    		triggerJson.put("createTime", sdf.format(getCreateTime()));
    		triggerJson.put("modifyTime", sdf.format(getModifyTime()));
			
    		JSONArray ja=new JSONArray();
         	for (TriggerTemplateFactor factor:getTriggerTemplateFactorList()) {
         		ja.put(factor.toJson());		    				
			}
         	triggerJson.put("factorList",ja);
         	
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
		String sqll="replace into "+triggerHeaderTable
				+" (triggerid  ,"    
				+"sttemplateid ,"
				+"triggername ,"
				+"description ,"
				+"isabstract,"
				+"createtime, "
				+"modifytime "
				+ ")"				
				+"values "
				+ "("
				+this.triggerTemplateID+","
				+this.profileTemplateID+",'"
				+this.triggerName+"','"
				+this.description+"',"
				+this.isAbstract+",'"
				+sdf.format(this.getCreateTime())+"','"
				+sdf.format(this.getModifyTime())
				+"');";
		int count=mysql.query(sqll);
		
		String [] sql=new String[this.triggerTemplateFactorList.size()];
		int i=0;
		for (TriggerTemplateFactor ft:this.triggerTemplateFactorList) {
			 sql[i]="replace into "+triggerFactorTable
					+" (triggerid  ,"    
					+"sttemplateid ,"
					+"logicalrelation,"
					+"roomtype ,"
					+"factorid ,"
					+"operator ,"
					+"min ,"
					+"max ,"
					+"accumilatetime   ,"
					//+"isabstract, "
					+"createtime, "
					+"modifytime "
					+ ")"				
					+"values "
					+ "("
					+this.triggerTemplateID+","
					+this.profileTemplateID+",'"
					+ft.getLogicalRelation()+"',"
					+ft.getRoomType()+","
					+ft.getFactorID()+","
					+ft.getOperator()+","
					+ft.getMinValue()+","
					+ft.getMaxValue()+","
					+ft.getAccumilateTime()+",'"
					//+ft.getIsAbstract()+",'"
					+sdf.format(ft.getCreateTime())+"','"
					+sdf.format(ft.getModifyTime())
					+"');";
			System.out.println(sql[i]);
			count=mysql.query(sql[i]);
			i++;
			//if(count>0) System.out.println("insert success"); 	
		}			
	
		for (TriggerTemplateReact react:this.triggerTemplateReactList) {
		String sql2="replace into "+triggerReactTable
				+" (triggerid ," 
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
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static TriggerTemplate getFromDB(MySqlClass mysql,int triggerid)
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			TriggerTemplate triggert=new TriggerTemplate();
			String sql0="select  "
					+" triggerid ," 
					+" sttemplateid ," 
					+"triggername ,"
					+"description, "	
					+"isabstract ,"
					+"createtime,"
					+"modifytime"
					+ " from  "	
					+triggerHeaderTable
					+" where triggerid="+triggerid
					+ ";";
			//System.out.println("query:"+sql0);
			String res0=mysql.select(sql0);
			String[] resArray0=res0.split(",");
			triggert.triggerTemplateID=Integer.parseInt(resArray0[0]);
			triggert.profileTemplateID=Integer.parseInt(resArray0[1]);
			triggert.triggerName=resArray0[2];
			triggert.description=resArray0[3];
			triggert.isAbstract=Integer.parseInt(resArray0[4]);
			try {
				triggert.createTime=sdf.parse(resArray0[5]);
				triggert.modifyTime=sdf.parse(resArray0[6]);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}			
			
			String sql="select "
					+"triggerid  ,"  
					+"sttemplateid ,"
					+"logicalrelation ,"
					+"roomtype ,"
					+"factorid ,"
					+"operator ,"
					+"min ,"
					+"max ,"
					+"accumilatetime   ,"
					+"isabstract, "
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+triggerFactorTable
					+" where triggerid="+triggerid
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null || res=="" ) {
				//System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");

			List<TriggerTemplateFactor> factorList=new ArrayList<TriggerTemplateFactor>();
			TriggerTemplateFactor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){			
					ft=new TriggerTemplateFactor();	
					ft.setLogicalRelation(cells[2]);
					ft.setRoomType(Integer.parseInt(cells[3]));
					ft.setFactorID(Integer.parseInt(cells[4]));
					ft.setOperator(Integer.parseInt(cells[5]));
					ft.setMinValue(Integer.parseInt(cells[6]));
					ft.setMaxValue(Integer.parseInt(cells[7]));
					ft.setAccumilateTime(Integer.parseInt(cells[8]));
					//ft.setIsAbstract(Integer.parseInt(cells[9]));
					try {
						ft.setCreateTime(sdf.parse(cells[10]));
						ft.setModifyTime(sdf.parse(cells[11]));
					} catch (ParseException e) {
						e.printStackTrace();
					}					
					factorList.add(ft);
					
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ triggerFactorTable);
					return null;				
				}
			}
			triggert.setTriggerTemplateFactorList(factorList);
			triggert.setTriggerTemplateID(Integer.parseInt(cells[0]));
			triggert.setProfileTemplateID(Integer.parseInt(cells[1]));
			
			List<TriggerTemplateReact> triggerReactList=new ArrayList<TriggerTemplateReact>();
			TriggerTemplateReact react=null;
			String sql2="select  "
					+" triggerid ," 
					+" reacttype ," 
					+"targetid ,"
					+"reactway "	
					+ " from  "	
					+triggerReactTable
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
	
	public void print(){
		System.out.println("TriggerID:"+triggerTemplateID+",profileTemplateID="+profileTemplateID);
		for ( TriggerTemplateFactor a: this.triggerTemplateFactorList) {
			System.out.println("    factorID="+a.getFactorID()+",logical="+a.getLogicalRelation()+",roomType="+a.getRoomType()
					+",operator="+a.getOperator()+",min="+a.getMinValue()+",max="+a.getMaxValue());
		}		
	}
	
	
	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");		
		TriggerTemplate t=getFromDB(mysql, 107);
		t.triggerTemplateID++;
		t.saveToDB(mysql);

	}

}
