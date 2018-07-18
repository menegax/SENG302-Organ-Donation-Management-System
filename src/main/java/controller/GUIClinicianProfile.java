package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Clinician;
import model.Administrator;
import service.Database;

import java.io.IOException;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIClinicianProfile {
    @FXML
    private Label idTxt;

    @FXML
    private Label nameTxt;

    @FXML
    private Label street1Txt;

    @FXML
    private Label street2Txt;

    @FXML
    private Label suburbTxt;

    @FXML
    private Label regionTxt;

    @FXML
    private Button deleteButton;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private Object user = new UserControl().getLoggedInUser();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        if (user instanceof Clinician) {
            deleteButton.setVisible( false );
            deleteButton.setDisable( true );
            loadProfile( ((Clinician) user) );
        } else if (user instanceof Administrator) {
            loadProfile( ((Clinician) user));
        } else {
            deleteButton.setVisible( false );
            deleteButton.setDisable( true );
        }
    }

    /**
     * Loads clinician attributes to display in the clinician profile screen
     * @param clinician clinician logged in
     */
    private void loadProfile(Clinician clinician) {
        idTxt.setText(String.valueOf(clinician.getStaffID()));
        nameTxt.setText(clinician.getNameConcatenated());
        if (clinician.getStreet1() != null && clinician.getStreet1().length() > 0) {
            street1Txt.setText(clinician.getStreet1());
        } else {
            street1Txt.setText("Not Set");
        }
        if (clinician.getStreet2() != null && clinician.getStreet2().length() > 0) {
            street2Txt.setText(clinician.getStreet2());
        } else {
            street2Txt.setText("Not Set");
        }
        if (clinician.getSuburb() != null && clinician.getSuburb().length() > 0) {
            suburbTxt.setText(clinician.getSuburb());
        } else {
            suburbTxt.setText("Not Set");
        }
        if (clinician.getRegion() != null) regionTxt.setText(clinician.getRegion().getValue());
    }

    /**
     * Opens the clinician edit screen
     */
    public void goToEdit() {
        try {
            screenControl.show(idTxt, "/scene/clinicianProfileUpdate.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load update clinician profile").show();
            userActions.log(SEVERE, "Failed to load update clinician profile", "Attempted to load update clinician profile");
        }
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        Clinician clinician = (Clinician) user;
        if (clinician.getStaffID() != 0) {
            Database.deleteClinician( clinician );
            goToAdministratorHome();
        }
    }

    /**
     * Opens the clinician home screen
     */
    public void goToClinicianHome() {
        try {
            screenControl.show(idTxt, "/scene/clinicianHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician home").show();
            userActions.log(SEVERE, "Failed to load clinician home", "Attempted to load clinician home");
        }
    }

    /**
     * Opens the administrator home screen
     */
    private void goToAdministratorHome() {
        try {
            screenControl.show(idTxt, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator home").show();
            userActions.log(SEVERE, "Failed to load administrator home", "Attempted to load administrator home");
        }
    }
}
