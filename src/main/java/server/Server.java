import java.io.*;
import java.net.*;
import java.util.Set;

public class Server {

	public static void main(String[] args) throws Exception {


	ServerSocket server = new ServerSocket(5000);
	System.out.println("Waiting for client");

	Socket socket = server.accept();
	System.out.println("Client connected");

	BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
	
	String message;
        while ((message = input.readLine()) != null) {
            System.out.println("Client: " + message);
        }

        socket.close();
        server.close();
}

}

