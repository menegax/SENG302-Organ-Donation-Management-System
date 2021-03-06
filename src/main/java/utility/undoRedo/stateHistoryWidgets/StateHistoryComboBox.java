package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.ComboBox;

import java.util.ArrayList;

/**
 * Represents the state history of a ComboBox field in the GUI
 * The ComboBox must have strings as it options
 */
public class StateHistoryComboBox extends StateHistoryControl {

    /**
     * Constructor for the state history
     *
     * @param comboBox the ComboBox whose state we are storing
     */
    public StateHistoryComboBox(ComboBox<String> comboBox) {
        this.control = comboBox;
        states.add(comboBox.getSelectionModel().getSelectedItem());
        setUpUndoableWrapper(this.control);
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the ComboBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(((ComboBox) control).getSelectionModel().getSelectedItem());
    }


    /**
     * Sets the ComboBox to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            // Cast is always safe
            ((ComboBox<String>) control).getSelectionModel().select((String) states.get(index));
            return true;
        }
        return false;
    }

    /**
     * Resets the ComboBox to the state immediately prior to an undo
     */
    public boolean redo() {
        if (index + 1 < states.size()) {
            index += 1;
            // Cast is always safe
            ((ComboBox<String>) control).getSelectionModel().select((String) states.get(index));
            return true;
        }
        return false;
    }
}
