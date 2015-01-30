package cooxm.devicecontrol.device;

import java.util.Date;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：30 Jan 2015 17:17:09 
 */

public class RegulationFactor extends Factor {
	//int ctrolID;
	int roomID;
	String logicalRelation;
	int accumilateTime;

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	

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

	public RegulationFactor(int factorID, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime,  int roomID,int roomType,
			int operator, int minValue, int maxValue, int validFlag,
			String logicalRelation, int accumilateTime) {
		super(factorID, createOperator, modifyOperator, createTime, modifyTime,
				roomType, operator, minValue, maxValue, validFlag);
		this.logicalRelation=logicalRelation;
		this.accumilateTime=accumilateTime;
		this.roomID = roomID;
	}

	public RegulationFactor() {
	}
	
}
