package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.RadioButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryRadioButton extends StateHistoryControl {

    private boolean undone = false;

    /**
     * Constructor for a StateHistoryRadioButton
     * @param radioButton the radioButton this object stores the state of
     */
    public StateHistoryRadioButton(RadioButton radioButton) {
        this.control = radioButton;
        states.add(radioButton.isSelected());

    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the RadioButtons and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(((RadioButton) control).isSelected());
    }


    /**
     * Sets the RadioButton to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            ((RadioButton) control).setSelected((Boolean) states.get(index));
            undone = true;
            return true;
        }
        return false;
    }


    /**
     * Resets the RadioButton to the state immediately prior to an undo
     */
    public boolean redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            ((RadioButton) control).setSelected((Boolean) states.get(index));
            return true;
        }
        return false;
    }
}
