package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import com.sun.javafx.css.StyleManager;
import controller.ScreenControl;
import de.codecentric.centerdevice.MenuToolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.UserDataService;
import utility.GlobalEnums;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;
import utility.parsing.ParseCSV;

import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();


    @Override
    public void start(Stage primaryStage) throws IOException {

        // setup GUI
        System.setProperty("connection_type", GlobalEnums.DbType.STORY44.getValue()); //LEAVE HERE!! production db
        ScreenControl screenControl = ScreenControl.getScreenControl();
//        primaryStage.setTitle("Login");
        screenControl.show("/scene/login.fxml", false, null, null);

        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app...");
        new UserDataService().prepareApplication();
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
