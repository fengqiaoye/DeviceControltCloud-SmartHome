/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：17 Dec 2014 17:51:32 
 */
package device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.MySqlClass;

public class DeviceMap extends HashMap<String, Device> {
	
	/*** Map<ctrolID_deviceID,Device>*/
	//static Map<String, Device> deviceMap=new HashMap<String, Device>(); 	

	private static final long serialVersionUID = 1L;
	
	public DeviceMap(){}
	public DeviceMap(Map<String, Device> profileSetMap){
		super(profileSetMap);	
	}
	
	public DeviceMap(MySqlClass mysql) throws SQLException{
		super(getDeviceMapFromDB(mysql));
	}

/*** 
   * 从入MYSQL读取device列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static HashMap<String, Device> getDeviceMapFromDB(MySqlClass mysql )throws SQLException	{
		HashMap<String, Device> deviceMap=new HashMap<String, Device>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="select  "
				+" ctr_id       ,"     
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
				//+" where ctr_id="+CtrolID
				//+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		String res=mysql.select(sql);
		System.out.println("get from mysql:\n"+res);
		if(res==null|| res==""){
			System.out.println("ERROR:empty query by : "+sql);
			return null ;
		} 
		
		String[] resArray=res.split("\n");
		//List<Device> deviceList=null;//new ArrayList<Factor>();
		Device device=new Device();
		for(String line:resArray){ 
			String[] index=line.split(",");
			device.CtrolID=Integer.parseInt(index[0]);	
			device.deviceID=Integer.parseInt(index[1]);	
			device.deviceSN=index[2];
			device.deviceType=Integer.parseInt(index[3]);
			device.type=Integer.parseInt(index[4]);
			device.roomID=Integer.parseInt(index[5]);
			device.wall=Integer.parseInt(index[6]);
			device.relatedDevType=Integer.parseInt(index[7]);
			try {
				device.createTime=sdf.parse(index[8]);
				device.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deviceMap.put(device.CtrolID+"_"+device.deviceID, device);
		}
		return deviceMap;
	}
	
	
	public List<Device> getDevicesByCtrolID(int CtrolID){
		List<Device> deviceList= new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==CtrolID){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
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
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		DeviceMap dm=new DeviceMap(mysql);
		System.out.println(dm.size());

	}

}
