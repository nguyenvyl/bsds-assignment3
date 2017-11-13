//package assignment3.bsds;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Class to generate a chart of latencies vs. time for timestamped requests
// */
//public class LatencyChart {
//
//  private List<TaskResult> tasks;
//
//  public LatencyChart(List<TaskResult> tasks) {
//    this.tasks = tasks;
//  }
//
//  public void generateChart(String title) {
//
//    XYSeries coordinates = new XYSeries("Average Latency");
//
//    Map<Integer, List<Integer>> mapping = combineLatencyMappings();
//    for(Integer time : mapping.keySet()){
//      coordinates.add(time, Double.valueOf(average(mapping.get(time))));
//    }
//
//    XYSeriesCollection dataset = new XYSeriesCollection();
//    dataset.addSeries(coordinates);
//
//    JFreeChart chart = ChartFactory.createXYLineChart("Average Latencies over Time",
//        "Timestamp",
//        "Average Latency (ms)",
//        dataset,
//        PlotOrientation.VERTICAL,
//        true,
//        true,
//        false);
//
//    try {
//      String chartPath = "C:\\Users\\BRF8\\IdeaProjects\\school\\BSDS_Assignment3\\" + title + ".JPEG";
//      ChartUtilities.saveChartAsJPEG(new File(chartPath), chart, 500, 300);
//    } catch (IOException e) {
//      System.err.println("Error in chart generation " + e);
//    }
//  }
//
//  private Map<Integer,List<Integer>> combineLatencyMappings() {
//    Map<Integer, List<Integer>> bucketsToLatencies = new HashMap<>();
//
//    // iterate through map keys, add all latencies to corresponding bucket
//    for (TaskResult task : tasks) {
//      Map<Integer, List<Integer>> taskMap = task.getLatencyMap(); // time, list<latency>
//      for (Integer timeBucket : taskMap.keySet()){
//        List<Integer> l = taskMap.get(timeBucket);
//        if(bucketsToLatencies.containsKey(timeBucket)){
//          List<Integer> newList = bucketsToLatencies.get(timeBucket);
//          newList.addAll(l);
//          bucketsToLatencies.put(timeBucket, newList);
//        }
//        else {
//          bucketsToLatencies.put(timeBucket, l);
//        }
//      }
//    }
//    return bucketsToLatencies;
//  }
//
//  private double average(List<Integer> input) {
//    int sum = 0;
//    for(Integer i : input) {
//      sum += i;
//    }
//    return sum / input.size();
//  }
//
//}