package cooxm.devicecontrol.device;

import java.util.Date;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：29 Jan 2015 14:23:50 
 */

public class RegulationTemplateFactor extends FactorTemplate{

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

	public RegulationTemplateFactor(int factorID, int createOperator,
			int modifyOperator, Date createTime, Date modifyTime, int roomType,
			int operator, int minValue, int maxValue, int validFlag,
			String logicalRelation, int accumilateTime) {
		super(factorID, createOperator, modifyOperator, createTime, modifyTime,
				roomType, operator, minValue, maxValue, validFlag);
		this.logicalRelation = logicalRelation;
		this.accumilateTime = accumilateTime;
	}

	public RegulationTemplateFactor() {
	}	
}
