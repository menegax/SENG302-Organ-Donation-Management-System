package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Clinician;
import model.Donor;

import java.io.IOException;
import java.util.HashMap;

public class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static HashMap<String, Stage> popMap = new HashMap<>();

    private static Scene main;


    public static Donor donor;

    public static Clinician clinician;


    static void setLoggedInDonor(Donor newDonor) {
        donor = newDonor;
    }


    public static Donor getLoggedInDonor() {
        return donor;
    }

    public static Scene getMain() {
        return main;
    }

    static void setLoggedInClinician(Clinician newClinician) {
        clinician = newClinician;
    }

    static Clinician getLoggedInClinician() {
        return clinician;
    }

    /**
     * set initial screen to display
     *
     * @param mainScene - main screen to display
     */
    static void setRootScene(Scene mainScene) {
        main = mainScene;
    }


    /**
     * Add screen to the hashmap of screens
     *
     * @param name - name of screen to add
     * @param pane - Pane object from FXML
     */
    static void addScreen(String name, Pane pane) {
        screenMap.put(name, pane);
    }


    /**
     * Remove screen from hashmap
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
     * Switches panes within a popup window, while passing along the current viewed donor
     *
     * @param scene      The scene to load the new pane into
     * @param fxmlLoader The fxmlLoader for the new pane
     * @param donor      The donor to pass to the next pane
     * @throws IOException If the pane fails to load
     */
    static void loadPopUpPane(Scene scene, FXMLLoader fxmlLoader, Donor donor) throws IOException {
        scene.setRoot(fxmlLoader.load());
        IPopupable controller = fxmlLoader.getController();
        controller.setViewedDonor(donor);
    }

    /**
     * Displays a given popup
     *
     * @param name - name of the pop up to display
     */
    static void displayPopUp(String name) {
        popMap.get(name).show();
    }

    /**
     * Hides a given popup
     *
     * @param name - name of the popup to hide
     */
    public static void hidePopUp(String name) {
        popMap.get(name).close();
    }

}
