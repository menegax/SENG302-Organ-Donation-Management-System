package controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import model.Clinician;
import model.Patient;

import java.util.HashMap;

class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static Scene main;

    public static Patient patient;

    public static Clinician clinician;


    static void setLoggedInDonor(Patient newPatient) {
        patient = newPatient;
    }

    static Patient getLoggedInDonor() {
        return patient;
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
}
