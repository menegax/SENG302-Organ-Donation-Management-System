package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.Database;
import utility.GlobalEnums.Stages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenControl {

    //Todo remove all deprecated stuff

    private static Map<Stages, Stage> applicationStages;

    private static ScreenControl screenControl;

    @Deprecated
    private static HashMap<String, Pane> screenMap = new HashMap<>();

    @Deprecated
    private static HashMap<String, Stage> popMap = new HashMap<>();

    @Deprecated
    public static Map<String, Parent> scenes = new HashMap<>();

    @Deprecated
    private static Scene main;

    private boolean macOs = System.getProperty("os.name").startsWith("Mac");

    private KeyCodeCombination logOut;

    private KeyCodeCombination save;

    private KeyCodeCombination importt;

    private KeyCodeCombination undo;

    private KeyCodeCombination redo;

    private String appName = "Big Pharma";


    private ScreenControl() {
        applicationStages = new HashMap<>();
        setUpKeyCodeCombinations();
    }


    static ScreenControl getScreenControl() {
        if (screenControl == null) {
            screenControl = new ScreenControl();
        }
        return screenControl;
    }


    /**
     * @param key
     * @param stage
     */
    void addStage(Stages key, Stage stage) {
        applicationStages.put(key, stage);
    }


    /**
     * @param root
     */
    void show(Stages stageName, Parent root) {
        Stage stage = applicationStages.get(stageName);
        stage.setScene(new Scene(root));
      //  setUpMenuBar(stage); //TODO: breaks on my pc
        stage.show();
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
        //
        screenMap.put(name, pane);
    }



    //todo
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
        scene.setRoot(fxmlLoader.load());
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


    public KeyCodeCombination getLogOut() {
        return logOut;
    }


    public boolean isMacOs() {
        return macOs;
    }

    public String getAppName() {
        return appName;
    }
}
