package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.Patient;
import service.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private Label heightLbl;

    @FXML
    private Label weightLbl;

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
        loadProfile(ScreenControl.getLoggedInDonor().getNhiNumber());
    }


    private void loadProfile(String nhi) {
        try {
            Patient patient = Database.getPatientByNhi(nhi);

            nhiLbl.setText(patient.getNhiNumber());
            nameLbl.setText(patient.getNameConcatenated());
            genderLbl.setText(patient.getGender() == null ? "Not set" : patient.getGender()
                    .toString());
            dobLbl.setText(patient.getBirth()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            heightLbl.setText(String.valueOf(patient.getHeight() + " m"));
            weightLbl.setText(String.valueOf(patient.getWeight() + " kg"));
            bloodGroupLbl.setText(patient.getBloodGroup() == null ? "Not set" : patient.getBloodGroup()
                    .getValue());
            addLbl1.setText(patient.getStreet1() == null ? "Not set" : patient.getStreet1());
            addLbl2.setText(patient.getStreet2() == null ? "Not set" : patient.getStreet2());
            addLbl3.setText(patient.getSuburb() == null ? "Not set" : patient.getSuburb());
            addLbl4.setText(patient.getRegion() == null ? "Not set" : patient.getRegion()
                    .getValue());
            addLbl5.setText(String.valueOf(patient.getZip()));
            for (GlobalEnums.Organ organ : patient.getDonations()) {
                donationList.setText(donationList.getText() + organ.getValue() + "\n");
            }
        }
        catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }


    public void goToEdit() {
        ScreenControl.removeScreen("patientProfileUpdate");
        try {
            ScreenControl.addScreen("patientProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/patientProfileUpdate.fxml")));
            ScreenControl.activate("patientProfileUpdate");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading edit page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }


    public void goToDonations() {
        try {
            ScreenControl.removeScreen("patientDonations");
            ScreenControl.addScreen("patientDonations", FXMLLoader.load(getClass().getResource("/scene/patientDonations.fxml")));
            ScreenControl.activate("patientDonations");
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading donation page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }


    public void goToHome() {
        ScreenControl.activate("patientHome");
    }

}
