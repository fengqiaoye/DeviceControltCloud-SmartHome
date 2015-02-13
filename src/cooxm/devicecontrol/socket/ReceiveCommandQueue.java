package cooxm.devicecontrol.socket;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.Configure;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：9 Jan 2015 15:25:50 
 */

public class ReceiveCommandQueue  extends ArrayBlockingQueue<Message>{

	/**
	 * 默认serialVersionUID
	 */
	private static final long serialVersionUID = -8076018759025681161L;

	static MySqlClass mysql;
	private static ReceiveCommandQueue instance = new ReceiveCommandQueue(3000);//getCapacity());
	
	private  ReceiveCommandQueue(int capacity) {
		super(capacity);	
	}
	
    public static ReceiveCommandQueue getInstance(){
        return instance;
    }
    private static int getCapacity(){
    	Configure conf=MainEntry.getConfig();//new Config();//
    	return Integer.parseInt(conf.getValue("max_recv_msg_queue"));    	
    }
    
    @Override
    public boolean offer(Message msg){
    	Event event=new Event(msg);
    	checkMysql();
     	event.toReceiveDB(mysql);
     	return super.offer(msg);

    }
    
    @Override
    public boolean offer(Message msg, long time, TimeUnit unit) throws InterruptedException {
    	if(msg.isValid()){
    	Event event=new Event(msg);
    	checkMysql();
    	
    		event.toReceiveDB(mysql);
    	}
		return super.offer(msg,time,unit);
    }
    
    
    
    @Override
    public void put(Message msg) throws InterruptedException{
    	Event event=new Event(msg);
    	checkMysql();
     	event.toReceiveDB(mysql);
     	super.put(msg);   	
    }

    private static void checkMysql(){
    	MySqlClass mySQL =LogicControl.getMysql();
    	if (mySQL!=null) {
    		mysql=mySQL;			
		}else{
			mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		}    	
    } 

    
    

	public static void main(String[] args) throws JSONException, InterruptedException {
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
    	json.put("ctrolID", 1234567);
    	json.put("sender", 1);
    	json.put("roomID", 103);
    	json.put("errorCode", -12);
    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);    	
    	msg=new Message(head, "87654321", json);
    	
    	ReceiveCommandQueue qe=ReceiveCommandQueue.getInstance();
    	qe.put(msg);
    	System.out.println(qe.size());
	}
}
