package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the state history of a text entry field in the GUI
 */
public class StateHistoryTextEntry extends StateHistoryControl {

    /**
     * True if an undo has been executed, false otherwise - could be reset at exit from each interface
     */
    private boolean undone = false;


    /**
     * Constructor for the State History
     *
     * @param entry the Text Field whose state we are storing
     */
    public StateHistoryTextEntry(TextField entry) {
        this.control = entry;
        states.add(entry.getText());
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the TextField and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(((TextField) control).getText());
    }


    /**
     * Sets the TextField to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            ((TextField) control).setText((String) states.get(index));
            undone = true;
            return true;
        }
        return false;
    }


    /**
     * Resets the TextField to the state immediately prior to an undo
     */
    public boolean redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            ((TextField) control).setText((String) states.get(index));
            return true;
        }
        return false;
    }
}
