//package controller_test;
//
//import controller.Main;
//import javafx.event.ActionEvent;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import model.Patient;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;;
//import org.testfx.matcher.control.TableViewMatchers;
//import org.testfx.matcher.control.TextInputControlMatchers;
//import org.testfx.util.WaitForAsyncUtils;
//import service.Database;
//
//import java.util.ArrayList;
//
//import static org.testfx.api.FxAssert.verifyThat;
//import static org.testfx.assertions.api.Assertions.assertThat;
//
//public class GUIClinicianDiagnosisTest extends ApplicationTest {
//
//    private Main main = new Main();
//    private Patient target;
//
//    @Override
//    public void start( Stage stage ) throws Exception {
//        main.start( stage );
//        target = Database.getPatientByNhi( "ABC1238" );
//    }
//
//    @Before
//    /*
//     * Tests logging in, navigating to diagnosis panel
//     */
//    public void LoginAndNavigateToDiseasePanel() {
//        // Log in to the app
//        interact( () -> {
//            lookup( "#nhiLogin" ).queryAs( TextField.class ).setText( "ABC1238" );
//            assertThat( lookup( "#nhiLogin" ).queryAs( TextField.class ) ).hasText( "ABC1238" );
//            lookup( "#loginButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#homePane", Node::isVisible ); // Verify that login has taken "user" to home panel
//        target.setCurrentMedications( new ArrayList <>() );
//        target.setMedicationHistory( new ArrayList <>() );
//        // Navigate to the profile panel (where the medication test button is currently found)
//        interact( () -> {
//            lookup( "#goToProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile
//
//        // Navigate to the diagnosis panel via the temporary test diagnosis button found in profile panel
//        interact( () -> {
//            lookup( "#diseaseHistoryButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible );  // Verify "user" has navigated to diseases
//    }
//
//    @After
//    public void waitForEvents() {
//        WaitForAsyncUtils.waitForFxEvents();
//        sleep( 1000 );
//    }
//
//    /**
//     * Tests entering a valid diagnosis to textfield and registering that diagnosis to current diseases table
//     */
//    @Test
//    public void testValidDiagnosisRegistration() {
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is in diagnosis panel
//        // Verify that the diagnosis entry text field is empty prior to entering a new diagnosis for registration
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new diagnosis to textfield for registering to the current diagnosis registry
//        interact( () -> {
//            lookup( "#newDiagnosis" ).queryAs( TextField.class ).setText( "Aids" ); // Enters new diagnosis
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the textfield currently has the entered diagnosis prior to registration being initiated
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "Aids" ) ) );
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#pastDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//
//        // Registers the new diagnosis entry in the textfield to the current disease class and tableView
//        interact( () -> {
//            lookup( "#addDiagnosisButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the diagnosis entry text field is empty again after registering the entered diagnosis
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the current diseases tableView now has two entries after a new diagnosis is registered
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 1, "DATE", "Aids", null)); // it's a little complicated here
//        //verifyThat( "#pastDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        // Verify that there is two diagnosis' registered to current and one to past diagnosis tableViews
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 2 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//    }
//
//    /**
//     * Tests entering an invalid diagnosis to textfield and registering that diagnosis to current diagnosis tableView
//     */
//    @Test
//    public void testInvalidDiagnosisRegistration() {
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is in diagnosis panel
//        // Verify that the diagnosis entry text field is empty prior to entering a new diagnosis for registration
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new diagnosis to textfield for registering to the current diagnosis registry
//        interact( () -> {
//            lookup( "#newDiagnosis" ).queryAs( TextField.class ).setText( "" ); // Enters new invalid diagnosis
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the textfield currently has the entered diagnosis prior to registration being initiated
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#pastDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//
//        // Registers the new diagnosis entry in the textfield to the current disease class and tableView
//        interact( () -> {
//            lookup( "#addDiagnosisButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the diagnosis entry text field is empty again after registering the entered diagnosis
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the current diseases tableViews still only have one entry each after a new registration is denied
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#pastDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//    }
//
//    /**
//     * Tests entering an invalid diagnosis to textfield and registering that diagnosis to current diagnosis tableView
//     */
//    @Test
//    public void testPastDiagnosisRegistration() {
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is in diagnosis panel
//        // Verify that the diagnosis entry text field is empty prior to entering a new diagnosis for registration
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new diagnosis to textfield for registering to the current diagnosis registry
//        interact( () -> {
//            lookup( "#newDiagnosis" ).queryAs( TextField.class ).setText( "influenza" ); // Enters new invalid diagnosis
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the textfield currently has the entered diagnosis prior to registration being initiated
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "influenza" ) ) );
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#pastDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 1 ) );
//
//        // Registers the new diagnosis entry in the textfield to the current disease class and tableView
//        interact( () -> {
//            lookup( "#addDiagnosisButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#clinicianDiagnosesPane", Node::isVisible ); // Verify "user" is still in diagnosis panel
//        // Verify that the diagnosis entry text field is empty again after registering the entered diagnosis
//        verifyThat( "#newDiagnosis", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the current diseases tableViews still only have one entry each after a new registration is denied
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 0, "2018-04-28", "Death curse", "chronic")); // it's a little complicated here
//        //verifyThat( "#currentDiagnosesView", TableViewMatchers.containsRowAtIndex( 1, "2018-04-28", "Influenza", "cured")); // it's a little complicated here
//        verifyThat( "#currentDiagnosesView", TableViewMatchers.hasNumRows( 2 ) );
//        verifyThat( "#pastDiagnosesView", TableViewMatchers.hasNumRows( 0 ) );
//    }
//}
