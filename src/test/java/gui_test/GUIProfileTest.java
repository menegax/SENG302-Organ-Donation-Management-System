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
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIProfileTest extends ApplicationTest {

    private Main main = new Main();
    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
        // add dummy patient
        Patient patient = Database.getPatientByNhi("TFX9999");
        if (patient == null) {
            ArrayList<String> dal = new ArrayList<>();
            dal.add("Middle");
//        WaitForAsyncUtils.waitForFxEvents();
//        sleep(2000);
            Database.addPatient(new Patient("TFX9999", "Joe", dal, "Bloggs", LocalDate.of(1990, 2, 9)));
//        WaitForAsyncUtils.waitForFxEvents();
//        sleep(2000);
            Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
            Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);
        }
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
    }

    @After
    public void waitForEvents() throws InvalidObjectException {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
//        try {
//        Database.removePatient("TFX9999");
//        Database.resetDatabase();
//        } catch (InvalidObjectException e) {
//            throw new InvalidObjectException(e.getMessage());
//        }
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
        interact(() -> { lookup("#donationsButton").queryAs(Button.class).fire();});
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
        patient.addDonation(GlobalEnums.Organ.LIVER);
        patient.addDonation(GlobalEnums.Organ.CORNEA);
        patient.setRequiredOrgans(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#donatingTitle", Node::isVisible);
        verifyThat("#receivingList", Node::isDisabled);
//        System.out.println(patient.getDonations());
        patient.addRequired(GlobalEnums.Organ.LIVER);
        patient.addRequired(GlobalEnums.Organ.CORNEA);
        patient.setDonations(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#receivingList", Node::isVisible);
        verifyThat("#donationList", Node::isDisabled);
    }

}
