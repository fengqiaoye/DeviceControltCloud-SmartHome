package cooxm.devicecontrol.synchronize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.encode.SQLiteUtil;
import cooxm.devicecontrol.util.MySqlClass;
import cooxm.devicecontrol.util.StringUtility;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 14, 2015 2:16:50 PM 
 */

public class IRMatch2 {
	static Logger log= Logger.getLogger(IRMatch.class);
	private File fileDir;//=new File("D:\\documents\\cooxm\\document\\小秘智能家居后台设计\\红外码库\\ird3\\keyfiles3\\AC\\codes");
	/** < 疑或后1的个数，文件名> */
	Map<Integer, String> fileScoreMap;//=new TreeMap<Integer, String>() ;	
	/** Map< Type,Map<fileName,List<String>>> */
	Map<String, HashMap<String, ArrayList<String>>> dirMap;
	static SQLiteUtil sqlite;
	int deviceType;
	String rawCode;	
	
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getRawCode() {
		return rawCode;
	}
	public void setRawCode(String rawCode) {
		this.rawCode = rawCode;
	}
	
	public void init(String rawCode,int deviceType){
		this.rawCode=rawCode;
		this.deviceType=deviceType;
	}

	public IRMatch2(){
		this.fileScoreMap=new TreeMap<Integer, String>() ;
		Configure cf=MainEntry.getConfig();
		fileDir=new File(cf.getValue("ir_file_path"));
		this.dirMap=new HashMap<String, HashMap<String, ArrayList<String>>>();
		this.sqlite=new SQLiteUtil("./ird4.db");
	}
	
	public void getTop5(){
		Object[] a =this.fileScoreMap.keySet().toArray();  
		int len=Math.min(5, this.fileScoreMap.keySet().size());
		for (int i = 0; i < len; i++) {
			System.out.println(a[i]+":"+this.fileScoreMap.get(a[i]));			
		}
		System.out.println("\n");
	}
	
	/** 获取码库中 的频率*/
    public String getRawCodeFrequency(){
    	return rawCode.substring(18,20);
    }
	
	/** 获取码库中 的频率 和长度，返回二维数组 [频率,C3长度]
	 * */
    public String[] getDBFeature(int fid,int deviceType){
    	int deviceID=0;
		switch (deviceType) {
		case 541: //空调
			deviceID=1;
			break;
		case 501: //电视
			deviceID=2;
			break;
		case 511: //机顶盒
			deviceID=3;
			break;
		case 522: //DVD
			deviceID=4;
			break;
		case 601: //电风扇
			deviceID=5;
			break;
		case 591: //空气净化器
			deviceID=6;
			break;
		case 521: //视频盒子
			deviceID=7;
			break;	
		default:
			deviceID=1;
			break;
		}		
		String sql="select format_string  from formats where device_id="+deviceID+" and fid="+fid +";";
		//System.out.println(sql);
		if(sqlite==null){
			System.out.println("error:sqlite is null ");
		}
		String rs=sqlite.select(sql);
		//System.out.println(rs);
		;
		String frequency=rs.substring(18,20);
		String C3length=getC3Length(rs);
		String[] freStr={frequency,C3length};
        return freStr;
    }
	
	public void clear(){
		this.fileScoreMap.clear();
	}
	
	/** 返回值为匹配最为相似的红外码相差的位数 和文件名，用逗号分隔*/
	public String getTop1(){
		Object[] a =this.fileScoreMap.keySet().toArray();  
		int len=Math.min(1, this.fileScoreMap.keySet().size());
		if(len>0 && (int)a[0]<=10){  //位差小于等于10
			return this.fileScoreMap.get(a[0]).split("\\|")[0]+"|"+a[0];
			//return (this.fileScoreMap.get(a[0]).split("\\|"))[0];

		}else{
			return null;
		}
	}
	
	
	/** 将匹配的结果按相差位数从小到大排序，结果保存在 map中
	 * deviceType 设备类型
	 * */
	public  void match(File file,String c3code) {	
		boolean return_flag=false;
    	Map<Integer, String> lineScoreMap= new TreeMap<Integer, String>();
    	
        if (file.isDirectory()) {
            File[] ch = file.listFiles();
            for (int i = 0; i < ch.length; i++) {
            	match(ch[i],c3code);
            	if (return_flag==true) {
					return;
				}
            } 
        } else {
            if (file.getName().endsWith("txt")) {
            	//System.out.println("matching:"+file.getPath()+"---------------------");
            	int score=Integer.MAX_VALUE;
            	int byteLen=c3code.split(",").length;
            	if(byteLen==getColumnNum(file)){
            		String line=null;
            		try {
            			String abstract_path="";
						BufferedReader br=new BufferedReader(new FileReader(file));
						while((line=br.readLine())!=null){
							int diff= differceBitCount(line,c3code);
							if(diff<score){
								lineScoreMap.put(diff, line);
								score=Math.min(score, diff);
							}
							String path=file.getPath();
							abstract_path=path.substring(path.indexOf("keyfiles3")+10,path.length() );
							
							int pos=abstract_path.lastIndexOf('\\');
							if(pos<0){
								pos=abstract_path.lastIndexOf('/');
							}
							String fid=abstract_path.substring(pos+1,abstract_path.length()-4);

							if(score<=10){								
								String[] dbFeature=this.getDBFeature(Integer.parseInt(fid),deviceType);
								String rawFrenquency=this.getRawCodeFrequency();
								String rawC3length=this.getC3Length(rawCode);
								if(dbFeature[0].equals(rawFrenquency) && dbFeature[1].equals(rawC3length)){
									//System.out.println("score=0,thisfrenquency="+dbFrenquency+",file="+abstract_path);
									fileScoreMap.put(score, abstract_path+"|"+line);
									return_flag=true;
									return;
								}else{
									break;
								}
							}else{
								fileScoreMap.put(score, abstract_path+"|"+lineScoreMap.get(score));
							}
						}	
						//System.out.println("SIZE of scoreMAP="+fileScoreMap.size()+",the top one is:"+abstract_path);
						br.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            } 
        } 
    }
	
	
	public  String getC3(String rawCode){
		String c3String="";
		String[] codeArray=rawCode.toUpperCase().split(",");
		for (int i = 0; i < codeArray.length; i++) {
			if(codeArray[i].equals("C1")){
				i=i+2;	
				continue;
			}else if(codeArray[i].equals("C2")){
				i=i+3;	
				continue;
			}else if(codeArray[i].equals("C3")){
				int c3Len=Integer.parseInt(codeArray[i+2],16); 
				//System.err.println(c3Len);
				int c3byte=(int)Math.ceil(c3Len*1.0/8.0);
				//System.err.println(c3byte);
				for (int j = 0; j < c3byte; j++) {
					c3String=c3String + codeArray[i+3+j] + ",";					
				}
			}
		}
		//log.info("C3code= "+c3String);
		return c3String;		
	}
	
	public  String getC3Length(String raw){
		String[] codeArray=raw.toUpperCase().split(",");
		for (int i = 0; i < codeArray.length; i++) {
			if(codeArray[i].equals("C1")){
				i=i+2;	
				continue;
			}else if(codeArray[i].equals("C2")){
				i=i+3;	
				continue;
			}else if(codeArray[i].equals("C3")){
				String len=codeArray[i+2];
				return len;
			}
		}
		//log.info("C3code lenth= "+len);
		return null;		
	}
	
	public  int getColumnNum(File file){
		try {
			BufferedReader br=new BufferedReader(new FileReader(file));
			String firstLine=br.readLine();
			int count= firstLine.split(",").length;
			br.close();
			return count;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;		
	}
	
	public int differceBitCount(String code1,String code2){
		int diffentceCount=0;
		String[] codeArr1=code1.split(",");
		String[] codeArr2=code2.split(",");
		if(codeArr1.length!=codeArr2.length){
			return Integer.MAX_VALUE;
		}
		if(codeArr1[codeArr1.length-1].length()>2 || codeArr2[codeArr2.length-1].length()>2){
			System.out.println(code1);
		}
		for (int i = 0; i < codeArr2.length; i++) {
			int a=Integer.parseInt(codeArr1[i],16);
			int b=Integer.parseInt(codeArr2[i],16);
			int yihuo=a^b; 
			
			diffentceCount=diffentceCount+BitCount5(yihuo);
			//System.out.println("a="+a+",b="+b+",a^b="+yihuo+",count="+diffentceCount);
		}
		return diffentceCount;
	}
	
	/** 求一个数中1的个数*/
	public static int BitCount5(int n) 
	{
	    int tmp = n - ((n >>1) &033333333333) - ((n >>2) &011111111111);
	    return ((tmp + (tmp >>3)) &030707070707) %63;
	}
	
	public static int saveUnknownCode(MySqlClass mysql,int ctrolID,int appliancetype,String appliancetypestr,String ircode){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="replace into "+ " unknown_infrared_code "
				+" (ctrolid  ,"     
				+"appliancetype ,"
				+"appliancetypestr ,"
				+"ircode ,"
				+"createtime   "
				+ ")"				
				+"values "
				+ "("
				+ctrolID+","
				+appliancetype+",'"
				+appliancetypestr+"','"
				+ircode+"','"
				+sdf.format(new Date())
				+"');";
		int count=mysql.query(sql);
		return count;
		
	}
	

	
	public String recursiveMatch(IRMatch2  im){
    	
		Configure cf=MainEntry.getConfig();
		File file=new File(cf.getValue("ir_file_path"));
		String C3code=im.getC3(this.rawCode);
		System.out.println("C3CODE="+C3code);
		//System.out.println("C3CODE len="+len);
		im.match(file, C3code);
		String res=im.getTop1();
		if(res==null){
    		if(C3code.charAt(C3code.length()-1)!=','){
    			C3code=C3code+",";
    		}
    		String tempSubStr=C3code;  
    		String subStr=null;
    		while(tempSubStr!=null){
    			 subStr=tempSubStr;
    			 tempSubStr=new StringUtility().getLongestSubStr(tempSubStr);//寻找最长重复子串    				 
    		}  
			if(subStr!=null){
				int count=StringUtility.getSubCount_2(C3code,subStr);  // 重复子串 重复的次数
				for (int j = count-1; j >=1; j--) {
					String C2code2=StringUtility.getNthDuplicateStr(subStr,j);      //子串重复j次
					im.match(file, C2code2);
					res=im.getTop1();
					if(res!=null)           //找到了 退出
						break;
				}
			}			
		}
		if(res==null){
			log.info("infraRed code match failed !!");
			return null;
		}
		String [] result=res.split("\\|");//[0];
		String fileName=result[0];
		String score=result[1];
		if(result!=null && result.length==2 ){
			fileName=result[0];
			score=result[1];
		}
		
		System.out.println(fileName+",diff="+score);

		int pos=fileName.lastIndexOf('\\');
		if(pos<0){
			pos=fileName.lastIndexOf('/');
		}
		String fid=fileName.substring(pos+1,fileName.length()-4);
		System.out.println("fid="+fid);
		
		int pos2=fileName.indexOf('\\');
		if(pos<0){
			pos=fileName.indexOf('/');
		}
		
		String deviceTypeStr=fileName.substring(0,pos2);
		System.out.println("TYPE="+deviceTypeStr);
		int devType=-1;
		switch (deviceTypeStr) {
		case "AC": //空调
			devType=541;
			break;
		case "TV": //电视
			devType=501;
			break;
		case "STB": //机顶盒
			devType=511;
			break;
		case "IPTV": //视频盒子
			devType=521;
			break;
		case "FAN": //电风扇
			devType=601;
			break;
		case "ACL": //空气净化器
			deviceType=591;
			break;	
		case "DVD": //DVD
			devType=621;
			break;
		default:
			devType=-1;
			break;
		}
		System.out.println("TYPE="+devType+"\n");
		return null;
	}
	
	public void initFileMap(File file){		
		if (file.isDirectory()) {
            File[] ch = file.listFiles();
            for (int i = 0; i < ch.length; i++) {
            	initFileMap(ch[i]);
            } 
        } else {
            if (file.getName().endsWith("txt")) {
            	String path=file.getPath();
            	String fileName=path.substring(path.indexOf("keyfiles3")+10,path.length() );
    			int pos2=fileName.indexOf('\\');
    			if(pos2<0){
    				pos2=fileName.indexOf('/');
    			}    			
    			String deviceTypeStr=fileName.substring(0,pos2);
    			
    			HashMap<String,ArrayList<String>> fileMap=dirMap.get(deviceTypeStr);
    			if(fileMap==null){
    				fileMap=new HashMap<String,ArrayList<String>>();
    			}            	
    			int pos=fileName.lastIndexOf('\\');
    			if(pos<0){
    				pos=fileName.lastIndexOf('/');
    			}
    			String fid=fileName.substring(pos+1,fileName.length());
    			
            	String line=null;
            	ArrayList<String> codeList=new ArrayList<String> ();
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(file));
					while((line=br.readLine())!=null){
						codeList.add(line);
					}
					fileMap.put(fid, codeList);
					this.dirMap.put(deviceTypeStr, fileMap);
	
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}            	
            }
        }		
	}

	public static void main(String[] args) {
		String[] raw=new String[50];
		raw[0]="29,04,00,00,24,00,26,82,79,02,28,82,79,06,6f,c1,22,d6,c2,00,11,67,c3,00,23,08,09,20,50,02,c2,00,4d,25,c3,00,20,00,20,00,d0,00,";
		raw[1]="29,04,00,00,24,00,26,82,79,02,28,82,79,06,6f,c1,22,d6,c2,00,11,67,c3,00,23,08,09,20,50,02,c2,00,4d,25,c3,00,20,00,20,00,d0,00,";
		raw[2]="1e,04,00,00,24,00,26,82,a0,02,a0,82,a0,06,86,c1,23,4d,c2,00,11,85,c3,00,23,04,0c,00,50,02,00";
		raw[3]="33,04,00,00,24,00,26,82,4e,01,f7,82,4e,6,3f,c1,11,76,c2,00,11,76,c3,00,30,4d,b2,f8,07,1b,e4,c2,00,14,a6,c1,11,76,c2,00,11,76,c3,00,30,4d,b2,f8,07,1b,e4,00,";
		raw[4]="2e,04,00,00,24,00,26,82,11,02,45,82,11,06,6a,c1,0b,c8,c2,00,0b,c8,c1,0b,c8,c2,00,11,4c,c3,00,70,65,01,00,00,02,35,00,00,00,00,00,00,a0,e8,00";
		raw[5]="24,04,00,00,24,00,26,81,b0,02,bd,81,b0,06,fc,c1,0b,d1,c2,00,11,8f,c3,00,58,65,01,00,00,35,00,00,00,00,00,4b,00";
		raw [6]="27,04,00,00,24,00,26,81,f8,01,f8,81,f8,04,8d,c1,0e,11,c2,00,05,ff,c3,00,70,23,cb,26,01,00,20,08,07,09,00,00,00,00,4d,00";
		raw [7]="47,04,00,00,24,00,26,82,88,02,88,82,88,06,79,c1,23,4d,c2,00,11,87,c3,00,23,18,09,20,50,02,c2,00,4e,1d,c3,00,20,00,20,00,e0,c2,00,9c,51,c1,23,4d,c2,00,11,87,c3,00,23,18,09,20,70,02,c2,00,4e,1d,c3,00,20,00,00,18,c0,00";
		raw [8]="1e,04,00,00,24,00,26,82,6d,02,6d,82,6d,06,7c,c1,23,02,c2,00,11,63,c3,00,23,08,00,00,50,02,00";
		raw[9]="2e,04,00,00,24,00,26,82,3e,02,3e,82,3e,06,3a,c1,0b,dc,c2,00,0b,dc,c1,0b,dc,c2,00,11,1c,c3,00,70,65,01,00,00,02,35,00,00,00,00,00,00,a0,e8,00";
	    raw[10]="2b,04,00,00,24,00,26,81,e1,02,8d,81,e1,06,cc,c1,0b,fd,c2,00,0b,fd,c1,0b,fd,c2,00,11,5d,c3,00,58,65,81,00,00,35,00,00,00,00,00,cb,00";
	    raw[11]="2a,04,00,00,24,00,26,81,8c,01,8c,81,8c,04,f9,c1,0d,6e,c2,00,04,f9,c3,00,88,40,00,14,80,43,01,a8,ee,03,00,68,00,00,02,00,00,52,00";
	    raw[12]="33,04,00,00,24,00,26,82,34,02,34,82,34,06,5b,c1,14,a9,c2,00,14,a9,c3,00,30,4d,b2,f8,07,1b,e4,c2,00,14,a9,c1,14,a9,c2,00,14,a9,c3,00,30,4d,b2,f8,07,1b,e4,00";
	    raw[13]="27,04,00,00,24,00,26,82,16,02,16,82,16,04,70,c1,0e,34,c2,00,04,70,c3,00,70,23,cb,26,01,00,20,08,07,09,00,00,00,00,4d,00";
	    raw[14]="47,04,00,00,24,00,26,82,6b,02,6b,82,6b,06,93,c1,23,2f,c2,00,11,a5,c3,00,23,18,09,20,50,02,c2,00,4e,3d,c3,00,20,00,20,00,e0,c2,00,9c,6f,c1,23,2f,c2,00,11,a5,c3,00,23,18,09,20,70,02,c2,00,4e,3d,c3,00,20,00,00,18,c0,00";
	    raw[15]="47,04,00,00,24,00,26,82,6b,02,6b,82,6b,06,93,c1,23,2f,c2,00,11,a5,c3,00,23,18,09,20,50,02,c2,00,4e,3a,c3,00,20,00,20,00,d0,c2,00,9c,6c,c1,23,2f,c2,00,11,a5,c3,00,23,18,09,20,70,02,c2,00,4e,3a,c3,00,20,00,00,18,c0,00";
        raw[16]="47,04,00,00,24,00,26,82,A0,02,A0,82,A0,06,61,C1,23,66,C2,00,11,70,C3,00,23,18,09,20,50,02,C2,00,4E,06,C3,00,20,00,20,00,E0,C2,00,9C,3A,C1,23,66,C2,00,11,70,C3,00,23,18,09,20,70,02,C2,00,4E,06,C3,00,20,00,00,18,C0,00";
        raw[17]="2E,04,00,00,24,00,26,82,40,02,40,82,40,06,3C,C1,0B,FC,C2,00,0B,FC,C1,0B,FC,C2,00,11,1C,C3,00,70,65,01,00,00,02,35,00,00,00,00,00,00,A0,E8,00";
        raw[18]="2A,04,00,00,24,00,26,81,8E,01,8E,81,8E,06,8F,C1,D,6F,C2,00,06,8F,C3,00,88,40,00,14,80,43,02,A8,EE,23,00,68,00,00,02,00,00,55,00";
        raw[19]="33,04,00,00,24,00,26,82,35,02,35,82,35,06,58,C1,11,5E,C2,00,11,5E,C3,00,30,4D,B2,F8,07,1B,E4,C2,00,11,5E,C1,11,5E,C2,00,11,5E,C3,00,30,4D,B2,F8,07,1B,E4,00";
        raw[20]="27,04,00,00,24,00,26,81,FC,01,FC,81,FC,05,F8,C1,0E,1B,C2,00,05,F8,C3,00,70,23,CB,26,01,00,24,8,07,09,00,00,00,00,51,00";
        raw[21]="33,04,00,00,24,00,26,81,AE,02,81,81,AE,06,D4,C1,10,BD,C2,00,11,94,C3,00,30,4D,B2,DE,21,07,F8,C2,00,14,C3,C1,10,BC,C2,00,11,92,C3,00,30,4D,B2,DE,21,07,F8,00";
        raw[22]="33,04,00,00,24,00,26,81,9F,02,93,81,9F,06,E3,C1,10,AE,C2,00,11,A0,C3,00,30,4D,B2,F8,07,1A,E5,C2,00,14,D4,C1,10,AA,C2,00,11,A4,C3,00,30,4D,B2,F8,07,1A,E5,00";
		raw[23]="33,04,00,00,24,00,26,81,9a,02,96,81,99,06,e8,c1,10,ac,c2,00,11,a5,c3,00,30,4d,b2,f8,07,1b,e4,c2,00,14,d7,c1,10,a9,c2,00,11,a3,c3,00,30,4d,b2,f8,07,1b,e4,00";   
		raw[24]="29,04,00,00,24,00,26,82,10,02,41,81,f5,06,6f,c1,17,75,c2,00,1c,88,c3,00,5f,ff,00,ff,00,ff,00,f7,08,f6,09,2a,55,c2,00,1d,28,00,";
	    raw[25]="1e,04,00,00,24,00,26,82,09,02,1e,81,ec,06,a9,c1,23,4f,c2,00,11,98,c3,00,28,c3,02,0c,10,aa,00";
		/* 以下为电视码  */
	    raw[26]="1d,04,00,00,24,00,26,82,86,01,e3,82,85,06,31,c1,23,4d,c2,00,11,60,c3,00,20,40,bf,12,ed,00";
	    raw[27]="28,04,00,00,24,00,26,82,6d,01,fb,82,83,06,34,c1,11,b9,c2,00,11,64,c3,00,20,18,18,b,f4,c2,00,be,63,c1,11,b5,c2,00,11,65,00";
	    
	    /* 以下为机顶盒  */
	    raw[28]="1d,04,00,00,24,00,26,82,49,02,1f,82,4a,06,6c,c1,23,29,c2,00,11,84,c3,00,20,20,8d,1a,e5,00";
	    raw[29]="28,04,00,00,24,00,26,82,89,01,df,82,8a,06,2e,c1,23,67,c2,00,11,44,c3,00,20,01,fe,3,fc,c2,00,9b,b3,c1,23,43,c2,00,8,91,00";
	    /* 以下为DVD  */
	    raw[30]="1d,04,00,00,24,00,26,82,63,02,04,82,66,06,51,c1,23,46,c2,00,11,66,c3,00,20,49,b6,1a,e5,00";
	    raw[31]="1d,04,00,00,24,00,26,82,46,02,21,82,45,06,70,c1,23,26,c2,00,11,85,c3,00,20,20,df,b,f4,00";
	    raw[32]="3e,04,00,00,24,00,26,81,ea,03,f5,81,ec,07,da,c1,0f,99,c2,00,0f,9d,c3,00,18,4f,05,ab,c2,00,21,0f,c1,0f,94,c2,00,0f,9e,c3,00,18,4f,05,ab,c2,00,21,0f,c1,0f,93,c2,00,0f,9d,c3,00,18,4f,05,ab,00,";
	    raw[33]="4f,04,00,00,24,00,38,82,41,03,9e,82,27,07,9e,c1,0f,d5,c2,00,0f,61,c3,00,18,4f,05,ab,c2,00,20,d2,c1,0f,d4,c2,00,0f,5f,c3,00,18,4f,05,ab,c2,00,20,d4,c1,0f,d3,c2,00,0f,60,c3,00,18,4f,05,ab,c2,00,20,d2,c1,0f,ea,c2,00,0f,49,c3,00,18,4f,05,ab,00";
	    raw[34]="b3,04,00,00,24,00,26,82,88,02,45,82,8d,06,8e,c1,23,0f,c2,00,11,80,c3,00,23,31,07,00,50,02,c2,00,f1,40,c1,0b,ce,c2,00,0b,b3,c1,0c,2b,c2,00,11,5b,c3,00,48,a5,0e,06,00,30,02,04,00,87,c2,00,ef,e5,c1,11,59,c2,00,11,37,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,86,c1,11,63,c2,00,11,36,c3,00,30,4d,b2,de,21,07,f8,c2,00,f0,06,c1,22,36,c2,00,12,0e,c3,00,28,03,18,0c,00,aa,c2,00,f2,24,c1,23,11,c2,00,11,6d,c3,00,23,71,07,20,50,02,c2,00,4e,51,c3,00,20,11,20,00,50,c2,00,9c,7a,c1,23,0d,c2,00,11,6e,c3,00,23,71,07,20,70,02,c2,00,4e,50,c3,00,20,11,00,30,60,00";
	    raw[35]="60,04,00,00,24,00,26,81,f7,03,e8,81,f9,07,cd,c1,0f,a2,c2,00,0f,90,c3,00,18,4f,05,ab,c2,00,21,05,c1,0f,9f,c2,00,0f,93,c3,00,18,4f,05,ab,c2,00,21,04,c1,0f,9f,c2,00,0f,91,c3,00,18,4f,05,ab,c2,00,21,03,c1,0f,9f,c2,00,0f,91,c3,00,18,4f,05,ab,c2,00,21,05,c1,0f,a0,c2,00,0f,93,c3,00,18,4f,05,ab,00";
	    raw[36]="82,04,00,00,24,00,26,82,43,03,9d,82,3f,07,80,c1,0f,e8,c2,00,0f,4e,c3,00,18,4f,05,ab,c2,00,20,ba,c1,0f,e8,c2,00,0f,4b,c3,00,18,4f,05,ab,c2,00,20,b6,c1,0f,eb,c2,00,0f,46,c3,00,18,4f,05,ab,c2,00,20,d4,c1,0f,ec,c2,00,0f,4c,c3,00,18,4f,05,ab,c2,00,20,b6,c1,0f,e9,c2,00,0f,4c,c3,00,18,4f,05,ab,c2,00,20,da,c1,0f,c6,c2,00,0f,78,c3,00,18,4f,05,ab,c2,00,20,d3,c1,0f,cc,c2,00,0f,65,c3,00,18,4f,05,ab,00";
	    raw[37]="60,04,00,00,24,00,26,81,fb,03,e3,81,fb,07,c9,c1,0f,a7,c2,00,0f,8d,c3,00,18,4f,05,ab,c2,00,20,fd,c1,0f,a4,c2,00,0f,8c,c3,00,18,4f,05,ab,c2,00,20,fd,c1,0f,a3,c2,00,0f,8c,c3,00,18,4f,05,ab,c2,00,20,fd,c1,0f,a4,c2,00,0f,8d,c3,00,18,4f,05,ab,c2,00,20,fd,c1,0f,a5,c2,00,0f,8d,c3,00,18,4f,05,ab,00";
	    raw[38]="ad,04,00,00,24,00,26,82,3c,03,9a,82,43,07,86,c1,0f,ec,c2,00,0f,48,c3,00,18,4f,05,ab,c2,00,20,b5,c1,0f,eb,c2,00,0f,44,c3,00,18,4f,05,ab,c2,00,20,ec,c1,0f,d8,c2,00,0f,46,c3,00,18,4f,05,ab,c1,0f,ec,c2,00,0f,48,c3,00,18,4f,05,ab,c2,00,20,b5,c1,0f,eb,c2,00,0f,44,c3,00,18,4f,05,ab,c2,00,20,ec,c1,0f,d8,c2,00,0f,46,c3,00,18,4f,05,ab,c1,0f,ec,c2,00,0f,48,c3,00,18,4f,05,ab,c2,00,20,b5,c1,0f,eb,c2,00,0f,44,c3,00,18,4f,05,ab,c2,00,20,ec,c1,0f,d8,c2,00,0f,46,c3,00,18,4f,05,ab,c2,00,20,da,c1,0f,c5,c2,00,0f,6b,c3,00,18,4f,05,ab,00";
	    raw[39]="3e,04,00,00,24,00,26,82,1c,03,c8,82,1b,07,aa,c1,0f,c2,c2,00,0f,70,c3,00,18,4f,05,ab,c2,00,20,e0,c1,0f,c1,c2,00,0f,6f,c3,00,18,4f,05,ab,c2,00,20,df,c1,0f,c1,c2,00,0f,70,c3,00,18,4f,05,ab,00";
	    raw[40]="4e,04,00,00,24,00,26,81,e1,02,52,81,e3,06,a1,c1,10,f2,c2,00,11,60,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,8e,c1,10,f1,c2,00,11,60,c3,00,30,4d,b2,de,21,07,f8,c1,10,f2,c2,00,11,60,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,8e,c1,10,f1,c2,00,11,60,00";
	    raw[41]="33,04,00,00,24,00,38,82,4d,01,fa,82,51,06,0d,c1,11,52,c2,00,10,f4,c3,00,30,4d,b2,de,21,07,f8,c2,00,14,1e,c1,11,51,c2,00,10,f2,c3,00,30,4d,b2,de,21,07,f8,00";
	    //TV
	    raw[42]="a8,04,00,00,24,00,26,82,14,03,c9,82,14,07,ad,c1,02,13,c2,00,03,ca,c3,00,09,56,01,c1,0f,c3,c2,00,0f,72,c3,00,18,4f,05,ab,c2,00,20,e2,c1,0f,bd,c2,00,0f,70,c3,00,18,4f,05,ab,c2,00,20,df,c1,0f,be,c2,00,0f,71,c3,00,18,4f,05,ab,c1,0f,c3,c2,00,0f,72,c3,00,18,4f,05,ab,c2,00,20,e2,c1,0f,bd,c2,00,0f,70,c3,00,18,4f,05,ab,c2,00,20,df,c1,0f,be,c2,00,0f,71,c3,00,18,4f,05,ab,c1,0f,c3,c2,00,0f,72,c3,00,18,4f,05,ab,c2,00,20,e2,c1,0f,bd,c2,00,0f,70,c3,00,18,4f,05,ab,c2,00,20,df,c1,0f,be,c2,00,0f,71,c3,00,18,4f,05,ab,00";
	    //System.out.println(im.getC3(raw16));
	    
        System.out.println(new Date());
	    for (int i = 42; i < 43; i++) {
	    	System.out.println(i);
	    	IRMatch2 im=new IRMatch2();
	    	im.init(raw[i], 501);
	    	im.recursiveMatch(im);
			
		}
        
    System.out.println(new Date());
	}
	

}
