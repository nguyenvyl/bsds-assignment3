package assignment2.bsds;

import bsdsass2testdata.RFIDLiftData;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
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
import org.glassfish.jersey.client.ClientProperties;

/**
 * A task which can be passed to an Executor object
 * When a task is called, it makes a specified number of HTTP requests and stores the results of these requests.
 */
public class PostTask implements Callable<TaskResult> {

    private List<RFIDLiftData> dataList;
    private final WebTarget webTarget;
    private final TaskResult result;
    private final Client client;
    private static final int BATCH_SIZE = 50;
    private int timeoutCount = 0;

    public PostTask(List<RFIDLiftData> dataList, String url) {
        this.client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 120000);
        client.property(ClientProperties.READ_TIMEOUT, 120000);
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
            System.out.println("" + timeoutCount);
            timeoutCount++;
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

        for(List<RFIDLiftData> data : Lists.partition(this.dataList, BATCH_SIZE)){
            makePostRequest(data);
        }

        client.close();
        return this.result;
    }
}
