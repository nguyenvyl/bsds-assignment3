package assignment2.bsds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Threadsafe class that holds the results of completing a Task
 */
public class TaskResult {

  private int requestCount;
  private int successCount;
  private List<Integer> latencies;
  // TODO: Map timestamp to latency
  private Map<Integer, List<Integer>> timeToLatencies;

  public TaskResult() {
    this.requestCount = 0;
    this.successCount = 0;
    this.latencies = new ArrayList<>();
    this.timeToLatencies = new HashMap<>();
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

  public synchronized void addLatencyMapping(Integer time, Integer latency) {
    if(timeToLatencies.containsKey(time)) {
      List<Integer> currentList = timeToLatencies.get(time);
      currentList.add(latency);
      timeToLatencies.put(time, currentList);
    }
    else {
      List<Integer> newlist = new ArrayList<>();
      newlist.add(latency);
      timeToLatencies.put(time, newlist);
    }
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

  public Map<Integer, List<Integer>> getLatencyMap() {
    return timeToLatencies;
  }

  public synchronized Integer totalLatency () {
    int total = 0;
    for (Integer time : latencies)
      total += time;
    return total;
  }
}
