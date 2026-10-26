package client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.Message;
import observers.Observer;

public class Client implements Observer {
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 5000);

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
