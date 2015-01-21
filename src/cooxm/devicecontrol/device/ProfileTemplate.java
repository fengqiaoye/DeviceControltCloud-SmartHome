package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：20 Jan 2015 09:30:51 
 */

public class ProfileTemplate {
	int	   profileTemplateID         ;
	String profileTemplateName       ;
	List<ProfileTemplatFactor> factorList;
	int createOperator;
	int modifyOperator;
	Date createTime;
	Date modifyTime;
	
	private static final String profileTemplatDetailTable="cfg_sttemplate_factor";
	private static final String profileTemplatIndexTable="cfg_sttemplate";	
	
	public String getProfileTemplateName() {
		return profileTemplateName;
	}

	public void setProfileTemplateName(String profileTemplateName) {
		this.profileTemplateName = profileTemplateName;
	}

	public int getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(int createOperator) {
		this.createOperator = createOperator;
	}

	public int getmodifyOperator() {
		return modifyOperator;
	}

	public void setmodifyOperator(int modifyOperator) {
		this.modifyOperator = modifyOperator;
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

	public int getProfileSetTempID() {
		return profileTemplateID;
	}

	public void setProfileSetTempID(int profileSetTempID) {
		this.profileTemplateID = profileSetTempID;
	}

	public List<ProfileTemplatFactor> getFactorList() {
		return factorList;
	}

	public void setFactorList(List<ProfileTemplatFactor> factorList) {
		this.factorList = factorList;
	}
	
	public ProfileTemplate(int profileTemplateID, String profileTemplateName,
			List<ProfileTemplatFactor> factorList, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime) {
		super();
		this.profileTemplateID = profileTemplateID;
		this.profileTemplateName = profileTemplateName;
		this.factorList = factorList;
		this.createOperator = createOperator;
		this.modifyOperator = modifyOperator;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	public ProfileTemplate() {
	}
	
	public ProfileTemplate(JSONObject profileTemplateJson) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.profileTemplateID=profileTemplateJson.getInt("profileTemplateID");
			this.profileTemplateName=profileTemplateJson.getString("profileTemplateName");
			JSONArray factorListJSON= profileTemplateJson.getJSONArray("factorList");
			List<ProfileTemplatFactor> factorList = new ArrayList<ProfileTemplatFactor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				ProfileTemplatFactor factor= new ProfileTemplatFactor();
				factor.factorID=factorJson.getInt("factorID");
				factor.spaceRange=factorJson.getInt("spaceRange");			
				factor.minValue=factorJson.getInt("minValue");
				factor.maxValue=factorJson.getInt("maxValue");
				factor.compareWay=factorJson.getInt("compareWay");
				factor.validFlag=factorJson.getInt("validFlag");
				factor.createTime=sdf.parse(factorJson.getString("createTime"));
				factor.modifyTime=sdf.parse(factorJson.getString("modifyTime"));	
				factorList.add(factor);		
			}		
			this.factorList=factorList;
			this.createTime=sdf.parse(profileTemplateJson.getString("createTime"));
			this.modifyTime=sdf.parse(profileTemplateJson.getString("createTime"));	
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
	}

	/*** 
	 * Save ProfileSetTemplat info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "xxx", "xxx");
	 * @table profileDetailTable :  cfg_sttemplate_factor
	 * @throws SQLException 
	 * */
	public  int saveToDB(MySqlClass mysql) throws SQLException{
		if(null==this.factorList){
			System.err.println("Error: save to db failed, make sure the target object is not empty!");
			return -1;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int resultCount=0;
		mysql.conn.setAutoCommit(false);		
		for (ProfileTemplatFactor factor:this.factorList) {
		String sql="insert into "+profileTemplatDetailTable
				+" ("
				+ "sttemplateid ," 
				+" factorid     ," 
				+"spacerange   ,"
				+"lower  ,"
				+"upper  ,"
				+"cmpalg ,"
				+"valid_flag ,"
				+"createoperator ,"
				+"modifyoperator ,"					
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ ")"
				+"values "
				+ "("
				+this.profileTemplateID+","	
				+factor.factorID+","	
				+factor.spaceRange+","
				+factor.minValue+","
				+factor.maxValue+","
				+factor.compareWay+","
				+factor.validFlag+",'"
			    +factor.createOperator+"','"
			    +factor.modifyOperator+"','"												
				+sdf.format(factor.createTime)+"','"
				+sdf.format(factor.modifyTime)
				+"')";
		System.out.println(sql);		
		mysql.query(sql);
		
		String sql2="insert into   "
				+ profileTemplatIndexTable
				+"("
				+" templateid       ,"
				+"name,"
				+"createoperator  ,"
				+"modifyoperator  ,"			
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ ")  values "				
				+this.profileTemplateID   +","
				+this.profileTemplateName +","
				+this.createOperator+","
				+this.modifyOperator+",'"
				+this.createTime+"','"
				+this.modifyTime+"'"
				+ ";";
		System.out.println("query:"+sql2);
		resultCount+=mysql.query(sql2);
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
	public	static ProfileTemplate getFromDB(MySqlClass mysql,int profileTemplateID) throws SQLException
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
			String sql="select "
					+" sttemplateid ," 
					+" factorid     ," 
					+"spacerange   ,"
					+"lower  ,"
					+"upper  ,"
					+"cmpalg ,"
					+"valid_flag ,"
					+"createoperator ,"
					+"modifyoperator ,"					
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileTemplatDetailTable
					+" where sttemplateid="+profileTemplateID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==""||res.length()==0) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			ProfileTemplate profileTemp=new ProfileTemplate();
			List<ProfileTemplatFactor> factorList=new ArrayList<ProfileTemplatFactor>();
			ProfileTemplatFactor factor=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				factor=new ProfileTemplatFactor();				
				factor.factorID=Integer.parseInt(cells[1]);
				factor.spaceRange=Integer.parseInt(cells[2]);
				factor.minValue=Integer.parseInt(cells[3]);
				factor.maxValue=Integer.parseInt(cells[4]);
				factor.compareWay=Integer.parseInt(cells[5]);
				factor.validFlag=Integer.parseInt(cells[6]);
				factor.createOperator=Integer.parseInt(cells[7]);
				factor.createOperator=Integer.parseInt(cells[8]);
				try {
					factor.createTime=sdf.parse(cells[9]);
					factor.modifyTime=sdf.parse(cells[10]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				factorList.add(factor);
				profileTemp.profileTemplateID=Integer.parseInt(cells[0]);
				
				
				String sql2="select  "
						+" templateid       ,"
						+"name,"
						+"createoperator  ,"
						+"modifyoperator  ,"			
						+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
						+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
						+ "  from "				
						+profileTemplatIndexTable
						+" where templateid="+profileTemplateID
						+ ";";
				System.out.println("query:"+sql2);
				String res2=mysql.select(sql2);
				System.out.println("get from mysql:\n"+res2);
				String[] cells2=res2.split("\n");
				profileTemp.setProfileTemplateName(cells2[1]);
				profileTemp.setCreateOperator(Integer.parseInt(cells2[2]));
				profileTemp.setmodifyOperator(Integer.parseInt(cells2[3]));
				profileTemp.setProfileTemplateName(cells2[4]);
				profileTemp.setProfileTemplateName(cells2[5]);						
			}
			mysql.conn.commit();
			return profileTemp;
		}
	


	public JSONObject toJsonObj() {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileTemplateJson = new JSONObject();  
        JSONObject factorJson ; //= new JSONObject();  
        try {
        	profileTemplateJson.put("profileTemplateID",this.profileTemplateID);
		    for(ProfileTemplatFactor factor: this.factorList){
		    	factorJson= new JSONObject(); 
		    	factorJson.put("factorID", factor.factorID);
		    	factorJson.put("spaceRange", factor.spaceRange);
		    	factorJson.put("minValue", factor.minValue);
		    	factorJson.put("maxValue", factor.maxValue);
		    	factorJson.put("compareWay", factor.compareWay);
		    	factorJson.put("validFlag", factor.validFlag);
		    	factorJson.put("createOperator", factor.createOperator);
		    	factorJson.put("modifyOperator", factor.modifyOperator);
		    	factorJson.put("createTime", sdf.format(factor.createTime));
		    	factorJson.put("modifyTime", sdf.format(factor.modifyTime));
		    	profileTemplateJson.accumulate("factorList",factorJson); 
		    }

        } catch (JSONException e) {
			e.printStackTrace();
		}  		
		return profileTemplateJson;

	}
	
	public static void main(String[] args) throws SQLException, JSONException {
		MySqlClass mysql=new  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		getFromDB(mysql, 1);
		//new ProfileTemplat().saveToDB(mysql);
		
	}

}
