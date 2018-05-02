package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Clinician;

import java.io.IOException;
import java.util.logging.Level;

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

    public void initialize() {
        loadProfile(ScreenControl.getLoggedInClinician());
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
        ScreenControl.removeScreen("clinicianProfileUpdate");
        try {
            ScreenControl.addScreen("clinicianProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/clinicianProfileUpdate.fxml")));
            ScreenControl.activate("clinicianProfileUpdate");
        }catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading clinician update screen", "attempted to navigate from the clinician profile page to the edit page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading clinician edit page", ButtonType.OK).show();
        }
    }

    public void goToClinicianHome() {
        ScreenControl.activate("clinicianHome");
    }
}
