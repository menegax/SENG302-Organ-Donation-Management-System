package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorProfile implements IPopupable {

    private UUID id = UUID.randomUUID();

    @FXML
    private AnchorPane profilePane;

    @FXML
    public Button medicationBtn;

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

    @FXML
    private Label back;

    private Donor viewedDonor;

    private void removeBack() {
        back.setDisable(true);
        back.setVisible(false);
    }

    public UUID getId() {
        return id;
    }

    public void setViewedDonor(Donor donor) {
        this.viewedDonor = donor;
        removeBack();
        loadProfile(this.viewedDonor.getNhiNumber());
    }

    public void initialize() {
        if (ScreenControl.getLoggedInDonor() != null) {
            medicationBtn.setDisable(true);
            medicationBtn.setVisible(false);
            loadProfile(ScreenControl.getLoggedInDonor()
                    .getNhiNumber());
        }
    }


    private void loadProfile(String nhi) {
        try { // todo remove this
            Donor donor = Database.getDonorByNhi(nhi);

            nhiLbl.setText(donor.getNhiNumber());
            nameLbl.setText(donor.getNameConcatenated());
            genderLbl.setText(donor.getGender() == null ? "Not set" : donor.getGender()
                    .toString());
            dobLbl.setText(donor.getBirth()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            heightLbl.setText(String.valueOf(donor.getHeight() + " m"));
            weightLbl.setText(String.valueOf(donor.getWeight() + " kg"));
            bloodGroupLbl.setText(donor.getBloodGroup() == null ? "Not set" : donor.getBloodGroup()
                    .getValue());
            addLbl1.setText(donor.getStreet1() == null ? "Not set" : donor.getStreet1());
            addLbl2.setText(donor.getStreet2() == null ? "Not set" : donor.getStreet2());
            addLbl3.setText(donor.getSuburb() == null ? "Not set" : donor.getSuburb());
            addLbl4.setText(donor.getRegion() == null ? "Not set" : donor.getRegion()
                    .getValue());
            addLbl5.setText(String.valueOf(donor.getZip()));
            for (GlobalEnums.Organ organ : donor.getDonations()) {
                donationList.setText(donationList.getText() + organ.getValue() + "\n");
            }
        } catch (InvalidObjectException e) {
            e.printStackTrace(); // todo remove
        }
    }


    public void goToEdit() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorProfileUpdate");
            try {
                ScreenControl.addScreen("donorProfileUpdate", FXMLLoader.load(getClass().getResource("/scene/donorProfileUpdate.fxml")));
                ScreenControl.activate("donorProfileUpdate");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).showAndWait();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorProfileUpdate.fxml"));
            try {
                ScreenControl.loadPopUpPane(profilePane.getScene(), fxmlLoader, viewedDonor);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading update screen in popup", "attempted to navigate from the profile page to the edit page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).showAndWait();
            }
        }
    }


    public void goToDonations() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorDonations");
            try {
                ScreenControl.addScreen("donorDonations", FXMLLoader.load(getClass().getResource("/scene/donorDonations.fxml")));
                ScreenControl.activate("donorDonations");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
                new Alert(Alert.AlertType.ERROR, "Error loading donation page", ButtonType.OK).showAndWait();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorDonations.fxml"));
            try {
                ScreenControl.loadPopUpPane(profilePane.getScene(), fxmlLoader, viewedDonor);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading donation screen in popup", "attempted to navigate from the profile page to the donation page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).showAndWait();
            }
        }
    }

    public void openMedication() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorMedications");
            try {
                ScreenControl.addScreen("donorMedications", FXMLLoader.load(getClass().getResource("/scene/donorMedications.fxml")));
                ScreenControl.activate("donorMedications");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading medication screen", "attempted to navigate from the profile page to the medication page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading medication page", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorMedications.fxml"));
            try {
                ScreenControl.loadPopUpPane(profilePane.getScene(), fxmlLoader, viewedDonor);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading medication screen in popup", "attempted to navigate from the profile page to the medication page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading medication page", ButtonType.OK).showAndWait();
            }
        }
    }

    public void goToDonorHome() {
        ScreenControl.activate("donorHome");
    }

}
