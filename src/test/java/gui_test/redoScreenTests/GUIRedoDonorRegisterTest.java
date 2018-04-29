package gui_test.redoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the donor register screen
 */
public class GUIRedoDonorRegisterTest extends ApplicationTest{

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
     * Tests that all possible TextFields can redo successfully
     */
    @Test
    public void redoTextFields() {
        // Check redo button first
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName2"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName2"));

        // Check Ctrl Y next (with both ways of release)
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName2"));
        assertThat(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName2"));
    }

    /**
     * Tests that all possible DatePickers can redo successfully
     */
    @Test
    public void redoDatePickers() {
        // Check redo button first
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });

        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));

        // Check Ctrl Y next
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
    }
    
    /**
     * Tests that all possible widgets can redo multiple times in a row
     */
    @Test
    public void redoMultipleTimes() {
        // Check redo button first
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiRegister").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiRegister").queryAs(TextField.class).setText("DDD4444");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2004, 4, 4));

        // Check Ctrl Y next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiRegister").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiRegister").queryAs(TextField.class).setText("DDD4444");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2003, 3, 3));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2004, 4, 4));
    }

    /**
     * Tests that multiple different widgets can redo after each other
     */
    @Test
    public void redoOverMultipleWidgets() {
        // Check redo button first
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));

        // Check Ctrl Y next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("AAA1111"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertThat(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertThat(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertThat(lookup("#birthRegister").queryAs(DatePicker.class).getValue() == LocalDate.of(2002, 2, 2));
    }
}
