package controller_component_test;

import static java.util.logging.Level.OFF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static utility.UserActionHistory.userActions;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GUIPatientUpdateProfileTest extends ApplicationTest {

    private Main main = new Main();

    private int patientUpdateProfileTabIndex = 1;

    private Patient existingPatient1 = new Patient("TFX9999", "Joe", null, "Bloggs", LocalDate.of(1990, 2, 9));

    private String existingNhi1 = existingPatient1.getNhiNumber();


    @Override
    public void start(Stage stage) throws Exception {

        Database.resetDatabase();

        // add dummy patients
        Database.addPatient(existingPatient1);


        main.start(stage);

        //Log in and navigate to profile update
        interact(() -> {
            lookup("#clinicianToggle").queryAs(CheckBox.class)
                    .setSelected(false);
            lookup("#nhiLogin").queryAs(TextField.class)
                    .setText(String.valueOf(existingNhi1));
            lookup("#loginButton").queryButton()
                    .fire();
            lookup("#horizontalTabPane").queryAs(TabPane.class)
                    .getSelectionModel()
                    .clearAndSelect(patientUpdateProfileTabIndex);
        });
    }

    /**
     * Turn off logging
     */
    @BeforeClass
    public static void setUpClass() {
        Database.resetDatabase();
        userActions.setLevel(OFF);
    }


    /**
     * Checks that a patients NHI cannot be updated to one with an invalid format
     */
    @Test
    public void testInvalidNhi() throws InvalidObjectException {

        givenNhi("999abcd");
        whenSave();
        thenDBPatientHasNhi(existingNhi1);

    }


    /**
     * Checks that a patients NHI cannot be updated to an existing one
     */
    @Test
    public void testDuplicateNhi() throws InvalidObjectException {

        // add second patient to db
        Database.addPatient(new Patient("TFX9998", "Jane", null, "ZEreraeDA", LocalDate.of(1876, 12, 31)));

        givenNhi("TFX998");
        whenSave();
        thenDBPatientHasNhi(existingNhi1);

    }

    //todo add test for valid nhi, firstname, last name...




    private void thenDBPatientHasNhi(String nhi) throws InvalidObjectException {
        assertThat(Database.getPatientByNhi(existingNhi1).getNhiNumber()).isEqualTo(nhi);
    }


    private void whenSave() {
        interact(() -> lookup("#saveButton").queryAs(Button.class).fire());
    }


    private void givenNhi(String newNhi) {
        interact(() -> lookup("#nhiTxt").queryAs(TextField.class).setText(newNhi));
    }

}
