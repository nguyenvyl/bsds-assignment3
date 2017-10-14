package assignment2.bsds;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

/**
 * Class to generate a chart of latencies vs. time for timestamped requests
 */
public class LatencyChart {

  public void generateChart() {
    // TODO: XYSeries for mean, median, 99th percentile
    // X-axis = time, Y-axis = latency (ms)

    XYSeries test = new XYSeries("Test Chart");
    test.add(1, 1);
    test.add(1, 2);
    test.add(2, 2);
    test.add(4, 3);

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(test);

    JFreeChart chart = ChartFactory.createXYLineChart("XY Chart",
        "x axis",
        "y-axis",
        dataset,
        PlotOrientation.VERTICAL,
        true,
        true,
        false);

    try {
      String chartPath = "/Users/irenakushner/Documents/Northeastern/CS 6650/Assignment 2/testChart.JPEG";
      ChartUtilities.saveChartAsJPEG(new File("chartPath"), chart, 500, 300);
    } catch (IOException e) {
      System.err.println("Error in chart generation " + e);
    }
  }

  public static void main(String[] args) {

    XYSeries test = new XYSeries("Test Chart");
    test.add(1, 1);
    test.add(1, 2);
    test.add(2, 2);
    test.add(4, 3);

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(test);

    JFreeChart chart = ChartFactory.createXYLineChart("XY Chart",
        "x axis",
        "y-axis",
        dataset,
        PlotOrientation.VERTICAL,
        true,
        true,
        false);

    try {
      ChartUtilities.saveChartAsJPEG(new File("/Users/irenakushner/Desktop/testChart.JPEG"), chart, 500, 300);
    } catch (IOException e) {
      System.err.println("Error in chart generation " + e);
    }
  }
}