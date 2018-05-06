//package controller_test;
//
//import controller.Main;
//import controller.ScreenControl;
//import javafx.event.ActionEvent;
//import javafx.scene.Node;
//import javafx.scene.control.*;
//import javafx.stage.Stage;
//import javafx.stage.Window;
//import model.Donor;
//import org.assertj.core.api.Assertions;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//import org.testfx.matcher.control.ListViewMatchers;
//import org.testfx.matcher.control.TableViewMatchers;
//import org.testfx.matcher.control.TextInputControlMatchers;
//import org.testfx.service.query.PointQuery;
//import org.testfx.util.WaitForAsyncUtils;
//import service.Database;
//
//import javax.xml.crypto.Data;
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//import static org.testfx.api.FxAssert.verifyThat;
//import static org.testfx.assertions.api.Assertions.assertThat;
//
//public class GUIMedicationTest extends ApplicationTest {
//
//    private Main main = new Main();
//    private Donor target;
//    private Stage mainStage;
//
//    @Override
//    public void start( Stage stage ) throws Exception {
//        Database.resetDatabase();
//        main.start( stage );
//        mainStage = stage;
//        ArrayList<String> middle = new ArrayList<>();
//        middle.add("Middle");
//        Database.addDonor(new Donor("TFX9999", "Joe", middle, "Bloggs", LocalDate.of(1990, 2, 9)));
//        target = Database.getDonorByNhi("TFX9999");
//    }
//
//    /**
//     * Tests logging in, navigating to medication panel
//     */
//    @Before
//    public void LoginAndNavigateToMedicationPanel() {
//        // Log in to the app
//        interact(() -> {
//            lookup("#nhiLogin").queryAs(TextField.class).setText("0");
//            assertThat(lookup("#nhiLogin").queryAs(TextField.class)).hasText("0");
//            lookup("#clinicianToggle").queryAs(CheckBox.class).setSelected(true);
//            Assertions.assertThat(lookup("#clinicianToggle").queryAs(CheckBox.class).isSelected());
//            lookup("#loginButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
//        });
//        verifyThat("#clinicianHomePane", Node::isVisible); // Verify that login has taken "user" to home panel
//        target.setCurrentMedications(new ArrayList<>());
//        target.setMedicationHistory(new ArrayList<>());
//        // Navigate to the profile panel (where the medication test button is currently found)
//        interact(() -> {
//            lookup("#searchDonors").queryAs(Button.class).getOnAction().handle(new ActionEvent());
//        });
//        verifyThat("#clinicianSearchDonorsPane", Node::isVisible); // Verify that "user" has navigated to search pane
//        verifyThat("#donorDataTable", TableViewMatchers.hasTableCell("Joe Middle Bloggs"));
//        interact(() -> {
//            doubleClickOn( "Joe Middle Bloggs" );
//        });
//        verifyThat("#donorProfilePane", Node::isVisible);
//        interact(() -> {
//            lookup("#medicationBtn").queryAs(Button.class).fire();
//        });
//        //verifyThat("#medicationPane", Node::isVisible);  // Verify "user" has navigated to medications
//    }
//
//    @After
//    public void waitForEvents() {
//        Database.resetDatabase();
//        for (Window window : listTargetWindows()) {
//            if (window.getScene().getWindow() != mainStage) {
//                interact(() -> {
//                    ((Stage) window.getScene().getWindow()).close();
//                });
//            }
//        }
//
//        WaitForAsyncUtils.waitForFxEvents();
//        sleep( 1000 );
//    }
//
//    /**
//     * Tests entering a valid medication to textfield and registering that medication to currentMedications listView
//     */
//    @Test
//    public void testValidMedicationRegistration() {
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Steroids" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Steroids" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Steroids" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty as no medication has been registered to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests entering an invalid medication to textfield and registering that medication to currentMedications listView
//     */
//    @Test
//    public void testInvalidMedicationRegistration() {
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is still empty as a medication has not been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        // Verify that the pastMedications listView is still empty as no medication has been registered to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests the selecting of a single medication in the currentMedications listView
//     */
//    @Test
//    public void testSelectingMedication() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Medicine" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Medicine" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medicine" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty as no medication has been registered to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Medicine" );
//        } );
//        // Currently unsure how test other than visual observation - which is passing
//    }
//
//    /**
//     * Tests moving a single medication from the currentMedications listView to the pastMedications listView
//     */
//    @Test
//    public void testRemovingMedication() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Pills" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Pills" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Pills" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty as no medication has been registered to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Pills" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the remove medication button for moving the selected medication from current to past medications
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Pills" ) );
//        // Verify that there is only one medication in pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty again after medication moved to pastMedications
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests moving a single medication from the pastMedications listView to the currentMedications listView
//     */
//    @Test
//    public void testAddingMedication() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Placebo" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Placebo" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty as no medication has been registered to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Placebo" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the remove medication button for moving the selected medication from current to past medications
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Placebo" ) );
//        // Verify that there is only one moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Placebo" );
//        } );
//
//        interact( () -> {
//            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the add medication button for moving the selected medication from past to current medications
//            lookup( "#addMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the currentMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Placebo" ) );
//        // Verify that there is only one medication moved to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests can't register medication if duplicate medication already registered to either current or past medications
//     */
//    @Test
//    public void testInvalidDuplicateRegistration() {
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "duplicate med" ); // Enters new med
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicate med" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Duplicate med" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "duplicate Med" ); // Enters new med
//        } );
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicate Med" ) ) );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "duplicate Med" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Duplicate med" ) );
//        // Verify that there is only one single medication in the currentMedications list and not two duplicates
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty as no medication has been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests that any already registered medication in history will be moved to current if registered again
//     */
//    @Test
//    public void testRegisterMedicationAlreadyInHistory() {
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "history med" ); // Enters new med
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "history med" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "History med" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "History med" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the remove medication button for moving the selected medication from current to past medications
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "History med" ) );
//        // Verify that there is only one single medication in the pastMedications list and not two duplicates
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "history med" ); // Enters new med
//        } );
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "history med" ) ) );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the medication entry text field is empty again after re-registering the already entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been re-registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "History med" ) );
//        // Verify that there is only one single medication in the currentMedication list and not two duplicates
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is now empty as a medication as the medication has been re-registered
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests deleting a single medication from the pastMedications listView and from the medicationHistory ArrayList
//     */
//    @Test
//    public void testValidDeletingMedication() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Codeine" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Codeine" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Codeine" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Codeine" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Codeine" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Codeine" );
//        } );
//
//        interact( () -> {
//            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the delete medication button for deleting the selected medication
//            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Tests that a duplicate medication CAN be registered ONLY AFTER the already registered duplicate is DELETED
//     */
//    @Test
//    public void testValidDuplicateRegistrationAfterDeletion() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "valid duplicate" ); // Enters new med
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "valid duplicate" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Valid duplicate" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the remove medication button for moving the selected medication from current to past medications
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Valid duplicate" );
//        } );
//
//        interact( () -> {
//            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            // Press the delete medication button for deleting the selected medication
//            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//
//        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "valid Duplicate" ); // Enters new med
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "valid Duplicate" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Valid duplicate" ) );
//        // Verify that there is only one single medication in the currentMedications list and not two duplicates
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//    }
//
//    /**
//     * Enter seven more new medications to textfield and register them to current ArrayList and listView
//     */
//    @Test
//    public void testValidMultipleMedicationRegistrations() {
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "morphine" ); // Enters new medication
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has one entry as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "aspirin" ); // Enters new medication
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has two medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 2 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "panadol" ); // Enters new medication
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has three medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 3 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "panadeine" ); // Enters new med
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has four medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadeine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 4 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "anti-biotic" ); // Enters new med
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has five medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 5 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "anti-psychotics" ); // Enters new med
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has six medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-psychotics" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 6 ) );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Prozac" ); // Enters new med
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty prior to entering a new medications for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView now has seven medication entries
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Aspirin" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Panadol" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Morphine" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-biotic" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Anti-psychotics" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Prozac" ) );
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 7 ) );
//    }
//
////    /**
////     * Tests that medications save when Donor data is saved - does not test exact implementation just yet
////     */
////    @Test
////    public void testMedicationSaving() {
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////        // Verify that the medication entry text field is empty prior to entering a new medication for registration
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////
////        // Enter a new medication to textfield for registering to the current medications registry
////        interact( () -> {
////            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Saved med" ); // Enters new medication
////        } );
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
////        // Verify that the textfield currently has the entered medication prior to registration being initiated
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Saved med" ) ) );
////        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
////        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
////        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
////
////        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
////        interact( () -> {
////            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
////        // Verify that the medication entry text field is empty again after registering the entered medication
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
////        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Saved med" ) );
////        // Verify that there is only one medication registered to currentMedications ListView
////        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
////        // Verify that the pastMedications listView is still empty as no medication has been registered to it
////        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
////
////        // Saves the Donor data to .json file
////        interact( () -> {
////            lookup( "#saveMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////
////        interact( () -> {
////            lookup("OK").queryAs(Button.class).fire();
////        });
////
////        interact( () -> {
////            clickOn( "Saved med" );
////        } );
////
////        interact( () -> {
////            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
////            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
////        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Saved med" ) );
////        // Verify that there is only one medication moved to pastMedications ListView
////        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
////        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
////        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
////
////        interact( () -> {
////            clickOn( "Saved med" );
////        } );
////
////        interact( () -> {
////            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
////            // Press the delete medication button for deleting the selected medication
////            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////
////        interact( () -> {
////            lookup("OK").queryAs(Button.class).fire();
////        });
////        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
////        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
////        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////        // Verify that the medication entry text field is empty prior to entering a new medication for registration
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////
////        // Load the saved data from the .json file
////        Database.importFromDisk( "./donor.json" );
////
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////
////        // Leave the Medication pane and navigates to Profile Pane
////        interact( () -> {
////          lookup( "#goBack" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        });
////
////        verifyThat( "#donorProfilePane", Node::isVisible ); // Verify that "user" has navigated to profile
////
////        // Navigate to the medication panel via the temporary test medication button found in profile panel
////        interact( () -> {
////           lookup( "#testMedication" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////        // Verify that the medication entry text field is empty prior to entering a new medication for registration
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////
////        // Verify that the currentMedications listView to it's previously last saved state prior to remove and delete
////        verifyThat("#currentMedications", ListViewMatchers.hasListCell("Saved med"));
////        // Verify that there is only one medication registered to currentMedications ListView
////        verifyThat("#currentMedications", ListViewMatchers.hasItems( 1 ));
////        // Verify that the pastMedications listView is still empty as no medication has been registered to it
////        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
////
////        // Remove medication save from file
////        interact( () -> {
////          clickOn("Saved med");
////        });
////
////        interact( () -> {
////            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
////          lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        });
////        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
////        verifyThat("#pastMedications", ListViewMatchers.hasListCell("Saved med"));
////        // Verify that there is only one medication moved to pastMedications ListView
////        verifyThat("#pastMedications", ListViewMatchers.hasItems( 1 ));
////        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
////        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
////
////        interact( () -> {
////          clickOn("Saved med");
////        });
////
////        interact( () -> {
////            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
////            // Press the delete medication button for deleting the selected medication
////             lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        });
////
////        interact( () -> {
////            lookup("OK").queryAs(Button.class).fire();
////        });
////
////        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
////        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
////        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////        // Verify that the medication entry text field is empty prior to entering a new medication for registration
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////
////        // Removes the donor medication save from .json file
////        interact( () -> {
////            lookup( "#saveMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////
////        interact( () -> {
////            lookup("OK").queryAs(Button.class).fire();
////        });
////
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////
////        // Leave the Medication pane and navigates to Profile Pane
////        interact( () -> {
////            lookup( "#goBack" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        });
////        verifyThat( "#donorProfilePane", Node::isVisible ); // Verify that "user" has navigated to profile
////
////        // Navigate to the medication panel via the temporary test medication button found in profile panel
////        interact( () -> {
////            lookup( "#testMedication" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
////        } );
////        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
////        // Verify that the medication entry text field is empty prior to entering a new medication for registration
////        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
////
////        // Verify that both of the medications listViews are now empty as a medication has been deleted from it
////        verifyThat("#currentMedications", ListViewMatchers.isEmpty());
////        verifyThat("#pastMedications", ListViewMatchers.isEmpty());
////    }
//
//    /**
//     * Tests the deletion of medication selected in both listViews at the same time
//     */
//    @Test
//    public void testMedicationDeletionInBothListsTogether() {
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Medication past" ); // Enters new medication
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Medication past" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
//        // Verify that the medication entry text field is empty again after registering the entered medication
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//        // Verify that the currentMedications listView is now not empty as a medication has now been registered to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medication past" ) );
//        // Verify that there is only one medication registered to currentMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is still empty
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//
//        interact( () -> {
//            clickOn( "Medication past" );
//        } );
//
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            lookup( "#removeMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Medication past" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the currentMedications listView is now empty as a medication has now been moved from it
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//
//        // Verify that the medication entry text field is empty prior to entering a new medication for registration
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "" ) ) );
//
//        // Enter a new medication to textfield for registering to the current medications registry
//        interact( () -> {
//            lookup( "#newMedication" ).queryAs( TextField.class ).setText( "Medication current" ); // Enters new medication
//        } );
//        // Verify that the textfield currently has the entered medication prior to registration being initiated
//        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( "Medication current" ) ) );
//        // Verify that both of the listViews are empty as no medication has yet been registered to any of them yet
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Medication past" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup( "#registerMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#pastMedications", ListViewMatchers.hasListCell( "Medication past" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#pastMedications", ListViewMatchers.hasItems( 1 ) );
//        // Verify that the pastMedications listView is now not empty as a medication has now been moved to it
//        verifyThat( "#currentMedications", ListViewMatchers.hasListCell( "Medication current" ) );
//        // Verify that there is only one medication moved to pastMedications ListView
//        verifyThat( "#currentMedications", ListViewMatchers.hasItems( 1 ) );
//
//        interact( () -> {
//            clickOn( "Medication current" );
//        } );
//
//        interact( () -> {
//            clickOn( "Medication past" );
//        } );
//
//        // Registers the new medication entry in the textfield to the current medications ArrayList and listView
//        interact( () -> {
//            lookup("#currentMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            lookup("#pastMedications").queryAs(ListView.class).getSelectionModel().select(0);
//            lookup( "#deleteMed" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//        } );
//
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//        interact( () -> {
//            lookup("OK").queryAs(Button.class).fire();
//        });
//        // Verify that the pastMedications listView is now empty as a medication has now been deleted from it
//        verifyThat( "#pastMedications", ListViewMatchers.isEmpty() );
//        // Verify that the currentMedications listView is now empty the only medication was deleted from pastMedications
//        verifyThat( "#currentMedications", ListViewMatchers.isEmpty() );
//    }
//}