package cooxm.devicecontrol.synchronize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.encode.SQLiteUtil;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 2, 2015 3:38:47 PM 
 */

public class IRMatch {
	static Logger log= Logger.getLogger(IRMatch.class);
	/*String rawdata;	
	String iRString;	
	String encode;
	List<String []> ids;*/
	

	/**万能码库格式转成Comsumer IR 格式*/
	private static String getIRString(String rawdata){
		String[] modulea = rawdata.split(",");
		String waves = "";
		int cnt = 15;
		String chr  = modulea[cnt];
		
		while(!chr.equals("00")){
			long hs;
			switch(chr){
			case "C1":
				hs = Long.parseLong(modulea[cnt + 1] + modulea[cnt + 2],16);
				waves += hs + ",";
				cnt += 3;
				break;
			case "C2":
				hs = Long.parseLong(modulea[cnt + 1] + modulea[cnt + 2] + modulea[cnt + 3],16);
		        	waves += hs + ",";
		        	cnt += 4;
				break;
			case "C3":
				long bits = Long.parseLong(modulea[cnt + 1] + modulea[cnt + 2],16);
				int bitt;
				for(int i= 0; i< bits; i++){
					int bytet = i/8;
					if(i%8 == 0){
						//bytet = bytet + 1;
					}
					if((Long.parseLong(modulea[5],16) & 0x0f) == 0){
						bitt = i%8;
					}else{
						bitt = ~((i%8)&7);
					}
					
					if((Long.parseLong(modulea[cnt+3 + bytet],16) & (0x01 << bitt)) == 0){
						hs = (Long.parseLong(modulea[7],16) & 127)*256 + Long.parseLong(modulea[8],16);
						waves +=   hs + ",";
						hs = Long.parseLong(modulea[9] + modulea[10],16);
						waves += hs + ",";
					}else{
						hs = (Long.parseLong(modulea[11],16) & 127)*256 + Long.parseLong(modulea[12],16);
						waves +=   hs + ",";
						hs = Long.parseLong(modulea[13] + modulea[14],16);
						waves += hs + ",";
					}
				}
				//2015-04-10 周先生
				if((Long.parseLong(modulea[5],16) & 240) != 0){
				//if((Long.parseLong(modulea[5],16) & 240) == 0){
					hs = (Long.parseLong(modulea[7],16) & 127) * 256 + Long.parseLong(modulea[8],16);
		            		waves += hs + ",";
				}
	           
				long bytes = bits/8;
				
				if(bits%8 != 0){
					bytes += 1;
				}
				
				cnt += bytes+3;
				break;
			default:
				log.error("数据错误");
				break;
			}
			chr = modulea[cnt];
		}
		
		long fcarr=(Long.parseLong(modulea[6],16) - 18)*1000;
		//log.info("17b6 = " + Long.parseLong(modulea[16] + modulea[18],16));
		float fcycle = (float)1000/(Long.parseLong(modulea[6],16)-18);
		/*log.info("fcycle = " + fcycle);
		String[] wavesa = waves.split(",");
		String wvs="";
		
		for(int i=0; i< wavesa.length; i++ ){
			//log.info( "wavesa[i] = " + wavesa[i]);
			//log.info("Long.parseLong(wavesa[i],10) = " + Long.parseLong(wavesa[i],10));
			//log.info("Long.parseLong(wavesa[i],10)/fcycle = " + Math.round(Long.parseLong(wavesa[i],10)/fcycle));
			wvs += Math.round(Long.parseLong(wavesa[i],10)/fcycle) + ",";
		}
		String makewave = fcarr + "," + wvs + "10";*/
		String makewave = fcarr + "," + waves;
		//log.info("makewave =" + makewave);
		return makewave;
	}
	
	
	//智能匹配java源程序
	public static List<String[]> getID(String dumps){
		String[][] t_score = new String[1500][3];
		String[] dump = dumps.split(",");
		String[][] rds;
		
		for(int i = 0; i< dump.length; i++){
			dump[i] = Integer.toString(Integer.parseInt(dump[i],16));
		}
		
		rds = getAllMaches();
		int rcnt = rds.length;
		
		for(int i=0; i<rds.length; i++){
			if(rds[i][1]==null || rds[i][1].equals("")){
			    t_score[i][0] = "0";  //分数
			    t_score[i][1] = rds[i][0]+"_"+rds[i][3];              //fid
			    t_score[i][2] = rds[i][2];              //型号
				continue;
			}
			String[] mobsa = rds[i][1].split(",");
			
			for(int j=0; j< mobsa.length; j++){
				mobsa[j] = Integer.toString(Integer.parseInt(mobsa[j],16));
			}
			
			int score = 0;
			if(mobsa[32].equals(dump[32]) && mobsa[33].equals(dump[33])){
				score  += 600;
			}
			
			for(int k =0; k< 15; k++){
				int samp = Integer.parseInt(dump[k*2])*256 + Integer.parseInt(dump[k*2 +1]);
				int samp1 = Integer.parseInt(mobsa[k*2])*256 + Integer.parseInt(mobsa[k*2 +1]);
				
				if(samp + samp1 == 0){
					score += 100;
					break;
				}else{
					if(samp != 0 && samp1 != 0){
						float tp = (float)samp /samp1;
						
						if(tp > 1){
							tp = 1/tp;
						}
						if(tp > 0.6){
							score += 50;
						}
					}
				}
			}

		    int lens = Integer.parseInt(dump[32])*256 + Integer.parseInt(dump[33]);
		    int lens1 = Integer.parseInt(mobsa[32])*256 + Integer.parseInt(mobsa[33]);
		    
		    if(lens > lens1){
		    	lens = lens1;
		    }
		    
		    int kcnt = 1;
		    int k = 1;
		    
		    do{
		    	if(Integer.parseInt(dump[k])/16 ==Integer.parseInt(mobsa[k])/16 ){
		    		score += 20;
		    	}
		    	
		    	if(Integer.parseInt(dump[k])%16 ==Integer.parseInt(mobsa[k])%16){
		    		score += 20;
		    	}
		        k += 1;
		        kcnt += 2;
		    }while(lens > kcnt);
		    
		    t_score[i][0] = String.valueOf(score);  //分数
		    t_score[i][1] = rds[i][0]+"_"+rds[i][3];              //fid           
		    t_score[i][2] = rds[i][2];              //
	
		}

		for(int i=0; i< rcnt; i++){
			for(int j=i+1; j< rcnt; j++){
				if(Integer.parseInt(t_score[i][0]) < Integer.parseInt(t_score[j][0])){
					String tmp = t_score[i][0];
					String tmp1 = t_score[i][1];
					String tmp2 = t_score[i][2];
					
					t_score[i][0] = t_score[j][0];
					t_score[i][1] = t_score[j][1];
					t_score[i][2] = t_score[j][2];
					
					t_score[j][0] = tmp;
					t_score[j][1] = tmp1;
					t_score[j][2] = tmp2;
				}
			}
		}
		
		String rets = t_score[0][1] + "-" + t_score[0][0] + " - "+ t_score[0][2]+ "\r\n"+
				      t_score[1][1] + "-" + t_score[1][0] + " - "+ t_score[1][2]+ "\r\n"
				 	+ t_score[2][1] + "-" + t_score[2][0] + " - "+ t_score[2][2]+"\r\n"
				 	+ t_score[3][1] + "-" + t_score[3][0] + " - "+ t_score[3][2]+"\r\n"
				 	+ t_score[4][1] + "-" + t_score[4][0] + " - "+ t_score[4][2]+"\r\n";
				 	//+ t_score[5][1] + "-" + t_score[5][0] + " - "+ t_score[5][2] ;
		log.info("\ngetid rets = \n" + rets);
//		return rets;
		List<String[]> format= new ArrayList<String[]>();
		format.add( t_score[0]);
		format.add( t_score[1]);
		format.add( t_score[2]);
		format.add( t_score[3]);
		format.add( t_score[4]);
		
		return format;
		
	}
		
	public static String[][] getAllMaches(){
		
		SQLiteUtil sqlite=new SQLiteUtil("ird5.db");
		String sql="select fid,matchs,format_name,device_id from formats ;";
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
	
	
	/** Comsumer IR格式转成适合匹配的 */
	public static String encode(String wavas){
		String[] dump = new String[512];
		String[] machd = new String[512];
		float fcycle;
		String[] wava = wavas.split(",");
		
		for(int i=0; i<dump.length; i++){
			dump[i] = "0";
		}
		
		fcycle =  Math.round((float)1000000*1.0/Integer.parseInt(wava[0],10));
		//log.info("fcycle = " + fcycle + ", wava[0] = " + wava[0]);
		for(int i=1; i< wava.length; i++){
			// 2015-04-03 周先生 更改			
			//wava[i] = Integer.toString(Math.round(Integer.parseInt(wava[i])*fcycle), 10);
			wava[i] = Integer.toString((int)Math.round(Integer.parseInt(wava[i])/100.0), 10);
			//log.info("wava[" + i + "] = " + wava[i]);
		}
		
		for(int i=1; i< wava.length-1; i++){
			for(int j=i+1; j < wava.length; j++){
				float brt = (float)Integer.parseInt(wava[i]) / Integer.parseInt(wava[j]);
				//log.info("brt = " + brt);
				
				if(brt > 1){
					brt = 1/brt;
				}
				
				//log.info("brt2 = " + brt);
				if(brt > 0.6){
					wava[j] = wava[i];
				}
			}
			
		}
		
		 int cnt = 0;
		 
		 for(int i=1; i < wava.length; i++){
			 String samples = wava[i];
			 int finds = 0;
			 for(int j=0; j<cnt; j++){
				 if(samples.equals(dump[j])){
					 finds = 1;
					 break;
				 }
			 }
			 
			 if(finds == 0){
				 dump[cnt] = samples;
				 //log.info("dump[" + cnt + "] = " + dump[cnt]);
				 cnt++;
			 }
		 }
		 cnt--;
		 
		 if(cnt >32){
			 log.info("sample > 32.");
		 }
		 //log.info("cnt = " + cnt);
		 for(int i=0; i<= cnt-1; i++){
			 for(int j= i+1; j<= cnt; j++){
				 if(Long.parseLong(dump[i]) > Long.parseLong(dump[j])){
					 String tmp = dump[i];
					 dump[i] = dump[j];
					 dump[j] = tmp;
				 }
			 }
		 }
		 
		 String bitstring = "";
		 for(int i=1; i< wava.length; i++){
			 String samples = wava[i];
			 int j = 0;
			 for(j=0; j<= cnt; j++){
				 if(samples.equals(dump[j])){
					 bitstring += (j+1) + ",";
					 //log.info( "bitstring = " + bitstring);
					 break;
				 }
			 }
			 
			 if(j == cnt+1){
				 log.info("No Samples.");
			 }
		 }
		 
		 int bcnt = 1;
		 for(int i = 0; i<16; i++ ){
			 // 2015-04-03 周先生
			 //int samps = (int)Long.parseLong(dump[i])/100;
			 int samps = (int)Long.parseLong(dump[i]);
			 
			 machd[bcnt] = Integer.toHexString(samps/256);
			 machd[bcnt+1] = Integer.toHexString(samps%256);
			 bcnt += 2;
		 }
		 
		 String[] bita = bitstring.split(",");
		 int bits = bita.length;
		 
		 machd[bcnt] = Integer.toHexString(bits/256);
		 machd[bcnt + 1] = Integer.toHexString(bits%256);
		 bcnt += 2;
		 for(int i = 0; i< bits-1;){
			 machd[bcnt] = Integer.toHexString(Integer.parseInt(bita[i])) + Integer.toHexString(Integer.parseInt(bita[i+1]));
			 if("".equals(machd[bcnt])){
				 break;
			 }
			 bcnt++;
			 i += 2;
		 }
		 
		 String rets = "";
		 for(int i= 1; i< machd.length; i++){
			 if("null".equals(machd[i])){
				 break;
			 }
			 rets += machd[i] + ",";
		 }
		 //log.info("rets = " + rets);
       String ret= rets.replaceAll("null,", "");
		return ret;
	}
	
	public static List<Set<String>> getModels(List<String[]> fids){
		List<Set<String>> models=new ArrayList<Set<String>>() ;
		SQLiteUtil sqlite=new SQLiteUtil("ird5.db");
		for(int i=0;i<fids.size();i++){
			String sql="select m_search_string from model where m_format_id='"+fids.get(i)[1]+"';";
			//System.out.println(sql);
			String rs=sqlite.select(sql);
			//System.out.println(rs);
			String[] line=rs.split("\n");
			Set<String> modelSet=new HashSet<>();
			for (int j = 0; j < line.length; j++) {
				modelSet.add(line[j]);
			}			
			models.add(modelSet);	

		}
		
		return models;		
	}
	
	/** 返回值：<  model_name,品牌 > */
	public static TreeMap<String, String> getModels2(List<String[]> fids){
		//TreeMap<String, String> models=new TreeMap<String, String> () ;	
		TreeMap<String, String> models = new TreeMap<String, String>(new Comparator<String>() {  
            public int compare(String d1, String d2) {  
                return d2.compareTo(d1);  
            }  
        }); 
		
		SQLiteUtil sqlite=new SQLiteUtil("ird5.db");
		for(int i=0;i<fids.size();i++){
			String[] fid=fids.get(i)[1].split("_");
			String sql="select m_search_string,m_label from model where cast(m_format_id as int)='"+fid[0]+"'  and device_id="+fid[1]+";";
			System.out.println(sql);
			String rs=sqlite.select(sql);
			//System.out.println(rs);
			String[] line=rs.split("\n");
			for (int j = 0; j < line.length; j++) {
				String cell[]=line[j].split(";");
				String brand=cell[0].split(" ")[0];
				boolean flag=false;
				for (Entry<String, String> entry:models.entrySet()) {
					if(((String)entry.getKey()).endsWith(cell[0])){
						flag=true;
					}					
				}
				if(flag==false){
					if(cell.length<=1){	
						models.put(fids.get(i)[0]+","+cell[0], brand);
					}else{
						models.put(fids.get(i)[0]+","+cell[0], brand+" "+cell[1]);
					}
				}
			}			
		}		
		return models;		
	}
	
	public TreeMap<Object,Object> compositorMap(TreeMap<String ,String> sortWordMap) throws Exception  
    {  
        //@SuppressWarnings("unchecked")
		TreeMap<Object,Object> tm = new TreeMap<Object,Object>(new Comparator<Object>()  
        {  
            public int compare(Object o1, Object o2)             
            {  
            	 //return o2.compareTo(o1);  
                return o1.hashCode() - o2.hashCode();  
            }  
        });  
          
        tm.putAll(sortWordMap);  
          
        return tm;  
    }
	
	public static void main(String[] args) {
		
		String[] raw=new String[33];
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
        raw[18]="2A,04,00,00,24,00,26,81,8E,01,8E,81,8E,06,8F,C1,D,6F,C2,00,06,8F,C3,00,88,40,00,14,80,43,02,A8,EE,23,00,68,00,00,2,00,00,55,00";
        raw[19]="33,04,00,00,24,00,26,82,35,02,35,82,35,06,58,C1,11,5E,C2,00,11,5E,C3,00,30,4D,B2,F8,07,1B,E4,C2,00,11,5E,C1,11,5E,C2,00,11,5E,C3,00,30,4D,B2,F8,07,1B,E4,00";
        raw[20]="27,04,00,00,24,00,26,81,FC,01,FC,81,FC,05,F8,C1,0E,1B,C2,00,05,F8,C3,00,70,23,CB,26,01,00,24,8,07,09,00,00,00,00,51,00";
        
        raw[21]="33,04,00,00,24,00,26,81,AE,02,81,81,AE,06,D4,C1,10,BD,C2,00,11,94,C3,00,30,4D,B2,DE,21,07,F8,C2,00,14,C3,C1,10,BC,C2,00,11,92,C3,00,30,4D,B2,DE,21,07,F8,00";
        raw[22]="33,04,00,00,24,00,26,81,9F,02,93,81,9F,06,E3,C1,10,AE,C2,00,11,A0,C3,00,30,4D,B2,F8,07,1A,E5,C2,00,14,D4,C1,10,AA,C2,00,11,A4,C3,00,30,4D,B2,F8,07,1A,E5,00";
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
	    /* 以下为TCL电视  */
	    raw[32]="3e,04,00,00,24,00,26,81,ea,03,f5,81,ec,07,da,c1,0f,99,c2,00,0f,9d,c3,00,18,4f,05,ab,c2,00,21,0f,c1,0f,94,c2,00,0f,9e,c3,00,18,4f,05,ab,c2,00,21,0f,c1,0f,93,c2,00,0f,9d,c3,00,18,4f,05,ab,00,";
	    for (int k = 32; k < raw.length; k++) {
    		System.out.println(k);
    	    String wave=getIRString(raw[k].toUpperCase());
    	    System.out.println("wave="+wave);
    		String encode=encode(wave.substring(0, wave.length()-1));
    		System.out.println("encode="+encode);
    		List<String[]> fids =getID(encode);
    		Map<String, String> models=getModels2(fids);
    		/*List<Set<String>> models=getModels(fids); 
    		for (int i = 0; i < fids.size(); i++) {
    			System.out.println(fids.get(i)[2]+":");
    			for (int j = 0; j < models.get(i).size(); j++) {
    				System.out.println("\t"+models.get(i).toArray()[j]);	
    			}			
    		}*/

    		for (Map.Entry<String, String> entry:models.entrySet()) {
    			System.out.println(entry.getValue()+" : "+entry.getKey());				
			}
    		System.out.println("\n");
		}

	}

}
