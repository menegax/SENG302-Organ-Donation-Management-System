package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianHome {

    public Button searchPatients;

    public AnchorPane clinicianHomePane;

    public Button profileButton;

    public Button saveButton;

    public Button logoutButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    @FXML
    public void goToClinicianProfile(){
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianProfile.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician profile").show();
            userActions.log(SEVERE, "Failed to load clinician profile", "Attempted to load clinician profile");
        }
    }

    @FXML
    public void goToSearchPatients(){
        try {
            screenControl.show(clinicianHomePane, "/scene/clinicianSearchPatients.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load search patients").show();
            userActions.log(SEVERE, "Failed to load search patients", "Attempted to load search patients");
        }
    }

    @FXML
    public void logOutClinician() {
        screenControl.closeStage(clinicianHomePane);
    }

    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
    }

    @FXML
    public void goToHistory() {
        try {
            ScreenControl.addPopUp("clinicianHistory", FXMLLoader.load(getClass().getResource("/scene/clinicianHistory.fxml")));
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load clinician history").show();
        }
        ScreenControl.activate("clinicianHistory");
    }

    public void goToClinicianWaitingList(ActionEvent event) {
        ScreenControl.removeScreen("clinicianWaitingList");
        try {
            ScreenControl.addPopUp("clinicianWaitingList", FXMLLoader.load(getClass().getResource("/scene/clinicianWaitingList.fxml")));
            ScreenControl.activate("clinicianWaitingList");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading organ waiting list screen", "attempted to navigate from the " +
                    "home page to the waiting list page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading organ waiting list page", ButtonType.OK).showAndWait();
        }
    }
}
