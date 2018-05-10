package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;

import javafx.scene.input.KeyCode;
import javafx.scene.control.CheckBox;
<<<<<<< HEAD
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
=======
>>>>>>> refs/heads/development
import javafx.scene.layout.Pane;
import model.Clinician;
import model.Patient;
import service.Database;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;
import static utility.UserActionRecord.logHistory;

public class GUILogin {

    @FXML

    public AnchorPane loginPane;

    public Button loginButton;

    public Hyperlink registerLabel;

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
        if (!clinicianToggle.isSelected()) {
            try {
                Patient newPatient = Database.getPatientByNhi(nhiLogin.getText());
                ScreenControl.setLoggedInPatient(newPatient); // THIS SHOULD BE CAHCED
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/patientUpdateProfile.fxml"));
                Pane pane = loader.load();
                GUIPatientUpdateProfile controller = loader.getController();
                ScreenControl.setLoggedInPatient(newPatient);
                loader.setController(controller);
                ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
                ScreenControl.addScreen("patientProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/patientUpdateProfile.fxml")));
                ScreenControl.addScreen("patientDonations", FXMLLoader.load(getClass().getResource("/scene/patientUpdateDonations.fxml")));
                ScreenControl.addScreen("patientContacts", FXMLLoader.load(getClass().getResource("/scene/patientUpdateContacts.fxml")));
                ScreenControl.activate("patientHome");
                ScreenControl.addScreen("patientHistory", FXMLLoader.load(getClass().getResource("/scene/patientHistory.fxml")));
                ScreenControl.activate("patientHome");
                if (newPatient.getPatientLog() != null) {
                    logHistory.addAll( newPatient.getPatientLog() ); // adds medication log from previous log-ins for user
                }
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            }
            catch (IOException e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading application scenes");
                alert.show();
<<<<<<< HEAD

=======
>>>>>>> refs/heads/development
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                ScreenControl.setLoggedInClinician(newClinician);
                ScreenControl.addScreen("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
                ScreenControl.addScreen("clinicianSearchPatients", FXMLLoader.load(getClass().getResource("/scene/clinicianSearchPatients.fxml")));
                ScreenControl.addScreen("clinicianProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/clinicianProfileUpdate.fxml")));
                ScreenControl.activate("clinicianHome");
            }
            catch (Exception e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
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
