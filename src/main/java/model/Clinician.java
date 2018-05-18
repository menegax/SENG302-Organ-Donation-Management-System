package model;

import utility.ClinicianActionRecord;
import utility.GlobalEnums;

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
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
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
        }
    }

    public ArrayList<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(ArrayList<String> middleNames) {
        this.middleNames = middleNames;
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
        }
    }

    /**
     * Concatenates a clinician's first, middle and last names, and returns the full name as a String
     * @return String concatenated name
     */
    public String getConcatenatedName() {
        String name = this.firstName;
        if(this.middleNames != null) {
            for(String middleName : this.middleNames) {
                name = name + " " + middleName;
            }
        }
        name = name + " " + this.lastName;
        return name;
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
        if (street1 != null && street1.length() > 0 && Pattern.matches("^[- a-zA-Z0-9]+$", street1)) {
            this.street1 = street1;
        }
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public GlobalEnums.Region getRegion() {
        return region;
    }

    public void setRegion(GlobalEnums.Region region) {
        this.region = region;
    }

    public Timestamp getModified() { return this.modified; }

    public void clinicianModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }

    public List<ClinicianActionRecord> getClinicianActionsList() {
        return clinicianActionsList;
    }

    public void addClinicianActionRecord(ClinicianActionRecord record) {
        clinicianActionsList.add(record);
    }
}
