package controller;

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
}
