package client;
import java.net.*;
import java.io.*;

import model.Message;
import observers.Observer;

public class Client implements Observer {
//tylissa: updated to account for multithread



public static void main(String[] args) throws Exception, UnknownHostException, IOException {
			String host = args.length > 0 ? args[0] : "localhost";

			Socket socket = new Socket(host, 5000);

		BufferedReader keyboard = new BufferedReader(
				new InputStreamReader(System.in));

		//i/o streams
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());
		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

	//send message thread
	Thread sendMsg = new Thread(new Runnable()
	{
		@Override
		public void run() {
			while (true) {
				try {
					String msg = keyboard.readLine();
					if (msg == null) {
						break;
					}
					outputStream.writeUTF(msg);
					outputStream.flush();
					if (msg.equals("logout")) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});

	//receive message thread
	Thread readMsg = new Thread(new Runnable()
	{
		@Override
		public void run(){
			while (true){
				try{
					//read sent message
					String msg = inputStream.readUTF();
					System.out.println(msg);
				}catch (IOException e){
					if (!socket.isClosed() && !(e instanceof EOFException)) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	});

	sendMsg.setDaemon(true);
	sendMsg.start();
	readMsg.start();
    }

// overrides for Subject Observer (Tylissa)
    private String text = "";

    @Override
    public void update(String message) {
        this.text = message;
    }

    @Override
    public void send(Message message) {
        System.out.println("Message sent");
    }

    @Override
    public void receive(Message message) {
        System.out.println(message.getSender() + " has sent a message");
    }

}
