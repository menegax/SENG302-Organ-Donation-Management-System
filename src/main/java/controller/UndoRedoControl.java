package controller;

import javafx.scene.control.*;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Includes methods to assist with undo and redo functionality across the whole application
 */
public class UndoRedoControl {

//    public String undoShortcut = "Ctrl+Z";
//    public String redoShortcut = "Ctrl+Y";

    private static UndoRedoControl undoRedoControl;

    private UndoRedoControl() {

    }

    /**
     * Sets the states of a list of control objects to the current states of the equivalent objects of a StatesHistoryScreen
     * The List of controls MUST be in the same order as they were instantiated in the StatesHistoryScreen
     * @param statesHistoryScreen stores the previous states of the screen that the controls were on
     * @param controlList the current control variables of the current screen to set the states of
     */
    public void setStates(StatesHistoryScreen statesHistoryScreen, List<Control> controlList) {
        for (int i = 0; i < controlList.size(); i++) {
            setControl(controlList.get(i), statesHistoryScreen.getStateOfControl(i));
        }
    }

    /**
     * Sets the controller's statesHistoryScreen to the states in the provided statesHistoryScreen
     * @param controller the controller of the current screen to set
     * @param statesHistoryScreen the statesHistoryScreen to get the states from
     */
    public void setStatesHistoryScreen (UndoableController controller, StatesHistoryScreen statesHistoryScreen) {
        controller.getStatesHistory().setIndex(statesHistoryScreen.getIndex());
        for (int i = 0; i < statesHistoryScreen.getStateHistories().size(); i++) {
            controller.setStateHistory(i, statesHistoryScreen.getStateHistories().get(i));
        }
    }

    /**
     * Sets the state of a control to the state provided
     * Only called from setStates method
     * Only works with states obtained from undo/redo StateHistories, else type exceptions may be thrown
     * @param control the control object whose state you want to set
     * @param state the state to set the control object to
     */
    private void setControl(Control control, Object state) {
        if ((control instanceof TextField)) {
            ((TextField) control).setText((String) state);
        }
        if ((control) instanceof RadioButton) {
            ((RadioButton) control).setSelected((Boolean) state);
        }
        if ((control) instanceof CheckBox) {
            ((CheckBox) control).setSelected((Boolean) state);
        }
        if ((control) instanceof ChoiceBox) {
            // Unchecked type call will always be safe
            ((ChoiceBox) control).getSelectionModel().select(state);
        }
        if (control instanceof ComboBox) {
            // Unchecked type call will always be safe
            ((ComboBox) control).getSelectionModel().select(state);
        }
        if (control instanceof DatePicker) {
            if (state != null) {
                ((DatePicker) control).setValue(LocalDate.parse((String) state));
            } else {
                ((DatePicker) control).setValue(null);
            }
        }
        if (control instanceof TableView) {
            // Unchecked type call will always be safe
            //((TableView) control).getItems().setAll((ArrayList) state);
        }
        if (control instanceof ListView) {
            // Unchecked type call will always be safe
            //((ListView) control).getItems().setAll((ArrayList) state);
        }
    }

    /**
     * Sets the actions of a stateHistoryScreen to the provided actions
     * @param actions the actions to set to
     * @param statesHistoryScreen the statesHistoryScreen whose actions need to be set
     */
    public void setActions(Map<Integer, Action> actions, StatesHistoryScreen statesHistoryScreen) {
        statesHistoryScreen.setActions(actions);
    }

    /**
     * Gets the undoRedoControl instance. If the instance is null, undoRedoControl is initialized
     * @return UndoRedoControl instance
     */
    static public UndoRedoControl getUndoRedoControl() {
        if (undoRedoControl == null) {
            undoRedoControl = new UndoRedoControl();
        }
        return undoRedoControl;
    }
}
