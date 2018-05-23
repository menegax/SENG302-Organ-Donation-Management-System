package utility.undoRedo.stateHistoryWidgets;

import javafx.scene.control.Control;
import utility.GlobalEnums;
import utility.undoRedo.UndoableStage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the stateHistory of a control object
 */
public abstract class StateHistoryControl {

    int index = 0;

    List<Object> states = new ArrayList<>();

    Control control;

    private UndoableStage undoableStage;

    /**
     * Display the state of the object one action ahead
     * @return whether the stateHistory was successfully redone or not
     */
    abstract public boolean redo();


    /**
     * Store current state of the object
     */
    abstract public void store();


    /**
     * Display the last state of the object to the user
     * @return whether the stateHistory was successfully undone or not
     */
    abstract public boolean undo();

    /**
     * Gets the states of the object
     * Currently only used in testing
     *
     * @return the States of the object
     */
    public List<Object> getStates() {
        return Collections.unmodifiableList(states);
    }


    /**
     * Gets the index of the current state
     *
     * @return the index of the current state
     */
    public int getIndex() {
        return index;
    };

    /**
     * Gets the current state of the control
     * @return the state of the control
     */
    public Object getCurrentState() {
        return states.get(index);
    }

    /**
     * Sets the undoable stage that this control is on
     */
    void setUpUndoableStage() {
        if (control.getScene() == null) {
            control.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    if (newScene.getWindow() == null) {
                        newScene.windowProperty().addListener((observable2, oldStage, newStage) -> {
                            if (newStage != null) {
                                undoableStage = (UndoableStage) newStage;
                            }
                        });
                    } else {
                        undoableStage = (UndoableStage) newScene.getWindow();
                    }
                }
            });
        } else if (control.getScene().getWindow() == null){
            control.getScene().windowProperty().addListener((observable2, oldStage, newStage) -> {
                if (newStage != null) {
                    undoableStage = (UndoableStage) newStage;
                }
            });
        } else {
            undoableStage = (UndoableStage) control.getScene().getWindow();
        }
    }

    /**
     * Gets the undoable stage that this stateHistoryControl is on
     * @return the undoableStage of this stateHistoryControl
     */
    public UndoableStage getUndoableStage() {
        return undoableStage;
    }

    /**
     * Sets the states in this StateHistoryControl to a copy of the provided StateHistoryControl
     * @param stateHistoryControl the StateHistoryControl to copy the state of
     */
    public void setStates(StateHistoryControl stateHistoryControl) {
        this.states = stateHistoryControl.getStates();
        this.index = stateHistoryControl.getIndex();
        this.undoableStage = stateHistoryControl.getUndoableStage();
    }
}
