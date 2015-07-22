package cooxm.devicecontrol.synchronize;


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cooxm.devicecontrol.util.MySqlClass;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 27, 2015 5:24:54 PM 
 */
public class IRLibIndex {
	
	List<IRLibFile> iRLibFileList;
	public List<IRLibFile> getiRLibFileList() {
		return iRLibFileList;
	}
	public void setiRLibFileList(List<IRLibFile> iRLibFileList) {
		this.iRLibFileList = iRLibFileList;
	}
	public IRLibIndex(List<IRLibFile> iRLibFileList) {
		this.iRLibFileList = iRLibFileList;
	}
	
	public	static IRLibIndex getFromDB(MySqlClass mysql,String fileName,String path) 
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<IRLibFile> iRLibFileList=new ArrayList<IRLibFile>();
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String sql2="select  "
		+" filename       ,"
		+"path,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S'),"
		+"filesize, "
		+"recordCount "	
		+" from "
		+IRLibFile.iRLibTable
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} else{
			String[] line=res2.split("\n");
			for (int i = 0; i < line.length; i++) {
				String[] index=line[i].split(",");
				IRLibFile irLibFile=new IRLibFile();
				irLibFile.fileName=index[0];	
				irLibFile.path=index[1];
				try {
					irLibFile.setCreateTime(sdf.parse(index[2]));
					irLibFile.setModifyTime(sdf.parse(index[3]));
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				irLibFile.fileSize=Integer.parseInt(index[4]);
				irLibFile.recordCount=Integer.parseInt(index[5]);
				iRLibFileList.add(irLibFile);
			}
			
		}		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}			
		return new IRLibIndex(iRLibFileList);			
	}
	
	public	int saveToDB(MySqlClass mysql) 
	{
		for(IRLibFile ir:this.iRLibFileList){
			ir.saveToDB(mysql);
		}
		return this.iRLibFileList.size();	
	}
	
	
	
	public static void main(String[] args) {

	}

}
