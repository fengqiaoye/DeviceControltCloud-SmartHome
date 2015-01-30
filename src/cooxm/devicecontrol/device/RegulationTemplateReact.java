package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：29 Jan 2015 16:58:14 
 */

public class RegulationTemplateReact {
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
	
	public RegulationTemplateReact(int reactType, int targetID, int reactWay) {
		this.reactType = reactType;
		this.targetID = targetID;
		this.reactWay = reactWay;
	}
	public RegulationTemplateReact() {
	}
}
