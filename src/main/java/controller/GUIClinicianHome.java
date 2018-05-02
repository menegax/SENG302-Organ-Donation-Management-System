package controller;

import cli.CLIMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.Database;

public class GUIClinicianHome {
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
    public static void launchCli() {
//        (Stage) ScreenControl.getMain().getRoot()
        CLIMain.main(null);
        Platform.exit();
    }

    @FXML
    public void saveClinician() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully Saved!");
        alert.show();
    }
}
