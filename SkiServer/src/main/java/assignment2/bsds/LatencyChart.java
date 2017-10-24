package assignment2.bsds;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Class to generate a chart of latencies vs. time for timestamped requests
 */
public class LatencyChart {

  private List<TaskResult> tasks;

  public LatencyChart(List<TaskResult> tasks) {
    this.tasks = tasks;
  }

  public void generateChart(String title) {

    XYSeries latencies = new XYSeries("Average Latency");

    Map<Integer, List<Integer>> mapping = null; // TODO: intellij complained about not being initialized

    // iterate through map keys, calculate average latency for each key
    // add key, average latency to series
    for (TaskResult task : tasks) {
      mapping = task.getLatencyMap();
      for (Integer timeBucket : mapping.keySet())
        latencies.add(timeBucket.doubleValue(), average(mapping.get(timeBucket)));
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(latencies);

    JFreeChart chart = ChartFactory.createXYLineChart("Average Latencies over Time",
        "Timestamp",
        "Average Latency (ms)",
        dataset,
        PlotOrientation.VERTICAL,
        true,
        true,
        false);

    try {
      String chartPath = "/Users/irenakushner/Documents/Northeastern/CS 6650/Assignment 2/" + title + ".JPEG";
      ChartUtilities.saveChartAsJPEG(new File(chartPath), chart, 500, 300);
    } catch (IOException e) {
      System.err.println("Error in chart generation " + e);
    }
  }

  private double average(List<Integer> input) {
    int sum = 0;
    for(Integer i : input) {
      sum += i;
    }
    return sum / input.size();
  }

}