package socket;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import util.MySqlClass;
import control.Config;
import control.LogicControl;

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
	private static ReceiveCommandQueue instance = new ReceiveCommandQueue(getCapacity());
	
	private  ReceiveCommandQueue(int capacity) {
		super(capacity);	
	}
	
    public static ReceiveCommandQueue getInstance(){
        return instance;
    }
    private static int getCapacity(){
    	Config conf=new Config();
    	return Integer.parseInt(conf.getValue("max_recv_msg_queue"));    	
    }
    
    @Override
    public boolean offer(Message msg){
    	Event event=new Event(msg);
    	checkMysql();
     	try {
			event.toReceiveDB(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
     	return super.offer(msg);

    }
    
    @Override
    public boolean offer(Message msg, long time, TimeUnit unit) throws InterruptedException {
    	Event event=new Event(msg);
    	checkMysql();
     	try {
			event.toReceiveDB(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return super.offer(msg,time,unit);
    }
    
    
    
    @Override
    public void put(Message msg) throws InterruptedException{
    	Event event=new Event(msg);
    	checkMysql();
     	try {
			event.toReceiveDB(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
    	short encType=1; 
    	short cookieLen=4;
    	int reserve=0;
    	
    	JSONObject json=new JSONObject();
    	json.put("CtrolID", 1234567);
    	json.put("sender", 1);
    	json.put("roomID", 103);
    	json.put("errorCode", -12);

    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
    	msg.header=head;
    	msg.cookie="87654321";
    	msg.json=json;
    	msg.receiveTime=new Date();
    	msg.replyTime=new Date();
    	
    	ReceiveCommandQueue qe=ReceiveCommandQueue.getInstance();
    	qe.put(msg);
    	System.out.println(qe.size());
		

	}

}
