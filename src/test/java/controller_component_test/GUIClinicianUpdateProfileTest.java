package controller_component_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Clinician;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import javax.xml.crypto.Data;

@Ignore //todo
public class GUIClinicianUpdateProfileTest extends ApplicationTest {

    private int existingStaffId = Database.getNextStaffID();

    private int clinicianUpdateProfileTabIndex = 1;


    @Override
    public void start(Stage stage) throws IOException {
        new Main().start(stage);
    }


    @BeforeClass
    public static void setupClass() {
        userActions.setLevel(OFF);
    }


    @Before
    public void setup() {

        Database.resetDatabase();

        existingStaffId = Database.getNextStaffID();

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


    /**
     * Tests that the clinician can successfully edit their staff ID with a valid field
     */
    @Ignore //todo figure out if clinician should be able to change staff id or not
    @Test
    public void successfulUpdateClinicianId() throws InvalidObjectException {

        // Set ID to 1234567890
        givenStaffId("1234567890");
        whenClickSave();
        thenClinicianStaffIdIs(1234567890);

        //Set id back to what it was
        givenStaffId(String.valueOf(existingStaffId));
        whenClickSave();
        thenClinicianStaffIdIs(existingStaffId);
    }

    private void thenClinicianStaffIdIs(int newId) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(newId)
                .getStaffID()).isEqualTo(newId);
    }

    private void thenClinicianFirstNameIs(String newName) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getFirstName()).isEqualTo(newName);
    }

    private void thenClinicianLastNameIs(String newName) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getLastName()).isEqualTo(newName);
    }

    private void thenClinicianStreet1Is(String newStreet) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getStreet1()).isEqualTo(newStreet);
    }
    private void thenClinicianStreet2Is(String newStreet) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getStreet2()).isEqualTo(newStreet);
    }

    private void thenClinicianSuburbIs(String newSuburb) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getSuburb()).isEqualTo(newSuburb);
    }

    private void thenClinicianRegionIs(String newRegion) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getRegion()).isEqualTo(newRegion);
    }

    private void thenClinicianMiddleNamesIs(ArrayList<String> newMiddles) throws InvalidObjectException {
        assertThat(Database.getClinicianByID(existingStaffId)
                .getMiddleNames()).isEqualTo(newMiddles);
    }

    /**
     * Tests that that the clinician can successfully edit their street2 name with a valid field
     */
    @Test
    public void successfulUpdateClinicianStreet2() throws InvalidObjectException {
        givenStreet2("Riccarton RD");
        whenClickSave();
        thenClinicianStreet2Is("Riccarton RD");

        givenStreet2("122 Street");
        whenClickSave();
        thenClinicianStreet2Is("122 Street");
    }


    /**
     * Tests unsuccessful edit of clinician staff ID with an invalid field
     */
    @Test
    public void unsuccessfulUpdateClinicianId() throws InvalidObjectException {
        givenStaffId("A");
        whenClickSave();
        thenClinicianStaffIdIs(existingStaffId);
    }


    /**
     * Tests that that the clinician can successfully edit their first name with a valid field
     */
    @Test
    public void successfulUpdateClinicianFirstName() throws InvalidObjectException {
        givenFirstName("James");
        whenClickSave();
        thenClinicianFirstNameIs("James");
    }


    /**
     * Tests unsuccessful edit of clinician first name with an invalid field
     */
    @Test
    public void unsuccessfulUpdateClinicianFirstName() throws InvalidObjectException {

        String existingFirstName = Database.getClinicianByID(existingStaffId).getFirstName();

        givenFirstName("122");
        whenClickSave();
        thenClinicianFirstNameIs(existingFirstName);
    }


    /**
     * Tests that the clinician can successfully edit their last name with a valid field
     */
    @Test
    public void successfulUpdateClinicianLastName() throws InvalidObjectException {
        givenLastName("Bond");
        whenClickSave();
        thenClinicianLastNameIs("Bond");
    }


    /**
     * Tests unsuccessful edit of clinician last name with an invalid field
     */
    @Test
    public void unsuccessfulUpdateClinicianLastName() throws InvalidObjectException {

        String existingLastName = Database.getClinicianByID(existingStaffId).getLastName();

        givenLastName("122");
        whenClickSave();
        thenClinicianLastNameIs(existingLastName);
    }


    /**
     * Tests that the clinician can successfully edit their middle name with a valid field
     */
//    @Ignore //todo
    @Test
    public void successfulUpdateClinicianMiddleName() {
        givenMiddleName("Andre");
        whenClickSave();
        assertThat(Database.getClinicians()
                .stream()
                .min(Comparator.comparing(Clinician::getStaffID))
                .get()
                .getMiddleNames()
                .get(0)
                .equals("Andre"));
    }


    /**
     * Tests unsuccessful edit of clinician middle name with an invalid field
     */
    @Test
    public void unsuccessfulUpdateClinicianMiddleName() throws InvalidObjectException {

        ArrayList<String> existingMiddles = Database.getClinicianByID(existingStaffId).getMiddleNames();

        givenMiddleName("122");
        whenClickSave();
        thenClinicianMiddleNamesIs(existingMiddles);
    }


    /**
     * Tests that the clinician can successfully edit their street1 name with a valid field
     */
    @Test
    public void successfulUpdateClinicianStreet1() throws InvalidObjectException {
        givenStreet1("Riccarton RD");
        whenClickSave();
        thenClinicianStreet1Is("Riccarton RD");

        givenStreet1("122 Street");
        whenClickSave();
        thenClinicianStreet1Is("122 Street");
    }


    /**
     * Tests unsuccessful edit of clinician street1 with an invalid field
     */
//    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianStreet1() throws InvalidObjectException {

        String existingStreet1 = Database.getClinicianByID(existingStaffId).getStreet1();

        givenStreet1("@");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("#");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("$");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("!");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("%");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("^");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("&");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("*");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("(");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1(")");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("-");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);

        givenStreet1("{}[]_+-=\\|?><';\":`~");
        whenClickSave();
        thenClinicianStreet1Is(existingStreet1);
    }

    /**
     * Tests unsuccessful edit of clinician street2 with an invalid field
     */
//    @Ignore //todo
    @Test
    public void unsuccessfulUpdateClinicianStreet2() throws InvalidObjectException {

        String existingStreet2 = Database.getClinicianByID(existingStaffId).getStreet2();

        givenStreet2("@");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("#");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("$");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("!");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("%");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("^");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("&");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("*");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("(");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2(")");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("-");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);

        givenStreet2("{}[]_+-=\\|?><';\":`~");
        whenClickSave();
        thenClinicianStreet2Is(existingStreet2);
    }


    /**
     * Tests that that the clinician can successfully edit their suburb with a valid field
     */
    @Test
    public void successfulUpdateClinicianSuburb() throws InvalidObjectException {
        givenSuburb("Fendalton");
        whenClickSave();
        thenClinicianSuburbIs("Fendalton");
    }


    /**
     * Tests unsuccessful edit of clinician suburb with an invalid field
     */
    @Test
    public void unsuccessfulUpdateClinicianSuburb() throws InvalidObjectException {

        String existingSuburb = Database.getClinicianByID(existingStaffId).getSuburb();

        givenSuburb("122");
        whenClickSave();
        thenClinicianSuburbIs(existingSuburb);
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


    private void whenClickSave() {
        interact(() -> {
            lookup("#saveProfile").queryAs(Button.class)
                    .getOnAction()
                    .handle(new ActionEvent());
        });
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
