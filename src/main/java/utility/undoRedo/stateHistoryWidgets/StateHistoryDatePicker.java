package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateHistoryDatePicker extends StateHistoryControl {

    /**
     * Constructor for the State History
     * @param datePicker the datePicker whose state we are storing
     */
    public StateHistoryDatePicker(DatePicker datePicker) {
        this.control = datePicker;
        if (((DatePicker) control).getValue() == null) {
            states.add(null);
        } else {
            states.add(((DatePicker) control).getValue().toString());
        }
        setUpUndoableStage();
    }

    /**
     * Called whenever the user makes an action
     * Stores the current state of the DatePicker and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        if (((DatePicker) control).getValue() == null) {
            states.add(null);
        } else {
            states.add(((DatePicker) control).getValue().toString());
        }
    }

    /**
     * Sets the DatePicker to the state before the current state
     */
    public boolean undo() {
        if (index != 0) {
            index -= 1;
            if (states.get(index) == null) {
                ((DatePicker) control).setValue(null);
            } else {
                ((DatePicker) control).setValue(LocalDate.parse((String) states.get(index)));
            }
            return true;
        }
        return false;
    }

    /**
     * Resets the DatePicker to the state immediately prior to an undo
     */
    public boolean redo() {
        if (index + 1 < states.size()) {
            index += 1;
            if (states.get(index) == null) {
                ((DatePicker) control).setValue(null);
            } else {
                ((DatePicker) control).setValue(LocalDate.parse((String) states.get(index)));
            }
            return true;
        }
        return false;
    }

}
