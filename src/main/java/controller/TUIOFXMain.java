package controller;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.tuiofx.Configuration;
import org.tuiofx.TangibleEvent;
import org.tuiofx.TangibleListener;
import org.tuiofx.internal.base.GestureHandler;
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

    /**
     * Creates a new Stage and positions and sizes the stage to be the size of the screen boundaries
     * @return stage resized and positioned stage
     */
    private Stage setUpStage() {

        Stage stage = new Stage();

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());

        return stage;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // set up GUI
        Stage stage = setUpStage();
        ScreenControlTouch screenControl = (ScreenControlTouch) ScreenControl.getScreenControl();
        screenControl.setTouchStage(stage);
        screenControl.show("/scene/login.fxml", false,null);
        screenControl.setLoginShowing(true);

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
        tuioFX.addTangibleListener(new TangibleListener() {
            @Override
            public void tuioTangibleUpdated(TangibleEvent tangibleEvent) {
                
            }

            @Override
            public void tuioTangibleAdded(TangibleEvent tangibleEvent) {

            }

            @Override
            public void tuioTangibleRemoved(TangibleEvent tangibleEvent) {

            }
        });

        stage.show();

        stage.setOnCloseRequest(event -> System.exit(0));

        systemLogger.log(INFO, "Finished the start method for the touch screen app. Beginning app");
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
