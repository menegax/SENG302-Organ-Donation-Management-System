package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Administrator;
import model.Clinician;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorProfile {
    @FXML
    private Label usernameTxt;

    @FXML
    private Label nameTxt;

    @FXML
    private Label modifiedLbl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private Object user = new UserControl().getLoggedInUser();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        loadProfile((Administrator) user);
    }

    /**
     * Loads administrator attributes to display in the administrator profile screen
     * @param administrator clinician logged in
     */
    private void loadProfile(Administrator administrator) {
        usernameTxt.setText(administrator.getUsername());
        nameTxt.setText(administrator.getConcatenatedName());
        modifiedLbl.setText(administrator.getModified() == null ? "--" : administrator.getModified().toString());
    }

    /**
     * Opens the administrator edit screen
     */
    public void goToEdit() {
        try {
            screenControl.show(usernameTxt, "/scene/administratorProfileUpdate.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load update administrator profile").show();
            userActions.log(SEVERE, "Failed to load update administrator profile", "Attempted to load update administrator profile");
        }
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        Administrator administrator = (Administrator) user;
        if (!administrator.getUsername().equals("admin")) {
            //Database.deleteAdministrator(administrator);
            goToAdministratorHome();
        }
    }

    /**
     * Opens the administrator home screen
     */
    @FXML
    public void goToAdministratorHome() {
        try {
            screenControl.show(usernameTxt, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator home").show();
            userActions.log(SEVERE, "Failed to load administrator home", "Attempted to load administrator home");
        }
    }
}
