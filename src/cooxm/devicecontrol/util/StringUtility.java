package cooxm.devicecontrol.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cooxm.devicecontrol.synchronize.IRMatch2;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jul 6, 2015 11:24:16 AM 
 */

public class StringUtility {
	
	String reg,left;
	
	public static boolean test1(String s){
		if(s.matches("^.*?(.+?)\\1.*?$")){
		      //System.out.println("有重复");
			return true;
		    }
		return false;
	}
	
	
	public static boolean duplicated(String s) {  //速度更快
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                String t = s.substring(i, j);
                if (s.indexOf(t) > -1  && t.length()>5 ) {
                	System.out.println("duplicate string:"+t);
                    return true;
                }
            }
        }
        return false;
    }
	
	public static int getSubCount_2(String str,String key){
	    int count = 0;
	    int index = 0;
	    while ((index=str.indexOf(key,index))!=-1){
	        str = str.substring(index+key.length());
	        count++;
	    }
	    return count;
	}
	
	public static void longestNodupSubstring(String string) {
		int len = string.length();
		if(len > 0){
			Map<Character,Integer> cursor = new HashMap<Character,Integer>();
			cursor.put(string.charAt(0),0);
			int[] lengthAt = new int[string.length()];
			lengthAt[0]=1;
			int max = 0 ;
			for(int i = 1 ; i < len;i++){
				char c =string.charAt(i);
				if(cursor.containsKey(c)){
					lengthAt[i] = Math.min(lengthAt[i-1]+1, i-cursor.get(c));
                }else {  
                    lengthAt[i] = lengthAt[i-1]+1;  
                }  
                max = Math.max(max, lengthAt[i]);  
                cursor.put(c, i);  
            }  
            for(int i=0;i<len;i++){  
                if(max == lengthAt[i]){  
                    System.out.println(string.substring(i-max+1, i+1));  
                }  
            }  
        }  
    }  
	
	/**获取最长的重复字串 ,重复字符串不存在，则返回空*/
	public  String getLongestSubStr(String str){
		//最长的重复字串，极端情况就比如abcabc，最长重复字串就是abc
		//即为字符串长度的一半，当然这是极端情况，通常都是小于串长一半的
		for(int len=str.length()/2;len>0;len--){
			//将字符串分隔为若干“最长字串”
			for(int i=0;i<len;i++)
				//获取“最长字串”
				reg=str.substring(0,len+1);
				//刨去“最长字串”剩下的串
				left=str.substring(len+1);
				//如果剩下的串里面包含“最长字串”
				if(left.indexOf(reg)!=-1 && reg.length()>=5)
				  return reg;
		}
	   //啥也找不到就返回空吧
	   return null;
   }
	
	public static  void bianli(File file) throws IOException{
		if (file.isDirectory()) {
            File[] ch = file.listFiles();
            for (int i = 0; i < ch.length; i++) {
            	bianli(ch[i]);
            } 
        } else {
            if (file.getName().endsWith("txt")) {
            	BufferedReader br=new BufferedReader(new FileReader(file));
            	String line=null;
            	while(( line=br.readLine())!=null){
            		if(line.charAt(line.length()-1)!=','){
            			line=line+",";
            		}
            		String str=new StringUtility().getLongestSubStr(line);
            		if(str!=null){
	            		int count=getSubCount_2(line,str);
	            		//if(count>=2){
	            		 System.out.println(file.getName()+" : "+line+" : "+str+" : "+count); 
	            		//}
            		}
            	}
            }
        }
	}
	
	
	public static String getNthDuplicateStr(String str,int n){
		String res="";
		for (int i = 0; i < n; i++) {
			res=res+str;			
		}
		return res;		
	}


  
 

	public static void main(String[] args) {
		String path="D:/documents/cooxm/document/Infrared/ird3/keyfiles5/DVD";
		
//		String s = "4F,05,AB,4F,05,AB,4F,05,AB,";		
//		String str=new StringUtility().find(s);
//		int count=getSubCount_2(s,str);
//		System.out.println(count);

//		try {
//			bianli(new File(path));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		String x=StringUtility.getNthDuplicateStr("abc", 3);
//		System.out.println(x);
		
		String x="4F,05,AB,4F,05,AB,4F,05,AB,";
		String x2=new StringUtility().getLongestSubStr(x);
		

	}

}
