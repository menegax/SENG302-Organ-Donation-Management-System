package utility.undoRedo;

import controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class UndoableWrapper {

    private Pane pane;

    private Stage stage;

    private List<StatesHistoryScreen> statesHistoryScreens = new ArrayList<>();

    private Map<StatesHistoryScreen, Tab> tabMap = new HashMap<>();

    private int index = -1;

    private boolean changingStates = false;

    private final UUID uuid = UUID.randomUUID();

    private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();

    private GUIHome guiHome;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    public UndoableWrapper(Object object) {
        if (object instanceof Pane) {
            this.pane = (Pane) object;
        } else if (object instanceof Stage) {
            this.stage = (Stage) object;
            stage.setMinWidth(800);
            stage.setMinHeight(640);
        }
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
        controller.setTarget(statesHistoryScreens.get(index).getTarget());
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

    public Stage getStage() {
        return this.stage;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Object getWrapped() {
        if (stage != null) {
            return stage;
        } else if (pane != null) {
            return pane;
        }
        return null;
    }
}
