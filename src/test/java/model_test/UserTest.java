package model_test;

import model.Administrator;
import model.Clinician;
import model.Patient;
import model.User;
import org.junit.Test;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for concrete methods of the user class
 */
public class UserTest {

    private User patient;
    private User patientClone;

    private User clinician;
    private User clinicianClone;

    private User administrator;
    private User administratorClone;

    /**
     * Tests the deep clone method of the user class with each type of user
     */
    @Test
    public void testDeepClone() {
        givenUsers();
        whenUsersCloned();
        thenUsersDetailsEqual();
        thenUsersAttributesDeepCopied();
    }

    /**
     * Creates the users for testing
     */
    private void givenUsers() {
        patient = new Patient("TST0001", "Test", new ArrayList<>(), "Tester", LocalDate.of(2000, 1, 1));
        clinician = new Clinician(1, "Test", new ArrayList<>(), "Tester", GlobalEnums.Region.CANTERBURY);
        administrator = new Administrator("Username", "Test", new ArrayList<>(), "Tester", "TestPassword");
    }

    /**
     * Clones users using the deep clone method
     */
    private void whenUsersCloned() {
        patientClone = patient.deepClone();
        clinicianClone = clinician.deepClone();
        administratorClone = administrator.deepClone();
    }

    /**
     * Asserts that the first names of the users and their clones are the same
     */
    private void thenUsersDetailsEqual() {
        assertEquals(patient.getFirstName(), patientClone.getFirstName());
        assertEquals(clinician.getFirstName(), clinicianClone.getFirstName());
        assertEquals(administrator.getFirstName(), administratorClone.getFirstName());
    }

    /**
     * Asserts that the list attributes (middle names) of the users have been deep cloned
     */
    private void thenUsersAttributesDeepCopied() {
        patientClone.getMiddleNames().add("Middle");
        assertNotEquals(patientClone.getMiddleNames(), patient.getMiddleNames());
        clinicianClone.getMiddleNames().add("Middle");
        assertNotEquals(clinicianClone.getMiddleNames(), clinician.getMiddleNames());
        administratorClone.getMiddleNames().add("Middle");
        assertNotEquals(administratorClone.getMiddleNames(), administrator.getMiddleNames());
    }
}
