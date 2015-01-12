package socket;
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

import util.MySqlClass;
import control.MainEntry;
import device.Factor;
import device.Profile;

public class Event {
	static Logger log =Logger.getLogger(MainEntry.class);
	private static final String receiveTable ="info_devicecontrol_msg_receive";
	private static final String replyTable   ="info_devicecontrol_msg_reply";
	MySqlClass mysql;
	
	/*** 填写序列号*/
	int eventID;
	
	/**
	 * <pre>事件类型:
	 *  填写 commandID
	 * */
	int commandID;
	
	int CtrolID;
	int roomID;
	
	/** Json里的ctrolID*/
	//String sender;
	
	/***"Json里的sender ：control":0  ;"mobile":1;  "cloud":2;  web:3;   other：4 */
	int senderRole;  

	/***<pre>处理结果： 填写errorCode*/
	int errorCode;
	Date receiveTime;
	Date replyTime;	
	String json;
	
	Event() {}	
	Event(
			int eventID,
			int commandID,
			int CtrolID,
			int roomID,
			String sender,
			int senderRole,
			int errorCode,
			Date receiveTime,
			Date replyTime,
			String json
			) {		
		this.eventID=eventID;
		this.commandID=commandID;
		//this.sender=sender;
		this.senderRole=senderRole;
		this.CtrolID=CtrolID;
		this.roomID=roomID;
		this.errorCode=errorCode;
		this.receiveTime=receiveTime;
		this.replyTime=replyTime;
		this.json=json;
	}
	
	Event(Message msg)  {
        this.eventID=Integer.parseInt(msg.cookie);
		this.commandID=msg.header.commandID;
		if(msg.json.has("CtrolID")){
			try {
				this.CtrolID=msg.json.getInt("CtrolID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("In contact message received, cant parse CtrolID in json："+msg.msgToString());
			return;
		}
		if(msg.json.has("roomID")){
			try {
				this.CtrolID=msg.json.getInt("roomID");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			this.CtrolID=0;
			System.out.println("In contact message received, cant parse roomID in json:"+msg.msgToString());
		}
		//this.sender=msg.cookie;
		if(msg.json.has("sender")){
			try {
				this.senderRole=msg.json.getInt("sender");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else this.senderRole=0;

		if(msg.json.has("errorCode")){
			try {
				this.CtrolID=msg.json.getInt("errorCode");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("In contact message received, cant parse errorCode in json:"+msg.msgToString());
			return;
		}
		this.receiveTime=msg.receiveTime;
		this.replyTime=msg.replyTime;
		this.json=msg.json.toString();
	}
	
	public void toReceiveDB(MySqlClass mysql) throws SQLException {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);
			String sql="insert into "+receiveTable
					+" ( "
					+ "eventid  ,"     
					+"commandid ,"
					+"ctrolid ,"
					+"roomid ,"
					//+"sender ,"
					+"senderrole ,"
					+"errorcode ,"
					+"receivetime ,"
					+"replytime ,"
					+"json "
					+ ")"				
					+"values "
					+ "("
					+this.eventID+","
					+this.commandID+","
					+this.CtrolID+","
					+this.roomID+",'"
					//+this.sender+"','"
					+this.senderRole+"',"
					+this.errorCode+",'"
					+sdf.format(this.receiveTime)+"','"
					+sdf.format(this.replyTime)+"','"
					+this.json
					+"')";
			System.out.println(sql);
			mysql.query(sql);		
		mysql.conn.commit();		
	}

	public void toReplyDB(MySqlClass mysql) throws SQLException {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);

			String sql="insert into "+replyTable
					+" ("
					+ "eventid  ,"     
					+"commandid ,"
					+"ctrolid ,"
					+"roomid ,"
					+"sender ,"
					+"senderrole ,"
					+"errorcode ,"
					+"receivetime ,"
					+"replytime ,"
					+"json "
					+ ")"				
					+"values "
					+ "("
					+this.eventID+","
					+this.commandID+","
					+this.CtrolID+","
					+this.roomID+",'"
					//+this.sender+"','"
					+this.senderRole+"',"
					+this.errorCode+",'"
					+sdf.format(this.receiveTime)+"','"
					+sdf.format(this.replyTime)+"','"
					+this.json
					+"')";
			System.out.println(sql);
			mysql.query(sql);		
		mysql.conn.commit();		
	}

	public static void main(String[] args) {



	}

}
