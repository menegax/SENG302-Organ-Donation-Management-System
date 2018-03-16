package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.Database;

import java.io.InvalidObjectException;

public class GUILogin {

    @FXML
    public void register(){
//        GUIScreenControl.activate("home");
    }

    @FXML
    public void logIn(){
        // todo surround with try catch. Try uses database getuserbyNHI, catch will throw a popup with error alert
        try {
//            Database.getDonorByNhi()
            GUIScreenControl.activate("home");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to log in");
            alert.show();
            // todo add a logger.log
        }
    }

}
