package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import service.Database;

import java.io.IOException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianHome {
    @FXML
    public void goToClinicianProfile(){ ScreenControl.activate("clinicianProfile"); }

    @FXML
    public void goToSearch(){
        // implement search functionality
    }

    @FXML
    public void logOutClinician() {
        ScreenControl.activate("login");
    }

    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully Saved!");
        alert.showAndWait();
    }

    public void goToClinicianWaitingList(ActionEvent event) {
        ScreenControl.removeScreen("clinicianWaitingList");
        try {
            ScreenControl.addScreen("clinicianWaitingList", FXMLLoader.load(getClass().getResource("/scene/clinicianWaitingList.fxml")));
            ScreenControl.activate("clinicianWaitingList");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading organ waiting list screen", "attempted to navigate from the " +
                    "home page to the waiting list page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading organ waiting list page", ButtonType.OK).showAndWait();
        }
    }
}
