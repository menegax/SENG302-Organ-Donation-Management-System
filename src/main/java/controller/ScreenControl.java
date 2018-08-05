package controller;

import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.undoRedo.UndoableWrapper;

import java.util.*;

/**
 * Maintains a collection of screens for the application
 */
public abstract class ScreenControl {

    protected static ScreenControl screenControl;

    private boolean macOs = System.getProperty("os.name")
            .startsWith("Mac");

    private KeyCodeCombination logOut;

    private KeyCodeCombination save;

    private KeyCodeCombination importt;

    private KeyCodeCombination undo;

    private KeyCodeCombination redo;

    private String appName = "Big Pharma";

    private static boolean isTouch;

    private Boolean isSaved = true;

    private Map<Object, TabPane> tabs = new HashMap<>();

    protected List<UndoableWrapper> undoableWrappers = new ArrayList<>();

    abstract void setUpNewLogin();
    abstract void removeUnsavedAsterisks();
    abstract void addUnsavedAsterisks();
    abstract public void show(String fxml);

    protected ScreenControl() {
        setUpKeyCodeCombinations();
    }

    public static void setUpScreenControl(String arg) {
        isTouch = arg.equals("touch");
    }

    /**
     * Getter to enable this to be a singleton
     * @return the single ScreenControl object
     */
    public static ScreenControl getScreenControl() {
        if (screenControl == null && isTouch) {
            screenControl = ScreenControlTouch.getScreenControl();
        } else if (screenControl == null) {
            screenControl = ScreenControlDesktop.getScreenControl();
        }
        return screenControl;
    }

    public void logOut() {
        setUpNewLogin();
        setIsSaved(true);
    }

    /**
     * Connects the tabpane to the stage it is on
     * @param stage the stage the tabpane is on
     * @param tabPane the tabpane object
     */
    public void addTab(Stage stage, TabPane tabPane) {
        tabs.put(stage, tabPane);
    }

    /**
     * Connects the tabpane to the pane it is on
     * @param pane the pane the tabpane is on
     * @param tabPane the tabpane object
     */
    public void addTab(Pane pane, TabPane tabPane) {
        tabs.put(pane, tabPane);
    }

    /**
     * Gets the tab pane of the given undoableWrapper
     * @param undoableWrapper the required undoableWrapper
     * @return the TabPane of that undoableWrapper
     */
    public TabPane getTabPane(UndoableWrapper undoableWrapper) {
        return tabs.get(undoableWrapper.getWrapped());
    }

    /**
     * Gets the undoable wrapper object that wraps the given stage or pane
     * @param wrapped the stage or pane wrapped by the undoableWrapper
     * @return the undoable wrapper
     */
    public UndoableWrapper getUndoableWrapper(Object wrapped) {
        for (UndoableWrapper undoableWrapper : undoableWrappers) {
            if (undoableWrapper.getWrapped().equals(wrapped)) {
                return undoableWrapper;
            }
        }
        return null;
    }

    /**
     * Sets the isSaved boolean and adjusts screens accordingly
     * @param isSaved whether local changes have been saved or not
     */
    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
        if (isSaved) {
            removeUnsavedAsterisks();
        } else {
            addUnsavedAsterisks();
        }
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    /**
     * Sets keyboard shortcuts depending on the OS
     */
    private void setUpKeyCodeCombinations() {
        if (System.getProperty("os.name")
                .startsWith("Mac")) { // MacOS
            logOut = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN, KeyCombination.META_DOWN);
            save = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);
            importt = new KeyCodeCombination(KeyCode.I, KeyCombination.META_DOWN);
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN);
            redo = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN);
        }
        else { // Windows or Linux
            logOut = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN);
            save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            importt = new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN);
            undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        }
    }


    public KeyCodeCombination getSave() {
        return save;
    }


    public KeyCodeCombination getImportt() {
        return importt;
    }


    public KeyCodeCombination getUndo() {
        return undo;
    }


    public KeyCodeCombination getRedo() {
        return redo;
    }


    KeyCodeCombination getLogOut() {
        return logOut;
    }


    boolean isMacOs() {
        return macOs;
    }


    String getAppName() {
        return appName;
    }

    public boolean isTouch() {
        return isTouch;
    }

    public List<UndoableWrapper> getUndoableWrappers() {
        return Collections.unmodifiableList(undoableWrappers);
    }

}

