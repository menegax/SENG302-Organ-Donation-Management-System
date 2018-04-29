package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import model.Donor;
import service.Database;


public class GUIDonorHome {
    @FXML
    public void goToProfile() {
        if (ScreenControl.getLoggedInDonor() instanceof Donor) {
            ScreenControl.activate("donorProfile");
        } else {
            ScreenControl.activate("receiverProfile");
        }
    }


    @FXML
    public void goToHistory() {
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
}
