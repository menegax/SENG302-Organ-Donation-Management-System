package controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import model.Clinician;
import model.Donor;

import java.util.HashMap;

public class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static Scene main;

    public static Donor donor;

    public static Clinician clinician;


    static void setLoggedInDonor(Donor newDonor) {
        donor = newDonor;
    }


    public static Donor getLoggedInDonor() {
        return donor;
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
<<<<<<< HEAD
     * Add screen to the hash map of screens
=======
     * Add screen to the hashmap of screens
>>>>>>> origin/development
     *
     * @param name - name of screen to add
     * @param pane - Pane object from FXML
     */
    static void addScreen(String name, Pane pane) {
        screenMap.put(name, pane);
    }


    /**
<<<<<<< HEAD
     * Remove screen from hash map
=======
     * Remove screen from hashmap
>>>>>>> origin/development
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
}
