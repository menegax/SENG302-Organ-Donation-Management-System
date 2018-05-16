package gui_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the Adding of procedures screen
 */
public class GUIAddProcedureTest extends ApplicationTest {

    private Main main = new Main();
    private Stage mainStage;
    private Patient patient;

    /**
     * Resets database, and creates a new patient specifically for the testing
     * @param stage Scene for a GUI window
     * @throws Exception Throws an exception
     */
    @Override
    public void start( Stage stage ) throws Exception {
        Database.resetDatabase();
        main.start( stage );
        mainStage = stage;
        ArrayList <String> middle = new ArrayList <>();
        middle.add( "Middle" );
        Database.addPatient( new Patient( "TFX9999", "Joe", middle, "Bloggs",
                LocalDate.of( 2008, 2, 9 ) ) );
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
        verifyThat( "#patientDataTable", TableViewMatchers.hasTableCell( "Willis Brucie" ) );
        interact( () -> {
            doubleClickOn( "Willis Brucie" );
        } );
        verifyThat( "#patientProfilePane", Node::isVisible );
        interact( () -> {
            lookup( "#proceduresButton" ).queryAs( Button.class ).fire();
        } );
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
    }

    /**
     * An interrupt for testing
     */
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

    /**
     * Tests that a procedure application is invalid when the procedure summary and date are null, no entries provided
     */
    @Test
    public void addInvalidNullAllProcedureTest() {
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

        // Set the date to null
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( null );
        } );

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

    /**
     * Tests that a procedure application is invalid when the procedure date is null, has no entry provided
     */
    @Test
    public void addInvalidNullProcedureDateTest() {
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
        // Set the date to null
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( null );
        } );
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

    /**
     * Tests that a procedure application is invalid when the procedure summary is null, has no entry provided
     */
    @Test
    public void addInvalidNullSummaryTest() {
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

        // Enter a null summary to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( "" );
        } );

        // Enter a new description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Valid description" );
        } );
        // Set the date to current date
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( LocalDate.now() );
        } );
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
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

    /**
     * Tests that a procedure application is invalid when the procedure summary contains a special character
     */
    @Test
    public void addInvalidSummaryEntryWithSpecialCharTest() {
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

        // Enter an invalid summary containing non-alphabet/numerical/-/space char to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( "$ummary" );
        } );

        // Enter a valid description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Valid description" );
        } );
        // Set the date to current date
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( LocalDate.now() );
        } );
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "$ummary" ) ) );
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

    /**
     * Tests that a procedure application is invalid when the procedure summary contains an empty string, ie. " "
     */
    @Test
    public void addInvalidEmptyStringSummaryTest() {
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

        // Enter an invalid summary containing empty string to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( " " );
        } );

        // Enter a valid description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Valid description" );
        } );
        // Set the date to current date
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( LocalDate.now() );
        } );
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( " " ) ) );
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

    /**
     * Tests that a procedure application is invalid when the procedure description includes invalid special characters
     */
    @Test
    public void addInvalidDescriptionEntryWithSpecialCharTest() {
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

        // Enter a valid summary to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( "Valid Summary" );
        } );

        // Enter an invalid empty string description to textfield for adding a procedure
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Description*" );
        } );
        // Set the date to current date
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( LocalDate.now() );
        } );
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "Valid Summary" ) ) );
        verifyThat( "#descriptionInput", TextInputControlMatchers.hasText( String.valueOf( "Description*" ) ) );

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

    /***
     * Tests that a procedure application is invalid when the procedure date is before the patient's date of birth
     */
    @Test
    public void addInvalidProcedureDateBeforePatientDOBTest() {
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

        // Enter a valid summary to textfield for adding a procedure
        interact( () -> {
            lookup( "#summaryInput" ).queryAs( TextField.class ).setText( "Valid Summary" );
        } );

        // Enter an invalid empty string description to textfield for adding a procedure
        interact( () -> {
            lookup( "#descriptionInput" ).queryAs( TextArea.class ).setText( "Description*" );
        } );

        LocalDate dob = patient.getBirth();

        // Set the date to 1 day before the DOB of the patient the procedure is being created for
        interact( () -> {
            lookup( "#dateInput").queryAs( DatePicker.class ).setValue( dob.minus(Period.ofDays(1)));
        } );
        verifyThat( "#summaryInput", TextInputControlMatchers.hasText( String.valueOf( "Valid Summary" ) ) );
        verifyThat( "#descriptionInput", TextInputControlMatchers.hasText( String.valueOf( "Description*" ) ) );

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

