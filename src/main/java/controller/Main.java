package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import javafx.application.Application;
import javafx.stage.Stage;
import service.UserDataService;
import utility.GlobalEnums;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;

import java.util.UUID;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();

    @Override
    public void start(Stage primaryStage) {

        // setup GUI
        System.setProperty("connection_type", GlobalEnums.DbType.PRODUCTION.getValue()); //LEAVE HERE!! production db
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
