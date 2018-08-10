package controller;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

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

import java.io.IOException;
import java.util.UUID;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class Main extends Application {

    private static final UUID uuid = UUID.randomUUID();


    @Override
    public void start(Stage primaryStage) throws IOException {

        // setup GUI
        ScreenControl screenControl = ScreenControl.getScreenControl();
        primaryStage.setTitle("Login");
        screenControl.addStage(uuid, primaryStage);
        primaryStage.setResizable(false);
        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        screenControl.show(uuid, loginScreen);

        Searcher.getSearcher().createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        systemLogger.log(INFO, "Finished the start method for the app. Beginning app");
        System.setProperty("connection_type", GlobalEnums.DbType.PRODUCTION.getValue()); //LEAVE HERE!! production db
        new UserDataService().prepareApplication();
       // openKeyboard();
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

    /**
     * Opens the Windows system on-screen keyboard.
     * This is only called on a Windows setup because touch controls are only available for a Windows system.
     */
    private void openKeyboard() {
        if(System.getProperty("os.name")
                .startsWith("Windows")) {
            try {
                Runtime.getRuntime().exec("cmd /c osk");
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("System keyboard could not be opened");
                alert.show();
            }
        }
    }
}
