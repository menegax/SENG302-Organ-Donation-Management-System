package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;

import javafx.scene.input.KeyCode;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import model.Clinician;
import model.Patient;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;


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
        UserControl login = new UserControl();
        ScreenControl screenControl = ScreenControl.getScreenControl();
        if (!clinicianToggle.isSelected()) {
            try {
                Patient newPatient = Database.getPatientByNhi(nhiLogin.getText());
                login.addLoggedInUserToCache(newPatient);
                Parent homeScreen = FXMLLoader.load(getClass().getResource("/scene/home.fxml"));
                screenControl.addStage(GlobalEnums.Stages.HOME, new Stage());
                screenControl.show(GlobalEnums.Stages.HOME, homeScreen);
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Failed to log in", "Attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Incorrect credentials");
                alert.show();
            } catch (IOException e) {
                e.printStackTrace(); //TODO: rm
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                login.addLoggedInUserToCache(newClinician);
                ScreenControl.addTabToHome("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
                ScreenControl.addTabToHome("clinicianSearchPatients", FXMLLoader.load(getClass().getResource("/scene/clinicianSearchPatients.fxml")));
                ScreenControl.addTabToHome("clinicianProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/clinicianProfileUpdate.fxml")));
                ScreenControl.activate("clinicianHome");
            }
            catch (Exception e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
                alert.show();
            }

        }
    }


    private void setUpPatientHome() {

        Stage primaryStage = new Stage();
        try {
            Scene home = FXMLLoader.load(getClass().getResource("/scene/home.fxml"));

            primaryStage.setScene(home);
        }
        catch (IOException e) {
            e.printStackTrace();//todo rm
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
