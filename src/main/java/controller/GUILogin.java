package controller;

import static utility.UserActionHistory.userActions;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.*;

import javafx.scene.control.TextField;

import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import model.Administrator;
import service.Database;
import utility.TouchPaneController;
import utility.TouchscreenCapable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;

public class GUILogin implements TouchscreenCapable {

    @FXML
    public GridPane loginPane;

    public Button loginButton;

    @FXML
    private TextField nhiLogin;

    @FXML
    private PasswordField password;

    @FXML
    private RadioButton patient;

    @FXML
    private RadioButton clinician;

    @FXML
    private RadioButton administrator;

    private TouchPaneController loginTouchPane;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the login window by adding key binding for login on enter and an event filter on the login field
     */
    public void initialize() {
        // Enter key triggers log in
        loginTouchPane = new TouchPaneController(loginPane);
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
        screenControl.show("/scene/userRegister.fxml");
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
        try {
            if (patient.isSelected()) {
                login.addLoggedInUserToCache(Database.getPatientByNhi(nhiLogin.getText()));
            } else if (clinician.isSelected()) {
                login.addLoggedInUserToCache(Database.getClinicianByID(Integer.parseInt(nhiLogin.getText())));
            } else {
                checkAdminCredentials();
                login.addLoggedInUserToCache(Database.getAdministratorByUsername(nhiLogin.getText().toUpperCase()));
            }
            screenControl.show("/scene/home.fxml");
        } catch (InvalidObjectException e) {
            password.setText(""); //Reset password field on invalid login
            userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
            alert.show();
        } catch (NumberFormatException e) {
            userActions.log(Level.WARNING, "Non-numeric staff IDs are not permitted", "Attempted to log in");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Non-numeric staff ID are not permitted");
            alert.show();
        }

    }

    private void checkAdminCredentials() throws InvalidObjectException {
        Administrator admin = Database.getAdministratorByUsername(nhiLogin.getText().toUpperCase());
        String hashedInput = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password.getText() + admin.getSalt());
        if (!hashedInput.equals(admin.getHashedPassword())) {
            throw new InvalidObjectException("Invalid username/password combination");
        }
    }

    /**
     * Adjusts the prompt text to appropriately match the input required
     * for the selected user type. Clears the password when the user type is changed
     */
    @FXML
    public void onRadioSelect() {
        nhiLogin.setText("");
        password.setText("");
        if (patient.isSelected()) {
            nhiLogin.setPromptText("NHI");
            password.setDisable(true);
        } else if (clinician.isSelected()) {
            nhiLogin.setPromptText("Staff ID");
            password.setDisable(true);
        } else {
            nhiLogin.setPromptText("Username");
            password.setDisable(false);
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
        if(scrollEvent.isDirect()) {
            loginTouchPane.scrollPane(scrollEvent);
        }
    }


}
