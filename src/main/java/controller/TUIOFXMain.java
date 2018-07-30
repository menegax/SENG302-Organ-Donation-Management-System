package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tuiofx.Configuration;
import service.Database;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;
import org.tuiofx.TuioFX;

import java.io.IOException;
import java.util.UUID;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class TUIOFXMain extends Application {

    private static final UUID uuid = UUID.randomUUID();


    @Override
    public void start(Stage primaryStage) throws Exception {
        //        // set up GUI
        //        ScreenControl screenControl = ScreenControl.getScreenControl();
        //        screenControl.addStage(uuid, primaryStage);
        //        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        //        screenControl.show(uuid, loginScreen);

        Searcher.getSearcher()
                .createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        openKeyboard();
        TuioFX tuioFX = new TuioFX(primaryStage, Configuration.debug());
        tuioFX.start();
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(true);

        Pane mainPane = new Pane();

        Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
        Pane loginPane = (Pane) loginScreen;

        Parent userRegister = FXMLLoader.load(getClass().getResource("/scene/userRegister.fxml"));
        Pane userRegisterPane = (Pane) userRegister;

        Scene mainScene = new Scene(mainPane);

        primaryStage.setScene(mainScene);
        
        mainPane.getChildren().addAll(loginPane);
        mainPane.getChildren().addAll(userRegisterPane);


        //        screenControl.setTouchStage(primaryStage); //todo...

        systemLogger.log(INFO, "Finished the start method for the touch screen app. Beginning app");
        primaryStage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup();
        SystemLogger.setup();
        launch(args);
    }


    /**
     * Gets the uuid hash key used for the primary stage
     *
     * @return the uuid hash key used in the primary stage
     */
    public static UUID getUuid() {
        return uuid;
    }


    /**
     * Opens the Windows system on-screen keyboard.
     * This is only called on a Windows setup because touch controls are only available for a Windows system.
     */
    private void openKeyboard() {
        if (System.getProperty("os.name")
                .startsWith("Windows")) {
            try {
                Runtime.getRuntime()
                        .exec("cmd /c osk");
            }
            catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("System keyboard could not be opened");
                alert.show();
            }
        }
    }

}
