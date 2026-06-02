package Task_01_new;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        //Validate command line arguments
        if (args.length !=2){
            System.out.println("Enter: java Client <SERVER_IP> <SERVER_PORT>");
            System.exit(1); //immediately terminates the
        }

        String serverIp = args[0];
        int serverPort = 0;

        try{
            serverPort=Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            System.out.println("Error: PORT must be a valid integer ");
            System.exit(1);
        }

        System.out.println("Attempting connection to "+ serverIp+": "+serverPort+" ...");

        //Establish connection and set up I/O pipelines
        try(
                Socket socket = new Socket(serverIp,serverPort);
                //PrintWriter handles outgoing data to the server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //BufferedReader captures what the user types locally into the CLI console
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

                // Read what the server sends
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ){
                    System.out.println("Connected Successfully! Type your messages below");
                    System.out.println("Type 'terminate' to disconnect and end the program \n");

                    String userInput;
                    //Perceptual write loop capturing user console input
                    while ((userInput = stdIn.readLine())!=null){
                        //Avoid sending pure empty inputs if the user just hits Enter
                        if(userInput.trim().isEmpty()){
                            continue;
                        }

                        //Send the text across the network pipeline to the server
                        out.println(userInput);

                        //Enforce exit criteria if user enters 'terminate'
                        if ("terminate".equalsIgnoreCase(userInput.trim())){
                            System.out.println("Disconnecting from the server...");
                            break;
                        }

                        //Recieve the reply from the server
                        String serverResponse = serverReader.readLine();
                        System.out.println("[Server Response]:  "+serverResponse);
                    }

        }catch (UnknownHostException e){
            System.out.println("Don't know about the host: "+serverIp);
            System.exit(1);
        }catch (IOException e){
            System.out.println("Coudln't get I/O for the connection to "+serverIp);
            System.exit(1);
        }
        System.out.println("Client session terminated cleanly");

    }
}
