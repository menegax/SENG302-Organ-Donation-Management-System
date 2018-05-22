package gui_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Clinician;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

public class GUIClinicianUpdateProfileTest extends ApplicationTest {

    private Main main = new Main();

    private int existingStaffId = Database.getNextStaffID();

    private int clinicianUpdateProfileTabIndex = 2;


    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
    }


    @BeforeClass
    public static void setupClass() {
        userActions.setLevel(OFF);
    }


    @Before
    public void setup() {

        Database.resetDatabase();

        // add dummy clinician
        Database.addClinician(new Clinician(existingStaffId, "initial", new ArrayList<String>() {{
            add("Middle");
        }}, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));

        //Log in as clinician and navigate to profile update
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class)
                    .setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText(String.valueOf(existingStaffId));
            lookup("#loginButton").queryButton()
                    .fire();
            lookup("#horizontalTabPane").queryAs(TabPane.class)
                    .getSelectionModel()
                    .clearAndSelect(clinicianUpdateProfileTabIndex);
        });
    }


    //todo rm
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
    }


    /**
     * Tests that the clinician can successfully edit their staff ID with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianId() {

        // Set ID to 1234567890
        givenStaffId("1234567890");
        whenClickSaveAndOk();
        thenOnClinicianProfilePane();

        //Set id back to what it was
        givenStaffId(String.valueOf(existingStaffId));
        whenClickSaveAndOk();
        thenOnClinicianProfilePane();
    }


    /**
     * Tests that that the clinician can successfully edit their street2 name with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianStreet2() throws InvalidObjectException {

        String newStreet2 = "Hanrahan RD";

        givenStreet2(newStreet2);
        whenClickSaveAndOk();
        assertThat(Database.getClinicianByID(existingStaffId)
                .getStreet2()
                .equals(newStreet2)); //todo does nothing
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician staff ID with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianId() {
        givenStaffId("A");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getStaffID() == 1);
        thenAlertIsWarning();
    }


    /**
     * Tests that that the clinician can successfully edit their first name with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianFirstName() {
        givenFirstName("James");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getFirstName()
                .equals("James"));
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician first name with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianFirstName() {
        givenFirstName("122");
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getFirstName()
                .equals("12"));
        thenAlertIsWarning();
    }


    /**
     * Tests that the clinician can successfully edit their last name with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianLastName() {
        givenLastName("Bond");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getLastName()
                .equals("Bond"));
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician last name with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianLastName() {
        givenLastName("122");
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getLastName()
                .equals("12"));
        thenAlertIsWarning();
    }


    /**
     * Tests that the clinician can successfully edit their middle name with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianMiddleName() {
        givenMiddleName("Andre");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getMiddleNames()
                .get(0)
                .equals("Andre"));
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician middle name with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianMiddleName() {
        givenMiddleName("122");
        verifyThat("#middlenameTxt", TextInputControlMatchers.hasText("122"));
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getMiddleNames()
                .get(0)
                .equals("12"));
        thenAlertIsWarning();
    }


    /**
     * Tests that the clinician can successfully edit their street1 name with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianStreet1() {
        givenStreet1("Riccarton RD");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getStreet1()
                .equals("Riccarton RD"));
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician street1 with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianStreet1() {
        givenStreet1("122 RD");
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getStreet1()
                .equals("12 RD"));
        thenAlertIsWarning();
    }


    /**
     * Tests unsuccessful edit of clinician street2 with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianStreet2() {
        givenStreet2("122 RD");
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getStreet1()
                .equals("12 RD"));
        thenAlertIsWarning();
    }


    /**
     * Tests that that the clinician can successfully edit their suburb with a valid field
     */
    @Ignore //todo
    @Test
    public void successfulUpdateClinicianSuburb() {
        givenSuburb("Fendalton");
        whenClickSaveAndOk();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getSuburb()
                .equals("Fendalton"));
        thenOnClinicianProfilePane();
    }


    /**
     * Tests unsuccessful edit of clinician suburb with an invalid field
     */
    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianSuburb() {
        givenSuburb("122");
        whenClickSaveAndOk();
        assertThat(!Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getStreet1()
                .equals("12 RD"));
        thenAlertIsWarning();
    }


    private void givenStaffId(String staffId) {
        interact(() -> {
            lookup("#staffId").queryAs(TextField.class)
                    .setText(staffId);
        });
    }


    private void givenFirstName(String invalidFirstName) {
        interact(() -> {
            lookup("#firstnameTxt").queryAs(TextField.class)
                    .setText(invalidFirstName);
        });
    }


    private void givenMiddleName(String newMiddleName) {
        interact(() -> {
            lookup("#middlenameTxt").queryAs(TextField.class)
                    .setText(newMiddleName);
        });
    }


    private void givenLastName(String newLastName) {
        interact(() -> {
            lookup("#lastnameTxt").queryAs(TextField.class)
                    .setText(newLastName);
        });
    }


    private void givenStreet1(String newStreet1) {
        interact(() -> {
            lookup("#street1Txt").queryAs(TextField.class)
                    .setText(newStreet1);
        });
    }


    private void givenStreet2(String newStreet2) {
        interact(() -> {
            lookup("#street2Txt").queryAs(TextField.class)
                    .setText(newStreet2);
        });
    }


    private void givenSuburb(String newSuburb) {
        interact(() -> {
            lookup("#suburbTxt").queryAs(TextField.class)
                    .setText(newSuburb);
        });
    }


    private void whenClickSaveAndOk() {
        interact(() -> {
            lookup("#saveProfile").queryAs(Button.class)
                    .getOnAction()
                    .handle(new ActionEvent());
            lookup("OK").queryAs(Button.class)
                    .fire();
        });
    }


    private void thenOnClinicianProfilePane() {
        verifyThat("#clinicianProfilePane", Node::isVisible);
    }


    /**
     * Ensures an alert exists with the title "Warning"
     *
     * <p>
     * The Alert popup is then closed by clicking a button with string "OK"
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
