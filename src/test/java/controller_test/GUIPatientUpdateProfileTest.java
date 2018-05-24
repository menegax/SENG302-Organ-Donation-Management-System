package controller_test;

import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static utility.UserActionHistory.userActions;

import main.Main;
import javafx.scene.Node;
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
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

public class GUIPatientUpdateProfileTest extends ApplicationTest {

    /**
     * Application entry point
     */
    private Main main = new Main();


    @Override
    public void start(Stage stage) throws Exception {

        Database.resetDatabase();

        // add dummy patients
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.CORNEA);

        Database.addPatient(new Patient("TFX9998", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));


        main.start(stage);

        // log in
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class)
                    .fire();
            lookup("#profileButton").queryAs(Button.class)
                    .fire();
        });

        // go to edit pane
        interact(() -> lookup("#editPatientButton").queryAs(Button.class)
                .fire());
        verifyThat("#patientUpdateAnchorPane", Node::isVisible);
    }


    /**
     * Turn off logging
     */
    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
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
     * Checks that a patients NHI cannot be updated to one with an invalid format
     */
    @Test
    public void testInvalidNhi() {
        // try changing to an invalid nhi
        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("999abcd");
            lookup("#saveButton").queryAs(Button.class).fire();
            lookup("OK").queryAs(Button.class).fire(); // closes alert
        });

        verifyThat("#patientUpdateAnchorPane", Node::isVisible);

    }

    /**
     * Checks that a patients NHI cannot be updated to an existing one
     */
    @Test
    public void testDuplicateNhi() {
    // try editing to an nhi that's already taken

        interact(() -> {
            lookup("#nhiTxt").queryAs(TextField.class).setText("TFX9998"); // nhi already in use
            lookup("#saveButton").queryAs(Button.class).fire();
            lookup("OK").queryAs(Button.class).fire(); // closes alert
        });

        verifyThat("#patientUpdateAnchorPane", Node::isVisible);

    }

}
