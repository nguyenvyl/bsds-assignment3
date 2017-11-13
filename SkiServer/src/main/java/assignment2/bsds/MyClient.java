package assignment2.bsds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientProperties;

public class MyClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
       
        System.out.println("Main client starting...time: " + System.currentTimeMillis() / 1000);

        final String getURL = "http://BsdsDatabase-env-1.us-west-2.elasticbeanstalk.com/webapi/myvert/";
        final String postURL = "http://BsdsDatabase-env-1.us-west-2.elasticbeanstalk.com/webapi/load/";
        Map<String, String> dayFiles = new HashMap<>();
        dayFiles.put("3", "C:\\Users\\BRF8\\school\\BSDSAssignment2Day3.ser");
        dayFiles.put("4", "C:\\Users\\BRF8\\school\\BSDSAssignment2Day4.ser");
        dayFiles.put("5", "C:\\Users\\BRF8\\school\\BSDSAssignment2Day5.ser");
//        dayFiles.put("1", "C:\\Users\\BRF8\\school\\BSDSAssignment2Day1.ser");


        List<TaskResult> postResults = new ArrayList<>();
        List<TaskResult> getResults = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        for (Map.Entry<String, String> entry : dayFiles.entrySet()) {
            postResults.addAll(PostClient.singleDayPost(postURL, entry.getValue()));
            System.out.println("Posts done for day " + entry.getKey());
            getResults.addAll(GetClient.singleDayGet(getURL, entry.getKey()));
            System.out.println("Gets done for day " + entry.getKey());
        }
        Long wallTime = (System.currentTimeMillis() - startTime) / 1000;
        StatGenerator postStats = new StatGenerator(postResults);
        StatGenerator getStats = new StatGenerator(getResults);
        
        System.out.println("All days done posting and getting!");
        System.out.println("Wall time: " + wallTime.toString());
        System.out.println("Overall results for all POST requests:");
        postStats.printStats();
        System.out.println("Overall results for all GET requests:");
        getStats.printStats();

    }

}
