package gui_test;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import controller.UserControl;
import utility.GlobalEnums;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;

public class GUIHomeTest extends ApplicationTest {

    Database database = Database.getDatabase();
    private Main main = new Main();
    UserControl loginHelper = new UserControl();

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
        });
    }


    @After
    public void waitForEvents() {
        database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }


    @Test
    public void should_be_on_home_screen() {
        verifyThat("#homePane", Node::isVisible);
    }


    @Test
    public void should_logout() {
        interact(() -> {
            lookup("#logOutButton").queryAs(Button.class)
                    .fire();
        });
        assertThat((loginHelper.getLoggedInUser()) == null);
        verifyThat("#loginPane", Node::isVisible);
    }


    @Test
    public void should_go_to_profile() {
        interact(() -> lookup("#profileButton").queryAs(Button.class)
                .fire());
        verifyThat("#patientProfilePane", Node::isVisible);
    }


    @Test
    public void should_go_to_log() {
        interact(() -> {
            lookup("#historyButton").queryAs(Button.class).fire();
        });
        verifyThat("#patientLogPane", Node::isVisible);
    }

}
