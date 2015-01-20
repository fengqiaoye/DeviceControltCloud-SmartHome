package cooxm.devicecontrol.device;

import java.util.Date;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：20 Jan 2015 17:53:29 
 */

public class ProfileTemplatFactor extends FactorDict {
	protected int spacerange;
	

	public static void main(String[] args) {

	}


	public int getSpacerange() {
		return spacerange;
	}


	public void setSpacerange(int spacerange) {
		this.spacerange = spacerange;
	}

	public ProfileTemplatFactor(int factorid, int factortype,
			String factorname, String description, String measurement,
			int mstype, String createoperator, String modifyoperator,
			Date createtime, Date modifytime,int spaceRange) {
		super(factorid, factortype, factorname, description, measurement, mstype,
				createoperator, modifyoperator, createtime, modifytime);
		this.spacerange=spaceRange;		
	}


	public ProfileTemplatFactor() {
	}
	

}
