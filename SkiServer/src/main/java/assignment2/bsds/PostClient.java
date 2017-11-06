package assignment2.bsds;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import bsdsass2testdata.RFIDLiftData;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Created by irenakushner on 10/8/17.
 */
public class PostClient {

  public static final int TASK_LIST_SIZE = 100;
  public static final int NUM_THREADS = 400;
  public static final int MS_PER_SEC = 1000;

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    System.out.println("Client starting...time: " + System.currentTimeMillis() / MS_PER_SEC);

    int numThreads = args.length == 1 ? Integer.parseInt(args[0]) : NUM_THREADS;
    System.out.println("Threads: " + numThreads);

    //final String postURL = "http://ec2-52-32-88-162.us-west-2.compute.amazonaws.com:8000/SkiServer_war/rest/load/";
    final String postURL = "http://bsdsdatabase-env-1.pk8kay72jp.us-west-2.elasticbeanstalk.com/load";

    ExecutorService exec = Executors.newFixedThreadPool(numThreads);

    // Read every record in the file containing a day of skier data
    DataReader reader = new DataReader();
    List<RFIDLiftData> dayOneData = reader.readData();

    // Send each record to the Server's POST method via a PostTask
    List<PostTask> postTasks = new ArrayList<>();

    Gson gson = new Gson();

    for (List<RFIDLiftData> subList : Lists.partition(dayOneData.subList(0, 100000), TASK_LIST_SIZE)) {
      List<String> dayOneJsons = new ArrayList<>();
      for (RFIDLiftData liftdata : subList) {
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
    exec.awaitTermination(1800, TimeUnit.SECONDS); // Blocks until all threads terminated

    long endTime = System.currentTimeMillis();
    System.out.println("All threads complete... time: " + System.currentTimeMillis());

    // Iterate through each Future returned from ExecutorService, and collect results
    List<TaskResult> results = new ArrayList<>();
    for (Future<TaskResult> tr : futureResults)
      results.add(tr.get());
    StatGenerator stats = new StatGenerator(results);
    LatencyChart chart = new LatencyChart(results);
    chart.generateChart("Part4");

    System.out.println("Test Wall time: " + (endTime - startTime) / MS_PER_SEC + " seconds");
    stats.printStats();

  }
}
