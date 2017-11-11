package assignment3.bsds.util;

import assignment3.bsds.client.TaskResult;

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
    Double idx = Math.ceil(percentile * numSuccesses / 100) - 1;
    int index = idx.intValue();
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

  public void printStats() {
    System.out.println("Total number of requests sent: " + getNumRequests());
    System.out.println("Total number of successful responses: " + getNumSuccesses());

    System.out.println("Mean latency: " + mean() + " milliseconds");
    System.out.println("Median latency: " + median() + " milliseconds");
    System.out.println("99th percentile latency: " + latencyPercentile(99) + " milliseconds");
    System.out.println("95th percentile latency: " + latencyPercentile(95) + " milliseconds");
  }
}