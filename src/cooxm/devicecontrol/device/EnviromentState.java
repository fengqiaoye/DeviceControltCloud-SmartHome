package cooxm.devicecontrol.device;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jun 1, 2015 3:57:36 PM 
 */

public class EnviromentState {
	
	public class State{
		/** 环境数值*/
		public double value;  
		/** 环境数值所处的等级*/
		public int level;	
		State(double value,int level){
			this.value=value;
			this.level=level;
		}
		public JSONObject toJson() {
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
	
	State lux;
	State pm25;
	/** 人体探测器*/
	//State infrared;
	State temprature;
	State moisture;
	State noise;
	State harmfulGas;
	//State waterLeak;
	public State getLux() {
		return lux;
	}
	public void setLux(State lux) {
		this.lux = lux;
	}
	public State getPm25() {
		return pm25;
	}
	public void setPm25(State pm25) {
		this.pm25 = pm25;
	}

	public State getTemprature() {
		return temprature;
	}
	public void setTemprature(State temprature) {
		this.temprature = temprature;
	}
	public State getMoisture() {
		return moisture;
	}
	public void setMoisture(State moisture) {
		this.moisture = moisture;
	}
	public State getNoise() {
		return noise;
	}
	public void setNoise(State noise) {
		this.noise = noise;
	}
	public State getHarmfulGas() {
		return harmfulGas;
	}
	public void setHarmfulGas(State harmfulGas) {
		this.harmfulGas = harmfulGas;
	}
	/*public State getInfrared() {
		return infrared;
	}
	public void setInfrared(State infrared) {
		this.infrared = infrared;
	}
	public State getWaterLeak() {
		return waterLeak;
	}
	public void setWaterLeak(State waterLeak) {
		this.waterLeak = waterLeak;
	}*/
	public EnviromentState(State lux, State pm25, 
			State temprature, State moisture, State noise, State harmfulGas //,
			/*State waterLeak,State infrared*/) {
		this.lux = lux;
		this.pm25 = pm25;
		//this.infrared = infrared;
		this.temprature = temprature;
		this.moisture = moisture;
		this.noise = noise;
		this.harmfulGas = harmfulGas;
		//this.waterLeak = waterLeak;
	}
	
	public JSONObject toJson(){
		JSONObject json=new JSONObject();
		try {
			json.put("lux",lux.toJson());
			json.put("pm25",pm25.toJson());
			//json.put("infrared",infrared.toJson());
			json.put("temprature",temprature.toJson());
			json.put("moisture",moisture.toJson());
			json.put("noise",noise.toJson());
			json.put("harmfulGas",harmfulGas.toJson());
			//json.put("waterLeak",waterLeak.toJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;		
	}
	
	public EnviromentState(JSONObject json){
		try {
			this.lux=new State(json.getJSONObject("lux"));
			this.pm25=new State(json.getJSONObject("pm25"));
			//this.infrared=new State(json.getJSONObject("infrared"));
			this.temprature=new State(json.getJSONObject("temprature"));
			this.moisture=new State(json.getJSONObject("moisture"));
			this.noise=new State(json.getJSONObject("noise"));
			this.harmfulGas=new State(json.getJSONObject("harmfulGas"));
			//this.waterLeak=new State(json.getJSONObject("waterLeak"));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
}
