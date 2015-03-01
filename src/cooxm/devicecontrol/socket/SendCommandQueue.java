package cooxm.devicecontrol.socket;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

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

public class SendCommandQueue  extends ArrayBlockingQueue<Message>{

	/**
	 * 默认serialVersionUID
	 */
	private static final long serialVersionUID = -8076018759025681161L;
	
	static MySqlClass mysql;

	private static SendCommandQueue instance = new SendCommandQueue(3000);//getCapacity());
	
	private  SendCommandQueue(int capacity) {
		super(capacity);	
	}
	
    public static SendCommandQueue getInstance(){
        return instance;
    }
    private static int getCapacity(){
    	Configure conf=MainEntry.getConfig();//new Config();//
    	return Integer.parseInt(conf.getValue("max_send_msg_queue"));    	
    }
    
    private static void checkMysql(){
    	if (mysql==null ||mysql.isClosed()) {
    		mysql=LogicControl.getMysql();			
		}  	
    }
    
    @Override
    public boolean offer(Message msg){
    	Event event=new Event(msg);
    	checkMysql();
    	if(msg.isValid()){
	     	try {
				event.toReplyDB(mysql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
     	return super.offer(msg);

    }
    
    @Override
    public boolean offer(Message msg, long time, TimeUnit unit) throws InterruptedException{
    	if(msg.isValid()){
	    	Event event=new Event(msg);
	    	checkMysql();
	     	try {
				event.toReplyDB(mysql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
			return super.offer(msg,time,unit);
    }
    
    
    
    @Override
    public void put(Message msg){
    	Event event=new Event(msg);
    	checkMysql();
     	try {
			event.toReplyDB(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
     	try {
			super.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}   	
    }    
    

	public static void main(String[] args) throws JSONException {
		Message msg= Message.getOneMsg();     	
    	SendCommandQueue.getInstance().put(msg);    	
    	SendCommandQueue qe=SendCommandQueue.getInstance();    	
    	System.out.println(qe.size());	

	}

}
