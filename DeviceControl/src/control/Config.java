package control;

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
 * @version Created£º24 Dec 2014 14:22:48 
 */

public class Config extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Properties pps=new Properties(); 
	//Enumeration enum1 ;
    static final String configFile="./conf/control.conf";
    static Logger log=Logger.getLogger(Config.class);
	
	Config(){		
	    try {
	    	checkExist(configFile);
	        pps.load(new FileInputStream(configFile));	        	
	        Enumeration enum1 = pps.propertyNames();
            if(!pps.containsKey("server_port"))   	{pps.setProperty("server_port", "64415");  }
            if(!pps.containsKey("mysql_ip"))   		{pps.setProperty("mysql_ip", "172.16.35.170");  }
            if(!pps.containsKey("mysql_port")) 		{pps.setProperty("mysql_port", "3306");  }
            if(!pps.containsKey("mysql_user")) 		{pps.setProperty("mysql_user", "root");  }
            if(!pps.containsKey("mysql_password"))	{pps.setProperty("mysql_password", "cooxm");  }
            if(!pps.containsKey("mysql_database"))	{pps.setProperty("mysql_database", "cooxm_device_control");  }
            
            pps.store(new FileOutputStream(configFile)  , "Copyright @cooxm corporation under one or more contributor license agreements. \nConfigurate for Device Control module default values.\n ");

	    } catch (Exception e) {
	        e.printStackTrace();
	        log.info("configure file ./conf/control.conf doesn't exist! A new configure file ./conf/control.conf have been generate by system.");
	    }		
	}
	
	public void checkExist(String fileName) {
		File file=new File(configFile);    
		if(!file.exists())    
		{    
		    try {    
		        file.createNewFile();    
		    } catch (IOException e) {    
		        // TODO Auto-generated catch block    
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
