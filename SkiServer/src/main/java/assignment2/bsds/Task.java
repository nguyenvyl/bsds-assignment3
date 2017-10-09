package assignment2.bsds;


import java.util.concurrent.Callable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A task which can be passed to an Executor object.
 * When a task is called, it makes a specified number of HTTP requests and stores the results of these requests.
 */
public class Task implements Callable<TaskResult> {

  private int numIterations;
  private String serverURL;
  private Client client;
  private WebTarget webTarget;
  private TaskResult result;

  public Task(int numIterations, String ip, int port) {
    this.numIterations = numIterations;
    this.serverURL =
        ip.equals("http://localhost") ? ip + ":" + Integer.toString(port) + "/rest/myresource"
            : ip + ":" + Integer.toString(port) + "/Server3_war/rest/myresource";
    this.client = ClientBuilder.newClient();
    this.webTarget = client.target(serverURL);
    this.result = new TaskResult();
  }

  private void makeGetRequest() {

    Response response = null;
    String output = null;
    long start = System.currentTimeMillis();

    try {
      response = webTarget.request(MediaType.TEXT_PLAIN).get();
      output = response.readEntity(String.class);
      response.close();
    }
    catch (Exception e) {
      System.out.println("Problem making GET request");
    }

    long end = System.currentTimeMillis();
    result.incrementRequest();

    if(response != null && response.getStatus() == 200 && output.equals("Got it!")) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency);
    }
  }

  private void makePostRequest() {

    Response response = null;
    long start = System.currentTimeMillis();
    Integer output = null;

    try {
      response = webTarget.request().post(Entity.text("abcd"));
      output = response.readEntity(Integer.class);
      response.close();
    }
    catch (Exception e) {
      System.out.println("Problem making Post request");
    }
    long end = System.currentTimeMillis();
    result.incrementRequest();

    if(response != null && response.getStatus() == 200 && output == 4) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency);
    }
  }

  @Override
  public TaskResult call() throws Exception {

    for (int i = 0; i < numIterations; i++) {
      makeGetRequest();
      makePostRequest();
    }

    client.close();
    return this.result;
  }
}

