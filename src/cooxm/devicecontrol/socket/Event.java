package cooxm.devicecontrol.socket;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：18 Dec 2014 16:43:09 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.control.MainEntry;
import cooxm.devicecontrol.device.Factor;
import cooxm.devicecontrol.device.Profile;
import cooxm.devicecontrol.util.MySqlClass;

public class Event {
	static Logger log =Logger.getLogger(MainEntry.class);
	private static final String receiveTable ="info_devicecontrol_msg_receive";
	private static final String replyTable   ="info_devicecontrol_msg_send";
	MySqlClass mysql;
	
	private int month;
	private int date;
	
	/*** 填写序列号*/
	String eventID;
	
	/**
	 * <pre>事件类型:
	 *  填写 commandID
	 * */
	int commandID;
	
	int ctrolID;
	int roomID;
	
	/** Json里的ctrolID*/
	//String sender;
	
	/***当前这条消息的发送角色 ：control":0  ;"mobile":1;  "cloud":2;  web:3;   other：4 */
	int senderRole;  
	
	/***当前这条消息的接收角色 ：control":0  ;"mobile":1;  "cloud":2;  web:3;   other：4 */
	int receiveRole; 
	
	/***收到的这一条消息的原始发送角色 ：control":0  ;"mobile":1;  "cloud":2;  web:3;   other：4 */
	int originalSenderRole	;				

	/***<pre>处理结果： 填写errorCode*/
	int errorCode;
	Date receiveTime=null;
	Date replyTime=null;	
	String json;
	
	Event() {}	
	Event(
			int month,
			int date,
			String eventID,
			int commandID,
			int ctrolID,
			int roomID,
			//String sender,
			int senderRole,
			int originalSenderRole,
			int errorCode,
			Date receiveTime,
			Date replyTime,
			String json
			) {	
		this.month=month;
		this.date=date;
		this.eventID=eventID;
		this.commandID=commandID;
		//this.sender=sender;
		this.senderRole=senderRole;
		this.originalSenderRole=originalSenderRole;
		this.ctrolID=ctrolID;
		this.roomID=roomID;
		this.errorCode=errorCode;
		this.receiveTime=receiveTime;
		this.replyTime=replyTime;
		this.json=json;
	}
	
	Event(Message msg)  {
		DateFormat monthSDF=new SimpleDateFormat("yyyyMM");
		DateFormat dateSDF=new SimpleDateFormat("yyyyMMdd");
		this.receiveTime=new Date();
		this.replyTime=this.receiveTime;
		this.month=Integer.parseInt(monthSDF.format(this.receiveTime));
		this.date= Integer.parseInt(dateSDF.format(this.receiveTime));
        this.eventID=msg.getCookie();
        this.commandID=msg.commandID;
		if(msg.getJson().has("ctrolID")){
			try {
				this.ctrolID=msg.getJson().getInt("ctrolID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			this.ctrolID=123456;
		}
		if(msg.getJson().has("roomID")){
			try {
				this.roomID=msg.getJson().getInt("roomID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			this.roomID=10;
		}
		if(msg.getJson().has("sender")){
			try {
				this.senderRole=msg.getJson().getInt("sender");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else this.senderRole=0;
		
		if(msg.getJson().has("originalSenderRole")){
			try {
				this.originalSenderRole=msg.getJson().getInt("originalSenderRole");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else this.originalSenderRole=0;

		if(msg.getJson().has("errorCode")){
			try {
				this.errorCode=msg.getJson().getInt("errorCode");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			this.errorCode=0;
		}
		this.json=msg.getJson().toString();
		//System.out.println(this.json);
	}
	
	public void toReceiveDB(MySqlClass mysql)  {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
			String sql="insert into "+receiveTable
					+" ( "
				    + "month  ,"  
				    + "date  ,"  
					+ "cookie  ,"     
					+"commandid ,"
					+"ctrolid ,"
					+"roomid ,"
					//+"sender ,"
					+"senderrole ,"
					+"origin_senderrole,"
					+"errorcode ,"
					+"receivetime ,"
					+"replytime ,"
					+"json "
					+ ")"				
					+"values "
					+ "("
					+this.month+","
					+this.date +",'"
					+this.eventID+"',"
					+this.commandID+","
					+this.ctrolID+","
					+this.roomID+","
					+this.senderRole+","
					+this.originalSenderRole+","
					+this.errorCode+",'"
					+sdf.format(this.receiveTime)+"','"
					+sdf.format(this.replyTime)+"','"
					+this.json
					+"')";
			System.out.println(sql);
			mysql.query(sql);		
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public void toReplyDB(MySqlClass mysql) throws SQLException {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);

			String sql="insert into "+replyTable
					+" ("
				    + "month  ,"  
					+ "date  ," 
					+ "cookie  ,"     
					+"commandid ,"
					+"ctrolid ,"
					+"roomid ,"
					//+"sender ,"
					+"receiverrole ,"
					+"origin_senderrole,"
					+"errorcode ,"
					+"receivetime ,"
					+"replytime ,"
					+"json "
					+ ")"				
					+"values "
					+ "("
					+this.month+","
					+this.date+",'"
					+this.eventID+"',"
					+this.commandID+","
					+this.ctrolID+","
					+this.roomID+","
					+this.receiveRole+","
					+this.originalSenderRole+","
					+this.errorCode+",'"
					+sdf.format(this.receiveTime)+"','"
					+sdf.format(this.replyTime)+"','"
					+this.json
					+"')";
			System.out.println(sql);
			mysql.query(sql);		
		mysql.conn.commit();		
	}

	public static void main(String[] args) throws SQLException {
     MySqlClass msyql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
     new Event(Message.getOneMsg()).toReceiveDB(msyql);


	}

}
