package testfx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class TestFxHelper extends ApplicationTest {

    /**
     * Helper method to login via UI
     * @param nhi - NHI number to login with
     */
    public void loginDonor(String nhi){
        interact( () -> {
            lookup( "#nhiLogin" ).queryAs( TextField.class ).setText( nhi );
            assertThat( lookup( "#nhiLogin" ).queryAs( TextField.class ) ).hasText( nhi );
            lookup( "#loginButton" ).queryAs( Button.class ).fire();
            verifyThat( "#homePane", Node::isVisible ); // Verify that login has taken "user" to home panel
        } );
    }


    /**
     * Helper method to go to donor profile from the home page
     */
    public void toProfileFromHomeDonor(){
        interact( () -> {
            lookup( "#goToProfile" ).queryAs( Button.class ).fire();
        } );
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile
    }


    /**
     * Helper method to open the medication scene from the profile
     */
    public void toMedicationsFromProfile(){
        interact( () -> {
            lookup( "#testMedication" ).queryAs( Button.class ).fire();
        } );
        verifyThat( "#medicationPane", Node::isVisible );  // Verify "user" has navigated to medications
    }


    /**
     * Helper method to wait for events
     */
    public void waitForEvents(){
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }


    /**
     * Helper method to enter a new medication into the medications field
     */
    public void registerMedication(String query){
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is in medication panel
        interact( () -> {
            lookup( "#newMedication" ).queryAs( TextField.class ).setText( query ); // Enters new medication
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel
        // Verify that the textfield currently has the entered medication prior to registration being initiated
        verifyThat( "#newMedication", TextInputControlMatchers.hasText( String.valueOf( query ) ) );
        interact( () -> {
            lookup( "#registerMed" ).queryAs( Button.class ).fire();
        } );
        verifyThat( "#medicationPane", Node::isVisible ); // Verify "user" is still in medication panel after registration
    }





}
