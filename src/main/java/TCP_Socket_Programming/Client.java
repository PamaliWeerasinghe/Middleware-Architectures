package TCP_Socket_Programming;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
public class Client {
    public static void main(String[] args) {
        try{
            Socket client = new Socket("localhost",5000);
            System.out.println("Client is Connected");

            //read data from the server
            //BufferedReader is character input
            //InputStream is byte oriented data
            //InputStreamReader will convert the byte data to character data
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                String data = bufferedReader.readLine();
                System.out.println("Data from Server: "+data);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
