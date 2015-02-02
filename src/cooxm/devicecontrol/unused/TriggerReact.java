package cooxm.devicecontrol.unused;
/*
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：30 Jan 2015 17:20:00 
 */
import cooxm.devicecontrol.device.TriggerTemplateReact;

public class TriggerReact extends TriggerTemplateReact {
	int ctrolID;
	//int roomID;
	
	

	public TriggerReact(int reactType, int targetID, int reactWay,  int ctrolID) {
		super(reactType, targetID, reactWay);
		this.ctrolID = ctrolID;
	}

	public int getCtrolID() {
		return ctrolID;
	}

	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}

	public TriggerReact() {
	}
	

}
