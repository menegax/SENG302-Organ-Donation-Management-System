package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.ListView;

import java.util.ArrayList;

public class StateHistoryListView extends StateHistoryControl {

    /**
     * Constructor which adds the current (base) state of the listView to the history
     *
     * @param listView - listView the object will be keeping history of
     */

    public StateHistoryListView(ListView<Object> listView) {
        control = listView;
        states.add(new ArrayList<>(listView.getItems()));
        setUpUndoableStage();
    }


    /**
     * Stores the current state of the listView and increments the index
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(new ArrayList<>(((ListView<Object>) control).getItems()));
    }


    /**
     * Sets the listView to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
//            ((ListView<Object>) control).getItems().clear();
//            for (Object object : (ArrayList<Object>) states.get(index)) {
//                ((ListView<Object>) control).getItems().add(object);
//            }
            return true;
        }
        return false;
    }

    /**
     * Resets the listView to the state immediately prior to an undo
     */
    public boolean redo() {
        if (index + 1 < states.size()) {
            index += 1;
//            ((ListView<Object>) control).getItems().clear();
//            for (Object object : (ArrayList<Object>) states.get(index)) {
//                ((ListView<Object>) control).getItems().add(object);
//            }
            return true;
        }
        return false;
    }
}
