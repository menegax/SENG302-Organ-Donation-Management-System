package controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

class GUIScreenControl {
    private static HashMap<String, Pane> screenMap = new HashMap<>();
    private static Scene main;

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
