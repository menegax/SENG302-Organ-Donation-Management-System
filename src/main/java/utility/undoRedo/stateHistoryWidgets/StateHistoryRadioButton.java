package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.RadioButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryRadioButton extends StateHistoryControl {

    private RadioButton radioButton;

    private boolean undone = false;


    public StateHistoryRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
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
        states.add(radioButton.isSelected());
    }


    /**
     * Sets the RadioButton to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            radioButton.setSelected((Boolean) states.get(index));
            undone = true;
        }
    }


    /**
     * Resets the RadioButton to the state immediately prior to an undo
     */
    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            radioButton.setSelected((Boolean) states.get(index));
        }
    }
}
