package cooxm.devicecontrol.control;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：6 Jan 2015 18:17:24 
 */

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;  
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.Future;  
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.TimeoutException;  

import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Header;
import cooxm.devicecontrol.socket.Message;
  
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
            System.out.println(result);
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
          	
				JSONObject json=new JSONObject();
    			msg.json=json;
            	msg.json.put("errorCode",LogicControl.TIME_OUT);
            	if(msg.json.has("sender")){
            		msg.json.put("receiver",msg.json.getInt("sender")); 
    				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
            	}
    			msg.json.put("sender",2);

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
    	static Map<String, Message>  msgMap= new ConcurrentHashMap<String, Message>() ;// new HashMap<String, Message>();
        //Collections.synchronizedMap(new HashMap(...));
    	waitForReply(Message msg){
    		this.msg=msg;
    	}
    	
        public Boolean call() {     
           while(true){  
        		int CtrolID;
        		String cookie=null;
        		int commandID;
        		int sender;
        		String key;
        		String originKey;
    			try {
    				if(msg.json.has("CtrolID") && msg.json.has("sender") ){
	    				CtrolID = msg.json.getInt("CtrolID");
			    		sender=msg.json.getInt("sender");
	    				commandID=msg.header.commandID;
	    				cookie=msg.cookie;
	    				key=CtrolID+"_"+commandID;
	    				originKey=CtrolID+"_"+(commandID-0x4000); 
    		    	}else {
						return false;
					}
    				if(commandID>=0x1600 && commandID<=0x19FF ){
    					if (!msgMap.containsKey(key)) {
        					msgMap.put(key, msg);
        					return true;							
						}
    				}else if( commandID>=0x5600 && commandID<=0x59FF && msgMap.containsKey(originKey) && msgMap.get(originKey).cookie==cookie){
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
    				}else {
    					JSONObject json=new JSONObject();
    	    			msg.json=json;
    	    			msg.json.put("errorCode", LogicControl.WRONG_COMMAND);
    	    			msg.json.put("sender",2);
    	    			msg.json.put("receiver",sender);
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
    	Message msg= new Message();
    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=2;
    	short msgLen=15;
    	short commandID=0x1601;
    	int sequeeceNo=123456;
    	byte encType=1; 
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

    	Config conf =MainEntry.getConfig();
    	CtrolSocketServer cServer=new CtrolSocketServer(conf);
    	Callable<Boolean> task= new waitForReply(msg);
    	
    	TimeOutTread to=new TimeOutTread(5,msg);
    	to.start();    
    	System.out.println("没有阻塞");
    }   
    
}  
