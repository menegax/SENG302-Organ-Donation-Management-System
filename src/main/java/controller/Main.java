package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

import javafx.application.Application;
import javafx.stage.Stage;
import service.UserDataService;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;

import java.util.UUID;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();

    @Override
    public void start(Stage primaryStage) {
        // set up GUI
        primaryStage.setTitle("Login");
        ScreenControl screenControl = ScreenControl.getScreenControl();
        screenControl.show("/scene/login.fxml", false, null, null, null);

        Searcher.getSearcher().createFullIndex(); // index globalPatients for search, needs to be after importing or adding any globalPatients
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
