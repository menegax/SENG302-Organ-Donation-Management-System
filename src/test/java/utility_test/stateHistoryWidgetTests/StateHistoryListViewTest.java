package utility_test.stateHistoryWidgetTests;

import com.sun.javafx.application.PlatformImpl;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.undoRedo.stateHistoryWidgets.StateHistoryListView;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Junit test class for the StateHistoryListView
 */
public class StateHistoryListViewTest {

    private static ListView listView;

    /**
     * Creates the ListView to be used and sets up the JavaFX environment so JavaFX objects can be created
     * @throws InterruptedException if the JavaFX environment is interrupted
     */
    @BeforeClass
    public static void setup() throws InterruptedException{
        PlatformImpl.startup(() -> {});
        listView = new ListView();
        listView.getItems().clear();
    }

    /**
     * Tests the constructor for a stateHistoryListView
     */
    @Test
    public void testConstructor() {
        StateHistoryListView stateHistoryListView = new StateHistoryListView(listView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(0, stateHistoryListView.getIndex());
    }

    /**
     * Tests the store method of the stateHistoryListView
     * Some assertions require undo to be functional
     */
    @Test
    public void testStore() {
        listView.getItems().clear();
        StateHistoryListView stateHistoryListView = new StateHistoryListView(listView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryListView.store();
        listView.getItems().clear();
        listView.getItems().add("A");
        stateHistoryListView.store();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<String>(){{add("A");}});
        // Undo will fails if these asserts fail
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(2, stateHistoryListView.getIndex());

        stateHistoryListView.undo();
        stateHistoryListView.undo();
        listView.getItems().clear();
        listView.getItems().add("B");
        stateHistoryListView.store();
        checkList = new ArrayList<>(checkList.subList(0, 1));
        checkList.add(new ArrayList<String>(){{add("B");}});
        // These asserts will fail if undo fails
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(1, stateHistoryListView.getIndex());
    }

    /**
     * Tests the undo method of the StateHistoryListView
     */
    @Test
    public void testUndo() {
        listView.getItems().clear();
        StateHistoryListView stateHistoryListView = new StateHistoryListView(listView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryListView.store();
        stateHistoryListView.undo();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(0, stateHistoryListView.getIndex());

        stateHistoryListView.undo();
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(0, stateHistoryListView.getIndex());
        listView.getItems().clear();
        listView.getItems().add("A");

        stateHistoryListView.store();
        listView.getItems().clear();
        listView.getItems().add("B");
        stateHistoryListView.store();
        stateHistoryListView.undo();
        checkList.remove(1);
        checkList.add(new ArrayList<String>(){{add("A");}});
        checkList.add(new ArrayList<String>(){{add("B");}});
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(1, stateHistoryListView.getIndex());
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("A");}}), listView.getItems());
        stateHistoryListView.undo();
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>()), listView.getItems());
    }

    /**
     * Tests the redo method of the StateHistoryListView
     */
    @Test
    public void testRedo() {
        listView.getItems().setAll(new ArrayList<String>());
        StateHistoryListView stateHistoryListView = new StateHistoryListView(listView);
        ArrayList<ArrayList<String>> checkList = new ArrayList<>();
        stateHistoryListView.store();
        stateHistoryListView.undo();
        stateHistoryListView.redo();
        checkList.add(new ArrayList<>());
        checkList.add(new ArrayList<>());
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(1, stateHistoryListView.getIndex());

        stateHistoryListView.redo();
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(1, stateHistoryListView.getIndex());

        stateHistoryListView.undo();
        listView.getItems().setAll(new ArrayList<String>(){{add("A");}});
        stateHistoryListView.store();
        listView.getItems().setAll(new ArrayList<String>(){{add("B");}});
        stateHistoryListView.store();
        stateHistoryListView.undo();
        stateHistoryListView.undo();
        stateHistoryListView.redo();
        stateHistoryListView.redo();
        checkList.remove(1);
        checkList.add(new ArrayList<String>(){{add("A");}});
        checkList.add(new ArrayList<String>(){{add("B");}});
        assertEquals(checkList, stateHistoryListView.getStates());
        assertEquals(2, stateHistoryListView.getIndex());
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("B");}}), listView.getItems());
        stateHistoryListView.undo();
        stateHistoryListView.undo();
        stateHistoryListView.redo();
        assertEquals(FXCollections.observableArrayList(new ArrayList<String>(){{add("A");}}), listView.getItems());
    }
}
