package gui_test;

import controller.Main;
import controller.ScreenControl;
import controller.UserControl;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import javax.xml.crypto.Data;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIProfileTest extends ApplicationTest {

    private UserControl userControl;

    private Main main = new Main();

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
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
    }

    @After
    public void waitForEvents() throws InvalidObjectException {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    @Test
    public void should_be_on_profile_screen() {
        verifyThat("#patientProfilePane", Node::isVisible);
    }

    /*@Test
    public void should_enter_edit_pane() {
        interact(() -> { lookup("#editPatientButton").queryAs(Button.class).fire();});
        verifyThat("#patientUpdateAnchorPane", Node::isVisible);
    }*/

    @Test
    public void should_enter_contact_details_pane() {
        interact(() -> { lookup("#contactButton").queryAs(Button.class).fire();});
        verifyThat("#patientContactsPane", Node::isVisible);
    }

    @Test
    public void should_go_to_donations() {
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
    }


    @Test
    public void should_have_correct_donations() {
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
    }
}
