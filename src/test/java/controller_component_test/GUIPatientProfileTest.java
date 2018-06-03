package controller_component_test;

import static java.util.logging.Level.OFF;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import controller.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Medication;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class GUIPatientProfileTest extends ApplicationTest {

    private int patientProfileTabIndex = 0;

    private String existingNhi = "TFX9999";

    private Patient existingPatient =
            new Patient(existingNhi, "Joe", new ArrayList<>(Collections.singletonList("Mile")), "Bloggs", LocalDate.of(1989, 2, 9));


    @Override
    public void start(Stage stage) throws Exception {
        userActions.setLevel(OFF);
        new Main().start(stage);
    }


    @Before
    public void setUp() throws InvalidObjectException {
        Database.resetDatabase();

        // add dummy patient
        Database.addPatient(existingPatient);
        Database.getPatientByNhi(existingNhi)
                .addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi(existingNhi)
                .addDonation(GlobalEnums.Organ.CORNEA);
        Database.getPatientByNhi(existingNhi)
                .setPreferredGender(GlobalEnums.PreferredGender.NONBINARY);
        Database.getPatientByNhi(existingNhi)
                .setDeath(LocalDate.now()
                        .minusDays(1));
        Database.getPatientByNhi(existingNhi)
                .setHeight(180.3);
        Database.getPatientByNhi(existingNhi)
                .setWeight(400.9);
        Database.getPatientByNhi(existingNhi)
                .setBloodGroup(GlobalEnums.BloodGroup.A_POSITIVE);
        Database.getPatientByNhi(existingNhi).getCurrentMedications().add(new Medication("Maggi noodles"));
        Database.getPatientByNhi(existingNhi).getCurrentMedications().add(new Medication("Alcohol"));

        // navigate to profile
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText(existingNhi);
            lookup("#loginButton").queryAs(Button.class)
                    .fire();
            lookup("#horizontalTabPane").queryAs(TabPane.class)
                    .getSelectionModel()
                    .clearAndSelect(patientProfileTabIndex);
        });
    }


    /**
     * Checks each of the patient's details to ensure they are correct
     */
    @Test
    public void correctPatientDetails() {
        assertThat(lookup("#nhiLbl").queryAs(Label.class)).hasText(existingNhi);
        assertThat(lookup("#nameLbl").queryAs(Label.class)).hasText(existingPatient.getNameConcatenated());
        assertThat(lookup("#genderStatus").queryAs(Label.class)).hasText(existingPatient.getPreferredGender()
                .toString());
        assertThat(lookup("#dobLbl").queryAs(Label.class)).hasText(existingPatient.getBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertThat(lookup("#dateOfDeathLabel").queryAs(Label.class)).hasText(existingPatient.getDeath()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertThat(lookup("#age").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getAge()));
        assertThat(lookup("#heightLbl").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getHeight()) + " m");
        assertThat(lookup("#age").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getAge()));
        assertThat(lookup("#weightLbl").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getWeight()) + " kg");
        assertThat(lookup("#bmi").queryAs(Label.class)).hasText(String.valueOf(existingPatient.getBmi()));
        assertThat(lookup("#bloodGroupLbl").queryAs(Label.class)).hasText(existingPatient.getBloodGroup()
                .toString());
    }


    /**
     * Ensures the shown donations list is correct
     */
    @Test
    public void correctDonationsList() {

        // convert from list of organs to capitalized strings
        ArrayList<String> donationsString = new ArrayList<>();
        for (GlobalEnums.Organ donation : existingPatient.getDonations()) {
            donationsString.add(StringUtils.capitalize(donation.toString()));
        }

        assertThat(lookup("#donationList").queryListView().getItems())
                .isEqualTo(donationsString);
    }


    /**
     * Ensures the shown medications list is correct
     */
    @Test
    public void correctMedicationsList() {

        // convert from list of organs to capitalized strings
        ArrayList<String> medicationsString = new ArrayList<>();
        for (Medication medication : existingPatient.getCurrentMedications()) {
            medicationsString.add(StringUtils.capitalize(medication.toString()));
        }

        assertThat(lookup("#medList").queryListView().getItems())
                .isEqualTo(medicationsString);
    }
}
