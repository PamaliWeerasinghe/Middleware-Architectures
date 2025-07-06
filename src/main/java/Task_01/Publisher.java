package Task_01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Publisher {
    private static final String SERVER_HOST="localhost";
    private static final Integer SERVER_PORT=9000;

    public static void main(String[] args) {
        try(Socket socket=new Socket(SERVER_HOST,SERVER_PORT);
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner=new Scanner(System.in))
        {
            System.out.println("Connected to PubSub Broker");
            System.out.println(in.readLine());
            while (true){
                System.out.println("Enter topic (or 'quit' to exit): ");
                String topic=scanner.nextLine();
                if ("quit".equalsIgnoreCase(topic)) break;
                System.out.println("Enter message: ");
                String message=scanner.nextLine();
                JSONObject json = new JSONObject();
                json.put("type", "publish");
                json.put("topic", topic);
                json.put("message", message);

                out.println(json.toString());
                System.out.println(in.readLine()); // Read server response
            }


        }catch (IOException e){
            System.err.println("Publisher error: " + e.getMessage());
        }
    }
}
