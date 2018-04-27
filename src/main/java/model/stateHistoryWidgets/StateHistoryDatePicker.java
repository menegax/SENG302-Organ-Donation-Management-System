package model.stateHistoryWidgets;

import controller.IUndoRedo;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;

public class StateHistoryDatePicker implements IUndoRedo {
    /**
     * The DatePicker this object holds the states for
     */
    private DatePicker date;

    /**
     * The states of the DatePicker
     */
    private ArrayList<String> states = new ArrayList<>();

    /**
     * The index of the current state in the ArrayList
     */
    private int index = 0;

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
                date.setValue(LocalDate.parse(states.get(index)));
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
                date.setValue(LocalDate.parse(states.get(index)));
            }
        }
    }

    /**
     * Gets the states of the DatePicker
     * Currently only used in testing
     * @return the states of the DatePicker
     */
    public ArrayList<Object> getStates() {
        return new ArrayList<>(states);
    }

    /**
     * Gets the index of the current state of the DatePicker
     * currently only used in testing
     * @return the index of the DatePicker
     */
    public int getIndex() {
        return index;
    }
}
