package PubSubNew;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        //Validate the three cmd line arguments
        if(args.length!=3){
            System.out.println("Usage: java Client <SERVER_IP> <SERVER_PORT> <ROLE>");
            System.exit(1);
        }

        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String mode = args[2].trim().toUpperCase();

        if (!"PUBLISHER".equals(mode) && !"SUBSCRIBER".equals(mode)){
            System.out.println("Invalid role specified. Role must be either PUBLISHER or SUBSCRIBER");
            System.exit(1);
        }

        System.out.println("Starting client application in "+mode+" mode...");

        try(
                Socket socket = new Socket(serverIp, serverPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ){
            //Inform the broker what role the client will be using
            out.println(mode);

            //Read server's welcome/confirmation message
            String welcomeMsg = serverReader.readLine();
            System.out.println("[Server]:   "+welcomeMsg);

            if ("PUBLISHER".equals(mode)){
                System.out.println("Ready to broadcast. Type text and press enter. \n Type 'terminate' to exit \n");
                String userInput;
                while ((userInput = stdIn.readLine())!=null){
                    if (userInput.trim().isEmpty()) continue;
                    out.println(userInput);

                    if ("terminate".equalsIgnoreCase(userInput.trim())){
                        System.out.println("Shutting down publishing mode");
                        break;
                    }
                }
            }else{
                System.out.println("Listening for updates... Type 'terminate' and press enter to exit \n");

                //creating thread to catch 'terminate'
                Thread terminalListener = new Thread(()->{
                    try{
                        String localInput;
                        while ((localInput = stdIn.readLine())!=null){
                            if ("terminate".equalsIgnoreCase(localInput.trim())){
                                System.out.println("Disconnecting subscriber node...");
                                out.println("terminate");
                                System.exit(0);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading terminal input: "+ e.getMessage());
                    }

                });
                terminalListener.setDaemon(true);
                terminalListener.start();

                String incomingBroadcast;
                while((incomingBroadcast = serverReader.readLine())!=null){
                    System.out.println(incomingBroadcast);
                }

            }



        }catch (Exception e){
            System.out.println("Client exception: "+ e.getMessage());
        }

        System.out.println("Client session dropped cleanly.");

    }
}
