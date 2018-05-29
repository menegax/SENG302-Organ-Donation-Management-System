package utility.undoRedo;

import controller.ScreenControl;
import utility.GlobalEnums.UndoableScreen;
import utility.undoRedo.stateHistoryWidgets.StateHistoryControl;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import utility.undoRedo.stateHistoryWidgets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the state history of a screen of FXML widgets
 */
public class StatesHistoryScreen {

    /**
     * ArrayList that stores all the stateHistories for a specific screen
     */
    private List<StateHistoryControl> stateHistories = new ArrayList<>();

    /**
     * Boolean to keep track of whether an action has been undone or not
     */
    private boolean undone = false;

    /**
     * Boolean to keep track of whether an action has been undone or not
     */
    private boolean redone = false;

    private UndoableScreen undoableScreen;

    /**
     * Constructor for the StatesHistoryScreen, creates state objects of passed in control items to keep track of
     * Creates the list of stateHistories in its initialisation
     * @param controls -
     * @param undoableScreen -
     */
    public StatesHistoryScreen(List<Control> controls, UndoableScreen undoableScreen) {
        // todo remove if unnecessary
//        pane.setOnKeyPressed(event -> {
//            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
//                undo();
//            }
//            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
//                redo();
//            }
//        });
        this.undoableScreen = undoableScreen;
        controls.get(0).sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((observable2, oldStage, newStage) -> {
                    if (newStage != null) {
                        ((UndoableStage) newStage).addStatesHistoryScreen(this);
                    }
                });
            }
        });
        for (Control control : controls) {
            if ((control instanceof TextField)) {
                createStateHistoriesTextField(control);
            }
            if ((control) instanceof RadioButton) {
                createStateHistoriesRadioButton(control);
            }
            if ((control) instanceof CheckBox) {
                createStateHistoriesCheckBox(control);
            }
            if ((control) instanceof ChoiceBox) {
                createStateHistoriesChoiceBox(control);
            }
            if (control instanceof ComboBox) {
                createStateHistoriesComboBox(control);
            }
            if (control instanceof DatePicker) {
                createStateHistoriesDatePicker(control);
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
            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
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
        ((RadioButton) radioButton).selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != oldValue && !undone && !redone && ((RadioButton) radioButton).focusedProperty().getValue()) { //don't want to store state when radioButton has been undone
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
        for (StateHistoryControl stateHistory : stateHistories) {
            stateHistory.store();
        }
        stateHistories.get(0).getUndoableStage().store();
    }


    /**
     * Undoes the previous action performed on the screen by returning it to its previous state
     * @return whether there was an action to undo or not
     */
    public boolean undo() {
        undone = true; // change to true as to not trigger listeners to store
        for (StateHistoryControl stateHistory : stateHistories) {
            boolean success = stateHistory.undo();
            if (!success) {
                return false;
            }
        }
        undone = false;
        return true;
    }


    /**
     * Resets the latest screen state undo by returning to the initial state immediately prior to undo
     * @return whether the was action to redo or not
     */
    public boolean redo() {
        redone = true; // change to true as to not trigger listeners to store
        for (StateHistoryControl stateHistory : stateHistories) {
            boolean success = stateHistory.redo();
            if (!success) {
                return false;
            }
        }
        redone = false;
        return true;
    }


    /**
     * Gets the stateHistories ArrayList
     * Currently only used for testing
     *
     * @return the ArrayList of state history objects
     */
    public List<StateHistoryControl> getStateHistories() {
        return Collections.unmodifiableList(stateHistories);
    }

    /**
     * Gets the current state of the control at the index provided
     * @param index the index of the control in the stateHistories list
     * @return the current state of that control
     */
    public Object getStateOfControl(int index){
        return stateHistories.get(index).getCurrentState();
    }

    /**
     * Gets the undoable screen enum of the screen this object represents
     * @return the undoable screen enum for this object
     */
    public UndoableScreen getUndoableScreen() {
        return undoableScreen;
    }
}
