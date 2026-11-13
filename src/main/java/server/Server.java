

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server {
	//tylissa: vector to store active clients and counter for clients
	static Vector<clientHandler> active = new Vector<>();
	static int counter = 0;
    public static void main(String[] args) throws Exception {

        try (
            ServerSocket server = new ServerSocket(5000)
        ) {
            System.out.println("Waiting for client");

            //Nimai and shashanth edited this — read client messages using UTF-8
            while (true) {
                Socket socket = server.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                clientHandler handler = new clientHandler(socket, "client " + counter, input, output);
                active.add(handler);
                new Thread(handler).start();
                counter++;
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
	volatile boolean loggedIn;

    public clientHandler(Socket socket, String username, DataInputStream input, DataOutputStream output){
		this.input = input;
		this.output  = output;
		this.username = username;
		this.socket = socket;
		this.loggedIn = true;
	}

    private void send(String text) throws IOException {
        synchronized (output) {
            output.writeUTF(text);
            output.flush();
        }
    }

@Override
	public void run(){
		String received;
        try {
            send("Connected as " + username + ". Use message>client 0 or logout.");
        } catch (IOException e) {
            Server.active.remove(this);
            try { socket.close(); } catch (IOException ignored) {}
            return;
        }
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
				StringTokenizer split = new StringTokenizer(received, ">");
				if (split.countTokens() != 2) { send("Use: message>client 0"); continue; }
                String sentMsg = split.nextToken().trim();
				String recipient = split.nextToken().trim();
                boolean found = false;

				//look for the recipient in active users
				for (clientHandler handler : Server.active.toArray(new clientHandler[0])){
					if(handler.username.equals(recipient) && handler.loggedIn == true){
						try {
                            handler.send(this.username + " : " + sentMsg);
                            found = true;
                        } catch (IOException ignored) {
                            // The recipient disconnected before delivery.
                        }
						break;
					}
				}
                if (!found) { send("Recipient not connected: " + recipient); }
			} catch (EOFException ignored) {
                break;
            } catch (IOException e) {
				e.printStackTrace();
                break;
			}
	}
        this.loggedIn = false;
        Server.active.remove(this);
		try{
			//close everything
            this.socket.close();
			this.input.close();
			this.output.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
