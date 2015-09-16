package cooxm.devicecontrol.util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Sep 6, 2015 3:26:28 PM 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class cassandra2 {
  public static void main(String[] a) {
    try {
      Class.forName("org.bigsql.cassandra2.jdbc.CassandraDriver");
      Connection con = DriverManager
          .getConnection("jdbc:cassandra://120.24.81.226:9042/system_auth");

      String query = "select * from demo.users";

      Statement statement = con.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        System.out.print(rs.getString(1) + ":" + rs.getString(2) + "\t"
            + rs.getString(3) + "\t" + rs.getString(4) + "\t"
            + rs.getString(5) + "\t" + rs.getString(6) + "\t"
            + "\n");
      }

      rs.close();
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}