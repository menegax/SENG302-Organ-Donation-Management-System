package model;

import utility.GlobalEnums;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Clinician {

    private int staffID;

    private String firstName;
    private ArrayList<String> middleNames;
    private String lastName;

    private String street1;
    private String street2;
    private String suburb;

    private GlobalEnums.Region region;

    public Clinician(int staffID, String firstName, ArrayList<String> middleNames, String lastName, GlobalEnums.Region region) {
        this.staffID = staffID;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.region = region;
    }

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
}
