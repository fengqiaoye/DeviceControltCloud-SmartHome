package cooxm.devicecontrol.httpRequest;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cooxm.devicecontrol.device.Factor;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 27, 2015 4:44:37 PM 
 */

public class IRLibFile {
	
	public static final String iRLibTable="dic_irlib_code";
	
	String fileName;
	/** 目录名*/
	String path;
	Date createTime;
	/** 所有文件的更新时间，所有文件的更新时间必须一致*/
	Date modifyTime;
	/** 以字节为单位*/
	int fileSize;
	/** 文件内记录条数*/
	int recordCount;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileNmae) {
		this.fileName = fileNmae;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public IRLibFile(String fileName, String path, Date createTime,
			Date modifyTime, int fileSize, int recordCount) {
		this.fileName = fileName;
		this.path = path;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
		this.fileSize = fileSize;
		this.recordCount = recordCount;
	}
	
	public IRLibFile() {
	}
	
	public	static IRLibFile getFromDB(MySqlClass mysql,String fileName,String path) 
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IRLibFile irLibFile=new IRLibFile();
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
		+iRLibTable
		+" where filename="+fileName
		+" and path="+path
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			System.err.println("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
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
		}		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}			
		return irLibFile;			
	}
	
	
	public	int saveToDB(MySqlClass mysql) 
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IRLibFile irLibFile=new IRLibFile();
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String sql2="insert into   "
		+iRLibTable
		+"("		
		+" filename       ,"
		+"path,"		
		+"createtime"
		+"modifytime"
		+"filesize, "
		+"recordCount"
		+ ") "	
		+" values ('"
		+this.fileName+"','"
		+this.path+"','"
		+sdf.format(getCreateTime())+"','"
		+sdf.format(getModifyTime())+"',"
		+this.fileSize+","
		+this.recordCount
		+ ";";
		System.out.println("query:"+sql2);
		int res2=mysql.query(sql2);
		
		return res2;			
	}
	
	public static void main(String[] args) {

	}

}
