package gui_test;

import controller.Main;
import controller.ScreenControl;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GUIHomeTest extends ApplicationTest {

    private Main main = new Main();


    @Override
    public void start(Stage stage) throws Exception {
        Database.resetDatabase();

        // add dummy donor
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addDonor(new Donor("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getDonorByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getDonorByNhi("TFX9999")
                .addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class)
                    .fire();
        });
    }


    @After
    public void waitForEvents() {
        Database.resetDatabase();
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
        assertThat(ScreenControl.getLoggedInDonor() == null);
        verifyThat("#loginPane", Node::isVisible);
    }


    @Test
    public void should_go_to_profile() {
        interact(() -> lookup("#profileButton").queryAs(Button.class)
                .fire());
        verifyThat("#donorProfilePane", Node::isVisible);
    }


    @Test
    public void should_go_to_log() {
        interact(() -> lookup("#historyButton").queryAs(Button.class)
                .fire());
        verifyThat("#donorLogPane", Node::isVisible);
    }

}
