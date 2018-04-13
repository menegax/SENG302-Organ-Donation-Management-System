package model;

import controller.IUndoRedo;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;

/**
 * Represents the state history of a check box in the GUI
 */
public class StateHistoryCheckBox implements IUndoRedo {

    /**
     * The CheckBox this object holds the states for
     */
    private CheckBox checkBox;

    /**
     * The states of the CheckBox
     */
    private ArrayList<Boolean> states = new ArrayList<>();

    /**
     * The index of the current state in the ArrayList
     */
    private int index = 0;

    /**
     * Constructor for the State History
     * @param checkBox the CheckBox whose state we are storing
     */
    public StateHistoryCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
        states.add(checkBox.isSelected());
    }

    /**
     * Called whenever the user makes an action
     * Stores the current state of the CheckBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        states.add(checkBox.isSelected());
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
    }

    /**
     * Sets the CheckBox to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            checkBox.setSelected(states.get(index));
        }
    }

    public void redo() {

    }
}
