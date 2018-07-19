package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import utility.undoRedo.stateHistoryWidgets.StateHistoryTableView;

import java.util.ArrayList;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static utility.UserActionHistory.userActions;

/**
 * Junit test class for the StateHistoryTableView
 */

@Ignore
public class StateHistoryTableViewTest {

    private static TableView tableView;

    /**
     * Creates the TableView to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        userActions.setLevel(Level.OFF);
        PlatformImpl.startup(() -> {});
        tableView = new TableView();
        tableView.getItems().clear();
    }

    /**
     * Tests the constructor for a stateHistoryTableView
     */
    @Test
    public void testConstructor() {
        StateHistoryTableView stateHistoryTableView = new StateHistoryTableView(tableView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(0, stateHistoryTableView.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryTableView
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        tableView.getItems().clear();
        StateHistoryTableView stateHistoryTableView = new StateHistoryTableView(tableView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryTableView.store();
        tableView.getItems().clear();
        tableView.getItems().add("A");
        stateHistoryTableView.store();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<String>(){{add("A");}});
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(2, stateHistoryTableView.getIndex());

        stateHistoryTableView.undo();
        stateHistoryTableView.undo();
        tableView.getItems().clear();
        tableView.getItems().add("B");
        stateHistoryTableView.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add(new ArrayList<String>(){{add("B");}});
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(1, stateHistoryTableView.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryTableView
     */
    @Test
    public void testUndo() {
        tableView.getItems().clear();
        StateHistoryTableView stateHistoryTableView = new StateHistoryTableView(tableView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryTableView.store();
        stateHistoryTableView.undo();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(0, stateHistoryTableView.getIndex());

        stateHistoryTableView.undo();
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(0, stateHistoryTableView.getIndex());
        tableView.getItems().clear();
        tableView.getItems().add("A");

        stateHistoryTableView.store();
        tableView.getItems().clear();
        tableView.getItems().add("B");
        stateHistoryTableView.store();
        stateHistoryTableView.undo();
        checkList.remove(1);
        checkList.add(new ArrayList<String>(){{add("A");}});
        checkList.add(new ArrayList<String>(){{add("B");}});
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(1, stateHistoryTableView.getIndex());
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("A");}}), tableView.getItems());
        stateHistoryTableView.undo();
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>()), tableView.getItems());
    }

    /**
     * Tests the redo method of the StateHistoryTableView
     */
    @Test
    public void testRedo() {
        tableView.getItems().setAll(new ArrayList<String>());
        StateHistoryTableView stateHistoryTableView = new StateHistoryTableView(tableView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryTableView.store();
        stateHistoryTableView.undo();
        stateHistoryTableView.redo();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(1, stateHistoryTableView.getIndex());

        stateHistoryTableView.redo();
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(1, stateHistoryTableView.getIndex());

        stateHistoryTableView.undo();
        tableView.getItems().setAll(new ArrayList<String>(){{add("A");}});
        stateHistoryTableView.store();
        tableView.getItems().setAll(new ArrayList<String>(){{add("B");}});
        stateHistoryTableView.store();
        stateHistoryTableView.undo();
        stateHistoryTableView.undo();
        stateHistoryTableView.redo();
        stateHistoryTableView.redo();
        checkList.remove(1);
        checkList.add(new ArrayList<String>(){{add("A");}});
        checkList.add(new ArrayList<String>(){{add("B");}});
        assertEquals(checkList, stateHistoryTableView.getStates());
        assertEquals(2, stateHistoryTableView.getIndex());
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("B");}}), tableView.getItems());
        stateHistoryTableView.undo();
        stateHistoryTableView.undo();
        stateHistoryTableView.redo();
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("A");}}), tableView.getItems());
    }
}
