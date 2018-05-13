package gui_test;


import com.sun.javafx.scene.control.behavior.DatePickerBehavior;
import com.sun.javafx.scene.control.skin.DatePickerContent;
import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Clinician;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the Adding of procedures screen
 */
public class GUIAddProcedureTest extends ApplicationTest {

    private Main main = new Main();
    private Stage mainStage;
    private Clinician clinician;
    private Patient patient;

    @Override
    public void start( Stage stage ) throws Exception {
        Database.resetDatabase();
        main.start( stage );
        mainStage = stage;
        ArrayList <String> middle = new ArrayList <>();
        middle.add( "Middle" );
        Database.addPatient( new Patient( "TFX9999", "Joe", middle, "Bloggs", LocalDate.of( 2008, 2, 9 ) ) );
        patient = Database.getPatientByNhi( "TFX9999" );
    }

    /**
     * Logs in a clinician and navigates to the procedures pane for testing procedure adding
     */
    @Before
    public void LoginAndNavigateToMedicationPanel() {
        // Log in to the app
        interact( () -> {
            lookup( "#nhiLogin" ).queryAs( TextField.class ).setText( "0" );
            lookup( "#clinicianToggle" ).queryAs( CheckBox.class ).setSelected( true );
            lookup( "#loginButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that login has taken "clinician" to home panel
        verifyThat( "#clinicianHomePane", Node::isVisible );
        // Navigate to the profile panel (where the Manage procedures button is found)
        interact( () -> {
            lookup( "#searchPatients" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that "clinician" has navigated to search pane
        verifyThat( "#clinicianSearchPatientsPane", Node::isVisible );
        verifyThat( "#patientDataTable", TableViewMatchers.hasTableCell( "Joe Middle Bloggs" ) );
        interact( () -> {
            doubleClickOn( "Joe Middle Bloggs" );
        } );
        verifyThat( "#patientProfilePane", Node::isVisible );
        interact( () -> {
            lookup( "#proceduresButton" ).queryAs( Button.class ).fire();
        } );
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
    }

    @After
    public void waitForEvents() {
        Database.resetDatabase();
        for (Window window : listTargetWindows()) {
            if (window.getScene().getWindow() != mainStage) {
                interact( () -> {
                    ((Stage) window.getScene().getWindow()).close();
                } );
            }
        }

        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }

    @Test
    public void addInvalidNullAllProcedure() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows( 0 ));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows( 0 ));
        interact( () -> {
            lookup( "#addProcedureButton" ).queryAs( Button.class ).fire();
        } );
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#descriptionInput", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        interact( () -> {
            // Press the done button for adding the procedure, which should result in an alert stating invalid add
            lookup( "#doneButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );

        interact( () -> {
            lookup("OK").queryAs(Button.class).fire();
        });

        interact( () -> {
            // Closes the add procedure pane and returns to procedures listing pane
            lookup( "#closePane" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows( 0 ));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows( 0 ));
    }

    @Test
    public void addInvalidNullDateProcedure() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows( 0 ));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows( 0 ));
        interact( () -> {
            lookup( "#addProcedureButton" ).queryAs( Button.class ).fire();
        } );
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#descriptionInput", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new summary to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( "Valid summary" );
        } );

        // Enter a new description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Valid description" );
        } );
        // Don't add any date
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "Valid summary" ) ) );
        verifyThat( "#descriptionInput", TextInputControlMatchers.hasText( String.valueOf( "Valid description" ) ) );

        interact( () -> {
            // Press the done button for adding the procedure, which should result in an alert stating invalid add
            lookup( "#doneButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );

        interact( () -> {
            lookup("OK").queryAs(Button.class).fire();
        });

        interact( () -> {
            // Closes the add procedure pane and returns to procedures listing pane
            lookup( "#closePane" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows( 0 ));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows( 0 ));
    }
}

