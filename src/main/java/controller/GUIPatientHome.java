package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

public class GUIPatientHome {

    @FXML
    public AnchorPane homePane;

    public Button profileButton;

    public Button historyButton;

    public Button saveButton;

    public Button logOutButton;


    @FXML
    public void goToProfile() {
        ScreenControl.activate("patientProfile");
    }


    @FXML
    public void goToHistory() {
        ScreenControl.activate("patientHistory");
    }


    @FXML
    public void logOut() {
        ScreenControl.setLoggedInPatient(null);
        ScreenControl.activate("login");
    }


    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully saved!");
        alert.show();
    }
}