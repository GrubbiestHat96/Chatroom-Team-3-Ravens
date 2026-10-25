package subject;

import observers.Observers;

public interface Subject {

    void addObserver(Observers observer);

    void removeObserver(Observers observer);

    void notifyObservers();
}


