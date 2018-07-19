package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    public abstract String getNameConcatenated();

    // transient means that this property is not serialized on saving to disk
    transient PropertyChangeSupport propertyChangeSupport;

    /**
     * Adds a listener to the propertyChangeSupport to be notified on user modification
     * @param propertyChangeListener the propertyChangeListener to be notified
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
}
