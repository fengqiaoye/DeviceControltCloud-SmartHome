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
	private String  factorName     ;
	
	/*** 0:家电因素，如灯 空调;  
         1：环境因素，如光强度
         2:系统因素，如时间、日期
    */
	private int  majorTypeID     ;
	private String  majortypename;
	/** 40: 空调类
	 *  10： 灯光类 等等 */
	private int  minorTypeID     ;
	private String  minorTypeName;
	
	/**是否是电器的开关 */
	private boolean isSwitch;

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
	public int getMajorTypeID() {
		return majorTypeID;
	}

	public void setMajorTypeID(int majorTypeID) {
		this.majorTypeID = majorTypeID;
	}

	public String getMajortypename() {
		return majortypename;
	}

	public void setMajortypename(String majortypename) {
		this.majortypename = majortypename;
	}

	public int getMinorTypeID() {
		return minorTypeID;
	}

	public void setMinorTypeID(int minorTypeID) {
		this.minorTypeID = minorTypeID;
	}

	public String getMinorTypeName() {
		return minorTypeName;
	}

	public void setMinorTypeName(String minorTypeName) {
		this.minorTypeName = minorTypeName;
	}

	public boolean isSwitch() {
		return isSwitch;
	}

	public void setSwitch(boolean isSwitch) {
		this.isSwitch = isSwitch;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getMstype() {
		return mstype;
	}

	public void setMstype(int mstype) {
		this.mstype = mstype;
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
	public FactorDict(int factorID, String factorName, int majorTypeID,
			String majortypename, int minorTypeID, String minorTypeName,
			boolean isSwitch, String unit, int mstype, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime) {
		this.factorID = factorID;
		this.factorName = factorName;
		this.majorTypeID = majorTypeID;
		this.majortypename = majortypename;
		this.minorTypeID = minorTypeID;
		this.minorTypeName = minorTypeName;
		this.isSwitch = isSwitch;
		this.unit = unit;
		this.mstype = mstype;
		this.createOperator = createOperator;
		this.modifyOperator = modifyOperator;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	public FactorDict(int factorID, int createOperator, int modifyOperator,
			Date createTime, Date modifyTime) {
		this.factorID = factorID;
		this.createOperator = createOperator;
		this.modifyOperator = modifyOperator;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}

	public FactorDict(int factorID, String factorName) {
		this.factorID = factorID;
		this.factorName = factorName;
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
		+"factorname   ,"		
		+"majortypeid   ,"
		+"majortypename ,"
		+"minortypeid   ,"
		+"minortypename   ,"
		+"isswitch ,"
		+"measurement  ,"
		+"mstype  ,"
		+"createoperator  ,"
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
			fd.factorName=index[1];
			fd.majorTypeID=Integer.parseInt(index[2]);	
			fd.majortypename=index[3];	
			fd.minorTypeID=Integer.parseInt(index[3]);	
			fd.minorTypeName=index[4];	
			fd.isSwitch=Boolean.parseBoolean(index[5]);			
			fd.unit=index[6]; 
			fd.mstype=Integer.parseInt(index[7]);	
			fd.createOperator=Integer.parseInt(index[8]);	
			fd.modifyOperator=Integer.parseInt(index[9]); 
			try {
				fd.createTime=sdf.parse(index[10]);
				fd.modifyTime=sdf.parse(index[11]);
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
		+"factorid     ,"
		+"factorname   ,"		
		+"majortypeid   ,"
		+"majortypename ,"
		+"minortypeid   ,"
		+"minortypename ,"
		+"isswitch ,"
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
			fd.factorName=index[1];
			fd.majorTypeID=Integer.parseInt(index[2]);	
			fd.majortypename=index[3];	
			fd.minorTypeID=Integer.parseInt(index[4]);	
			fd.minorTypeName=index[5];	
			fd.isSwitch=Boolean.parseBoolean(index[6]);			
			fd.unit=index[7]; 
			fd.mstype=Integer.parseInt(index[8]);	
			fd.createOperator=Integer.parseInt(index[9]);	
			fd.modifyOperator=Integer.parseInt(index[10]); 
			try {
				fd.createTime=sdf.parse(index[11]);
				fd.modifyTime=sdf.parse(index[12]);
			} catch (ParseException e) {
				e.printStackTrace();
			}			
			FactorDict.factorDictMap.put(fd.factorID, fd);
		}
		
	}
	

	

	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		
		InitializeFactorDictMap(mysql);
		
		System.out.println("初始化成功");
	}

}
