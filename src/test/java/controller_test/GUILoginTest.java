package controller_test;

import main.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
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
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import java.time.LocalDate;
import java.util.ArrayList;

public class GUILoginTest extends ApplicationTest {

<<<<<<< HEAD:src/test/java/gui_test/GUILoginTest.java
    Database database = Database.getDatabase();
=======
    /**
     * Application entry point
     */
>>>>>>> development:src/test/java/controller_test/GUILoginTest.java
    private Main main = new Main();


    /**
     * Login helper for getting logged in users
     */
    private UserControl loginHelper = new UserControl();

    @Override
    public void start(Stage stage) throws Exception {

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        database.add(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

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
        database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }


    /**
     * Verifies the the loginPane is visible
     */
    @Test
    public void should_open_on_login() {
        verifyThat("#loginPane", Node::isVisible);
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
    public void should_login() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            assertThat(lookup("#nhiLogin").queryAs(TextField.class)).hasText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        assertThat(((Patient)loginHelper.getLoggedInUser()).getNhiNumber().equals("TFX9999"));
        verifyThat("#homePane", Node::isVisible);
    }

    /**
     * Enter an incorrect NHI and verify that the login pane is still visible
     */
    @Test
    public void should_fail_login() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABD1234");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        assertThat(((Patient)loginHelper.getLoggedInUser()).getNhiNumber() == null);
        verifyThat("#loginPane", Node::isVisible);
    }

    /**
     * Enter no text and to the NHI field and verifies that the login screen is still visible
     */
    @Test
    public void should_fail_login_blank() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        assertThat(((Patient)loginHelper.getLoggedInUser()).getNhiNumber() == null);
        verifyThat("#loginPane", Node::isVisible);
    }

    /**
     * Verify that the register form is correctly displayed
     */
    @Test
    public void should_open_register_form() {
        interact(() -> lookup("#registerHyperlink").queryAs(Hyperlink.class).fire());
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Tests the invalid login with wrong staffId
     */
    @Test
    public void unsuccessfulClinicianLoginTest() {
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("111");
        });
        verifyThat("#nhiLogin", TextInputControlMatchers.hasText("111"));
        interact(() -> {
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat( "#loginPane", Node::isVisible ); // Verify that logout button has taken "user" to the login panel
    }


}
