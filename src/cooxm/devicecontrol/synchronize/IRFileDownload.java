package cooxm.devicecontrol.synchronize;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import cooxm.devicecontrol.encode.ChineseToSpell;
import cooxm.devicecontrol.encode.SQLiteUtil;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 27, 2015 4:20:55 PM 
 */

public class IRFileDownload {
	private static final String DOWNLOAD_FTP_IP="172.16.35.173";
	int format_ID;
	public int getFormat_ID() {
		return format_ID;
	}
	public void setFormat_ID(int format_ID) {
		this.format_ID = format_ID;
	}
	public IRFileDownload(int format_ID) {
		this.format_ID = format_ID;
	}	
	
	
	/** 获取要下载文件的路径 */
	public String getFilename(){
		SQLiteUtil sqlite=new SQLiteUtil("ird.db");
		String sql="select format_name from formats where fid='"+this.format_ID+"';";
		String rs=sqlite.select(sql);
		return ChineseToSpell.converterToSpell(rs);
	}
	public String getURL(){	
		String url=DOWNLOAD_FTP_IP+"/keyfiles/AC/codes/"+this.getFilename()+".txt"; 		
		return url;		
	}
	
	public void downlaod(){
		FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 

        try { 
            ftpClient.connect(DOWNLOAD_FTP_IP); 
            ftpClient.login("anonymous", "12345678"); 

            String remoteFileName = "/keyfiles/AC/codes/"+this.getFilename()+".txt"; 
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
		
		IRFileDownload ir=new IRFileDownload(14);
		String name=ir.getFilename();
		System.out.println(name);
		//ir.downlaod();
		
		System.out.println(ir.getURL());		

	}

}
