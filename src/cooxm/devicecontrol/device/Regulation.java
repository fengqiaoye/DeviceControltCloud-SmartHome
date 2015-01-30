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

public class Regulation {

	private int ctrolID;
	private int regulationID;
	
	private List<RegulationFactor>  regulationFactorList;
	private List<RegulationTemplateReact>   regulationReactList;
	
	private static String regulationFactorInfoTable="info_regular";
	private static String regulationReactInfoTable ="info_regular_react";
	

	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public int getRegulationID() {
		return regulationID;
	}
	public void setRegulationID(int regulationID) {
		this.regulationID = regulationID;
	}
	public List<RegulationFactor> getRegulationFactorList() {
		return regulationFactorList;
	}
	public void setRegulationFactorList(
			List<RegulationFactor> regulationFactorList) {
		this.regulationFactorList = regulationFactorList;
	}
	public List<RegulationTemplateReact> getRegulationTemplateReactList() {
		return regulationReactList;
	}
	public void setRegulationTemplateReactList(
			List<RegulationTemplateReact> regulationReactList) {
		this.regulationReactList = regulationReactList;
	}
	public Regulation(){}
	
	public Regulation(int ctrolID,int regulationID,
			List<RegulationFactor> regulationFactorList,
			List<RegulationTemplateReact> regulationReactList) {
		this.ctrolID=ctrolID;		
		this.regulationID = regulationID;
		this.regulationFactorList = regulationFactorList;
		this.regulationReactList = regulationReactList;
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
		for (RegulationFactor ft:this.regulationFactorList) {
			String sql="insert into "+regulationFactorInfoTable
					+" ("
					+ "ctrolid ," 
					+ "regularid ,"     
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
					+this.regulationID+",'"
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
	
		for (RegulationTemplateReact react:this.regulationReactList) {
		String sql2="insert into "+regulationReactInfoTable
				+" ("
				+ "ctrolid ," 
				+ "regularid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "
				+ ")"				
				+"values "
				+ "("
				+this.ctrolID+","	
				+this.regulationID+","	
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
	public	static Regulation getFromDB(MySqlClass mysql,int ctrolID,int regularid)
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String sql="select "
					+"ctrolid  ,"    			
					+"regularid  ,"     
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
					+regulationFactorInfoTable
					+" where regularid="+regularid
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
			Regulation regulationt=new Regulation();
			List<RegulationFactor> factorList=new ArrayList<RegulationFactor>();
			RegulationFactor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){			
					ft=new RegulationFactor();					
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
					
					factorList.add(ft);
					regulationt.setRegulationFactorList(factorList);
					regulationt.setCtrolID(Integer.parseInt(cells[0]));
					regulationt.setRegulationID(Integer.parseInt(cells[1]));
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ regulationFactorInfoTable);
					return null;				
				}
			}			
			
			List<RegulationTemplateReact> regulationReactList=new ArrayList<RegulationTemplateReact>();
			RegulationTemplateReact react=null;
			String sql2="select  "
					+"ctrolid  ,"    			
     				+" regularid ," 
					+" reacttype ," 
					+"targetid ,"
					+"reactway "	
					+ " from  "	
					+regulationReactInfoTable
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
				//react.setCtrolID(Integer.parseInt(array[0]));
				react.setReactType(Integer.parseInt(array[2]));
				react.setTargetID(Integer.parseInt(array[3]));
				react.setReactWay(Integer.parseInt(array[4]));
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
		
		Regulation T=getFromDB(mysql, 1234567,1025);
		T.saveToDB(mysql);

	}

}
