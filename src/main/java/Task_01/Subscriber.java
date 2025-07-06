package Task_01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Subscriber {
    private static final String SERVER_HOST="localhost";
    private static final Integer SERVER_PORT=9000;

    public static void main(String[] args) {
        try(Socket socket = new Socket(SERVER_HOST,SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)){

            System.out.println("Connected to PubSub Broker");
            System.out.println(in.readLine());

            System.out.println("Enter the topics to subscribe (comma separated):");
            String[] topics=scanner.nextLine().split(",");

            JSONObject subscribeJson= new JSONObject();
            subscribeJson.put("type","subscribe");
            subscribeJson.put("topics",new JSONArray(topics));
            out.println(subscribeJson.toString());
            System.out.println(in.readLine());

            //Start a separate thread to listen for messages
            new Thread(()->{
                try{
                    JSONObject json=new JSONObject(message);
                    System.out.println("\nNew message on topic '"+
                            json.getString("topic")+"': "+
                            json.getString("message"));
                    System.out.print("> ");
                }catch(IOException e){
                    System.err.println("Connection lost: " + e.getMessage());
                }   System.out.print("> ");
            }).start();
        //Main thread handles user input
            while(true){
                System.out.print("> ");
                String input=scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)){
                    //unsubscribing before quitting
                    JSONObject unsubscribeJson = new JSONObject();
                    unsubscribeJson.put("type","unsubscribe");
                    unsubscribeJson.put("topics",new JSONArray());
                    out.println(unsubscribeJson.toString());
                    break;
                }
            }

        }catch (IOException e){
            System.err.println("Subscriber error: "+e.getMessage());
        }
    }
}
