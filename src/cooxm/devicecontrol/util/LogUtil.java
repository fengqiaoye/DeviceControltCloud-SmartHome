package cooxm.devicecontrol.util;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：18 Dec 2014 17:42:35 
 */

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil  {
	
    public Logger log;//= Logger.getLogger("global"); 	
	
	public  void config() throws IOException {
        PropertyConfigurator.configure( "./log4j.properties" );
	}
	
	public LogUtil(String logClass)  {

		try {
			this.config();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log= Logger.getLogger(logClass);        
	}



	public static void main(String[] args) throws IOException {
        //PropertyConfigurator.configure( "./log4j.properties" );
        Logger logger = Logger.getLogger(LogUtil.class);

        logger.debug("debug 1");
        logger.info("info 2");
        logger.warn("warn 3");
        logger.error("error 4");
        logger.fatal("fatal 2");
	}

}
