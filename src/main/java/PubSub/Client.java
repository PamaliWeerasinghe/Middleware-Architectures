package PubSub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static String clientType;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PubSubClient <host> <port> <PUBLISHER|SUBSCRIBER>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        clientType = args[2].toUpperCase();

        if (!clientType.equals("PUBLISHER") && !clientType.equals("SUBSCRIBER")) {
            System.out.println("Client type must be either PUBLISHER or SUBSCRIBER");
            return;
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // First send client type to server
            out.println(clientType);

            if (clientType.equals("PUBLISHER")) {
                handlePublisher(out);
            } else {
                handleSubscriber(in);
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("I/O error for connection to " + host + ": " + e.getMessage());
        }
    }
    private static void handlePublisher(PrintWriter out) {
        System.out.println("Connected as PUBLISHER. Type messages to send to subscribers (type 'exit' to quit):");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if ("exit".equalsIgnoreCase(message)) {
                break;
            }
            out.println(message);
        }
        scanner.close();
    }
    private static void handleSubscriber(BufferedReader in) throws IOException {
        System.out.println("Connected as SUBSCRIBER. Waiting for messages...");
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println("Received: " + message);
        }
        System.out.println("Disconnected from server");
    }

}
