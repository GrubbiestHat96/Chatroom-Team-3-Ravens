
import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("ip-172-31-11-172", 5000);

        PrintWriter output = new PrintWriter(
            socket.getOutputStream(), true);

        BufferedReader keyboard = new BufferedReader(
            new InputStreamReader(System.in));

        String message;
        while ((message = keyboard.readLine()) != null) {
            output.println(message);
        }

        socket.close();
    }
}
