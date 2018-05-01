package controller_test;

import controller.Main;
import javafx.scene.Node;
import javafx.stage.Stage;
import model.Donor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import service.Database;
import testfx.FXNavigation;
import testfx.General;
import testfx.FXMedicationHelper;

import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;

public class GUIMedicationTest extends ApplicationTest {

    private Main main = new Main();
    private General general = new General();
    private FXMedicationHelper medicationScreen = new FXMedicationHelper();
    private FXNavigation navigation = new FXNavigation();
    private Donor target;

    @Override
    public void start( Stage stage ) throws Exception {
        main.start( stage );
        target = Database.getDonorByNhi( "ABC1238" );
    }

    /**
     * Tests logging in, navigating to medication panel
     */
    @Before
    public void LoginAndNavigateToMedicationPanel() {
        // Log in to the app
        general.loginDonor("ABC1238");
        target.setCurrentMedications( new ArrayList <>() );
        target.setMedicationHistory( new ArrayList <>() );
        // Navigate to the profile panel (where the medication test button is currently found)
        navigation.toProfileFromHomeDonor();
        // Navigate to the medication panel via the temporary test medication button found in profile panel
        navigation.toMedicationsFromProfile();
    }

    @After
    public void waitForEvents() {
        general.waitForEvents();
    }

    /**
     * Tests entering a valid medication to textfield and registering that medication to currentMedications listView
     */
    @Test
    public void testValidMedicationRegistration() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Steroids");

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Steroids" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty  as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests entering an invalid medication to textfield and registering that medication to currentMedications listView
     */
    @Test
    public void testInvalidMedicationRegistration() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        medicationScreen.registerMedication("");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        general.clickAlert("OK");

        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is still empty as a medication has not been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests the selecting of a single medication in the currentMedications listView
     */
    @Test
    public void testSelectingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Medicine");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medicine" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        general.clickMatchingText("Medicine");
        // Currently unsure how test other than visual observation - which is passing
    }

    /**
     * Tests moving a single medication from the currentMedications listView to the pastMedications listView
     */
    @Test
    public void testRemovingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Pills");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Pills" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        medicationScreen.removeMedication("Pills");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Pills" ) );
        // Verify that there is only one medication in pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ));
        // Verify that the currentMedications listView is now empty again after medication moved to pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests moving a single medication from the pastMedications listView to the currentMedications listView
     */
    @Test
    public void testAddingMedication() {

        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Placebo");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        medicationScreen.removeMedication("Placebo");

        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        medicationScreen.addMedication("Placebo");
        // Verify that the currentMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
        // Verify that there is only one medication moved to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests can't register medication if duplicate medication already registered to either current or past medications
     */
    @Test
    public void testInvalidDuplicateRegistration() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Duplicate med");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Duplicate med" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        medicationScreen.registerMedication("duplicate Med");
        general.clickAlert("OK");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicate Med" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Duplicate med" ) );
        // Verify that there is only one single medication in the currentMedications list and not two duplicates
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests that any already registered medication in history will be moved to current if registered again
     */
    @Test
    public void testRegisterMedicationAlreadyInHistory() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("History med");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "History med" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        medicationScreen.removeMedication("History med");
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "History med" ) );
        // Verify that there is only one single medication in the pastMedications list and not two duplicates
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("history med");
        // Verify that the medication entry text field is empty again after re-registering the already entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been re-registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "History med" ) );
        // Verify that there is only one single medication in the currentMedication list and not two duplicates
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is now empty as a medication as the medication has been re-registered
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests deleting a single medication from the pastMedications listView and from the medicationHistory ArrayList
     */
    @Test
    public void testValidDeletingMedication() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Codeine");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Codeine" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        medicationScreen.removeMedication("Codeine");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Codeine" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        medicationScreen.deleteMedication("Codeine");
        general.clickAlert("OK");
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Tests that a duplicate medication CAN be registered ONLY AFTER the already registered duplicate is DELETED
     */
    @Test
    public void testValidDuplicateRegistrationAfterDeletion() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Valid duplicate");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        medicationScreen.removeMedication("Valid duplicate");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        medicationScreen.deleteMedication("Valid duplicate");

        general.clickAlert("OK");

        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Valid duplicate");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
        // Verify that there is only one single medication in the currentMedications list and not two duplicates
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
    }

    /**
     * Enter seven more new medications to textfield and register them to current ArrayList and listView
     */
    @Test
    public void testValidMultipleMedicationRegistrations() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        medicationScreen.registerMedication("Morphine");
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        medicationScreen.registerMedication("Aspirin");
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 2 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        medicationScreen.registerMedication("panadol");
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 3 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        medicationScreen.registerMedication("panadeine");
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadeine" ) );
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 4 ) );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        medicationScreen.registerMedication("anti-biotic");
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
        medicationScreen.registerMedication("anti-psychotics");
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
        medicationScreen.registerMedication("Prozac");
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

    /**
     * Tests that medications save when Donor data is saved - does not test exact implementation just yet
     */
    @Test
    public void testMedicationSaving() {
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Saved med");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Saved med" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Saves the Donor data to .json file
        medicationScreen.saveMedication();

        general.clickAlert("OK");

        medicationScreen.removeMedication("Saved med");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Saved med" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        medicationScreen.deleteMedication("Saved med");

        general.clickAlert("OK");
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
        navigation.goBack();

        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile

        // Navigate to the medication panel via the temporary test medication button found in profile panel
        navigation.toMedicationsFromProfile();
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that the currentMedications listView to it's previously last saved state prior to remove and delete
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Saved med"));
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat("#currentMedications", ListViewMatchers.hasItems( 1 ));
        // Verify that the pastMedications listView is still empty as no medication has been registered to it
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Remove medication save from file
        medicationScreen.removeMedication("Saved med");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#pastMedications", ListViewMatchers.hasListCell("Saved med"));
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat("#pastMedications", ListViewMatchers.hasItems( 1 ));
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());

        medicationScreen.deleteMedication("Saved med");

        general.clickAlert("OK");
        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Removes the donor medication save from .json file
        medicationScreen.saveMedication();
        general.clickAlert("OK");

        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel

        // Leave the Medication pane and navigates to Profile Pane
        navigation.goBack();
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile

        // Navigate to the medication panel via the temporary test medication button found in profile panel
        navigation.toMedicationsFromProfile();
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
    }

    /**
     * Tests the deletion of medication selected in both listViews at the same time
     */
    @Test
    public void testMedicationDeletionInBothListsTogether() {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Medication past");
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medication past" ) );
        // Verify that there is only one medication registered to currentMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is still empty
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );

        medicationScreen.removeMedication("Medication past");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Medication past" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        medicationScreen.registerMedication("Medication current");
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Medication past" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medication current" ) );
        // Verify that there is only one medication moved to pastMedications ListView
        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );

        medicationScreen.deleteMedicationMultiSelect(new ArrayList<String>() {{
            add("Medication past");
            add("Medication current");
        }});

        general.clickAlert("OK");
        general.clickAlert("OK");
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
    }
}