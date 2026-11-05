import java.io.*;
import java.net.*;
import java.util.*;

//Nimai and shashanth edited this — added Kafka messaging imports
import messaging.MessageQueue;
import model.Message;

public class Server {
	//tylissa: vector to store active clients and counter for clients
	static Vector<clientHandler> active = new Vector<>();
	static int counter = 0;
    public static void main(String[] args) throws Exception {

        //Nimai and shashanth edited this — create producer and automatically close resources
        try (
            ServerSocket server = new ServerSocket(5000);
            MessageQueue queue = new MessageQueue()
        ) {
            System.out.println("Waiting for client");

            //Nimai and shashanth edited this — read client messages using UTF-8
            try (
                Socket socket = server.accept();
                BufferedReader input = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8
                    )
                )
            ) {
                System.out.println("Client connected");
		//tylissa: input/output streams for messages
		DataInputStream input = new DataInputStream(socket.getInputStream());
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());

		/* tylissa:
		*creating a client handler + new thread,  then adding them to the active
		* client list. the thread is then started
		* "client " + counter will be replaced with their username
		*/
		clientHandler handler = new clientHandler(socket, "client " + counter, input, output);
		Thread t = new Thread(handler);
		active.add(handler);
		t.start;
		counter++;
                //Nimai and shashanth edited this — forward client messages to Kafka
                String text;

                while ((text = input.readLine()) != null) {
                    System.out.println("Client: " + text);

                    Message message = new Message("Client", text);
                    queue.sendMessage(message);
                }
			

            }
        }
    }
}

//tylissa: clientHandler class
class clientHandler implements Runnable{
	Scanner keybd = new Scanner(System.in);
	private String username;
	final DataInputStream input;
	final DataOutputStream output;
	Socket socket;
	boolean loggedIn;

	public clientHandler(Socket socket, String username, DataInputSteam input, DataOutputStream output){
		this.input = input;
		this.output  = output;
		this.username = username;
		this.socket = socket;
		this.loggedIn = true;
	}

@Override
	public void run(){
		String received;
		while(true){
			try{ 
				received = input.readUTF(); // get message
				
				//should be changed later for a button that closes the socket
				if(received.equals("logout")){
					this.loggedIn = false;
					this.socket.close();
					break;
				}

				//splits the message into the message and the recipient
				StringTokenizer split = new StringTokenizer(received, '>');
				String sentMsg = split.nextToken();
				String recipient = split.nextToken();

				//look for the recipient in active users
				for (clientHandler handler : Server.active){
					if(handler.username.equals(recipient) && handler.loggedIn == true){
						handler.output.writeUTF(this.username + " : " + sentMsg);
						break;
					}
				}
			}catch (IOException e){
				e.printStackTrace();
			}
	}
		try{
			//close everything
			this.input.close();
			this.output.close();
		}catch(IOException e){
			e.printStackTrace():
		}
	}
}			
