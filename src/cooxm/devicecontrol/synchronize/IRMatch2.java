package cooxm.devicecontrol.synchronize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.MainEntry;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 14, 2015 2:16:50 PM 
 */

public class IRMatch2 {
	static Logger log= Logger.getLogger(IRMatch.class);
	private static File file;//=new File("D:\\documents\\cooxm\\document\\小秘智能家居后台设计\\红外码库\\ird3\\keyfiles3\\AC\\codes");
	/** < 疑或后1的个数，文件名> */
	Map<Integer, String> fileScoreMap;//=new TreeMap<Integer, String>() ;
	public IRMatch2(){
		this.fileScoreMap=new TreeMap<Integer, String>() ;
		Configure cf=MainEntry.getConfig();
		file=new File(cf.getValue("ir_file_path"));
	}
	
	public void getTop5(){
		Object[] a =this.fileScoreMap.keySet().toArray();  
		int len=Math.min(5, this.fileScoreMap.keySet().size());
		for (int i = 0; i < len; i++) {
			System.out.println(a[i]+":"+this.fileScoreMap.get(a[i]));			
		}
		System.out.println("\n");
	}
	
	/** 返回值为匹配最为相似的红外码相差的位数 和文件名，用逗号分隔*/
	public String getTop1(){
		Object[] a =this.fileScoreMap.keySet().toArray();  
		int len=Math.min(1, this.fileScoreMap.keySet().size());
		if(len>0){
			return a[0]+","+(this.fileScoreMap.get(a[0]).split("\\|"))[0];
		}else{
			return null;
		}
	}
	
	/** 将匹配的结果按相差位数从小到大排序，结果保存在 map中*/
	public  void match(File file,String c3code) {		
    	Map<Integer, String> lineScoreMap= new TreeMap<Integer, String>();
        if (file.isDirectory()) {
            File[] ch = file.listFiles();
            for (int i = 0; i < ch.length; i++) {
            	match(ch[i],c3code);
            } 
        } else {
            if (file.getName().endsWith("txt")) {
            	int score=Integer.MAX_VALUE;
            	int byteLen=c3code.split(",").length;
            	if(byteLen==getColumnNum(file)){
            		String line=null;
            		try {
						BufferedReader br=new BufferedReader(new FileReader(file));
						while((line=br.readLine())!=null){
							int diff= differceBitCount(line,c3code);
							if(diff<score){
								lineScoreMap.put(diff, line);
								score=Math.min(score, diff);
							}

							if(score==0){
								fileScoreMap.put(score, file.getName()+"|"+line);
								return;
							}
						}	
						fileScoreMap.put(score, file.getName()+"|"+lineScoreMap.get(score));
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
			if(codeArray[i].equals("C3")){
				int c3Len=Integer.parseInt(codeArray[i+2],16); 
				//System.err.println(c3Len);
				int c3byte=(int)Math.ceil(c3Len*1.0/8.0);
				//System.err.println(c3byte);
				for (int j = 0; j < c3byte; j++) {
					c3String=c3String + codeArray[i+3+j] + ",";					
				}
			}
		}
		System.out.println("C3code= "+c3String);
		return c3String;		
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
	

	public static void main(String[] args) {
	
		//IRMatch2 im2=new IRMatch2();
		//System.out.println(im2.differceBitCount("23,CB,26,01,00,20,08,07,09,00,00,00,00,4D,","23,CB,26,01,00,20,08,07,08,00,00,00,00,4C"));

		
		String[] raw=new String[21];
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
		    
	    //System.out.println(im.getC3(raw16));

	    for (int i = 0; i < 21; i++) {
	    	System.out.println(i);
	    	IRMatch2 im=new IRMatch2();
			im.match(file, im.getC3(raw[i]));
			String x=im.getTop1();
			System.out.println(x);
		}

		
		
	}

}
