package controller_component_test;

import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static javafx.scene.input.KeyCode.CONTROL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

//todo make it used and add javadoc
public class ATestHelper extends ApplicationTest {


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

    /**
     * Sets the TestFx tests to run in headless mode so that they can pass the runner
     */
    @BeforeAll
    public static void setTestFXHeadless() {
        {
            System.setProperty("prism.verbose", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.setup.timeout", "2500");
        }
    }

    /**
     * Turns off user action logging
     */
    @BeforeAll
    public static void setLoggingFalse() {
        userActions.setLevel(Level.OFF);
        systemLogger.setLevel(Level.OFF);
    }

}
