package utility;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * The Observable class that manages the current status text. When the status text is set externally
 * using setStatus, the current observers are notified.
 */
public class StatusObservable extends Observable {

    // Singleton instance
    private static StatusObservable instance = new StatusObservable();

    private String status;

    private Set<Observer> observers;

    // The clearStatus event
    private Timeline timeline;


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
     *
     * @param observer The new observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }


    /**
     * Updates the status text and notifies observers of the new value
     * After 10 seconds, the status text will be cleared
     *
     * @param text The new status text
     */
    void setStatus(String text) {
        //If there is an existing clear event, clear it
        if (timeline != null) {
            timeline.stop();
        }
        status = text;
        notifyObservers();

        timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> clearStatus()));
        timeline.play();
    }


    /**
     * Resets the status to an empty string and notifies observers of this new value
     */
    private void clearStatus() {
        status = "";
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
