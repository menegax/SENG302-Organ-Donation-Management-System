package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.junit.Ignore;
import utility.undoRedo.stateHistoryWidgets.StateHistoryComboBox;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static utility.UserActionHistory.userActions;

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
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {});
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

    /**
     * Tests the redo method of the StateHistoryComboBox
     */
    @Test
    public void testRedo() {
        comboBox.getSelectionModel().select(0);
        StateHistoryComboBox stateHistoryComboBox = new StateHistoryComboBox(comboBox);
        ArrayList<String> comboList = new ArrayList<>();
        stateHistoryComboBox.store();
        stateHistoryComboBox.undo();
        stateHistoryComboBox.redo();
        comboList.add("A");
        comboList.add("A");
        assertEquals(comboList, stateHistoryComboBox.getStates());
        assertEquals(1, stateHistoryComboBox.getIndex());

        stateHistoryComboBox.redo();
        assertEquals(comboList, stateHistoryComboBox.getStates());
        assertEquals(1, stateHistoryComboBox.getIndex());

        stateHistoryComboBox.undo();
        comboBox.getSelectionModel().select(1);
        stateHistoryComboBox.store();
        comboBox.getSelectionModel().select(2);
        stateHistoryComboBox.store();
        stateHistoryComboBox.undo();
        stateHistoryComboBox.undo();
        stateHistoryComboBox.redo();
        stateHistoryComboBox.redo();
        comboList.remove(1);
        comboList.add("B");
        comboList.add("C");
        assertEquals(comboList, stateHistoryComboBox.getStates());
        assertEquals(2, stateHistoryComboBox.getIndex());
        assertEquals("C", comboBox.getSelectionModel().getSelectedItem());
        stateHistoryComboBox.undo();
        stateHistoryComboBox.undo();
        stateHistoryComboBox.redo();
        assertEquals("B", comboBox.getSelectionModel().getSelectedItem());
    }
}
