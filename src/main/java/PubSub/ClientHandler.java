package PubSub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static PubSub.Server.publishers;
import static PubSub.Server.subscribers;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientType;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //First message from client is its type
            clientType = in.readLine();
            if (clientType == null) return;

            System.out.println("New "+clientType+" connected: "+clientSocket.getRemoteSocketAddress());
            if (clientType.equalsIgnoreCase("PUBLISHER")){
                publishers.add(out);
                handlePublisher();
            }else if (clientType.equalsIgnoreCase("SUBSCRIBER")){
                subscribers.add(out);
                handleSubscriber();
            }
        }catch (IOException e){
            System.err.println("Error in ClientHandler: "+e.getMessage());
        }finally {
            try{
               if (out!=null){
                   if(clientType.equalsIgnoreCase("PUBLISHER")){
                       publishers.remove(out);
                   } else if (clientType.equalsIgnoreCase("SUBSCRIBER")) {
                       subscribers.remove(out);
                   }
               }
               clientSocket.close();
                System.out.println(clientType+"disconnected "+clientSocket.getRemoteSocketAddress());
            }catch (IOException e){
                System.err.println("Error closing socket: "+e.getMessage());
            }
        }
    }
    private void handlePublisher() throws IOException{
        String inputLine;
        while((inputLine = in.readLine())!=null){
            System.out.println("Received from publisher "+inputLine);
            broadCastToSubscribers(inputLine);
        }
    }
    private void handleSubscriber() throws IOException{
        //Will just keep the connection open
        while (true){
            //Detect client disconnect
            if(in.readLine()==null) break;
        }
    }

    private void broadCastToSubscribers(String message){
        for (PrintWriter subscriber : subscribers){
            try{
                subscriber.println(message);
            }catch (Exception e){
                System.err.println("Error broadcasting to Subscriber: "+e.getMessage());

            }
        }
    }
}

