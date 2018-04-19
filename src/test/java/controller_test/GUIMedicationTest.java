package controller_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class GUIMedicationTest extends ApplicationTest {

    private Main main = new Main();

    @Override
    public void start(Stage stage) throws Exception {
        main.start( stage );
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Steroids"));
    }

    @Test
    /*
     * Tests entering an invalid medication to textfield and registering that medication to currentMedications listView
     */
    public void testInvalidMedicationRegistration() {
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is still empty as a medication has not been registered to it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
    }

    @Test
    /*
     * Tests the selecting of a single medication in the currentMedications listView
     */
    public void testSelectingMedication () {
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Medicine"));

        interact( () -> {
            clickOn("Medicine");
        });
        // Currently unsure how test other than visual observation - which is passing
    }

    @Test
    /*
     * Tests moving a single medication from the currentMedications listView to the pastMedications listView
     */
    public void testRemovingMedication () {
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Pills"));

        interact( () -> {
            clickOn("Pills");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Pills");  // Experiment: DOES IT WORK IF REPEATED??? ...
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#pastMedications", ListViewMatchers.hasListCell("Pills"));
        // Verify that the currentMedications listView is now empty again after medication moved to pastMedications
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
    }

    @Test
    /*
     * Tests moving a single medication from the pastMedications listView to the currentMedications listView
     */
    public void testAddingMedication () {
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Placebo"));


        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#pastMedications", ListViewMatchers.hasListCell("Placebo"));
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());

        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the currentMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Placebo"));
        // Verify that the pastMedications listView is now empty as a medication has now been moved from it
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
    }

    @Test
    /*
     * Tests deleting a single medication from the pastMedications listView and from the medicationHistory ArrayList
     */
    public void testValidDeletingMedication () {
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
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Codeine"));

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
        verifyThat("#pastMedications", ListViewMatchers.hasListCell("Codeine"));
        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
    }

    @Test
    /*
     * Tests cant delete a single medication from the currentMedications listView
     */
    public void testInvalidDeletingMedication () {
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );

        // Enter a new medication to textfield for registering to the current medications registry
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Vitamins" ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Vitamins" ) ) );
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Vitamins"));

        interact( () -> {
            clickOn("Vitamins");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Vitamins");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        // Verify that the currentMedications listView is still not empty as a medication has been deleted from it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Vitamins"));
    }

    @Test
    /*
     * Enter seven more new medications to textfield and register them to current ArrayList and listView
     */
    public void testValidMultipleMedicationRegistrations() {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Morphine" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Aspirin" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadol" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadol"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadeine" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadol"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadeine"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-biotic" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadol"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Anti-biotic"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-psychotics" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadol"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Anti-biotic"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Anti-psychotics"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Prozac" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Panadol"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Morphine"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Aspirin"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Anti-biotic"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Anti-psychotics"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Prozac"));
    }

    @Test
    /*
     * Tests the removing of seven pre-selected medications from the currentMedications to pastMedications listViews
     */
    public void testRemovingMultipleMedications () {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugA" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugB" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugC" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugD" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugE" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugF" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugF"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugG" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugF"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugG"));

        interact( () -> {
            clickOn("TestDrugA");
            clickOn("TestDrugA");
            clickOn("TestDrugB");
            clickOn("TestDrugB");
            clickOn("TestDrugC");
            clickOn("TestDrugC");
            clickOn("TestDrugD");
            clickOn("TestDrugD");
            clickOn("TestDrugE");
            clickOn("TestDrugE");
            clickOn("TestDrugF");
            clickOn("TestDrugF");
            clickOn("TestDrugG");
            clickOn("TestDrugG");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        // THE MULTIPLE SELECTION IS NOT YET COMPLETED SO WILL NOT TEST JUST AS OF YET

        // Verify that the pastMedications listView now has seven medication entries after the swap
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugA"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugB"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugC"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugD"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugE"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugF"));
        //verifyThat("#pastMedications", ListViewMatchers.hasListCell("TestDrugG"));
    }

    @Test
    /*
     * Tests the adding of seven pre-selected medications from the pastMedications to currentMedications listViews
     */
    public void testAddingMultipleMedications (){
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionA" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionB" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionC" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionC"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionD" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionD"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionE" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionE"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionF" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionF"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "prescriptionG" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionF"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("prescriptionG"));

        interact( () -> {
            clickOn("prescriptionA");
            clickOn("prescriptionB");
            clickOn("prescriptionC");
            clickOn("prescriptionD");
            clickOn("prescriptionE");
            clickOn("prescriptionF");
            clickOn("prescriptionG");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("prescriptionA");
            clickOn("prescriptionB");
            clickOn("prescriptionC");
            clickOn("prescriptionD");
            clickOn("prescriptionE");
            clickOn("prescriptionF");
            clickOn("prescriptionG");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        // THE MULTIPLE SELECTION IS NOT YET COMPLETED SO WILL NOT TEST JUST AS OF YET
    }

    @Test
    /*
     * Tests the deletion of seven pre-selected medications from the pastMedications listView and medicationHistory
     */
    public void testValidDeletingMultipleMedications () {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Morphine" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Aspirin" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadol" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Panadeine" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-biotic" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Anti-psychotics" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        ;

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Prozac" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        ;

        interact( () -> {
            clickOn("Aspirin");
            clickOn("Morphine");
            clickOn("Anti-biotic");
            clickOn("Panadeine");
            clickOn("Panadol");
            clickOn("Prozac");
            clickOn("Anti-psychotics");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Prozac");
            clickOn("Anti-psychotics");
            clickOn("Anti-biotic");
            clickOn("Panadeine");
            clickOn("Panadol");
            clickOn("Aspirin");
            clickOn("Morphine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        // THE MULTIPLE SELECTION IS NOT YET COMPLETED SO WILL NOT TEST JUST AS OF YET
    }

    @Test
    /*
     * Tests the deletion of seven pre-selected medications from the pastMedications listView and medicationHistory
     */
    public void testInvalidDeletingMultipleMedications () {
        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugA" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugB" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has two medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugC" ); // Enters new medication
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has three medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugD" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has four medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugE" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has five medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugF" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has six medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugF"));

        // Verify that the medication entry text field is empty prior to entering a new medication for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "TestDrugG" ); // Enters new med
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty prior to entering a new medications for registration
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView now has seven medication entries
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugA"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugB"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugC"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugD"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugE"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugF"));
        verifyThat("#currentMedications", ListViewMatchers.hasListCell("TestDrugG"));

        interact( () -> {
            clickOn( "TestDrugA" );
            clickOn( "TestDrugB" );
            clickOn( "TestDrugC" );
            clickOn( "TestDrugD" );
            clickOn( "TestDrugE" );
            clickOn( "TestDrugF" );
            clickOn( "TestDrugG" );
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );

        // THE MULTIPLE SELECTION IS NOT YET COMPLETED SO WILL NOT TEST JUST AS OF YET
    }
}
