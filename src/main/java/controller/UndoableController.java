package controller;

import javafx.scene.control.Control;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

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
}
