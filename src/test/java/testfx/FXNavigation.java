package testfx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class FXNavigation extends ApplicationTest {


    /**
     * Click the back hyperlink
     */
    public void goBack(){
        interact( () -> {
            lookup( "#goBack" ).queryAs( Hyperlink.class ).fire();
        });
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
     * Helper method to go to donor profile from the home page
     */
    public void toProfileFromHomeDonor(){
        interact( () -> {
            lookup( "#goToProfile" ).queryAs( Button.class ).fire();
        } );
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile
    }
}
