package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import main.Main;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorHome {

    @FXML
    public Button searchPatients;
    public Button searchClinicians;
    public Button searchAdministrators;
    public AnchorPane administratorHomePane;
    public Button importDataButton;
    public Button waitingListButton;
    public Button registerButton;
    public Button profileButton;
    public Button saveButton;
    public Button logoutButton;
    public Button history;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    /**
     * Opens the administrator profile screen
     */
    @FXML
    public void goToAdministratorProfile(){
        try {
            screenControl.show(administratorHomePane, "/scene/administratorProfile.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician profile").show();
            userActions.log(SEVERE, "Failed to load clinician profile", "Attempted to load clinician profile");
        }
    }

    /**
     * Opens the register user profile screen
     */
    @FXML
    public void registerNewUser() {
        try {
            screenControl.show(Main.getUuid(), FXMLLoader.load(getClass().getResource("/scene/patientRegister.fxml")));
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load user register").show();
            userActions.log(SEVERE, "Failed to load user register", "Attempted to load user register");
        }
    }

    /**
     * Opens the patient search screen
     */
    @FXML
    public void goToSearchPatients(){
        try {
            screenControl.show(administratorHomePane, "/scene/clinicianSearchPatients.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search patients").show();
            userActions.log(SEVERE, "Failed to load search patients", "Attempted to load search patients");
        }
    }

    /**
     * Opens the clinician search screen
     */
    @FXML
    public void goToSearchClinicians() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorSearchClinicians.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search clinicians").show();
            userActions.log(SEVERE, "Failed to load search clinicians", "Attempted to load search clinicians");
        }
    }

    /**
     * Opens the clinician search screen
     */
    @FXML
    public void goToSearchAdministrators() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorSearchAdministrators.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search clinicians").show();
            userActions.log(SEVERE, "Failed to load search clinicians", "Attempted to load search clinicians");
        }
    }

    /**
     * Logs out the administrator and closes the open window for the logged in administrator
     */
    @FXML
    public void logOutAdministrator() {
        screenControl.closeStage(administratorHomePane);
        userControl.setTargetPatient(null);
    }

    /**
     * Saves changes made by the administrator to the database
     */
    @FXML
    public void saveAdministrator() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
    }

    /**
     * Opens the administrator history screen
     */
    @FXML
    public void goToHistory() {
        try {
            screenControl.show(administratorHomePane, "/scene/administratorHome.fxml");
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load administrator history").show();
            userActions.log(SEVERE, "Failed to load administrator history", "Attempted to load administrator history");
        }
    }

    /**
     * Opens the receiver waiting list screen
     */
    public void goToClinicianWaitingList() {
        try {
            screenControl.show(administratorHomePane, "/scene/clinicianWaitingList.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "ERROR loading organ waiting list page").show();
            userActions.log(SEVERE, "Error loading organ waiting list screen", "attempted to navigate from the " +
                    "home page to the waiting list page");
        }
    }

    /**
     * Imports data from disk/DB..
     */
    public void importData() {
        ; // TO DO
    }
}

