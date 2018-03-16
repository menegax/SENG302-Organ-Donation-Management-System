package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage parentWindow;

    @Override
    public void start(Stage primaryStage) throws Exception{
        parentWindow = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene);
        primaryStage.show();
        GUIScreenControl screenController = new GUIScreenControl(rootScene);
        screenController.addScreen("login", FXMLLoader.load(getClass().getResource( "/login.fxml" )));
        screenController.activate("login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
