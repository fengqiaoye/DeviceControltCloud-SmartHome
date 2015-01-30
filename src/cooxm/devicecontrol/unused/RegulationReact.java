package cooxm.devicecontrol.unused;
/*
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：30 Jan 2015 17:20:00 
 */
import cooxm.devicecontrol.device.RegulationTemplateReact;

public class RegulationReact extends RegulationTemplateReact {
	int ctrolID;
	//int roomID;
	
	

	public RegulationReact(int reactType, int targetID, int reactWay,  int ctrolID) {
		super(reactType, targetID, reactWay);
		this.ctrolID = ctrolID;
	}

	public int getCtrolID() {
		return ctrolID;
	}

	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}

	public RegulationReact() {
	}
	

}
