package utility.undoRedo;

import controller.IUndoRedo;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import utility.undoRedo.stateHistoryWidgets.*;

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
     *
     * @param params optional widget parameters to initialise
     */
    public StatesHistoryScreen(Pane pane, ArrayList<Control> params) {
        pane.setOnKeyPressed(event -> {
            if (KeyCodeCombination.keyCombination("Ctrl+Z")
                    .match(event)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y")
                    .match(event)) {
                redo();
            }
        });
        for (Control param : params) {
            if ((param instanceof TextField)) {
                createStateHistoriesTextField(param);
            }
            if ((param) instanceof RadioButton) {
                createStateHistoriesRadioButton(param);
            }
            if ((param) instanceof CheckBox) {
                createStateHistoriesCheckBox(param);
            }
            if ((param) instanceof ChoiceBox) {
                createStateHistoriesChoiceBox(param);
            }
            if (param instanceof ComboBox) {
                createStateHistoriesComboBox(param);
            }
            if (param instanceof DatePicker) {
                createStateHistoriesDatePicker(param);
            }
        }
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param entry - object which can be cast to an arraylist<TextField>
     */
    private void createStateHistoriesTextField(Object entry) {
        stateHistories.add(new StateHistoryTextEntry((TextField) entry));
        ((TextField) entry).textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!newValue.equals(oldValue) && !undone && !redone) { //don't want to store state when textfield has been undone or redone
                        store();
                    }
                });
        ((TextField) entry).setOnKeyPressed(event -> {
            if (KeyCodeCombination.keyCombination("Ctrl+Z")
                    .match(event)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y")
                    .match(event)) {
                redo();
            }
        });
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param comboBox - object which can be cast to an arraylist<ComboBox>
     */
    private void createStateHistoriesComboBox(Object comboBox) {
        stateHistories.add(new StateHistoryComboBox((ComboBox<String>) comboBox));
        ((ComboBox<String>) comboBox).getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if ((oldValue == null && newValue != null)
                            || !newValue.equals(oldValue) && !undone && !redone) { //don't want to store state when ComboBox has been undone
                        store();
                    }
                });
        //        The following code is commented out as it is assumed, like choiceBox, Ctrl+Z still triggers on the AnchorPane when the comboBox is selected
        //        ((ComboBox<String>) comboBox).setOnKeyPressed(event -> {
        //            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
        //                undo();
        //            } else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
        //                redo();
        //            }
        //        });
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param radioButton - object which can be cast to an arraylist<RadioButton>
     */

    private void createStateHistoriesRadioButton(Object radioButton) {
        StateHistoryRadioButton stateHistoryRadioButton = new StateHistoryRadioButton((RadioButton) radioButton);
        stateHistories.add(stateHistoryRadioButton);
        ((RadioButton) radioButton).setOnMouseClicked(event -> {
            if (!(boolean) stateHistoryRadioButton.getStates().get(stateHistoryRadioButton.getIndex())) {
                store();
            }
        });
        ((RadioButton) radioButton).setOnKeyPressed(event -> {
            ((RadioButton) radioButton).getParent().requestFocus();
        });
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param checkBox - object which can be cast to an arraylist<CheckBox>
     */
    private void createStateHistoriesCheckBox(Object checkBox) {
        stateHistories.add(new StateHistoryCheckBox((CheckBox) checkBox));
        ((CheckBox) checkBox).selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!newValue.equals(oldValue) && !undone && !redone) { //don't want to store state when CheckBox has been undone
                        store();
                    }
                });
        //        The following code is commented out as Ctrl + Z still still triggers on parent pane when checkbox is selected
        //        ((CheckBox) checkBox).setOnKeyPressed(event -> {
        //            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
        //                undo();
        //            } else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
        //                redo();
        //            }
        //        });
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param choiceBox - object which can be cast to an arraylist<ChoiceBox>
     */

    private void createStateHistoriesChoiceBox(Object choiceBox) {
        stateHistories.add(new StateHistoryChoiceBox((ChoiceBox<String>) choiceBox));
        ((ChoiceBox<String>) choiceBox).getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (((oldValue == null || newValue == null) || !newValue.equals(oldValue)) && !undone
                            && !redone) { //don't want to store state when ChoiceBox has been undone
                        store();
                    }
                });
        //        The following code is commented out as Ctrl+Z still triggers on the AnchorPane when the choiceBox is selected
        //        ((ChoiceBox<String>) choiceBox).setOnKeyPressed(event -> {
        //            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
        //                undo();
        //            } else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
        //                redo();
        //            }
        //        });
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param datePicker - object which can be cast to an arraylist<DatePicker>
     */
    private void createStateHistoriesDatePicker(Object datePicker) {
        stateHistories.add(new StateHistoryDatePicker((DatePicker) datePicker));
        ((DatePicker) datePicker).valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != oldValue && !undone && !redone) { //don't want to store state when DatePicker has been undone
                        store();
                    }
                });
        // Allows for parent screen to listen for Ctrl z, Ctrl y, undo and redo as DatePicker does not recognise letters
        ((DatePicker) datePicker).setOnKeyPressed(event -> {
            ((DatePicker) datePicker).getParent().requestFocus();
        });
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
     *
     * @return the ArrayList of state history objects
     */
    public ArrayList<IUndoRedo> getStateHistories() {
        return stateHistories;
    }
}
