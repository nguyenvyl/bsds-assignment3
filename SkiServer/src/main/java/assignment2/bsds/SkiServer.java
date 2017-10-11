package assignment2.bsds;

import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SkiServer {

  //Method handling HTTP GET requests.
  @GET
  @Path("myvert/{skierId}&{dayNum}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getData(@PathParam("skierId") int skierId,
                        @PathParam("dayNum") int dayNum)
  {
    return "Skier ID: " + skierId + "  Day: " + dayNum;
  }

  // Method handling HTTP POST requests.
  @POST
  @Path("load")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public String postData(@FormParam("resortId") int resortId,
                         @FormParam("dayNum") int dayNum,
                         @FormParam("skierId") int skierId,
                         @FormParam("liftId") int liftId,
                         @FormParam("timestamp") int timestamp)
  {
    return "Wow new row of data! " + resortId + " " + dayNum + " " + timestamp + " " + skierId + " " + liftId;
  }
}