package controller;

import static utility.UserActionHistory.userActions;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.TextField;

import model.Clinician;
import model.Patient;
import org.tuiofx.Configuration;
import org.tuiofx.TuioFX;
import service.Database;
import utility.TouchCapablePane;
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

    private TouchCapablePane loginTouchPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the login window by adding key binding for login on enter and an event filter on the login field
     */
    public void initialize() {
        // Enter key triggers log in
        loginTouchPane = new TouchCapablePane(loginPane);
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
                login.clearCache(); //clear cache on user login
                UndoableStage stage = new UndoableStage();
                login.addLoggedInUserToCache(newPatient);
                screenControl.addStage(stage.getUUID(), stage);
                Parent homeScreen = FXMLLoader.load(getClass().getResource("/scene/home.fxml"));
                screenControl.show(stage.getUUID(), homeScreen);
                screenControl.closeStage(Main.getUuid()); // close login scene after login
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load patient home page", "Attempted to log in");
                systemLogger.log(Level.INFO, "Failed to find the .fxml file for login", e);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
                alert.show();
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                UndoableStage stage = new UndoableStage();
                login.addLoggedInUserToCache(newClinician);
                screenControl.addStage(stage.getUUID(), stage);
                Parent clinicianHome = FXMLLoader.load((getClass().getResource("/scene/home.fxml")));
                TuioFX tuioFXLoggedIn = new TuioFX(stage, Configuration.debug());
                tuioFXLoggedIn.start();
                screenControl.show(stage.getUUID(), clinicianHome);
                screenControl.closeStage(Main.getUuid()); // close login scene after login
            }
            catch (InvalidObjectException | NumberFormatException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load clinician home page", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
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
        loginTouchPane.zoomPane(zoomEvent);
    }

    @Override
    public void rotateWindow(RotateEvent rotateEvent) {
        loginTouchPane.rotatePane(rotateEvent);
    }

    @Override
    public void scrollWindow(ScrollEvent scrollEvent) {
        loginTouchPane.scrollPane(scrollEvent);
    }

}
