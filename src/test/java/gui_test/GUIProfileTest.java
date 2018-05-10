package gui_test;

import controller.Main;
import controller.ScreenControl;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIProfileTest extends ApplicationTest {

    private Main main = new Main();
    @Override
    public void start(Stage stage) throws Exception {

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText("ABC1238");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
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
        verifyThat("#patientProfilePane", Node::isVisible);
    }

    @Test
    public void should_enter_edit_pane() {
        interact(() -> { lookup("#editPatientButton").queryAs(Button.class).fire();});
        verifyThat("#patientUpdateAnchorPane", Node::isVisible);
    }

    @Test
    public void should_enter_contact_details_pane() {
        interact(() -> { lookup("#contactButton").queryAs(Button.class).fire();});
        verifyThat("#patientContactsPane", Node::isVisible);
    }

    @Test
    public void should_go_to_donations() {
        interact(() -> { lookup("#donationButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
    }

//    @Test
//    public void should_have_correct_patient_details() {
//        // Made around default patient in the system with NHI of ABC1238
//        assertThat(lookup("#nhiLbl").queryAs(Label.class)).hasText("TFX9999");
//        assertThat(lookup("#nameLbl").queryAs(Label.class)).hasText(ScreenControl.getLoggedInPatient().getNameConcatenated());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
//        LocalDate birth = LocalDate.parse(lookup("#dobLbl").queryAs(Label.class).getText(), formatter);
//        assertThat(birth == LocalDate.of(1990, 2, 9));
//    }

    @Test
    public void should_have_correct_donations() {
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
    }

    @Test
    public void check_receiver_donor_segregation() {
        Patient patient = ScreenControl.getLoggedInPatient();
        patient.setDonations(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
        verifyThat("#receivingList", Node::isVisible);
        verifyThat("#donationList", Node::isDisabled);
        patient.setRequiredOrgans(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#receivingList", Node::isDisabled);
        verifyThat("#donationList", Node::isVisible);
    }

}
