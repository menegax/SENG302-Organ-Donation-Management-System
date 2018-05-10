package controller_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import static org.testfx.api.FxAssert.verifyThat;

public class ControllerClinicianTest extends ApplicationTest {
    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    /**
     * Tests logging in as a clinician successfully
     */
    public void successfulLoginTestandGoToProfile() {
        System.out.println(Database.getClinicians().size());
        System.out.println(Database.getClinicians().get(0).getFirstName());
        System.out.println(Database.getClinicians().get(0).getStaffID());
        //Check 'I am Clinician" checkbox to login as clinician
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("1");
        });
        verifyThat("#nhiLogin", TextInputControlMatchers.hasText("1"));
        interact(() -> {
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        verifyThat("#clinicianHomePane", Node::isVisible); // Verify that login has taken "user" to the clinician home panel
        interact(() -> {
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        verifyThat("#clinicianProfilePane", Node::isVisible); // Verify that login has taken "user" to the clinician profile panel
    }

    @Test
    /*
     * Tests the invalid login with wrong staffId
     */
    public void unsuccessfulLoginTest() {
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText("111");
        });
        verifyThat("#nhiLogin", TextInputControlMatchers.hasText("111"));
        interact(() -> {
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        verifyThat( "#loginPane", Node::isVisible ); // Verify that logout button has taken "user" to the login panel
    }
}
