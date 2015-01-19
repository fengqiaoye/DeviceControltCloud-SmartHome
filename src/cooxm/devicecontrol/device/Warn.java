/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：17 Dec 2014 19:38:07 
 */
package cooxm.devicecontrol.device;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Warn {	
	

	int  warnID       ;  
	String  warnName     ; 
	String warnContent;
	
	int  type       ; 
	/*** 
	 * 告警渠道： 0：网络； 1：SMS; 2 :both
	 * */
	int  channel    ;  
	Date  createTime ;  
	Date  modifyTime ;   

	Warn(
		int  warnID       ,  
		String  warnName       ,  
		String  warnContent,  
		int  type       ,  
		int  channel    ,  
		Date  createTime ,  
		Date  modifyTime )
	{
		this.warnID     = warnID       ;  
		this.warnName   = warnName       ;  
		this.warnContent= warnContent;  
		this.type       = type       ;  
		this.channel    = channel    ;  
		this.createTime = createTime ;  
		this.modifyTime = modifyTime ;		
	}
	
	public JSONObject toJsonObject(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject warnJson = new JSONObject();  
	    try {
			warnJson.put("warnID",         this.warnID      );
		    warnJson.put("warnName",        this.warnName      );
		    warnJson.put("warnContent",        this.warnContent       );
		    warnJson.put("type",     		   this.type        );
		    warnJson.put("channel",            this.channel    );
		    warnJson.put("createTime",      sdf.format(this.createTime  )  );
		    warnJson.put("modifyTime",      sdf.format(this.modifyTime  )  );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return warnJson;
	}

	/*** 
	 * @Title: main 
	 * @Description: TODO
	 * @param @param args    
	 * @return void    
	 * @throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
