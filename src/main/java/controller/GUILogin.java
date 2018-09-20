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
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.Patient;
import service.AdministratorDataService;
import service.ClinicianDataService;
import service.PatientDataService;
import service.interfaces.IClinicianDataService;
import utility.TouchPaneController;
import utility.TouchscreenCapable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.SystemLogger.systemLogger;

public class GUILogin implements TouchscreenCapable, IWindowObserver {

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

    private PatientDataService patientDataService = new PatientDataService();

    private AdministratorDataService administratorDataService = new AdministratorDataService();

    private UserControl userControl = UserControl.getUserControl();

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
        if(screenControl.isTouch()) {
            loginTouchPane = new TouchPaneController(loginPane);
            loginPane.setOnZoom(this::zoomWindow);
            loginPane.setOnRotate(this::rotateWindow);
            loginPane.setOnScroll(this::scrollWindow);
        }

    }

    /**
     * Open the register screen
     */
    @FXML
    public void goToRegister() {
        screenControl.show("/scene/userRegister.fxml", false, null, null);
    }

    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void logIn() {
        try {
            if (patient.isSelected()) {
                Patient patient2 = patientDataService.getPatientByNhi(nhiLogin.getText());
                if (patient2 == null) {
                    throw new InvalidObjectException("User doesn't exist");
                }
                patientDataService.save(patient2);
                userControl.addLoggedInUserToCache(patientDataService.getPatientByNhi(nhiLogin.getText()));

            } else if (clinician.isSelected()) {
                IClinicianDataService clinicianDataService = new ClinicianDataService();
                Clinician clinician = clinicianDataService.getClinician(Integer.parseInt(nhiLogin.getText()));
                if (clinician == null) {
                    throw new InvalidObjectException("User doesn't exist");
                }
                clinicianDataService.save(clinician);
                userControl.addLoggedInUserToCache(clinicianDataService.getClinician(Integer.parseInt(nhiLogin.getText())));
                openMap();
                screenControl.getMapController().setPatients(new ArrayList<>());
            } else {
                administrator.isSelected();
                checkAdminCredentials();
                Administrator administrator = administratorDataService.getAdministratorByUsername(nhiLogin.getText().toUpperCase());
                administratorDataService.save(administrator);
                userControl.addLoggedInUserToCache(administratorDataService.getAdministratorByUsername(nhiLogin.getText().toUpperCase()));
            }
            GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, userControl.getLoggedInUser());
            controller.setTarget(userControl.getLoggedInUser());
            if(!screenControl.isTouch()) {
                Stage stage = (Stage) loginPane.getScene().getWindow();
                stage.close();
            }
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

    private void openMap() {
        screenControl.show("/scene/map.fxml", true, this, userControl.getLoggedInUser());
        screenControl.setMapOpen(true);
    }


    /**
     * Called when the map shown on login is closed
     */
    public void windowClosed() {
        screenControl.setMapOpen(false);
    }

    private void checkAdminCredentials() throws InvalidObjectException {
        Administrator admin = administratorDataService.getAdministratorByUsername(nhiLogin.getText().toUpperCase());
        if (admin == null) {
            throw new InvalidObjectException("User doesn't exist");
        }
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
