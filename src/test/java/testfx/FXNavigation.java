package testfx;

import javafx.scene.Node;
import javafx.scene.control.Hyperlink;

import static org.testfx.api.FxAssert.verifyThat;

public class FXNavigation extends General {


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
        clickButton("#testMedication");
        verifyThat( "#medicationPane", Node::isVisible );  // Verify "user" has navigated to medications
    }


    /**
     * Helper method to go to patient profile from the home page
     */
    public void toProfileFromHomeDonor(){
        clickButton("#goToProfile");
        verifyThat( "#profilePane", Node::isVisible ); // Verify that "user" has navigated to profile
    }
}
