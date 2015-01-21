package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：17 Dec 2014 18:30:11 
 */

public class FactorDict extends Factor {
	static final String FactorDictionaryTable = "dic_st_factor";
	static Map<Integer, FactorDict> factorDictMap=new HashMap<Integer, FactorDict>(); 
	

	
	/*** 0:家电因素，如灯 空调;  
          1：环境因素，如光强度'
    */
	int  factorType     ;
	String  factorName     ;
	String   description    ;
	/** 度量单位*/
	String   unit    ;
	
	/***1、绝对值；2、相对值,*/
	int  mstype         ;
	int  createOperator ;
	int  modifyOperator ;

	
	public FactorDict(
			int  factorid       ,
			int  factortype     ,
			String  factorname     ,
			String  description    ,
			String  measurement    ,
			int  mstype         ,
			int  createoperator ,
			int  modifyoperator ,
			Date  createtime     ,
			Date  modifytime		)
	{	
		this.factorID       =  factorid       ;
		this.factorType     =  factortype     ;
		this.factorName     =  factorname     ;
		this.description    =  description    ;
		this.unit    =  measurement    ;
		this.mstype         =  mstype         ;
		this.createOperator =  createoperator ;
		this.modifyOperator =  modifyoperator ;
		this.createTime     =  createtime     ;
		this.modifyTime		=  modifytime	  ;
		
	}
	
	public FactorDict() {
	}

	public FactorDict getOneFactor(MySqlClass mysql) throws SQLException	
	{
		FactorDict fd= null;//new FactorDictionary();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"factorid     ,"
		+"factortype   ,"
		+"factorname   ,"
		+"description  ,"
		+"measurement  ,"
		+"mstype  ,"
		+"modifyoperator  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+FactorDict.FactorDictionaryTable
		+" where factorid="+factorID
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} 
//		String[] records=res2.split("\n");
//		for(String line:records){			
			fd =new FactorDict();
			String[] index=res2.split(",");
			fd.factorID=Integer.parseInt(index[0]);	
			fd.factorType=Integer.parseInt(index[1]);
			fd.factorName=index[2];	
			fd.description=index[3];	
			fd.unit=index[4]; 
			fd.mstype=Integer.parseInt(index[5]);	
			fd.createOperator=Integer.parseInt(index[6]);	
			fd.modifyOperator=Integer.parseInt(index[7]); 
			try {
				fd.createTime=sdf.parse(index[8]);
				fd.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				e.printStackTrace();
			}			
				
		//}
		return fd;
	}
	
	
	public void InitializeFactorDictMap(MySqlClass mysql) throws SQLException	
	{
		FactorDict fd= null;//new FactorDictionary();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"factorid        ,"
		+"factortype        ,"
		+"factorname      ,"
		+"description  ,"
		+"measurement  ,"
		+"mstype  ,"
		+"modifyoperator  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+FactorDict.FactorDictionaryTable
		//+" where factorid="+factorid
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return ;
		} 
		String[] records=res2.split("\n");
		for(String line:records){			
			fd =new FactorDict();
			String[] index=line.split(",");
			fd.factorID=Integer.parseInt(index[0]);	
			fd.factorType=Integer.parseInt(index[1]);
			fd.factorName=index[2];	
			fd.description=index[3];	
			fd.unit=index[4]; 
			fd.mstype=Integer.parseInt(index[5]);	
			fd.createOperator=Integer.parseInt(index[6]);	
			fd.modifyOperator=Integer.parseInt(index[7]); 
			try {
				fd.createTime=sdf.parse(index[8]);
				fd.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			FactorDict.factorDictMap.put(fd.factorID, fd)	;
		}
		
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
