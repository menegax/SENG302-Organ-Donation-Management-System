package service_test;

import model.Clinician;
import model.Patient;
import model.User;
import org.junit.Before;
import org.junit.Test;
import service.Database;
import utility.GlobalEnums;
import utility.UserActionHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

/**
 * Class used to test Database methods
 */
public class DatabaseTest {

    private User user;

    private String nhi = "TST0001";

    private int staffId = 1;

    /**
     * Resets the database and user to null
     */
    @Before
    public void reset() {

        user = null;
        Database.resetDatabase();
    }

    /**
     * Tests the addUser method of the database
     * @throws IOException thrown by invalid addition (caught when testing failure)
     */
    @Test
    public void testAddUser() throws IOException{
        givenNewPatient();
        whenUserAdded();
        thenPatientInDatabase();

        reset();
        givenPatientInDatabase();
        whenUserAdded();
        thenPatientInDatabase();

        reset();
        givenNullUser();
        whenUserAdded();
        thenUserNotInDatabase();

        reset();
        givenNewClinician();
        whenUserAdded();
        thenClinicianInDatabase();

        reset();
        givenClinicianInDatabase();
        whenUserAdded();
        thenClinicianInDatabase();
    }

    /**
     * Tests that the removeUser method of the database
     */
    @Test
    public void testRemoveUser() {
        givenPatientInDatabase();
        whenUserRemoved();
        thenUserNotInDatabase();

        reset();
        givenNewPatient();
        whenUserRemoved();
        thenUserNotInDatabase();

        reset();
        givenNullUser();
        whenUserRemoved();
        thenUserNotInDatabase();

        reset();
        givenClinicianInDatabase();
        whenUserRemoved();
        thenUserNotInDatabase();

        reset();
        givenNewClinician();
        whenUserRemoved();
        thenUserNotInDatabase();
    }

    /**
     * creates the user as a new patient
     */
    private void givenNewPatient() {
        user = new Patient(nhi, "Test", new ArrayList<>(), "Tester", LocalDate.of(2000, 1, 1));
    }

    /**
     * creates a new patient and adds it to the database
     */
    private void givenPatientInDatabase() {
        givenNewPatient();
        whenUserAdded();
    }

    /**
     * sets the user to null
     */
    private void givenNullUser() {
        user = null;
    }

    /**
     * attempts to add the user to the database
     */
    private void whenUserAdded() {
        Database.addUser(user);
    }

    /**
     * checks that a patient with the correct nhi is in the database
     * @throws IOException thrown when no such patient is found
     */
    private void thenPatientInDatabase() throws IOException{
        assertNotEquals(null, Database.getPatientByNhi(nhi));
    }

    /**
     * checks that a user with the designated nhi or staffId is not in the database
     * does this by catching exceptions that should be being thrown
     */
    private void thenUserNotInDatabase() {
        try {
            Database.getPatientByNhi(nhi);
            assertTrue(false);
        } catch (IOException e) {
            try {
                Database.getClinicianByID(staffId);
                assertTrue(false);
            } catch (IOException e1) {
                assertTrue(true);
            }
        }
    }

    /**
     * creates the user as a new patient
     */
    private void givenNewClinician() {
        user = new Clinician(staffId, "Test", new ArrayList<>(), "Tester", GlobalEnums.Region.CANTERBURY);
    }

    /**
     * creates a new clinician and adds it to the database
     */
    private void givenClinicianInDatabase() {
        givenNewClinician();
        whenUserAdded();
    }

    /**
     * checks that a clinician with the correct staffId is in the database
     * @throws IOException thrown when no such clinician is found
     */
    private void thenClinicianInDatabase() throws IOException{
        assertNotEquals(null, Database.getClinicianByID(staffId));
    }

    /**
     * Removes the user from the database
     */
    private void whenUserRemoved() {
        Database.removeUser(user);
    }
}
