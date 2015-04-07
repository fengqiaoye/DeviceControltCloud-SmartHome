package cooxm.devicecontrol.synchronize;

import java.io.FileOutputStream;
import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Mar 27, 2015 4:20:55 PM 
 */

public class FileDownloadTest {
	

	
	

	public static void main(String[] args) {
		
		FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 

        try { 
            ftpClient.connect("172.16.35.173"); 
            ftpClient.login("anonymous", "12345678"); 

            String remoteFileName = "/keyfiles/AC/codes/50560.txt"; 
            fos = new FileOutputStream("d:/50560.txt"); 

            ftpClient.setBufferSize(1024); 
            //设置文件类型（二进制） 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.retrieveFile(remoteFileName, fos); 
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

}
