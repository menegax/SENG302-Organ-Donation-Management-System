package controller_test;

import main.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import testfx.GitLabTestFXConfiguration;

import static org.testfx.api.FxAssert.verifyThat;
import static utility.UserActionHistory.userActions;

import java.util.logging.Level;

public class ControllerClinicianTest extends ApplicationTest {
    Database database = Database.getDatabase();
    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
    }

    /**
     * Sets the configuration to run in headless mode
     */
    @BeforeClass
    static public void setHeadless() {
        GitLabTestFXConfiguration.setHeadless();
    }

    /**
     * Turn off logging
     */
    @Before
    public void setUp() {
        userActions.setLevel(Level.OFF);
        main = new Main();
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
     * Tests the invalid login with wrong staffId
     */
    @Test
    public void unsuccessfulLoginTest() {
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
