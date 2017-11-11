package assignment3.bsds.client;

import assignment3.bsds.model.RFIDLiftData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A task which can be passed to an Executor object
 * When a task is called, it makes a specified number of HTTP requests and stores the results of these requests.
 */
public class PostTask implements Callable<TaskResult> {

    private List<RFIDLiftData> dataList;
    private WebTarget webTarget;
    private TaskResult result;
    private Client client;
    private static final int BATCH_SIZE = 50;

    public PostTask(List<RFIDLiftData> dataList, String url) {
        this.client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
        this.dataList = dataList;
        this.result = new TaskResult();
    }

    private void makePostRequest(List<RFIDLiftData> data) {

        Response response = null;
        long start = System.currentTimeMillis();
        Long timeBucket = null;

        try {
            response = webTarget.request().post(Entity.entity(data, MediaType.APPLICATION_JSON));
            response.close();
            Calendar calendar = Calendar.getInstance();
            Date timestamp = new Timestamp(calendar.getTime().getTime()); // in milliseconds
            timeBucket = timestamp.getTime() / 1000; // divide by 1000 to get one-second time bucket
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        result.incrementRequest();

        if(response != null && response.getStatus() == 200) {
            result.incrementSuccess();
            int latency = (int) (end - start);
            result.addLatency(latency); // TODO: replace this with latency mapping
            result.addLatencyMapping(timeBucket.intValue(), latency);
        }
    }

    @Override
    public TaskResult call() throws Exception {

//        for(List<RFIDLiftData> data : Lists.partition(this.dataList, BATCH_SIZE)){
//            makePostRequest(data);
//        }
        for(RFIDLiftData data : this.dataList){
            List<RFIDLiftData> singleData = new ArrayList<>();
            singleData.add(data);
            makePostRequest(singleData);
        }

        client.close();
        return this.result;
    }
}
