package controller;

import static utility.UserActionHistory.userActions;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Clinician;
import model.Patient;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;


public class GUILogin {

    @FXML
    public AnchorPane loginPane;

    public Button loginButton;

    @FXML
    private TextField nhiLogin;

    @FXML
    private CheckBox clinicianToggle;


    public void initialize() {
        // Enter key triggers log in
        loginPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                logIn();
            }
        });
    }


    /**
     * Open the register screen
     */
    @FXML
    public void goToRegister() {
        ScreenControl.activate("patientRegister");
    }


    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void logIn() {
        UserControl login = new UserControl();
        if (!clinicianToggle.isSelected()) {
            try {
                Patient newPatient = Database.getPatientByNhi(nhiLogin.getText());
                login.addLoggedInUserToCache(newPatient);
                ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
                ScreenControl.addScreen("patientProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/patientUpdateProfile.fxml")));
                ScreenControl.addScreen("patientDonations", FXMLLoader.load(getClass().getResource("/scene/patientUpdateDonations.fxml")));
                ScreenControl.addScreen("patientContacts", FXMLLoader.load(getClass().getResource("/scene/patientUpdateContacts.fxml")));
                ScreenControl.activate("patientHome");
//                if (newPatient.getPatientLog() != null) {
//                    logHistory.addAll( newPatient.getPatientLog() ); // adds medication log from previous log-ins for user
//                }
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load patient home page", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
                alert.show();
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                login.addLoggedInUserToCache(newClinician);
                ScreenControl.addScreen("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
                ScreenControl.addScreen("clinicianSearchPatients", FXMLLoader.load(getClass().getResource("/scene/clinicianSearchPatients.fxml")));
                ScreenControl.addScreen("clinicianProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/clinicianProfileUpdate.fxml")));
                ScreenControl.activate("clinicianHome");
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Incorrect credentials", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "Unable to load clinician home page", "Attempted to log in");
                e.printStackTrace(); //todo rm
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

}
