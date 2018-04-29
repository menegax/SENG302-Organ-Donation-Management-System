package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Clinician;
import model.Donor;

import java.io.IOException;
import java.util.HashMap;

class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static HashMap<Stage, Donor> popMap = new HashMap<>();

    private static Scene main;


    public static Donor donor;

    public static Clinician clinician;

    static void setLoggedInDonor(Donor newDonor) {
        donor = newDonor;
    }

    static Donor getLoggedInDonor() {
        return donor;
    }

    public static Scene getMain() {
        return main;
    }

    static void setLoggedInClinician(Clinician newClinician) {
        clinician = newClinician;
    }

    static Clinician getLoggedInClincian() {
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
     * @param stage - stage to display
     * @param donor - the donor the popup is focused on
     */
    static void addPopUp(Stage stage, Donor donor) {
        popMap.put(stage, donor);
    }

    static void loadPopUpPane(Scene scene, FXMLLoader fxmlLoader, Donor donor) throws IOException {
        scene.setRoot(fxmlLoader.load());
        IPopupable controller = fxmlLoader.getController();
        controller.setViewedDonor(donor);
    }

    /**
     * Displays a given popup
     *
     * @param stage - name of the pop up to display
     */
    static void displayPopUp(Stage stage) {
        stage.show();
    }

}
