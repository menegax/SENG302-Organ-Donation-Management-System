package utility;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ImportObservable extends Observable {
    private static ImportObservable instance = null;

    private Set<Observer> observers;

    private int completed;
    private int total;

    private ImportObservable() {
        observers = new HashSet<>();
        completed = 0;
        total = 0;
    }

    public static ImportObservable getInstance() {
        if (instance == null) {
            instance = new ImportObservable();
        }
        return instance;
    }

    public void addObserver(Observer o) {
        observers.add(o);
        double progress = getProgress();
        o.update(this, progress);
    }

    public void setCompleted(int completed) {
        this.completed = completed;
        notifyObservers();
    }

    public void setFinished() {
        this.completed = this.total;
        notifyObservers();
    }

    public int getCompleted() {
        return completed;
    }

    public void setTotal(int total) {
        this.total = total;
        notifyObservers();
    }

    private double getProgress() {
        return (total != 0) ? ((double) completed) / total : 1.0;
    }

    public void notifyObservers() {
        double progress = getProgress();
        for (Observer o : observers) {
            o.update(this, progress);
        }
    }

}
