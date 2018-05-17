package utility.undoRedo;

import controller.ScreenControl;
import controller.UndoRedoControl;
import controller.UndoableController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controls undo and redo functionality within its own stage
 */
public class UndoableStage extends Stage {

    private List<StatesHistoryScreen> statesHistoryScreens = new ArrayList<>();

    private int index = 0;

    private boolean changingStates = false;

    /**
     * Constructor for the undoable stage
     * Sets up the action listeners for undo and redo
     */
    public UndoableStage() {
        super();
        this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCodeCombination.keyCombination(UndoRedoControl.undoShortcut).match(event)) {
                undo();
            } else if (KeyCodeCombination.keyCombination(UndoRedoControl.redoShortcut).match(event)) {
                redo();
            }
        });
    }

    /**
     * Undoes the previous action and navigates to the appropriate screen where applicable
     */
    private void undo() {
        changingStates = true;
        boolean success = false;
        while (statesHistoryScreens.size() != 0 && !success) {
            success = statesHistoryScreens.get(index).undo();
            if (index == 0) {
                success = true;
            }
            if (!success) {
                index -= 1;
                navigateToScreen("undo");
            }
        }
        changingStates = false;
    }

    /**
     * Redoes the last undone action and navigates to the appropriate screen where applicable
     */
    private void redo() {
        changingStates = true;
        boolean success = false;
        while (statesHistoryScreens.size() != 0 && !success) {
            success = statesHistoryScreens.get(index).redo();
            if (index == statesHistoryScreens.size() - 1) {
                success = true;
            }
            if (!success) {
                index += 1;
                navigateToScreen("redo");
            }
        }
        changingStates = false;
    }

    /**
     * Called whenever a statesHistoryScreen stores a new state
     * Resets the statesHistoryScreens list to contain statesHistoryScreens up to the current index
     */
    public void store() {
        if (!changingStates) {
            statesHistoryScreens = new ArrayList<>(statesHistoryScreens.subList(0, index + 1));
        }
    }

    /**
     * Navigates the user to the screen given by the current stateHistoryScreen
     * @param method whether this was called from an undo or redo
     */
    private void navigateToScreen(String method) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource((statesHistoryScreens.get(index)).getUndoableScreen().toString() + ".fxml"));
        try {
            ScreenControl.addScreen(statesHistoryScreens.get(index).getUndoableScreen().toString(), fxmlLoader.load());
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading screen", "Attempted to navigate screens during " + method);
            new Alert(Alert.AlertType.WARNING, "ERROR loading screen", ButtonType.OK).showAndWait();
        }
        ScreenControl.activate(statesHistoryScreens.get(index).getUndoableScreen().toString());
        UndoableController controller = fxmlLoader.getController();
        UndoRedoControl.setStates(statesHistoryScreens.get(index), controller.getControls());
    }

    /**
     * Adds a new statesHistoryScreen to the list
     * Called whenever a statesHistoryScreen is naturally (not through undo/redo) instantiated
     * @param statesHistoryScreen the new statesHistoryScreen to be added
     */
    public void addStatesHistoryScreen(StatesHistoryScreen statesHistoryScreen) {
        if (!changingStates) {
            index += 1;
            statesHistoryScreens = new ArrayList<>(statesHistoryScreens.subList(0, index));
            statesHistoryScreens.add(statesHistoryScreen);
        }
    }
}
