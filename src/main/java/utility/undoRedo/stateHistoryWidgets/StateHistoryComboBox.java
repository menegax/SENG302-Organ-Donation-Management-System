package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the state history of a ComboBox field in the GUI
 * The ComboBox must have strings as it options
 */
public class StateHistoryComboBox extends StateHistoryControl {

    /**
     * The ComboBox this object holds the states for
     */
    private ComboBox<String> comboBox;

    /*
     * True if an undo has been executed, false otherwise - could be reset at exit from each interface
     */
    private boolean undone = false;


    /**
     * Constructor for the state history
     *
     * @param comboBox the ComboBox whose state we are storing
     */
    public StateHistoryComboBox(ComboBox<String> comboBox) {
        this.comboBox = comboBox;
        states.add(comboBox.getSelectionModel().getSelectedItem());
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the ComboBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(comboBox.getSelectionModel().getSelectedItem());
    }


    /**
     * Sets the ComboBox to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            comboBox.getSelectionModel().select((String) states.get(index));
            undone = true;
        }
    }

    /**
     * Resets the ComboBox to the state immediately prior to an undo
     */
    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            comboBox.getSelectionModel().select((String) states.get(index));
        }
    }
}
