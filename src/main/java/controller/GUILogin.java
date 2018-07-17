package controller;

import static utility.UserActionHistory.userActions;

import com.sun.javafx.scene.control.skin.FXVK;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.TextField;

import javafx.stage.Window;
import main.Main;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.SystemLogger;
import utility.TouchscreenCapable;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.SystemLogger.systemLogger;

import static java.util.logging.Level.SEVERE;

public class GUILogin implements TouchscreenCapable {

    @FXML
    public AnchorPane loginPane;

    public Button loginButton;

    @FXML
    private TextField nhiLogin;

    @FXML
    private CheckBox clinicianToggle;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the login window by adding key binding for login on enter and an event filter on the login field
     */
    public void initialize() {
        // Enter key triggers log in
        nhiLogin.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        loginPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                logIn();
            }
        });
        loginPane.setOnZoom(this::zoomWindow);
        loginPane.setOnRotate(this::rotateWindow);
        loginPane.setOnScroll(this::scrollWindow);
    }

    /**
     * Open the register screen
     */
    @FXML
    public void goToRegister() {
        try {
            screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/patientRegister.fxml")));
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load patient register").show();
            userActions.log(SEVERE, "Failed to load patient register", "Attempted to load patient register");
        }
    }

    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void logIn() {
        UserControl login = new UserControl();
        ScreenControl screenControl = ScreenControl.getScreenControl();
        if (!clinicianToggle.isSelected()) {
            try {
                Patient newPatient = Database.getPatientByNhi(nhiLogin.getText());
                login.addLoggedInUserToCache(newPatient);
                Parent homeScreen = FXMLLoader.load(getClass().getResource("/scene/patientHome.fxml"));
                UndoableStage stage = new UndoableStage();
                screenControl.addStage(stage.getUUID(), stage);
                screenControl.show(stage.getUUID(), homeScreen);
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load patient home page", "Attempted to log in");
                systemLogger.log(Level.INFO, "Failed to find the .fxml file for login" + e.getStackTrace());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
                alert.show();
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                login.addLoggedInUserToCache(newClinician);
                UndoableStage stage = new UndoableStage();
                Parent clincianHome = FXMLLoader.load((getClass().getResource("/scene/clinicianHome.fxml")));
                screenControl.addStage(stage.getUUID(), stage);
                screenControl.show(stage.getUUID(), clincianHome);
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load clinician home page", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
                alert.show();
            }
            catch (NumberFormatException e) {
                userActions.log(Level.WARNING, "Non-numeric staff IDs are not permitted", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Non-numeric staff ID are not permitted");
                alert.show();
            }
        }
    }

    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void toggleClinician() {
        if (clinicianToggle.isSelected()) {
            clinicianToggle.setSelected(true);
            nhiLogin.setPromptText("Staff ID");
        }
        else {
            clinicianToggle.setSelected(false);
            nhiLogin.setPromptText("NHI");
        }
    }

    @Override
    public void zoomWindow(ZoomEvent zoomEvent) {
        Window currentWindow = ((Node)zoomEvent.getTarget()).getScene().getWindow();
        loginPane.setScaleX(loginPane.getScaleX() * zoomEvent.getZoomFactor());
        currentWindow.setWidth(currentWindow.getWidth() * zoomEvent.getZoomFactor() - 0.1);
        loginPane.setScaleY(loginPane.getScaleY() * zoomEvent.getZoomFactor());
        currentWindow.setHeight(currentWindow.getHeight() * zoomEvent.getZoomFactor() - 0.1);

        loginPane.setTranslateX(0);
        loginPane.setTranslateY(0);
    }

    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        loginPane.setRotate(loginPane.getRotate() + rotateEvent.getAngle());
//        ((Node)rotateEvent.getTarget()).getScene().getWindow();
    }

    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        if(!scrollEvent.isInertia()) {
            loginPane.setTranslateX(loginPane.getTranslateX() + scrollEvent.getDeltaX());
            loginPane.setTranslateY(loginPane.getTranslateY() + scrollEvent.getDeltaY());
        }
    }

}
