package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：29 Jan 2015 16:58:14 
 */

public class TriggerTemplateReact {
	//ProfileMap profileMap;	

	
	/**<pre>
	1:告警
	2:家电
	3:情景模式	 */
	private int reactType;
	
	/**<pre>
      告警类：
	1：有害气体过高；
	2：PM2.5指标超标；
	3：温度过高 ；
	4：火警；
	5：入侵告警；
	6：防盗大门未关；
	7：台风告警；
	8：暴雨告警
       家电类：
	(家电类型)
	10：灯
	20：电视
	40: 空调
	41: 空调开关
	42：空调温度
	43：空调风速
	60：窗户
	80：窗帘
	90：暖器
       情景模式：
	  （标情景模式类型）
	 301: 睡眠模式
	 302: 离家模式
	 303: 观影模式 */
	private int targetID;
	
	/**<pre>
	1：SMS
	2：消息推送
	3：打开
	4：关闭
	5：调大
	6：调小
	11：情景切换*/
	private int reactWay;
	
	public int getReactType() {
		return reactType;
	}
	public void setReactType(int reactType) {
		this.reactType = reactType;
	}
	public int getTargetID() {
		return targetID;
	}
	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}
	public int getReactWay() {
		return reactWay;
	}
	public void setReactWay(int reactWay) {
		this.reactWay = reactWay;
	}
	
	public TriggerTemplateReact(int reactType, int targetID, int reactWay) {
		this.reactType = reactType;
		this.targetID = targetID;
		this.reactWay = reactWay;
		/*Configure cf=MainEntry.getConfig();
		String mysql_ip			=cf.getValue("mysql_ip");
		String mysql_port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String mysql_database	=cf.getValue("mysql_database");	
		MySqlClass mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		profileMap = new ProfileMap(mysql);*/
	}
	public TriggerTemplateReact() {
	}
	
	public  JSONObject toJson(){
		//DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject factorJson=new JSONObject();
    	try {
        	factorJson.put("reactType", getReactType());
        	factorJson.put("targetID", getTargetID());
        	factorJson.put("reactWay", getReactWay());
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return factorJson;		
	}
	
	
	public static TriggerTemplateReact fromJson(JSONObject reactJson) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TriggerTemplateReact factor= new TriggerTemplateReact();
		try {
			factor.setReactType(reactJson.getInt("reactType"));
			factor.setTargetID(reactJson.getInt("targetID"));
			factor.setReactWay(reactJson.getInt("reactWay"));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return factor;		
	}
	
	
	public Message react(MySqlClass mysql,Jedis jedis,int ctrolID,int roomID) {
		int onOFF=-1;
		int mode=-1;
		int tempreture=-1;
		int speed=-1;
		int channel=-1;
		int volumn=-1;

		String cookie=System.currentTimeMillis()/1000+"_5";
		Message msg=null;
		JSONObject json;
		switch (reactType) {
		case 1:  //通知或告警
			Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2, this.getTargetID(), 2);
			json=new JSONObject();
			try {
				json.put("ctrolID", ctrolID);
				json.put("sender",6);
				json.put("receiver",0); 
				json.put("warn", warn.toJsonObject());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}

			msg=new Message((short) (LogicControl.WARNING_START+3), cookie,json );
			break;
		case 2:  //家电			
			switch (reactWay) {
			case 11:    //		11：打开
				 onOFF=0;
			case 12:   	//		12：关闭
				 onOFF=1;
			case 1300:  // 打开且 自动模式
				onOFF=0; 
				mode=0;
			case 1301:  // 打开且自动模式
				onOFF=0;
				mode=1;	
			case 1302:  // 打开且除湿模式
				onOFF=0;
				mode=2;	
			case 1303:  // 打开且智能模式
				onOFF=0;
				mode=3;	
			case 1304:  // 打开且制热模式
				onOFF=0;
				mode=4;	
			case 1400:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=16;
			case 1401:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=17;
			case 1402:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=18;
			case 1403:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=19;
			case 1404:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=20;
			case 1405:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=21;
			case 1406:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=22;
			case 1407:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=23;
			case 1408:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=24;
			case 1409:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=25;
			case 1410:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=26;
			case 1411:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=27;
			case 1412:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=28;
			case 1413:  // 打开且温度设置为16度
				onOFF=0;
				tempreture=29;
			case 1414:  // 打开且温度设置为30度
				onOFF=0;
				tempreture=30;
			case 1500:  // 打开且风速设置为自动
				onOFF=0;
				tempreture=30;
			case 1501:  // 打开且风速设置为1
				onOFF=0;
				tempreture=30;
			case 1502:  // 打开且风速设置为2
				onOFF=0;
				tempreture=30;
			case 1503:  // 打开且风速设置为3
				onOFF=0;
				tempreture=30;
			default:
				break;
			}
			DeviceState state= new DeviceState(onOFF, mode, speed, -1, tempreture, channel, volumn, -1);
			Set<String> deviceIDSet = jedis.hkeys(ctrolID+"_roomBind");
			for (String deiceID:deviceIDSet) {
				Device device=new Device();
				try {
					device = new Device(new JSONObject(jedis.hget(ctrolID+"_roomBind", deiceID+"")));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				if (device.getDeviceType()==this.targetID) {
					json=new JSONObject();
					try {
						json.put("ctrolID", ctrolID);
						json.put("roomID", roomID);
						json.put("deviceID", device.getDeviceID());
						json.put("deviceType", device.getDeviceType());
						json.put("sender",6);
						json.put("receiver",0); 
						json.put("state", state.toJson());
					} catch (JSONException e) {
						e.printStackTrace();
					}					
					msg=new Message((short) (LogicControl.SWITCH_DEVICE_STATE), cookie,json );	
				}else{
					continue;
				}	
			}			
			
			break;
		case 3:  //profile
			Profile p;
			try {
				p = Profile.getFromDBByTemplateID(mysql, ctrolID, roomID,targetID); // targertID就是情景模板ID
				json=new JSONObject();
				json.put("ctrolID", ctrolID);
				json.put("sender",6);
				json.put("receiver",0); 
				json.put("profileID",p.getProfileID()); 
				msg=new Message((short) (LogicControl.SWITCH_RROFILE_SET), cookie,json);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (JSONException e) {   
				e.printStackTrace();
			}	
			break;
		case 4:  //profileSet
			ProfileSet ps;
			try {
				ps = ProfileSet.getProfileSetByTemplateID(mysql, ctrolID, targetID); // targertID就是情景模板ID
				json=new JSONObject();
				json.put("ctrolID", ctrolID);
				json.put("sender",6);
				json.put("receiver",0); 
				json.put("profileSetID",ps.getProfileSetID()); 
				msg=new Message((short) (LogicControl.SWITCH_RROFILE_SET), cookie,json);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (JSONException e) {   
				e.printStackTrace();
			}	
			break;
		default:
			break;
		}	
			return msg;			
		}
}
