package model;

import controller.IUndoRedo;
import javafx.scene.control.*;

import java.util.ArrayList;

/**
 * Represents the state history of a screen of FXML widgets
 */
public class StatesHistoryScreen {

    /**
     * ArrayList that stores all the stateHistorys for a specific screen
     */
    ArrayList<IUndoRedo> stateHistorys = new ArrayList<>();

    /**
     * Constructor for the StatesHistoryScreen
     * Creates the list of stateHistorys in its initialisation
     * @param entrys the TextField widgets on the screen
     * @param comboBoxes the ComboBox widgets on the screen
     * @param checkBoxes the CheckBox widgets on the screen
     */
    public StatesHistoryScreen(ArrayList<TextField> entrys, ArrayList<ComboBox> comboBoxes, ArrayList<CheckBox> checkBoxes) {
        for (TextField entry : entrys) {
            StateHistoryTextEntry entryState = new StateHistoryTextEntry(entry);
            stateHistorys.add(entryState);
        }
        for (ComboBox comboBox : comboBoxes) {
            StateHistoryComboBox comboBoxState = new StateHistoryComboBox(comboBox);
            stateHistorys.add(comboBoxState);
        }
        for (CheckBox checkBox : checkBoxes) {
            StateHistoryCheckBox checkBoxState = new StateHistoryCheckBox(checkBox);
            stateHistorys.add(checkBoxState);
        }
    }

    /**
     * Stores the current state of the screen
     */
    public void store() {
        for (IUndoRedo stateHistory : stateHistorys) {
            stateHistory.store();
        }
    }

    /**
     * Undoes the previous action performed on the screen by returning it to its previous state
     */
    public void undo() {
        for (IUndoRedo stateHistory : stateHistorys) {
            stateHistory.undo();
        }
    }
}
