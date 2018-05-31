package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.CheckBox;
import utility.undoRedo.stateHistoryWidgets.StateHistoryCheckBox;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

/**
 * Junit test class for the StateHistoryCheckBox
 */
public class StateHistoryCheckBoxTest {

    private static CheckBox checkBox;

    /**
     * Creates the checkbox to be used and sets up the JavaFX environment so JavaFX objects can be created
     */
    @BeforeClass
    public static void setup() {
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {}); //todo look at. does this do thangs?
        checkBox = new CheckBox();
        checkBox.setSelected(false);
    }

    /**
     * Tests the constructor for a stateHistoryCheckBox
     */
    @Test
    public void testConstructor() {
        checkBox.setSelected(false);
        StateHistoryCheckBox stateHistoryCheckBox = new StateHistoryCheckBox(checkBox);
        ArrayList<Boolean> checkList = new ArrayList<>();
        checkList.add(false);
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(0, stateHistoryCheckBox.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryCheckBox
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        checkBox.setSelected(false);
        StateHistoryCheckBox stateHistoryCheckBox = new StateHistoryCheckBox(checkBox);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryCheckBox.store();
        checkBox.setSelected(true);
        stateHistoryCheckBox.store();
        checkList.add(false);
        checkList.add(false);
        checkList.add(true);
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(2, stateHistoryCheckBox.getIndex());

        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.undo();
        checkBox.setSelected(true);
        stateHistoryCheckBox.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add(true);
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(1, stateHistoryCheckBox.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryCheckBox
     */
    @Test
    public void testUndo() {
        checkBox.setSelected(false);
        StateHistoryCheckBox stateHistoryCheckBox = new StateHistoryCheckBox(checkBox);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryCheckBox.store();
        stateHistoryCheckBox.undo();
        checkList.add(false);
        checkList.add(false);
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(0, stateHistoryCheckBox.getIndex());

        stateHistoryCheckBox.undo();
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(0, stateHistoryCheckBox.getIndex());

        checkBox.setSelected(true);
        stateHistoryCheckBox.store();
        checkBox.setSelected(false);
        stateHistoryCheckBox.store();
        stateHistoryCheckBox.undo();
        checkList.remove(1);
        checkList.add(true);
        checkList.add(false);
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(1, stateHistoryCheckBox.getIndex());
        assertTrue(checkBox.isSelected());
        stateHistoryCheckBox.undo();
        assertFalse(checkBox.isSelected());
    }

    /**
     * Tests the redo method of the StateHistoryCheckBox
     */
    @Test
    public void testRedo() {
        checkBox.setSelected(false);
        StateHistoryCheckBox stateHistoryCheckBox = new StateHistoryCheckBox(checkBox);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryCheckBox.store();
        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.redo();
        checkList.add(false);
        checkList.add(false);
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(1, stateHistoryCheckBox.getIndex());

        stateHistoryCheckBox.redo();
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(1, stateHistoryCheckBox.getIndex());

        stateHistoryCheckBox.undo();
        checkBox.setSelected(true);
        stateHistoryCheckBox.store();
        checkBox.setSelected(false);
        stateHistoryCheckBox.store();
        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.redo();
        stateHistoryCheckBox.redo();
        checkList.remove(1);
        checkList.add(true);
        checkList.add(false);
        assertEquals(checkList, stateHistoryCheckBox.getStates());
        assertEquals(2, stateHistoryCheckBox.getIndex());
        assertFalse(checkBox.isSelected());
        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.undo();
        stateHistoryCheckBox.redo();
        assertTrue(checkBox.isSelected());
    }
}
