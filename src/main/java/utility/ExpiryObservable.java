package utility;

import model.PatientOrgan;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ExpiryObservable extends Observable {
    private static ExpiryObservable instance = null;

    private Set<Observer> observers;

    private ExpiryObservable() {
        observers = new HashSet<>();
    }

    public static ExpiryObservable getInstance() {
        if (instance == null) {
            instance = new ExpiryObservable();
        }
        return instance;
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void setExpired(PatientOrgan patientOrgan) {
        for (Observer o : observers) {
            o.update(this, patientOrgan);
        }
    }

}
