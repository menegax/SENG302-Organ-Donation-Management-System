package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.Database;

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
}
