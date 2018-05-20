package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Clinician;

import java.io.IOException;
import java.util.logging.Level;

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

    public void initialize() {
        UserControl userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Clinician){
            loadProfile(((Clinician) user));
        }
    }

    private void loadProfile(Clinician clinician) {
        idTxt.setText(String.valueOf(clinician.getStaffID()));
        nameTxt.setText(clinician.getConcatenatedName());
        if (clinician.getStreet1() != null) street1Txt.setText(clinician.getStreet1());
        if (clinician.getStreet2() != null) street2Txt.setText(clinician.getStreet2());
        if (clinician.getSuburb() != null) suburbTxt.setText(clinician.getSuburb());
        if (clinician.getRegion() != null) regionTxt.setText(clinician.getRegion().getValue());
    }

    public void goToEdit() {
        try {
            screenControl.show(idTxt, "/scene/clinicianProfileUpdate.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load update clinician profile").show();
            userActions.log(SEVERE, "Failed to load update clinician profile", "Attempted to load update clinician profile");
        }
    }

    public void goToClinicianHome() {
        try {
            screenControl.show(idTxt, "/scene/clinicianHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician home").show();
            userActions.log(SEVERE, "Failed to load clinician home", "Attempted to load clinician home");
        }
    }
}
