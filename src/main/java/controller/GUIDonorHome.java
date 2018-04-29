package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import service.Database;


public class GUIDonorHome {

    @FXML
    public AnchorPane homePane;
    public Button goToProfile;

    @FXML
    public void goToProfile(){
        ScreenControl.activate("donorProfile");
    }


    @FXML
    public void goToHistory() {
        ScreenControl.activate("donorHistory");
    }


    @FXML
    public void logOut() {
        ScreenControl.setLoggedInDonor(null);
        ScreenControl.activate("login");
    }


    @FXML
    public void save() {
        Database.saveToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully saved!");
        alert.showAndWait();
    }
}
