package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryChoiceBox extends StateHistoryControl {

    private ChoiceBox<String> choiceBox;

    private boolean undone = false;


    /**
     * Constructor which adds the current (base) state of the choice box to the history
     *
     * @param choiceBox - ChoiceBox the object will be keeping history of
     */

    public StateHistoryChoiceBox(ChoiceBox<String> choiceBox) {
        this.choiceBox = choiceBox;
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
        states.add(choiceBox.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Sets the ChoiceBox to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            choiceBox.getSelectionModel().select((String) states.get(index));
            undone = true;
        }
    }

    /**
     * Resets the ChoiceBox to the state immediately prior to an undo
     */
    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            choiceBox.getSelectionModel().select((String) states.get(index));
        }
    }
}
