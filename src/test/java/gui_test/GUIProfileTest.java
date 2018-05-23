package gui_test;

import static java.util.logging.Level.OFF;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import controller.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import javax.xml.crypto.Data;
import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class GUIProfileTest extends ApplicationTest {

    private UserControl userControl;

    private Main main = new Main();

    private String existingNhi = "TFX9999";

    private Patient existingPatient = new Patient(existingNhi, "Joe", new ArrayList<>(Collections.singletonList("Mile")),"Bloggs", LocalDate.of(1989, 2, 9));

    @Override
    public void start(Stage stage) throws IOException {

        main.start(stage);

    }

    @BeforeClass
    public static void setUpClass() {
        userActions.setLevel(OFF);
    }

    @Before
    public void setUp() throws InvalidObjectException {
        Database.resetDatabase();

        // add dummy patient
        Database.addPatient(existingPatient);
        Database.getPatientByNhi(existingNhi).addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi(existingNhi).addDonation(GlobalEnums.Organ.CORNEA);
        Database.getPatientByNhi(existingNhi).setGender(GlobalEnums.Gender.OTHER);
        Database.getPatientByNhi(existingNhi).setDeath(LocalDate.now().minusDays(1));
        Database.getPatientByNhi(existingNhi).setHeight(180.3);
        Database.getPatientByNhi(existingNhi).setWeight(400.9);
        Database.getPatientByNhi(existingNhi).setBloodGroup(GlobalEnums.BloodGroup.A_POSITIVE);

        // navigate to profile
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText(existingNhi);
            lookup("#loginButton").queryAs(Button.class).fire();
        });
    }

    @Test
    public void CheckPatientDetails() {
        assertThat(lookup("#nhiLbl").queryAs(Label.class)).hasText(existingNhi);
        assertThat(lookup("#nameLbl").queryAs(Label.class)).hasText(existingPatient.getNameConcatenated());
        assertThat(lookup("#genderLbl").queryAs(Label.class)).hasText(existingPatient.getGender().toString());
        assertThat(lookup("#dobLbl").queryAs(Label.class)).hasText(existingPatient.getBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertThat(lookup("#dateOfDeathLabel").queryAs(Label.class)).hasText(existingPatient.getDeath().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertThat(lookup("#age").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getAge()));
        assertThat(lookup("#heightLbl").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getHeight()) + " m");
        assertThat(lookup("#age").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getAge()));
        assertThat(lookup("#weightLbl").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getWeight()) + " kg");
        assertThat(lookup("#bmi").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getBmi()));
        assertThat(lookup("#bloodGroupLbl").queryAs(Label.class)).hasText(existingPatient.getBloodGroup().toString());
    }

    @Test
    public void CheckLists() {

        // todo make these asserts actually be enforced
        assertThat(lookup("#organList").queryListView().getItems().containsAll(existingPatient.getDonations()));
        assertThat(lookup("#medList").queryListView().getItems().containsAll(existingPatient.getMedicationHistory()));
    }

    @Test
    public void check_receiver_donor_segregation() throws InvalidObjectException {
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);
        Database.getPatientByNhi("TFX9999").setRequiredOrgans(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#donatingTitle", Node::isVisible);
        verifyThat("#receivingList", Node::isDisabled);
//        System.out.println(patient.getDonations());
        Database.getPatientByNhi("TFX9999").addRequired(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addRequired(GlobalEnums.Organ.CORNEA);
        Database.getPatientByNhi("TFX9999").setDonations(new ArrayList<GlobalEnums.Organ>());
        interact(() -> {
            lookup("#donationsButton").queryAs(Button.class).fire();
            lookup("#save").queryAs(Button.class).fire();
        });
        verifyThat("#receivingList", Node::isVisible);
        verifyThat("#donationList", Node::isDisabled);
    }

}
