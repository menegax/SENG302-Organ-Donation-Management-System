package model;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import utility.Searcher;
import utility.parsing.AsciiConverterCSV;
import utility.parsing.DateConverterCSV;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();

    @Parsed(field = "first_names")
    @Convert(conversionClass =AsciiConverterCSV.class)
    protected String firstName;

    protected List<String> middleNames;

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
   }

    public boolean getChanged() {
        return changed;
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
