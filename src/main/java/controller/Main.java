package controller;

import com.google.gson.stream.JsonReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.Database;
import utility.UserActionHistory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        ScreenControl.setRootScene(rootScene); // set this scene in screen controller

        Database.importFromDisk("./donor.json");

        // Add FXML screens to ScreenControl
        ScreenControl.addScreen("login", FXMLLoader.load(getClass().getResource("/scene/login.fxml")));
        ScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/scene/donorRegister.fxml")));
        ScreenControl.addScreen("home", FXMLLoader.load(getClass().getResource("/scene/home.fxml")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        launch(args);
    }
}
