package utility_test;

import model.Clinician;
import model.Patient;
import model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import utility.GlobalEnums;
import utility.undoRedo.Action;

import java.time.LocalDate;
import java.util.ArrayList;

public class ActionTest {

    private User current;

    private User after;

    private Action action;

    private String beforeName = "beforeChange";

    private String afterName = "afterChange";

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
     * tests the action constructor using patients
     */
    @Test
    public void testPatientActionConstructor() {
        givenEditedPatient();
        whenActionCreated();
        thenCurrentEqualsAfterPatient();
    }

    /**
     * tests the action constructor using clinicians
     */
    @Test
    public void testClinicianActionConstructor() {
        givenEditedClinician();
        whenActionCreated();
        thenCurrentEqualsAfterClinician();
    }

    /**
     * tests the action unexecute method using patients
     */
    @Test
    public void testPatientUnexecute() {
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        thenCurrentEqualsBeforePatient();
    }

    /**
     * tests the action unexecute method using clincians
     */
    @Test
    public void testClinicianUnexecute() {
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        thenCurrentEqualsBeforeClinician();
    }

    /**
     * tests the action execute method using patients
     */
    @Test
    public void testPatientExecute() {
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionExecuted();
        thenCurrentEqualsAfterPatient();
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
        thenCurrentEqualsAfterClinician();
    }

    /**
     * tests the action execute method does nothing gracefully using patients
     */
    @Test
    public void testUselessExecutePatient() {
        givenEditedPatient();
        whenActionCreated();
        whenActionExecuted();
        thenCurrentEqualsAfterPatient();
    }

    /**
     * tests the action execute method does nothing gracefully using clinicians
     */
    @Test
    public void testUselessExecuteClinician() {
        givenEditedClinician();
        whenActionCreated();
        whenActionExecuted();
        thenCurrentEqualsAfterClinician();
    }

    /**
     * tests the action unexecute method does nothing gracefully using patients
     */
    @Test
    public void testUselessUnexecutePatient() {
        givenEditedPatient();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenCurrentEqualsBeforePatient();
    }

    /**
     * tests the action unexecute method does nothing gracefully using clinicians
     */
    @Test
    public void testUselessUnexecuteClinician() {
        givenEditedClinician();
        whenActionCreated();
        whenActionUnexecuted();
        whenActionUnexecuted();
        thenCurrentEqualsBeforeClinician();
    }

    /**
     * sets up the current and after users as patients with different first names
     */
    private void givenEditedPatient() {
        current = new Patient("TST0001", beforeName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
        after = new Patient("TST0001", afterName, new ArrayList<>(), "test", LocalDate.of(2000, 1, 1));
    }

    /**
     * sets up the current and after users as clinicians with different first names
     */
    private void givenEditedClinician() {
        current = new Clinician(1, beforeName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
        after = new Clinician(1, afterName, new ArrayList<>(), "test", GlobalEnums.Region.CANTERBURY);
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
}
