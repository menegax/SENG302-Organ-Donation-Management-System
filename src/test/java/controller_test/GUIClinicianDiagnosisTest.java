package controller_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIClinicianDiagnosisTest extends ApplicationTest {

    private Main main = new Main();
    private Donor target;

    @Override
    public void start( Stage stage ) throws Exception {
        main.start( stage );
        target = Database.getDonorByNhi( "ABC1238" );
    }

    @Before
    /*
     * Tests logging in, navigating to diagnosis panel
     */
    public void LoginAndNaivgateToDiseasePanel() {
        // Log in to the app
        interact( () -> {
            lookup( "#nhiLogin" ).queryAs( TextField.class ).setText( "ABC1238" );
            assertThat( lookup( "#nhiLogin" ).queryAs( TextField.class ) ).hasText( "ABC1238" );
            lookup( "#loginButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#homePane", Node::isVisible ); // Verify that login has taken "user" to home panel
        target.setCurrentMedications( new ArrayList <>() );
        target.setMedicationHistory( new ArrayList <>() );
        // Navigate to the profile panel (where the medication test button is currently found)
        interact( () -> {
            lookup( "#goToProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile

        // Navigate to the diagnosis panel via the temporary test diagnosis button found in profile panel
        interact( () -> {
            lookup( "#diseaseHistoryButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible );  // Verify "user" has navigated to diseases
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }

    @Test
    /*
     * Tests entering a valid diagnosis to textfield and registering that diagnosis to current diseases table
     */
    public void testValidDiagnosisRegistration() {
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is in diagnosis panel
        // Verify that the diagnosis entry text field is empty prior to entering a new diagnosis for registration
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new diagnosis to textfield for registering to the current diagnosis registry
        interact( () -> {
            lookup( "#newDiagnosis" ).queryAs( TextField.class ).setText( "Aids" ); // Enters new diagnosis
        } );
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
        // Verify that the textfield currently has the entered diagnosis prior to registration being initiated
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "Aids" ) ) );
        // Verify that both of the tableViews have 1 entry as in the donor.json and no new entry has been registered
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Death curse"));
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Influenza"));


        verifyThat( "#currentDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );

        // Registers the new diagnosis entry in the textfield to the current disease class and tableView
        interact( () -> {
            lookup( "#addDiagnosisButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
        // Verify that the diagnosis entry text field is empty again after registering the entered diagnosis
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the current diseases tableView now has two entries after a new diagnosis is registered
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.containsRowAtIndex( 2, "Aids"));
        // Verify that there is two diagnosis' registered to current and one to past diagnosis tableViews
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.hasNumRows( 3 ) );
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );
    }

    @Test
    /*
     * Tests entering an invalid diagnosis to textfield and registering that diagnosis to current diagnosis tableView
     */
    public void testInvalidDiagnosisRegistration() {
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is in diagnosis panel
        // Verify that the diagnosis entry text field is empty prior to entering a new diagnosis for registration
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new diagnosis to textfield for registering to the current diagnosis registry
        interact( () -> {
            lookup( "#newDiagnosis" ).queryAs( TextField.class ).setText( "" ); // Enters new invalid diagnosis
        } );
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
        // Verify that the textfield currently has the entered diagnosis prior to registration being initiated
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that both of the tableViews have 1 entry as in the donor.json and no new entry has been registered
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Death curse"));
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Influenza"));
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );

        // Registers the new diagnosis entry in the textfield to the current disease class and tableView
        interact( () -> {
            lookup( "#addDiagnosisButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
        // Verify that the diagnosis entry text field is empty again after registering the entered diagnosis
        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the current diseases tableViews still only have one entry each after a new registration is denied
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Death curse"));
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.containsRowAtIndex( 1, "Influenza"));
        verifyThat( "#currentDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );
        verifyThat( "#pastDiagnosisCol", TableViewMatchers.hasNumRows( 2 ) );
    }
}
