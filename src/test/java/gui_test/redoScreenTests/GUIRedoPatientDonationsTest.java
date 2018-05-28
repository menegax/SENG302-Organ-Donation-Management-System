package gui_test.redoScreenTests;

import main.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.Database;

import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.Y;
import static javafx.scene.input.KeyCode.Z;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the redo functionality of the patient donations update screen
 */
public class GUIRedoPatientDonationsTest extends ApplicationTest{

    Database database = Database.getDatabase();
    private Main main = new Main();

    private boolean liverCBDefault;
    private boolean kidneyCBDefault;
    private boolean pancreasCBDefault;
    private boolean heartCBDefault;
    private boolean lungCBDefault;
    private boolean intestineCBDefault;
    private boolean corneaCBDefault;
    private boolean middleearCBDefault;
    private boolean skinCBDefault;
    private boolean boneCBDefault;
    private boolean bonemarrowCBDefault;
    private boolean connectivetissueCBDefault;

    /**
     * Launches the main application
     * @param stage the stage to launch the app on
     * @throws Exception any exception that occurs during the launching of the program
     */
    @Override
    public void start(Stage stage) throws Exception {

        // add dummy patient
        ArrayList<String> dal = new ArrayList<>();
        dal.add("Middle");
        database.add(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#donationsButton").queryAs(Button.class).fire();
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
            liverCBDefault = lookup("#liverCB").queryAs(CheckBox.class).isSelected();
            kidneyCBDefault = lookup("#kidneyCB").queryAs(CheckBox.class).isSelected();
            pancreasCBDefault = lookup("#pancreasCB").queryAs(CheckBox.class).isSelected();
            heartCBDefault = lookup("#heartCB").queryAs(CheckBox.class).isSelected();
            lungCBDefault = lookup("#lungCB").queryAs(CheckBox.class).isSelected();
            intestineCBDefault = lookup("#intestineCB").queryAs(CheckBox.class).isSelected();
            corneaCBDefault = lookup("#corneaCB").queryAs(CheckBox.class).isSelected();
            middleearCBDefault = lookup("#middleearCB").queryAs(CheckBox.class).isSelected();
            skinCBDefault = lookup("#skinCB").queryAs(CheckBox.class).isSelected();
            boneCBDefault = lookup("#boneCB").queryAs(CheckBox.class).isSelected();
            bonemarrowCBDefault = lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected();
            connectivetissueCBDefault = lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected();
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
        verifyThat("#patientDonationsAnchorPane", Node::isVisible);
    }

    /**
     * Tests that all possible CheckBoxes can redo successfully
     */
    @Test
    public void redoCheckBoxes() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#heartCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#lungCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#intestineCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#middleearCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#skinCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#boneCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#heartCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#lungCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#intestineCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#corneaCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#middleearCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#skinCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#boneCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());

        interact(() -> {
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);;
            press(CONTROL).press(Y).release(CONTROL).release(Y);;
        });
        assertTrue(lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that all possible widgets can redo multiple times in a row
     */
    @Test
    public void redoMultipleTimes() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertFalse(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertFalse(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertFalse(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that multiple different widgets can redo after each other
     */
    @Test
    public void redoOverMultipleWidgets() {
        // Check redo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#redoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Y next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Y).release(CONTROL).release(Y);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
    }
}
