package Task_01;


import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Subscriber {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to PubSub Broker");
            System.out.println(in.readLine()); // Read welcome message

            // Subscribe to topics
            System.out.print("Enter topics to subscribe (comma separated): ");
            String[] topics = scanner.nextLine().split(",");

            JSONObject subscribeJson = new JSONObject();
            subscribeJson.put("type", "subscribe");
            subscribeJson.put("topics", new JSONArray(topics));
            out.println(subscribeJson.toString());
            System.out.println(in.readLine()); // Read subscription confirmation

            // Start a separate thread to listen for messages
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        JSONObject json = new JSONObject();
                        System.out.println("\nNew message on topic '" +
                                json.getString("topic") + "': " +
                                json.getString("message"));
                        System.out.print("> "); // Prompt
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost: " + e.getMessage());
                }
            }).start();

            // Main thread handles user input
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    // Unsubscribe before quitting
                    JSONObject unsubscribeJson = new JSONObject();
                    unsubscribeJson.put("type", "unsubscribe");
                    unsubscribeJson.put("topics", new JSONArray(topics));
                    out.println(unsubscribeJson.toString());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Subscriber error: " + e.getMessage());
        }
    }
}