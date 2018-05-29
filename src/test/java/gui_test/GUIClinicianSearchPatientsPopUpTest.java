package gui_test;

import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.xml.crypto.Data;

//todo rename to just serarh patients test not popup
public class GUIClinicianSearchPatientsPopUpTest extends ApplicationTest {

    private Main main = new Main();

    private String existingStaffId = "0";
    private String existingPatientNhi1 = "ABC1238";
    private String existingPatientNhi2 = "ABC1234";

    private int clinicianSearchPatientsTabIndex = 1;

    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);

        Database.resetDatabase();

        // add dummy users
        Database.addClinician(new Clinician(Integer.valueOf(existingStaffId), "initial", null, "clinician", "Creyke RD", "Ilam RD", "ILAM", GlobalEnums.Region.CANTERBURY));
        Database.addPatient(new Patient(existingPatientNhi1, "Joe", null, "Bloggs", LocalDate.of(1990, 2, 9)));
        Database.addPatient(new Patient(existingPatientNhi2, "Jane", null, "Doe", LocalDate.of(1990, 2, 9)));

    }

    @BeforeClass
    public static void setUpClass() {
        userActions.setLevel(OFF);
    }

    @Before
    public void setUpGoToSearchPatients() {
        interact( () -> {

            //log in
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#nhiLogin").queryAs(TextField.class).setText(existingStaffId);
            lookup( "#loginButton" ).queryButton().fire();

            // goto search patients
            lookup("#horizontalTabPane").queryAs(TabPane.class)
                    .getSelectionModel()
                    .clearAndSelect(clinicianSearchPatientsTabIndex);
        });
    }

    /**
     * Tests the pop up for table row double-clicking
     */
    @Ignore //todo: convert
    @Test
    public void successfulPopUpNavigation() {


        //Todo rework -- it passes even if nothing is double clicked

        // double-click to get a pop up
        interact( () -> {
            lookup( "#patientDataTable" ).queryAs(TableView.class).getSelectionModel().select(0);
//            doubleClickOn( "#patientDataTable" ).doubleClickOn();
        });

        TabPane patientProfileTabPane = (TabPane) lookup("#homePane").queryAs(BorderPane.class).getCenter();
        verifyThat(patientProfileTabPane, Node::isVisible);

    }
}

