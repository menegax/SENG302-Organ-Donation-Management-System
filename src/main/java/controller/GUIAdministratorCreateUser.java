package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIAdministratorCreateUser {
    @FXML
    private AnchorPane administratorCreateUserPane;
    private ScreenControl screenControl = ScreenControl.getScreenControl();

    @FXML
    public void goToCreatePatient() {
        try {
            screenControl.show(administratorCreateUserPane, "/scene/patientRegister.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load patient register").show();
            userActions.log(SEVERE, "Failed to load patient register", "Attempted to load patient register");
        }
    }

    @FXML
    public void goToCreateClinician() {

    }

    @FXML
    public void goToCreateAdministrator() {

    }

    @FXML
    public void goToAdminHome() {
        try {
            screenControl.show(administratorCreateUserPane, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator home").show();
            userActions.log(SEVERE, "Failed to load administrator home", "Attempted to load administrator home");
        }
    }
}
