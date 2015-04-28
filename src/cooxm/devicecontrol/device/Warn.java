/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：17 Dec 2014 19:38:07 
 */
package cooxm.devicecontrol.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Warn {	
	/**中控ID*/
	int    ctrolID;
	/** 1:网络推送；2:SMS 3:Both*/
	int    channel;
	/**推送目标: 1:移动终端；2：中控器终端；3：两者都推送*/
	int    target;
	/**消息超时时间，0表示永远不超时*/
	int    timeOut;
	/**产生时间*/
	Date   createTime; 
	
	/**告警产生系统:1：中控系统；2：安防系统;3：云端*/
	int    madeFrom;
	/**告警类型:1：有害气体过高；2：PM2.5指标严重；3：温度过高 ；4：火警；5：入侵告警；6：防盗大门未关；7：台风告警；8：暴雨告警*/	
	int  warnType;
	int  severity;
	/**告警内容 中文UTF-8*/
	String msgContent; 

	
	public int getctrolID() {
		return ctrolID;
	}

	public void setctrolID(int ctrolID) {
		this.ctrolID=ctrolID;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int gettimeOut() {
		return timeOut;
	}

	public void settimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getMadeFrom() {
		return madeFrom;
	}

	public void setMadeFrom(int madeFrom) {
		this.madeFrom = madeFrom;
	}

	public int getWarnType() {
		return warnType;
	}

	public void setWarnType(int warnType) {
		this.warnType = warnType;
	}

	public String getmsgContent() {
		return msgContent;
	}

	public void setmsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	
	public int getCtrolID() {
		return ctrolID;
	}

	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Warn(){	}
	
	public Warn(int ctrolID, int channel, int target, int timeOut,
			Date createTime, int madeFrom, int warnType, int severity) {
		this.ctrolID = ctrolID;
		this.channel = channel;
		this.target = target;
		this.timeOut = timeOut;
		this.createTime = createTime;
		this.madeFrom = madeFrom;
		this.warnType = warnType;
		this.severity=severity;
	}
	
	public Warn(JSONObject warnJson) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.ctrolID = warnJson.getInt("ctrolID");
			this.channel=warnJson.getInt("channel");
			this.target=warnJson.getInt("target");
			this.timeOut=warnJson.getInt("timeOut");
			this.createTime=sdf.parse(warnJson.getString("createTime")); 
			this.madeFrom=warnJson.getInt("madeFrom");
			this.warnType=warnJson.getInt("warnType");
			this.severity=warnJson.optInt("severity");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public JSONObject toJsonObject(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject warnJson = new JSONObject();  
	    try {
			warnJson.put("ctrolID",        this.ctrolID      );
		    warnJson.put("channel",        this.channel      );
		    warnJson.put("target",         this.target    );
		    warnJson.put("timeOut",        this.timeOut    );	
		    warnJson.put("createTime",     sdf.format(this.createTime  )  );
		    warnJson.put("madeFrom",       this.madeFrom    );		    
		    warnJson.put("warnType",       this.warnType        );
		    warnJson.put("severity",    this.severity       );
		} catch (JSONException e) {
			e.printStackTrace();
		}	    
	    return warnJson;
	}

	public static void main(String[] args) {	

	}

}
