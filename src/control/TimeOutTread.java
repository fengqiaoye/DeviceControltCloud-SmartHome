package control;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 18:17:24 
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;  
import java.util.concurrent.ExecutionException;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.Future;  
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.TimeoutException;  

import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.INTERNAL;

import redis.clients.jedis.Jedis;
import socket.CtrolSocketServer;
import socket.Header;
import socket.Message;
  
public class TimeOutTread extends Thread {  
  	int timOut;
  	Callable<Boolean> task;
  	Message msg;
	
  	TimeOutTread(int timOut,Message msg){
		this.task= new waitForReply(msg);
  		//this.task= new MyJob();
		this.msg=msg;
		this.timOut=timOut;
	}
    
    public void run(){  
        //int timeout = 10; //秒.  
        ExecutorService executor = Executors.newSingleThreadExecutor();  
        Boolean result = false;     
        Future<Boolean> future = executor.submit(this.task);// 将任务提交到线程池中     
        try {  
            System.out.println(new Date());  
            result = future.get(timOut*1000, TimeUnit.MILLISECONDS);// 设定在2000毫秒的时间内完成   
            
            System.out.println(new Date());
        } catch (InterruptedException e) {  
            System.out.println("线程中断出错。");  
            future.cancel(true);      // 中断执行此任务的线程     
        } catch (ExecutionException e) {     
            System.out.println("线程服务出错");  
            e.printStackTrace();
            future.cancel(true);      // 中断执行此任务的线程     
        } catch (TimeoutException e) {// 超时异常     
        	
            System.out.println("超时"); 
            try {
            	//msg.json=new JSONObject();
            	msg.json.put("errorCode",LogicControl.TIME_OUT);
            	if(msg.json.has("sender")){
    			msg.json.put("receiver",msg.json.getInt("sender")); 
            	}else {
            		msg.json.put("receiver",0);
            	}
    			msg.json.put("sender",2);
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | JSONException e1) {
				e1.printStackTrace();
			}
            future.cancel(true);      // 中断执行此任务的线程
        }finally{  
            System.out.println("线程服务关闭");  
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
    	static Map<String, Message>  msgMap= new HashMap<String, Message>();
    	waitForReply(Message msg){
    		this.msg=msg;
    	}
    	
        public Boolean call() {     
           while(true){  
        		int CtrolID;
        		String cookie;
        		int commandID;
    			try {
    				CtrolID = msg.json.getInt("CtrolID");
    				commandID=msg.header.commandID;
    				cookie=msg.cookie;
    				String key=CtrolID+"_"+commandID;
    				String originKey=CtrolID+"_"+(commandID-0x4000);
    				if(commandID>=0x1600 && commandID>=0x19FF ){
    					msgMap.put(key, msg);
    					return true;
    				}else if( commandID>=0x5600 && commandID>=0x59FF && msgMap.containsKey(originKey) && msgMap.get(originKey).cookie==cookie){
        	    		try {
        	    			msg.json.put("sender",2);
        	    			msg.json.put("receiver",0);  
							boolean t=CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
							if(t==true){
								return true;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        	    		msgMap.remove(originKey);
    				}
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
            	
                if (Thread.interrupted()){ //很重要  
                    return false;     
                }  
            }
   
        }     
    }  
    
  
    
    public static void main(String[] args) throws JSONException{
    	Message msg= new Message();
    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=2;
    	short msgLen=15;
    	short commandID=0x1601;
    	int sequeeceNo=123456;
    	short encType=1; 
    	short cookieLen=4;
    	int reserve=0;
    	
    	JSONObject json=new JSONObject();
    	json.put("CtrolID", 1234567);
    	json.put("sender", 1);
    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
    	msg.header=head;
    	msg.cookie="87654321";
    	msg.json=json;
     	System.out.println(msg.json.toString());

    	Config conf =new Config();
    	CtrolSocketServer cServer=new CtrolSocketServer(conf);
    	Callable<Boolean> task= new waitForReply(msg);
    	
    	TimeOutTread to=new TimeOutTread(5,msg);
    	to.start();    
    	System.out.println("没有阻塞");
    }   
    
}  
