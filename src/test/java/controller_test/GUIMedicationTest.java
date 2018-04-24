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
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIMedicationTest extends ApplicationTest {

    private Main main = new Main();
    private Donor target;

    @Override
    public void start( Stage stage ) throws Exception {
        main.start( stage );
        target = Database.getDonorByNhi( "ABC1238" );
    }

    @Before
    /*
     * Tests logging in, navigating to medication panel
     */
    public void LoginAndNaivgateToMedicationPanel() {
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

        // Navigate to the medication panel via the temporary test medication button found in profile panel
        interact( () -> {
            lookup( "#testMedication" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible );  // Verify "user" has navigated to medications
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }

    @Test
    /*
     * Tests entering a valid medication to textfield and registering that medication to currentMedications listView
     */
    public void testValidMedicationRegistration() {
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Steroids" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Steroids" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Steroids" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests entering an invalid medication to textfield and registering that medication to currentMedications listView
     */
    public void testInvalidMedicationRegistration() {
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is still empty as a medication has not been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests the selecting of a single medication in the currentMedications listView
     */
    public void testSelectingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Medicine" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Medicine" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medicine" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Medicine" );
        } );
        // Currently unsure how test other than visual observation - which is passing
    }

    @Test
    /*
     * Tests moving a single medication from the currentMedications listView to the pastMedications listView
     */
    public void testRemovingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Pills" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Pills" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Pills" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Pills" );
        } );

        interact( () -> {
            // Press the remove medication button for moving the selected medication from current to past medications
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Pills" ) );
        // Verify that there is only one medication in pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty again after medication moved to pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests moving a single medication from the pastMedications listView to the currentMedications listView
     */
    public void testAddingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Placebo" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Placebo" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Placebo" );
        } );

        interact( () -> {
            // Press the remove medication button for moving the selected medication from current to past medications
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Placebo" );
        } );

        interact( () -> {
            // Press the add medication button for moving the selected medication from past to current medications
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the currentMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one medication moved to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests can't register medication if duplicate medication already registered to either current or past medications
     */
    public void testInvalidDuplicateRegistration() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "duplicateMed" ); // Enters new med
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicateMed" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "duplicateMed" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "duplicateMed" ); // Enters new med
        } );
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicateMed" ) ) );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "duplicateMed" ) );
        // Verify that there is only one single medication in the currentMedications list and not two duplicates
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "duplicateMed" );
        } );

        interact( () -> {
            // Press the remove medication button for moving the selected medication from current to past medications
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "duplicateMed" ) );
        // Verify that there is only one single medication in the pastMedications list and not two duplicates
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "duplicateMed" ); // Enters new med
        } );
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicateMed" ) ) );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "duplicateMed" ) );
        // Verify that there is only one single medication in the pastMedications list and not two duplicates
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests deleting a single medication from the pastMedications listView and from the medicationHistory ArrayList
     */
    public void testValidDeletingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Codeine" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Codeine" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Codeine" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Codeine" );
        } );

        interact( () -> {
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Codeine" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "Codeine" );
        } );

        interact( () -> {
            // Press the delete medication button for deleting the selected medication
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Tests that a duplicate medication CAN be registered ONLY AFTER the already registered duplicate is DELETED
     */
    public void testValidDuplicateRegistrationAfterDeletion() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "validDuplicate" ); // Enters new med
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "validDuplicate" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "validDuplicate" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "validDuplicate" );
        } );

        interact( () -> {
            // Press the remove medication button for moving the selected medication from current to past medications
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "validDuplicate" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "validDuplicate" );
        } );

        interact( () -> {
            // Press the delete medication button for deleting the selected medication
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "validDuplicate" ); // Enters new med
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "validDuplicate" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "validDuplicate" ) );
        // Verify that there is only one single medication in the currentMedications list and not two duplicates
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    @Test
    /*
     * Enter seven more new medications to textfield and register them to current ArrayList and listView
     */
    public void testValidMultipleMedicationRegistrations() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Morphine" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Aspirin" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 2 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadol" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 3 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadeine" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadeine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 4 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-biotic" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 5 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-psychotics" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-psychotics" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 6 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Prozac" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-psychotics" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Prozac" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 7 ) );
    }

    @Test
    /*
     * Tests that medications save when Donor data is saved - does not test exact implementation just yet
     *
     * INCOMPLETE AS REQUIRES ONE ASSERT TO BE COMPLETED SO NUMEROUS LINES HASHED OUT
     *
     */
    public void testMedicationSaving() {
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "SavedMed" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "SavedMed" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "SavedMed" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Saves the Donor data to .json file
        interact( () -> {
            lookup( "#saveMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );

        interact( () -> {
            clickOn( "SavedMed" );
        } );

        interact( () -> {
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "SavedMed" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "SavedMed" );
        } );

        interact( () -> {
            // Press the delete medication button for deleting the selected medication
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Load the saved data from the .json file
        Database.importFromDisk( "./donor.json" );

        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel

        // Leave the Medication pane and navigates to Profile Pane
        interact( () -> {
          lookup( "#goBack" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile

        // Navigate to the medication panel via the temporary test medication button found in profile panel
        interact( () -> {
           lookup( "#testMedication" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that the currentMedications listView to it's previously last saved state prior to remove and delete
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("SavedMed"));
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat("#currentMedications", ListViewMatchers.hasItems( 1 ));
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());


        // Remove medication save from file
        interact( () -> {
          clickOn("SavedMed");
        });

        interact( () -> {
          lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#pastMedications", ListViewMatchers.hasListCell("SavedMed"));
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat("#pastMedications", ListViewMatchers.hasItems( 1 ));
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());

        interact( () -> {
          clickOn("SavedMed");
        });

        interact( () -> {
        // Press the delete medication button for deleting the selected medication
          lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Removes the donor medication save from .json file
        interact( () -> {
            lookup( "#saveMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );

        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel

        // Leave the Medication pane and navigates to Profile Pane
        interact( () -> {
            lookup( "#goBack" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile

        // Navigate to the medication panel via the temporary test medication button found in profile panel
        interact( () -> {
            lookup( "#testMedication" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
    }

    @Test
    public void testMedicationDeletionInBothListsTogether() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "MedicationPast" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "MedicationPast" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "MedicationPast" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        interact( () -> {
            clickOn( "MedicationPast" );
        } );

        interact( () -> {
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "MedicationPast" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "MedicationCurrent" ); // Enters new medication
        } );
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "MedicationCurrent" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "MedicationPast" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "MedicationPast" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "MedicationCurrent" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );

        interact( () -> {
            clickOn( "MedicationCurrent" );
        } );

        interact( () -> {
            clickOn( "MedicationPast" );
        } );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }
}