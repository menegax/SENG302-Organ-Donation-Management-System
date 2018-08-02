package controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tuiofx.Configuration;
import service.Database;
import utility.Searcher;
import utility.SystemLogger;
import utility.UserActionHistory;
import org.tuiofx.TuioFX;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.UUID;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class TUIOFXMain extends Application {

    private static final UUID uuid = UUID.randomUUID();


    @Override
    public void start(Stage primaryStage) throws Exception {
       // set up GUI
       UndoableStage stage = new UndoableStage();
       ScreenControl screenControl = ScreenControl.getScreenControl();
       screenControl.setTouchStage(stage);
//       screenControl.addStage(uuid, stage);
       Parent loginScreen = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
       screenControl.show(uuid, loginScreen);

        Database.importFromDiskPatients("./patient.json");
        Database.importFromDiskClinicians("./clinician.json");
        Database.importFromDiskWaitlist("./waitlist.json");
        Database.importFromDiskAdministrators("./administrator.json");

        Searcher.getSearcher()
                .createFullIndex(); // index patients for search, needs to be after importing or adding any patients
        openKeyboard();
        TuioFX tuioFX = new TuioFX(stage, Configuration.pqLabs());
        tuioFX.enableMTWidgets(true);
        tuioFX.start();
        stage.setResizable(true);
        stage.setFullScreen(true);
        stage.show();

//        primaryStage = new UndoableStage();
//        Pane mainPane = new Pane();
//
//        Pane loginPane = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
//
//        Scene mainScene = new Scene(mainPane);
//
//        mainPane.getChildren().addAll(loginPane);
////        mainPane.getChildren().addAll(userRegisterPane);
//
//        Button button = new Button();
//        button.setText("Load register");
//        mainPane.getChildren().addAll(button);
//        button.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    Pane userRegisterPane = FXMLLoader.load(getClass().getResource("/scene/userRegister.fxml"));
//                    mainPane.getChildren().addAll(userRegisterPane);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        primaryStage.setScene(mainScene);
//
//        TuioFX tuioFX = new TuioFX(primaryStage, Configuration.debug());
//        tuioFX.start();
//
//        primaryStage.show();



        //        screenControl.setTouchStage(stage); //todo...

        systemLogger.log(INFO, "Finished the start method for the touch screen app. Beginning app");
//        stage.show();
    }


    public static void main(String[] args) {
        UserActionHistory.setup();
        SystemLogger.setup();
        TuioFX.enableJavaFXTouchProperties();
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
