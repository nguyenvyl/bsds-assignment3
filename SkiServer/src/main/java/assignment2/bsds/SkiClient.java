package assignment2.bsds;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import bsdsass2testdata.RFIDLiftData;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Created by irenakushner on 10/8/17.
 */
public class SkiClient {

  public static final int TASK_LIST_SIZE = 100;

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    System.out.println("Client starting...time: " + System.currentTimeMillis() / 1000);

    // Commandline can specify number of threads; otherwise default is used
    int numThreads = args.length == 1 ? Integer.parseInt(args[0]) : 100;
    System.out.println("Threads: " + numThreads);
    //final String postURL = "http://ec2-34-215-21-235.us-west-2.compute.amazonaws.com:8000/SkiServer_war/rest/load/";
    final String postURL = "http://localhost:8080/rest/load";

    //Client client = ClientBuilder.newClient();
    //WebTarget postTarget = client.target(postURL);

    ExecutorService exec = Executors.newFixedThreadPool(numThreads);

    // Read every record in the file containing a day of skier data
    DataReader reader = new DataReader();
    List<RFIDLiftData> dayOneData = reader.readData();
    List<String> dayOneJsons = new ArrayList<>();

    // Send each record to the Server's POST method via a PostTask
    List<PostTask> postTasks = new ArrayList<>();

    Gson gson = new Gson();

//    for (int i = 0; i < 100; i++) {
//      String json = gson.toJson(dayOneData.subList(i, i+1).get(0));
//      dayOneJsons.add(json);
//    }
//    postTasks.add(new PostTask(dayOneJsons, postTarget));

    for(List<RFIDLiftData> subList : Lists.partition(dayOneData, TASK_LIST_SIZE)) {

      for(RFIDLiftData liftdata : subList) {
        String json = gson.toJson(liftdata);
        dayOneJsons.add(json);
      }
      postTasks.add(new PostTask(dayOneJsons, postURL));
    }

    // Execute all tasks
    System.out.println("All threads running...");
    long startTime = System.currentTimeMillis();
    List<Future<TaskResult>> futureResults = exec.invokeAll(postTasks);
    exec.shutdown();
    exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // Blocks until all threads terminated

    //client.close();
    long endTime = System.currentTimeMillis();
    System.out.println("All threads complete... time: " + System.currentTimeMillis());

    // Iterate through each Future returned from ExecutorService, and collect results
    List<TaskResult> results = new ArrayList<>();
    for (Future<TaskResult> tr : futureResults)
      results.add(tr.get());
    StatGenerator stats = new StatGenerator(results);
    LatencyChart chart = new LatencyChart(results);
    chart.generateChart();

    System.out.println("Total number of requests sent: " + stats.getNumRequests());
    System.out.println("Total number of successful responses: " + stats.getNumSuccesses());
    System.out.println("Test Wall time: " + (endTime - startTime) / 1000 + " seconds");

    System.out.println("Mean latency: " + stats.mean() + " milliseconds");
    System.out.println("Median latency: " + stats.median() + " milliseconds");
    System.out.println("100th percentile latency: " + stats.latencyPercentile(100) + " milliseconds");
    System.out.println("99th percentile latency: " + stats.latencyPercentile(99) + " milliseconds");
    System.out.println("95th percentile latency: " + stats.latencyPercentile(95) + " milliseconds");

  }
}
