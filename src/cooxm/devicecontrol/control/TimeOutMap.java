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

public class TimeOutMap extends HashMap<String,Message>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log =Logger.getLogger(TimeOutTread.class);	
	
	/** 超时时间，单位是秒*/
    static int timeOut;   
    
    public TimeOutMap(){
    	Configure cf=MainEntry.getConfig();
    	timeOut          =Integer.parseInt(cf.getValue("msg_timeout"));
    	new Thread(new TimeOut(this)).start();
    }
    
	@Override
	public Message put(String key, Message msg) {
		//String cookie=msg.getCookie();
		if(!this.containsKey(key)){  //不存在，说明这个命令是一个超时命令
		   msg.setCreateTime(new Date());
	       return super.put(key, msg);
		}else{                      //存在，说明这个命令是一个ack
			Message originMsg=this.get(key);
			if(originMsg==null){
				return null;
			}
			if(msg.getCommandID()-originMsg.getCommandID()==0x4000){			
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
		    	System.out.println("Success, find the origin msg ---------------------------");
		    	return	super.remove(key);  
			}else{
				return null;
			}
		}
	
	}
    
   static class TimeOut  implements Runnable{  
	   TimeOutMap timeOutMap;
	   

	   TimeOut(TimeOutMap t){
		   this.timeOutMap=t;
	   }
	   
		@Override
		public void run() {
			while(true){
				for (Map.Entry<String,Message> entry : this.timeOutMap.entrySet()) {
					long nowTime=new Date().getTime();
					long createTime=entry.getValue().getCreateTime().getTime();
					int  time_diff =(int) ((nowTime-createTime)/(1000));
					if(time_diff>=timeOut){      //超时，返回超时错误
                        Message msg=entry.getValue();
						int sender=0;
						if(msg.getJson().has("sender")){
							   sender=msg.getJson().optInt("sender");
						}
						JSONObject json=msg.getJson();
						try {
							json.put("sender",2);
							json.put("receiver",sender);
							json.put("errorCode", LogicControl.TIME_OUT);
							System.out.println("Timeout, can't find the origin msg ---------------------------");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						msg.setJson(json);						
				    	try {
				    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
						this.timeOutMap.remove(entry.getKey());
					}				
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

   }
    


	


	public static void main(String[] args) throws JSONException, InterruptedException {
		JSONObject json;
		json = new JSONObject("{\"sender\":0,\"receiver\":2}");
		Message msg1=new Message((short)0x1635, "1433128078_15", json);
		Message msg2=new Message((short)0x5635, "1433128078_15", json);
		TimeOutMap tm=new TimeOutMap();
		tm.put(msg1.getCookie(), msg1);

		//Thread.sleep(10);

		tm.put(msg2.getCookie(), msg2);
		
		

	}



}
