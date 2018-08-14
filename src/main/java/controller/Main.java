package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import service.UserDataService;
import utility.GlobalEnums;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;
import utility.undoRedo.UndoableStage;
import utility.parsing.ParseCSV;

import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();

    @Override
    public void start(Stage primaryStage) throws IOException {

        // setup GUI
        System.setProperty("connection_type", GlobalEnums.DbType.PRODUCTION.getValue()); //LEAVE HERE!! production db
        ScreenControl screenControl = ScreenControl.getScreenControl();
        primaryStage.setTitle("Login");
        screenControl.addStage(uuid, primaryStage);
        primaryStage.setResizable(false);
        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        screenControl.show(uuid, loginScreen);

        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app...");
        new UserDataService().prepareApplication();
        primaryStage.show();
    }

    public static void main(String[] args) {
        UserActionHistory.setup(); // start user action logs
        SystemLogger.setup();
        launch(args);
    }

    /**
     * Gets the uuid hash key used for the primary stage
     * @return the uuid hash key used in the primary stage
     */
    static UUID getUuid() {
        return uuid;
    }

}
