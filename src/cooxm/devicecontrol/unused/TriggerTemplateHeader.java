package cooxm.devicecontrol.unused;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jun 29, 2015 2:17:46 PM 
 * 触发模板的头部
 */

public class TriggerTemplateHeader {
	int	  triggerID    ;
	
	/** <pre>规则生效所依赖的情景模式，如果在任意模式下生效，则为254。ID如下：
	1	睡眠模式
	2	观影模式
	3	离家模式
	4	居家模式
	254  任意情景模式
    */
	int	  profileTemplateID ;
	/**显示中控上的触发名字 */
	String	  triggerName  ;
	/**显示中控上的触发规则描述 */
	String	  description  ;
	
	/**是否抽象，1.显示中控->设置->功能设置； 0.在情景模式设置中. */
	int	  isAbstract   ;
	Date	  createTime   ;
	Date	  modifyTime   ;
	
	public int getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(int triggerID) {
		this.triggerID = triggerID;
	}

	public int getProfileTemplateID() {
		return profileTemplateID;
	}

	public void setProfileTemplateID(int stTemplateID) {
		this.profileTemplateID = stTemplateID;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(int isAbstract) {
		this.isAbstract = isAbstract;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	

	public TriggerTemplateHeader(int triggerID, int profileTemplateID,
			String triggerName, String description, int isAbstract,
			Date createTime, Date modifyTime) {
		this.triggerID = triggerID;
		this.profileTemplateID = profileTemplateID;
		this.triggerName = triggerName;
		this.description = description;
		this.isAbstract = isAbstract;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
	}
	
	public TriggerTemplateHeader(JSONObject json) {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.triggerID    =json.getInt("triggerID");
			this.profileTemplateID =json.getInt("profileTemplateID");
			this.triggerName  =json.getString("triggerName");
			this.description  =json.getString("description");
			this.isAbstract   = json.getInt("isAbstract");;
			this.createTime   =sdf.parse(json.getString("createTime"));
			this.modifyTime   =sdf.parse(json.getString("createTime"));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJson(){
		JSONObject json=new JSONObject();
		try {
			json.put("triggerID", triggerID);
			json.put("profileTemplateID", profileTemplateID);
			json.put("triggerName", triggerName);
			json.put("description", description);
			json.put("isAbstract", isAbstract);
			json.put("createTime", modifyTime);			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static void main(String[] args) {

	}

}
