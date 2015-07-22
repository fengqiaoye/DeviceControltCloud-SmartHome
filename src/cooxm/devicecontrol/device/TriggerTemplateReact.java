package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.socket.Message;
import cooxm.devicecontrol.util.MySqlClass;
import redis.clients.jedis.Jedis;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：29 Jan 2015 16:58:14 
 */

public class TriggerTemplateReact {
	//ProfileMap profileMap;

	
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
		//this.cookieNo =((System.currentTimeMillis()/1000)%(24*3600))*10000;
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
		//this.cookieNo =((System.currentTimeMillis()/1000)%(24*3600))*10000;
	}
	public TriggerTemplateReact(TriggerTemplateReact react) {
		//this.cookieNo =((System.currentTimeMillis()/1000)%(24*3600))*10000;
		this.reactType = react.reactType;
		this.targetID = react.targetID;
		this.reactWay = react.reactWay;
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
	
	

	 public static void main(String [] args) {
		 //long a=System.currentTimeMillis()/1000;
		 //System.out.println(a);
		 
		 Jedis jedis =new Jedis("172.16.35.170", 6379);//("120.24.81.226", 6379);
		 jedis.select(9);
		 String b = jedis.hget("currentProfile:40008", 3000+"");
		 System.out.println(b);
		 
		 Long c = jedis.hset("roomBind:40006", 3000+"",b);
		 System.out.println(c);
		 
		 
		 
		
	}
}
