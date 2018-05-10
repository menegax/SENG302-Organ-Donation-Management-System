package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.Database;

public class GUIClinicianHome {
    @FXML
    public void goToClinicianProfile(){ ScreenControl.activate("clinicianProfile"); }

    @FXML
    public void goToSearchPatients(){
        ScreenControl.activate("clinicianSearchPatients");
    }

    @FXML
    public void logOutClinician() {
        ScreenControl.activate("login");
    }

    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
    }
}
