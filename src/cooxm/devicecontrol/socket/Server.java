package cooxm.devicecontrol.socket;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 10, 2015 10:59:08 AM 
 */

public class Server {
	static Logger log =Logger.getLogger(Server.class);
	private String serverIP;
	private int serverPort;
	private int serverType;
	private int clusterid;
	private Date lastHeartBeatTime;
	private int serverID;
	private MySqlClass mysql;
	
	
	

	public int getServerID() {
		return serverID;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public int getServerType() {
		return serverType;
	}
	public void setServerType(int serverType) {
		this.serverType = serverType;
	}
	public int getClusterid() {
		return clusterid;
	}
	public void setClusterid(int clusterid) {
		this.clusterid = clusterid;
	}
	
	public Date getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}
	public void setLastHeartBeatTime(Date lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
	}
	public Server(String serverIP, int serverPort, int serverType,
			int clusterid,int serverID) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.serverType = serverType;
		this.clusterid = clusterid;
		this.serverID=serverID;
	}
	
	public static Server getMsgServer(MySqlClass mysql,int clusterID){
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}		
		String sql2="select  "
		+" serverid       ,"
		+" serverip,"
		+" serverport ,"
		+ "  from "				
		+" info_server "
		+" where servertype="+107
		+" and clusterid="+clusterID
		+ ";";
		System.out.println("query:"+sql2);
		String res=mysql.select(sql2);
		String [] array=res.split(",");
		
		String serverIP = array[1];
		int serverPort = Integer.parseInt(array[2]);
		int serverID= Integer.parseInt(array[0]);
		Server server=new Server(serverIP, serverPort, 107, clusterID, serverID);
		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return server;		
	}
	
	/*public static Server getMainServer(MySqlClass mysql,int clusterID){
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}		
		String sql2="select  "
		+" serverid       ,"
		+" serverip,"
		+" serverport ,"
		+ "  from "				
		+" info_server "
		+" where servertype="+102
		+" and clusterid="+clusterID
		+ ";";
		System.out.println("query:"+sql2);
		String res=mysql.select(sql2);
		String [] array=res.split(",");
		
		String serverIP = array[1];
		int serverPort = Integer.parseInt(array[2]);
		int serverID= Integer.parseInt(array[0]);
		Server server=new Server(serverIP, serverPort, 107, clusterID, serverID);
		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return server;		
	}*/
	
	public static Server getDataServer(MySqlClass mysql,int clusterID){
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}		
		String sql2="select  "
		+" serverid       ,"
		+" serverip,"
		+" serverport ,"
		+ "  from "				
		+" info_server "
		+" where servertype="+107
		+" and clusterid="+clusterID
		+ ";";
		System.out.println("query:"+sql2);
		String res=mysql.select(sql2);
		String [] array=res.split(",");
		
		String serverIP = array[1];
		int serverPort = Integer.parseInt(array[2]);
		int serverID= Integer.parseInt(array[0]);
		Server server=new Server(serverIP, serverPort, 107, clusterID, serverID);
		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return server;
		
	}
	
	public   Server getServerByID(MySqlClass mysql,int serverID){		
		String sql="select  "
				+" serverid  ,"				
				+"serverip ,"
				+"serverport ,"
				+"servertype ,"
				+"clusterid "
				+ "  from "				
				+"info_server"
				+" where serverid= "+serverID
				+ ";";

		String res=mysql.select(sql);
		if(res==null ) {
			log.error("ERROR:exception happened: "+sql);
			return null;
		}else if(res=="") {
			log.error("ERROR:query result is empty: "+sql);
			return null;
		}
		//System.err.println(res);System.err.println(res);
		String[]  cells=res.split(",");
		Server server=new Server(cells[1], Integer.parseInt(cells[2]), Integer.parseInt(cells[3]), Integer.parseInt(cells[4]),serverID);
		mysql.close();	
		return server;
	}
	
	public static void main(String[] args) {

	}

}
