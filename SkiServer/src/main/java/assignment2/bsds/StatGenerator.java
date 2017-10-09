package assignment2.bsds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to generate statistics about requests and latencies from a List of task results
 */
public class StatGenerator {

  private List<TaskResult> results;
  private int numSuccesses;
  private int numRequests;
  private int totalLatency;

  public StatGenerator(List<TaskResult> results) {
    this.results = results;
    for (TaskResult result : results) {
      numSuccesses += result.getSuccessCount();
      numRequests += result.getRequestCount();
      totalLatency += result.totalLatency();
    }
  }

  public int getNumSuccesses() {
    return numSuccesses;
  }

  public int getNumRequests() {
    return numRequests;
  }

  public int latencyPercentile(int percentile) {
    int index = new Double( Math.ceil(percentile * numSuccesses / 100)).intValue() - 1;
    List<Integer> latencies = getLatencies();
    Collections.sort(latencies);
    return latencies.get(index);
  }

  public int median() {
    return latencyPercentile(50);
  }

  public int mean() {
    return totalLatency/numSuccesses;
  }

  // Extracts and combines all latency times from each TaskResult object
  private List<Integer> getLatencies() {
    List<Integer> allTimes = new ArrayList<>();
    for(TaskResult result : results)
      allTimes.addAll(result.getLatencies());
    return allTimes;
  }
}