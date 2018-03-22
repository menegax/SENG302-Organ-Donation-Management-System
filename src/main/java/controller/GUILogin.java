package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.Donor;
import service.Database;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUILogin {

    @FXML
    private TextField nhiLogin;
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
            Donor newDonor = Database.getDonorByNhi(nhiLogin.getText());
            ScreenControl.setLoggedInDonor(newDonor);
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.addScreen("donorProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/donorProfileUpdate.fxml")));
            ScreenControl.addScreen("donorDonations", FXMLLoader.load(getClass().getResource("/scene/donorDonations.fxml")));
            ScreenControl.activate("home");
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.addScreen("donorHistory", FXMLLoader.load(getClass().getResource("/scene/donorHistory.fxml")));
        } catch (Exception e) {
            userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
            alert.show();
        }
    }

}
