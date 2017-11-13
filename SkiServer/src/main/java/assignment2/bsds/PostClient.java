package assignment2.bsds;

import bsdsass2testdata.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.collect.Lists;

public class PostClient {

    public static final int TASK_LIST_SIZE = 200;
    public static final int NUM_THREADS = 100;
    public static final int MS_PER_SEC = 1000;

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//
//        System.out.println("Client starting...time: " + System.currentTimeMillis() / MS_PER_SEC);
//
//        int numThreads = args.length == 1 ? Integer.parseInt(args[0]) : NUM_THREADS;
//        System.out.println("Threads: " + numThreads);
//
//        final String postURL = "http://BsdsDatabase-env-1.us-west-2.elasticbeanstalk.com/webapi/load";
//        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
//
//        // Read every record in the file containing a day of skier data
//        DataReader reader = new DataReader();
//        String filePath = "C:\\Users\\BRF8\\school\\BSDSAssignment2Day3.ser";
//
//        List<RFIDLiftData> dayOneData = reader.readData(filePath);
//
//        // Send each record to the Server's POST method via a PostTask
//        List<PostTask> postTasks = new ArrayList<>();
//
//        // Add .subList(0, 800000) to dayOneData to reduce number of requests
//        for (List<RFIDLiftData> subList : Lists.partition(dayOneData, TASK_LIST_SIZE)) {
//            postTasks.add(new PostTask(subList, postURL));
//        }
//
//        // Execute all tasks
//        System.out.println("All threads running...");
//        long startTime = System.currentTimeMillis();
//        List<Future<TaskResult>> futureResults = exec.invokeAll(postTasks);
//        exec.shutdown();
//        exec.awaitTermination(1800, TimeUnit.SECONDS); // Blocks until all threads terminated
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("All threads complete... time: " + System.currentTimeMillis());
//
//        // Iterate through each Future returned from ExecutorService, and collect results
//        List<TaskResult> results = new ArrayList<>();
//        for (Future<TaskResult> tr : futureResults)
//            results.add(tr.get());
//        StatGenerator stats = new StatGenerator(results);
////        LatencyChart chart = new LatencyChart(results);
////        chart.generateChart("Part4");
//
//        Long wallTime = (endTime - startTime) / MS_PER_SEC;
//        System.out.println("Test Wall time: " + wallTime + " seconds");
//        Long throughput = stats.getNumRequests() / wallTime;
//        System.out.println("Average throughput: " + throughput + " requests per second");
//
//        stats.printStats();
//
//    }

    public static List<TaskResult> singleDayPost(String postURL, String filePath) throws ExecutionException, InterruptedException{
        System.out.println("POST client starting...time: " + System.currentTimeMillis() / MS_PER_SEC);

        System.out.println("Threads: " + NUM_THREADS);

        ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

        // Read every record in the file containing a day of skier data
        DataReader reader = new DataReader();

        List<RFIDLiftData> dayOneData = reader.readData(filePath);

        // Send each record to the Server's POST method via a PostTask
        List<PostTask> postTasks = new ArrayList<>();

        // Add .subList(0, 800000) to dayOneData to reduce number of requests
        for (List<RFIDLiftData> subList : Lists.partition(dayOneData, TASK_LIST_SIZE)) {
            postTasks.add(new PostTask(subList, postURL));
        }

        // Execute all tasks
        System.out.println("All threads running...");
        long startTime = System.currentTimeMillis();
        List<Future<TaskResult>> futureResults = exec.invokeAll(postTasks);
        exec.shutdown();
        exec.awaitTermination(1800, TimeUnit.SECONDS); // Blocks until all threads terminated

        long endTime = System.currentTimeMillis();
        System.out.println("All threads complete... time: " + System.currentTimeMillis());

        // Iterate through each Future returned from ExecutorService, and collect results
        List<TaskResult> results = new ArrayList<>();
        for (Future<TaskResult> tr : futureResults){
            results.add(tr.get());
        }
        StatGenerator stats = new StatGenerator(results);


        Long wallTime = (endTime - startTime) / MS_PER_SEC;
        System.out.println("Test Wall time: " + wallTime + " seconds");
        Long throughput = stats.getNumRequests() / wallTime;
        System.out.println("Average throughput: " + throughput + " requests per second");

        stats.printStats();

        return results;
    }
    
    
}
