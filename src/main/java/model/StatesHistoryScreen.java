package model;

import controller.IUndoRedo;
import javafx.scene.control.*;

import java.util.ArrayList;

/**
 * Represents the state history of a screen of FXML widgets
 */
public class StatesHistoryScreen {

    /*
     * ArrayList that stores all the stateHistories for a specific screen
     */
    private ArrayList<IUndoRedo> stateHistories = new ArrayList<>();

    /**
     * Constructor for the StatesHistoryScreen
     * Creates the list of stateHistories in its initialisation
     * @param entries the TextField widgets on the screen
     * @param comboBoxes the ComboBox widgets on the screen
     * @param checkBoxes the CheckBox widgets on the screen
     */
    public StatesHistoryScreen(ArrayList<TextField> entries, ArrayList<ComboBox<String>> comboBoxes, ArrayList<CheckBox> checkBoxes) {
        for (TextField entry : entries) {
            StateHistoryTextEntry entryState = new StateHistoryTextEntry(entry);
            stateHistories.add(entryState);
        }
        for (ComboBox<String> comboBox : comboBoxes) {
            StateHistoryComboBox comboBoxState = new StateHistoryComboBox(comboBox);
            stateHistories.add(comboBoxState);
        }
        for (CheckBox checkBox : checkBoxes) {
            StateHistoryCheckBox checkBoxState = new StateHistoryCheckBox(checkBox);
            stateHistories.add(checkBoxState);
        }
    }

    /**
     * Stores the current state of the screen
     */
    public void store() {
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.store();
        }
    }

    /**
     * Undoes the previous action performed on the screen by returning it to its previous state
     */
    public void undo() {
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.undo();
        }
    }

    /**
     * Resets the latest screen state undo by returning the the initial state immediately prior to undo
     */
    public void redo() {
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.redo();
        }
    }
}
