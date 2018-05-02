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

public class GUIPatientProfile1 {

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

    /**
     * A list for the organs a patient is donating
     */
    @FXML
    private Label donatingList;

    /**
     * A list for the organs a patient is receiving
     */
    @FXML
    private Label receivingList;

    /**
     * Initializes the patient profile GUI pane
     */
    public void initialize() {
        loadProfile(ScreenControl.getLoggedInPatient().getNhiNumber());
    }

    /**
     * Loads all of the profile attribute data for a specified patient by NHI number
     * @param nhi The NHI number of the donor profile attribute data is being loaded
     */
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

            for (GlobalEnums.Organ donatingOrgan : patient.getDonations()) {
                donatingList.setText(donatingList.getText() + donatingOrgan.getValue() + "\n");
            }

            for (GlobalEnums.Organ receivingOrgan : patient.getRequiredOrgans()) {
                receivingList.setText(receivingList.getText() + receivingOrgan.getValue() + "\n");
            }
        }
        catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates a user to patient profile edit GUI pane when the 'edit' button is activated
     */
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

    /**
     * Navigates a user to the donations GUI pane when the 'Manage donations' button is activated
     */
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

    /**
     * Navigates a user to the home GUI pane when the '〱back' button is activated
     */
    public void goToHome() {
        ScreenControl.activate("patientHome");
    }

}
