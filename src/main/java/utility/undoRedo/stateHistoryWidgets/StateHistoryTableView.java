package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.TableView;
import java.util.ArrayList;

public class StateHistoryTableView extends StateHistoryControl {

    /**
     * Constructor which adds the current (base) state of the tableView to the history
     *
     * @param tableView - tableView the object will be keeping history of
     */

    public StateHistoryTableView(TableView<Object> tableView) {
        this.control = tableView;
        states.add(new ArrayList<>(tableView.getItems()));
        setUpUndoableStage();
    }


    /**
     * Stores the current state of the tableView and increments the index
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(new ArrayList<>(((TableView<Object>) control).getItems()));
    }


    /**
     * Sets the tableView to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            ((TableView<Object>) control).getItems().clear();
            for (Object object : (ArrayList<Object>) states.get(index)) {
                ((TableView<Object>) control).getItems().add(object);
            }
            return true;
        }
        return false;
    }

    /**
     * Resets the tableView to the state immediately prior to an undo
     */
    public boolean redo() {
        if (index + 1 < states.size()) {
            index += 1;
            ((TableView<Object>) control).getItems().clear();
            for (Object object : (ArrayList<Object>) states.get(index)) {
                ((TableView<Object>) control).getItems().add(object);
            }
            return true;
        }
        return false;
    }
}
