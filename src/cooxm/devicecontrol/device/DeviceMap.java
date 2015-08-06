/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：17 Dec 2014 17:51:32 
 */
package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.MySqlClass;
import redis.clients.jedis.Jedis;

/*** Map< ctrolID_deviceID,Device >*/
public class DeviceMap extends HashMap<String, Device> {
	static Logger log= Logger.getLogger(DeviceMap.class);
	/*** Map<ctrolID_deviceID,Device>*/
	//static Map<String, Device> deviceMap=new HashMap<String, Device>(); 	

	private static final long serialVersionUID = 1L;
	private MySqlClass mysql;
	Jedis jedis;
	
	public DeviceMap(){}
	public DeviceMap(Map<String, Device> profileSetMap){
		super(profileSetMap);	
	}
	
	public DeviceMap(MySqlClass mysql,Jedis jedis) throws SQLException{
		super(getDeviceMapFromDB(mysql));
		this.mysql=mysql;
		this.jedis=jedis;
		this.jedis.select(9);
	}

/*** 
   * 从入MYSQL读取device列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static HashMap<String, Device> getDeviceMapFromDB(MySqlClass mysql )throws SQLException	{
		log.info("Start to initialize deviceMap....");
		HashMap<String, Device> deviceMap=new HashMap<String, Device>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="select  "
				+" ctr_id       ," 
				+" name       ," 
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				//+" where ctr_id="+ctrolID
				//+" and deviceid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res);
		if(res==null|| res==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null ;
		} 
		
		String[] resArray=res.split("\n");
		//List<Device> deviceList=null;//new ArrayList<Factor>();

		for(String line:resArray){ 
			String[] index=line.split(",");
			Device device=new Device();
			device.ctrolID=Integer.parseInt(index[0]);	
			device.deviceName=index[1];
			device.deviceID=Integer.parseInt(index[2]);	
			device.deviceSN=index[3];
			device.deviceType=Integer.parseInt(index[4]);
			device.type=Integer.parseInt(index[5]);
			device.roomID=Integer.parseInt(index[6]);
			device.wall=Integer.parseInt(index[7]);
			device.relatedDevType=Integer.parseInt(index[8]);
			try {
				device.createTime=sdf.parse(index[9]);
				device.modifyTime=sdf.parse(index[10]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			deviceMap.put(device.ctrolID+"_"+device.deviceID, device);
		}
		log.info("Initialize deviceMap finished !");
		return deviceMap;
	}
	
	/*** 获取 该家庭所有设备，包含加电和 传感器
	 * 
	 * */
	public List<Device> getDevicesByctrolID(int ctrolID){
		List<Device> deviceList= new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==ctrolID){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
	}
	
	/*** 获取 该家庭所有设备，包含加电和 传感器
	 * 
	 * */
	public List<Device> getDevicesByroomID(int ctrolID,int roomID){
		List<Device> deviceList= new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(entry.getValue().getRoomID()==roomID && entry.getValue().getCtrolID()==ctrolID){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
	}
	
	
	/*** 删除 该家庭所有设备，包含加电和 传感器
	 * 
	 * */
	public void deleteDevicesByroomID(int ctrolID,int roomID){
		Iterator<Map.Entry<String, Device>> it = this.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<String, Device> entry=it.next();  
			if(entry.getValue().getRoomID()==roomID && entry.getValue().getCtrolID()==ctrolID){
				it.remove();
				Device.DeleteOneDeviceFromDB(mysql, ctrolID, entry.getValue().getDeviceID());
			}			
		}
	}
	
	
	/*** 获取 该家庭所有设备，只包含加电
	 *   <pre> device.type=0;
	 **/
	//@SuppressWarnings("null")
	public List<Device> getApplianceByctrolID(int ctrolID){
		List<Device> deviceList=new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==ctrolID  && entry.getValue().type==1){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
	}
	
	
	
	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public Device put(String key,Device device) {
		if(null==this.mysql){
			log.error("Can't insert data,mysql is null.");
			return null;

		}
		int x=device.saveToDB(this.mysql)	;
		if(x>0){
		    super.put(key, device);
		    return device;
		}else{
			return null;
		}
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	@Override
	public Device remove(Object ctrolID_deviceID) {		
		if(null==this.mysql)
			return null;
		Device device = super.get(ctrolID_deviceID);		
		int x=Device.DeleteOneDeviceFromDB(mysql, device.ctrolID, device.deviceID);
		try {
			Profile.deleteFactorByDeviceIDFromRedis(jedis, device.ctrolID, device.deviceID);
		} catch (JSONException | ParseException e) {
			e.printStackTrace();			
		}
		if(x>0){
			return super.remove(ctrolID_deviceID);
		}else{
			return null;
		}
		
	}

	/**
	 * @throws SQLException * 
	 * @Title: main 
	 * @Description: TODO
	 * @param  args    
	 * @return void    
	 * @throws 
	 */
	public static void main(String[] args) throws SQLException {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
		Jedis jedis=new Jedis("172.16.35.170", 6379,5000);
		jedis.select(9);
		DeviceMap dm=new DeviceMap(mysql,jedis);
		System.out.println(dm.size());
		
		//dm.deleteDevicesByroomID(40006,3000);
		List<Device> x = dm.getDevicesByctrolID(40007);
		List<Device> x2 = dm.getApplianceByctrolID(40007);
		System.out.println(x.size());
		

		

	}

}
