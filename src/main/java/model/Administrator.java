package model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Defines the class for Administrators. Administrators have basic identifying information like a patient
 * However they are identified by their username, and their NHI is not collected.
 * Administrators also have a password that is hashed and they are given a generated user salt
 */
public class Administrator extends User {
    private String username;

    private String firstName;
    private List<String> middleNames;
    private String lastName;

    private final String salt;
    private String password;

    private Timestamp modified;

    /**
     * Creates a new Administrator
     *
     * @param username    The new administrators username. This should be unique
     * @param firstName   The administrators first name
     * @param middleNames The administrators middle names. This should should be a list of Strings where each string is a single middle name
     * @param lastName    The administrators last name
     * @param password    The administrators password
     * @throws IllegalArgumentException If the administrators password is too short (less than 6 characters)
     */
    public Administrator(String username, String firstName, List<String> middleNames, String lastName, String password) throws IllegalArgumentException {
        this.username = username;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.salt = org.apache.commons.codec.digest.DigestUtils.sha256Hex(LocalDate.now().toString());
        this.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password + salt);
    }

    private void adminModified() {
        this.modified = new Timestamp(System.currentTimeMillis());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return password;
    }
}
