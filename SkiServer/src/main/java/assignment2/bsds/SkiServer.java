package assignment2.bsds;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SkiServer {

  //Method handling HTTP GET requests.
  @GET
  @Path("myvert/{skierId}/{dayNum}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getData(@PathParam("skierId") int skierId, @PathParam("dayNum") int dayNum) {
    return "Skier ID: " + skierId + "  Day: " + dayNum;
  }

  // Method handling HTTP POST requests.
  @POST
  @Path("load/{resortId}/{dayNum}/{timestamp}/{skierId}/{liftId}")
  @Consumes(MediaType.TEXT_PLAIN)
  public String postData(@PathParam("resortId") int resortId, @PathParam("dayNum") int dayNum,
                      @PathParam("timestamp") int time, @PathParam("skierId") int skierId,
                      @PathParam("liftId") int liftId)
  {
    return "Resort: " + resortId + "\nDay: " + dayNum + "\nTime: " + time + "\nSkier: " + skierId
        + "\nLift: " + liftId;
  }
}