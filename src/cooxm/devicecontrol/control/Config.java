package cooxm.devicecontrol.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：24 Dec 2014 14:22:48 
 */

public class Config extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private	static Properties pps=new Properties(); 
    private static final String configFile="./conf/control.conf";
	private static Logger log=Logger.getLogger(Config.class);
	
	public Config(){		
	    try {
    	    log.info("Starting reading system config file : "+configFile+"...");
	    	checkExist(configFile);
	        pps.load(new FileInputStream(configFile));
	        
            if(!pps.containsKey("server_port"))   	{pps.setProperty("server_port", "20190"); } 
            if(!pps.containsKey("mysql_ip"))   		{pps.setProperty("mysql_ip", "172.16.35.170");  }
            if(!pps.containsKey("mysql_port")) 		{pps.setProperty("mysql_port", "3306");  }
            if(!pps.containsKey("mysql_user")) 		{pps.setProperty("mysql_user", "root");  }
            if(!pps.containsKey("mysql_password"))	{pps.setProperty("mysql_password", "cooxm");  }
            if(!pps.containsKey("mysql_database"))	{pps.setProperty("mysql_database", "cooxm_device_control"); }
            if(!pps.containsKey("mysql_database_main"))	{pps.setProperty("mysql_database_main", "cooxm_main"); }
            if(!pps.containsKey("redis_ip"))	    {pps.setProperty("redis_ip", "172.16.35.170"); }
            if(!pps.containsKey("redis_port"))	    {pps.setProperty("redis_port", "6379"); }
            if(!pps.containsKey("max_send_msg_queue"))	    {pps.setProperty("max_send_msg_queue", "20000"); }
            if(!pps.containsKey("max_recv_msg_queue"))	    {pps.setProperty("max_recv_msg_queue", "20000"); }
            if(!pps.containsKey("request_timeout"))	    {pps.setProperty("request_timeout", "30"); }
            if(!pps.containsKey("msg_server_ip"))	    {pps.setProperty("msg_server_ip", "172.16.35.174"); }
            if(!pps.containsKey("msg_server_port"))	    {pps.setProperty("msg_server_port", "10790"); }
            pps.store(new FileOutputStream(configFile)  , "Copyright @cooxm corporation under one or more contributor license agreements. \nConfigurate for Device Control module default values.\n ");

	    } catch (Exception e) {
	        e.printStackTrace();
	        log.info("configure file ./conf/control.conf doesn't exist! A new configure file ./conf/control.conf have been generate by system.");
	    }	
	    log.info("System config finished. ");
	}
	
	public void checkExist(String fileName) {
		File file=new File(configFile);    
		if(!file.exists())    
		{    
		    try {    
		        file.createNewFile();  
		        log.info("config file:"+configFile+ "not existed,create a default one. ");
		    } catch (IOException e) {    
		        e.printStackTrace();    
		    }    
		}  	
	}
	
	public String getValue(String key) {
      return this.pps.getProperty(key) 	;	
	}

	public static void main(String[] args) {		
		Config cf= new Config();
		System.out.println(cf.getValue("server_port"));	

	}

}
