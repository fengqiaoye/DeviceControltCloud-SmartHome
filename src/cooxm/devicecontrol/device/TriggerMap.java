package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2 Feb 2015 10:23:02 
 */

/**  < ctrolID_triggerID,Trigger > */
public class TriggerMap  extends HashMap<String, Trigger>{

	private static final long serialVersionUID = 1L;
	private MySqlClass mysql;
	
	
	public TriggerMap(){}
	public TriggerMap(Map<String, Trigger> TriggerMap){
		super(TriggerMap);		
	}
	
	public TriggerMap(MySqlClass mysql) {
		super(getTriggerMapFromDB(mysql));
		this.mysql=mysql;
	}
	
   /*** 
   * 从入MYSQL读取情景模式列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_trigger
   * @throws SQLException 
    */
	public static HashMap<String, Trigger> getTriggerMapFromDB(MySqlClass mysql) 
	{   
		HashMap<String, Trigger> triggerMap=new HashMap<String, Trigger>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String sql="select "
				+"ctrolid  ,"    			
				+"triggerid  ,"     
				+"logicalrelation ,"
				+"roomtype ,"
				+"roomid ,"					
				+"factorid ,"
				+"operator ,"
				+"min ,"
				+"max ,"
				+"accumilatetime   ,"
				+"validflag, "
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+Trigger.triggerFactorInfoTable
				//+" where triggerid="+triggerid
				//+" and ctrolid="+ctrolID
				+ ";";

		String res=mysql.select(sql);
		if(res==null || res=="" ) {
			System.err.println("ERROR:query result is empty: "+sql);
			return null;
		}
		Trigger trigger=null;
		List<TriggerFactor> factorList=null;
		List<TriggerTemplateReact> triggerReactList=null;
		
		String[] resArray=res.split("\n");
		for(String line:resArray){
			String[] cells=line.split(",");
			if(cells.length>0){	
				String key=cells[0]+"_"+cells[1];
				if(triggerMap.containsKey(key)){
					trigger=triggerMap.get(key);
					factorList=trigger.getTriggerFactorList();				
				}else{
					trigger=new Trigger();
					factorList=new ArrayList<TriggerFactor>();
					//triggerReactList=new ArrayList<TriggerTemplateReact>();
				}
				TriggerFactor ft=new TriggerFactor();					
				ft.setLogicalRelation(cells[2]);					
				ft.setRoomType(Integer.parseInt(cells[3]));
				ft.setRoomType(Integer.parseInt(cells[4]));
				ft.setFactorID(Integer.parseInt(cells[5]));
				ft.setOperator(Integer.parseInt(cells[6]));
				ft.setMinValue(Integer.parseInt(cells[7]));
				ft.setMaxValue(Integer.parseInt(cells[8]));
				ft.setAccumilateTime(Integer.parseInt(cells[9]));
				ft.setValidFlag(Integer.parseInt(cells[10]));
				try {
					ft.setCreateTime(sdf.parse(cells[11]));
					ft.setModifyTime(sdf.parse(cells[12]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				factorList.add(ft);
				trigger.setTriggerFactorList(factorList);
				trigger.setCtrolID(Integer.parseInt(cells[0]));
				trigger.setTriggerID(Integer.parseInt(cells[1]));
				triggerMap.put(key, trigger);
			}else {
				System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ Trigger.triggerFactorInfoTable);
				return null;				
			}
			
		}			
		
//new ArrayList<TriggerTemplateReact>();
		
		String sql2="select  "
				+"ctrolid  ,"    			
 				+" triggerid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "	
				+ " from  "	
				+Trigger.triggerReactInfoTable
				//+" where triggerid="+triggerid
				+ ";";

		String res2=mysql.select(sql2);
		if(res2==null|| res2==""){
			System.err.println("ERROR:empty query by : "+sql2);
			return null;
		} 
		String[] resArray2=res2.split("\n");
		for(String line:resArray2){		
			String [] array=line.split(",");
			
			String key=array[0]+"_"+array[1];
			if(triggerMap.containsKey(key)){
				trigger=triggerMap.get(key);
				triggerReactList=trigger.getTriggerTemplateReactList();	
				if(null==triggerReactList){
					triggerReactList=new ArrayList<TriggerTemplateReact>();
				}
			}else{
				trigger=new Trigger();
				triggerReactList=new ArrayList<TriggerTemplateReact>();
				//factorList=new ArrayList<TriggerFactor>();
			}
			TriggerTemplateReact react=new TriggerTemplateReact();
			react.setReactType(Integer.parseInt(array[2]));
			react.setTargetID(Integer.parseInt(array[3]));
			react.setReactWay(Integer.parseInt(array[4]));
			triggerReactList.add(react);
			trigger.setTriggerTemplateReactList(triggerReactList);
		}	
		trigger.setTriggerTemplateReactList(triggerReactList);
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return triggerMap;		
	}
	

	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public Trigger put(String key,Trigger trigger) {
		if(null==this.mysql)
			return null;
		int x=trigger.saveToDB(this.mysql)	;
		if(x>0){
			return super.put(key, trigger);

		}else{
			return null;
		}
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	@Override
	public Trigger remove(Object key) {
		Trigger trigger = super.get(key);
		Trigger.deleteFromDB(mysql, trigger.getCtrolID(), trigger.getTriggerID());
		return super.remove(key);

	}
	

	/*** 获取一个家庭所有情景模式
	 * @param ctrolID
	 * @return  List < Trigger > 情景模式列表	 * 
	 * */
	public List<Trigger> getTriggersByctrolID(int ctrolID){	
		List<Trigger> triggerList=new ArrayList<Trigger>();
		for (Entry<String, Trigger> entry : this.entrySet()) {
			if(entry.getKey().split("_")[0]==ctrolID+""){
				triggerList.add(entry.getValue());
			}			
		}
		return triggerList;
	}


	/*** 获取一个房间所有情景模式
	 * @param: roomID
	 * @param: ctrolID 
	 * */
	public List<Trigger> getTriggersByRoomID(int roomID){	
		List<Trigger> triggerList=new ArrayList<Trigger>();
		for (Entry<String, Trigger> entry : this.entrySet()) {
			Trigger trigger=entry.getValue();
			for (Factor factor : trigger.getTriggerFactorList()) {
				if(factor.getRoomID()==roomID){
					triggerList.add(trigger);
					break;
				}				
			}		
		}
		return triggerList;
	}
	
	

	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		TriggerMap rm=new TriggerMap(mysql);
		System.out.println(rm.size());

	}

}