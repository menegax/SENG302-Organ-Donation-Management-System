package testfx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javafx.scene.input.KeyCode.CONTROL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

public class General extends ApplicationTest {

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
     * Helper method to wait for events
     */
    public void waitForEvents(){
        WaitForAsyncUtils.waitForFxEvents();
        sleep( 1000 );
    }


    /**
     * Click the button on an the alert pop up
     * @param buttonText - text of the button e.g. OK, CANCEL
     */
    public void clickAlert(String buttonText){
        interact( () -> {
            lookup(buttonText).queryAs(Button.class).fire();
        });
    }


    /**
     * Finds the matching text on the screen and clicks on it
     * @param match - string of the text to click on
     */
    public void clickMatchingText(String match){
        interact( () -> {
            clickOn( match );
        } );
    }


    /**
     * Click control by ID
     * @param elementId - ID of the element to click on e.g. #removeMed
     */
    void clickButton(String elementId) {
        interact( () -> {
            // Press the remove medication button for moving the selected medication from current to past medications
            lookup( elementId).queryAs( Button.class ).fire();
        } );
    }


    void multiSelectListView(List<String> selections){
        press(CONTROL);
        for (String selection : selections){
            clickMatchingText(selection);
        }
        release(CONTROL);
    }


    /**
     *
     * @param expectedHeader -
     * @param expectedContent -
     */
    public void validateAlert(String expectedHeader, String expectedContent) {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertEquals(expectedContent, dialogPane.getContentText());
        clickMatchingText("OK");
    }

    /**
     *
     * @return -
     */
    public Stage getTopModalStage() {
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);
        return (Stage) allWindows.stream().filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst().orElse(null);
    }





}
