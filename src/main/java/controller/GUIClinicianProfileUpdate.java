package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import model.Clinician;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.UserActionHistory.userActions;

public class GUIClinicianProfileUpdate {
    @FXML
    private Label lastModifiedLbl;

    @FXML
    private TextField staffId;

    @FXML
    private TextField firstnameTxt;
    @FXML
    private TextField lastnameTxt;
    @FXML
    private TextField middlenameTxt;

    @FXML
    private TextField street1Txt;
    @FXML
    private TextField street2Txt;
    @FXML
    private TextField suburbTxt;
    @FXML
    private TextField regionTxt;

    private Clinician target;

    public void initialize() {
        loadProfile(ScreenControl.getLoggedInClincian().getStaffID());
    }

    private void loadProfile(int staffId) {
        try {
            Clinician clinician = Database.getClinicianByID(staffId);
            target = clinician;
            populateForm(clinician);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to edit the logged in user");
            e.printStackTrace();
        }
    }

    private void populateForm(Clinician clinician) {
//        lastModifiedLbl.setText("Last Modified: " + clinician.getModified());
        staffId.setText(Integer.toString(clinician.getStaffID()));
        firstnameTxt.setText(clinician.getFirstName());
        lastnameTxt.setText(clinician.getLastName());
        for (String name : clinician.getMiddleNames()) {
            middlenameTxt.setText(middlenameTxt.getText() + name + " ");
        }
        if (clinician.getStreet1() != null) street1Txt.setText(clinician.getStreet1());
        if (clinician.getStreet2() != null) street2Txt.setText(clinician.getStreet2());
        if (clinician.getSuburb() != null) suburbTxt.setText(clinician.getSuburb());
        if (clinician.getRegion() != null) regionTxt.setText(clinician.getRegion().getValue());
    }

    public void saveProfile() {
        Boolean valid = true;
        if (!Pattern.matches("[0-9]{1,3}", staffId.getText())) {
            valid = false;
            setInvalid(staffId);
        }
        if (firstnameTxt.getText().length() == 0) {
            valid = false;
            setInvalid(firstnameTxt);
        }
        if (lastnameTxt.getText().length() == 0) {
            valid = false;
            setInvalid(lastnameTxt);
        }
        if (regionTxt.getText().length() > 0) {
            Enum region = GlobalEnums.Region.getEnumFromString(regionTxt.getText());
            if (region == null) {
                valid = false;
                setInvalid(regionTxt);
            }
        }
        if (valid) {
            target.setStaffID(Integer.parseInt(staffId.getText()));
            target.setFirstName(firstnameTxt.getText());
            target.setLastName(lastnameTxt.getText());
            List<String> middlenames = Arrays.asList(middlenameTxt.getText().split(" "));
            ArrayList middles = new ArrayList();
            middles.addAll(middlenames);
            target.setMiddleNames(middles);

            if (street1Txt.getText().length() > 0) target.setStreet1(street1Txt.getText());
            if (street2Txt.getText().length() > 0) target.setStreet2(street2Txt.getText());
            if (suburbTxt.getText().length() > 0) target.setSuburb(suburbTxt.getText());
            if (regionTxt.getText().length() > 0) target.setRegion((GlobalEnums.Region) GlobalEnums.Region.getEnumFromString(regionTxt.getText()));
            new Alert(Alert.AlertType.CONFIRMATION, "Donor successfully updated", ButtonType.OK).showAndWait();
            goBackToProfile();
        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid fields", ButtonType.OK).showAndWait();
        }
    }

    private void setInvalid(TextField target) {
        target.getStyleClass().add("invalid");
    }

    /**
     * Checks if the keyevent target was a textfield. If so, if the target has the invalid class, it is removed.
     * @param e The KeyEvent instance
     */
    public void onKeyReleased(KeyEvent e) {
        if (e.getTarget().getClass() == TextField.class) {
            TextField target = (TextField) e.getTarget();
            if (target.getStyleClass().contains("invalid")) {
                target.getStyleClass().remove("invalid");
            }
        }
    }

    public void goBackToProfile() {
        ScreenControl.removeScreen("clinicianProfile");
        try {
            ScreenControl.addScreen("clinicianProfile", FXMLLoader.load(getClass().getResource("/scene/clinicianProfile.fxml")));
            ScreenControl.activate("clinicianProfile");
        }catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the edit page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
