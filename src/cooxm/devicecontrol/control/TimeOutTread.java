﻿package cooxm.devicecontrol.control;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 18:17:24 
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;  
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.Future;  
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.TimeoutException;  

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Header;
import cooxm.devicecontrol.socket.Message;
  
public class TimeOutTread extends Thread {  
	static Logger log =Logger.getLogger(TimeOutTread.class);
	
  	int timOut;
  	Callable<Boolean> task;
  	Message msg;

	
  	TimeOutTread(int timOut,Message msg){
		this.task= new waitForReply(msg);
  		//this.task= new MyJob();
		this.msg=msg;
		this.timOut=timOut;
	}
  
  static	public String getKey(Message msg){
  		int ctrolID;
  		int commandID;
  		String cookie;
		if(msg.getJson().has("ctrolID") && msg.getJson().has("sender") ){
			try {
				ctrolID = msg.getJson().getInt("ctrolID");
				commandID=msg.getCommandID();
				cookie=msg.getCookie();
				return ctrolID+"_"+commandID+"_"+cookie;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//originKey=ctrolID+"_"+(commandID-0x4000)+cookie; 
    	}else {
			return null;
		}
		return null;
  	}
  	
  	public static String getOriginKey(Message msg){
  		int ctrolID;
  		int commandID;
  		String cookie;
		if(msg.getJson().has("ctrolID") && msg.getJson().has("sender") ){
			try {
				ctrolID = msg.getJson().getInt("ctrolID");
				commandID=msg.getCommandID();
				cookie=msg.getCookie();
				return ctrolID+"_"+(commandID-0x4000)+"_"+cookie;
			} catch (JSONException e) {
				e.printStackTrace();
			}

    	}else {
			return null;
		}
		return null;
  	}
    
    public void run(){  
        //int timeout = 10; //秒.  
        ExecutorService executor = Executors.newSingleThreadExecutor();  
        Boolean result = false;     
        Future<Boolean> future = executor.submit(this.task);// 将任务提交到线程池中   
       
        try {  

            result = future.get(timOut*1000, TimeUnit.MILLISECONDS);// 设定在5000毫秒的时间内完成   
            System.out.println(this.msg.getCommandID()+" 线程执行结果："+result);
        } catch (InterruptedException e) {  
            System.out.println(this.msg.getCommandID()+"线程中断出错"+this.msg.getCommandID());  
            future.cancel(true);      // 中断执行此任务的线程     
        } catch (ExecutionException e) {     
            System.out.println(this.msg.getCommandID()+"线程服务出错");  
            e.printStackTrace();
            future.cancel(true);      // 中断执行此任务的线程     
        } catch (TimeoutException e) {// 超时异常             	
            log.error("Request time out,didn't get responce in 10 seconds for Message:"+msg.toString()); 
            try {          	
				JSONObject json=new JSONObject();
            	json.put("errorCode",LogicControl.TIME_OUT);
            	if(msg.getJson().has("sender")){
            		json.put("receiver",msg.getJson().getInt("sender"));     				
            	}else{
            		json.put("sender",2);
            	}    			
    			msg.setJson(json);
    			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | JSONException e1) {
				e1.printStackTrace();
			}
            //log.warn(this.msg.getCommandID()+"线程取消，comanid："); 
            future.cancel(true);      // 中断执行此任务的线程

        }finally{  
        	//System.err.println("size of msgMap:"+waitForReply.msgMap.size()+",key:"+waitForReply.msgMap.keySet().toString()+",Command:"+msg.getCommandID());
            //System.out.println(this.msg.getCommandID()+" finally 线程服务关闭!");  
            executor.shutdown();  
        }  
    }  
    
    static class MyJob implements Callable<Boolean> {   
        public Boolean call() {     
            while(true){   	
            	
                if (Thread.interrupted()){ //很重要  
                    return false;     
                }  
            }
    
        }     
    }
      
    static class waitForReply implements Callable<Boolean> {   
    	Message msg;
    	static Map<String, Message>  msgMap= new ConcurrentHashMap<String, Message>() ;// ctrolID_commandID_cookie
    	Boolean hasKey=false;
        //Collections.synchronizedMap(new HashMap(...));
    	waitForReply(Message msg){
    		this.msg=msg;
    	}
    	
        public Boolean call() {
        	String key=getKey(msg);
        	String originKey=getOriginKey(msg);
        	int commandID=msg.getCommandID();
        	int sender=0;
			try {
				sender = msg.getJson().getInt("sender");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
           while(true){  
    			try {    				
    				if(commandID>=0x1600 && commandID<=0x19FF ){
    					if (!msgMap.containsKey(key)) {
							if(hasKey==false){
								msgMap.put(key, msg);
								hasKey=true;								
							}else{
								 return true;
							}					
						}
    				}else if( commandID>=0x5600 && commandID<=0x59FF ){
    					//System.out.println("进入 reply线程");
    	    				if(msgMap.containsKey(originKey) && msgMap.get(originKey).getCookie()==msg.getCookie())
		        	    		try {	
		        	    			JSONObject json=msg.getJson();
		        	    			json.put("sender",2);
		        	    			json.put("receiver",0);  
		        	    			msg.setJson(json);
									boolean t=CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
									if(t==true){
										msgMap.remove(originKey);
										return true;
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
							}
    				}else {
    					JSONObject json=new JSONObject();
    	    			json.put("errorCode", LogicControl.WRONG_COMMAND);
    	    			json.put("sender",2);
    	    			json.put("receiver",sender);
    	    			json.put("originalSenderRole", sender);
    	    			msg.setJson(json);
						CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);

					    return false;
					}
    			} catch (JSONException | InterruptedException  e) {
    				e.printStackTrace();
    			}
            	
                if (Thread.interrupted()){    //很重要  
                    return false;     
                }  
            }
   
        }     
    }  
    
  
    
    public static void main(String[] args) throws JSONException{
    	String str1="{\"ctrolID\":12345678,\"sender\":0,\"student\":[{\"name\":\"leilei\",\"age\":23},{\"name\":\"leilei02\",\"age\":23}]}";
    	Header head=Message.getOneHeaer((short)0x1601);
    	Message msg=new Message(head, "87654321", str1);    	
    	int ctrolID = msg.getJson().getInt("ctrolID");

        //waitForReply.msgMap.put(getKey(msg), msg)	;

    	Configure conf =new Configure();
    	//CtrolSocketServer cServer=new CtrolSocketServer(conf);
    	Callable<Boolean> task= new waitForReply(msg);
    	
    	TimeOutTread to=new TimeOutTread(5,msg);
    	to.start();		
    	

    	
     	
    	Header head2=Message.getOneHeaer((short)0x5601);
    	Message msg2 = new Message(head2, "87654321", str1); 
    	TimeOutTread to2=new TimeOutTread(5,msg2);
    	to2.start(); 
    }   
    
}  
