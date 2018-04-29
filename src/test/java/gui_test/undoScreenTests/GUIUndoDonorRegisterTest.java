package gui_test.undoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the donor register screen
 */
public class GUIUndoDonorRegisterTest extends ApplicationTest{

    private Main main = new Main();

    /**
     * Launches the main application
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {
        main.start(stage);
        interact(() -> {
            lookup("#registerHyperlink").queryAs(Hyperlink.class).fire();
        });
    }

    /**
     * Sets the widgets on the screen to known values before testing
     */
    @Before
    public void setFields() {
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName");
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName");
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName");
            lookup("#nhiRegister").queryAs(TextField.class).setText("AAA1111");

            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2001, 1, 1));
        });
    }

    /**
     * Waits for all events to finish before moving on
     */
    @After
    public void waitForEvents() {
        Database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Tests that we have navigated to the right screen
     */
    @Test
    public void verifyScreen() {
        verifyThat("#donorRegisterAnchorPane", Node::isVisible);
    }

    /**
     * Tests that all possible TextFields can undo successfully
     */
    @Test
    public void undoTextFields() {
        // Check undo button first
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName"));

        // Check Ctrl Z next (with both ways of release)
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName"));
    }

    /**
     * Tests that all possible DatePickers can undo successfully
     */
    @Test
    public void undoDatePickers() {
        // Check undo button first
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
    }
    
    /**
     * Tests that all possible widgets can undo multiple times in a row
     */
    @Test
    public void undoMultipleTimes() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiRegister").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiRegister").queryAs(TextField.class).setText("DDD4444");
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiRegister").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiRegister").queryAs(TextField.class).setText("DDD4444");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
    }

    /**
     * Tests that multiple different widgets can undo after each other
     */
    @Test
    public void undoOverMultipleWidgets() {
        // Check undo button first
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2001, 1, 1));
    }
}
