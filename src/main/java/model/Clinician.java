package model;

import org.apache.commons.lang3.StringUtils;
import utility.ClinicianActionRecord;
import utility.GlobalEnums;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines the class for clinician members. Clinicians have basic identifying information like a patient
 * However they are identified by their staff ID, and their NHI is not collected.
 */
public class Clinician extends User {

    private int staffID;

    private String firstName;
    private ArrayList<String> middleNames;
    private String lastName;
    private String street1;
    private String street2;
    private String suburb;
    private Timestamp modified;

    private GlobalEnums.Region region;

    private List<ClinicianActionRecord> clinicianActionsList = new ArrayList<>();

    /**
     * Creates a clinician instance without providing the workplace address details - as they are optional
     *
     * @param staffID     The staffID for the new clinician
     * @param firstName   Their first name
     * @param middleNames A list of the clinicians middle names
     * @param lastName    Their last name
     * @param region      The region they are located in
     */
    public Clinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, GlobalEnums.Region region) {
        this.staffID = staffID;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.region = region;
        clinicianModified();
    }

    /**
     * Creates a clinician instance with full details - including workplace address
     *
     * @param staffID     The staffID for the new clinician
     * @param firstName   Their first name
     * @param middleNames A list of the clinicians middle names
     * @param lastName    Their last name
     * @param street1     Street 1 address of their workplace
     * @param street2     Street 2 address of their workplace
     * @param suburb      The suburb of their workplace
     * @param region      The region they are located in
     */
    public Clinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, String street1, String street2, String suburb, GlobalEnums.Region region) {
        this.staffID = staffID;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.street1 = street1;
        this.street2 = street2;
        this.suburb = suburb;
        this.region = region;
        clinicianModified();
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
        clinicianModified();
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the clinicians first name if the provided new value is valid.
     * The first name must be non-null and have non-zero length. The first name can only
     * contain alphabetic characters and hyphens
     *
     * @param firstName The new first name
     */
    public void setFirstName(String firstName) {
        if (firstName != null && firstName.length() > 0 && Pattern.matches("^[-a-zA-Z]+$", firstName)) {
            this.firstName = firstName;
            clinicianModified();
        }
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleNames) {
        this.middleNames = middleNames;
        clinicianModified();
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the clinicians last name if the provided new value is valid.
     * The last name must be non-null and have non-zero length. The last name can only
     * contain alphabetic characters and hyphens
     *
     * @param lastName The new last name
     */
    public void setLastName(String lastName) {
        if (lastName != null && lastName.length() > 0 && Pattern.matches("^[-a-zA-Z]+$", lastName)) {
            this.lastName = lastName;
            clinicianModified();
        }
    }

    /**
     * Concatenates a clinician's first, middle and last names, and returns the full name as a String
     * @return String concatenated name
     */
    public String getNameConcatenated() {

        String firstNameFormatted = StringUtils.capitalize(getFirstName());

        StringBuilder middleNamesFormatted = new StringBuilder();
        if(getMiddleNames() != null) {
            for(String middleName : getMiddleNames()) {
                 middleNamesFormatted.append(StringUtils.capitalize(middleName))
                         .append(" ");
            }
        }
        String lastNameFormatted = StringUtils.capitalize(getLastName());
        return firstNameFormatted + " " + middleNamesFormatted + lastNameFormatted;
    }

    public String getStreet1() {
        return street1;
    }

    /**
     * Updates the clinicians Street1 for their work address if the provided new value is valid.
     * The Street1 string must be non-null and have non-zero length. It can only be comprised
     * of alphanumerics, spaces, and hyphens
     *
     * @param street1 The new street1 value
     */
    public void setStreet1(String street1) {
        if (street1 != null && Pattern.matches("^[- 0-9a-zA-Z]*$", street1)) {
            this.street1 = street1;
            clinicianModified();
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        if (street2 != null && Pattern.matches("^[- 0-9a-zA-Z]*$", street2)) {
            this.street2 = street2;
            clinicianModified();
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        if (suburb != null && Pattern.matches("^[- a-zA-Z]*$", suburb)) {
            this.suburb = suburb;
            clinicianModified();
        }
    }

    public GlobalEnums.Region getRegion() {
        return region;
    }

    public void setRegion(GlobalEnums.Region region) {
        this.region = region;
        clinicianModified();
    }

    public Timestamp getModified() { return this.modified; }

    public void clinicianModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "Clinician Modified", null, null));
    }

    /**
     * Returns the list of this clinicians actions. This should only be modified within UserActionHistory
     * @return the list of Clinician Action Records
     */
    public List<ClinicianActionRecord> getClinicianActionsList() {
        return clinicianActionsList;
    }
}
