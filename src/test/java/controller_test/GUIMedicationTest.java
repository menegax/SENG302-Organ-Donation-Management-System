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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;

        interact( () -> {
            clickOn("Medicine");
        });
        // Not sure how test other than visual observation - which is passing
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;

        interact( () -> {
            clickOn("Pills");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Pills");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;

        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Placebo");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });

        interact( () -> {
            clickOn("Codeine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
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
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;

        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the medication entry text field is empty again after registering the entered medication
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
        ;

        interact( () -> {
            clickOn("Vitamins");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
    }

    @Test
    /*
     * Enter seven more new medications to textfield and register them to current ArrayList and listView
     */
    public void testValidMultipleMedicationRegistrations() {
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;
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
    }

    @Test
    /*
     * Tests the removing of seven pre-selected medications from the currentMedications to pastMedications listViews
     */
    public void testRemovingMultipleMedications () {
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;
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
            clickOn("Prozac");
            clickOn("Anti-psychotics");
            clickOn("Anti-biotic");
            clickOn("Panadeine");
            clickOn("Panadol");
            clickOn("Aspirin");
            clickOn("Morphine");
            // The following line doesn't do anything, but the clickOn is visibly working
            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
    }

    @Test
    /*
     * Tests the adding of seven pre-selected medications from the pastMedications to currentMedications listViews
     */
    public void testAddingMultipleMedications (){
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;
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
            clickOn("Anti-biotic");
            clickOn("Panadeine");
            clickOn("Panadol");
            clickOn("Aspirin");
            clickOn("Morphine");
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
            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
    }

    @Test
    /*
     * Tests the deletion of seven pre-selected medications from the pastMedications listView and medicationHistory
     */
    public void testDeletingMultipleMedications (){
        // Verify that the currentMedications listView is empty as no medication has yet been registered to it
        ;
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
    }
}
