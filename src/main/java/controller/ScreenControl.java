package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

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

    public static ScreenControl getScreenControl() {
        if (screenControl == null) {
            screenControl = new ScreenControl();
        }
        return screenControl;
    }


    /**
     *
     * @param key
     * @param stage
     */
    public void addStage(UUID key, Stage stage){
        applicationStages.put(key, stage);
    }

    /**
     *
     * @param root
     */
    public void show(UUID stageName, Parent root) {
        Stage stage = applicationStages.get(stageName);
        stage.setScene(new Scene(root));
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

}
