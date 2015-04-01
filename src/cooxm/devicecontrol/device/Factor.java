package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：27 Jan 2015 14:22:01 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.INTERNAL;

import cooxm.devicecontrol.util.MySqlClass;

/*** 情景模式所包含的情景因素*/
public class Factor  extends FactorDict  { 

	private int roomID;
	private int roomType;
	/**<pre>
	1：= 等于
	2：≠不等于
	3：between 介于[左右封闭]
	4：not between不在之间
	5：≥大于等于
	6：>大于
	7:  ≤小于等于
	8:  <小于
	9：    介于(左右都开)
	10: 介于[左闭右开)
	11: 介于(左开右闭]
    */
	private int operator;
	
	private int minValue;
	private int maxValue;

	/**0：无效； 
	   1：有效 */
	private int validFlag;
	public int getRoomID() {
		return roomID;
	}
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	public int getRoomType() {
		return roomType;
	}

	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(int validFlag) {
		this.validFlag = validFlag;
	}

	public Factor() {	}

	public Factor(int factorid, int roomType,int operator, int minValue, int maxValue,
			int validFlag) {
		super(factorid);
		this.roomType = roomType;
		this.operator = operator;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.validFlag = validFlag;
	}	
	
	
    /**情景的factor初始化 */
	public Factor(int factorID, int createOperator, int modifyOperator,
			Date createTime, Date modifyTime, int roomID,int roomType, int operator,
			int minValue, int maxValue, int validFlag) {
		super(factorID, createOperator, modifyOperator, createTime, modifyTime);
		this.roomID=roomID;
		this.roomType = roomType;
		this.operator = operator;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.validFlag = validFlag;
	}
	
	

	public Factor(int factorID, int createOperator, int modifyOperator,
			Date createTime, Date modifyTime, int roomType, int operator,
			int minValue, int maxValue, int validFlag) {
		this.roomType = roomType;
		this.operator = operator;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.validFlag = validFlag;		
	}
	public  JSONObject toProfileJson(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject factorJson=new JSONObject();
    	try {
        	factorJson.put("factorID", getFactorID());
        	factorJson.put("roomID", getRoomID());
        	factorJson.put("roomType", getRoomType());
        	factorJson.put("minValue", getMinValue());
        	factorJson.put("maxValue", getMaxValue());
        	factorJson.put("operator", getOperator());
        	factorJson.put("validFlag", getValidFlag());
        	factorJson.put("createTime", sdf.format(getCreateTime()));
			factorJson.put("modifyTime", sdf.format(getModifyTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return factorJson;		
	}
	
	
	public static Factor fromProfileJson(JSONObject factorJson) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Factor factor= new Factor();
		try {
			factor.setFactorID(factorJson.getInt("factorID"));
			factor.roomID=factorJson.getInt("roomID");
			factor.roomType=factorJson.getInt("roomType");
			factor.setMinValue(factorJson.getInt("minValue"));
			factor.setMaxValue(factorJson.getInt("maxValue"));
			factor.operator=factorJson.getInt("operator");
			factor.validFlag=factorJson.getInt("validFlag");
			factor.setCreateTime(sdf.parse(factorJson.getString("createTime")) );
			factor.setModifyTime(sdf.parse(factorJson.getString("modifyTime")) );
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return factor;		
	}
	

/*	public  JSONObject toProfleTemplateJson(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject factorJson=new JSONObject();
    	try {
        	factorJson.put("factorID", getFactorID());
        	//factorJson.put("roomID", getRoomID());
        	factorJson.put("roomType", getRoomType());
        	factorJson.put("minValue", getMinValue());
        	factorJson.put("maxValue", getMaxValue());
        	factorJson.put("operator", getOperator());
        	factorJson.put("validFlag", getValidFlag());
        	factorJson.put("createTime", sdf.format(getCreateTime()));
			factorJson.put("modifyTime", sdf.format(getModifyTime()));
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return factorJson;		
	}
	
	
	public static Factor fromProfleTemplateJson(JSONObject factorJson) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Factor factor= new Factor();
		try {
			factor.setFactorID(factorJson.getInt("factorID"));
			//factor.setRoomID(factorJson.getInt("roomID"));
			factor.setRoomType(factorJson.getInt("roomType"));
			factor.setMinValue(factorJson.getInt("minValue"));
			factor.setMaxValue(factorJson.getInt("maxValue"));
			factor.setOperator(factorJson.getInt("operator"));
			factor.setValidFlag(factorJson.getInt("validFlag"));
			factor.setCreateTime(sdf.parse(factorJson.getString("createTime")) );
			factor.setModifyTime(sdf.parse(factorJson.getString("modifyTime")) );
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return factor;		
	}*/	

	public static void main(String[] args){
		FactorDict fact= new FactorDict();

		
		
	}
	
	
}


