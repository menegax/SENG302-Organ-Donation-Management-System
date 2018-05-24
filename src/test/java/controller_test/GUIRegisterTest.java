package controller_test;

import main.Main;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.util.logging.Level.OFF;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static utility.UserActionHistory.userActions;

public class GUIRegisterTest extends ApplicationTest {

    private Main main = new Main();
    private LocalDate d = LocalDate.of(1957,6,21);


    /**
     *Reset db to a clean state
     */
    @Before
    public void setup() {
        Database.resetDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
        interact(() ->  lookup("#registerHyperlink").queryAs(Hyperlink.class).fire());

    }

    /**
     * Turn off logging
     */
    @BeforeClass
    public static void setUp() {
        userActions.setLevel(OFF);
    }
    /**
     * Reset db to a clean state wait for 1000ms
     */
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Verify the register screen is visible
     */
    @Test
    public void verify_screen_register() {
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Checks that the user has registered successfully register with a middle name
     */
    @Test
    public void should_successfully_register_with_middle_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB9922");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#middlenameRegister").queryAs(TextField.class).setText("Wil");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB9922"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("William"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("Wil"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("Williamson"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == d);

        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            //clickOn(900,450);
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#loginPane", Node::isVisible);
    }


    /**
     * Checks that the user has registered successfully register without a middle name
     */
    @Test
    public void should_successfully_register_without_middle_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB9782");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("Willis");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Brucie");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB9752"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("Willis"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("Brucie"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == d);

        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#loginPane", Node::isVisible);
    }

    /**
     * Checks that the user has not been registered with no input
     */
    @Test
    public void unsuccessful_register_no_input() {
        interact(() -> {
            lookup("#doneButton").queryAs(Button.class).fire();
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat(lookup("#patientRegisterAnchorPane"), Node::isVisible);
    }


    /**
     * Checks that the user has not been registered with no nhi
     */
    @Test
    public void unsuccessful_register_no_nhi() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Checks that the user has not been registered with invalid nhi
     */
    @Test
    public void unsuccessful_register_invalid_nhi() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }


    /**
     * Checks that the user has not been registered with no firstname
     */
    @Test
    public void unsuccessful_register_no_first_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Checks that the user has not been registered with no last name
     */
    @Test
    public void unsuccessful_register_no_last_name() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Checks that the user has not been registered with no date of birth
     */
    @Test
    public void unsuccessful_register_no_birth_date() {
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("2222bbb");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Checks that the user has not been registered with duplicate nhi
     */
    @Test
    public void unsuccessful_register_duplicate_nhi() throws InvalidObjectException {

        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        Database.addPatient(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.LIVER);
        Database.getPatientByNhi("TFX9999").addDonation(GlobalEnums.Organ.CORNEA);

        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("TFX9999");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("William");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("Williamson");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(1957,6,21));
            lookup("#doneButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
            lookup("OK").queryAs(Button.class).fire();
        });
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
    }

}