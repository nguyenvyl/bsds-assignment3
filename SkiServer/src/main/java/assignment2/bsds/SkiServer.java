package assignment2.bsds;

import com.google.gson.Gson;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import bsdsass2testdata.RFIDLiftData;

@Path("/")
public class SkiServer {

  private static BasicDataSource dataSource = getDataSource();

  private static BasicDataSource getDataSource() {
    if (dataSource == null) {
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl("jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication");
      ds.setUsername("root");
      ds.setPassword("password");
      ds.setDriverClassName("com.mysql.jdbc.Driver");
      ds.setInitialSize(10);
      ds.setMaxTotal(100);
//      ds.setMinIdle(5);
//      ds.setMaxIdle(10);
      dataSource = ds;
    }
    return dataSource;
  }

  //Method handling HTTP GET requests.
  @GET
  @Path("myvert/{skierId}/{dayNum}")
  @Produces(MediaType.TEXT_PLAIN)
  public int getData(@PathParam("skierId") int skierId,
                        @PathParam("dayNum") int dayNum) {

    String query = "SELECT COUNT(skier.LiftID) as Lift_Rides, SUM(height) as TOTAL_VERTICAL " +
        "FROM (SELECT SkierId, LiftID FROM SkierData WHERE Day = " + dayNum + ") skiers " +
        "LEFT JOIN LiftHeights ON skiers.LiftID = LiftHeights.liftId" +
        " GROUP BY SkierId HAVING SkierId = " + skierId;

//    String URL = "jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication";
//    String USERNAME = "root";
//    String PASSWORD = "password";

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int totalVert = 0;

    try {
//      Class.forName("com.mysql.jdbc.Driver");
//      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        totalVert = rs.getInt(1);
      }
      return totalVert;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null)
          stmt.close();
        if(rs != null)
          rs.close();
        if (conn != null)
          conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return totalVert;
    }
  }


  @POST
  @Path("load")
  @Consumes(MediaType.APPLICATION_JSON)
  public int postData(String json) {

//    String URL = "jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication";
//    String USERNAME = "root";
//    String PASSWORD = "password";

    Gson gson = new Gson();
    RFIDLiftData data = gson.fromJson(json, RFIDLiftData.class);

    String query = "INSERT INTO SkierData (ResortId, Day, SkierId, LiftId, Time) VALUES (" +
        + data.getResortID() + ","
        + data.getDayNum() + ","
        + data.getSkierID() + ","
        + data.getLiftID() + ","
        + data.getTime() + ");";

    Connection conn = null;
    //Statement stmt = null;
    PreparedStatement prepStatement = null;

    Integer rs;

    try {
//      Class.forName("com.mysql.jdbc.Driver");
//      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      conn = dataSource.getConnection();
      prepStatement = conn.prepareStatement(query);
      rs = prepStatement.executeUpdate();
      //stmt = conn.createStatement();
      //rs = stmt.executeUpdate(query);
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (prepStatement != null)
          prepStatement.close();
        if (conn != null)
          conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return 0;
    }
  }
}