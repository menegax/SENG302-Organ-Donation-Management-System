package gui_test;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class GUIProfileTest extends ApplicationTest {

    private Main main = new Main();

    private String existingNhi = "TFX9999";

    private Patient existingPatient = new Patient(existingNhi, "Joe", new ArrayList<>(Collections.singletonList("Mile")),"Bloggs", LocalDate.of(1989, 2, 9));

    @Override
    public void start(Stage stage) throws Exception {

        // add dummy patient
        Database.addPatient(existingPatient);
        Database.getPatientByNhi(existingNhi).addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi(existingNhi).addDonation(GlobalEnums.Organ.CORNEA);

        main.start(stage);

        // navigate to profile
        interact(() ->  {
            lookup("#nhiLogin").queryAs(TextField.class).setText(existingNhi);
            lookup("#loginButton").queryAs(Button.class).fire();
            verifyThat("#patientUpdateAnchorPane", Node::isVisible);
        });
    }

    @Ignore //Todo
    @Test
    public void CheckPatientDetails() {
        // Made around default patient in the system with NHI of ABC1238
        assertThat(lookup("#nhiLbl").queryAs(Label.class)).hasText(existingNhi);
        assertThat(lookup("#nameLbl").queryAs(Label.class)).hasText(existingPatient.getNameConcatenated());
        // todo other labels...

        LocalDate birth = LocalDate.parse(lookup("#dobLbl").queryAs(Label.class).getText());
        assertThat(birth.isEqual(existingPatient.getBirth()));
    }

    // todo move this isn't in profile scene. can change test to verify list of organs are in the donations table
    @Ignore //Todo
    @Test
    public void CheckDonations() {
        interact(() -> lookup("#donationButton").queryAs(Button.class).fire());
        assertThat(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertThat(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
    }

}
