package cooxm.devicecontrol.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;

import cooxm.devicecontrol.util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Oct 19, 2015 10:14:15 AM 
 */

/** Hash<srcDeviceType,List<Linkage>>*/
public class LinkageMap extends HashMap<Integer, List<Linkage>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	LinkageMap(){}
	
	public LinkageMap initFromDB(MySqlClass mysql){
		LinkageMap linkageMap = new LinkageMap();
		String sql="select srcdevicetype,linkdevicetype,islinked,priority ,operationcode  from info_device_linkage;";
		String res=mysql.select(sql);
		if (res==null) {
			return null;
		}
		String [] lineArray=res.split("\n");

		for (String  line : lineArray) {
			String[] cells=line.split(",");
			List<Linkage> linkageList= linkageMap.get(Integer.parseInt(cells[0]));
			if (linkageList==null) {
				linkageList=new ArrayList<Linkage>();
			}
			Linkage linkage=new Linkage(Integer.parseInt(cells[0]), Integer.parseInt(cells[1]), Integer.parseInt(cells[2]), Integer.parseInt(cells[3]), Integer.parseInt(cells[4]));
			linkageList.add(linkage);
			linkageMap.put(Integer.parseInt(cells[0]), linkageList);
		}
		return linkageMap;		
	}	
	

	public static void main(String[] args) {
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "cooxm", "cooxm");		
		LinkageMap map=new LinkageMap();
		map.initFromDB(mysql);	
		System.out.println(map.size());
	}

}
