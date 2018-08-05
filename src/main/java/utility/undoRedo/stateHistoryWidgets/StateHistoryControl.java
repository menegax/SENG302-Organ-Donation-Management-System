package utility.undoRedo.stateHistoryWidgets;

import controller.ScreenControl;
import javafx.scene.Node;
import javafx.scene.control.Control;
import utility.undoRedo.UndoableWrapper;

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

    private UndoableWrapper undoableWrapper;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

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
    }

    /**
     * Gets the current state of the control
     * @return the state of the control
     */
    public Object getCurrentState() {
        return states.get(index);
    }

    /**
     * Called when this StateHistory's undoable stage completes a store action
     * Truncates state lists to current index
     */
    public void notifyStoreComplete() {
        states = new ArrayList<>(states.subList(0, index + 1));
    }

    /**
     * Finds the appropriate undoable wrapper for this control
     * @param node a node on the screen of the undoable wrapper
     */
    protected void setUpUndoableWrapper(Node node) {
        if (node.getParent() == null) {
            node.parentProperty().addListener((observable, oldValue, newValue) -> {
                if (screenControl.getUndoableWrapper(newValue) == null) {
                    setUpUndoableWrapper(newValue);
                } else {
                    undoableWrapper = screenControl.getUndoableWrapper(newValue);
                }
            });
        } else {
            if (screenControl.getUndoableWrapper(node.getParent()) == null) {
                setUpUndoableWrapper(node.getParent());
            } else {
                undoableWrapper = screenControl.getUndoableWrapper(node.getParent());
            }
        }
    }

    /**
     * Gets the undoableWrapper that this stateHistoryControl is on
     * @return the undoableWrapper of this stateHistoryControl
     */
    private UndoableWrapper getUndoableWrapper() {
        return undoableWrapper;
    }

    /**
     * Sets the states in this StateHistoryControl to a copy of the provided StateHistoryControl
     * @param stateHistoryControl the StateHistoryControl to copy the state of
     */
    public void setStates(StateHistoryControl stateHistoryControl) {
        this.states = stateHistoryControl.getStates();
        this.index = stateHistoryControl.getIndex();
        this.undoableWrapper = stateHistoryControl.getUndoableWrapper();
    }
}
