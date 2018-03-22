package controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import model.Donor;

import java.util.HashMap;

class ScreenControl {

    private static HashMap<String, Pane> screenMap = new HashMap<>();

    private static Scene main;

    public static Donor donor;

    static void setLoggedInDonor(Donor newDonor) {
        donor = newDonor;
    }

    static Donor getLoggedInDonor() {
        return donor;
    }

    static void setRootScene(Scene mainScene){
        main = mainScene;
    }

    static void addScreen(String name, Pane pane){
        screenMap.put(name, pane);
    }

    static void removeScreen(String name){
        screenMap.remove(name);
    }

    static void activate(String name){
        main.setRoot(screenMap.get(name));
    }
}
