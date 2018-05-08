package gui_test.undoScreenTests;

import controller.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import static javafx.scene.input.KeyCode.Z;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TestFX class to test the undo functionality of the patient donations update screen
 */
public class GUIUndoPatientDonationsTest extends ApplicationTest{

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
        Database.addPatient(new Patient("TFX9999", "Joe", dal,"Bloggs", LocalDate.of(1990, 2, 9)));

        main.start(stage);
        interact(() -> {
            lookup("#nhiLogin").queryAs(TextField.class).setText("TFX9999");
            lookup("#loginButton").queryAs(Button.class).fire();
            lookup("#profileButton").queryAs(Button.class).fire();
            lookup("#donationButton").queryAs(Button.class).fire();
        });
    }

    /**
     * Gets the values of the widgets on the screen before testing
     */
    @Before
    public void getFields() {
        interact(() -> {
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
        Database.resetDatabase();
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
     * Tests that all possible CheckBoxes can undo successfully
     */
    @Test
    public void undoCheckBoxes() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });

        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertEquals(heartCBDefault, lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertEquals(lungCBDefault, lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertEquals(intestineCBDefault, lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertEquals(corneaCBDefault, lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertEquals(middleearCBDefault, lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertEquals(skinCBDefault, lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertEquals(boneCBDefault, lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertEquals(bonemarrowCBDefault, lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertEquals(connectivetissueCBDefault, lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#heartCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#lungCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#intestineCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#corneaCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#middleearCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#skinCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#boneCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#bonemarrowCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
            lookup("#connectivetissueCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });

        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        assertEquals(heartCBDefault, lookup("#heartCB").queryAs(CheckBox.class).isSelected());
        assertEquals(lungCBDefault, lookup("#lungCB").queryAs(CheckBox.class).isSelected());
        assertEquals(intestineCBDefault, lookup("#intestineCB").queryAs(CheckBox.class).isSelected());
        assertEquals(corneaCBDefault, lookup("#corneaCB").queryAs(CheckBox.class).isSelected());
        assertEquals(middleearCBDefault, lookup("#middleearCB").queryAs(CheckBox.class).isSelected());
        assertEquals(skinCBDefault, lookup("#skinCB").queryAs(CheckBox.class).isSelected());
        assertEquals(boneCBDefault, lookup("#boneCB").queryAs(CheckBox.class).isSelected());
        assertEquals(bonemarrowCBDefault, lookup("#bonemarrowCB").queryAs(CheckBox.class).isSelected());
        assertEquals(connectivetissueCBDefault, lookup("#connectivetissueCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that all possible widgets can undo multiple times in a row
     */
    @Test
    public void undoMultipleTimes() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(false);
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(!lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());
    }

    /**
     * Tests that multiple different widgets can undo after each other
     */
    @Test
    public void undoOverMultipleWidgets() {
        // Check undo button first
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            lookup("#undoButton").queryAs(Button.class).fire();
        });
        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());

        // Check Ctrl Z next
        interact(() -> {
            lookup("#liverCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#kidneyCB").queryAs(CheckBox.class).setSelected(true);
            lookup("#pancreasCB").queryAs(CheckBox.class).setSelected(true);
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertTrue(lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
        interact(() -> {
            press(CONTROL).press(Z).release(CONTROL).release(Z);
        });
        assertEquals(liverCBDefault, lookup("#liverCB").queryAs(CheckBox.class).isSelected());
        assertEquals(kidneyCBDefault, lookup("#kidneyCB").queryAs(CheckBox.class).isSelected());
        assertEquals(pancreasCBDefault, lookup("#pancreasCB").queryAs(CheckBox.class).isSelected());
    }
}