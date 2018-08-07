package utility_test;

import DataAccess.interfaces.IPatientDataAccess;
import model.Clinician;
import model.Patient;
import model.User;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

import service.AdministratorDataService;
import service.ClinicianDataService;
import service.Database;
import service.PatientDataService;
import service.interfaces.IAdministratorDataService;
import service.interfaces.IClinicianDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.Action;

import java.io.InvalidObjectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    private IPatientDataService patientDataService = new PatientDataService();


    private static boolean validConnection = false;

    /**
     * Turns of logging before the test starts
     */
    @BeforeClass
    public static void setup() {
        userActions.setLevel(Level.OFF);
        validConnection = validateConnection();
    }

	private static boolean validateConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://mysql2.csse.canterbury.ac.nz:3306/seng302-2018-team800-test?allowMultiQueries=true", "seng302-team800", "ScornsGammas5531");
		} catch (SQLException e1) {
			System.err.println("Failed to connect to UC database server.");
		}
		if (conn == null) {
			return false;
		}
		return true;
	}

    /**
     * Resets the users and action attributes before each test
     */
    @Before
    public void reset() {
    	Assume.assumeTrue(validConnection);
        current = null;
        after = null;
        action = null;
    }

    /**
     * tests the action constructor using patients
     */
    @Test
    public void testPatientActionConstructor() throws InvalidObjectException{
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
        thenBeforePatientNotIndatabase();
    }

    /**
     * tests the action constructor using clinicians
     */
    @Test
    public void testClinicianActionConstructor() throws InvalidObjectException{
        givenEditedClinician();
        whenActionCreated();
        whenUserSaved();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenUserSaved();
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenUserSaved();
        thenBeforeClinicianNotIndatabase();
    }

//    /**
//     * tests the action unexecute method using patients
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
//        thenAfterPatientNotIndatabase();
//
//        reset();
//        givenDeletedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        thenBeforePatientIndatabase();
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
     * tests the action execute method using patients
     */
    @Test
    public void testPatientExecute() throws InvalidObjectException{
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
        thenBeforePatientNotIndatabase();
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
    public void testClinicianExecute() throws InvalidObjectException{
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
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        whenUserSaved();
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
        whenUserSaved();
        thenCurrentEqualsAfterClinician();

        reset();
        givenNewClinician();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenAfterClinicianIndatabase();

        reset();
        givenDeletedPatient();
        whenActionCreated();
        whenActionExecuted();
        whenUserSaved();
        thenBeforeClinicianNotIndatabase();
    }

//    /**
//     * tests the action unexecute method does nothing gracefully using patients
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
//        thenAfterPatientNotIndatabase();
//
//        reset();
//        givenDeletedPatient();
//        whenActionCreated();
//        whenActionUnexecuted();
//        whenActionUnexecuted();
//        thenBeforePatientIndatabase();
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
        assertEquals(afterName, patientDataService.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and before first name is in the database
     * @throws InvalidObjectException thrown when no patient with that nhi
     */
    private void thenBeforePatientIndatabase() throws InvalidObjectException {
        assertEquals(beforeName, patientDataService.getPatientByNhi(nhi).getFirstName());
    }

    /**
     * checks that a patient with the correct nhi and after first name is not in the database
     */
    private void thenAfterPatientNotIndatabase() {
        try {
            assertNotEquals(afterName, patientDataService.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a patient with the correct nhi and before first name is not in the database
     */
    private void thenBeforePatientNotIndatabase() {
        try {
            assertNotEquals(beforeName, patientDataService.getPatientByNhi(nhi).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    /**
     * checks that a clinician with the correct staffId and after first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenAfterClinicianIndatabase() throws InvalidObjectException {
        assertEquals(afterName, clinicianDataService.getClinician(staffId).getFirstName());
    }

    /**
     * checks that a clinician with the correct staffId and before first name is in the database
     * @throws InvalidObjectException thrown when no clinician with that staffId
     */
    private void thenBeforeClinicianIndatabase() throws InvalidObjectException {
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
    private void thenBeforeClinicianNotIndatabase() {
        try {
            assertNotEquals(beforeName, clinicianDataService.getClinician(staffId).getFirstName());
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

}
