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
	long cookieNo;
	
	/**<pre>
	1:告警
	2:家电
	3:情景模式
	4:情景集	 */
	private int reactType;
	
	/**<pre>
	1	有害气体过高；
	2	PM2.5指标超标；
	3	温度过高 ；
	4	火警；
	5	入侵告警；
	6	防盗大门未关；
	7	台风告警；
	8	暴雨告警
	9	漏水告警
	101	图灵猫中控
	111	图灵猫旋钮
	121	图灵猫CO探测
	131	图灵猫插座
	141	图灵猫插座棒
	201	烟雾探测器
	211	漏水探测器
	221	门磁
	231	其他探测器
	241	中继器
	251	门铃
	301	主机遥控器
	311	智能门锁
	321	报警器
	331	漏水机械手
	341	天然气机械手
	401	无线遥控灯
	411	遥控窗
	421	遥控窗帘
	501	电视
	511	机顶盒
	521	视频盒子
	531	音响
	541	空调
	551	冰箱
	561	热水器
	571	灯
	581	取暖器
	591	空气净化器
	601	电风扇
	611	饮水机
	801	电饭煲
	811	豆浆机
	821	电烤箱
	831	电水壶
	841	微波炉
	1001	扫地机器人
	1011	擦窗机
	1021	拖地机
	1031	镜面加热
	1041	智能马桶
	1051	加香机
	1061	投影仪
	1071	投影幕
	1081	自动演奏钢琴
	1091	除湿机
	1101	加湿器
	1111	洗衣机
	1121	美发器
	1131	遥控车门
	1141	其他家电
	2201	五合一传感器
	2211	红外发射器
	2221	射频发射器
	10001	睡眠模式
	10002	离家模式
	10003	观影模式
	10004	居家模式*/
	private int targetID;
	
	/**<pre>
	11 	SMS
	12 	消息推送
	13 	SMS、APP都推送
	1 	打开
	0 	关闭
	1300 	打开且自动模式
	1301 	打开且制冷模式；
	1302 	打卡器且除湿模式；
	1303 	打开且送风模式，
	1304 	打开且制热模式；
	21 	情景切换*/
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
		this.cookieNo =((System.currentTimeMillis()/1000)%(24*3600))*10000;
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
		this.cookieNo =((System.currentTimeMillis()/1000)%(24*3600))*10000;
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

		String cookie=this.cookieNo+"_5";
		this.cookieNo++;
		Message msg=null;
		JSONObject json;
		switch (reactType) {
		case 1:  //通知或告警
			Warn warn=new Warn(ctrolID, 3, 3, 0, new Date(), 2, this.getTargetID(), 2);
			json=new JSONObject();
			try {
				json.put("ctrolID", ctrolID);
				json.put("sender",5);
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
						json.put("sender",5);
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
				json.put("sender",5);
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
				json.put("sender",5);
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
	
	 public static void main(String [] args) {
		 long a=System.currentTimeMillis()/1000;
		 System.out.println(a);
		 
		 Jedis jedis =new Jedis("172.16.35.170", 6379);
		 String b = jedis.hget("1256791_currentProfile", 203+"");
		
	}
}
