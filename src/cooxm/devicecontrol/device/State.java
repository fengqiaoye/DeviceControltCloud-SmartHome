package cooxm.devicecontrol.device;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jul 2, 2015 5:40:06 PM 
 */

public  class State{
	/** 环境数值*/
	public double value;  
	/** 环境数值所处的等级*/
	public int level;

	
	public State(double value,int level){
		this.value=value;
		this.level=level;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public JSONObject toJson(){
		JSONObject json =new JSONObject();
		try {
			json.put("value", value);
			json.put("level", level);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;			
	}
	
	public State(JSONObject json) {

		try {
			this.value=json.getDouble("value");
			this.level=json.getInt("level");
		} catch (JSONException e) {
			e.printStackTrace();
		}			
	}
}