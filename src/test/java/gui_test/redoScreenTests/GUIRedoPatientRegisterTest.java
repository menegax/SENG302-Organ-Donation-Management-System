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
import service.Database;

import java.time.LocalDate;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the patient register screen
 */
public class GUIRedoPatientRegisterTest extends ApplicationTest{

    Database database = Database.getDatabase();

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
            while (lookup("OK").queryAs(Button.class) != null) {
                lookup("OK").queryAs(Button.class).fire();
            }
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
        database.resetDatabase();
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);
    }

    /**
     * Tests that we have navigated to the right screen
     */
    @Test
    public void verifyScreen() {
        verifyThat("#patientRegisterAnchorPane", Node::isVisible);
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
        });
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));

        interact(() -> {
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName2"));

        interact(() -> {
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName2"));

        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        
        // Check Ctrl Y next (with both ways of release)
        interact(() -> {
            lookup("#firstnameRegister").queryAs(TextField.class).setText("FirstName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));

        interact(() -> {
            lookup("#lastnameRegister").queryAs(TextField.class).setText("LastName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#lastnameRegister").queryAs(TextField.class).getText().equals("LastName2"));

        interact(() -> {
            lookup("#middlenameRegister").queryAs(TextField.class).setText("MiddleName2");
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#middlenameRegister").queryAs(TextField.class).getText().equals("MiddleName2"));

        interact(() -> {
            lookup("#nhiRegister").queryAs(TextField.class).setText("BBB2222");
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
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

        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });

        assertEquals(LocalDate.of(2003, 3, 3), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertEquals(LocalDate.of(2004, 4, 4), lookup("#birthRegister").queryAs(DatePicker.class).getValue());

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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("CCC3333"));
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("DDD4444"));

        interact(() -> {
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2002, 2, 2));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2003, 3, 3));
            lookup("#birthRegister").queryAs(DatePicker.class).setValue(LocalDate.of(2004, 4, 4));
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2003, 3, 3), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertEquals(LocalDate.of(2004, 4, 4), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());

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
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals(firstnameRegisterDefault));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(birthRegisterDefault, lookup("#birthRegister").queryAs(DatePicker.class).getValue());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#nhiRegister").queryAs(TextField.class).getText().equals("BBB2222"));
        assertTrue(lookup("#firstnameRegister").queryAs(TextField.class).getText().equals("FirstName2"));
        assertEquals(LocalDate.of(2002, 2, 2), lookup("#birthRegister").queryAs(DatePicker.class).getValue());
    }
}
