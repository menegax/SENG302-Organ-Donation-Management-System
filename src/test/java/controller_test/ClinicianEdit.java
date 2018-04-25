package controller_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Clinician;
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

public class ClinicianEdit extends ApplicationTest {
    private Main main = new Main();
    private ArrayList<Clinician> clinicians = Database.getClinicians();
    private String staffId;

    @Override
    public void start(Stage stage) throws Exception {
        main.start( stage );
    }

    @Before
    /**
     * Tests logging in as a clinician
     */
    public void Login() {
        staffId = Integer.toString(clinicians.get(0).getStaffID());
        //Check 'I am Clinician" checkbox to login as clinician
        interact( () -> {
            lookup( "#clinicianToggle" ).queryAs( CheckBox.class ).setSelected(true);
            assertThat( lookup ("#clinicianToggle" ).queryAs( CheckBox.class ).isSelected());
            lookup( "#nhiLogin" ).queryAs( TextField.class ).setText( staffId );
            assertThat( lookup( "#nhiLogin" ).queryAs( TextField.class ) ).hasText( staffId );
            lookup( "#loginButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#clinicianHomePane", Node::isVisible ); // Verify that login has taken "user" to the clinician home panel
        interact( () -> {
            lookup( "#profileButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that profile button has taken "user" to the clinician profile panel
        interact( () -> {
            lookup( "#edit" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that edit button has taken "user" to the profile edit panel
    }

    @After
    public void waitForEvents() {
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }

    @Test
    /*
     * Tests that the clinician can successfully edit their staff ID with a valid field
     */
    public void successfulUpdateClinicianId () {
        interact( () -> {
            lookup("#staffId").queryAs(TextField.class).setText("1");
            assertThat( lookup( "#staffId" ).queryAs( TextField.class ) ).hasText( "1" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getStaffID() == 1);
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that save button has taken the "user to the profile page
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician staff ID with an invalid field
     */
    public void unsuccessfulUpdateClinicianId () {
        interact( () -> {
            lookup("#staffId").queryAs(TextField.class).setText("A");
            assertThat( lookup( "#staffId" ).queryAs( TextField.class ) ).hasText( "A" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getStaffID() == 1);
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that the save button prompted an invalid field alert and did not leave the profile edit panel
    }

    @Test
    /*
     * Tests that that the clinician can successfully edit their first name with a valid field
     */
    public void successfulUpdateClinicianFirstName () {
        interact( () -> {
            lookup("#firstnameTxt").queryAs(TextField.class).setText("James");
            assertThat( lookup( "#firstnameTxt" ).queryAs( TextField.class ) ).hasText( "James" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getFirstName().equals("James"));
        verifyThat( "#clinicianProfilePane", Node::isVisible );  // Verify that save button has taken the "user to the profile page
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician first name with an invalid field
     */
    public void unsuccessfulUpdateClinicianFirstName () {
        interact(() -> {
            lookup("#firstnameTxt").queryAs(TextField.class).setText("12");
            assertThat(lookup("#firstnameTxt").queryAs(TextField.class)).hasText("12");
            lookup("#saveProfile").queryAs(Button.class).getOnAction().handle(new ActionEvent());
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getFirstName().equals("12"));
        verifyThat("#clinicianUpdate", Node::isVisible); // Verify that the save button prompted an invalid field alert and did not leave the profile edit panel
    }

    @Test
    /*
     * Tests that the clinician can successfully edit their last name with a valid field
     */
    public void successfulUpdateClinicianLastName () {
        interact( () -> {
            lookup("#lastnameTxt").queryAs(TextField.class).setText("Bond");
            assertThat( lookup( "#lastnameTxt" ).queryAs( TextField.class ) ).hasText( "Bond" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getLastName().equals("Bond"));
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that profile button has taken "user" to the clinician profile panel
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician last name with an invalid field
     */
    public void unsuccessfulUpdateClinicianLastName () {
        interact( () -> {
            lookup("#lastnameTxt").queryAs(TextField.class).setText("12");
            assertThat( lookup( "#lastnameTxt" ).queryAs( TextField.class ) ).hasText( "12" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getLastName().equals("12"));
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that the save button prompted an invalid field alert and did not leave the profile edit panel
    }

    @Test
    /*
     * Tests that the clinician can successfully edit their middle name with a valid field
     */
    public void successfulUpdateClinicianMiddleName () {
        interact( () -> {
            lookup("#middlenameTxt").queryAs(TextField.class).setText("Andre");
            assertThat( lookup( "#middlenameTxt" ).queryAs( TextField.class ) ).hasText( "Andre" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getMiddleNames().get(0).equals("Andre"));
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that profile button has taken "user" to the clinician profile panel
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician middle name with an invalid field
     */
    public void unsuccessfulUpdateClinicianMiddleName () {
        interact( () -> {
            lookup("#middlenameTxt").queryAs(TextField.class).setText("12");
            assertThat( lookup( "#middlenameTxt" ).queryAs( TextField.class ) ).hasText( "12" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getMiddleNames().get(0).equals("12"));
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that the save button prompted an invalid field alert and did not leave the profile edit panel
    }

    @Test
    /*
     * Tests that the clinician can successfully edit their street1 name with a valid field
     */
    public void successfulUpdateClinicianStreet1 () {
        interact( () -> {
            lookup("#street1Txt").queryAs(TextField.class).setText("Riccarton RD");
            assertThat( lookup( "#street1Txt" ).queryAs( TextField.class ) ).hasText( "Riccarton RD" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getStreet1().equals("Riccarton RD"));
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that the save button has taken "user" to the profile panel
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician street1 with an invalid field
     */
    public void unsuccessfulUpdateClinicianStreet1 () {
        interact( () -> {
            lookup("#street1Txt").queryAs(TextField.class).setText("12 RD");
            assertThat( lookup( "#street1Txt" ).queryAs( TextField.class ) ).hasText( "12 RD" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getStreet1().equals("12 RD"));
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that save button has prompted the "user" with an invalid field alert
    }

    @Test
    /*
     * Tests that that the clinician can successfully edit their street2 name with a valid field
     */
    public void successfulUpdateClinicianStreet2 () {
        interact( () -> {
            lookup("#street2Txt").queryAs(TextField.class).setText("Hanrahan RD");
            assertThat( lookup( "#street2Txt" ).queryAs( TextField.class ) ).hasText( "Hanrahan RD" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getStreet2().equals("Hanrahan RD"));
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that profile button has taken "user" to the clinician profile panel
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician street2 with an invalid field
     */
    public void unsuccessfulUpdateClinicianStreet2 () {
        interact( () -> {
            lookup("#street2Txt").queryAs(TextField.class).setText("12 RD");
            assertThat( lookup( "#street2Txt" ).queryAs( TextField.class ) ).hasText( "12 RD" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getStreet1().equals("12 RD"));
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that save button has prompted the "user" with an invalid field alert
    }

    @Test
    /*
     * Tests that that the clinician can successfully edit their suburb with a valid field
     */
    public void successfulUpdateClinicianSuburb () {
        interact( () -> {
            lookup("#suburbTxt").queryAs(TextField.class).setText("Fendalton");
            assertThat( lookup( "#suburbTxt" ).queryAs( TextField.class ) ).hasText( "Fendalton" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(Database.getClinicians().get(0).getSuburb().equals("Fendalton"));
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that profile button has taken "user" to the clinician profile panel
    }

    @Test
    /*
     * Tests unsuccessful edit of clinician suburb with an invalid field
     */
    public void unsuccessfulUpdateClinicianSuburb () {
        interact( () -> {
            lookup("#suburbTxt").queryAs(TextField.class).setText("12");
            assertThat( lookup( "#suburbTxt" ).queryAs( TextField.class ) ).hasText( "12" );
            lookup( "#saveProfile" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
//            lookup("#OK").queryAs(Button.class).fire();
        });
        assertThat(!Database.getClinicians().get(0).getStreet1().equals("12 RD"));
        verifyThat( "#clinicianUpdate", Node::isVisible ); // Verify that save button has prompted the "user" with an invalid field alert
    }

    // TODO testing region checkboxes (valid and invalid)

    @Test
    /*
     * Tests the back and logout buttons work
     */
    public void logoutButtonTakesUserToLoginScreen () {
        interact( () -> {
            lookup( "#back" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent());
        });
        verifyThat( "#clinicianProfilePane", Node::isVisible ); // Verify that back button has taken "user" to the profile panel
        interact( () -> {
            lookup( "#back" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#clinicianHomePane", Node::isVisible ); // Verify that back button has taken "user" to the home panel
        interact( () -> {
            lookup( "#logoutButton" ).queryAs( Button.class ).getOnAction().handle( new ActionEvent() );
        });
        verifyThat( "#pane", Node::isVisible ); // Verify that logout button has taken "user" to the login panel
    }
}
