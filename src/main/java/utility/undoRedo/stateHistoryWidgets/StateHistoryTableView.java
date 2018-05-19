package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.TableView;
import java.util.ArrayList;

public class StateHistoryTableView extends StateHistoryControl {

    private TableView<String> tableView;

    private boolean undone = false;


    /**
     * Constructor which adds the current (base) state of the tableView to the history
     *
     * @param tableView - tableView the object will be keeping history of
     */

    public StateHistoryTableView(TableView<String> tableView) {
        this.tableView = tableView;
        states.add(tableView.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Stores the current state of the tableView and increments the index
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(tableView.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Sets the tableView to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            tableView.getSelectionModel().select((String) states.get(index));
            undone = true;
            return true;
        }
        return false;
    }

    /**
     * Resets the tableView to the state immediately prior to an undo
     */
    public boolean redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            tableView.getSelectionModel().select((String) states.get(index));
            return true;
        }
        return false;
    }
}
