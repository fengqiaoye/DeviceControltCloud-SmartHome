package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：20 Jan 2015 09:30:51 
 */

public class ProfileTemplat {
	int	   profileSetTempID         ;
	List<ProfileTemplatFactor> factorList;
	
	private static final String table="cfg_sttemplate_factor";
	
	
	public int getProfileSetTempID() {
		return profileSetTempID;
	}

	public void setProfileSetTempID(int profileSetTempID) {
		this.profileSetTempID = profileSetTempID;
	}

	public List<ProfileTemplatFactor> getFactorList() {
		return factorList;
	}

	public void setFactorList(List<ProfileTemplatFactor> factorList) {
		this.factorList = factorList;
	}
	

	public ProfileTemplat(int profileSetTempID,
			List<ProfileTemplatFactor> factorList) {
		super();
		this.profileSetTempID = profileSetTempID;
		this.factorList = factorList;
	}

	public ProfileTemplat() {
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
		String sql="insert into "+table
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
				+this.profileSetTempID+","	
				+factor.factorID+","	
				+factor.spacerange+","
				+factor.minValue+","
				+factor.maxValue+","
				+factor.compareWay+","
				+factor.validFlag+",'"
			    +factor.createoperator+"','"
			    +factor.modifyoperator+"','"												
				+sdf.format(factor.createTime)+"','"
				+sdf.format(factor.modifyTime)
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
	public	static ProfileTemplat getFromDB(MySqlClass mysql,int profileSetTempID) throws SQLException
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
					+table
					+" where sttemplateid="+profileSetTempID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==""||res.length()==0) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			ProfileTemplat profileTemp=new ProfileTemplat();
			List<ProfileTemplatFactor> factorList=new ArrayList<ProfileTemplatFactor>();
			ProfileTemplatFactor factor=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				factor=new ProfileTemplatFactor();				
				factor.factorID=Integer.parseInt(cells[1]);
				factor.spacerange=Integer.parseInt(cells[2]);
				factor.minValue=Integer.parseInt(cells[3]);
				factor.maxValue=Integer.parseInt(cells[4]);
				factor.compareWay=Integer.parseInt(cells[5]);
				factor.validFlag=Integer.parseInt(cells[6]);
				factor.createoperator=cells[7];
				factor.createoperator=cells[8];
				try {
					factor.createTime=sdf.parse(cells[9]);
					factor.modifyTime=sdf.parse(cells[10]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				factorList.add(factor);
				profileTemp.profileSetTempID=Integer.parseInt(cells[0]);
			}
			mysql.conn.commit();
			return profileTemp;
		}
	
	public static void main(String[] args) throws SQLException, JSONException {
		MySqlClass mysql=new  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		//getFromDB(mysql, 1);
		new ProfileTemplat().saveToDB(mysql);
		
	}

}
