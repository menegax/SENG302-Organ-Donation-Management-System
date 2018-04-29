package gui_test;

import controller.Main;
import controller.ScreenControl;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIProfileTest extends ApplicationTest {

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
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
        });
    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void should_be_on_profile_screen() {
        verifyThat("#donorProfilePane", Node::isVisible);
    }

    @Test
    public void should_enter_edit_pane() {
        interact(() -> lookup("#editDonorButton").queryAs(Button.class).fire());
        verifyThat("#donorUpdateAnchorPane", Node::isVisible);
    }

    @Test
    public void should_enter_contact_details_pane() {
        interact(() -> lookup("#contactButton").queryAs(Button.class).fire());
        verifyThat("#donorContactsPane", Node::isVisible);
    }

    @Test
    public void should_go_to_donations() {
        interact(() -> lookup("#donationsButton").queryAs(Button.class).fire());
        verifyThat("#donorDonationsAnchorPane", Node::isVisible);
    }

    @Test
    public void should_have_correct_donor_details() {
        //Made around default donor in the system with NHI of ABC1238
        assertThat(lookup("#nhiLbl").queryAs(Label.class)).hasText(ScreenControl.getLoggedInDonor().getNhiNumber());
        assertThat(lookup("#nameLbl").queryAs(Label.class)).hasText(ScreenControl.getLoggedInDonor().getNameConcatenated());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate birth = LocalDate.parse(lookup("#dobLbl").queryAs(Label.class).getText(), formatter);
        assertThat(birth == LocalDate.of(1990, 2, 9));
    }

    @Test
    public void should_have_correct_donations() {
        interact(() -> lookup("#donationsButton").queryAs(Button.class).fire());
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
    }

}
