package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import model.Donor;
import service.Database;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUILogin {

    /**
     * Open the register screen
     */
    @FXML
    public void goToRegister(){
        ScreenControl.activate("donorRegister");
    }

    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void logIn(){
        // todo surround with try catch. Try uses database getuserbyNHI, catch will throw a popup with warning alert
        try {
            Donor newDonor = Database.getDonorByNhi("AAA111"); // todo change AAA111 to getNhi() from textfield in fxml
            ScreenControl.setLoggedInDonor(newDonor);
            ScreenControl.activate("home");
        } catch (Exception e) {
            userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
            alert.show();
        }
    }

}
