
package database;

import model.Message;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @Test
    void storesAndFetchesMessages() {
        DatabaseManager db = new DatabaseManager();
        Message message = new Message("Alice", "Hello Bob");

        db.storeMessage("Bob", message);

        List<Message> result =
                db.fetchMessagesByReceiverId("Bob");

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getSender());
        assertEquals("Hello Bob", result.get(0).getTxt());
    }

    @Test
    void separatesMessagesByReceiver() {
        DatabaseManager db = new DatabaseManager();

        db.storeMessage("Bob",
                new Message("Alice", "Hello Bob"));

        db.storeMessage("Charlie",
                new Message("Alice", "Hello Charlie"));

        assertEquals(1,
                db.fetchMessagesByReceiverId("Bob").size());

        assertEquals(1,
                db.fetchMessagesByReceiverId("Charlie").size());
    }

    @Test
    void returnsEmptyHistoryForUnknownReceiver() {
        DatabaseManager db = new DatabaseManager();

        assertTrue(
                db.fetchMessagesByReceiverId("Unknown").isEmpty()
        );
    }

    @Test
    void rejectsInvalidMessages() {
        DatabaseManager db = new DatabaseManager();

        assertThrows(IllegalArgumentException.class,
                () -> db.storeMessage("", new Message("A", "Hi")));

        assertThrows(NullPointerException.class,
                () -> db.storeMessage("Bob", null));
    }
}
