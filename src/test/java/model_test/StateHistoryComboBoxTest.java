package model_test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import model.StateHistoryComboBox;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Junit test class for the StateHistoryComboBox
 */
public class StateHistoryComboBoxTest {

    private static ComboBox comboBox;

    /**
     * Creates the ComboBox to be used and sets up the JavaFX environment so JavaFX objects can be created
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
        comboBox = new ComboBox();
        ArrayList<String> items = new ArrayList<>();
        items.add("A");
        items.add("B");
        items.add("C");
        comboBox.setItems(FXCollections.observableArrayList(items));
    }

    /**
     * Tests the constructor for a stateHistoryComboBox
     */
    @Test
    public void testConstructor() {
        comboBox.getSelectionModel().select(0);
        StateHistoryComboBox stateHistoryComboBox = new StateHistoryComboBox(comboBox);
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add("A");
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(0, stateHistoryComboBox.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryComboBox
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        comboBox.getSelectionModel().select(0);
        StateHistoryComboBox stateHistoryComboBox = new StateHistoryComboBox(comboBox);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryComboBox.store();
        comboBox.getSelectionModel().select(1);
        stateHistoryComboBox.store();
        checkList.add("A");
        checkList.add("A");
        checkList.add("B");
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(2, stateHistoryComboBox.getIndex());

        stateHistoryComboBox.undo();
        stateHistoryComboBox.undo();
        comboBox.getSelectionModel().select(2);
        stateHistoryComboBox.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add("C");
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(1, stateHistoryComboBox.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryComboBox
     */
    @Test
    public void testUndo() {
        comboBox.getSelectionModel().select(0);
        StateHistoryComboBox stateHistoryComboBox = new StateHistoryComboBox(comboBox);
        ArrayList<String> checkList = new ArrayList<>();
        stateHistoryComboBox.store();
        stateHistoryComboBox.undo();
        checkList.add("A");
        checkList.add("A");
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(0, stateHistoryComboBox.getIndex());

        stateHistoryComboBox.undo();
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(0, stateHistoryComboBox.getIndex());

        comboBox.getSelectionModel().select(1);
        stateHistoryComboBox.store();
        comboBox.getSelectionModel().select(2);
        stateHistoryComboBox.store();
        stateHistoryComboBox.undo();
        checkList.remove(1);
        checkList.add("B");
        checkList.add("C");
        assertEquals(checkList, stateHistoryComboBox.getStates());
        assertEquals(1, stateHistoryComboBox.getIndex());
        assertEquals("B", comboBox.getSelectionModel().getSelectedItem());
        stateHistoryComboBox.undo();
        assertEquals("A", comboBox.getSelectionModel().getSelectedItem());
    }
}
