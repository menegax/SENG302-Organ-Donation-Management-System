package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import model.Patient;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIPatientProfile {

    @FXML
    private Label nhiLbl;

    @FXML
    private Label nameLbl;

    @FXML
    private Label genderLbl;

    @FXML
    private Label dobLbl;

    @FXML
    private Label dateOfDeath;

    @FXML
    private Label age;

    @FXML
    private Label heightLbl;

    @FXML
    private Label weightLbl;

    @FXML
    private Label bmi;

    @FXML
    private Label bloodGroupLbl;

    @FXML
    private Label addLbl1;

    @FXML
    private Label addLbl2;

    @FXML
    private Label addLbl3;

    @FXML
    private Label addLbl4;

    @FXML
    private Label addLbl5;

    @FXML
    private Label donationList;


    public void initialize() {
        try {
            loadProfile(ScreenControl.getLoggedInPatient()
                    .getNhiNumber());
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Unable to load patient from database", "Attempted to load patient profile");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load patient from database");
            alert.showAndWait();
        }
    }


    private void loadProfile(String nhi) throws InvalidObjectException {
        Patient patient = Database.getPatientByNhi(nhi);

        nhiLbl.setText(patient.getNhiNumber());
        nameLbl.setText(patient.getNameConcatenated());
        genderLbl.setText(patient.getGender() == null ? "Not set" : patient.getGender()
                .toString());
        dobLbl.setText(patient.getBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateOfDeath.setText(patient.getDeath() == null ? "Not set" : patient.getDeath().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        age.setText(String.valueOf(patient.getAge()));
        heightLbl.setText(String.valueOf(patient.getHeight() + " m"));
        weightLbl.setText(String.valueOf(patient.getWeight() + " kg"));
        bmi.setText(String.valueOf(patient.getBmi()));
        bloodGroupLbl.setText(patient.getBloodGroup() == null ? "Not set" : patient.getBloodGroup()
                .getValue());
        addLbl1.setText(patient.getStreet1() == null ? "Not set" : patient.getStreet1());
        addLbl2.setText(patient.getStreet2() == null ? "Not set" : patient.getStreet2());
        addLbl3.setText(patient.getSuburb() == null ? "Not set" : patient.getSuburb());
        addLbl4.setText(patient.getRegion() == null ? "Not set" : patient.getRegion()
                .getValue());
        if(patient.getZip() != 0) {
            addLbl5.setText(String.valueOf(patient.getZip()));
            while (addLbl5.getText().length() < 4) {
                addLbl5.setText("0" + addLbl5.getText());

            }
        } else addLbl5.setText("Not set");
        for (GlobalEnums.Organ organ : patient.getDonations()) {
            donationList.setText(donationList.getText() + StringUtils.capitalize(organ.getValue()) + "\n");
        }
    }


    public void goToEdit() {
        ScreenControl.removeScreen("patientProfileUpdate");
        try {
            ScreenControl.addScreen("patientProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/patientUpdateProfile.fxml")));
            ScreenControl.activate("patientProfileUpdate");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
            new Alert(Alert.AlertType.WARNING, "Error loading edit page", ButtonType.OK).showAndWait();
        }
    }


    public void goToDonations() {
        try {
            ScreenControl.removeScreen("patientDonations");
            ScreenControl.addScreen("patientDonations", FXMLLoader.load(getClass().getResource("/scene/patientUpdateDonations.fxml")));
            ScreenControl.activate("patientDonations");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
            new Alert(Alert.AlertType.WARNING, "Error loading donation page", ButtonType.OK).showAndWait();
        }
    }


    public void goToContactDetails() {
        ScreenControl.removeScreen("patientContactDetails");
        try {
            ScreenControl.addScreen("patientContactDetails", FXMLLoader.load(getClass().getResource("/scene/patientUpdateContacts.fxml")));
            ScreenControl.activate("patientContactDetails");
        } catch (IOException e) {
            userActions.log(Level.SEVERE,
                    "Error loading contact details screen",
                    "attempted to navigate from the profile page to the contact details page");
            new Alert(Alert.AlertType.WARNING, "Error loading contact details page", ButtonType.OK).showAndWait();
        }
    }


    public void goToHome() {
        ScreenControl.activate("patientHome");
    }

}
