package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import utility.SearchPatients;

public abstract class User {

    private final UUID uuid = UUID.randomUUID();
    
    protected String firstName;

    protected ArrayList<String> middleNames;

    protected String lastName;
    
    protected Timestamp modified;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (this.firstName == null || (!firstName.equals(this.firstName))) {
        	SearchPatients.getSearcher().removeIndex(this);
            this.firstName = firstName;
            if (getPreferredName() == null) {
                setPreferredName( firstName );
            }
            SearchPatients.getSearcher().addIndex(this);
            userModified();
        }
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleNames) {
        if (this.middleNames == null || (!middleNames.equals(this.middleNames))) {
        	SearchPatients.getSearcher().removeIndex(this);
            this.middleNames = middleNames;
            SearchPatients.getSearcher().addIndex(this);
            userModified();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (this.lastName == null || (!lastName.equals(this.lastName))) {
        	SearchPatients.getSearcher().removeIndex(this);
            this.lastName = lastName;
            SearchPatients.getSearcher().addIndex(this);
            userModified();
        }
    }
    
    /**
     * Returns the name of the patient as a formatted concatenated string
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
    
    /**
    *
    * Updates the modified timestamp of the patient
    */
   protected void userModified() {
       this.modified = new Timestamp(System.currentTimeMillis());
   }
    
    public UUID getUuid() {
        return uuid;
    }

}
