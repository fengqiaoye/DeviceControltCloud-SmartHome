package cooxm.devicecontrol.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：29 Jan 2015 14:23:50 
 */

public class TriggerTemplateFactor extends FactorTemplate{
	/** 是否触发，触发则为true*/
	Boolean state;

	/**<pre> 逻辑关系：
	 * and：并且；
	 * or：并且 */
	private String logicalRelation;
	
	/**<pre> 条件满足之后，规则生效之前所需积累的时间；
	 * 单位是 秒；
	 */
	private int accumilateTime;
	
	
	public String getLogicalRelation() {
		return logicalRelation;
	}

	public void setLogicalRelation(String logicalRelation) {
		this.logicalRelation = logicalRelation;
	}

	public int getAccumilateTime() {
		return accumilateTime;
	}

	public void setAccumilateTime(int accumilateTime) {
		this.accumilateTime = accumilateTime;
	}

	public TriggerTemplateFactor(int factorID, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime, int roomType,
			int operator, int minValue, int maxValue, int validFlag,
			String logicalRelation, int accumilateTime) {
		super(factorID, createOperator, modifyOperator, createTime, modifyTime,
				roomType, operator, minValue, maxValue, validFlag);
		this.logicalRelation = logicalRelation;
		this.accumilateTime = accumilateTime;
	}
	

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public TriggerTemplateFactor() {
	}
	
	public static TriggerTemplateFactor fromJson(JSONObject factorJson){		
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TriggerTemplateFactor factor= new TriggerTemplateFactor();
		try {
			factor.setLogicalRelation(factorJson.getString("logicalRelation"));
			factor.setRoomType(factorJson.getInt("roomType"));
			factor.setFactorID(factorJson.getInt("factorID"));
			factor.setOperator(factorJson.getInt("operator"));
			factor.setMinValue(factorJson.getInt("minValue"));
			factor.setMaxValue(factorJson.getInt("maxValue"));
			factor.setMaxValue(factorJson.getInt("accumilateTime"));
			factor.setValidFlag(factorJson.getInt("validFlag"));
			factor.setCreateTime(sdf.parse(factorJson.getString("createTime")));
			factor.setModifyTime(sdf.parse(factorJson.getString("modifyTime")) );
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return factor;		
	}

	public JSONObject toJson() {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject factorJson=new JSONObject();
    	try {
        	factorJson.put("logicalRelation", getLogicalRelation());        	
        	factorJson.put("roomType", getRoomType());
        	factorJson.put("factorID", getFactorID());
        	factorJson.put("operator", getOperator());
        	factorJson.put("minValue", getMinValue());
        	factorJson.put("maxValue", getMaxValue());
        	factorJson.put("accumilateTime", getAccumilateTime());
        	factorJson.put("validFlag", getValidFlag());
        	factorJson.put("createTime", sdf.format(getCreateTime()));
			factorJson.put("modifyTime", sdf.format(getModifyTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		return factorJson;
	}	
}
