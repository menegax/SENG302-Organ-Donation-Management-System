package model;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines the class for Administrators. Administrators have basic identifying information like a patient
 * However they are identified by their username, and their NHI is not collected.
 * Administrators also have a password that is hashed and they are given a generated user salt
 */
public class Administrator extends User {
    private int staffId;

    private String firstName;
    private List<String> middleNames;
    private String lastName;

    private Timestamp modified;

    /**
     * Creates a new Administrator
     *
     * @param username    The new Administrators username. This should be unique
     * @param firstName   The Administrators first name
     * @param middleNames The Administrators middle names. This should should be a list of Strings where each string is a single middle name
     * @param lastName    The Administrators last name
     */
    public Administrator(int staffId, String firstName, List<String> middleNames, String lastName) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
    }

    private void adminModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }

    public int getStaffId() {
        return staffId;
    }

    public void setUsername(int staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the administrators last name if the provided new value is valid.
     * The last name must be non-null and have non-zero length. The last name can only
     * contain alphabetic characters and hyphens
     *
     * @param firstName The new last name
     */
    public void setFirstName(String firstName) {
        if (firstName != null && firstName.length() > 0 && Pattern.matches("^[-a-zA-Z]+$", firstName)) {
            this.firstName = firstName;
            adminModified();
        }
    }

    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        this.middleNames = middleNames;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the administrators last name if the provided new value is valid.
     * The last name must be non-null and have non-zero length. The last name can only
     * contain alphabetic characters and hyphens
     *
     * @param lastName The new last name
     */
    public void setLastName(String lastName) {
        if (lastName != null && lastName.length() > 0 && Pattern.matches("^[-a-zA-Z]+$", lastName)) {
            this.lastName = lastName;
            adminModified();
        }
    }

    public Timestamp getModified() {
        return modified;
    }
}
