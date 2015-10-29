/*
 * Copyright 2015 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@turingcat.com
 * Created：Oct 28, 2015 3:22:11 PM 
 */
package test;

import java.text.ParseException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import cooxm.devicecontrol.device.Device;
import cooxm.devicecontrol.util.JedisUtil;
import junit.framework.TestCase;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 28, 2015 3:22:11 PM 
 */

/**
 * @author Chen Guanghua (richard@turingcat.com)
 *
 */
public class TestDevice  {
	
	JedisUtil jedis;
	
	@Before
	public void init(){
		 jedis= new JedisUtil("172.16.35.170", 6379,9);
	}
	
	@Test
	public void batchSwitch() throws JSONException{
		Device.batchSwitchRole( this.jedis, 10005, 0) ;
		System.out.println("batchSwitch finish");
	}
	
	
	@Test
	public void batchSwitchByRoomID() throws JSONException, ParseException{
		Device.batchSwitchRoleByRoomID(jedis, 10005, 5, 1000);
		System.out.println("batchSwitchByRoomID finish");
	}

}
