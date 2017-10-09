package assignment2.bsds;

import java.util.ArrayList;
import java.util.List;

/**
 * Threadsafe class that holds the results of completing a Task
 */
public class TaskResult {

  private int requestCount;
  private int successCount;
  private List<Integer> latencies;

  public TaskResult() {
    this.requestCount = 0;
    this.successCount = 0;
    this.latencies = new ArrayList<>();
  }

  public synchronized void incrementRequest() {
    requestCount++;
  }

  public synchronized void incrementSuccess() {
    successCount++;
  }

  public synchronized void addLatency(int time) {
    latencies.add(time);
  }

  public synchronized int getRequestCount() {
    return requestCount;
  }

  public synchronized int getSuccessCount() {
    return successCount;
  }

  public synchronized List<Integer> getLatencies() {
    return latencies;
  }

  public synchronized Integer totalLatency () {
    int total = 0;
    for (Integer time : latencies)
      total += time;
    return total;
  }
}
