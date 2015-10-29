package cooxm.devicecontrol.security;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.device.Warn;
import cooxm.devicecontrol.socket.CtrolSocketServer;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.constant.*;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 26, 2015 7:56:28 PM 
 */

public class MonitorStatus {
	static Logger log =Logger.getLogger(MonitorStatus.class);
	static Map<Integer, Timer> timerMap=new HashMap<Integer, Timer>();
	static long cookieNo = ((System.currentTimeMillis() / 1000) % (24 * 3600)) * 10000;
	
	int ctrolID;
	int monitorStatus;	
	int sender;
	Date time;
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public int getMonitorStatus() {
		return monitorStatus;
	}
	public void setMonitorStatus(int monitorStatus) {
		this.monitorStatus = monitorStatus;
	}
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public MonitorStatus(int ctrolID, int monitorStatus, int sender, Date time) {
		this.ctrolID = ctrolID;
		this.monitorStatus = monitorStatus;
		this.sender = sender;
		this.time = time;
	}
	
	public MonitorStatus(JSONObject json){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.ctrolID=json.getInt("ctrolID");
			this.monitorStatus=json.getInt("monitorStatus");
			this.sender=json.getInt("sender");
			this.time=sdf.parse(json.getString("time"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	
	public JSONObject toJson(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json =new JSONObject();
		try {
			json.put("ctrolID", ctrolID);
			json.put("monitorStatus", monitorStatus);
			json.put("sender", sender);
			json.put("time", sdf.format(time));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;		
	}
	/**正在输密码 */
	public void enterPassword(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				log.info("Break in warn: user didn't input correct password in 60 seconds");
				SendWarnMsg(WarnID.BREAK_IN_WARN);
			}
		}
		, 60 *1000);		
		timerMap.put(this.ctrolID, timer);		
	}
	
	/**停止布防 */
	public void stopMonitor(){
		Timer timer =timerMap.get(this.ctrolID);
		if (timer!=null) {
			timer.cancel();
			timerMap.remove(this.ctrolID);
		}		
	}
	
	/**入侵告警 */
	public void SendWarnMsg(int warnType){
		JSONObject json =new JSONObject();
		JSONObject msgContent=new JSONObject();
		try {
			msgContent.put("roomName", "全家");
			Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2,warnType, 0, sender,msgContent.toString());
			json.put("ctrolID", ctrolID);
			json.put("sender", 2);
			json.put("receiver", 0);
			json.put("warn", warn.toJsonObject());
			String cookie=cookieNo++  +"_2";
			Message msg = new Message((short) (LogicControl.WARNING_START + 3),	cookie, json);
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	
}
