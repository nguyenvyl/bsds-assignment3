package assignment2.bsds;


import java.util.concurrent.ExecutionException;

/**
 * Writes all day 2 data while all skiers try to read their day 1 data.
 */
public class ReadWriteClient {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    GetClient getClient = new GetClient();
    PostClient postClient = new PostClient();

    String[] noargs = new String[1];
    postClient.main(noargs);
    getClient.main(noargs);

  }

}
