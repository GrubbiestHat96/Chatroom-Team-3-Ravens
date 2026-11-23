
package database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import model.Message;

public class DatabaseManager {

    private final LinkedList<StoredMessage> messages =
            new LinkedList<>();

    private record StoredMessage(
            String receiverId,
            Message message
    ) {}

    public synchronized void storeMessage(
            String receiverId, Message message) {

        if (receiverId == null || receiverId.isBlank()) {
            throw new IllegalArgumentException(
                "Receiver ID cannot be empty"
            );
        }

        Objects.requireNonNull(message, "Message cannot be null");

        messages.add(new StoredMessage(receiverId, message));
    }

    public synchronized List<Message> fetchMessagesByReceiverId(
            String receiverId) {

        if (receiverId == null || receiverId.isBlank()) {
            throw new IllegalArgumentException(
                "Receiver ID cannot be empty"
            );
        }

        List<Message> result = new ArrayList<>();

        for (StoredMessage stored : messages) {
            if (stored.receiverId().equals(receiverId)) {
                result.add(stored.message());
            }
        }

        return result;
    }
}
