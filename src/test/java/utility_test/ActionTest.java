package utility_test;

import model.Clinician;
import model.Patient;
import model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import service.Database;
import utility.GlobalEnums;
import utility.undoRedo.Action;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ActionTest {

    private User current;

    private User after;

    private Action action;

    private String beforeName = "beforeChange";

    private String afterName = "afterChange";

    private String nhi = "TST0001";

    private int staffId = 1;

    /**
     * Resets the users and action attributes before each test
     */
    @Before
    public void reset() {
        current = null;
        after = null;
        action = null;
        Database.resetDatabase();
    }

    /**
     * tests the action constructor using patients
     */
    @Test
    public void testPatientActionConstructor() throws InvalidObjectException{
        givenEditedPatient();
        whenActionCreated();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        thenAfterPatientInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        thenBeforePatientNotInDatabase();
    }

    /**
     * tests the action constructor using clinicians
     */
    @Test
    public void testClinicianActionConstructor() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        thenBeforeClinicianNotInDatabase();
    }

    /**
     * tests the action unexecute method using patients
     */
    @Test
    public void testPatientUnexecute() throws InvalidObjectException{
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        thenCurrentEqualsBeforePatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionUnexecuted();
        thenAfterPatientNotInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        thenBeforePatientInDatabase();
    }

    /**
     * tests the action unexecute method using clincians
     */
    @Test
    public void testClinicianUnexecute() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        thenCurrentEqualsBeforeClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionUnexecuted();
        thenAfterClinicianNotInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        thenBeforeClinicianInDatabase();
    }

    /**
     * tests the action execute method using patients
     */
    @Test
    public void testPatientExecute() throws InvalidObjectException{
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenAfterPatientInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenBeforePatientNotInDatabase();
    }

    /**
     * tests the action execute method using clinicians
     */
    @Test
    public void testClinicianExecute() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenBeforeClinicianNotInDatabase();
    }

    /**
     * tests the action execute method does nothing gracefully using patients
     */
    @Test
    public void testUselessExecutePatient() throws InvalidObjectException{
        givenEditedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionExecuted();
        thenAfterPatientInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenBeforePatientNotInDatabase();
    }

    /**
     * tests the action execute method does nothing gracefully using clinicians
     */
    @Test
    public void testUselessExecuteClinician() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        whenActionExecuted();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionExecuted();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenBeforeClinicianNotInDatabase();
    }

    /**
     * tests the action unexecute method does nothing gracefully using patients
     */
    @Test
    public void testUselessUnexecutePatient() throws InvalidObjectException{
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenCurrentEqualsBeforePatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenAfterPatientNotInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenBeforePatientInDatabase();
    }

    /**
     * tests the action unexecute method does nothing gracefully using clinicians
     */
    @Test
    public void testUselessUnexecuteClinician() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenCurrentEqualsBeforeClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenAfterClinicianNotInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenBeforeClinicianInDatabase();
    }

    /**
     * sets up the current and after users as patients with different first names
     */
    private void givenEditedPatient() {
        current = new Patient(nhi, beforeName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
        after = new Patient(nhi, afterName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
    }

    /**
     * sets up the current and after users as clinicians with different first names
     */
    private void givenEditedClinician() {
        current = new Clinician(staffId, beforeName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
        after = new Clinician(staffId, afterName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
    }

    /**
     * Sets up the current and after patients as a new patient being created
     */
    private void givenNewPatient() {
        current = null;
        after = new Patient(nhi, afterName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
    }

    /**
     * Sets up the current and after clinicians as a new clinician being created
     */
    private void givenNewClinician() {
        current = null;
        after = new Clinician(staffId, afterName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
    }

    /**
     * Sets up the current and after patients as a patient being deleted
     */
    private void givenDeletedPatient() {
        current = new Patient(nhi, beforeName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
        after = null;
    }

    /**
     * Sets up the current and after clinicians as a clinician being deleted
     */
    private void givenDeletedClinician() {
        current = new Clinician(staffId, beforeName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
        after = null;
    }

    /**
     * creates an action using the current and after users
     */
    private void whenActionCreated() {
        action = new Action(current, after);
    }

    /**
     * unexecutes the action attribute
     */
    private void whenActionUnexecuted() {
        action.unexecute();
    }

    /**
     * executes the action method
     */
    private void whenActionExecuted() {
        action.execute();
    }

    /**
     * checks that the current patient's first name is the after value
     */
    private void thenCurrentEqualsAfterPatient() {
        assertEquals(((Patient) current).getFirstName(), ((Patient) after).getFirstName());
        assertEquals(afterName, ((Patient) current).getFirstName());
    }

    /**
     * checks that the current clinician's first name is the after value
     */
    private void thenCurrentEqualsAfterClinician() {
        assertEquals(((Clinician) current).getFirstName(), ((Clinician) after).getFirstName());
        assertEquals(afterName, ((Clinician) current).getFirstName());
    }

    /**
     * checks that the current patient's first name is the before value
     */
    private void thenCurrentEqualsBeforePatient() {
        assertNotEquals(((Patient) current).getFirstName(), ((Patient) after).getFirstName());
        assertEquals(beforeName, ((Patient) current).getFirstName());
    }

    /**
     * checks that the current clinician's first name is the before value
     */
    private void thenCurrentEqualsBeforeClinician() {
        assertNotEquals(((Clinician) current).getFirstName(), ((Clinician) after).getFirstName());
        assertEquals(beforeName, ((Clinician) current).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is in the database
     * @throws InvalidObjectException thrown when no patient with that nhi
     */
    private void thenAfterPatientInDatabase() throws InvalidObjectException {
        assertEquals(afterName, Database.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and before first name is in the database
     * @throws InvalidObjectException thrown when no patient with that nhi
     */
    private void thenBeforePatientInDatabase() throws InvalidObjectException {
        assertEquals(beforeName, Database.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is not in the database
     */
    private void thenAfterPatientNotInDatabase() {
        try {
            assertNotEquals(afterName, Database.getPatientByNhi(nhi).getFirstName());
        } catch (InvalidObjectException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a patient with the correct nhi and before first name is not in the database
     */
    private void thenBeforePatientNotInDatabase() {
        try {
            assertNotEquals(beforeName, Database.getPatientByNhi(nhi).getFirstName());
        } catch (InvalidObjectException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and after first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenAfterClinicianInDatabase() throws InvalidObjectException {
        assertEquals(afterName, Database.getClinicianByID(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and before first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenBeforeClinicianInDatabase() throws InvalidObjectException {
        assertEquals(beforeName, Database.getClinicianByID(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and after first name is not in the database
     */
    private void thenAfterClinicianNotInDatabase() {
        try {
            assertNotEquals(afterName, Database.getClinicianByID(staffId).getFirstName());
        } catch (InvalidObjectException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and before first name is not in the database
     */
    private void thenBeforeClinicianNotInDatabase() {
        try {
            assertNotEquals(beforeName, Database.getClinicianByID(staffId).getFirstName());
        } catch (InvalidObjectException e) {
            assertTrue(true);
        }
    }

}
