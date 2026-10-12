public interface Subject{
        void addObserver(Observer observer);
        void removeObserver(Observer observer);
        void nofity();
}

public interface Observer{
        void update(String);
        void send(message);
        void receive(message);
}
