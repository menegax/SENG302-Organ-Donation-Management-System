package controller;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//todo put javadoc bruh
public class ScreenControl {

    //Todo remove all deprecated stuff
    private static ScreenControl screenControl;

    @Deprecated
    private static HashMap<String, Pane> screenMap = new HashMap<>();

    @Deprecated
    private static HashMap<String, Stage> popMap = new HashMap<>();

    @Deprecated
    public static Map<String, Parent> scenes = new HashMap<>();

    private static Map<User, Set<Stage>> userStages = new HashMap<>();

    @Deprecated
    private static Scene main;

    private static Map<UUID, Stage> applicationStages;

    private boolean macOs = System.getProperty("os.name")
            .startsWith("Mac");

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


    /**
     * Getter to enable this to be a singleton
     * @return the single ScreenControl object
     */
    public static ScreenControl getScreenControl() {
        if (screenControl == null) {
            screenControl = new ScreenControl();
        }
        return screenControl;
    }

    /**
     * Adds a stage to the hashmap
     * @param key the uuid of the stage to add
     * @param stage the stage object to add to the hashmap
     */
    public void addStage(UUID key, Stage stage){
        applicationStages.put(key, stage);
        if (new UserControl().isUserLoggedIn()) { // if scene belongs to a user
            systemLogger.log(FINE, "User is logged in and a stage is being added");
            addUserStage(new UserControl().getLoggedInUser(), stage);
        }
    }

    /**
     * Shows the root(screen) on the given stage
     * @param stageName the UUID of the stage
     * @param root the screen to display on the stage
     */
    public void show(UUID stageName, Parent root) {
        Stage stage = applicationStages.get(stageName);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * shows the fxml (screen) on the stage that a node is on
     * @param node a node on the appropriate stage
     * @param fxml the fxml to display
     * @throws IOException any issue in loading the fxml file
     */
    public void show(Node node, String fxml) throws IOException{
        Stage stage = applicationStages.get(((UndoableStage) node.getScene().getWindow()).getUUID());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
        stage.show();
    }

    /**
     * Closes the stage that the provided node is on
     * @param - node which is on the stage to close
     */

    void closeStage(UUID stageName) {
        applicationStages.get(stageName)
                .close();
        applicationStages.remove(stageName);
        userStages.remove(new UserControl().getLoggedInUser(), stageName);
    }


    void closeStage(Node node) {
        ((Stage) node.getScene()
                .getWindow()).close();
        if (node.getScene()
                .getWindow() instanceof UndoableStage) {
            applicationStages.remove(((UndoableStage) node.getScene()
                    .getWindow()).getUUID());
        }
        userStages.remove(new UserControl().getLoggedInUser(), node);

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


    private static void addUserStage(User user, Stage newStage) {

        systemLogger.log(FINE, "Added user and stage");

        if (userStages.containsKey(user)) {
            userStages.get(user)
                    .add(newStage);
        }
        else {
            Set<Stage> stages = new HashSet<>();
            stages.add(newStage);
            userStages.put(user, stages);
        }
    }


    static void closeAllUserStages(User user) {
        systemLogger.log(FINER, "Attempting to close all user stages...");
        Set<Stage> stages = userStages.get(user);
        try {
            for (Stage stage : stages) {
                stage.close();
            }
            systemLogger.log(FINER, "Closed all user stages.");
        } catch (NullPointerException e) {
            systemLogger.log(SEVERE, "Failed to close all user stages.", e);
        }
        userStages.remove(user);

    }


    /**
     * Sets up a new login scene
     *
     * To be used when the user has logged out and a new login scene needs to be instantiated
     */
    public void setUpNewLogin() {
        // UNTIL WE SUPPORT MULTI USER LOGIN
        try {
            ScreenControl screenControl = ScreenControl.getScreenControl();
            Pane loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            screenControl.addStage(Main.getUuid(), new Stage());
            screenControl.show(Main.getUuid(), loginScreen);
        }
        catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene");
        }
    }

}

