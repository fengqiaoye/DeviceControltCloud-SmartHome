 	JSONArray     JSONStringer   Ў HashMap    java    	
 AMP    Boolean    XML    void    myArrayList   √ long    QUOT    io   
 mode   
 EQ    Cookie    String    	
 Float   ° String[]   ° char    top   Ї org    	
 boolean    Long    Field   ° comma   Ї 
JSONObject   
  JSONObject$Null    writer   
 TRUE    
JSONWriter   
 util    entity   Є 
JSONString   	 
XMLTokener    Integer    SLASH    IOException    cause   · Iterator    StringBuilder     int    LT    Double    Null    Writer    JSONTokener     Set   ° stack   Ї FALSE    GT    HTTPTokener    Map       HTTP/0/! /org.json/    ¤ 2JSONObject/1/!раА/org.json/(Ljava\util\Map;)V/map/    ° 4JSONTokener/1/!раА/org.json/(Ljava\lang\String;)V/s/    ї JSONString/#/╪Б /org.json   ў MJSONObject/2/!раА/org.json/(Lorg\json\JSONObject;[Ljava\lang\String;)V/jo,sa/    ° 8JSONObject/1/!раА/org.json/(Ljava\lang\String;)V/string/    ° 7JSONArray/1/!раА/org.json/(Ljava\lang\String;)V/string/    √ 	Null/0/тАА   ∙ 4HTTPTokener/1/!раА/org.json/(Ljava\lang\String;)V/s/    № QJSONObject/2/!раА/org.json/(Ljava\lang\Object;[Ljava\lang\String;)V/object,names/    ° CookieList/0/! /org.json/    ■ CDL/0/! /org.json/      Cookie/0/! /org.json/      JSONObject/0/! /org.json/    ° JSONStringer/0/! /org.json/    Ў JSONArray/0/! /org.json/    √ 3XMLTokener/1/!раА/org.json/(Ljava\lang\String;)V/s/    Є <JSONException/1/!раА/org.json/(Ljava\lang\String;)V/message/    · 9JSONException/1/!раА/org.json/(Ljava\lang\Throwable;)V/t/    · 1JSONWriter/1/!раА/org.json/(Ljava\io\Writer;)V/w/    Ї 7JSONObject/1/!раА/org.json/(Lorg\json\JSONTokener;)V/x/    ° 6JSONArray/1/!раА/org.json/(Lorg\json\JSONTokener;)V/x/    √ ?JSONArray/1/!раА/org.json/(Ljava\util\Collection;)V/collection/    √ XML/0/! /org.json/    є    Null/0   ° JSONArray/0     JSONArray/1    JSONObject$Null/0   ° XMLTokener/1   є StringBuilder/1     JSONTokener/1     	Integer/1    JSONObject/1    JSONException/1    ArrayList/0   √ ArrayList/1   √ StringWriter/0   Ў Character/1   є JSONWriter/1   Ў Long/1    JSONObject/0    	HashMap/1    Exception/1   · Double/1    	HashMap/0   ° HTTPTokener/1   ¤ Object/0   
  StringBuffer/0   	  StringBuffer/1       Null/org.json/JSONObject/    ∙ JSONWriter/org.json//!    Ї XMLTokener/org.json//!    Є HTTPTokener/org.json//!    № JSONString/org.json//╪Б    ў JSONException/org.json//!    · XML/org.json//!    є Cookie/org.json//!      CDL/org.json//!      HTTP/org.json//!    ¤ CookieList/org.json//!    ■ JSONArray/org.json//!    √ JSONObject/org.json//!    ° JSONTokener/org.json//!    ї JSONStringer/org.json//!    Ў    +Object/java.lang/JSONTokener///org.json/CC!   ї *Object/java.lang/CookieList///org.json/CC!   ■ *Object/java.lang/JSONObject///org.json/CC!   ° #Object/java.lang/CDL///org.json/CC!     )Object/java.lang/JSONArray///org.json/CC!   √ &Object/java.lang/Cookie///org.json/CC!     $Object/java.lang/HTTP///org.json/CC!   ¤ #Object/java.lang/XML///org.json/CC!   є .Object/java.lang/Null/JSONObject//org.json/CC   ∙ *Object/java.lang/JSONWriter///org.json/CC!   Ї 'JSONTokener/org.json/XMLTokener///0/CC!   Є (JSONTokener/org.json/HTTPTokener///0/CC!   № (JSONWriter/org.json/JSONStringer///0/CC!   Ў 0Exception/java.lang/JSONException///org.json/CC!   ·   |      ▓    	fieldDecl   ▒ 	methodRef  │ 
methodDecl  ╧ ref  P constructorDecl  $ constructorRef  х typeDecl  ▌ superRef  З                                                                                                                                                                                                                                                                                                                                                           Sline=plate+",";
				plate=null;
				break;
			case POSINFO_LONGITUDE:
				long lon=0;
				if(unit_len==2) {
				} else if(unit_len==4)
					lon = bytesToInt(unit_value);

				double dLon=lon/1000000.0;
				df2.applyPattern("0.000000"); 
				GPSline=GPSline+df2.format(dLon)+",";
				break;
			case POSINFO_LATITUDE:
				long lan=0;
				if(unit_len==2)
					lan = bytesToShort(unit_value);
				else if(unit_len==4)
					lan = bytesToInt(unit_value);
				else if(unit_len==8)
					lan = bytesToLong(unit_value);

				double dLan=lan/1000000.0;
				df2.applyPattern("0.000000");
				GPSline=GPSline+df2.format(dLan)+",";
				break;
			case POSINFO_REPORT_TIME:				

				df2.applyPattern("00"); 
				//System.out.println(df2.format(1.2));
				
				tempshort1 =(short) bytesToShort(unit_value);

				tempshort2 = (short)unit_value[2] ;

				tempshort3 = (short) unit_value[3] ;

				tempshort4 =  (short) unit_value[4] ;

				tempshort5 =  (short) unit_value[5] ;

				tempshort6 = (short) unit_value[6] ;
				//System.out.println("	Date: "+tempshort1+"-"+tempshort2+"-"+tempshort3+"-"+tempshort4+"-"+tempshort5+"-"+tempshort6+"\t");
				GPSline=GPSline+tempshort1+"-"+df2.format(tempshort2)+"-"+df2.format(tempshort3)+
						" "+df2.format(tempshort4)+":"+df2.format(tempshort5)+":"+df2.format(tempshort6)+",";
				break;
			case POSINFO_DEV_ID:
				long sim =0;
				if(unit_len==2)
					sim = bytesToShort(unit_value);
				else if(unit_len==4)
					sim = bytesToInt(unit_value);
				else if(unit_len==8)
					sim= bytesToLong(unit_value);

				//System.out.println("	Device ID:"+tempint+"\t");
				GPSline=GPSline+sim+",";
				break;
			case POSINFO_SPEED:	

				tempshort1 = (short) bytesToShort(unit_value);
				//System.out.println("	Speed:"+tempshort1+"\t");
				GPSline=GPSline+df2.format(tempshort1) +",";
				break;
			case POSINFO_DIRECTION:	
				if(unit_len>=2){
				tempshort1 = (short) bytesToShort(unit_value);
				//System.out.println("	Bearing:"+tempshort1+"\t");
				//df2.applyPattern("000"); 
				GPSline=GPSline+(short)(tempshort1/100) +",";
				}
				break;
			case POSINFO_LOCATION_STATUS:
				tempchar = (char) unit_value[0];
				tempshort1 = (short) tempchar;
				//System.out.println("	positioning status:"+tempshort1+"\t");
				//GPSline=GPSline+tempshort1 +",";
				break;
			case ALARMINFO_SIM_NUMBER:
				plate=new String(unit_value,"GBK");
				//System.out.println("	SIM NO.:"+plate+"\t");
				//GPSline=GPSline+plate+",";
				plate=null;
				break;
			case ALARMINFO_CAR_STATUS:
				tempchar = (char) unit_value[0];
				tempshort1 = (short) tempchar;
				//System.out.println("	With passenger:"+tempshort1+"\t");
				GPSline=GPSline+tempshort1 +",";
				break;
			case ALARMINFO_CAR_COLOUR:
				plate=new String(unit_value,"GBK");
				//System.out.println("	Car Color:"+plate+"\n");
				GPSline=GPSline+plate +"\n";
				plate=null;		
				
				new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				new SimpleDateFormat("yyyy-MM-dd");
				new SimpleDateFormat("yyyy-MM-dd-HH-");
				df2.applyPattern("00"); 
				
				Date nowDate=new Date();
				System.getProperty("user.dir");				 
				 int min=nowDate.getMinutes();
				nowDate.getSeconds();
				if(min<30 ){min=00;	}
				else if(min>=30){min=30;}
				return GPSline;
				
			default:
				System.out.println("\n	### 	Error: can't resort message info!   #### unit_id="+unit_id+"\n");
				Thread.sleep(100);	
				
				break;
			}
		}
		return null;
	}

	
	public static short bytesToShort(byte[] b, int offset) {  
		return (short)    (b[offset + 1] & 0xff <<8 | (b[offset] & 0xff) << 0)   ; 
	}  
	
	public static short bytesToShort(byte[] b) {  
		return (short)( (b[1] & 0xff)<<8 | (b[0] & 0xff) );// << 8);  
	} 
	
	public static long bytesToLong(byte[] array) {  
		return ((((long) array[0] & 0xff) << 0) | (((long) array[1] & 0xff) << 8) | (((long) array[2] & 0xff) << 16)  
				| (((long) array[3] & 0xff) << 24) | (((long) array[4] & 0xff) << 32)  
				| (((long) array[5] & 0xff) << 40) | (((long) array[6] & 0xff) << 48) | (((long) array[7] & 0xff) <<56));  
	}  



	public static int bytesToInt(byte b[]) {  
		return (b[3] & 0xff )<<24 | (b[2] & 0xff )<< 16 | (b[1] & 0xff) << 8 | (b[0] & 0xff) << 0;  
	}	

}
