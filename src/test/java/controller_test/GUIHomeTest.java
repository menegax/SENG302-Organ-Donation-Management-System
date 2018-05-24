package controller_test;

import main.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import controller.UserControl;
import testfx.GitLabTestFXConfiguration;
import utility.GlobalEnums;

import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import java.time.LocalDate;
import java.util.ArrayList;

public class GUIHomeTest extends ApplicationTest {


    /**
     * Application entry point
     */
    private Main main = new Main();

    /**
     * Login helper for getting logged in users
     */
    private UserControl loginHelper = new UserControl();

    /**
     * Turn off logging
     */
    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Database.resetDatabase();

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
        });
    }

    /**
     * Sets the configuration to run in headless mode
     */
    @BeforeClass
    static public void setHeadless() {
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


    /**
     * Verifies the current view pane as the current one
     */
    @Test
    public void should_be_on_home_screen() {
        verifyThat("#homePane", Node::isVisible);
    }


    /**
     * Verifies that the UI has logged out
     */
    @Test
    public void should_logout() {
        interact(() -> {
            lookup("#logOutButton").queryAs(Button.class)
                    .fire();
        });
        assertThat((loginHelper.getLoggedInUser()) == null);
        verifyThat("#loginPane", Node::isVisible);
    }


    /**
     * Clicks the profile button and verifies that the profile pane is shown
     */
    @Test
    public void should_go_to_profile() {
        interact(() -> lookup("#profileButton").queryAs(Button.class)
                .fire());
        verifyThat("#patientProfilePane", Node::isVisible);
    }


    /**
     * Clicks the history button and then verifies the current pane is the history pane
     */
    @Test
    public void should_go_to_log() {
        interact(() -> {
            lookup("#historyButton").queryAs(Button.class).fire();
        });
        verifyThat("#patientLogPane", Node::isVisible);
    }

}
