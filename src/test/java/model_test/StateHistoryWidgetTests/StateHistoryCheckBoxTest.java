package model_test.StateHistoryWidgetTests;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.CheckBox;
import model.StateHistoryWidgets.StateHistoryCheckBox;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Junit test class for the StateHistoryCheckBox
 */
public class StateHistoryCheckBoxTest {

    private static CheckBox checkBox;

    /**
     * Creates the checkbox to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        latch.await();
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
}
