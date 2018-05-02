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
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the donor register screen
 */
public class GUIUndoDonorRegisterTest extends ApplicationTest{

    private Main main = new Main();

    private String firstnameRegisterDefault;
    private String lastnameRegisterDefault;
    private String middlenameRegisterDefault;
    private String nhiRegisterDefault;

    private LocalDate birthRegisterDefault;
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
     * Gets the values of the widgets on the screen before testing
     */
    @Before
    public void getFields() {
        interact(() -> {
            firstnameRegisterDefault = lookup("#firstnameRegister").queryAs(TextField.class).getText();
            lastnameRegisterDefault = lookup("#lastnameRegister").queryAs(TextField.class).getText();
            middlenameRegisterDefault = lookup("#middlenameRegister").queryAs(TextField.class).getText();
            nhiRegisterDefault = lookup("#nhiRegister").queryAs(TextField.class).getText();

            birthRegisterDefault = lookup("#birthRegister").queryAs(DatePicker.class).getValue();
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

        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertTrue(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals(lastnameRegisterDefault));
        assertTrue(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals(middlenameRegisterDefault));

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

        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertTrue(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals(lastnameRegisterDefault));
        assertTrue(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals(middlenameRegisterDefault));
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

        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#nhiRegister").queryAs(TextField.class).setText("CCC3333");
            lookup("#nhiRegister").queryAs(TextField.class).setText("DDD4444");
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals(nhiRegisterDefault));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
    }
}
