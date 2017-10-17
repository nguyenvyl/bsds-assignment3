package assignment2.bsds;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
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

  private Connection connection;

  //Method handling HTTP GET requests.
  @GET
  @Path("myvert/{skierId}&{dayNum}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getData(@PathParam("skierId") int skierId,
                        @PathParam("dayNum") int dayNum) {
    return "Skier ID: " + skierId + "  Day: " + dayNum;
  }

  // Method handling HTTP POST requests.
//  @POST
//  @Path("load")
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  public Integer postData(@FormParam("resortId") String resortId,
//                          @FormParam("dayNum") String dayNum,
//                          @FormParam("skierId") String skierId,
//                          @FormParam("liftId") String liftId,
//                          @FormParam("timestamp") String timestamp) {
//
//    String URL = "jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication";
//    String USERNAME = "root";
//    String PASSWORD = "password";
//
//    String query = "INSERT INTO SkierData (ResortId, Day, SkierId, LiftId, Time) VALUES (" +
//        Integer.parseInt(resortId) + ","
//        + Integer.parseInt(dayNum) + ","
//        + Integer.parseInt(skierId) + ","
//        + Integer.parseInt(liftId) + ","
//        + Integer.parseInt(timestamp) + ");";
//    System.out.println("*** ResortId: " + Integer.parseInt(resortId));
//
//    Connection conn = null;
//    Statement stmt = null;
//    Integer rs = null;
//
//    try {
//      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//      stmt = conn.createStatement();
//      rs = stmt.executeUpdate(query);
//      System.out.println("*** Query executed! " + query);
//    } catch (SQLException e) {
//      e.printStackTrace();
//    } finally {
//      try {
//        if (stmt != null)
//          stmt.close();
////        if (rs != null)
////          rs.close();
//        if (conn != null)
//          conn.close();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
//      return 0;
//    }
//  }

  @POST
  @Path("load")
  @Consumes(MediaType.APPLICATION_JSON)
  public Integer postData(String json) {

    String URL = "jdbc:mysql://skidb.c9gtnfpnhpvo.us-west-2.rds.amazonaws.com:3306/SkiApplication";
    String USERNAME = "root";
    String PASSWORD = "password";

    Gson gson = new Gson();
    RFIDLiftData data = gson.fromJson(json, RFIDLiftData.class);

    String query = "INSERT INTO SkierData (ResortId, Day, SkierId, LiftId, Time) VALUES (" +
        + data.getResortID() + ","
        + data.getDayNum() + ","
        + data.getSkierID() + ","
        + data.getLiftID() + ","
        + data.getTime() + ");";

    Connection conn = null;
    Statement stmt = null;
    Integer rs = null;

    try {
      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      stmt = conn.createStatement();
      rs = stmt.executeUpdate(query);
      System.out.println("*** Query executed! " + query);
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null)
          stmt.close();
        if (conn != null)
          conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return 0;
    }
  }
}