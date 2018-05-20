package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the state history of a check box in the GUI
 */
public class StateHistoryCheckBox extends StateHistoryControl {

    /**
     * True if an undo has been executed, false otherwise - could be reset at exit from each interface
     */
    private boolean undone = false;


    /**
     * Constructor for the State History
     *
     * @param checkBox the CheckBox whose state we are storing
     */
    public StateHistoryCheckBox(CheckBox checkBox) {
        this.control = checkBox;
        states.add(checkBox.isSelected());
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the CheckBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(((CheckBox) control).isSelected());
    }


    /**
     * Sets the CheckBox to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            ((CheckBox) control).setSelected((Boolean) states.get(index));
            undone = true;
            return true;
        }
        return false;
    }


    /**
     * Resets the Checkbox to the state immediately prior to an undo
     */
    public boolean redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            ((CheckBox) control).setSelected((Boolean) states.get(index));
            return true;
        }
        return false;
    }

}
