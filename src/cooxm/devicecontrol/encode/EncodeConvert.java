package cooxm.devicecontrol.encode;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 2, 2015 11:04:08 AM 
 */

public class EncodeConvert {
	
	private static void convert(File file) {
        if (file.isDirectory()) {
            File[] ch = file.listFiles();
            for (int i = 0; i < ch.length; i++) {
                convert(ch[i]);
            }
 
        } else {
            if (file.getName().endsWith("txt")) {
                //                    String s = FileUtils.readFileToString(file, "UTF-8");
				//                    System.out.println("convert " + file.getPath());
				//                    File file2=new File(new String ((file.getName()+".bak").getBytes("UTF-8"),"UTF-8"));
				//                    System.out.println("file2= " + file2.getName());
				//                    FileUtils.writeStringToFile(file2, s, "UTF-8");
	        	String filename2=ChineseToSpell.converterToSpell(file.getName()).toLowerCase();
	        	 System.out.println("file2= " + filename2);
	        	 String rename=file.getParentFile()+"\\rename\\";
	        	 new File(rename).mkdir();
	        	file.renameTo(new File(file.getParentFile()+"\\rename\\"+filename2));
	        	
	        	
            }
 
        }
 
    }

	public static void main(String[] args) {
		
		EncodeConvert.convert(new File("D:\\documents\\小秘智能家居后台设计\\红外码库\\ird2\\keyfiles\\AC\\codes"));
		
//		String encoding = System.getProperty("file.encoding"); 
//		System.out.println(encoding); 

	}

}