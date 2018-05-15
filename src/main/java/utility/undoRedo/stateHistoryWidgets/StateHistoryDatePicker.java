package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryDatePicker extends StateHistoryControl {
    /**
     * The DatePicker this object holds the states for
     */
    private DatePicker date;

    /**
     * True if an undo has been executed, false otherwise - could be reset at exit from each interface
     */
    private boolean undone = false;

    /**
     * Constructor for the State History
     * @param datePicker the datePicker whose state we are storing
     */
    public StateHistoryDatePicker(DatePicker datePicker) {
        this.date = datePicker;
        if (date.getValue() == null) {
            states.add(null);
        } else {
            states.add(date.getValue().toString());
        }
    }

    /**
     * Called whenever the user makes an action
     * Stores the current state of the DatePicker and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        if (date.getValue() == null) {
            states.add(null);
        } else {
            states.add(date.getValue().toString());
        }
    }

    /**
     * Sets the DatePicker to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            if (states.get(index) == null) {
                date.setValue(null);
            } else {
                date.setValue(LocalDate.parse((String) states.get(index)));
            }
            undone = true;
        }
    }

    /**
     * Resets the DatePicker to the state immediately prior to an undo
     */
    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            if (states.get(index) == null) {
                date.setValue(null);
            } else {
                date.setValue(LocalDate.parse((String) states.get(index)));
            }
        }
    }

}
