package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utility.UserActionHistory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene);
        GUIScreenControl.setRootScene(rootScene);
        GUIScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/donorProfile.fxml")));
        GUIScreenControl.addScreen("donorHistory", FXMLLoader.load(getClass().getResource("/donorHistory.fxml")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        UserActionHistory.setup(); // start logs
        launch(args);
    }
}
