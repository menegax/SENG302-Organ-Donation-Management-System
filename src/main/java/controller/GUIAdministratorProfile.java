package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Administrator;
import model.User;
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

    @FXML
    private Button deleteButton;

    private Administrator target;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private UserControl userControl = new UserControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        User loggedIn = userControl.getLoggedInUser();
        if (userControl.getTargetUser() instanceof Administrator) {
            target = (Administrator) userControl.getTargetUser();
        } else {
            target = (Administrator) loggedIn;
        }
        if (target.getUsername().toLowerCase().equals("admin")) {
            deleteButton.setVisible(false);
            deleteButton.setDisable(true);
        }
        loadProfile(target);
    }

    /**
     * Loads administrator attributes to display in the administrator profile screen
     * @param administrator clinician logged in
     */
    private void loadProfile(Administrator administrator) {
        usernameTxt.setText(administrator.getUsername());
        nameTxt.setText(administrator.getNameConcatenated());
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
        if (!target.getUsername().toLowerCase().equals("admin")) {
            Database.deleteAdministrator(target);
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
