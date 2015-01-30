package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：27 Jan 2015 14:18:07 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cooxm.devicecontrol.util.MySqlClass;

public class FactorDict {
	public static final String FactorDictionaryTable = "dic_st_factor";
	public static Map<Integer, FactorDict> factorDictMap=new HashMap<Integer, FactorDict>(); 
	
	/***<pre>0-10：保留
	10：灯
	20：电视
	40: 空调

	41: 空调开关
	42：空调温度
	43：空调风速

	60：窗户
	80：窗帘
	90：暖器

	201：光
	301：PM2.5 
	401：有害气体
	501：湿度
	601：温度
	701：天气（预报）
	901：声音*/
	private int factorID;
	
	/*** 0:家电因素，如灯 空调;  
         1：环境因素，如光强度'
    */
	private int  factorType     ;
	private String  factorName     ;
	private String   description    ;
	/** 度量单位*/
	private String   unit    ;
	
	/***1、绝对值；2、相对值,*/
	private int  mstype         ;
	private int  createOperator ;
	private int  modifyOperator ;
	private Date  createTime    ;
	private Date  modifyTime	;


	
	public int getFactorID() {
		return factorID;
	}

	public void setFactorID(int factorID) {
		this.factorID = factorID;
	}

	public int getFactorType() {
		return factorType;
	}

	public void setFactorType(int factorType) {
		this.factorType = factorType;
	}

	public String getFactorName() {
		return factorName;
	}

	public void setFactorName(String factorName) {
		this.factorName = factorName;
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
	
	public FactorDict(){}
	
	public FactorDict(int factorid){
		this.factorID=factorid;
	}
	public FactorDict(
			int  factorid       ,
			int  factortype     ,
			String  factorname     ,
			String  description    ,
			String  measurement    ,
			int  mstype         ,
			int  createoperator ,
			int  modifyoperator ,
			Date  createTime     ,
			Date  modifyTime		)
	{	
		this.factorID       =  factorid       ;
		this.factorType     =  factortype     ;
		this.factorName     =  factorname     ;
		this.description    =  description    ;
		this.unit    =  measurement    ;
		this.mstype         =  mstype         ;
		this.createOperator =  createoperator ;
		this.modifyOperator =  modifyoperator ;
		this.createTime     =  createTime     ;
		this.modifyTime		=  modifyTime	  ;
		
	}

	public FactorDict(int factorID, int createOperator, int modifyOperator,
			Date createTime, Date modifyTime) {
		this.factorID = factorID;
		this.createOperator = createOperator;
		this.modifyOperator = modifyOperator;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}
	
	


	public FactorDict(int factorID, Date createTime, Date modifyTime) {
		this.factorID = factorID;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	public FactorDict getFromDB(MySqlClass mysql) throws SQLException	
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

		return fd;
	}
	
	
	public static void InitializeFactorDictMap(MySqlClass mysql) 	
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
		+"createoperator ,"
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
				e.printStackTrace();
			}			
			FactorDict.factorDictMap.put(fd.factorID, fd);
		}
		
	}
	

	

	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		
		InitializeFactorDictMap(mysql);
	}

}
