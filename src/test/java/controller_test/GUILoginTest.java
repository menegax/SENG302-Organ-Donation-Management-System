package controller_test;



import main.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Clinician;
import model.Patient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import controller.UserControl;
import testfx.GitLabTestFXConfiguration;
import utility.GlobalEnums;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.Data;

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

    /**
     * Turn off logging and Sets the configuration to run in headless mode
     */
    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
        GitLabTestFXConfiguration.setHeadless();
    }

    /**
     * Reset db to a clean state wait for 1000ms
     */
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }


    @Test
    public void PatientValidCredentials() {
        givenCredentials(existingNhi);
        givenClinicianToggle(false);
        whenClickLogIn();
        thenHomePaneVisible();
    }



    /**
     * Verifies the the loginPane is visible
     */
    @Test
    public void PatientNonExistent() {
        givenCredentials("ABD1234");
        givenClinicianToggle(false);
        whenClickLogIn();
        thenNoPatientLoggedIn();
        thenAlertIsWarning();
    }

    /**
     * Clicks invalid button and expects exception to be thrown
     */
    @Test(expected = FxRobotException.class)
    public void click_on_wrong_button() {
        clickOn("#login");
    }

    /**
     * Enter NHI and verifies that the UI has logged in
     */
    @Test
    public void PatientBlankInput() {
        givenCredentials("");
        givenClinicianToggle(false);
        whenClickLogIn();
        thenNoPatientLoggedIn();
        thenAlertIsWarning();
    }


    /**
     * Enter an incorrect NHI and verify that the login pane is still visible
     */
    @Test
    public void ClinicianValidCredentials() {
        givenCredentials(String.valueOf(existingStaffId));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenHomePaneVisible();
    }


    /**
     * Enter no text and to the NHI field and verifies that the login screen is still visible
     */
    @Test
    public void ClinicianBlankInput() {
        givenCredentials(String.valueOf(""));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenAlertIsWarning();
    }


    /**
     * Verify that the register form is correctly displayed
     */
    @Test
    public void ClinicianNonExistent() {
        givenCredentials(String.valueOf(1234567890));
        givenClinicianToggle(true);
        whenClickLogIn();
        thenAlertIsWarning();
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


    private void thenNoPatientLoggedIn() {
        assertThat(userControl.isUserLoggedIn());
        //        assertThat(((Patient) userControl.getLoggedInUser()).getNhiNumber() == null);
    }


    private void thenHomePaneVisible() {
        verifyThat("#homePane", Node::isVisible);
    }


    /**
     * Ensures an alert exists with the title "Warning"
     *
     * <p>
     * The Alert is then closed by clicking a button with string "OK"
     * </p>
     */
    private void thenAlertIsWarning() {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene()
                .getRoot();
        assertEquals("Warning", dialogPane.getHeaderText());
        interact(() -> clickOn("OK"));
    }


    //todo doc
    private Stage getTopModalStage() {
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder()
                .listWindows());
        Collections.reverse(allWindows);
        return (Stage) allWindows.stream()
                .filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);
    }

}
