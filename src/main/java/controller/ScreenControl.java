package controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import model.Donor;

import java.util.HashMap;

public class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static Scene main;

    public static Donor donor;

    public static void setLoggedInDonor(Donor newDonor) {
        donor = newDonor;
    }

    public static Donor getLoggedInDonor() {
        return donor;
    }

    /**
     * set initial screen to display
     * @param mainScene - main screen to display
     */
    static void setRootScene(Scene mainScene){
        main = mainScene;
    }


    /**
     * Add screen to the hashmap of screens
     * @param name - name of screen to add
     * @param pane - Pane object from FXML
     */
    static void addScreen(String name, Pane pane){
        screenMap.put(name, pane);
    }

    /**
     * Remove screen from hashmap
     * @param name - screen to remove from the hashmap of screens
     */
    static void removeScreen(String name){
        screenMap.remove(name);
    }

    /**
     * Displays the given scene to the UI
     * @param name - screen name to display
     */
    static void activate(String name){
        main.setRoot(screenMap.get(name));
    }
}
