package client;
import java.net.*;
import java.io.*;
import oberservers.Observers;
import model.Message;

public class Client implements Observers {
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
