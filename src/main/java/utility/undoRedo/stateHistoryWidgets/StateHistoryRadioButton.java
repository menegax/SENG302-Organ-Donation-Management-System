package utility.undoRedo.stateHistoryWidgets;

import controller.IUndoRedo;
import javafx.scene.control.RadioButton;

import java.util.ArrayList;

public class StateHistoryRadioButton implements IUndoRedo {

    private RadioButton radioButton;

    private ArrayList<Boolean> states = new ArrayList<>();

    private boolean undone = false;

    private int index = 0;


    public StateHistoryRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
        states.add(radioButton.isSelected());

    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the RadioButtons and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(radioButton.isSelected());
    }


    /**
     * Sets the RadioButton to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            radioButton.setSelected(states.get(index));
            undone = true;
        }
    }


    /**
     * Resets the RadioButton to the state immediately prior to an undo
     */
    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            radioButton.setSelected(states.get(index));
        }
    }


    /**
     * Gets the states of the RadioButton
     * @return the states of the RadioButton
     */
    public ArrayList<Object> getStates() {
        return new ArrayList<>(states);
    }


    /**
     * Gets the index of the current state of the RadioButton
     *
     * @return the index of the current state
     */
    public int getIndex() {
        return index;
    }
}
