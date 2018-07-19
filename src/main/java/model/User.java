package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();

    // transient means that this property is not serialized on saving to disk
    transient PropertyChangeSupport propertyChangeSupport;

    public UUID getUuid() {
        return uuid;
    }

    /**
     * returns the full name of a user as one string
     * @return the user's full name
     */
    public abstract String getNameConcatenated();

    /**
     * sets the attributes of this user to the same as the one provided
     * @param newUserAttributes a user whose attributes this function copies
     */
    public abstract void setAttributes(User newUserAttributes);

    /**
     * Returns a deep clone of this user
     * @return the deep clone
     */
    public User deepClone() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (User) in.readObject();
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE, "Error setting attributes of patient");
        } catch (ClassNotFoundException e) {
            systemLogger.log(Level.SEVERE, "Error setting attributes of patient");
        }
        return null;
    }

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
