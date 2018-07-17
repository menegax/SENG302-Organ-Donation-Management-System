package controller_component_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Patient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIRegisterTest extends ApplicationTest {

    private Main main = new Main();


    /**
     * Reset db to a clean state
     */
    @Before
    public void setup() {
        Database.resetDatabase();
    }


    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);

        // navigate to register scene
        interact(() -> lookup("#registerHyperlink").queryAs(Hyperlink.class)
                .fire());

    }


    /**
     * Turn off logging and sets the configuration to run in headless mode
     */
    @BeforeClass
    public static void setUp() {
        TestHelper.setLoggingFalse();
        TestHelper.setTestFXHeadless();
    }


    /**
     * Verify the register screen is visible
     */
    @Test
    public void verify_screen_register() {
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }


    @Test
    public void AllFields() {
        givenNhi("BBB2222");
        givenValidFirstName();
        givenValidMiddleNames();
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Message");
        thenPatientInDb("BBB2222", true);
    }


    /**
     * Checks that the user has registered successfully register with a middle name
     */
    @Test
    public void NoMiddleName() {
        givenNhi("BBB2222");
        givenValidFirstName();
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Message");
        thenPatientInDb("BBB2222", true);
    }


    /**
     * Checks that the user has registered successfully register without a middle name
     */
    @Test
    public void NoInput() {
        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with no input
     */
    @Test
    public void NoNhi() {
        givenValidFirstName();
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with no nhi
     */
    @Test
    public void InvalidNhi() {
        givenInvalidNhi();
        givenValidFirstName();
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with invalid nhi
     */
    @Test
    public void NoFirstName() {
        givenNhi("BBB2222");
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with no firstname
     */
    @Test
    public void NoLastName() {
        givenNhi("BBB2222");
        givenValidFirstName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with no last name
     */
    @Test
    public void NoBirthDate() {
        givenNhi("BBB2222");
        givenValidFirstName();
        givenValidLastName();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }

    /**
     * Tests to ensure a patient cannot register with a birth date in the future
     */

    @Test
    public void InvalidBirthDate() {
        givenInvalidNhi();
        givenValidFirstName();
        givenValidLastName();
        givenInvalidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    /**
     * Checks that the user has not been registered with duplicate nhi
     */
    @Test
    public void DuplicateNhi() {

        Patient existingPatient = new Patient("TFX9999", "Joe", new ArrayList<>(Collections.singletonList("Middle")), "Bloggs", LocalDate.of(1990, 2, 9));
        Database.addPatient(existingPatient);

        givenNhi("TFX9999");
        givenValidFirstName();
        givenValidLastName();
        givenValidBirthDate();
        whenClickDone();
        thenAlertHasHeader("Warning");

        thenPatientInDb("TFX9999", true); // just the first one

        assertThat(Collections.frequency(Database.getPatients(), existingPatient)).isEqualTo(1); // exactly one occurrence of existing patient in db
    }


    /**
     * Ensures an invalid first name is not used to register a new patient
     */
    @Test
    public void InvalidFirstName() {
        givenNhi("BBB2222");
        givenInvalidFirstName();
        givenValidMiddleNames();
        givenValidLastName();
        givenValidBirthDate();

        whenClickDone();

        thenAlertHasHeader("Warning");
        thenPatientInDb("BBB2222", false);
    }


    private void givenInvalidFirstName() {
        lookup("#firstnameRegister").queryAs(TextField.class)
                .setText("@");
    }


    private void givenNhi(String nhi) {
        interact(() -> lookup("#nhiRegister").queryAs(TextField.class)
                .setText(nhi));
    }


    private void givenValidFirstName() {
        interact(() -> lookup("#firstnameRegister").queryAs(TextField.class)
                .setText("William"));
    }


    private void givenValidLastName() {
        interact(() -> lookup("#lastnameRegister").queryAs(TextField.class)
                .setText("Williamson"));
    }


    private void givenValidBirthDate() {
        interact(() -> lookup("#birthRegister").queryAs(DatePicker.class)
                .setValue(LocalDate.of(1957, 6, 21)));
    }


    private void givenValidMiddleNames() {
        interact(() -> lookup("#middlenameRegister").queryAs(TextField.class)
                .setText("Wil"));
    }


    private void givenInvalidBirthDate() {
        lookup("#birthRegister").queryAs(DatePicker.class)
                .setValue(LocalDate.now()
                        .plusDays(1)); // register with bday of tomorrow
    }


    private void givenInvalidNhi() {
        lookup("#nhiRegister").queryAs(TextField.class)
                .setText("2222bbb");
    }


    private void whenClickDone() {
        interact(() -> lookup("#doneButton").queryAs(Button.class)
                .getOnAction()
                .handle(new ActionEvent()));
    }

    private void thenPatientInDb(String nhi, boolean isIn) {
        assertThat(Database.isPatientInDb(nhi)).isEqualTo(isIn);
    }


    /**
     * Ensures a given Alert has the expected header
     *
     * <p>
     * The Alert popup is then closed by clicking a button with string "OK"
     * </p>
     *
     * @param expectedHeader the string header that is the title of the Alert
     */
    private void thenAlertHasHeader(String expectedHeader) {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene()
                .getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
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
