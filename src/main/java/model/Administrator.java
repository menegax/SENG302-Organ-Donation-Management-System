package model;

import utility.AdministratorActionRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines the class for Administrators. Administrators have basic identifying information like a patient
 * However they are identified by their username, and their NHI is not collected.
 * Administrators also have a password that is hashed and they are given a generated user salt
 */
public class Administrator extends User {

    private String username;

    private final String salt;

    private String password;

    private List<AdministratorActionRecord> adminActionsList = new ArrayList<>();


    /**
     * Creates a new Administrator
     *
     * @param username    The new administrators username. This should be unique
     * @param firstName   The administrators first name
     * @param middleNames The administrators middle names. This should should be a list of Strings where each string is a single middle name
     * @param lastName    The administrators last name
     * @param password    The administrators password
     * @exception IllegalArgumentException If the administrators password is too short (less than 6 characters)
     */
    public Administrator(String username, String firstName, List<String> middleNames, String lastName, String password)
            throws IllegalArgumentException {
        super(firstName, middleNames, lastName);
        this.username = username.toUpperCase();

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.salt = org.apache.commons.codec.digest.DigestUtils.sha256Hex(LocalDate.now()
                .toString());
        this.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password + salt);
        userModified();
    }


    public void adminModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }


    public String getUsername() {
        return username;
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


    public String getSalt() {
        return salt;
    }


    public String getHashedPassword() {
        return password;
    }


    /**
     * Updates the administrators password
     *
     * @param password the administrators new password
     */
    public void setPassword(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password + salt);
        adminModified();
    }


    /**
     * Returns the list of this admins actions. This should only be modified within UserActionHistory
     *
     * @return the list of Admin Action Records
     */
    public List<AdministratorActionRecord> getAdminActionsList() {
        return adminActionsList;
    }
}
