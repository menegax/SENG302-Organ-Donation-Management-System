package utility.undoRedo.stateHistoryWidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StateHistoryControl {

    int index = 0;

    List<Object> states = new ArrayList<>();

    /**
     * Display the state of the object one action ahead
     */
    abstract public void redo();


    /**
     * Store current state of the object
     */
    abstract public void store();


    /**
     * Display the last state of the object to the user
     */
    abstract public void undo();


    /**
     * Gets the states of the object
     * Currently only used in testing
     *
     * @return the States of the object
     */
    public List<Object> getStates() {
        return Collections.unmodifiableList(states);
    };


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
    };
}
