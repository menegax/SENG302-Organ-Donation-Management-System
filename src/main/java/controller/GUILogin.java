package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import model.Clinician;
import model.Donor;
import service.Database;

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUILogin {

    @FXML

    public AnchorPane loginPane;

    public Button loginButton;

    public Hyperlink registerLabel;

    @FXML
    private TextField nhiLogin;



    @FXML
    private CheckBox clinicianToggle;

    public void initialize() {
        // Enter key triggers log in
        loginPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                logIn();
            }
        });
    }


    /**
     * Open the register screen
     */
    @FXML
    public void goToRegister() {
        ScreenControl.activate("donorRegister");
    }


    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void logIn() {
        // todo surround with try catch. Try uses database getuserbyNHI, catch will throw a popup with warning alert
        if (!clinicianToggle.isSelected()) {
            try {
                Donor newDonor = Database.getDonorByNhi(nhiLogin.getText());
                ScreenControl.setLoggedInDonor(newDonor);
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.addScreen("donorProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/donorUpdateProfile.fxml")));
                ScreenControl.addScreen("donorDonations", FXMLLoader.load(getClass().getResource("/scene/donorUpdateDonations.fxml")));
                ScreenControl.addScreen("donorContacts", FXMLLoader.load(getClass().getResource("/scene/donorUpdateContacts.fxml")));
                ScreenControl.activate("donorHome");
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.addScreen("donorHistory", FXMLLoader.load(getClass().getResource("/scene/donorHistory.fxml")));
            }
            catch (Exception e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
                alert.show();
            }
        }
        else {
            try {
                Clinician newClinician = Database.getClinicianByID(Integer.parseInt(nhiLogin.getText()));
                ScreenControl.setLoggedInClinician(newClinician);
                ScreenControl.addScreen("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
//                ScreenControl.addScreen("clinicianSearchDonors", FXMLLoader.load(getClass().getResource("/scene/clinicianSearchDonors.fxml")));
                ScreenControl.addScreen("clinicianProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/clinicianProfileUpdate.fxml")));
                ScreenControl.activate("clinicianHome");
            }
            catch (Exception e) {
                userActions.log(Level.WARNING, "failed to log in", "attempted to log in");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to log in");
                alert.show();
            }

        }
    }


    /**
     * Attempt to log the user in using the entered NHI
     * If successful, opens home screen
     * If failed, gives alert
     */
    @FXML
    public void toggleClinician() {
        if (clinicianToggle.isSelected()) {
            clinicianToggle.setSelected(true);
            nhiLogin.setPromptText("Staff ID");
        }
        else {
            clinicianToggle.setSelected(false);
            nhiLogin.setPromptText("NHI");
        }
    }

}
