package controller;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;
import service.UserDataService;
import utility.SystemLogger;
import utility.UserActionHistory;

import static java.util.logging.Level.INFO;
import static utility.SystemLogger.systemLogger;

public class TUIOFXMain extends Application {

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
    public void start(Stage primaryStage) {
        // set up GUI
        Stage stage = setUpStage();
        ScreenControlTouch screenControl = (ScreenControlTouch) ScreenControl.getScreenControl();
        screenControl.setTouchStage(stage);
        screenControl.show("/scene/login.fxml", false,null, null);
        screenControl.setLoginShowing(true);
        Application.setUserAgentStylesheet("MODENA");
        StyleManager.getInstance().addUserAgentStylesheet("/css/guiStyle.css");


        new UserDataService().prepareApplication();

        TuioFX tuioFX = new TuioFX(stage, Configuration.pqLabs());
        tuioFX.enableMTWidgets(true);
        tuioFX.start();

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

}