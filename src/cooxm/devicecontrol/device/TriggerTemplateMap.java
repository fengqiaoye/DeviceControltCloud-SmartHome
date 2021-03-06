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

/**  < triggerTemplateID,TriggerTemplate > */
public class TriggerTemplateMap  extends HashMap<Integer, TriggerTemplate>{


	private static final long serialVersionUID = 1L;
	private MySqlClass mysql;
	
	
	public TriggerTemplateMap(){}
	public TriggerTemplateMap(Map<Integer, TriggerTemplate> TriggerTemplateMap){
		super(TriggerTemplateMap);		
	}
	
	public TriggerTemplateMap(MySqlClass mysql) {
		super(getTriggerTemplateMapFromDB2(mysql));
		this.mysql=mysql;
	}
	   /*** 
	   * 从入MYSQL读取情景模式列表
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
	   * @table  info_trigger
	   * @throws SQLException 
	    */
		public static HashMap<Integer, TriggerTemplate> getTriggerTemplateMapFromDB2(MySqlClass mysql) 
		{ 		
			HashMap<Integer, TriggerTemplate> triggerMap=new HashMap<Integer, TriggerTemplate>();
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				mysql.conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			} 
			String sql="select distinct triggerid from " +TriggerTemplate.triggerHeaderTable+"; ";
			String res=mysql.select(sql);
			if(res==null){
				return null;
			}
			String[] ctrilIDs=res.split("\n");
			for (int i = 0; i < ctrilIDs.length; i++) {
				TriggerTemplate t=TriggerTemplate.getFromDB(mysql, Integer.parseInt(ctrilIDs[i]));
				if(t!=null){
					triggerMap.put(Integer.parseInt(ctrilIDs[i]), t);
				}
			}
			return triggerMap;
		}
	
   /*** 
   * 从入MYSQL读取情景模式列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
   * @table  info_trigger
   * @throws SQLException 
    */
	/*public static HashMap<Integer, TriggerTemplate> getTriggerTemplateMapFromDB(MySqlClass mysql) 
	{   
		HashMap<Integer, TriggerTemplate> triggerMap=new HashMap<Integer, TriggerTemplate>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mysql.conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		TriggerTemplate trigger=null;
		List<TriggerTemplateFactor> factorList=null;
		List<TriggerTemplateReact> triggerReactList=null;
		
		//TriggerTemplate triggert=new TriggerTemplate();
		String sql0="select  "
				+" triggerid ," 
				+" sttemplateid ," 
				+"triggername ,"
				+"description, "	
				+"isabstract ,"
				+"createtime,"
				+"modifytime"
				+ " from  "	
				+TriggerTemplate.triggerHeaderTable
				//+" where triggerid="+triggerid
				+ ";";
		//System.out.println("query:"+sql0);
		String res3=mysql.select(sql0);
		String[] resArray3= res3.split("\n");
		for (int i = 0; i < resArray3.length; i++) {
			trigger=new TriggerTemplate();
			String[] resArray0=resArray3[i].split(",");
			trigger.setTriggerTemplateID(Integer.parseInt(resArray0[0]));
			trigger.setProfileTemplateID(Integer.parseInt(resArray0[1]));
			trigger.setTriggerName(resArray0[2]);
			trigger.setDescription(resArray0[3]);
			trigger.setIsAbstract(Integer.parseInt(resArray0[4]));
			try {
				trigger.setCreateTime(sdf.parse(resArray0[5]));
				trigger.setModifyTime(sdf.parse(resArray0[6]));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			factorList=new ArrayList<TriggerTemplateFactor>();
			triggerReactList=new ArrayList<>();
			trigger.setTriggerTemplateFactorList(factorList);
			trigger.setTriggerTemplateReactList(triggerReactList);
			triggerMap.put(trigger.getTriggerTemplateID(), trigger);
		}

		
		String sql="select "		
				+"triggerid  ,"   
				+"sttemplateid ,"
				+"logicalrelation ,"
				+"roomtype ,"		
				+"factorid ,"
				+"operator ,"
				+"min ,"
				+"max ,"
				+"accumilatetime   ,"
				+"isabstract "
				//+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				//+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from  "				
				+TriggerTemplate.triggerFactorTable
				//+" where triggerid="+triggerid
				//+" and ctrolid="+ctrolID
				+ ";";

		String res=mysql.select(sql);
		if(res==null || res=="" ) {
			System.err.println("ERROR:query result is empty: "+sql);
			return null;
		}


		
		String[] resArray=res.split("\n");
		for(String line:resArray){
			String[] cells=line.split(",");
			if(cells.length>0){	
				int key=Integer.parseInt(cells[0]);//+"_"+cells[1];
				if(triggerMap.containsKey(key)){
					trigger=triggerMap.get(key);
					factorList=trigger.getTriggerTemplateFactorList();				
				}else{
					trigger=new TriggerTemplate();
					factorList=new ArrayList<TriggerTemplateFactor>();
					//triggerReactList=new ArrayList<TriggerTemplateReact>();
				}
				TriggerTemplateFactor ft=new TriggerTemplateFactor();					
				ft.setLogicalRelation(cells[1]);					
				ft.setRoomType(Integer.parseInt(cells[2]));
				ft.setFactorID(Integer.parseInt(cells[3]));
				ft.setOperator(Integer.parseInt(cells[4]));
				ft.setMinValue(Integer.parseInt(cells[5]));
				ft.setMaxValue(Integer.parseInt(cells[6]));
				ft.setAccumilateTime(Integer.parseInt(cells[7]));
				//ft.setValidFlag(Integer.parseInt(cells[10]));
				ft.setIsAbstract(Integer.parseInt(cells[8]));
//				try {
//					ft.setCreateTime(sdf.parse(cells[10]));
//					ft.setModifyTime(sdf.parse(cells[11]));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
				
				factorList.add(ft);
				trigger.setTriggerTemplateFactorList(factorList);
				trigger.setTriggerTemplateID(Integer.parseInt(cells[0]));
				//trigger.setProfileTemplateID(Integer.parseInt(cells[1]));
				triggerMap.put(key, trigger);
			}else {
				System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ TriggerTemplate.triggerFactorTable);
				return null;				
			}
			
		}			
		
		String sql2="select  "
				//+"ctrolid  ,"    			
 				+" triggerid ," 
				+" reacttype ," 
				+"targetid ,"
				+"reactway "	
				+ " from  "	
				+TriggerTemplate.triggerReactTable
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
			
			int key=Integer.parseInt(array[0]);//+"_"+array[1];
			if(triggerMap.containsKey(key)){
				trigger=triggerMap.get(key);
				triggerReactList=trigger.getTriggerTemplateReactList();	
				if(null==triggerReactList){
					triggerReactList=new ArrayList<TriggerTemplateReact>();
					trigger.setTriggerTemplateReactList(triggerReactList);
				}
				
			}else{
				trigger=new TriggerTemplate();
				triggerReactList=new ArrayList<TriggerTemplateReact>();
				trigger.setTriggerTemplateReactList(triggerReactList);
			}
			TriggerTemplateReact react=new TriggerTemplateReact();
			react.setReactType(Integer.parseInt(array[1]));
			react.setTargetID(Integer.parseInt(array[2]));
			react.setReactWay(Integer.parseInt(array[3]));
			triggerReactList.add(react);
		}	
		trigger.setTriggerTemplateReactList(triggerReactList);
		try {
			mysql.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return triggerMap;		
	}*/
	

	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public TriggerTemplate put(Integer key,TriggerTemplate trigger) {
		if(null==this.mysql)
			return null;
		trigger.saveToDB(this.mysql)	;
		super.put(key, trigger);
		return trigger;		
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	/*@Override
	public TriggerTemplate remove(Object key) {
		TriggerTemplate trigger = super.get(key);
		TriggerTemplate.deleteFromDB(mysql, trigger.getTriggerTemplateID(), trigger.getTriggerTemplateID());
		return super.remove(key);

	}*/
	

	/*** 获取一个家庭所有情景模式
	 * @param ctrolID
	 * @return  List < TriggerTemplate > 情景模式列表	 * 
	 * */
	public List<TriggerTemplate> getTriggerTemplatesByProfileID(int profileID){	
		List<TriggerTemplate> triggerList=new ArrayList<TriggerTemplate>();
		for (Entry<Integer, TriggerTemplate> entry : this.entrySet()) {
			if(entry.getValue().getProfileTemplateID()==profileID){
				triggerList.add(entry.getValue());
			}			
		}
		return triggerList;
	}


	/*** 获取一个房间所有情景模式
	 * @param: roomID
	 * @param: ctrolID 
	 * */
	public List<TriggerTemplate> getTriggerTemplatesByRoomType(int roomType){	
		List<TriggerTemplate> triggerList=new ArrayList<TriggerTemplate>();
		for (Entry<Integer, TriggerTemplate> entry : this.entrySet()) {
			TriggerTemplate trigger=entry.getValue();
			for (TriggerTemplateFactor factor : trigger.getTriggerTemplateFactorList()) {
				if(factor.getRoomType()==roomType){
					triggerList.add(trigger);
					break;
				}				
			}		
		}
		return triggerList;
	}
	
	

	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");
		TriggerTemplateMap rm=new TriggerTemplateMap(mysql);
		System.out.println(rm.size());

	}

}