package controller_component_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Clinician;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import controller.UserControl;
import utility.GlobalEnums;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GUILoginTest extends ApplicationTest {

    /**
     * Application entry point
     */
    private Main main = new Main();

    /**
     * Login helper for getting logged in users
     */
    private UserControl userControl = new UserControl();

    private String existingNhi = "TFX9999";

    private int existingStaffId = 0;


    @Override
    public void start(Stage stage) throws IOException {

        Database.resetDatabase();

        // add dummy patient
        Database.addPatient(new Patient(existingNhi,
                "Joe",
                new ArrayList<>(Collections.singletonList("Middle")),
                "Bloggs",
                LocalDate.of(1990, 2, 9)));

        // add dummy clinician
        Database.addClinician(new Clinician(existingStaffId, "initial", new ArrayList<String>() {{
            add("Middle");
        }}, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));

        main.start(stage);
    }


    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
        systemLogger.setLevel(OFF);
    }


    @After
    public void waitForEvents() {
        Database.resetDatabase();
        userControl.rmLoggedInUserCache(); // log out
    }


    /**
     * Ensures valid patient credentials results in successful login
     */
    @Test
    public void PatientValidCredentials() throws InvalidObjectException {
        givenCredentials(existingNhi);
        givenClinicianToggle(false);
        whenClickLogIn();
        thenLoggedInUserIs(Database.getPatientByNhi(existingNhi).getUuid());
    }


    /**
     * Ensures non-existent patient cannot be logged into
     */
    @Test
    public void PatientNonExistent() {
        givenCredentials("ABD1234");
        givenClinicianToggle(false);
        whenClickLogIn();
        thenNoUserLoggedIn();
    }


    /**
     * Ensures blank input cannot be used to log in with
     */
    @Test
    public void PatientBlankInput() {
        givenCredentials("");
        givenClinicianToggle(false);
        whenClickLogIn();
        thenNoUserLoggedIn();
    }


    /**
     * Ensure valid clinician login credentials result in successful log in
     */
    @Test
    public void ClinicianValidCredentials() throws InvalidObjectException {
        givenCredentials(String.valueOf(existingStaffId));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenHomePaneVisible();
        thenLoggedInUserIs(Database.getClinicianByID(existingStaffId).getUuid());
    }


    /**
     * Blank input considered invalid and no logging in
     */
    @Test
    public void ClinicianBlankInput() {
        givenCredentials(String.valueOf(""));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenNoUserLoggedIn();
    }


    /**
     * Ensures invalid clinician credentials result in no logged in user
     */
    @Test
    public void ClinicianNonExistent() {
        givenCredentials(String.valueOf(1234567890));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenNoUserLoggedIn();
    }


    private void givenClinicianToggle(boolean toggle) {
        lookup("#clinicianToggle").queryAs(CheckBox.class)
                .setSelected(toggle);
    }


    private void givenCredentials(String nhi) {
        lookup("#nhiLogin").queryAs(TextField.class)
                .setText(nhi);
    }


    private void whenClickLogIn() {
        interact(() -> lookup("#loginButton").queryAs(Button.class)
                .getOnAction()
                .handle(new ActionEvent()));
    }


    private void thenNoUserLoggedIn() {
        assertThat(userControl.isUserLoggedIn()).isFalse();
    }


    private void thenHomePaneVisible() {
        verifyThat("#homePane", Node::isVisible);
    }

    private void thenLoggedInUserIs(UUID existingUuid) {
        assertThat(userControl.getLoggedInUser().getUuid()).isEqualByComparingTo(existingUuid);
    }

}
