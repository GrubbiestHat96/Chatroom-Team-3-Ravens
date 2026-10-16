package observers;

import model.Message;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void nofity();
}

interface Observer {
    void update(String text);
    void send(Message message);
    void receive(Message message);
}
