package cooxm.devicecontrol.device;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 21, 2015 10:49:36 AM 
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
	
	/**频道的值 */
	int channel;
	/** 音量的值 */
	int volumn;
	/** 屏幕亮度值 */
	int brightness;	
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

	public int getChannel() {
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
	}

	public DeviceState() {
		this.onOff = -1;
		this.mode = -1;
		this.windSpeed = -1;
		this.windDirection =-1;
		this.tempreature = -1;
		this.channel = -1;
		this.volumn = -1;
		this.brightness = -1;
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
			int tempreature, int channel, int volumn, int brightness) {
		this.onOff = onOff;
		this.mode = mode;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.tempreature = tempreature;
		this.channel = channel;
		this.volumn = volumn;
		this.brightness = brightness;
	}
	
	public JSONObject toJson(){
		JSONObject json=new JSONObject();
		try {
			json.put("onOff", this.onOff);
			json.put("mode", this.mode);
			json.put("windSpeed", this.windSpeed);
			json.put("windDirection", this.windDirection);
			json.put("tempreature", this.tempreature);
			json.put("channel", this.channel);
			json.put("volumn", this.volumn);
			json.put("brightness", this.brightness);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
		
	}
	
	public DeviceState (JSONObject json){
		this.onOff=json.optInt("onOff");
		this.mode=json.optInt("mode");
		this.windSpeed=json.optInt("windSpeed");
		this.windDirection=json.optInt("windDirection");
		this.tempreature=json.optInt("tempreature");
		this.channel=json.optInt("channel");
		this.volumn=json.optInt("volumn");
		this.brightness=json.optInt("brightness");
	}

	public static void main(String[] args) {

	}

}
