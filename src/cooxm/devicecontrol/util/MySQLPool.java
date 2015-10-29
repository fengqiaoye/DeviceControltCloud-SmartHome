package cooxm.devicecontrol.util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 15, 2015 6:52:57 PM 
 */

 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import cooxm.devicecontrol.control.Configure;
 
public class MySQLPool {
	
    private static volatile MySQLPool pool;
    private MysqlDataSource ds;
    private Map<Connection, Boolean> map;
  
    private  String url = null;
    private  String username = null;
    private  String password = null;
//    private static String host = null;
//    private static String PORT = null;
//    private static String databasename = null;
    
    private int initPoolSize = 10;
    private int maxPoolSize = 70;
    private int waitTime = 100;
    
	private static volatile int a;
     

    private MySQLPool() {
    	Configure cf=new Configure();
		String host			=cf.getValue("mysql_ip");
		String port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String databaseName	=cf.getValue("mysql_database");	
		
    	url="jdbc:mysql://"+host+":"+port+"/"+databaseName+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true";    	
    	username=mysql_user;
    	password=mysql_password; 

        init();
    }
     
    public static MySQLPool getInstance() {
        if (pool == null) {
            synchronized (MySQLPool.class) {
                if(pool == null) {
                    pool = new MySQLPool( );
                }
            }
        }
        return pool;
    }
     
    private void init() {
        try {
            ds = new MysqlDataSource();
            
            ds.setUrl(url);
            ds.setUser(username);
            ds.setPassword(password);
            ds.setCacheCallableStmts(true);
            ds.setConnectTimeout(1000);
            ds.setLoginTimeout(2000);
            ds.setUseUnicode(true);
            ds.setEncoding("UTF-8");
            ds.setZeroDateTimeBehavior("convertToNull");
            ds.setMaxReconnects(5);
            ds.setAutoReconnect(true);
            map = new HashMap<Connection, Boolean>();
            for (int i = 0; i < initPoolSize; i++) {
                map.put(getNewConnection(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public Connection getNewConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
     
    public synchronized Connection getConnection() {
        Connection conn = null;
        try {
            for (Entry<Connection, Boolean> entry : map.entrySet()) {
                if (entry.getValue()) {
                    conn = entry.getKey();
                    map.put(conn, false);
                    break;
                }
            }
            if (conn == null) {
                if (map.size() < maxPoolSize) {
                    conn = getNewConnection();
                    map.put(conn, false);
                } else {
                    wait(waitTime);
                    conn = getConnection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
     
    public void releaseConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            if(map.containsKey(conn)) {
                if (conn.isClosed()) {
                    map.remove(conn);
                } else {
                    if(!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    map.put(conn, true);
                }
            } else {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private synchronized static void incr() {
        a++;
    }
    

	
    public static void main(String[] args) throws InterruptedException {
    	
    	MySqlClass mysql =new MySqlClass("172.16.35.170", "6379", "cooxm_device_control", "cooxm", "cooxm");
        int times = 100;        
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
        	mysql.select("select * from info_trigger");
        }
/*        for (int i = 0; i < times; i++) {
            new Thread(new Runnable() {
 
                @Override
                public void run() {
 
                    MySQLPool pool = MySQLPool.getInstance();
                    Connection conn = pool.getConnection();
                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery("select * from info_trigger");
                        while (rs.next()) {
                            //System.out.println(rs.getInt(1) + ", " + rs.getString(2));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        incr();
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException e) {
                            }
                        }
                        pool.releaseConnection(conn);
                    }
                    System.out.println("finished, time:" + (System.currentTimeMillis() - start));
                }
            }).start();
        }*/
        System.out.println("finished, time:" + (System.currentTimeMillis() - start));
    }
}
 