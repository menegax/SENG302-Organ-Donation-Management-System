package utility.undoRedo;

import controller.ScreenControl;
import javafx.scene.Node;
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

    private UndoableWrapper undoableWrapper;

    private Map<Integer, List<Action>> actions = new HashMap<>();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private int index = 0;

    /**
     * Constructor for the StatesHistoryScreen, creates state objects of passed in control items to keep track of
     * Creates the list of stateHistories in its initialisation
     * @param controls list of controls on the screen (can also contain arraylists of controls)
     * @param undoableScreen the enum of the screen this StatesHistoryScreen represents
     */
    public StatesHistoryScreen(List<Control> controls, UndoableScreen undoableScreen) {
        this.undoableScreen = undoableScreen;
        findUndoableWrapper(controls.get(0));
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
     * Finds the appropriate undoable wrapper for this stateshistoryscreen
     * @param node a node on the current screen
     */
    private void findUndoableWrapper(Node node) {
        if (node.getParent() == null) {
            node.parentProperty().addListener((observable, oldValue, newValue) -> {
                if (screenControl.getUndoableWrapper(newValue) == null) {
                    if (newValue != null) {
                        findUndoableWrapper(newValue);
                    }
                } else {
                    addToUndoableWrapper(screenControl.getUndoableWrapper(newValue));
                }
            });
        } else {
            if (screenControl.getUndoableWrapper(node.getParent()) == null) {
                findUndoableWrapper(node.getParent());
            } else {
                addToUndoableWrapper(screenControl.getUndoableWrapper(node.getParent()));
            }
        }
    }

    /**
     * Adds this object to the appropriate undoableWrapper
     */
    private void addToUndoableWrapper(UndoableWrapper undoableWrapper) {
        this.undoableWrapper = undoableWrapper;
        undoableWrapper.addStatesHistoryScreen(this);
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
                    if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null) || !newValue.equals(oldValue)) {
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
                        //store();
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
                //store();
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
        if (!undone && !redone && !undoableWrapper.isChangingStates()) {
            index += 1;
            for (Integer key : actions.keySet()) {
                if (key >= index) {
                    actions.remove(key);
                }
            }
            for (StateHistoryControl stateHistory : stateHistories) {
                stateHistory.store();
            }
            undoableWrapper.store();
        }
    }


    /**
     * Undoes the previous action performed on the screen by returning it to its previous state
     * @return whether there was an action to undo or not
     */
    public boolean undo() {
        undone = true; // change to true as to not trigger listeners to store
        if (actions.get(index) != null) {
            for (int i = actions.get(index).size() - 1; i >= 0; i--) {
                if (actions.get(index).get(i).isExecuted()) {
                    actions.get(index).get(i).unexecute();
                    userActions.log(Level.INFO, "Local change undone", "User undoed through local change");
                    undone = false;
                    return true;
                }
            }
        }
        for (StateHistoryControl stateHistory : stateHistories) {
            Boolean success = stateHistory.undo();
            if (!success) {
                return false;
            }
        }
        index -= 1;
        undone = false;
        return true;
    }


    /**
     * Resets the latest screen state undo by returning to the initial state immediately prior to undo
     * @return whether the was action to redo or not
     */
    public boolean redo() {
        redone = true; // change to true as to not trigger listeners to store
        if (actions.get(index) != null) {
            for (Action action : actions.get(index)) {
                if (!action.isExecuted()) {
                    action.execute();
                    userActions.log(Level.INFO, "Local change redone", "User redoed through local change");
                    undone = false;
                    return true;
                }
            }
        }
        for (StateHistoryControl stateHistory : stateHistories) {
            Boolean success = stateHistory.redo();
            if (!success) {
                return false;
            }
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
        if (actions.get(index) == null) {
            actions.put(index, new ArrayList<Action>(){{add(action);}});
        } else {
            actions.get(index).add(action);
        }
    }

    /**
     * Returns the current actions map, used for passing the existing action map to a new instance of StatesHistoryScreen
     * @return the current actions map
     */
    public Map<Integer, List<Action>> getActions() {
        return actions;
    }

    public void setActions(Map<Integer, List<Action>> actions) {
        this.actions = actions;
    }

    /**
     * Gets the undoableWrapper stage this statesHistoryScreen is on
     * @return the undoableWrapper
     */
    public UndoableWrapper getUndoableWrapper() {
        return undoableWrapper;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
