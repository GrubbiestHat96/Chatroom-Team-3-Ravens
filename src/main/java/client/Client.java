
import java.net.*;
import java.io.*;

public class Client {
//tylissa: updated to account for multithread
	public static void main(String[] args) throws Exception, UnknownHostException, IOException {

        	Socket socket = new Socket("ip-172-31-11-172", 5000);

        	PrintWriter output = new PrintWriter(
			 socket.getOutputStream(), true);

        	BufferedReader keyboard = new BufferedReader(
            		new InputStreamReader(System.in));

        	String message;
        	while ((message = keyboard.readLine()) != null) {
            		output.println(message);
        	}
		//tylissa: i commented this out because the 'logout' part in the clientHandler
		// handles the socket closing. i lieft it jsut in case there's an issue
       		// socket.close();

		//gets localHost ip
		InetAddress ip = InetAddress.getByName("localHost");
	
		//connecting the localHost ip to the server
		Socket connect = new Socket(ip, socket);

		//i/o streams
		DataInputStream inputStream = new DataInputStream(connect.getInputStream());
		DataOutputStream outputStream = new DataOutputStream(connect.getOutputStream());

	//send message thread
	Thread sendMsg = new Thread(new Runnable()
	{
		@Override
		public void run(){
			while(true){
				String msg = keyboard.readLine();
				try{
					outputStream(msg);
				}catch (IOException e){
					e.printStackTrace();
				}
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
					output.println(msg);
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	});

	sendMsg.start();
	readMsg.start();
    }
}
