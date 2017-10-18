package assignment2.bsds;

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


/**
 * Created by irenakushner on 10/17/17.
 */
public class GetClient {

  public static final int N_THREADS = 100;

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    System.out.println("Client starting...time: " + System.currentTimeMillis() / 1000);

    final String baseURL = "http://localhost:8080/rest/myvert/";

    Client client = ClientBuilder.newClient();

    ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);

    // Send each record to the Server's POST method via a PostTask
    List<GetTask> getTasks = new ArrayList<>();

    for(int skierId = 0; skierId < 40000; skierId++) {
      String getUrl = baseURL + skierId + "/1"; // for now, we only have day 1
      WebTarget postTarget = client.target(getUrl);
      getTasks.add(new GetTask(postTarget));
    }

    // Execute all tasks
    System.out.println("All threads running...");
    long startTime = System.currentTimeMillis();
    List<Future<TaskResult>> futureResults = exec.invokeAll(getTasks);
    exec.shutdown();
    exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // Blocks until all threads terminated

    client.close();
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
