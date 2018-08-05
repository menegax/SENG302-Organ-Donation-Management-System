package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utility.undoRedo.UndoableWrapper;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

class ScreenControlTouch extends ScreenControl {

    private Stage touchStage;

    private Pane touchPane = new Pane();

    private static ScreenControlTouch screenControlTouch;

    private ScreenControlTouch() {
    }

    public static ScreenControlTouch getScreenControl() {
        if (screenControlTouch == null) {
            screenControlTouch = new ScreenControlTouch();
        }
        return screenControlTouch;
    }

    public void show(String fxml) {
        try {
            List<Node> panes = new ArrayList<>(touchPane.getChildren());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Pane pane = new Pane();
            panes.add(0, pane);
            UndoableWrapper undoablePane = new UndoableWrapper(pane);
            undoableWrappers.add(undoablePane);
            if (fxmlLoader.getController() instanceof GUIHome) {
                undoablePane.setGuiHome(fxmlLoader.getController());
            }
            pane.getChildren().add(0, fxmlLoader.load());
            Parent root = new FXMLLoader(getClass().getResource("/scene/touchScene.fxml")).load();
            touchPane = new Pane(root);
            touchPane.getChildren().addAll(panes);
//            if (touchStage.getScene() == null) {
                touchStage.setScene(new Scene(touchPane));
//            }

            systemLogger.log(INFO, "Showing new touch stage scene");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to load window", "Attempted to load a new window");
            new Alert(Alert.AlertType.ERROR, "Unable to open window", ButtonType.OK).show();
        }
    }

    void setUpNewLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchPane = new Pane();
            touchPane.getChildren().addAll(new Pane(root));
            touchStage.setScene(new Scene(touchPane));
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

    void setTouchStage(Stage touchStage) {
        this.touchStage = touchStage;
    }

    void addUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            undoablePane.getGuiHome().addAsterisk();
        }
    }

    void removeUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            undoablePane.getGuiHome().removeAsterisk();
        }
    }

}
