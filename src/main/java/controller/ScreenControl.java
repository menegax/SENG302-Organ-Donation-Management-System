package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.undoRedo.UndoableStage;
import utility.GlobalEnums;

import java.io.IOException;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

public class ScreenControl {

    private static ScreenControl screenControl;

    @Deprecated
    private static HashMap<String, Pane> screenMap = new HashMap<>();
    @Deprecated
    private static HashMap<String, Stage> popMap = new HashMap<>();
    @Deprecated
    public static Map<String, Parent> scenes = new HashMap<>();

    @Deprecated
    private static Scene main;

    private static Map<UUID, Stage> applicationStages;


    private ScreenControl() {
        applicationStages = new HashMap<>();
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
     * Closes a stage based on the UUID of the stage
     * @param stageName the UUID of the stage to close
     */
    public void closeStage(UUID stageName) {
        applicationStages.get(stageName).close();
        applicationStages.remove(stageName);
    }

    /**
     * Closes the stage that the provided node is on
     * @param node a node which is on the stage to close
     */
    public void closeStage(Node node) {
        ((Stage) node.getScene().getWindow()).close();
        if (node.getScene().getWindow() instanceof UndoableStage) {
            applicationStages.remove(((UndoableStage) node.getScene().getWindow()).getUUID());
        }
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

}
