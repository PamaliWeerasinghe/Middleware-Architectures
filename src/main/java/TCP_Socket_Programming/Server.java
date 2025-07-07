package TCP_Socket_Programming;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Server {
    private static final Integer PORT = 5000;
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket= new ServerSocket(PORT);
            System.out.println("Waiting for Client");
            Socket socket = serverSocket.accept();
            System.out.println("Client request accepted");

            //Sending data from the server to the client
            Scanner scan= new Scanner(System.in);
            //Write the data received from the client to the socket
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            while(true){
                System.out.println("Enter Data: ");
                String data = scan.nextLine();
                pw.println(data);
                pw.flush();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
