package observers;

import model.Message;

public interface Observer {
    void update(String text);
    void send(Message message);
    void receive(Message message);
}
