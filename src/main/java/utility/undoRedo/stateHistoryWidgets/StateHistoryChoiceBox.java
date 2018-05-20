package utility.undoRedo.stateHistoryWidgets;

import utility.undoRedo.IUndoRedo;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;

public class StateHistoryChoiceBox implements IUndoRedo {

    private ChoiceBox<String> choiceBox;

    private ArrayList<String> states = new ArrayList<>();

    private boolean undone = false;

    private int index = 0;


    /**
     * Constructor which adds the current (base) state of the choice box to the history
     *
     * @param choiceBox - ChoiceBox the object will be keeping history of
     */

    public StateHistoryChoiceBox(ChoiceBox<String> choiceBox) {
        this.choiceBox = choiceBox;
        states.add(choiceBox.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Called whenever the user makes an action
     * Stores the current state of the ChoiceBox and increments the index accordingly
     * Also removes any states after the current action in the ArrayList
     */
    public void store() {
        index += 1;
        states = new ArrayList<>(states.subList(0, index));
        states.add(choiceBox.getSelectionModel()
                .getSelectedItem());
    }


    /**
     * Sets the ChoiceBox to the state before the current state
     */
    public void undo() {
        if (index != 0) {
            index -= 1;
            choiceBox.getSelectionModel()
                    .select(states.get(index));
            undone = true;
        }
    }


    public void redo() {
        if (undone && index + 1 < states.size()) {
            index += 1;
            choiceBox.getSelectionModel()
                    .select(states.get(index));
        }
    }


    /**
     * Gets the states of the Combo Box
     * Currently only used in testing
     *
     * @return the states of the combo box
     */
    public ArrayList<Object> getStates() {
        return new ArrayList<>(states);
    }


    /**
     * Gets the index of the current state of the ChoiceBox
     * currently only used in testing
     *
     * @return the index of the current state
     */
    public int getIndex() {
        return index;
    }
}
