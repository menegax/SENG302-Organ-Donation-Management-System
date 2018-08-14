package utility.undoRedo.stateHistoryWidgets;

import javafx.event.Event;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;

import java.util.ArrayList;

/**
 * Represents the state history of a text entry field in the GUI
 */
public class StateHistoryTextEntry extends StateHistoryControl {


    /**
     * Constructor for the State History
     *
     * @param entry the Text Field whose state we are storing
     */
    public StateHistoryTextEntry(TextField entry) {
        entry.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume); // removes right click menu which shows undo/redo
        this.control = entry;
        states.add(entry.getText());
        setUpUndoableWrapper(this.control);
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
            return true;
        }
        return false;
    }


    /**
     * Resets the TextField to the state immediately prior to an undo
     */
    public boolean redo() {
        if (index + 1 < states.size()) {
            index += 1;
            ((TextField) control).setText((String) states.get(index));
            return true;
        }
        return false;
    }
}
