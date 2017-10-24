package assignment2.bsds;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by irenakushner on 10/17/17.
 */

public class GetTask implements Callable<TaskResult> {

  private WebTarget webTarget;
  private TaskResult result;

  public GetTask(WebTarget webTarget) {
    this.webTarget = webTarget;
    this.result = new TaskResult();
  }

  private void makeGETRequest() {

    Response response = null;
    long start = System.currentTimeMillis();
    Long timeBucket = null;

    try {
      response = webTarget.request().get();
      response.close();
      Calendar calendar = Calendar.getInstance();
      Date timestamp = new Timestamp(calendar.getTime().getTime()); // in milliseconds
      timeBucket = timestamp.getTime() / 1000; // divide by 1000 to get one-second time bucket
    } catch (Exception e) {
      System.err.println("Problem making GET request");
    }
    long end = System.currentTimeMillis();
    result.incrementRequest();

    if (response != null && response.getStatus() == 200) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency); // TODO: replace this with latency mapping
      result.addLatencyMapping(timeBucket.intValue(), latency);
    }
  }

  @Override
  public TaskResult call() throws Exception {
    makeGETRequest();
    return this.result;
  }
}

