package model;

import controller.IUndoRedo;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import model.StateHistoryWidgets.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Represents the state history of a screen of FXML widgets
 */
public class StatesHistoryScreen {

    /**
     * ArrayList that stores all the stateHistories for a specific screen
     */
    private ArrayList<IUndoRedo> stateHistories = new ArrayList<>();

    /**
     * Boolean to keep track of whether an action has been undone or not
     */
    private boolean undone = false;

    /**
     * Boolean to keep track of whether an action has been undone or not
     */
    private boolean redone = false;

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
               if (firstItem instanceof DatePicker) {
                   createStateHistoriesDatePicker(param);
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
            ((TextField)entry).textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue) && !undone && !redone){ //don't want to store state when textfield has been undone or redone
                    store();
                }
            });
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param comboBoxes - object which can be cast to an arraylist<ComboBox>
     */
    private void createStateHistoriesComboBox(Object comboBoxes) {
        for (Object comboBox : ((ArrayList<?>) comboBoxes)) {
            stateHistories.add(new StateHistoryComboBox((ComboBox<String>)comboBox));
            ((ComboBox<String>) comboBox).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if ((oldValue == null && newValue!= null) || !newValue.equals(oldValue) && !undone && !redone){ //don't want to store state when textfield has been undone
                    store();
                }
            });
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param radioButtons - object which can be cast to an arraylist<RadioButton>
     */

    private void createStateHistoriesRadioButton(Object radioButtons){
        for (Object radioButton : ((ArrayList<?>) radioButtons)) {
            stateHistories.add(new StateHistoryRadioButton((RadioButton)radioButton));
            ((RadioButton) radioButton).selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue) && !undone && !redone){ //don't want to store state when textfield has been undone
                    store();
                }
            });
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param checkBoxes - object which can be cast to an arraylist<CheckBox>
     */

    private void createStateHistoriesCheckBox(Object checkBoxes){
        for (Object checkBox : ((ArrayList<?>) checkBoxes)) {
            stateHistories.add(new StateHistoryCheckBox((CheckBox)checkBox));
            ((CheckBox) checkBox).selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue) && !undone && !redone){ //don't want to store state when textfield has been undone
                    store();
                }
            });
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param choiceBoxes - object which can be cast to an arraylist<ChoiceBox>
     */

    private void createStateHistoriesChoiceBox(Object choiceBoxes){
        for (Object choiceBox : ((ArrayList<?>) choiceBoxes)) {
            stateHistories.add(new StateHistoryChoiceBox((ChoiceBox<String>)choiceBox));
            ((ChoiceBox<String>) choiceBox).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (((oldValue == null || newValue == null) || !newValue.equals(oldValue)) && !undone && !redone){ //don't want to store state when textfield has been undone
                    store();
                }
            });
        }
    }

    /**
     * Creates state objects for every control item in the passed in array
     * @param datePickers - object which can be cast to an arraylist<DatePicker>
     */
    private void createStateHistoriesDatePicker(Object datePickers) {
        for (Object datePicker : ((ArrayList<?>) datePickers)) {
            stateHistories.add(new StateHistoryDatePicker((DatePicker)datePicker));
            ((DatePicker) datePicker).valueProperty().addListener((observable, oldValue, newValue) -> {

            });
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
        undone = true; // change to true as to not trigger listeners to store
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.undo();
        }
        undone = false;
    }

    /**
     * Resets the latest screen state undo by returning to the initial state immediately prior to undo
     */
    public void redo() {
        redone = true; // change to true as to not trigger listeners to store
        for (IUndoRedo stateHistory : stateHistories) {
            stateHistory.redo();
        }
        redone = false;
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
