package cooxm.devicecontrol.control;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 22, 2015 4:27:24 PM 
 */

public class Language {

	int ctrolID;
	int languageID;
	Date modifyTime;
	
	public int getCtrolID() {
		return ctrolID;
	}
	public void setCtrolID(int ctrolID) {
		this.ctrolID = ctrolID;
	}
	public int getLanguageID() {
		return languageID;
	}
	public void setLanguageID(int languageID) {
		this.languageID = languageID;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public Language(int ctrolID, int languageID, Date modifyTime) {
		this.ctrolID = ctrolID;
		this.languageID = languageID;
		this.modifyTime = modifyTime;
	}
	
	public Language() {
	}
	
	
	public Language(JSONObject json) {
		try {
			this.ctrolID=json.getInt("ctrolID");
			this.ctrolID=json.getInt("languageID");
			this.ctrolID=json.getInt("modifyTime");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject toJson(){
		JSONObject json=new JSONObject();
		try {
			json.put("ctrolID", this.ctrolID);
			json.put("languageID", this.languageID);
			json.put("modifyTime", this.modifyTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}		 
		return json;
	}
	
	
	public static Language getLanguage(MySqlClass mysql,int ctrolID)  {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql=" select ctrolid,languageid, date_format(modifytime,'%Y-%m-%d %H:%i:%S') from info_control_language where ctrolid="+ctrolID+";";
		String result=mysql.select(sql);	
		String [] cells=result.split(",");	
		Date date;
		try {
			date = sdf.parse(cells[2]);
			Language lan=new Language(Integer.parseInt(cells[0]), Integer.parseInt(cells[1]), date);
			return lan;	
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public int saveLanguage(MySqlClass mysql)  {
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql=" replace into  info_control_language(ctrolid,languageid, modifytime) values ( "
				+ this.ctrolID+","
				+ this.languageID+","
				+ sdf.format(this.modifyTime)
				+";";
				
		int result=mysql.query(sql);		
		return result;	
	}
}
