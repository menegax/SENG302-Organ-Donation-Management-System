package model_test;

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
    }

    /**
     * Clones users using the deep clone method
     */
    private void whenUsersCloned() {
        patientClone = patient.deepClone();
        clinicianClone = clinician.deepClone();
    }

    /**
     * Asserts that the first names of the users and their clones are the same
     */
    private void thenUsersDetailsEqual() {
        assertEquals(((Patient) patient).getFirstName(), ((Patient) patientClone).getFirstName());
        assertEquals(((Clinician) clinician).getFirstName(), ((Clinician) clinicianClone).getFirstName());
    }

    /**
     * Asserts that the list attributes (middle names) of the users have been deep cloned
     */
    private void thenUsersAttributesDeepCopied() {
        ((Patient) patientClone).getMiddleNames().add("Middle");
        assertNotEquals(((Patient) patientClone).getMiddleNames(), ((Patient) patient).getMiddleNames());
        ((Clinician) clinicianClone).getMiddleNames().add("Middle");
        assertNotEquals(((Clinician) clinicianClone).getMiddleNames(), ((Clinician) clinician).getMiddleNames());
    }
}
