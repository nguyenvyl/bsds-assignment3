package assignment2.bsds;

import com.google.gson.Gson;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
  private static Map<Integer, Integer> liftToHeight = loadMap();

  private static BasicDataSource getDataSource() {
    if (dataSource == null) {
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl("jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication");
      ds.setUsername("root");
      ds.setPassword("password");
      ds.setDriverClassName("com.mysql.jdbc.Driver");
      ds.setInitialSize(10);
      ds.setMaxTotal(100);
      dataSource = ds;
    }
    return dataSource;
  }

  private static Map<Integer, Integer> loadMap() {

    Map<Integer, Integer> map = new HashMap<>();
    String query = "SELECT * FROM LiftHeights";

    try {
      Connection conn = dataSource.getConnection();
      PreparedStatement prepStatement = conn.prepareStatement(query);
      ResultSet rs = prepStatement.executeQuery();
      while(rs.next()){
        map.put(rs.getInt(1), rs.getInt(2));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return map;
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

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int totalVert = 0;

    try {
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

    Gson gson = new Gson();
    RFIDLiftData data = gson.fromJson(json, RFIDLiftData.class);

    String query = "INSERT INTO SkierData (ResortId, Day, SkierId, LiftId, Time) VALUES (" +
        + data.getResortID() + ","
        + data.getDayNum() + ","
        + data.getSkierID() + ","
        + data.getLiftID() + ","
        + data.getTime() + ");";

    String query2 = "INSERT INTO SkierStats (SkierId, DayNum, NumLifts, TotalVert) " +
        "VALUES(" + data.getSkierID() + "," + data.getDayNum() + "," + 1 + "," +
        liftToHeight.get(data.getLiftID()) + ") ON DUPLICATE KEY UPDATE NumLifts=NumLifts + 1, " +
        "TotalVert = TotalVert + " + liftToHeight.get(data.getLiftID()) + ";";

    Connection conn = null;
    PreparedStatement prepStatement = null;
    PreparedStatement prepStatement2 = null;

    Integer rs;

    try {

      conn = dataSource.getConnection();
      prepStatement = conn.prepareStatement(query);
      prepStatement2 = conn.prepareStatement(query2);
      rs = prepStatement.executeUpdate();
      prepStatement2.executeUpdate();
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (prepStatement != null)
          prepStatement.close();
        if(prepStatement2 != null)
          prepStatement2.close();
        if (conn != null)
          conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return 0;
    }
  }
}