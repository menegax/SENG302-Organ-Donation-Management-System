package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.Clinician;

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

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the clinician profile view screen by loading the logged in clinician's profile
     */
    public void initialize() {
        UserControl userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Clinician){
            loadProfile(((Clinician) user));
        }
    }

    /**
     * Loads clinician attributes to display in the clinician profile screen
     * @param clinician clinician logged in
     */
    private void loadProfile(Clinician clinician) {
        idTxt.setText(String.valueOf(clinician.getStaffID()));
        nameTxt.setText(clinician.getConcatenatedName());
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
}
