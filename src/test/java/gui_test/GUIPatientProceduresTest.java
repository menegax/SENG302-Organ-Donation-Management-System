package gui_test;


import controller.Main;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * TestFX class to test the Patient Procedures screen
 */
public class GUIPatientProceduresTest extends ApplicationTest {

    private Main main = new Main();
    private Patient patient1;

    /**
     * Launches the main application
     *
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {
        // add dummy patient
        patient1 = new Patient("TFX9999", "Joe", new ArrayList<>(), "TestProceduresSearch1", LocalDate.of(1990, 2, 9));
        Database.addPatient(patient1);
        main.start(stage);
        //stage.setFullScreen(true); //This was causing tests to fail on MacOS
    }

    /**
     * Waits for all events to finish before moving on
     */
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void verifyProceduresNonEditableAsPatient() {
        givenPatientLoggedIn();
        assertFalse(lookup("#addProcedureButton").queryAs(Button.class).isVisible());
        assertFalse(lookup("#editProcedureButton").queryAs(Button.class).isVisible());
        assertFalse(lookup("#deleteProcedureButton").queryAs(Button.class).isVisible());

    }

    @Test
    public void verifyEditDeleteButtonsDisabledByDefault() {
        givenClinicianLoggedIn();
        assertTrue(lookup("#editProcedureButton").queryAs(Button.class).isDisabled());
        assertTrue(lookup("#deleteProcedureButton").queryAs(Button.class).isDisabled());
    }

    @Test
    public void verifyProceduresEditableAsClinician() {
        givenClinicianLoggedIn();
        assertTrue(lookup("#addProcedureButton").queryAs(Button.class).isVisible());
        assertTrue(lookup("#deleteProcedureButton").queryAs(Button.class).isVisible());
    }

    @Test
    public void verifyProceduresAssignedToCorrectTable() {

    }

    @Test
    public void addProcedure() {

    }

    private void givenClinicianLoggedIn() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("0");
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#searchPatients").queryAs(Button.class).fire();
        });
        sleep(500);
        interact(() -> {
            lookup("#searchEntry").queryAs(TextField.class).setText("TestProceduresSearch1");
        });
        interact(() -> {
            doubleClickOn("Joe TestProceduresSearch1");
        });
        interact(() -> {
            lookup("#proceduresButton").queryAs(Button.class).fire();
        });
    }

    private void givenPatientLoggedIn() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#proceduresButton").queryAs(Button.class).fire();
        });
    }
}
