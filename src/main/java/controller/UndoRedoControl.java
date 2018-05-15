package controller;

import javafx.scene.control.*;
import utility.undoRedo.StatesHistoryScreen;

import java.time.LocalDate;
import java.util.List;

/**
 * Includes methods to assist with undo and redo functionality across the whole application
 */
public class UndoRedoControl {

    /**
     * Sets the states of a list of control objects to the current states of the equivalent objects of a StatesHistoryScreen
     * The List of controls MUST be in the same order as they were instantiated in the StatesHistoryScreen
     * @param statesHistoryScreen stores the previous states of the screen that the controls were on
     * @param controlList the current control variables of the current screen to set the states of
     */
    static public void setStates(StatesHistoryScreen statesHistoryScreen, List<Control> controlList) {
        for (int i = 0; i < controlList.size(); i++) {
            setControl(controlList.get(i), statesHistoryScreen.getStateOfControl(i));
        }
    }

    /**
     * Sets the state of a control to the state provided
     * Only called from setStates method
     * Only works with states obtained from undo/redo StateHistories, else type exceptions may be thrown
     * @param control the control object whose state you want to set
     * @param state the state to set the control object to
     */
    static private void setControl(Control control, Object state) {
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
            ((DatePicker) control).setValue(LocalDate.parse((String) state));
        }
    }
}
