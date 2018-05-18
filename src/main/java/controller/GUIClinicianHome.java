package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

import java.io.IOException;

public class GUIClinicianHome {

    public Button searchPatients;

    public AnchorPane clinicianHomePane;

    public Button profileButton;

    public Button saveButton;

    public Button logoutButton;


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

    @FXML
    public void goToHistory() {
        try {
            ScreenControl.addScreen("clinicianHistory", FXMLLoader.load(getClass().getResource("/scene/clinicianHistory.fxml")));
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable load clinician history").show();
        }
        ScreenControl.activate("clinicianHistory");
    }
}
