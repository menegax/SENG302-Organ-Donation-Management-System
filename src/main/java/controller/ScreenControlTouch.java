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
import org.tuiofx.examples.demo.FXMLController;
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

    private Pane rootPane;

    private Pane touchPane = new Pane();

    private static ScreenControlTouch screenControlTouch;

    private boolean isLoginShowing;

    private ScreenControlTouch() {
        isLoginShowing = true;
    }

    public static ScreenControlTouch getScreenControl() {
        if (screenControlTouch == null) {
            screenControlTouch = new ScreenControlTouch();
        }
        return screenControlTouch;
    }

    /**
     * Displays a new pane with the loaded fxml
     * @param fxml the fxml to display
     * @param undoable if the pane to be displayed is undoable or not
     * @param parentController controller to notify when pane shown closes
     * @return the controller created for this fxml
     */
    public Object show(String fxml, Boolean undoable, IWindowObserver parentController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Pane pane = fxmlLoader.load();
            Object controller = fxmlLoader.getController();
            pane.setStyle("-fx-background-color: #2c2f34; -fx-border-color: #f5f5f5;");
            List<Node> panes;
            if(isLoginShowing) {
                panes = new ArrayList<>();
                rootPane = pane;
                setLoginShowing(false);
            } else {
                panes = new ArrayList<>(touchPane.getChildren());
            }
            panes.add(pane);
            if (undoable) {
                UndoableWrapper undoablePane = new UndoableWrapper(pane);
                undoableWrappers.add(undoablePane);
                if (fxmlLoader.getController() instanceof GUIHome) {
                    undoablePane.setGuiHome(fxmlLoader.getController());
                }
            }
            Parent root = new FXMLLoader(getClass().getResource("/scene/touchScene.fxml")).load();
            touchPane = new Pane(root);
            touchPane.getChildren().addAll(panes);
            touchStage.setScene(new Scene(touchPane));
            pane.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && parentController != null) {
                    parentController.windowClosed();
                }
            });
            systemLogger.log(INFO, "Showing new touch stage scene");
            return controller;
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to load window", "Attempted to load a new window");
            new Alert(Alert.AlertType.ERROR, "Unable to open window", ButtonType.OK).show();
        }
        return null;
    }

    /**
     * Creates a login pane
     */
    void setUpNewLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/scene/login.fxml"));
            touchPane = new Pane();
            touchPane.getChildren().addAll(new Pane(root));
            touchStage.setScene(new Scene(touchPane));
            setLoginShowing(true);
        } catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in touch application");

        }
    }

    void setTouchStage(Stage touchStage) {
        this.touchStage = touchStage;
    }

    /**
     * Adds asterisks to all panes with a coloured bar
     */
    void addUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            if (undoablePane.getGuiHome() != null) {
                undoablePane.getGuiHome().addAsterisk();
            }
        }
    }

    /**
     * Removes all asterisks from panes with a coloured bar
     */
    void removeUnsavedAsterisks() {
        for (UndoableWrapper undoablePane : undoableWrappers) {
            if(undoablePane.getGuiHome() != null) {
                undoablePane.getGuiHome().removeAsterisk();
            }
        }
    }

    void setLoginShowing(boolean showing) {
        this.isLoginShowing = showing;
    }

    /**
     * Closes the provided pane
     * @param pane the pane to close
     * @return if a pane was closed successfully
     */
    boolean closeWindow(Pane pane) {
        List<Node> nodes = new ArrayList<>(touchPane.getChildren());
        for(Node n : nodes) {
            if(n.equals(pane) && !(pane.equals(rootPane))) {
                touchPane.getChildren().remove(n);
                n.setVisible(false);
                return true;
            }
        }
        return false;
    }

}
