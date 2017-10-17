package assignment2.bsds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.FormParam;

/**
 * Created by irenakushner on 10/15/17.
 */
public class DBTest {

  public Integer postData(@FormParam("resortId") int resortId,
                          @FormParam("dayNum") int dayNum,
                          @FormParam("skierId") int skierId,
                          @FormParam("liftId") int liftId,
                          @FormParam("timestamp") int timestamp) {

    //String URL = "skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com";
    String URL = "jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication";
    String USERNAME = "root";
    String PASSWORD = "password";

    String query = "INSERT INTO SkierData (ResortId, Day, SkierId, LiftId, Time) VALUES (" +
        resortId + "," + dayNum + "," + skierId + "," + liftId + "," + timestamp + ");";

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);

      if (rs.next()) {
        System.out.println(rs.getString(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally

    {
      try {
        if (stmt != null)
          stmt.close();
        if (rs != null)
          rs.close();
        if (conn != null)
          conn.close();
      } catch (Exception e) {
        e.printStackTrace();

      }
    }

    return 0;
  }
}
