package cooxm.devicecontrol.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 21, 2015 10:49:36 AM 
 * 空调状态 描述专用类
 */

public class DeviceState {
	/** 开关 0：打开状态；  1：关闭状态 ; -1:未知 */
	int onOff;
	/** 模式  0：自动； 1：制冷； 2=除湿， 3=送风， 4=制热; -1:未知 */
	int mode;
	/** 风速  0=自动，1=风速1，2=风速2，3=风速3*/
	int windSpeed;
	/** 0=自动，1=风向1，2=风向2，3=风向3，4=风向4*/
	int windDirection;
	/** 温度 16-30度分别是：  0=16 ,1=17。。。。 14=30*/
	int tempreature;
	
	// 2015-06-29更改 DeviceState 只保留空调的特征字段，电视、DVD等家电切换，采用keyType描述
	/**频道的值 */
	//int channel;
	/** 音量的值 */
	//int volumn;
	/** 屏幕亮度值 */
	//int brightness;	
	
	Date modifyTime;
	
	int keyType;
	
	//空调恒定的温度，若不是恒温，则stable=0
	int stable;
	
	
	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public int getOnOff() {
		return onOff;
	}

	public void setOnOff(int onOff) {
		this.onOff = onOff;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}

	public int getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

	public int getTempreature() {
		return tempreature;
	}

	public void setTempreature(int tempreature) {
		this.tempreature = tempreature;
	}
	

	/*public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}*/
	

	public int getStable() {
		return stable;
	}

	public void setStable(int stable) {
		this.stable = stable;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public DeviceState() {
		this.onOff = -1;
		this.mode = -1;
		this.windSpeed = -1;
		this.windDirection =-1;
		this.tempreature = -1;
		this.keyType=-1;
		/*this.channel = -1;
		this.volumn = -1;
		this.brightness = -1;*/
		this.modifyTime=new Date();
		this.stable=-1;
	}
	/** <pre> 开关   0：打开状态；  1：关闭状态 ; -1:未知 
	模式  0=自动； 1=制冷； 2=除湿， 3=送风， 4=制热; -1:未知 
	风速  0=自动，1=风速1，2=风速2，3=风速3
	风向  0=自动，1=风向1，2=风向2，3=风向3，4=风向4
	温度 16-30度分别是：  0=16 ,1=17。。。。 14=30
	
	频道的值 
	音量的值 
	 屏幕亮度值 */
	public DeviceState(int onOff, int mode, int windSpeed, int windDirection,
			int tempreature /*, int channel, int volumn, int brightness*/,int keyType,int stable) {
		this.onOff = onOff;
		this.mode = mode;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.tempreature = tempreature;
		/*this.channel = channel;
		this.volumn = volumn;
		this.brightness = brightness;*/
		this.modifyTime=new Date();
		this.keyType=keyType;
		this.stable=stable;
	}
	
	public JSONObject toJson(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json=new JSONObject();
		try {
			json.put("onOff", this.onOff);
			json.put("mode", this.mode);
			json.put("windSpeed", this.windSpeed);
			json.put("windDirection", this.windDirection);
			json.put("tempreature", this.tempreature);
			/*json.put("channel", this.channel);
			json.put("volumn", this.volumn);
			json.put("brightness", this.brightness);*/
			//json.put("modifyTime", sdf.format(new Date()));
			json.put("keyType", keyType);
			json.put("stable", stable);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
		
	}
	
	public DeviceState (JSONObject json) throws ParseException, JSONException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.onOff=json.getInt("onOff");
		this.mode=json.getInt("mode");
		this.windSpeed=json.getInt("windSpeed");
		this.windDirection=json.getInt("windDirection");
		this.tempreature=json.getInt("tempreature");
		/*this.channel=json.optInt("channel");
		this.volumn=json.optInt("volumn");
		this.brightness=json.optInt("brightness");*/
		/*try {
			this.modifyTime=sdf.parse(json.getString("modifyTime"));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		
		this.keyType=json.getInt("keyType");
		this.stable=json.optInt("stable",0);
	}
	
	/**<pre> 将一个旧的状态和一个新的状态叠加
	 * 如果新状态某个字段是-1，则保持和 旧的相同;
	 * 如果新状态某个字段不是-1，则将旧的状态这个字段用新的替换*/
	public DeviceState replaceAdd(DeviceState newState) {
		this.onOff=(newState.onOff==-1)         	? 	this.onOff		:newState.onOff;
		this.mode=(newState.mode==-1)           	?  	this.mode		:newState.mode;
		this.windSpeed=(newState.windSpeed==-1) 	?	this.windSpeed	:newState.windSpeed;
		this.windDirection=(newState.windDirection==-1)	?this.windDirection	:newState.windDirection;
		this.tempreature=(newState.tempreature==-1)	?	this.tempreature	:newState.tempreature;
		this.keyType=(newState.keyType==-1)				?	this.keyType		:newState.keyType;	
		this.stable=(newState.stable>=16)				?	newState.stable:this.stable		;	
		return this;		
	}

	public static void main(String[] args) throws ParseException, JSONException {
    DeviceState old=new DeviceState(-1, 0, -1, 5, -1, -1,0);
    DeviceState newd=new DeviceState(1, 0, -1, 6, 25, 4,0);
    old.replaceAdd(newd);
    
    DeviceState d=new DeviceState();
    String dd=d.toString();
    System.out.println(dd);

	}

}
