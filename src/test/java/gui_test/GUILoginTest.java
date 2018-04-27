package gui_test;



import controller.Main;
import controller.ScreenControl;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;

public class GUILoginTest extends ApplicationTest {


    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {

        // add dummy donor
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addDonor(new Donor("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getDonorByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getDonorByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void should_open_on_login() {
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test(expected = FxRobotException.class)
    public void click_on_wrong_button() {
        clickOn("#login");
    }

    @Test
    public void should_login() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            assertThat(lookup("#nhiLogin").queryAs(TextField.class)).hasText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        assertThat(ScreenControl.getLoggedInDonor().getNhiNumber().equals("TFX9999"));
        verifyThat("#homePane", Node::isVisible);
    }

    @Test
    public void should_fail_login() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABD1234");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        assertThat(ScreenControl.getLoggedInDonor().getNhiNumber() == null);
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_fail_login_blank() {
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        assertThat(ScreenControl.getLoggedInDonor().getNhiNumber() == null);
        verifyThat("#loginPane", Node::isVisible);
    }

    @Test
    public void should_open_register_form() {
        interact(() -> lookup("#registerLabel").queryAs(Hyperlink.class).fire());
        verifyThat("#registerPane", Node::isVisible);
    }


}
