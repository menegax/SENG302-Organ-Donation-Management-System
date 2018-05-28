package controller_test;

import main.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import testfx.GitLabTestFXConfiguration;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

public class GUIProfileTest extends ApplicationTest {

    Database database = Database.getDatabase();

    /**
     * Application entry point
     */
    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        database.resetDatabase();

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        database.add(new Patient("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
        database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.LIVER);
        database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
    }

    /**
     * Turn off logging and set the configuration to run in headless mode
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
    public void waitForEvents(){
        database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Verify that the profile screen is displayed
     */
    @Test
    public void should_be_on_profile_screen() {
        verifyThat("#patientProfilePane", Node::isVisible);
    }


    /**
     * Verify that the contacts screen displays when clicking the contacts button
     */
    @Test
    public void should_enter_contact_details_pane() {
        interact(() -> { lookup("#contactButton").queryAs(Button.class).fire();});
        verifyThat("#patientContactsPane", Node::isVisible);
    }

    /**
     * Verify that the donations screen displays when clicking the donations button
     */
    @Test
    public void should_go_to_donations() {
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
    }


    /**
     * Verify the donations of the patient
     */
    @Test
    public void should_have_correct_donations() {
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
    }
}
