package cooxm.devicecontrol.control;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：May 31, 2015 2:21:59 PM 
 * Map<cookieID,msg>
 */

public class TimeOutMap extends HashMap<String,Message> implements Runnable {
	
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
    }
    
	@Override
	public Message put(String key, Message msg) {
		//String cookie=msg.getCookie();
		if(!this.containsKey(key)){  //不存在，说明这个命令是一个命令
			if(!msg.isAck()){        //不是ACK,是原命令
			   msg.setCreateTime(new Date());
		       return super.put(key, msg);
			}else{
				return null;
			}
		}else{                      //存在，说明这个命令是一个ack
			Message originMsg=this.get(key);
			if(originMsg==null){   //说明已经已经超时
				return null;
			}
			if(msg.getCommandID()-originMsg.getCommandID()==0x4000){	//没有超时		
				JSONObject json=msg.getJson();
				try {
					json.put("sender",2);
					if(originMsg.getJson().has("sender")){
						 json.put("receiver",originMsg.getJson().getInt("sender")); 
					}else{
					     json.put("receiver",1); 
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				msg.setJson(json);
				msg.setServerID(originMsg.getServerID());
		    	try {
		    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
		    	//System.out.println("Success, find the origin msg ---------------------------");
		    	return	super.remove(key);  
			}else{
				return null;
			}
		}
	
	}
	
	@Override
	public void run() {
		while(true){
			//for (Map.Entry<String,Message> entry : this.entrySet()) {  //遍历时删除会有问题
			Iterator<Map.Entry<String,Message>> it = this.entrySet().iterator(); 
			while(it.hasNext()){
			    Map.Entry<String,Message> entry=it.next();  
				long nowTime=new Date().getTime();
				long createTime=entry.getValue().getCreateTime().getTime();
				int  time_diff =(int) ((nowTime-createTime)/(1000));
				long timeOutMax=Math.max(timeOut, entry.getValue().getTimeOut());  //取默认timeOut 和Message timeout较大者
				if(time_diff>=timeOutMax){      //超时，返回超时错误
                    Message msg=entry.getValue();
					JSONObject json=msg.getJson();
					try {

						json.put("errorCode", LogicControl.TIME_OUT);
						log.error("Timeout, havn't received ACK for msg: "+msg.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					msg.setCommandID((short) (msg.getCommandID()+LogicControl.COMMAND_ACK_OFFSET));
					msg.setJson(json);						
			    	try {
			    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
					this.remove(entry.getKey());
					break;
					//System.out.println("after remove, size="+this.size());
				}				
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
    
 /*  static class TimeOut  implements Runnable{  
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
							//System.out.println("Timeout, hav't received ACK ---------------------------");
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
						//System.out.println("after remove, size="+timeOutMap.size());
					}				
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

   }*/
    


	


	public static void main(String[] args) throws JSONException, InterruptedException {
		JSONObject json;
		json = new JSONObject("{\"sender\":0,\"receiver\":2}");
		Message msg1=new Message((short)0x1635, "1433128078_15", json);
		Message msg2=new Message((short)0x5635, "1433128078_15", json);
		TimeOutMap tm=new TimeOutMap();
		new Thread(tm).start();
		
		tm.put(msg1.getCookie(), msg1);
		System.out.println("insert origin");
		System.out.println("size of map="+tm.size());

		Thread.sleep(20);

		tm.put(msg2.getCookie(), msg2);
		System.out.println("size of map="+tm.size());
		
		

	}



}
