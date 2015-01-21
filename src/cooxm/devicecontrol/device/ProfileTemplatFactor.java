package cooxm.devicecontrol.device;

import java.util.Date;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：20 Jan 2015 17:53:29 
 */

public class ProfileTemplatFactor extends FactorDict {
	protected int spaceRange;
	

	public static void main(String[] args) {

	}


	public int getSpacerange() {
		return spaceRange;
	}


	public void setSpacerange(int spacerange) {
		this.spaceRange = spacerange;
	}

	public ProfileTemplatFactor(int factorid, int factortype,
			String factorname, String description, String measurement,
			int mstype, int createoperator, int modifyoperator,
			Date createtime, Date modifytime,int spaceRange) {
		super(factorid, factortype, factorname, description, measurement, mstype,
				createoperator, modifyoperator, createtime, modifytime);
		this.spaceRange=spaceRange;		
	}


	public ProfileTemplatFactor() {
	}
}
