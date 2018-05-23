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
import utility.GlobalEnums;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test adding procedures
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
    public void start(Stage stage) throws Exception {
        Database.resetDatabase();
        main.start(stage);
        mainStage = stage;
        ArrayList<String> middle = new ArrayList<>();
        middle.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", middle, "Bloggs",
                LocalDate.of(2008, 2, 9)));
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        patient = Database.getPatientByNhi("TFX9999");
    }

    /**
     * Logs in a clinician and navigates to the procedures pane for testing procedure adding
     */
    @Before
    public void LoginAndNavigateToProceduresPane() {
        // Log in to the app
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("0");
            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        // Verify that login has taken "clinician" to home panel
        verifyThat("#clinicianHomePane", Node::isVisible);
        // Navigate to the profile panel (where the Manage procedures button is found)
        interact(() -> lookup("#searchPatients").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        // Verify that "clinician" has navigated to search pane
        verifyThat("#clinicianSearchPatientsPane", Node::isVisible);
        verifyThat("#patientDataTable", TableViewMatchers.hasTableCell("Joe Middle Bloggs"));
        interact(() -> {
            doubleClickOn("Joe Middle Bloggs");
        });
        verifyThat("#patientProfilePane", Node::isVisible);
        interact(() -> lookup("#proceduresButton").queryAs(Button.class).fire());
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
                interact(() -> ((Stage) window.getScene().getWindow()).close());
            }
        }

        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Closes any open context menus. Used to close the organ selection context menu
     */
    private void closeContextMenus() {
        for (Window window : listTargetWindows()) {
            if (window.getScene().getWindow() instanceof ContextMenu) {
                interact(() -> window.getScene().getWindow().hide());
            }
        }
    }

    /**
     * Tests that a procedure application is invalid when the procedure summary and date are null, no entries provided
     */
    @Test
    public void addInvalidNullAllProcedureTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));

        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());

        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Set the date to null
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(null));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is invalid when the procedure date is null, has no entry provided
     */
    @Test
    public void addInvalidNullProcedureDateTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));

        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());

        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter a new summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid summary"));

        // Enter a new description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid description"));
        // Set the date to null
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(null));

        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Valid summary")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid description")));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is invalid when the procedure summary is null, has no entry provided
     */
    @Test
    public void addInvalidNullSummaryTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));

        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter a null summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText(""));

        // Enter a new description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid description"));
        // Set the date to current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now()));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid description")));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is invalid when the procedure summary contains a special character
     */
    @Test
    public void addInvalidSummaryEntryWithSpecialCharTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());

        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter an invalid summary containing non-alphabet/numerical/-/space char to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("$ummary"));

        // Enter a valid description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid description"));

        // Set the date to current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now()));

        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("$ummary")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid description")));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is invalid when the procedure summary contains an empty string, ie. " "
     */
    @Test
    public void addInvalidEmptyStringSummaryTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter an invalid summary containing empty string to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText(" "));

        // Enter a valid description to textfield for adding a procedure. Descriptions aren't necessary for validation
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid description"));

        // Set the date to current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now()));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf(" ")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid description")));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is invalid when the procedure description includes invalid special characters
     */
    @Test
    public void addInvalidEmptyStringDescriptionTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());

        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid Summary"));

        // Enter an invalid empty string description to textfield for adding a procedure
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Inv@lid description"));

        // Set the date to current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now()));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Valid Summary")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Inv@lid description")));

        // Press the done button for adding the procedure, which should result in an alert stating invalid add
        interact(() -> lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        interact(() -> lookup("OK").queryAs(Button.class).fire());

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /***
     * Tests that a procedure application is invalid when the procedure date is before the patient's date of birth
     */
    @Test
    public void addInvalidProcedureDateBeforePatientDOBTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());

        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("")));

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid Summary"));

        // Enter an invalid empty string description to textfield for adding a procedure
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Description*"));

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

        // Closes the add procedure pane and returns to procedures listing pane
        interact(() -> lookup("#closePane").queryAs(Button.class).getOnAction().handle(new ActionEvent()));

        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a pending procedure application is valid when contains valid summary and current date
     */
    @Test
    public void addPendingProcedureWithValidStringAndCurrentDateTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid String 2"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Valid String 2")));
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid description"));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid description")));

        // Set the date to the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now()));
        interact(() -> lookup("#doneButton").queryAs(Button.class).fire());

        // Verify that the Appendectomy was added to the correct table
        verifyThat("#pendingProceduresView", TableViewMatchers.hasTableCell("Valid String 2"));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(1));
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests a previous procedure application is valid when contains valid summary and a past date after patient DOB
     */
    @Test
    public void addPreviousProcedureWithValidStringAndPastDateAfterPateintDOBTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid String 3"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Valid String 3")));
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid Desc"));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid Desc")));

        LocalDate dob = patient.getBirth();
        // Set the date to the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(dob.plus( Period.ofDays( 1 ) )));
        interact(() -> lookup("#doneButton").queryAs(Button.class).fire());

        // Verify that the Appendectomy was added to the correct table
        verifyThat("#previousProceduresView", TableViewMatchers.hasTableCell("Valid String 3"));
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(1));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests a pending procedure application is valid when contains valid summary and a future date
     */
    @Test
    public void addPendingProcedureWithValidStringAndFutureDateTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Valid String 4"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Valid String 4")));
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Valid Desc"));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Valid Desc")));

        // Set the date to the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now().plus( Period.ofDays( 1 ) )));
        interact(() -> lookup("#doneButton").queryAs(Button.class).fire());

        // Verify that the Appendectomy was added to the correct table
        verifyThat("#pendingProceduresView", TableViewMatchers.hasTableCell("Valid String 4"));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(1));
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
    }

    /**
     * Tests that a procedure application is valid when contains valid summary, past date and an affected donation organ
     */
    @Test
    public void addValidPreviousProcedureWithAffectedOrganTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Appendectomy"));

        // Enter an invalid empty string description to textfield for adding a procedure
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Removed appendix"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Appendectomy")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Removed appendix")));

        // Set the date to 1 day before the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now().minus(Period.ofDays(1))));

        // Open the organ selection menu
        interact(() -> lookup("#affectedInput").queryAs(MenuButton.class).fire());
        interact(() -> lookup("liver").queryAs(CheckBox.class).setSelected(true));
        // Close organ menu
        closeContextMenus();
        interact(() -> lookup("#doneButton").queryAs(Button.class).fire());

        // Verify that the Appendectomy was added to the correct table
        verifyThat("#previousProceduresView", TableViewMatchers.hasTableCell("Appendectomy"));
    }

    /**
     * Tests that a procedure application is valid when contains valid summary, future date and an affected donation organ
     */
    @Test
    public void addValidPendingProcedureWithAffectedDonationTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);
        // Verify that each of the previous and pending procedures tableViews are empty
        verifyThat("#previousProceduresView", TableViewMatchers.hasNumRows(0));
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(0));
        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Angiogram"));

        // Enter an invalid empty string description to textfield for adding a procedure
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Inspected blood vessels around the heart"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Angiogram")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Inspected blood vessels around the heart")));

        // Set the date to 1 day after the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now().plus(Period.ofDays(1))));

        // Open the organ selection menu
        interact(() -> lookup("#affectedInput").queryAs(MenuButton.class).fire());
        interact(() -> lookup("liver").queryAs(CheckBox.class).setSelected(true));
        // Close organ menu
        closeContextMenus();
        interact(() -> lookup("#doneButton").queryAs(Button.class).fire());

        // Verify that the Angiogram was added to the correct table
        verifyThat("#pendingProceduresView", TableViewMatchers.hasTableCell("Angiogram"));
    }

    /**
     * Tests that a cancelled procedure application will not affect the state of either procedures tables
     */
    @Test
    public void cancelOutOfAddProcedureFormTest() {
        // Verify "clinician" has navigated to procedures
        verifyThat("#patientProceduresPane", Node::isVisible);

        int numPendingProcedures = lookup("#pendingProceduresView").queryAs(TableView.class).getItems().size();

        interact(() -> lookup("#addProcedureButton").queryAs(Button.class).fire());
        // Verify "clinician" has navigated to add procedures
        verifyThat("#procedureAnchorPane", Node::isVisible);

        // Enter a valid summary to textfield for adding a procedure
        interact(() -> lookup("#summaryInput").queryAs(TextField.class).setText("Heart Surgery"));

        // Enter an invalid empty string description to textfield for adding a procedure
        interact(() -> lookup("#descriptionInput").queryAs(TextArea.class).setText("Operate on the heart"));
        verifyThat("#summaryInput", TextInputControlMatchers.hasText(String.valueOf("Heart Surgery")));
        verifyThat("#descriptionInput", TextInputControlMatchers.hasText(String.valueOf("Operate on the heart")));

        // Set the date to 1 day after the current date
        interact(() -> lookup("#dateInput").queryAs(DatePicker.class).setValue(LocalDate.now().plus(Period.ofDays(1))));

        // Open the organ selection menu
        interact(() -> lookup("#affectedInput").queryAs(MenuButton.class).fire());
        interact(() -> lookup("liver").queryAs(CheckBox.class).setSelected(true));
        // Close organ menu
        closeContextMenus();
        interact(() -> lookup("#closePane").queryAs(Button.class).fire());

        // Verify that no new procedure was added to the table
        verifyThat("#pendingProceduresView", TableViewMatchers.hasNumRows(numPendingProcedures));
    }
}

