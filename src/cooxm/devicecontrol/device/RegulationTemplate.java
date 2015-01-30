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

import cooxm.devicecontrol.util.MySqlClass;

public class RegulationTemplate {

	//private int ctrolID;
	private int regulationTemplateID;
	
	private List<RegulationTemplateFactor>  regulationTemplateFactorList;
	private List<RegulationTemplateReact>   regulationTemplateReactList;
	
	private static String regulationFactorTable="cfg_regular_template";
	private static String regulationReactTable="cfg_regular_template_react";
	

	public int getRegulationTemplateID() {
		return regulationTemplateID;
	}
	public void setRegulationTemplateID(int regulationTemplateID) {
		this.regulationTemplateID = regulationTemplateID;
	}
	public List<RegulationTemplateFactor> getRegulationTemplateFactorList() {
		return regulationTemplateFactorList;
	}
	public void setRegulationTemplateFactorList(
			List<RegulationTemplateFactor> regulationTemplateFactorList) {
		this.regulationTemplateFactorList = regulationTemplateFactorList;
	}
	public List<RegulationTemplateReact> getRegulationTemplateReactList() {
		return regulationTemplateReactList;
	}
	public void setRegulationTemplateReactList(
			List<RegulationTemplateReact> regulationTemplateReactList) {
		this.regulationTemplateReactList = regulationTemplateReactList;
	}
	public RegulationTemplate(){}
	
	public RegulationTemplate(int regulationTemplateID,
			List<RegulationTemplateFactor> regulationFactorList,
			List<RegulationTemplateReact> regulationReactList) {
		this.regulationTemplateID = regulationTemplateID;
		this.regulationTemplateFactorList = regulationFactorList;
		this.regulationTemplateReactList = regulationReactList;
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
		for (RegulationTemplateFactor ft:this.regulationTemplateFactorList) {
			String sql="insert into "+regulationFactorTable
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
					+this.regulationTemplateID+",'"
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
	
		for (RegulationTemplateReact react:this.regulationTemplateReactList) {
		String sql2="insert into "+regulationReactTable
				+" (regularid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "
				+ ")"				
				+"values "
				+ "("
				+this.regulationTemplateID+","	
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
	public	static RegulationTemplate getFromDB(MySqlClass mysql,int regularid)
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
					+regulationFactorTable
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
			RegulationTemplate regulationt=new RegulationTemplate();
			List<RegulationTemplateFactor> factorList=new ArrayList<RegulationTemplateFactor>();
			RegulationTemplateFactor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){			
					ft=new RegulationTemplateFactor();	
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
					regulationt.setRegulationTemplateFactorList(factorList);
					regulationt.setRegulationTemplateID(Integer.parseInt(cells[0]));
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ regulationFactorTable);
					return null;				
				}
			}			
			
			List<RegulationTemplateReact> regulationReactList=new ArrayList<RegulationTemplateReact>();
			RegulationTemplateReact react=null;
			String sql2="select  "
					+" regularid ," 
					+" reacttype ," 
					+"targetid ,"
					+"reactway "	
					+ " from  "	
					+regulationReactTable
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
				react=new RegulationTemplateReact();
				react.setReactType(Integer.parseInt(array[1]));
				react.setTargetID(Integer.parseInt(array[2]));
				react.setReactWay(Integer.parseInt(array[3]));
				regulationReactList.add(react);
			}	
			regulationt.setRegulationTemplateReactList(regulationReactList);
			try {
				mysql.conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		 return regulationt;			
	}
	
	
	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		
		RegulationTemplate t=getFromDB(mysql, 10005);
		t.saveToDB(mysql);

	}

}
