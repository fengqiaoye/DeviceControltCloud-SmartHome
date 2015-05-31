package cooxm.devicecontrol.control;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：May 31, 2015 2:21:59 PM 
 * Map<cookieID,msg>
 */

public class TimeOutMap extends HashMap<String,Message> implements Runnable{
	
	static Logger log =Logger.getLogger(TimeOutTread.class);	
	
	/** 超时时间，单位是秒*/
    int timeOut;   
    
    @Override
    public Message put(String key, Message msg) {
    	String cookie=msg.getCookie();
    	if(!this.containsKey(key)){  //不存在，说明这个命令是一个超时命令
    	   msg.setCreateTime(new Date());
           return super.put(key, msg);
    	}else{                      //存在，说明这个命令是一个ack
    		int sender=0;
			if(msg.getJson().has("sender")){
				   sender=msg.getJson().optInt("sender");
			}
			JSONObject json=msg.getJson();
			try {
				json.put("sender",2);
				json.put("receiver",sender); 
			} catch (JSONException e) {
				e.printStackTrace();
			}
			msg.setJson(json);
	    	try {
	    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
	    	return msg;    		 
    	}

    }
    
    TimeOutMap(){
    	Configure cf=MainEntry.getConfig();
    	this.timeOut          =Integer.parseInt(cf.getValue("msg_timeout"));
    }

	
	@Override
	public void run() {
		while(true){
			for (Map.Entry<String,Message> entry : this.entrySet()) {
				long nowTime=new Date().getTime();
				long createTime=entry.getValue().getCreateTime().getTime();
				int  time_diff =(int) ((nowTime-createTime)/(1000));
				if(time_diff>=this.timeOut){
					this.remove(entry.getKey());
				}				
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

	}



}
