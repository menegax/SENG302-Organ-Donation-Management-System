package model;

import utility.ClinicianActionRecord;
import utility.GlobalEnums;
import utility.GlobalEnums.Region;

import java.sql.Time;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines the class for clinician members. Clinicians have basic identifying information like a patient
 * However they are identified by their staff ID, and their NHI is not collected.
 */
public class Clinician extends User {

    private int staffID;
    private String street1;
    private String street2;
    private String suburb;
    private Region region;

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
        super(firstName, middleNames, lastName);
        this.staffID = staffID;
        this.region = region;
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
        super(firstName, middleNames, lastName);
        this.staffID = staffID;
        this.street1 = street1;
        this.street2 = street2;
        this.suburb = suburb;
        this.region = region;
    }

    /**
     * Constructor for importing existing clinicians.
     * @param staffID StaffID of the clinician.
     * @param firstName First name of the clinician.
     * @param middleNames Middle names of the clinicians.
     * @param lastName Last name of the clinician.
     * @param street1 Street 1 address of the clinician's workplace.
     * @param street2 Street 2 address of the clinician's workplace.
     * @param suburb Suburb of the clinician's workplace.
     * @param region Region of the clinician's workplace.
     * @param modified Timestamp of the last time the clinician was modified.
     */
    public Clinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, String street1,
    		String street2, String suburb, Region region, Timestamp modified) {
    	super(firstName, middleNames, lastName);
        this.staffID = staffID;
    	this.street1 = street1;
    	this.street2 = street2;
    	this.suburb = suburb;
    	this.region = region;
    	this.modified = modified;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
        userModified();
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
            userModified();
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        if (street2 != null && Pattern.matches("^[- 0-9a-zA-Z]*$", street2)) {
            this.street2 = street2;
            userModified();
        }
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        if (suburb != null && Pattern.matches("^[- a-zA-Z]*$", suburb)) {
            this.suburb = suburb;
            userModified();
        }
    }

    public GlobalEnums.Region getRegion() {
        return region;
    }

    public void setRegion(GlobalEnums.Region region) {
        this.region = region;
        userModified();
    }

    /**
     * Returns the list of this clinicians actions. This should only be modified within UserActionHistory
     * @return the list of Clinician Action Records
     */
    public List<ClinicianActionRecord> getClinicianActionsList() {
        return clinicianActionsList;
    }
}
