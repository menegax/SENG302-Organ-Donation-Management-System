package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Donor;
import org.apache.commons.lang3.StringUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorProfile {

    public Button editDonorButton;

    public Button contactButton;

    public Button donationButton;

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
            loadProfile(ScreenControl.getLoggedInDonor()
                    .getNhiNumber());
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Unable to load donor from database", "Attempted to load donor profile");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load donor from database");
            alert.showAndWait();
        }
    }


    private void loadProfile(String nhi) throws InvalidObjectException {
        Donor donor = Database.getDonorByNhi(nhi);

        nhiLbl.setText(donor.getNhiNumber());
        nameLbl.setText(donor.getNameConcatenated());
        genderLbl.setText(donor.getGender() == null ? "Not set" : donor.getGender()
                .toString());
        dobLbl.setText(donor.getBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateOfDeath.setText(donor.getDeath() == null ? "Not set" : donor.getDeath().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        age.setText(String.valueOf(donor.getAge()));
        heightLbl.setText(String.valueOf(donor.getHeight() + " m"));
        weightLbl.setText(String.valueOf(donor.getWeight() + " kg"));
        bmi.setText(String.valueOf(donor.getBmi()));
        bloodGroupLbl.setText(donor.getBloodGroup() == null ? "Not set" : donor.getBloodGroup()
                .getValue());
        addLbl1.setText(donor.getStreet1() == null ? "Not set" : donor.getStreet1());
        addLbl2.setText(donor.getStreet2() == null ? "Not set" : donor.getStreet2());
        addLbl3.setText(donor.getSuburb() == null ? "Not set" : donor.getSuburb());
        addLbl4.setText(donor.getRegion() == null ? "Not set" : donor.getRegion()
                .getValue());
        addLbl5.setText(String.valueOf(donor.getZip()));
        for (GlobalEnums.Organ organ : donor.getDonations()) {
            donationList.setText(donationList.getText() + StringUtils.capitalize(organ.getValue()) + "\n");
        }
    }


    public void goToEdit() {
        ScreenControl.removeScreen("donorProfileUpdate");
        try {
            ScreenControl.addScreen("donorProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/donorUpdateProfile.fxml")));
            ScreenControl.activate("donorProfileUpdate");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
            new Alert(Alert.AlertType.WARNING, "Error loading edit page", ButtonType.OK).showAndWait();
        }
    }


    public void goToDonations() {
        ScreenControl.removeScreen("donorDonations");
        try {
            ScreenControl.addScreen("donorDonations", FXMLLoader.load(getClass().getResource("/scene/donorUpdateDonations.fxml")));
            ScreenControl.activate("donorDonations");
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
            new Alert(Alert.AlertType.WARNING, "Error loading donation page", ButtonType.OK).showAndWait();
        }
    }


    public void goToContactDetails() {
        ScreenControl.removeScreen("donorContactDetails");
        try {
            ScreenControl.addScreen("donorContactDetails", FXMLLoader.load(getClass().getResource("/scene/donorUpdateContacts.fxml")));
            ScreenControl.activate("donorContactDetails");
        } catch (IOException e) {
            userActions.log(Level.SEVERE,
                    "Error loading contact details screen",
                    "attempted to navigate from the profile page to the contact details page");
            new Alert(Alert.AlertType.WARNING, "Error loading contact details page", ButtonType.OK).showAndWait();
        }
    }


    public void goToHome() {
        ScreenControl.activate("home");
    }

}
