package assignment2.bsds;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import bsdsass2testdata.RFIDLiftData;

/**
 * A task which can be passed to an Executor object
 * When a task is called, it makes a specified number of HTTP requests and stores the results of these requests.
 */
public class PostTask implements Callable<TaskResult> {

  private List<RFIDLiftData> dataList;
  private Client client;
  private WebTarget webTarget;
  private TaskResult result;

  public PostTask(List<RFIDLiftData> dataList, String url) {
    this.dataList = dataList;
    this.client = ClientBuilder.newClient();
    this.webTarget = client.target(url);
    this.result = new TaskResult();
  }

  private void makePostRequest(RFIDLiftData data) {

    Response response = null;
    long start = System.currentTimeMillis();
    Long timeBucket = null;

    Form form = new Form();
    form.param("resortId", Integer.toString(data.getResortID()));
    form.param("dayNum", Integer.toString(data.getDayNum()));
    form.param("skierId", Integer.toString(data.getSkierID()));
    form.param("liftId", Integer.toString(data.getLiftID()));
    form.param("timestamp", Integer.toString(data.getTime()));

    try {
      response = webTarget.request().post(Entity.form(form));
      response.close();
      Calendar calendar = Calendar.getInstance();
      Date timestamp = new Timestamp(calendar.getTime().getTime()); // in milliseconds
      timeBucket = timestamp.getTime() / 10; // divide by 1000 to get one-second time bucket
    }
    catch (Exception e) {
      System.err.println("Problem making Post request");
    }
    long end = System.currentTimeMillis();
    result.incrementRequest();

    if(response != null && response.getStatus() == 200) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency); // TODO: replace this with latency mapping
      result.addLatencyMapping(timeBucket.intValue(), latency);
    }
  }

  @Override
  public TaskResult call() throws Exception {

    for(RFIDLiftData data : dataList){
      makePostRequest(data);
    }

    client.close();
    return this.result;
  }
}
