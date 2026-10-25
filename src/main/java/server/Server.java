import java.io.*;
import java.net.*;

//Nimai and shashanth edited this — added Kafka messaging imports
import messaging.MessageQueue;
import model.Message;
import observers.Observers;
import subject.Subject;

public class Server implements Subject {

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

// overrides for Subject observers (Tylissa)
    private List<Observers> observers = new ArrayList<>();
    private String message = "";

    @Override
    public void addObserver(Observers observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observers observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observers observer : observers) {
            observer.update(message);
        }
    }

}
