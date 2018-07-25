package utility_test;

import model.Clinician;
import model.Patient;
import model.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import service.Database;
import utility.GlobalEnums;
import utility.undoRedo.Action;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

public class ActionTest {

    private User current;

    private User after;

    private Action action;

    private String beforeName = "beforeChange";

    private String afterName = "afterChange";

    private String nhi = "TST0001";

    private int staffId = 1;

    Database database = Database.getDatabase();

    /**
     * Turns of logging before the test starts
     */
    @BeforeClass
    public static void setup() {
        userActions.setLevel(Level.OFF);
    }

    /**
     * Resets the users and action attributes before each test
     */
    @Before
    public void reset() {
        current = null;
        after = null;
        action = null;
//        database.resetdatabase();
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
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        thenBeforePatientNotIndatabase();
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
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        thenBeforeClinicianNotIndatabase();
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
        thenAfterPatientNotIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        thenBeforePatientIndatabase();
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
        thenAfterClinicianNotIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        thenBeforeClinicianIndatabase();
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
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenBeforePatientNotIndatabase();
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
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenBeforeClinicianNotIndatabase();
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
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenBeforePatientNotIndatabase();
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
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenBeforeClinicianNotIndatabase();
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
        thenAfterPatientNotIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenBeforePatientIndatabase();
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
        thenAfterClinicianNotIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenBeforeClinicianIndatabase();
    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {
        userActions.setLevel(Level.INFO);
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
    private void thenAfterPatientIndatabase() throws InvalidObjectException {
        assertEquals(afterName, database.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and before first name is in the database
     * @throws InvalidObjectException thrown when no patient with that nhi
     */
    private void thenBeforePatientIndatabase() throws InvalidObjectException {
        assertEquals(beforeName, database.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is not in the database
     */
    private void thenAfterPatientNotIndatabase() {
        try {
            assertNotEquals(afterName, database.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a patient with the correct nhi and before first name is not in the database
     */
    private void thenBeforePatientNotIndatabase() {
        try {
            assertNotEquals(beforeName, database.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and after first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenAfterClinicianIndatabase() throws InvalidObjectException {
        assertEquals(afterName, database.getClinicianByID(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and before first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenBeforeClinicianIndatabase() throws InvalidObjectException {
        assertEquals(beforeName, database.getClinicianByID(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and after first name is not in the database
     */
    private void thenAfterClinicianNotIndatabase() {
        try {
            assertNotEquals(afterName, database.getClinicianByID(staffId).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and before first name is not in the database
     */
    private void thenBeforeClinicianNotIndatabase() {
        try {
            assertNotEquals(beforeName, database.getClinicianByID(staffId).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

}
