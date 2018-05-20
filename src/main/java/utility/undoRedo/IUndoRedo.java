package utility.undoRedo;

import java.util.ArrayList;

public interface IUndoRedo {

    /**
     * Display the state of the object one action ahead
     */
    void redo();


    /**
     * Store current state of the object
     */
    void store();


    /**
     * Display the last state of the object to the user
     */
    void undo();


    /**
     * Gets the states of the object
     * Currently only used in testing
     *
     * @return the States of the object
     */
    ArrayList<Object> getStates();


    /**
     * Gets the index of the current state
     *
     * @return the index of the current state
     */
    int getIndex();
}
