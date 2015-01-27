package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：27 Jan 2015 14:22:01 
 */

import java.util.Date;

/*** 情景模式所包含的情景因素*/
public class Factor  extends FactorDict  { 

	public int minValue;
	public int maxValue;
	public int compareWay;
	public int validFlag;
	
	public Factor() {	}

	public Factor(int factorid, int factortype, String factorname,
			String description, String measurement, int mstype,
			int createoperator, int modifyoperator, Date createTime,
			Date modifyTime, int minValue, int maxValue, int compareWay,
			int validFlag) {
		super(factorid, factortype, factorname, description, measurement,
				mstype, createoperator, modifyoperator, createTime, modifyTime);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.compareWay = compareWay;
		this.validFlag = validFlag;
	}
}


