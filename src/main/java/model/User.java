package model;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import utility.Searcher;
import utility.parsing.AsciiConverterCSV;

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

public abstract class User implements Serializable, Comparable<User> {

    private final UUID uuid = UUID.randomUUID();

    @Parsed(field = "first_names")
    @Convert(conversionClass =AsciiConverterCSV.class)
    protected String firstName;

    protected List<String> middleNames;

    // transient means that this property is not serialized on saving to disk
    transient PropertyChangeSupport propertyChangeSupport;

    @Parsed(field = "last_names")
    @Convert(conversionClass = AsciiConverterCSV.class)
    protected String lastName;

    private boolean changed = true;

    protected Timestamp modified;

    public User(){}

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
       changed = true;
       if (propertyChangeSupport != null) {
           propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "User Modified", null, null));
       }
//       systemLogger.log(FINEST, "User " + getUuid() + " modified");
   }


    public boolean getChanged() {
        return changed;
    }

    protected void databaseImport() {
        changed = false;
    }


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

    @Override
    public int compareTo(User o) {
        return this.getNameConcatenated().compareTo(o.getNameConcatenated());
    }


    /**
     * Sets modified on the user - hot fix for patient obj constructions
     * @param modified - modified timestamp
     */
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
}
