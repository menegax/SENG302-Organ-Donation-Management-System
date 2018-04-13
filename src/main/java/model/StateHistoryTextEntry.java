package model;

import controller.IUndoRedo;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Represents the state history of a text entry field in the GUI
 */
public class StateHistoryTextEntry implements IUndoRedo {

    /**
     * The TextField this object holds the states for
     */
    private TextField entry;

    /**
     * The states of the TextField
     */
    private ArrayList<String> states = new ArrayList<>();

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
     * @param entry the Text Field whose state we are storing
     */
    public StateHistoryTextEntry(TextField entry) {
        this.entry = entry;
        states.add(entry.getText());
    }

    /**
     * Called whenever the user makes an action
     * Stores the current state of the TextField and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        states.add(entry.getText());
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
    }

    /**
     * Sets the TextField to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            entry.setText(states.get(index));
            undone = true;
        }
    }

    /**
     * Resets the TextField to the state immediately prior to an undo
     */
    public void redo() {
        if (undone) {
            index += 1;
            entry.setText(states.get(index));
        }
    }
}
