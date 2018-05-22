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
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
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

    private ScreenControl screenControl = ScreenControl.getScreenControl();

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
                login.addLoggedInUserToCache(newPatient);
                Parent homeScreen = FXMLLoader.load(getClass().getResource("/scene/home.fxml"));
                UndoableStage stage = new UndoableStage();
                screenControl.addStage(stage.getUUID(), stage);
                screenControl.show(stage.getUUID(), homeScreen);
                screenControl.closeStage(Main.getUuid());
            }
            catch (InvalidObjectException e) {
                userActions.log(Level.WARNING, "Failed to log in", "Attempted to log in");
                new Alert(Alert.AlertType.WARNING, "Incorrect credentials").show();
            } catch (IOException e) {
                e.printStackTrace(); //TODO: rm
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                login.addLoggedInUserToCache(newClinician);
                UndoableStage stage = new UndoableStage();
                Parent clinicianHome = FXMLLoader.load((getClass().getResource("/scene/home.fxml")));
                screenControl.addStage(stage.getUUID(), stage);
                screenControl.show(stage.getUUID(), clinicianHome);
            }
            catch (Exception e) {
                e.printStackTrace();
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
                alert.show();
            }

        }
    }


    private void setUpPatientHome() {

        Stage primaryStage = new Stage();
        try {
            Scene home = FXMLLoader.load(getClass().getResource("/scene/patientHome.fxml"));

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
