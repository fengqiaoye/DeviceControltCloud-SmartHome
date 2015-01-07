package control;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：18 Dec 2014 16:43:09 
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Event {
	int eventID;
	/**
	 * <pre>事件类型:
	 *  填写 commandID
	 * */
	int eventType;
	/***"control":0 ;"mobile":1; "cloud":2; web:3; */
	int senderRole;  
	int CtrolID;
	int roomID;
	/***<pre>处理结果： 填写errorCode*/
	int errorCode;
	
	//static Map<Integer,Event> eventMap= new HashMap<Integer,Event>();
	//static BlockingQueue<Event> evnetQueue= new ArrayBlockingQueue<Event>(1000) ;
	
	Event() {}	
	Event(
			int eventID,
			int eventType,
			int senderRole,
			int CtrolID,
			int roomID,
			int errorCode
			) {
		this.eventID=eventID;
		this.eventType=eventType;
		this.senderRole=senderRole;
		this.CtrolID=CtrolID;
		this.roomID=roomID;
		this.errorCode=errorCode;	
	}
	
//	public void regiter(Event event){
//		Event.evnetQueue.offer(event);		
//	}
//	
//	public void withDraw(Event event) throws InterruptedException{
//		Event.evnetQueue.poll(200, TimeUnit.MICROSECONDS);		
//	}

	public static void main(String[] args) {


	}

}
