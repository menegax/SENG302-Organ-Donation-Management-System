package utility.undoRedo;

import utility.GlobalEnums.UndoableScreen;
import utility.undoRedo.stateHistoryWidgets.StateHistoryControl;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCodeCombination;
import utility.undoRedo.stateHistoryWidgets.*;

import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

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

    private UndoableStage undoableStage;

    private Map<Integer, Action> actions = new HashMap<>();

    private int index = 0;

    /**
     * Constructor for the StatesHistoryScreen, creates state objects of passed in control items to keep track of
     * Creates the list of stateHistories in its initialisation
     * @param controls list of controls on the screen (can also contain arraylists of controls)
     * @param undoableScreen the enum of the screen this StatesHistoryScreen represents
     */
    public StatesHistoryScreen(List<Control> controls, UndoableScreen undoableScreen) {
        this.undoableScreen = undoableScreen;
        addToUndoableStage(controls.get(0));
        for (Object control : controls) {
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
            if (control instanceof TableView) {
                createStateHistoriesTableView(control);
            }
            if (control instanceof ListView) {
                createStateHistoriesListView(control);
            }
        }
    }

    /**
     * Adds this object to the appropriate undoableStage
     * @param control a control on the current screen
     */
    private void addToUndoableStage(Control control) {
        if (control.getScene() == null) {
            control.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    if (newScene.getWindow() == null) {
                        newScene.windowProperty().addListener((observable2, oldStage, newStage) -> {
                            if (newStage != null) {
                                ((UndoableStage) newStage).addStatesHistoryScreen(this);
                                undoableStage = (UndoableStage) newStage;
                            }
                        });
                    } else {
                        ((UndoableStage) newScene.getWindow()).addStatesHistoryScreen(this);
                        undoableStage = (UndoableStage) newScene.getWindow();
                    }
                }
            });
        } else if (control.getScene().getWindow() == null){
            control.getScene().windowProperty().addListener((observable2, oldStage, newStage) -> {
                if (newStage != null) {
                    ((UndoableStage) newStage).addStatesHistoryScreen(this);
                    undoableStage = (UndoableStage) newStage;
                }
            });
        } else {
            ((UndoableStage) control.getScene().getWindow()).addStatesHistoryScreen(this);
            undoableStage = (UndoableStage) control.getScene().getWindow();
        }
    }


    /**
     * Creates state objects for every control item in the passed in array
     *
     * @param entry - object which can be cast to an arraylist<TextField>
     */
    private void createStateHistoriesTextField(Object entry) {
        stateHistories.add(new StateHistoryTextEntry((TextField) entry));
        ((TextField) entry).textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                store();
            }
        });
        ((TextField) entry).setOnKeyPressed(event -> {
            if (KeyCodeCombination.keyCombination("Ctrl+Z").match(event)) {
                ((TextField) entry).getParent().requestFocus();
                ((UndoableStage) ((TextField) entry).getScene().getWindow()).undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(event)) {
                ((TextField) entry).getParent().requestFocus();
                ((UndoableStage) ((TextField) entry).getScene().getWindow()).redo();
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
                            || !newValue.equals(oldValue)) {
                        store();
                    }
                });
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
                    if (newValue != oldValue && ((RadioButton) radioButton).focusedProperty().getValue()) {
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
                    if (!newValue.equals(oldValue)) {
                        store();
                    }
                });
    }


    /**
     * Creates state objects for every tableView item in the passed in array
     *
     * @param tableView - object which can be cast to an arraylist<TableView>
     */
    private void createStateHistoriesTableView(Object tableView) {
        stateHistories.add(new StateHistoryTableView( (TableView<Object>) tableView ));
        ((TableView<Object>) tableView).itemsProperty().addListener((observable, oldValue, newValue) -> {
                    if (((oldValue == null || newValue == null) || !newValue.equals(oldValue))) {
                        store();
                    }
                });
    }

    /**
     * Creates state objects for every listView item in the passed in array
     *
     * @param listView - object which can be cast to an arraylist<ListView>
     */
    private void createStateHistoriesListView(Object listView) {
        stateHistories.add(new StateHistoryListView( (ListView<Object>) listView ));
        ((ListView<Object>) listView).itemsProperty().addListener((observable, oldValue, newValue) -> {
            if (((oldValue == null || newValue == null) || !newValue.equals(oldValue)) && ((ListView<Object>) listView).focusedProperty().getValue()) {
                store();
            }
        });
    }


    /**
     * Creates state objects for every choiceBox item in the passed in array
     *
     * @param choiceBox - object which can be cast to an arraylist<choiceBox>
     */
    private void createStateHistoriesChoiceBox(Object choiceBox) {
        stateHistories.add( new StateHistoryChoiceBox( (ChoiceBox <String>) choiceBox ) );
        ((ChoiceBox <String>) choiceBox).getSelectionModel()
                .selectedItemProperty()
                .addListener( ( observable, oldValue, newValue ) -> {
                    if (((oldValue == null || newValue == null) || !newValue.equals( oldValue ))) {
                        store();
                    }
                } );
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
                    if (newValue != oldValue) {
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
        if (!undone && !redone && !undoableStage.isChangingStates()) {
            index += 1;
            for (StateHistoryControl stateHistory : stateHistories) {
                stateHistory.store();
            }
            undoableStage.store();
        }
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
        index -= 1;
        if (actions.get(index) != null) {
            actions.get(index).unexecute();
            userActions.log(Level.INFO, "Local change undone", "User undoed through local change");
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
        if (actions.get(index) != null) {
            actions.get(index).execute();
            userActions.log(Level.INFO, "Local change redone", "User redoed through local change");
        }
        index += 1;
        redone = false;
        return true;
    }

    /**
     * Called when this StatesHistoryScreen's undoable stage completes a store action
     * Truncates state lists to current index
     */
    public void notifyStoreComplete() {
        for (StateHistoryControl stateHistoryControl : stateHistories) {
            stateHistoryControl.notifyStoreComplete();
        }
    }


    /**
     * Gets the stateHistories ArrayList
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

    /**
     * Adds an action to the actions map for this StatesHistoryScreen
     * Action is assciated with the current index in the undo/redo stack
     * @param action the new action to add
     */
    public void addAction(Action action) {
        actions.put(index, action);
    }

    /**
     * Gets the undoable stage this statesHistoryScreen is on
     * @return the undoableStage
     */
    public UndoableStage getUndoableStage() {
        return undoableStage;
    }
}
