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
     * Constructor for the StatesHistoryScreen, creates state objects of passed in control items to keep track of
     * Creates the list of stateHistories in its initialisation
     * @Object... params - optional number of params
     */
    public StatesHistoryScreen(Object... params) {
       for (Object param : params) {
           if (param instanceof ArrayList<?>){ // check if generic arraylist
               Object firstItem = ((ArrayList) param).size() == 0 ? null : ((ArrayList) param).get(0); // avoid null pointers
               if ((firstItem instanceof TextField)){
                   createStateHistoriesTextField(param);
               }
               if ((firstItem) instanceof RadioButton){
                   createStateHistoriesRadioButton(param);
               }
               if ((firstItem) instanceof CheckBox){
                   createStateHistoriesCheckBox(param);
               }
               if ((firstItem) instanceof ChoiceBox){
                   createStateHistoriesChoiceBox(param);
               }
               if (firstItem instanceof ComboBox){
                   createStateHistoriesComboBox(param);
               }
           }
       }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param entries - object which can be cast to an arraylist<TextField>
     */
    private void createStateHistoriesTextField(Object entries){
        for (Object entry : ((ArrayList<?>) entries)) {
            stateHistories.add(new StateHistoryTextEntry((TextField)entry));
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param comboBoxes - object which can be cast to an arraylist<ComboBox>
     */
    private void createStateHistoriesComboBox(Object comboBoxes) {
        for (Object comboBox : ((ArrayList<?>) comboBoxes)) {
            stateHistories.add(new StateHistoryComboBox((ComboBox<String>)comboBox));
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param radioButtons - object which can be cast to an arraylist<RadioButton>
     */

    private void createStateHistoriesRadioButton(Object radioButtons){
        for (Object radioButton : ((ArrayList<?>) radioButtons)) {
            stateHistories.add(new StateHistoryRadioButton((RadioButton)radioButton));
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param checkBoxes - object which can be cast to an arraylist<CheckBox>
     */

    private void createStateHistoriesCheckBox(Object checkBoxes){
        for (Object checkBox : ((ArrayList<?>) checkBoxes)) {
            stateHistories.add(new StateHistoryCheckBox((CheckBox)checkBox));
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param choiceBoxes - object which can be cast to an arraylist<ChoiceBox>
     */

    private void createStateHistoriesChoiceBox(Object choiceBoxes){
        for (Object choiceBox : ((ArrayList<?>) choiceBoxes)) {
            stateHistories.add(new StateHistoryChoiceBox((ChoiceBox<String>)choiceBox));
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
     * Resets the latest screen state undo by returning to the initial state immediately prior to undo
     */
    public void redo() {
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.redo();
        }
    }

    /**
     * Gets the stateHistories ArrayList
     * Currently only used for testing
     * @return the ArrayList of state history objects
     */
    public ArrayList<IUndoRedo> getStateHistories() {
        return stateHistories;
    }
}
