package Task_01_new;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        //Validate the command line arguments
        if (args.length !=1){
            System.out.println("Enter: java Server <PORT>");
            System.exit(1); //immediately terminates the
        }
        int port = 0;

        try {
            port = Integer.parseInt(args[0]);

        } catch (NumberFormatException e) {
            System.out.println("Error: PORT must be a valid integer ");
            System.exit(1);
        }

        System.out.println("Server is starting up...");

        //Instantiate the ServerSocket using try-with-resources for safe cleanup
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // accept() blocks execution until a clients connect
            try(Socket clientSocket = serverSocket.accept()){
                System.out.println("Connection established with client at:  "+clientSocket.getRemoteSocketAddress());

                //Set up the input stream reader to capture client data
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                //Perceptual read loop
                while ((inputLine = reader.readLine())!=null){
                    //Display text on the server side CLI as standard system output text
                    System.out.println("[Client]:   "+ inputLine);

                    //Handle the termination keyword
                    if("terminate".equalsIgnoreCase(inputLine.trim())){
                        System.out.println("\n Termination keyword detected. Closing server connection gracefully");
                        break;
                    }
                }
                //Capturing the input typed by the server
                String serverInput;
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

                //Type a reply back to the client
                System.out.println("Type reply to client:   ");
                String serverResponse = stdIn.readLine();

                //send the server's message down to the network pipeline
                out.println(serverResponse);


            }
        }catch (IOException e){
            System.out.println("Exception caught when trying to listen on port "+port+" or listening for a connection");
            System.out.println(e.getMessage());
        }
        System.out.println("Server session terminated cleanly");
    }
}
