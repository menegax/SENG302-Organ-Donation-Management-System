package utility.undoRedo;

import controller.GUIHome;
import controller.ScreenControl;
import controller.UndoRedoControl;
import controller.UndoableController;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

/**
 * Controls undo and redo functionality within its own stage
 */
public class UndoableStage extends Stage {

    private List<StatesHistoryScreen> statesHistoryScreens = new ArrayList<>();

    private Map<StatesHistoryScreen, Tab> tabMap = new HashMap<>();

    private int index = -1;

    private boolean changingStates = false;

    private final UUID uuid = UUID.randomUUID();

    private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();

    private GUIHome guiHome;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    /**
     * Constructor for the undoable stage
     */
    public UndoableStage() {
        //set min sizes
        super.setMinWidth(800);
        super.setMinHeight(640);
    }

    /**
     * Undoes the previous action and navigates to the appropriate screen where applicable
     */
    public void undo() {
        changingStates = true;
        boolean success = false;
        while (statesHistoryScreens.size() != 0 && !success) {
            success = statesHistoryScreens.get(index).undo();
            if (index == 0) {
                success = true;
            }
            if (!success) {
                index -= 1;
            }
            navigateToScreen("undo");
        }
        changingStates = false;
    }

    /**
     * Redoes the last undone action and navigates to the appropriate screen where applicable
     */
    public void redo() {
        changingStates = true;
        boolean success = false;
        while (statesHistoryScreens.size() != 0 && !success) {
            success = statesHistoryScreens.get(index).redo();
            if (index == statesHistoryScreens.size() - 1) {
                success = true;
            }
            if (!success) {
                index += 1;
            }
            navigateToScreen("redo");
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
            for (StatesHistoryScreen statesHistoryScreen : statesHistoryScreens) {
                statesHistoryScreen.notifyStoreComplete();
            }
        }
    }

    /**
     * Navigates the user to the screen given by the current stateHistoryScreen
     * @param method whether this was called from an undo or redo
     */
    private void navigateToScreen(String method) {
        Tab newTab = tabMap.get(statesHistoryScreens.get(index));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/" + (statesHistoryScreens.get(index)).getUndoableScreen().toString() + ".fxml"));
        try {
            newTab.setContent(fxmlLoader.load());
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading screen", "Attempted to navigate screens during " + method);
        }
        UndoableController controller = fxmlLoader.getController();
        undoRedoControl.setActions(statesHistoryScreens.get(index).getActions(), controller.getStatesHistory());
        undoRedoControl.setStates(statesHistoryScreens.get(index), controller.getControls());
        undoRedoControl.setStatesHistoryScreen(controller, statesHistoryScreens.get(index));
        statesHistoryScreens.set(index, controller.getStatesHistory());
        screenControl.getTabPane(this).getSelectionModel().select(newTab);
        tabMap.put(controller.getStatesHistory(), newTab);
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
            tabMap.put(statesHistoryScreen, screenControl.getTabPane(this).getSelectionModel().getSelectedItem());
        }
    }

//    /**
//     * Adds listeners for undo/redo for undoable pop-ups (procedures, diagnoses)
//     */
//    public void setPopUp() {
//        this.getScene().setOnKeyPressed(event ->  {
//            if (screenControl.getUndo().match(event)) {
//                undo();
//            } else if (screenControl.getRedo().match(event)) {
//                redo();
//            }
//        });
//    }

    /**
     * Gets the UUID of the stage
     * to be used as the stages hash key in screen control
     * @return the uuid of the stage
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Whether the undoable stage has called an undo or redo (changing states)
     * @return if the stage is in the process of an undo or redo
     */
    public boolean isChangingStates() {
        return changingStates;
    }

    public GUIHome getGuiHome() {
        return guiHome;
    }

    public void setGuiHome(GUIHome guiHome) {
        this.guiHome = guiHome;
    }

    public void setChangingStates(Boolean changingStates) {
        this.changingStates = changingStates;
    }

    public boolean getChangingStates() {
        return this.changingStates;
    }

    public List<StatesHistoryScreen> getStatesHistoryScreens() {
        return statesHistoryScreens;
    }
}
