package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.*;

/**
 * Maintains a collection of screens for the application
 */
public class ScreenControl {

    protected static ScreenControl screenControl;

    @Deprecated
    private static HashMap<String, Pane> screenMap = new HashMap<>();

    @Deprecated
    private static HashMap<String, Stage> popMap = new HashMap<>();

    @Deprecated
    public static Map<String, Parent> scenes = new HashMap<>();

    private Map<User, Set<Stage>> userStages = new HashMap<>();

    @Deprecated
    private static Scene main;

    private Map<Stage, TabPane> stageTabs = new HashMap<>();

    private boolean macOs = System.getProperty("os.name")
            .startsWith("Mac");

    private KeyCodeCombination logOut;

    private KeyCodeCombination save;

    private KeyCodeCombination importt;

    private KeyCodeCombination undo;

    private KeyCodeCombination redo;

    private String appName = "Big Pharma";

    private Boolean isSaved = true;

    protected UndoableStage touchStage = null;

//    private Scene touchScene;

    protected Pane touchPane;

    private boolean isTouch;

    protected ScreenControl() {
        setUpKeyCodeCombinations();
    }



    public static void setUpScreenControl(String arg) {
        if(arg.equals("touch")) {
            screenControl = ScreenControlTouch.getScreenControl();
        } else {
            screenControl = ScreenControlDesktop.getScreenControl();
        }
    }

    /**
     * Getter to enable this to be a singleton
     * @return the single ScreenControl object
     */
    public static ScreenControl getScreenControl() {
//        if (screenControl == null) {
//            screenControl = new ScreenControl();
//        }
        return screenControl;
    }

    /**
     * Adds a stage to the hashmap
     * @param key the uuid of the stage to add
     * @param stage the stage object to add to the hashmap
     */
    public void addStage(UUID key, Stage stage) {

    }

    /**
     * Shows the root(screen) on the given stage
     * @param stageName the UUID of the stage
     * @param root the screen to display on the stage
     */
    public void show(UUID stageName, Parent root) {

    }

    /**
     * shows the fxml (screen) on the stage that a node is on
     * @param node a node on the appropriate stage
     * @param fxml the fxml to display
     * @throws IOException any issue in loading the fxml file
     */
    public void show(Node node, String fxml) throws IOException{

    }

    /**
     * Closes the stage that the provided node is on
     * @param stageName - node which is on the stage to close
     */
    void closeStage(UUID stageName) {
    }


    /**
     * Closes (terminates) a stage
     * @param node the stage to be closed
     */
    void closeStage(Node node) {

    }


    @Deprecated
    public static Scene getMain() {
        return main;
    }

    /**
     * set initial screen to display
     *
     * @param mainScene - main screen to display
     */
    @Deprecated
    static void setRootScene(Scene mainScene) {
        main = mainScene;
    }


    public void setTouchStage(UndoableStage touchStage) {

    }

    /**
     * Add screen to the hash map of screens
     *
     * @param name - name of screen to add
     * @param pane - Pane object from FXML
     */
    @Deprecated
    static void addTabToHome(String name, Pane pane) {
        //create tab
        // load pane into tab
        // add tab to existing tab pane
        screenMap.put(name, pane);
    }


    /**
     * Remove screen from hash map
     *
     * @param name - screen to remove from the hashmap of screens
     */
    @Deprecated
    static void removeScreen(String name) {
        screenMap.remove(name);
    }


    /**
     * Displays the given scene to the UI
     *
     * @param name - screen name to display
     */
    @Deprecated
    static void activate(String name) {
        main.setRoot(screenMap.get(name));
    }


    /**
     * Adds stage, name pair to a hashmap
     *
     * @param name  - name of the popup
     * @param stage - stage to display
     */
    @Deprecated
    static void addPopUp(String name, Stage stage) {
        popMap.put(name, stage);
    }


    /**
     * Switches panes within a popup window, while passing along the current viewed patient
     *
     * @param scene      The scene to load the new pane into
     * @param fxmlLoader The fxmlLoader for the new pane
     * @exception IOException If the pane fails to load
     */
    static void loadPopUpPane(Scene scene, FXMLLoader fxmlLoader) throws IOException {
    }


    /**
     * Displays a given popup
     *
     * @param name - name of the pop up to display
     */
    @Deprecated
    static void displayPopUp(String name) {
        popMap.get(name)
                .show();
    }


    /**
     * Closes a given popup
     *
     * @param name - name of the popup to hide
     */
    @Deprecated
    public static void closePopUp(String name) {
        popMap.get(name)
                .close();
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


    /**
     * Adds a stage to the collection of stages with a connection the user responsible for the stage
     * @param user the user responsible for the stage
     * @param newStage the new stage to be tied to the user
     */
    private void addUserStage(User user, Stage newStage) {

    }


    /**
     * Closes all the stages related to a given user
     * @param user the user whose stages will be closed
     */
    void closeAllUserStages(User user) {

    }


    /**
     * Sets up a new login scene
     *
     * To be used when the user has logged out and a new login scene needs to be instantiated
     */
    void setUpNewLogin() {
        // UNTIL WE SUPPORT MULTI USER LOGIN
    }

    /**
     * Adds a tabpane to a map of stages to tabpanes
     * @param stage the stage of the tadpane
     * @param tabPane the tabpane associated with that stage
     */
    public void addStageTab(Stage stage, TabPane tabPane) {
        stageTabs.put(stage, tabPane);
    }

    /**
     * Gets the tab pane of the given stage
     * @param stage the required stage
     * @return the TabPane of that stage
     */
    public TabPane getTabPane(Stage stage) {
        return stageTabs.get(stage);
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

    /**
     * Removes asterisks from all stages when local changes are saved to disk
     */
    private void removeUnsavedAsterisks() {

    }

    /**
     * Adds asterisks to all stages when local changes have been made
     */
    private void addUnsavedAsterisks() {

    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public Set<Stage> getUsersStages(User user) {
        return userStages.get(user);
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    public boolean getTouch() {
        return isTouch;
    }


}

