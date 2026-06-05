package PubSubNew;

import PubSub.ClientHandler;
import com.sun.jdi.PathSearchingVirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    //Thread-safe list to hold output streams of all active SUBSCRIBERS
    private static final List<PrintWriter> subscriberStreams = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("java Server <PORT>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        System.out.println("Message Broker Server is starting up...");

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Broker is listening on port "+port+" ...");

            //Listen and accepts for new client connections
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("[System] New connection accepted from:  "+clientSocket.getRemoteSocketAddress());

                //Hand over the socket to a new ClientHandler task and start it in a new thread
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(handler);
                clientThread.start();

            }

        }catch (IOException e){
            System.out.println("Server exception:   " + e.getMessage());
        }
    }
    //Register Subscriber
    public static void registerSubscriber(PrintWriter out){
        subscriberStreams.add(out);
    }
    //Remove Subscriber
    public static void removeSubscriber(PrintWriter out){
        subscriberStreams.remove(out);
    }
    //Broadcasts a message to all registered subscriber terminals
    public static void broadcastToSubscribers(String message, String senderInfo){
        String formattedMessage = "["+ senderInfo+"]:   "+message;

        for (PrintWriter writer: subscriberStreams){
            try{
                writer.println(formattedMessage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Inner worker class
    private static class ClientHandler implements Runnable{
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientMode = "UNKNOWN"; //can be either a publisher or a subscriber
        private String clientAddress;

        public ClientHandler(Socket socket){
            this.socket = socket;
            this.clientAddress = socket.getRemoteSocketAddress().toString();
        }

        @Override
        public void run(){
            try{
                //Establish communication pipelines
                in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                out = new PrintWriter(socket.getOutputStream(), true);

                //Client should select either SUBSCRIBER or PUBLISHER
                String initialModelMsg = in.readLine();

                if (initialModelMsg==null) return;

                clientMode = initialModelMsg.trim().toUpperCase();
                System.out.println("Client "+clientAddress+" registered as: "+ clientMode);

                //If the client is SUBSCRIBER then register to subscriber list
                if ("SUBSCRIBER".equals(clientMode)){
                    Server.registerSubscriber(out);
                    out.println("Welcome Subscriber! Waiting for broadcasts...");
                }else if("PUBLISHER".equals(clientMode)){
                    out.println("Welcome Publisher! You can transmit messages...");
                }

                //listening loop for this specific client stream
                String inputLine;
                while((inputLine = in.readLine())!=null){
                    System.out.println("[" + clientMode+ " " + clientAddress + "]: "+inputLine);
                    if ("terminate".equalsIgnoreCase(inputLine.trim())){
                        System.out.println("[System] "+ clientMode + " "+ clientAddress + "requested disconnection");
                        break;
                    }

                    //Broker Logic: If the message arrives from a Publisher, broadcast it to all Subscribers
                    if("PUBLISHER".equals(clientMode)){
                        Server.broadcastToSubscribers(inputLine, "Pub "+clientAddress);
                    }
                }

            } catch(Exception e){
                System.out.println("[System] Connection loss with " + clientMode + " (" + clientAddress + "): " + e.getMessage());
            } finally {
                if ("SUBSCRIBER".equals(clientMode)){
                    Server.removeSubscriber(this.out);
                }

                try{
                    socket.close();
                    System.out.println("[System] Closed socket handles for "+clientAddress);

                } catch (IOException e) {
                    System.out.println("Error closing socket:   " + e.getMessage());
                }
            }
        }
    }


}
