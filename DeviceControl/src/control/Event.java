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
	int CtrolID;
	/*** 0: 等待处理； 1 ：已处理*/
	int state;
	
	//static Map<Integer,Event> eventMap= new HashMap<Integer,Event>();
	static BlockingQueue<Event> evnetQueue= new ArrayBlockingQueue<Event>(1000) ;
	
	Event() {}
	
	Event(
			int eventID,
			int CtrolID,
			int state
			) {
		this.eventID=eventID;
		this.CtrolID=CtrolID;
		this.state=state;	
	}
	
	public void regiter(Event event){
		Event.evnetQueue.offer(event);		
	}
	
	public void withDraw(Event event) throws InterruptedException{
		Event.evnetQueue.poll(200, TimeUnit.MICROSECONDS);		
	}

	public static void main(String[] args) {


	}

}
