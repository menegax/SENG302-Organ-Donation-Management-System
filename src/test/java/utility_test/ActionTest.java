package utility_test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static utility.UserActionHistory.userActions;

import model.Clinician;
import model.Patient;
import model.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.ClinicianDataService;
import service.PatientDataService;
import service.interfaces.IClinicianDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.IAction;
import utility.undoRedo.SingleAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

public class ActionTest {

    private User current;

    private User after;

    private IAction action;

    private String beforeName = "beforeChange";

    private String afterName = "afterChange";

    private String nhi = "TST0001";

    private int staffId = 1;

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    private IPatientDataService patientDataService = new PatientDataService();


    private static boolean validConnection = false;

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
    }

    /**
     * tests the action constructor using globalPatients
     */
    @Test
    public void testPatientActionConstructor() {
        givenEditedPatient();
        whenActionCreated();
        whenUserSaved();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenUserSaved();
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenUserSaved();
        thenBeforePatientNotInDatabase();
    }

    /**
     * tests the action constructor using clinicians
     */
    @Test
    public void testClinicianActionConstructor() {
        givenEditedClinician();
        whenActionCreated();
        whenUserSaved();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenUserSaved();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenUserSaved();
        thenBeforeClinicianNotInDatabase();
    }

//    /**
//     * tests the action unexecute method using globalPatients
//     */
//    @Test
//    public void testPatientUnexecute() throws InvalidObjectException{
//        givenEditedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenCurrentEqualsBeforePatient();
//
//        reset();
//        givenNewPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenAfterPatientNotInDatabase();
//
//        reset();
//        givenDeletedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenBeforePatientInDatabase();
//    }

//    /**
//     * tests the action unexecute method using clincians
//     */
//    @Test
//    public void testClinicianUnexecute() throws InvalidObjectException{
//        givenEditedClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenCurrentEqualsBeforeClinician();
//
//        reset();
//        givenNewClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenAfterClinicianNotIndatabase();
//
//        reset();
//        givenDeletedClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenBeforeClinicianIndatabase();
//    }

    /**
     * tests the action execute method using globalPatients
     */
    @Test
    public void testPatientExecute() {
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenBeforePatientNotInDatabase();
    }

    private void whenUserSaved() {
        if (after instanceof Patient) {
            patientDataService.save((Patient) after);
        } else if (after instanceof Clinician) {
            clinicianDataService.save((Clinician) after);
        }
    }

    /**
     * tests the action execute method using clinicians
     */
    @Test
    public void testClinicianExecute() {
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
        thenBeforeClinicianNotInDatabase();
    }

    /**
     * tests the action execute method does nothing gracefully using globalPatients
     */
    @Test
    public void testUselessExecutePatient() {
        givenEditedPatient();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenCurrentEqualsAfterPatient();

        reset();
        givenNewPatient();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenAfterPatientIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenBeforePatientNotInDatabase();
    }

    /**
     * tests the action execute method does nothing gracefully using clinicians
     */
    @Test
    public void testUselessExecuteClinician() {
        givenEditedClinician();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenAfterClinicianInDatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenBeforeClinicianNotInDatabase();
    }

//    /**
//     * tests the action unexecute method does nothing gracefully using globalPatients
//     */
//    @Test
//    public void testUselessUnexecutePatient() throws InvalidObjectException{
//        givenEditedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenCurrentEqualsBeforePatient();
//
//        reset();
//        givenNewPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenAfterPatientNotInDatabase();
//
//        reset();
//        givenDeletedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenBeforePatientInDatabase();
//    }

//    /**
//     * tests the action unexecute method does nothing gracefully using clinicians
//     */
//    @Test
//    public void testUselessUnexecuteClinician() throws InvalidObjectException{
//        givenEditedClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenCurrentEqualsBeforeClinician();
//
//        reset();
//        givenNewClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenAfterClinicianNotIndatabase();
//
//        reset();
//        givenDeletedClinician();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenBeforeClinicianIndatabase();
//    }

    /**
     * Reset the logging level
     */
    @AfterClass
    public static void tearDown() {
        userActions.setLevel(Level.INFO);
    }

    /**
     * sets up the current and after users as globalPatients with different first names
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
     * Sets up the current and after globalPatients as a new patient being created
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
     * Sets up the current and after globalPatients as a patient being deleted
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
        action = new SingleAction(current, after);
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
        assertEquals(current.getFirstName(), after.getFirstName());
        assertEquals(afterName, current.getFirstName());
    }

    /**
     * checks that the current clinician's first name is the after value
     */
    private void thenCurrentEqualsAfterClinician() {
        assertEquals(current.getFirstName(), after.getFirstName());
        assertEquals(afterName, current.getFirstName());
    }

    /**
     * checks that the current patient's first name is the before value
     */
    private void thenCurrentEqualsBeforePatient() {
        assertNotEquals(current.getFirstName(), after.getFirstName());
        assertEquals(beforeName, current.getFirstName());
    }

    /**
     * checks that the current clinician's first name is the before value
     */
    private void thenCurrentEqualsBeforeClinician() {
        assertNotEquals(current.getFirstName(), after.getFirstName());
        assertEquals(beforeName, current.getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is in the database
     */
    private void thenAfterPatientIndatabase() {
        assertEquals(afterName, patientDataService.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and before first name is in the database
     */
    private void thenBeforePatientInDatabase() {
        assertEquals(beforeName, patientDataService.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is not in the database
     */
    private void thenAfterPatientNotInDatabase() {
        try {
            assertNotEquals(afterName, patientDataService.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a patient with the correct nhi and before first name is not in the database
     */
    private void thenBeforePatientNotInDatabase() {
        try {
            assertNotEquals(beforeName, patientDataService.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and after first name is in the database
     */
    private void thenAfterClinicianInDatabase() {
        assertEquals(afterName, clinicianDataService.getClinician(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and before first name is in the database
     */
    private void thenBeforeClinicianIndatabase() {
        assertEquals(beforeName, clinicianDataService.getClinician(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and after first name is not in the database
     */
    private void thenAfterClinicianNotIndatabase() {
        try {
            assertNotEquals(afterName, clinicianDataService.getClinician(staffId).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and before first name is not in the database
     */
    private void thenBeforeClinicianNotInDatabase() {
        try {
            assertNotEquals(beforeName, clinicianDataService.getClinician(staffId).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

}
