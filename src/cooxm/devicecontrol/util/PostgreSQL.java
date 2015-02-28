package cooxm.devicecontrol.util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Feb 27, 2015 11:39:35 AM 
 */



import java.sql.*;

import org.apache.log4j.Logger;

public class PostgreSQL {
   
   public Connection conn=null;
   private Statement st=null;
   private ResultSet rs=null;
   static final Logger logger = Logger.getLogger(PostgreSQL.class);
   
   public PostgreSQL(String host, String port,String databaseName,String userName,String password){
       try{
           Class.forName("org.postgresql.Driver").newInstance();
	       //conn=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+databaseName,userName,password);
	       conn=DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+databaseName,userName,password);
		   st=conn.createStatement();          
       }catch(Exception e){
           System.out.println("ERROR:"+e.toString());
           logger.fatal(e.getMessage(),e);          
       }       
   }
   
  
   public String select(String sqlStatement){
       String result=new String();
       int size=0;
       try{
           rs=st.executeQuery(sqlStatement);
           size=st.getResultSet().getMetaData().getColumnCount();
           
           while(rs!=null && rs.next()){
        	   for(int i=0;i<size;i++){
        		   result=result+rs.getString(i+1);//+",";
        		   if(i!=size-1){
        			   result=result+",";
        		   }
        	   }
        	   result=result+"\n";
           }           
           rs.close();
           return result.substring(0, result.length()-1);
       }catch(Exception e){
           //System.out.println("ERROR:"+e.toString());
    	   logger.error(e.toString(),e);
           return null;
       }
   }
   
 
   /***执行SQL语句，失败返回-1，成功则返回成功执行的记录条数 */
   public int query(String sqlStatement){
       int row=-1;
       try{
           row=st.executeUpdate(sqlStatement);
           //this.close();
           return row;
       }catch(Exception e){
           //System.out.println("Executing SQL: "+e.toString());
           //logger.error(e.getMessage(),e);
    	   logger.error(e.toString(),e);
           return row;
       }
       
   }
   
   public int getQueryRowNum(String sqlResult){
	   if(sqlResult==null){
		   return -1;
	   }else{
		  return sqlResult.split("\n").length;		   
	   }
   }
   
   public void close(){
      try{
          if(rs!=null)
            this.rs.close();
          if(st!=null)
            this.st.close();
          if(conn!=null)
            this.conn.close();
          
      }catch(Exception e){
          System.out.println("ERROR: Mysql close failed"+e.toString());
      }       
   }
   
   public static void main(String[] args) throws SQLException{
	   
	   PostgreSQL pg=new PostgreSQL("210.75.252.140","5432","gisinfo", "postgres", "postgres");
	   
	   System.out.println(pg==null?false:true);  

	   String s=pg.select("show databases ;");
	   System.out.print(s);System.out.print(s);

	   
 	   
   }
}


