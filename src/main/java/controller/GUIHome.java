package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.Database;


public class GUIHome {

    private static boolean clinician;

    @FXML
    public void goToProfile(){
        if (!clinician) {
            ScreenControl.activate("donorProfile");
        } else {
            ScreenControl.activate("clinicianProfile");
        }
    }

    @FXML
    public void goToHistory(){
        ScreenControl.activate("donorHistory");
    }

    @FXML
    public void logOut() {
        ScreenControl.activate("login");
    }

    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully Saved!");
        alert.showAndWait();
    }

    public static boolean getClinician() { return clinician; }

    public static boolean setClinician(boolean set) { return clinician = set; }
}
