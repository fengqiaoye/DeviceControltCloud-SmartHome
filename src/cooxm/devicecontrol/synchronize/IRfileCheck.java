package cooxm.devicecontrol.synchronize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import cooxm.devicecontrol.encode.SQLiteUtil;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jul 30, 2015 3:59:29 PM 
 */

public class IRfileCheck {
	static String path="D:/documents/cooxm/document/Infrared/ird6/keyfiles6/";
	
	public static String[][] getAllMaches(){
		
		SQLiteUtil sqlite=new SQLiteUtil("ird5.db");
		String sql="select fid,matchs,format_name,device_id ,c3rv from formats ;";
		String rs=sqlite.select(sql);
		//System.out.println(rs);
		String[] line=rs.split("\n");
		String [][] waves=new String[line.length][];
		for (int i = 0; i < line.length; i++) {
			waves[i]=line[i].split(";");	
			//System.out.println(waves[i][0]+" "+waves[i][2]);
		}		
		return waves;	
	}
	
	public static int getColumnNum(int deviceType,String fid){
		path="D:/documents/cooxm/document/Infrared/ird6/keyfiles6/";
		String deviceTypeStr="";
		switch (deviceType) {
		case 1: //空调
			deviceTypeStr="AC";
			break;
		case 2: //电视
			deviceTypeStr="TV";
			break;
		case 3: //机顶盒
			deviceTypeStr="STB";
			break;
		case 4: //DVD
			deviceTypeStr="DVD";
			break;
		case 5: //电风扇
			deviceTypeStr="FAN";
			break;
		case 6: //空气净化器
			deviceTypeStr="ACL";
			break;
		case 7: //视频盒子
			deviceTypeStr="IPTV";
			break;	
		default:
			deviceTypeStr="";
			break;
		}
		path=path+deviceTypeStr+"/codes/"+fid+".txt";
		try {
			File file=new File(path);
			if(!file.exists()){
				 DecimalFormat df = new DecimalFormat("000");
				 String fid2= df.format(Integer.parseInt(fid));
				path="D:/documents/cooxm/document/Infrared/ird6/keyfiles6/"+deviceTypeStr+"/codes/"+fid2+".txt";
			}
			BufferedReader br=new BufferedReader(new FileReader(path));
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
	
	public static int getC3rvLen(String c3rv){
		int len=0;
		if(c3rv!=null && c3rv!=""){
		    String [] x= c3rv.split("\\|");
		    for (int i = 0; i < x.length; i++) {
		    	String[] xx= x[i].split("-");
				len+=Integer.parseInt(xx[1]);
			}
		    return len;
		}
		
		return -1;
	}

	public static void main(String[] args) {
		
		String[][] waves =getAllMaches();
		for (int i = 0; i < waves.length; i++) {
			int a=getColumnNum(Integer.parseInt(waves[i][3]),waves[i][0]);
			int b=getC3rvLen(waves[i][4]);
			if(a!=b){
				System.out.println(path+", File column num:"+a+", SQL column num:"+b);
			}
		}

	}

}
