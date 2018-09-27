package model;

import utility.AdministratorActionRecord;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the class for Administrators. Administrators have basic identifying information like a patient
 * However they are identified by their username, and their NHI is not collected.
 * Administrators also have a password that is hashed and they are given a generated user salt
 */
public class Administrator extends User {

    private String username;

    private String salt;

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

    /**
     * Constructor for loading from database.
     * @param username The username of the admin.
     * @param fName The first name of the admin.
     * @param mNames The middle names of the admin in a ArrayList.
     * @param lName The last name of the admin.
     * @param salt The salt of the password hash.
     * @param records The records of the admin
     * @param password The hashed password of the admin.
     * @param modified Timestamp of the last time the admin was modified.
     * @param records The list of admin log records of the admin
     */
    public Administrator(String username, String fName, ArrayList<String> mNames, String lName, 
    		String salt, String password, Timestamp modified, List<AdministratorActionRecord> records) {
		super(fName, mNames, lName);
		this.username = username;
		this.salt = salt;
		this.password = password;
		this.modified = modified;
		this.adminActionsList = records;
		databaseImport();
	}

	public String getUsername() {
        return username;
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
        userModified();
    }

    private void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    private void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Returns the list of this admins actions. This should only be modified within UserActionHistory
     *
     * @return the list of Admin Action Records
     */
    public List<AdministratorActionRecord> getAdminActionsList() {
        return adminActionsList;
    }

    /**
     * Sets the attributes of the administrator to the attributes of the provided administrator
     * @param newUserAttributes a user whose attributes this function copies
     */
    public void setAttributes(User newUserAttributes) {
        Administrator newAdministratorAttributes = (Administrator) newUserAttributes.deepClone();

        setFirstName(newAdministratorAttributes.getFirstName());
        setLastName(newAdministratorAttributes.getLastName());
        setMiddleNames(newAdministratorAttributes.getMiddleNames());
        setHashedPassword(newAdministratorAttributes.getHashedPassword());
        setSalt(newAdministratorAttributes.getSalt());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Administrator)) {
            return false;
        }
        return username.toLowerCase().equals(((Administrator) obj).getUsername().toLowerCase());
    }

    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }
}
