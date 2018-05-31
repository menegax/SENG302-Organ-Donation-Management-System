package controller;

import javafx.scene.control.Control;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.stateHistoryWidgets.StateHistoryControl;

import java.util.Collections;
import java.util.List;

/**
 * Class for a controller who controls an undoable screen
 */
public abstract class UndoableController {

    List<Control> controls;
    StatesHistoryScreen statesHistoryScreen;

    /**
     * Gets the controls in the screen of this controller
     * @return the list of controls in this controller
     */
    public List<Control> getControls() {
        return Collections.unmodifiableList(controls);
    }

    /**
     * Sets the states of a stateHistoryControl to the states of the one provided
     * @param index the index of the stateHistoryControl to set the states of
     * @param stateHistoryControl the stateHistoryControl whose states are to be emulated
     */
    void setStateHistory (int index, StateHistoryControl stateHistoryControl) {
        statesHistoryScreen.getStateHistories().get(index).setStates(stateHistoryControl);
    }

    /**
     * Gets the statesHistoryScreen for this undoableController
     * @return the statesHistoryScreen of this controller
     */
    public StatesHistoryScreen getStatesHistory() {
        return statesHistoryScreen;
    }
}
