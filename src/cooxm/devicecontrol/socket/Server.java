package cooxm.devicecontrol.socket;

import java.util.Date;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 10, 2015 10:59:08 AM 
 */

public class Server {
	
	private String serverIP;
	private int serverPort;
	private int serverType;
	private int clusterid;
	private Date lastHeartBeatTime;
	
	
	

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
			int clusterid) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.serverType = serverType;
		this.clusterid = clusterid;
	}
	
	public static void main(String[] args) {

	}

}
