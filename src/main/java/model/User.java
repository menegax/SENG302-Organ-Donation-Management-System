package model;

import utility.Searcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();

    protected String firstName;

    protected List<String> middleNames;

    protected String lastName;

    private boolean changed = true;

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
       changed = true;
       propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "User Modified", null, null));
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
