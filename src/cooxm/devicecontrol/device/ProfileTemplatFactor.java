package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：27 Jan 2015 14:15:48 
 */


import java.util.Date;

public class ProfileTemplatFactor extends Factor {
	

	public int spaceRange;

	
	public int getSpaceRange() {
		return spaceRange;
	}
	public void setSpaceRange(int spaceRange) {
		this.spaceRange = spaceRange;
	}
	public ProfileTemplatFactor(int factorid, int factortype,
			String factorname, String description, String measurement,
			int mstype, int createoperator, int modifyoperator,
			Date createTime, Date modifyTime, int minValue, int maxValue,
			int compareWay, int validFlag, int spaceRange) {
		super(factorid, factortype, factorname, description, measurement,
				mstype, createoperator, modifyoperator, createTime, modifyTime,
				minValue, maxValue, compareWay, validFlag);
		this.spaceRange = spaceRange;
	}
	public ProfileTemplatFactor() {}
	public static void main(String[] args) {
		
		ProfileTemplatFactor t=new ProfileTemplatFactor();
		t.setSpaceRange(0);

	}
}
