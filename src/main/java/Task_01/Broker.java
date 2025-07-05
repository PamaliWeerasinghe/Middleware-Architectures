package Task_01;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class Broker {
    //The port which the Broker will be listening for connections
    private static final int PORT=9000;
    //The server socket that will accept client connections
    private ServerSocket serverSocket;
    //A thread pool to handle multiple client connections concurrently
    private ExecutorService executorService;

    //Map maintains the list of subscribers for each topic
    //Each PrintWriter represents one connected subscriber
    
    private Map<String, List<PrintWriter>> topicSubscribers= new ConcurrentHashMap<>();

}
