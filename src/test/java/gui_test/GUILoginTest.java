package gui_test;

import controller.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Patient;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import controller.UserControl;

import static java.util.logging.Level.OFF;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.Data;

public class GUILoginTest extends ApplicationTest {

    private Main main = new Main();

    private UserControl userControl = new UserControl();

    private String existingNhi = "TFX9999";


    @Override
    public void start(Stage stage) throws IOException {

        Database.resetDatabase();

        // add dummy patient
        Database.addPatient(new Patient(existingNhi,
                "Joe",
                new ArrayList<>(Collections.singletonList("Middle")),
                "Bloggs",
                LocalDate.of(1990, 2, 9)));

        main.start(stage);
    }


    @BeforeClass
    public static void setUpClass() {
        userActions.setLevel(OFF);
    }


    @Test
    public void ValidCredentials() {
        givenCredentials(existingNhi);
        whenClickLogIn();
        verifyThat("#homePane", Node::isVisible);
    }


    @Test
    public void NonExistentPatient() {
        givenCredentials("ABD1234");
        whenClickLogIn();
        thenNoPatientLoggedIn();
        thenAlertIsWarning();
    }

    @Test
    public void BlankInput() {
        givenCredentials("");
        whenClickLogIn();
        thenNoPatientLoggedIn();
        thenAlertIsWarning();
    }


    private void givenCredentials(String nhi) {
        lookup("#nhiLogin").queryAs(TextField.class)
                .setText(nhi);
    }


    private void whenClickLogIn() {
        interact(() -> lookup("#loginButton").queryAs(Button.class)
                .getOnAction()
                .handle(new ActionEvent()));
    }


    private void thenNoPatientLoggedIn() {
        assertThat(userControl.isUserLoggedIn());
//        assertThat(((Patient) userControl.getLoggedInUser()).getNhiNumber() == null);
    }


    /**
     * Ensures an alert exists with the title "Warning"
     *
     * <p>
     * The Alert popup is then closed by clicking a button with string "OK"
     * </p>
     */
    private void thenAlertIsWarning() {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);
        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene()
                .getRoot();
        assertEquals("Warning", dialogPane.getHeaderText());
        interact(() -> clickOn("OK"));
    }


    //todo doc
    private Stage getTopModalStage() {
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder()
                .listWindows());
        Collections.reverse(allWindows);
        return (Stage) allWindows.stream()
                .filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);
    }

}
