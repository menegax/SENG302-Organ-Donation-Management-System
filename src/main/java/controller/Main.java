package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene rootScene = new Scene(root, 600, 400);
        primaryStage.setScene(rootScene); //set scene on primary stage
        GUIScreenControl.setRootScene(rootScene); // set this scene in screen controller

        //TODO: if you have a FXML file, please add it to the screen controller! ty :)
        GUIScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/donorProfile.fxml")));
        GUIScreenControl.addScreen("donorRegister", FXMLLoader.load(getClass().getResource("/donorRegister.fxml")));
        GUIScreenControl.addScreen("donorHistory", FXMLLoader.load(getClass().getResource("/donorHistory.fxml")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
