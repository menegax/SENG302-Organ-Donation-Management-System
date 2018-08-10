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
import javafx.stage.Window;
import model.User;
import utility.undoRedo.UndoableWrapper;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static java.util.logging.Level.*;
import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;
import static utility.UserActionHistory.userActions;

public class ScreenControlDesktop extends ScreenControl {
    private static ScreenControlDesktop screenControl;
    private UserControl userControl = UserControl.getUserControl();

    private boolean macOs = System.getProperty("os.name")
            .startsWith("Mac");

    /**
     * Getter to enable this to be a singleton
     * @return the single ScreenControl object
     */
    public static ScreenControl getScreenControl() {
        if (screenControl == null) {
            screenControl = new ScreenControlDesktop();
        }
        return screenControl;
    }

    /**
     * shows the fxml (screen)
     * @param fxml the fxml to display
     * @param undoable if the displayed screen is to be undoable or not
     * @param parentController controller to notify when stage shown closes
     * @return the controller of this fxml
     */
    public Object show(String fxml, Boolean undoable, IWindowObserver parentController, User targetUser) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load());
            Object controller = fxmlLoader.getController();
            if (undoable) {
                UndoableWrapper undoableWrapper = new UndoableWrapper(stage);
                if (controller instanceof GUIHome) {
                    undoableWrapper.setGuiHome((GUIHome) controller);
                }
                undoableWrappers.add(new UndoableWrapper(stage));
                userControl.setTargetUser(targetUser, undoableWrapper);
            }
            stage.setScene(scene);
            stage.show();
            if (parentController != null) {
                stage.setOnHiding(event -> parentController.windowClosed());
            }
            systemLogger.log(Level.INFO, "Showing new desktop stage");
            return controller;
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Unable to load window", "Attempted to load fxml: " + fxml);
            new Alert(Alert.AlertType.ERROR, "Unable to open window", ButtonType.OK).show();
        }
        return null;
    }

    @Override
    boolean closeWindow(Pane pane) {
        Stage window = (Stage) pane.getScene().getWindow();
        window.close();
        return true;
    }

    /**
     * Sets up a new login scene
     * To be used when the user has logged out and a new login scene needs to be instantiated
     */
    void setUpNewLogin() {
        for (UndoableWrapper undoableStage : undoableWrappers) {
            undoableStage.getStage().close();
        }
        try {
            FXMLLoader loginScreen = new FXMLLoader(getClass().getResource("/scene/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loginScreen.load()));
            stage.show();
        }
        catch (IOException e) {
            systemLogger.log(SEVERE, "Failed to recreate login scene in desktop application");
        }
    }

    /**
     * Removes asterisks from all stages when local changes are saved to disk
     */
    void removeUnsavedAsterisks() {
        for (UndoableWrapper undoableStage : undoableWrappers) {
            if (undoableStage.getGuiHome() != null) {
                undoableStage.getGuiHome().removeAsterisk();
            }
        }
    }

    /**
     * Adds asterisks to all stages when local changes have been made
     */
    void addUnsavedAsterisks() {
        for (UndoableWrapper undoableStage : undoableWrappers) {
            if (undoableStage.getGuiHome() != null) {
                undoableStage.getGuiHome().addAsterisk();
            }
        }
    }

}
