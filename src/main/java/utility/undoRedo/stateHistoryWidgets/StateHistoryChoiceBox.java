package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryChoiceBox extends StateHistoryControl {

    private boolean undone = false;

    /**
     * Constructor which adds the current (base) state of the choice box to the history
     *
     * @param choiceBox - ChoiceBox the object will be keeping history of
     */

    public StateHistoryChoiceBox(ChoiceBox<String> choiceBox) {
        this.control = choiceBox;
        states.add(choiceBox.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the ChoiceBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(((ChoiceBox) control).getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Sets the ChoiceBox to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            // Cast will always be safe
            ((ChoiceBox<String>) control).getSelectionModel().select((String) states.get(index));
            undone = true;
            return true;
        }
        return false;
    }

    /**
     * Resets the ChoiceBox to the state immediately prior to an undo
     */
    public boolean redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            // Cast will always be safe
            ((ChoiceBox<String>) control).getSelectionModel().select((String) states.get(index));
            return true;
        }
        return false;
    }
}
