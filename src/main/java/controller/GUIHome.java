package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import service.Database;


public class GUIHome {

    @FXML
    public AnchorPane homePane;

    @FXML
    public void goToProfile(){
        ScreenControl.activate("donorProfile");
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

}
