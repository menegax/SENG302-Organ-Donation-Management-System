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
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianHome {

    public Button searchPatients;

    public AnchorPane clinicianHomePane;

    public Button profileButton;

    public Button saveButton;

    public Button logoutButton;

    Database database = Database.getDatabase();

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
        database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Saved!");
        alert.show();
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
