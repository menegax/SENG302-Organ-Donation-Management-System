package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.User;
import service.Database;

import java.io.IOException;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

public class GUIClinicianProfile {
    @FXML
    private GridPane clinicianProfilePane;

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

    @FXML
    private Button back;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private UserControl userControl = new UserControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        if (userControl.getLoggedInUser() instanceof Clinician) {
            deleteButton.setVisible( false );
            deleteButton.setDisable( true );
            loadProfile( ((Clinician) userControl.getLoggedInUser()) );
        } else if (userControl.getLoggedInUser() instanceof Administrator) {
            back.setVisible(false);
            back.setDisable(true);
            loadProfile( ((Clinician) userControl.getTargetUser()));
            if (((Clinician) userControl.getTargetUser()).getStaffID() == 0) {
                deleteButton.setVisible( false );
                deleteButton.setDisable( true );
            }
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
        Clinician clinician = (Clinician) userControl.getTargetUser();
        if (clinician.getStaffID() != 0) {
            userActions.log(Level.INFO, "Successfully deleted clinician profile", "Attempted to delete clinician profile");
            Database.deleteClinician( clinician );
            ((Stage) clinicianProfilePane.getScene().getWindow()).close();
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
