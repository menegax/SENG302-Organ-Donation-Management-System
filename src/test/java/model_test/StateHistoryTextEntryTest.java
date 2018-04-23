package model_test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import model.StateHistoryWidgets.StateHistoryTextEntry;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Junit test class for the StateHistoryTextEntry
 */
public class StateHistoryTextEntryTest {

    private static TextField textField;

    /**
     * Creates the TextField to be used and sets up the JavaFX environment so JavaFX objects can be created
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
        textField = new TextField();
    }

    /**
     * Tests the constructor for a stateHistoryTextEntry
     */
    @Test
    public void testConstructor() {
        textField.setText("");
        StateHistoryTextEntry stateHistoryTextEntry = new StateHistoryTextEntry(textField);
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add("");
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(0, stateHistoryTextEntry.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryTextEntry
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        textField.setText("");
        StateHistoryTextEntry stateHistoryTextEntry = new StateHistoryTextEntry(textField);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryTextEntry.store();
        textField.setText("A");
        stateHistoryTextEntry.store();
        checkList.add("");
        checkList.add("");
        checkList.add("A");
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(2, stateHistoryTextEntry.getIndex());

        stateHistoryTextEntry.undo();
        stateHistoryTextEntry.undo();
        textField.setText("B");
        stateHistoryTextEntry.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add("B");
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(1, stateHistoryTextEntry.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryTextEntry
     */
    @Test
    public void testUndo() {
        textField.setText("");
        StateHistoryTextEntry stateHistoryTextEntry = new StateHistoryTextEntry(textField);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryTextEntry.store();
        stateHistoryTextEntry.undo();
        checkList.add("");
        checkList.add("");
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(0, stateHistoryTextEntry.getIndex());

        stateHistoryTextEntry.undo();
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(0, stateHistoryTextEntry.getIndex());

        textField.setText("A");
        stateHistoryTextEntry.store();
        textField.setText("B");
        stateHistoryTextEntry.store();
        stateHistoryTextEntry.undo();
        checkList.remove(1);
        checkList.add("A");
        checkList.add("B");
        assertEquals(checkList, stateHistoryTextEntry.getStates());
        assertEquals(1, stateHistoryTextEntry.getIndex());
        assertEquals("A", textField.getText());
        stateHistoryTextEntry.undo();
        assertEquals("", textField.getText());
    }
}
