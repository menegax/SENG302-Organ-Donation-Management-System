package model;

import utility.Searcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.List;
import java.io.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.SystemLogger.systemLogger;

public abstract class User implements Serializable {

    private final UUID uuid = UUID.randomUUID();

    protected String firstName;

    protected List<String> middleNames;

    protected String lastName;

    protected Timestamp modified;

    public User(String firstName, List<String> middleNames, String lastName) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (Pattern.matches("[a-z|A-Z|-]{1,35}", firstName)) {
        	Searcher.getSearcher().removeIndex(this);
            this.firstName = firstName;
            Searcher.getSearcher().addIndex(this);
            userModified();
        }
    }

    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        if (!middleNames.equals(this.middleNames)) {
        	Searcher.getSearcher().removeIndex(this);
            this.middleNames = middleNames;
            Searcher.getSearcher().addIndex(this);
            userModified();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (this.lastName == null || (!lastName.equals(this.lastName))) {
        	Searcher.getSearcher().removeIndex(this);
            this.lastName = lastName;
            Searcher.getSearcher().addIndex(this);
            userModified();
        }
    }

    /**
     * Returns the name of the user as a formatted concatenated string
     *
     * @return string named
     */
    public String getNameConcatenated() {
        StringBuilder concatName;

        concatName = new StringBuilder( firstName + " " );
        if (middleNames != null && middleNames.size() > 0) {
            for (String middleName : middleNames) {
                concatName.append(middleName)
                        .append(" ");
            }
        }
        concatName.append(lastName);
        return concatName.toString();
    }

    public Timestamp getModified() {
        return modified;
    }

    /**
    *
    * Updates the modified timestamp of the patient
    */
   public void userModified() {
       this.modified = new Timestamp(System.currentTimeMillis());
       propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "User Modified", null, null));
   }

    // transient means that this property is not serialized on saving to disk
    transient PropertyChangeSupport propertyChangeSupport;

    public UUID getUuid() {
        return uuid;
    }

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
        } catch (Exception e) {
            e.printStackTrace();
            systemLogger.log(Level.SEVERE, "Error cloning user");
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
