package cooxm.devicecontrol.encode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Apr 2, 2015 5:22:06 PM 
 */

public class SQLiteUtil {
	static final Logger logger = Logger.getLogger(SQLiteUtil.class);
	   
	String dbNanme;
	Statement stat;
	
	public SQLiteUtil(String dbNanme){
	    try {
			Class.forName("org.sqlite.JDBC");
		    Connection conn =	      DriverManager.getConnection("jdbc:sqlite:"+dbNanme);
		    this.stat = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
   /***执行SQL语句，失败返回-1，成功则返回成功执行的记录条数 */
   public int query(String sqlStatement){
       int row=-1;
       try{
           row=stat.executeUpdate(sqlStatement);
           return row;
       }catch(Exception e){
    	   System.out.println(e.toString());
           return row;
       }	       
   }
   
   public synchronized String select(String sqlStatement){
	   ResultSet rs=null;
       String result=new String();
       int size=0;
       try{
           rs=stat.executeQuery(sqlStatement);
           size=rs.getMetaData().getColumnCount();           
           while(rs!=null && rs.next()){
        	   for(int i=0;i<size;i++){
        		   result=result+rs.getString(i+1);//+",";
        		   if(i!=size-1){
        			   result=result+";";
        		   }
        	   }
        	   result=result+"\n";
           }           
           rs.close();
           if(result.length()>=1){
        	   return result.substring(0, result.length()-1);
           }else{
        	   return null;
           }        
       }catch(Exception e){
           //System.out.println("ERROR:"+e.toString());
    	   logger.error(e.toString(),e);
           return null;
       }
   }
	   
	   
	
	
	public static void main(String[] args) throws Exception {
	    Class.forName("org.sqlite.JDBC");
	    Connection conn =	      DriverManager.getConnection("jdbc:sqlite:ird.db");
	    Statement stat = conn.createStatement();
	    //stat.executeUpdate("create table formats2 as select * from formats;");

	    ResultSet rs = stat.executeQuery( "SELECT * FROM formats;" );
	    
	    List<String[]> rsList=new ArrayList<String[]>();
	      while ( rs.next() ) {
	    	  String [] line=new String[7];
	         String  format_name = rs.getString("format_name");
	         String rename=ChineseToSpell.converterToSpell(format_name).toLowerCase();
	         for (int i=0;i<7;i++){
	        	 line[i]=rs.getString(i+1);
	         }
	         line[3]=rename;
	         rsList.add(line);
	      }
	      
	      for (int i = 0; i < rsList.size(); i++) {
	    	  String[] line2=rsList.get(i);
		         System.out.println("insert into formats2 values("+line2[0]+","
		        		 +line2[1]+","
		        		 +line2[2]+",'"
		        		 +line2[3]+"','"
		        		 +line2[4]+"','"
		        		 +line2[5]+"','"
		        		 +line2[6]+"');");
		         stat.executeUpdate( "insert into formats2 values("+line2[0]+","
		        		 +line2[1]+","
		        		 +line2[2]+",'"
		        		 +line2[3]+"','"
		        		 +line2[4]+"','"
		        		 +line2[5]+"','"
		        		 +line2[6]+"');"	        		 
		        		 );
	      

			
		}
	      rs.close();
	}
	    
	

}
