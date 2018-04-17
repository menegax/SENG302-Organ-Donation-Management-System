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

    /*
     * True if an undo has been executed, false otherwise - could be reset at exit from each interface
     */
    private boolean undone = false;

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
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(checkBox.isSelected());
    }

    /**
     * Sets the CheckBox to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            checkBox.setSelected(states.get(index));
            undone = true;
        }
    }

    /**
     * Resets the Checkbox to the state immediately prior to an undo
     */
    public void redo() {
        if (undone) {
            index += 1;
            checkBox.setSelected(states.get(index));
        }
    }

    /**
     * Gets the states of the Check Box
     * Currently only used in testing
     * @return the states of the check box
     */
    public ArrayList<Boolean> getStates() {
        return states;
    }

    /**
     * Gets the index of the current state of the checkbox
     * currently only used in testing
     * @return the index of the current state
     */
    public int getIndex() {
        return index;
    }
}
