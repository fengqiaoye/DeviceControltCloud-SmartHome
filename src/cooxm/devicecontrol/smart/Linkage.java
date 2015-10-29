package cooxm.devicecontrol.smart;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 19, 2015 10:01:16 AM 
 */

public class Linkage {
	/**本来要操作的家电 */
	int srcDeviceType;
	/**联动家电类型 */
	int linkDeviceType;
	/** 1关联； 0 ：未关联*/
	int isLinked;
	/**优先级 */
	int priority;
	
	/**状态码 或者 操作码 */
	int operationCode;
	
	public int getSrcDeviceType() {
		return srcDeviceType;
	}
	public void setSrcDeviceType(int srcDeviceType) {
		this.srcDeviceType = srcDeviceType;
	}
	public int getLinkDeviceType() {
		return linkDeviceType;
	}
	public void setLinkDeviceType(int linkDeviceType) {
		this.linkDeviceType = linkDeviceType;
	}
	public int getIsLinked() {
		return isLinked;
	}
	public void setIsLinked(int isLinked) {
		this.isLinked = isLinked;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(int operationCode) {
		this.operationCode = operationCode;
	}
	
	public Linkage(int srcDeviceType, int linkDeviceType, int isLinked,
			int priority, int operationCode) {
		this.srcDeviceType = srcDeviceType;
		this.linkDeviceType = linkDeviceType;
		this.isLinked = isLinked;
		this.priority = priority;
		this.operationCode = operationCode;
	}
	
	public static void main(String[] args) {

	}

}
