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

  public static final int NUM_THREADS = 200;
  public static final int NUM_SKIERS = 40000;
  public static final int MS_PER_SEC = 1000;

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    System.out.println("Client starting...time: " + System.currentTimeMillis() / MS_PER_SEC);
    int numThreads = args.length == 1 ? Integer.parseInt(args[0]) : NUM_THREADS;

    final String baseURL = "http://bsdsdatabase-env-1.pk8kay72jp.us-west-2.elasticbeanstalk.com/myvert";

    Client client = ClientBuilder.newClient();
    ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

    // Send each record to the Server's POST method via a PostTask
    List<GetTask> getTasks = new ArrayList<>();

    for(int skierId = 1; skierId < NUM_SKIERS + 1; skierId++) {
      String getUrl = baseURL + skierId + "/1"; // for now, we only have day 1
      WebTarget getTarget = client.target(getUrl);
      getTasks.add(new GetTask(getTarget));
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
    chart.generateChart("Part5");

    System.out.println("Test Wall time: " + (endTime - startTime) / MS_PER_SEC + " seconds");
    stats.printStats();
  }
}
