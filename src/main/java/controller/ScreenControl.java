package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static HashMap<String, Stage> popMap = new HashMap<>();

    private static Map<String, Parent> scenes = new HashMap<>();

    private static Scene main;

    private static Stage primaryStage;


    public ScreenControl(Stage stage) {
        primaryStage = stage;
    }


    void addScene(String path) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(path));
        scenes.put(path, parent);
    }


    static void setUpHomeForPatient() {
        // sets icons on left sided tabs
        // adds scenes to stage

        // create tab pane
        TabPane profileTabPane = new TabPane();

        Tab profileViewTab = new Tab();
        profileViewTab.setContent(scenes.get("/scene/patientProfile.fxml"));
        Tab historyTab = new Tab();
        historyTab.setContent(scenes.get("/scene/patientProfile.fxml"));

        profileTabPane.getTabs().add(profileViewTab);

        Parent home = scenes.get("/scene/home.fxml");

        primaryStage.setScene(new Scene(scenes.get("/scene/home.fxml")));


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
    static void displayPopUp(String name) {
        popMap.get(name)
                .show();
    }


    /**
     * Closes a given popup
     *
     * @param name - name of the popup to hide
     */
    public static void closePopUp(String name) {
        popMap.get(name)
                .close();
    }

}
