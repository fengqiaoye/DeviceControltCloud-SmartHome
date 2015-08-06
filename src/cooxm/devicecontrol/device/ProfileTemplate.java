package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.log.Log;

import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：20 Jan 2015 09:30:51 
 */

public class ProfileTemplate {
	static Logger log =Logger.getLogger(ProfileTemplate.class);	
	/**
	  睡眠模式
	  离家模式
	  观影模式
   */
	private int	   profileTemplateID         ;
	private String profileTemplateName       ;
	//private int roomType;
	private List<FactorTemplate> factorTempList;
	private int createOperator ;
	private int modifyOperator ;
	private Date createTime;
	private Date modifyTime;

	
	public static final String profileTemplatDetailTable="cfg_sttemplate_factor";
	public static final String profileTemplatIndexTable="cfg_sttemplate";	
	
	
	public int getProfileTemplateID() {
		return profileTemplateID;
	}

	public void setProfileTemplateID(int profileTemplateID) {
		this.profileTemplateID = profileTemplateID;
	}


	public String getProfileTemplateName() {
		return profileTemplateName;
	}

	public void setProfileTemplateName(String profileTemplateName) {
		this.profileTemplateName = profileTemplateName;
	}

	public List<FactorTemplate> getFactorTemplateTempList() {
		return factorTempList;
	}

	public void setFactorTemplateTempList(List<FactorTemplate> factorList) {
		this.factorTempList = factorList;
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
	

	public int getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(int createOperator) {
		this.createOperator = createOperator;
	}

	public int getModifyOperator() {
		return modifyOperator;
	}

	public void setModifyOperator(int modifyOperator) {
		this.modifyOperator = modifyOperator;
	}

	public ProfileTemplate(int profileTemplateID, String profileTemplateName,int roomType,
			List<FactorTemplate> factorList, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime) {
		this.profileTemplateID = profileTemplateID;
		this.profileTemplateName = profileTemplateName;
		this.factorTempList = factorList;
		this.createOperator = createOperator;
		this.modifyOperator = modifyOperator;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}
	
    /**情景模式 */
	public ProfileTemplate(int profileTemplateID, List<FactorTemplate> factorList,
			Date createTime, Date modifyTime) {
		this.profileTemplateID = profileTemplateID;
		this.factorTempList = factorList;
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
			List<FactorTemplate> factorList = new ArrayList<FactorTemplate>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				FactorTemplate factor= new FactorTemplate();
				/*factor.setFactorTemplateID(factorJson.getInt("factorID"));	
				factor.setRoomType(factorJson.getInt("roomType"));
				factor.setMinValue(factorJson.getInt("minValue"));
				factor.setMaxValue(factorJson.getInt("maxValue"));
				factor.setOperator(factorJson.getInt("operator"));
				factor.setValidFlag(factorJson.getInt("validFlag"));
				factor.setCreateTime(sdf.parse(factorJson.getString("createTime")));
				factor.setModifyTime(sdf.parse(factorJson.getString("modifyTime")));*/
				factor=FactorTemplate.fromProfleTemplateJson(factorJson);
				factorList.add(factor);		
			}		
			this.factorTempList=factorList;
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
	public  int saveToDB(MySqlClass mysql) {
		if(null==this.factorTempList){
			System.err.println("Error: save to db failed, make sure the target object is not empty!");
			return -1;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int resultCount=0;
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] sql=new String[factorTempList.size()];
		int i=0;
		for (FactorTemplate factor:this.factorTempList) {
		sql[i]="replace into "+profileTemplatDetailTable
				+" ("
				+ "sttemplateid ," 
				+" factorid     ," 
				+"roomType   ,"
				+"min  ,"
				+"max  ,"
				+"operator ,"
				+"valid_flag ,"
				+"createoperator ,"
				+"modifyoperator ,"					
				+"createtime,"
				+"modifytime"
				+ ")"
				+"values "
				+ "("
				+this.profileTemplateID+","	
				+factor.getFactorID()+","	
				+factor.getRoomType()+","
				+factor.getMinValue()+","
				+factor.getMaxValue()+","
				+factor.getOperator()+","
				+factor.getValidFlag()+","
			    +factor.getCreateOperator()+","
			    +factor.getModifyOperator()+",'"												
				+sdf.format(factor.getCreateTime())+"','"
				+sdf.format(factor.getModifyTime())
				+"')";
		//System.out.println(sql[i]);		
		mysql.query(sql[i]);
		i++;

		}
		String sql2="replace into   "
				+ profileTemplatIndexTable
				+"("
				+" templateid       ,"
				+"name,"
				+"createoperator  ,"
				+"modifyoperator  ,"			
				+"createtime,"
				+"modifytime"
				+ ")  values ("				
				+this.profileTemplateID   +",'"
				+this.profileTemplateName +"',"
				+this.getCreateOperator()+","
				+this.getModifyOperator()+",'"
				+sdf.format(this.getCreateTime())+"','"
				+sdf.format(this.getModifyTime())
				+ "');";
		//System.out.println("query:"+sql2);
		resultCount+=mysql.query(sql2);
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
		return resultCount;	
	}

   /*** 
   * 从入MYSQL读取profile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static ProfileTemplate getFromDB(MySqlClass mysql,int profileTemplateID)
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String sql="select "
					+" sttemplateid ," 
					+" factorid     ," 
					+"roomType   ,"
					+"min  ,"
					+"max  ,"
					+"operator ,"
					+"valid_flag ,"
					+"createoperator ,"
					+"modifyoperator ,"					
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileTemplatDetailTable
					+" where sttemplateid="+profileTemplateID
					+ ";";
			//System.out.println("query:"+sql);
			String res=mysql.select(sql);
			//System.out.println("get from mysql:\n"+res);
			if(res==null||res.equals("")||res.length()==0) {
				//System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			ProfileTemplate profileTemp=new ProfileTemplate();
			List<FactorTemplate> factorList=new ArrayList<FactorTemplate>();
			FactorTemplate factor=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				factor=new FactorTemplate();				
				factor.setFactorID(Integer.parseInt(cells[1]));
				factor.setRoomType(Integer.parseInt(cells[2]));
				factor.setMinValue(Integer.parseInt(cells[3]));
				factor.setMaxValue(Integer.parseInt(cells[4]));
				factor.setOperator(Integer.parseInt(cells[5]));
				factor.setValidFlag(Integer.parseInt(cells[6]));
				factor.setCreateOperator(Integer.parseInt(cells[7]));;
				factor.setModifyOperator(Integer.parseInt(cells[8]));
				try {
					factor.setCreateTime(sdf.parse(cells[9]));
					factor.setModifyTime(sdf.parse(cells[10]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				factorList.add(factor);
				profileTemp.profileTemplateID=Integer.parseInt(cells[0]);
				//profileTemp.roomType=Integer.parseInt(cells[2]);
				
						
			}
			profileTemp.setFactorTemplateTempList(factorList);
			
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
			//System.out.println("get from mysql:\n"+res2);
			String[] cells2=res2.split(",");
			profileTemp.setProfileTemplateName(cells2[1]);
			profileTemp.setCreateOperator(Integer.parseInt(cells2[2]));
			profileTemp.setModifyOperator(Integer.parseInt(cells2[3]));
			try {
				profileTemp.setCreateTime(sdf.parse(cells2[4]));
				profileTemp.setModifyTime(sdf.parse(cells2[5]));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return profileTemp;
		}
	
	   /*** 
	   * 从入MYSQL读取profile模板列表
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_user_room_st_factor
	   * @throws SQLException 
	   */
		public	static Map<Integer,ProfileTemplate> getAllFromDB(MySqlClass mysql) throws SQLException
		{
			Map<Integer,ProfileTemplate> pList=new HashMap<Integer,ProfileTemplate>();				 
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			mysql.conn.setAutoCommit(false);
			String sql2="select  "
					+" templateid       ,"
					+"name,"
					+"createoperator  ,"
					+"modifyoperator  ,"			
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from "				
					+profileTemplatIndexTable
					+" where templateid!=254"
					+ ";";
			//System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			//System.out.println("get from mysql:\n"+res2);
			if(res2==null || res2==""){
				log.error("select result empty,can't  get profile Template list");
				return null;
			}
			String[] line2=res2.split("\n");
			for (int i = 0; i < line2.length; i++) {	
				ProfileTemplate profileTemp=new ProfileTemplate();
				String[] cells2=line2[i].split(",");
				profileTemp.profileTemplateID=Integer.parseInt(cells2[0]);
				profileTemp.setProfileTemplateName(cells2[1]);
				profileTemp.setCreateOperator(Integer.parseInt(cells2[2]));
				profileTemp.setModifyOperator(Integer.parseInt(cells2[3]));
				try {
					profileTemp.setCreateTime(sdf.parse(cells2[4]));
					profileTemp.setModifyTime(sdf.parse(cells2[5]));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}	
				List<FactorTemplate> factorList=new ArrayList<FactorTemplate>();
				profileTemp.setFactorTemplateTempList(factorList);
				pList.put(profileTemp.profileTemplateID, profileTemp);
			}
			
			String sql="select "
					+" sttemplateid ," 
					+" factorid     ," 
					+"roomType   ,"
					+"min  ,"
					+"max  ,"
					+"operator,"
					+"valid_flag,"
					+"createoperator ,"
					+"modifyoperator ,"					
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileTemplatDetailTable
					+ ";";
			String res=mysql.select(sql);

			if(res!=null && res!=""  && res.length()>0){
				String[] resArray=res.split("\n");					
				FactorTemplate factor=null;
				String[] cells=null;
				
				for(String line:resArray){
					cells=line.split(",");
					factor=new FactorTemplate();				
					factor.setFactorID(Integer.parseInt(cells[1]));
					factor.setRoomType(Integer.parseInt(cells[2]));
					factor.setMinValue(Integer.parseInt(cells[3]));
					factor.setMaxValue(Integer.parseInt(cells[4]));
					factor.setOperator(Integer.parseInt(cells[5]));
					factor.setValidFlag(Integer.parseInt(cells[6]));
					factor.setCreateOperator(Integer.parseInt(cells[7]));;
					factor.setModifyOperator(Integer.parseInt(cells[8]));
					try {
						factor.setCreateTime(sdf.parse(cells[9]));
						factor.setModifyTime(sdf.parse(cells[10]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					ProfileTemplate profileTemp2 = pList.get(Integer.parseInt(cells[0]));
					List<FactorTemplate> factorList = profileTemp2.getFactorTemplateTempList();
					factorList.add(factor);
					profileTemp2.setFactorTemplateTempList(factorList);
					pList.put(profileTemp2.profileTemplateID, profileTemp2);
				}	
				
			}
			mysql.conn.commit();
			return pList;
		}
	


	public JSONObject toJsonObj() {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileTemplateJson = new JSONObject();  
        JSONObject factorJson ; //= new JSONObject();  
        try {
        	profileTemplateJson.put("profileTemplateID",this.profileTemplateID);
        	profileTemplateJson.put("profileTemplateName",this.profileTemplateName);
        	profileTemplateJson.put("createOperator", createOperator);
        	profileTemplateJson.put("modifyOperator", modifyOperator);
        	profileTemplateJson.put("createTime", sdf.format(createTime));
        	profileTemplateJson.put("modifyTime", sdf.format(modifyTime));
        	JSONArray ja=new JSONArray();
		    for(FactorTemplate factor: this.factorTempList){
		    	factorJson= new JSONObject(); 
		    	factorJson.put("factorID", factor.getFactorID());
		    	factorJson.put("roomType", factor.getRoomType());
		    	factorJson.put("minValue", factor.getMinValue());
		    	factorJson.put("maxValue", factor.getMaxValue());
		    	factorJson.put("operator", factor.getOperator());
		    	factorJson.put("validFlag", factor.getValidFlag());
		    	factorJson.put("createOperator", factor.getCreateOperator());
		    	factorJson.put("modifyOperator", factor.getModifyOperator());
		    	factorJson.put("createTime", sdf.format(factor.getCreateTime()));
		    	factorJson.put("modifyTime", sdf.format(factor.getModifyTime()));
		    	ja.put(factorJson);
		    }
		    profileTemplateJson.put("factorList",ja);
		    //profileTemplateJson.accumulate("factorList",factorJson); 
        } catch (JSONException e) {
			e.printStackTrace();
		}  		
		return profileTemplateJson;

	}
	
	public static void main(String[] args) throws SQLException, JSONException {
		MySqlClass mysql=new  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
		Map<Integer, ProfileTemplate>a=null;
		for (int i = 0; i < 100; i++) {
			  a = getAllFromDB(mysql);
			//new ProfileTemplat().saveToDB(mysql);
			System.out.println("size="+a.size());
			
			//a.get(0).saveToDB(mysql);
			a=null;
		}

		
	}


}
