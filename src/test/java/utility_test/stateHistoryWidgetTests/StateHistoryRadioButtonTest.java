package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.RadioButton;
import org.junit.Ignore;
import utility.undoRedo.stateHistoryWidgets.StateHistoryRadioButton;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

/**
 * Junit test class for the StateHistoryRadioButton
 */
public class StateHistoryRadioButtonTest {

    private static RadioButton radioButton;

    /**
     * Creates the radioButton to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {});
        radioButton = new RadioButton();
        radioButton.setSelected(false);
    }

    /**
     * Tests the constructor for a stateHistoryRadioButton
     */
    @Test
    public void testConstructor() {
        radioButton.setSelected(false);
        StateHistoryRadioButton stateHistoryRadioButton = new StateHistoryRadioButton(radioButton);
        ArrayList<Boolean> checkList = new ArrayList<>();
        checkList.add(false);
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(0, stateHistoryRadioButton.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryRadioButton
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        radioButton.setSelected(false);
        StateHistoryRadioButton stateHistoryRadioButton = new StateHistoryRadioButton(radioButton);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryRadioButton.store();
        radioButton.setSelected(true);
        stateHistoryRadioButton.store();
        checkList.add(false);
        checkList.add(false);
        checkList.add(true);
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(2, stateHistoryRadioButton.getIndex());

        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.undo();
        radioButton.setSelected(true);
        stateHistoryRadioButton.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add(true);
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(1, stateHistoryRadioButton.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryRadioButton
     */
    @Test
    public void testUndo() {
        radioButton.setSelected(false);
        StateHistoryRadioButton stateHistoryRadioButton = new StateHistoryRadioButton(radioButton);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryRadioButton.store();
        stateHistoryRadioButton.undo();
        checkList.add(false);
        checkList.add(false);
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(0, stateHistoryRadioButton.getIndex());

        stateHistoryRadioButton.undo();
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(0, stateHistoryRadioButton.getIndex());

        radioButton.setSelected(true);
        stateHistoryRadioButton.store();
        radioButton.setSelected(false);
        stateHistoryRadioButton.store();
        stateHistoryRadioButton.undo();
        checkList.remove(1);
        checkList.add(true);
        checkList.add(false);
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(1, stateHistoryRadioButton.getIndex());
        assertTrue(radioButton.isSelected());
        stateHistoryRadioButton.undo();
        assertFalse(radioButton.isSelected());
    }

    /**
     * Tests the redo method of the StateHistoryRadioButton
     */
    @Test
    public void testRedo() {
        radioButton.setSelected(false);
        StateHistoryRadioButton stateHistoryRadioButton = new StateHistoryRadioButton(radioButton);
        ArrayList<Boolean> checkList = new ArrayList<>();
        stateHistoryRadioButton.store();
        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.redo();
        checkList.add(false);
        checkList.add(false);
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(1, stateHistoryRadioButton.getIndex());

        stateHistoryRadioButton.redo();
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(1, stateHistoryRadioButton.getIndex());

        stateHistoryRadioButton.undo();
        radioButton.setSelected(true);
        stateHistoryRadioButton.store();
        radioButton.setSelected(false);
        stateHistoryRadioButton.store();
        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.redo();
        stateHistoryRadioButton.redo();
        checkList.remove(1);
        checkList.add(true);
        checkList.add(false);
        assertEquals(checkList, stateHistoryRadioButton.getStates());
        assertEquals(2, stateHistoryRadioButton.getIndex());
        assertFalse(radioButton.isSelected());
        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.undo();
        stateHistoryRadioButton.redo();
        assertTrue(radioButton.isSelected());
    }
}
