package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.Database;

public class GUIPatientHome {
    @FXML
    public void goToProfile() { ScreenControl.activate("patientProfile"); }

    @FXML
    public void goToHistory() { ScreenControl.activate("patientHistory"); }

    @FXML
    public void logOut() {
        ScreenControl.activate("login");
    }

    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.showAndWait();
    }
}
