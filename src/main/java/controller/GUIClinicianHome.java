package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

import java.io.IOException;

import static utility.UserActionHistory.userActions;

import static java.util.logging.Level.SEVERE;

public class GUIClinicianHome {

    @FXML
    public Button searchPatients;

    public AnchorPane clinicianHomePane;

    public Button profileButton;

    public Button saveButton;

    public Button logoutButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = new UserControl();

    /**
     * Opens the clinician profile screen
     */
    @FXML
    public void goToClinicianProfile(){
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianProfile.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician profile").show();
            userActions.log(SEVERE, "Failed to load clinician profile", "Attempted to load clinician profile");
        }
    }

    /**
     * Opens the patient search screen
     */
    @FXML
    public void goToSearchPatients(){
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianSearchPatients.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search patients").show();
            userActions.log(SEVERE, "Failed to load search patients", "Attempted to load search patients");
        }
    }

    /**
     * Logs out the clinician and closes the open window for the logged in clinician
     */
    @FXML
    public void logOutClinician() {
        screenControl.closeStage(clinicianHomePane);
        userControl.setTargetUser(null);
    }

    /**
     * Saves changes made to the clinician to the database
     */
    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
    }

    /**
     * Opens the clinician history screen
     */
    @FXML
    public void goToHistory() {
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianHistory.fxml");
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load clinician history").show();
            userActions.log(SEVERE, "Failed to load clinician history", "Attempted to load clinician history");

        }
    }

    /**
     * Opens the receiver waiting list screen
     */
    public void goToClinicianWaitingList() {
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianWaitingList.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "ERROR loading organ waiting list page").show();
            userActions.log(SEVERE, "Error loading organ waiting list screen", "attempted to navigate from the " +
                    "home page to the waiting list page");
        }
    }
}
