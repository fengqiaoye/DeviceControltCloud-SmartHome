package control;

import java.util.HashMap;
import java.util.Set;

import org.omg.CORBA.INTERNAL;

import redis.clients.jedis.Jedis;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created£º5 Jan 2015 14:32:59 
 */

public class EventMapJedis extends Jedis {
	

	private static final String eventMapName="eventMap";
	
	EventMapJedis(String redisIP,int redisPort){
		super(redisIP, redisPort);
	}
	

	
	
	

	public static void main(String[] args) {

	}

}
