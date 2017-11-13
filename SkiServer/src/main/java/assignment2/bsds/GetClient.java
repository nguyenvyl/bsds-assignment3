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
import org.glassfish.jersey.client.ClientProperties;


public class GetClient {

  public static final int NUM_THREADS = 200;
  public static final int NUM_SKIERS = 40000;
  public static final int MS_PER_SEC = 1000;
  
    public static List<TaskResult> singleDayGet(String url, String day) throws ExecutionException, InterruptedException {

    System.out.println("GET client starting for day " + day + " ...time: " + System.currentTimeMillis() / MS_PER_SEC);

    Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, 120000);
    client.property(ClientProperties.READ_TIMEOUT, 120000);
    ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

    // Send each record to the Server's POST method via a PostTask
    List<GetTask> getTasks = new ArrayList<>();

    for(int skierId = 1; skierId < NUM_SKIERS + 1; skierId++) {
      String getUrl = url + skierId + "/" + day; // for now, we only have day 1
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

    Long wallTime = (endTime - startTime) / 1000;
    System.out.println("Test Wall time: " + wallTime + " seconds");
    Long throughput = stats.getNumRequests() / wallTime;    
    System.out.println("Average throughput: " + throughput + " requests per second");
    stats.printStats();
    return results;
  }
  
  
  
}
