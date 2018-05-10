package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import service.Database;

public class GUIClinicianHome {

    public AnchorPane clinicianHomePane;

    public Button profileButton;

    public Button saveButton;

    public Button logoutButton;


    @FXML
    public void goToClinicianProfile(){ ScreenControl.activate("clinicianProfile"); }

    @FXML
    public void goToSearchDonors(){
        ScreenControl.activate("clinicianSearchDonors");
    }

    @FXML
    public void logOutClinician() {
        ScreenControl.activate("login");
    }

    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully Saved!");
        alert.show();
    }
}
