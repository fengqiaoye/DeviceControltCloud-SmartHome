/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午2:34:49 
 */
package cooxm.devicecontrol.util;

import java.sql.*;

import org.apache.log4j.Logger;

public class MySqlClass {
   
   public Connection conn=null;
   private Statement st=null;
   private ResultSet rs=null;
   static final Logger logger = Logger.getLogger(MySqlClass.class);
   
   public MySqlClass(String host, String port,String databaseName,String userName,String password){
       try{
           Class.forName("com.mysql.jdbc.Driver").newInstance();
	       //conn=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+databaseName,userName,password);
	       conn=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+databaseName+"?useUnicode=true&characterEncoding=utf8",userName,password);
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
           return result;
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
	   
	   MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_main", "root", "cooxm");
	   
	   System.out.println(mysql==null?false:true);  
	   
       //mysql=new MySqlClass("172.20.36.247","3306","realTimeTraffic", "ghchen", "ghchen");
	   //String s=mysql.select("show databases;");
	   //String ss=mysql.select("select * from HIS_Mobile_Operation_1507;");
	   //System.out.println(ss);
	   
 	   
   }
}

