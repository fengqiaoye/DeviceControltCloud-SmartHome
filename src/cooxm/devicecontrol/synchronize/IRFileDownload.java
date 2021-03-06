package cooxm.devicecontrol.synchronize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.encode.ChineseToSpell;
import cooxm.devicecontrol.encode.SQLiteUtil;
import cooxm.devicecontrol.encyco.TuringCatAesCrypto;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 27, 2015 4:20:55 PM 
 */

public class IRFileDownload {
	private static  String DOWNLOAD_FTP_IP;//="172.16.35.173";
	String fileName;
	int applianceType;
	public String getfileName() {
		return fileName;
	}
	public void setfileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getApplianceType() {
		return applianceType;
	}
	public void setApplianceType(int applianceType) {
		this.applianceType = applianceType;
	}
	
	public IRFileDownload(int applianceType, String fileName) {
		this.fileName = fileName;
		this.applianceType=applianceType;
		Configure cf=MainEntry.getConfig();
		//file=new File(cf.getValue("ir_file_path"));
		DOWNLOAD_FTP_IP=cf.getValue("ir_file_ip");
		
	}	
	
	
	/** 获取要下载文件的路径 */
	public String getFilename(){
		/*SQLiteUtil sqlite=new SQLiteUtil("ird.db");
		String sql="select format_name from formats where fid='"+this.fileName+"';";
		String rs=sqlite.select(sql);
		return ChineseToSpell.converterToSpell(rs);*/
		return this.fileName;
	}
	
	/** 如果初始化时候 filename的文件含没有.txt后缀，用这个函数
	 */
	public String getLocalURL(){	
		String url=DOWNLOAD_FTP_IP+"/keyfiles6/"+this.getFilename()+".txt"; 		
		return url;		
	}
	
	/** 如果初始化时候 filename的文件含没有.txt后缀，用这个函数
	 */
	public String getEncycroURL(){	
		TuringCatAesCrypto crypto = new TuringCatAesCrypto();
		crypto.setToken("token_key");
		String url="keyfiles6/"+getDevTypeStr()+"/codes/"+this.getFilename()+".txt"; 	
		String encrypted = crypto.encrypt(url);
		return DOWNLOAD_FTP_IP+encrypted;		
	}
	
	/**
	* @Title: getURLWithoutExtention 
	* @Description:  如果初始化时候 filename的文件含有.txt后缀，用这个函数
	* @param @return   带后缀的文件下载路径 
	* @return String    
	*/
	public String getURLWithoutExtention(){	
		String url=DOWNLOAD_FTP_IP+"/keyfiles6/"+this.getFilename(); 
		return url;		
	}
	
	public String getDevTypeStr(){
		String appliceTypeStr="";
		switch (applianceType) {
		case 541: //空调
			appliceTypeStr="AC";
			break;
		case 501: //电视
			appliceTypeStr="TV";
			break;
		case 511: //机顶盒
			appliceTypeStr="STB";
			break;
		case 521: //视频盒子
			appliceTypeStr="IPTV";
			break;
		case 601: //电风扇
			appliceTypeStr="FAN";
			break;
		case 591: //空气净化器
			appliceTypeStr="ACL";
			break;	
		case 522: //DVD
			appliceTypeStr="DVD";
			break;
		default:
			appliceTypeStr="";
			break;
		}
		return appliceTypeStr;
	}
	
	public void downlaod(){
		FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 

        try { 
            ftpClient.connect(DOWNLOAD_FTP_IP); 
            ftpClient.login("anonymous", "12345678"); 

            String remoteFileName = "/keyfiles5/AC/codes/"+this.getFilename()+".txt"; 
            //String remoteFileName = "/AC/codes/"+this.getFilename()+".txt"; 
            fos = new FileOutputStream("d:/"+this.getFilename()+".txt"); 

            ftpClient.setBufferSize(1024); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            System.out.println("donwload "+DOWNLOAD_FTP_IP+remoteFileName+"...");
            ftpClient.retrieveFile(remoteFileName, fos); 
            System.out.println("finished");
           
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP客户端出错！", e); 
        } finally { 
            IOUtils.closeQuietly(fos); 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("关闭FTP连接发生异常！", e); 
            } 
        } 
	}
	

	
	
	public static void main(String[] args) {
		
		IRFileDownload ir=new IRFileDownload(541,"84");
		String name=ir.getFilename();
		System.out.println(name);
        Date start=new Date();
		ir.downlaod();		
		System.out.println("finished in "+(new Date().getTime()-start.getTime())/1000 +"secods");
		System.out.println(ir.getEncycroURL());		

	}

}
