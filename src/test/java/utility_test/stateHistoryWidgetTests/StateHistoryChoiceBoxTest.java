package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import utility.undoRedo.stateHistoryWidgets.StateHistoryChoiceBox;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static utility.UserActionHistory.userActions;

/**
 * Junit test class for the StateHistoryChoiceBox
 */
public class StateHistoryChoiceBoxTest {

    private static ChoiceBox choiceBox;

    /**
     * Creates the ChoiceBox to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {});
        choiceBox = new ChoiceBox();
        ArrayList<String> items = new ArrayList<>();
        items.add("A");
        items.add("B");
        items.add("C");
        choiceBox.setItems(FXCollections.observableArrayList(items));
    }

    /**
     * Tests the constructor for a stateHistoryChoiceBox
     */
    @Test
    public void testConstructor() {
        choiceBox.getSelectionModel().select(0);
        StateHistoryChoiceBox stateHistoryChoiceBox = new StateHistoryChoiceBox(choiceBox);
        ArrayList<String> choiceList = new ArrayList<>();
        choiceList.add("A");
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(0, stateHistoryChoiceBox.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryChoiceBox
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        choiceBox.getSelectionModel().select(0);
        StateHistoryChoiceBox stateHistoryChoiceBox = new StateHistoryChoiceBox(choiceBox);
        ArrayList<String> choiceList = new ArrayList<>();
        stateHistoryChoiceBox.store();
        choiceBox.getSelectionModel().select(1);
        stateHistoryChoiceBox.store();
        choiceList.add("A");
        choiceList.add("A");
        choiceList.add("B");
        // Undo will fails if these asserts fail
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(2, stateHistoryChoiceBox.getIndex());

        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.undo();
        choiceBox.getSelectionModel().select(2);
        stateHistoryChoiceBox.store();
        choiceList = new ArrayList<>(choiceList.subList(0, 1));
        choiceList.add("C");
        // These asserts will fail if undo fails
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(1, stateHistoryChoiceBox.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryChoiceBox
     */
    @Test
    public void testUndo() {
        choiceBox.getSelectionModel().select(0);
        StateHistoryChoiceBox stateHistoryChoiceBox = new StateHistoryChoiceBox(choiceBox);
        ArrayList<String> choiceList = new ArrayList<>();
        stateHistoryChoiceBox.store();
        stateHistoryChoiceBox.undo();
        choiceList.add("A");
        choiceList.add("A");
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(0, stateHistoryChoiceBox.getIndex());

        stateHistoryChoiceBox.undo();
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(0, stateHistoryChoiceBox.getIndex());

        choiceBox.getSelectionModel().select(1);
        stateHistoryChoiceBox.store();
        choiceBox.getSelectionModel().select(2);
        stateHistoryChoiceBox.store();
        stateHistoryChoiceBox.undo();
        choiceList.remove(1);
        choiceList.add("B");
        choiceList.add("C");
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(1, stateHistoryChoiceBox.getIndex());
        assertEquals("B", choiceBox.getSelectionModel().getSelectedItem());
        stateHistoryChoiceBox.undo();
        assertEquals("A", choiceBox.getSelectionModel().getSelectedItem());
    }

    /**
     * Tests the redo method of the StateHistoryChoiceBox
     */
    @Test
    public void testRedo() {
        choiceBox.getSelectionModel().select(0);
        StateHistoryChoiceBox stateHistoryChoiceBox = new StateHistoryChoiceBox(choiceBox);
        ArrayList<String> choiceList = new ArrayList<>();
        stateHistoryChoiceBox.store();
        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.redo();
        choiceList.add("A");
        choiceList.add("A");
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(1, stateHistoryChoiceBox.getIndex());

        stateHistoryChoiceBox.redo();
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(1, stateHistoryChoiceBox.getIndex());

        stateHistoryChoiceBox.undo();
        choiceBox.getSelectionModel().select(1);
        stateHistoryChoiceBox.store();
        choiceBox.getSelectionModel().select(2);
        stateHistoryChoiceBox.store();
        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.redo();
        stateHistoryChoiceBox.redo();
        choiceList.remove(1);
        choiceList.add("B");
        choiceList.add("C");
        assertEquals(choiceList, stateHistoryChoiceBox.getStates());
        assertEquals(2, stateHistoryChoiceBox.getIndex());
        assertEquals("C", choiceBox.getSelectionModel().getSelectedItem());
        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.undo();
        stateHistoryChoiceBox.redo();
        assertEquals("B", choiceBox.getSelectionModel().getSelectedItem());
    }
}
