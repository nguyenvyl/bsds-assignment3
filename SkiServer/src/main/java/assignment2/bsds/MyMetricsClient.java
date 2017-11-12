package assignment2.bsds;

import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

public class MyMetricsClient {
    
    private static final List<String> urls = new ArrayList<>();
    private static final List<RequestMetrics> allMetrics = new ArrayList<>();
    private static Map<String, List<RequestMetrics>> instanceMetrics = new HashMap<>();
    

    public static void main(String[] args) {
        urls.add("http://ec2-54-245-2-13.us-west-2.compute.amazonaws.com/webapi/metrics");
        urls.add("http://ec2-34-209-27-219.us-west-2.compute.amazonaws.com/webapi/metrics");
        urls.add("http://ec2-34-210-70-72.us-west-2.compute.amazonaws.com/webapi/metrics");
        
        // Get list of server metrics from each instance
        for(String url : urls){
            List<RequestMetrics> metrics = callGET(url);
            instanceMetrics.put(url, metrics);
            allMetrics.addAll(metrics);
        }

        System.out.println("Metrics by instance: ");
        for(String url : instanceMetrics.keySet()) {
            System.out.println("For instance URL " + url + ":");
            calculateAllMetrics(instanceMetrics.get(url));
        }

        System.out.println("Metrics across all instances: ");
        calculateAllMetrics(allMetrics);

    }

    // Retrieves the list of server metrics from an instance
    public static List<RequestMetrics> callGET(String url) {
        List<RequestMetrics> metrics = new ArrayList<>();
        Client client = ClientBuilder.newClient();
        try {
            metrics = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<RequestMetrics>>(){});
        }  catch (ProcessingException e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Stack trace: " + Arrays.toString(e.getStackTrace()));
        } catch (OutOfMemoryError e) {
            System.out.println("You don't have enough memory");
        } catch (Exception e) {
            return metrics;
        }
        return metrics;
    }

    // Calculates all stats for a list ot metrics by POST request, GET request, and combined GET and POST
    private static void calculateAllMetrics(List<RequestMetrics> list) {
        Map<String, List<RequestMetrics>> sortedMetrics = separateGETandPOST(list);
        System.out.println("Metrics for all POST requests: ");
        System.out.println("Number of POST requests received: " + sortedMetrics.get("POST").size());
        calculateMetrics(sortedMetrics.get("POST"));
        System.out.println("Metrics for all GET requests: ");
        System.out.println("Number of GET requests received: " + sortedMetrics.get("GET").size());
        calculateMetrics(sortedMetrics.get("GET"));
        System.out.println("Combined POST and GET Metrics: ");
        System.out.println("Total requests received: " + (sortedMetrics.get("GET").size() + sortedMetrics.get("POST").size()));
        calculateMetrics(list);
    }

    // Gets the average, median, 95th, and 99th percentile of a list of metrics.
    private static void calculateMetrics(List<RequestMetrics> list) {
        System.out.println("Average Response Time: " + getAverageResponseTime(list) + " milliseconds");
        System.out.println("Average DB Query Time: " + getAverageDBTime(list) + " milliseconds");
        sortByResponseTime(list);
        System.out.println("Median Response Time: " + getResponseMedian(list) + " milliseconds");
        System.out.println("99th Percentile Response Time: " + list.get((int) (list.size() * 0.99)).getResponseTime() + " milliseconds");
        System.out.println("95th Percentile Response Time: " + list.get((int) (list.size() * 0.95)).getResponseTime() + " milliseconds");
        sortByDBTime(list);
        System.out.println("Median DB Query Time: " + getDBMedian(list) + " milliseconds");
        System.out.println("99th Percentile DB Query Time: " + list.get((int) (list.size() * 0.99)).getDbQueryTime() + " milliseconds");
        System.out.println("95th Percentile DB Query Time: " + list.get((int) (list.size() * 0.95)).getDbQueryTime() + " milliseconds");
    }

    private static Double getAverageResponseTime(List<RequestMetrics> sortedList) {
        return sortedList.stream().mapToDouble(RequestMetrics::getResponseTime).average().getAsDouble();
    }

    private static Double getAverageDBTime(List<RequestMetrics> sortedList) {
        return sortedList.stream().mapToDouble(RequestMetrics::getDbQueryTime).average().getAsDouble();
    }

    private static Double getResponseMedian(List<RequestMetrics> sortedList) {
        if (sortedList.size() % 2 == 0) {
            return (sortedList.get(sortedList.size() / 2 - 1).getResponseTime() + sortedList.get(sortedList.size() / 2).getResponseTime()) / 2.0;
        }
        return sortedList.get(sortedList.size() / 2).getResponseTime();
    }

    private static Double getDBMedian(List<RequestMetrics> sortedList) {
        if (sortedList.size() % 2 == 0) {
            return (sortedList.get(sortedList.size() / 2 - 1).getDbQueryTime() + sortedList.get(sortedList.size() / 2).getDbQueryTime()) / 2.0;
        }
        return sortedList.get(sortedList.size() / 2).getDbQueryTime();
    }


    private static void sortByResponseTime(List<RequestMetrics> metricsList) {
        Collections.sort(metricsList, (o1, o2) -> {
            Double result = o1.getResponseTime() - o2.getResponseTime();
            return result.intValue();
        });
    }

    private static void sortByDBTime(List<RequestMetrics> metricsList) {
        Collections.sort(metricsList, (o1, o2) -> {
            Double result = o1.getDbQueryTime() - o2.getDbQueryTime();
            return result.intValue();
        });
    }

    // Separates a list of metrics by request type, either GET or POST.
    private static Map<String, List<RequestMetrics>> separateGETandPOST(List<RequestMetrics> metricsList) {
        List<RequestMetrics> getMetrics = new ArrayList<>();
        List<RequestMetrics> postMetrics = new ArrayList<>();
        for(RequestMetrics metric : metricsList) {
            if(metric.getRequestType().equals("GET")) {
                getMetrics.add(metric);
            } else {
                postMetrics.add(metric);
            }
        }
        Map<String, List<RequestMetrics>> metrics = new HashMap<>();
        metrics.put("GET", getMetrics);
        metrics.put("POST", postMetrics);
        return metrics;
    }

}
