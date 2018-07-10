package gui_test;

import controller.ScreenControl;
import controller.UserControl;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;
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

    Database database = Database.getDatabase();
    private UserControl userControl;

    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        database.resetLocalDatabase();

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
            lookup("#profileButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
    }

    @After
    public void waitForEvents() throws InvalidObjectException {
        database.resetLocalDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
//        try {
//        database.removePatient("TFX9999");
//        database.resetDatabase();
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
    public void check_receiver_donor_segregation() throws InvalidObjectException {
        database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);
        database.getPatientByNhi("TFX9999").setRequiredOrgans(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#donatingTitle", Node::isVisible);
        verifyThat("#receivingList", Node::isDisabled);
//        System.out.println(patient.getDonations());
        database.getPatientByNhi("TFX9999").addRequired(GlobalEnums.Organ.LIVER);
        database.getPatientByNhi("TFX9999").addRequired(GlobalEnums.Organ.CORNEA);
        database.getPatientByNhi("TFX9999").setDonations(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#receivingList", Node::isVisible);
        verifyThat("#donationList", Node::isDisabled);
    }

}
