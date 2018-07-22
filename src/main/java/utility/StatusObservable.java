package utility;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * The Observable class that manages the current status text. When the status text is set externally
 * using setStatus, the current observers are notified.
 */
public class StatusObservable extends Observable {
    //Singleton instance
    private static StatusObservable instance = new StatusObservable();

    private String status;
    private Set<Observer> observers;

    private StatusObservable() {
        status = "";
        observers = new HashSet<>();
    }

    public static StatusObservable getInstance() {
        return instance;
    }

    /**
     * Adds an observer to the current set of observers
     * This observer will be an instance of GUIHome
     * @param observer The new observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Updates the status text and notifies observers of the new value
     * @param text The new status text
     */
    public void setStatus(String text) {
        status = text;
        notifyObservers();
    }

    /**
     * Notifies each observer (instances of GUIHome) to perform an update.
     */
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(instance, status);
        }
    }
}
