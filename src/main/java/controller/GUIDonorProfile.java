package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Donor;
import model.Medication;
import org.apache.commons.lang3.StringUtils;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static utility.UserActionHistory.userActions;

public class GUIDonorProfile implements IPopupable {

    private UUID id = UUID.randomUUID();

    @FXML
    private AnchorPane donorProfilePane;

    public Button editDonorButton;

    public Button contactButton;

    public Button donationButton;

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
    private ListView<String> organList;

    @FXML
    private ListView<String> medList;

    @FXML
    private Label back;

    private Donor viewedDonor;

    private ListProperty<String> organListProperty = new SimpleListProperty<>();
    private ListProperty<String> medListProperty = new SimpleListProperty<>();


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
        try {
            loadProfile(this.viewedDonor.getNhiNumber());
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Failed to set the viewed donor", "Attempted to set the viewed donor");
        }
    }


    public void initialize() {
        if (ScreenControl.getLoggedInDonor() != null) {
            medicationBtn.setDisable(true);
            medicationBtn.setVisible(false);
            try {
                loadProfile(ScreenControl.getLoggedInDonor()
                        .getNhiNumber());
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Cannot load donor profile");
            }
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
        dateOfDeath.setText(donor.getDeath() == null ? "Not set" : donor.getDeath()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
        if (donor.getZip() != 0) {
            addLbl5.setText(String.valueOf(donor.getZip()));
            while (addLbl5.getText()
                    .length() < 4) {
                addLbl5.setText("0" + addLbl5.getText());
            }
        }
        else {
            addLbl5.setText("Not set");
        }
        //Populate organ listview
        Collection<GlobalEnums.Organ> organs = donor.getDonations();
        List<String> organsMapped = organs.stream().map(e -> StringUtils.capitalize(e.getValue())).collect(Collectors.toList());
        organListProperty.setValue(FXCollections.observableArrayList(organsMapped));
        organList.itemsProperty().bind(organListProperty);
        //Populate current medication listview
        Collection<Medication> meds = donor.getCurrentMedications();
        List<String> medsMapped = meds.stream().map(Medication::getMedicationName).collect(Collectors.toList());
        medListProperty.setValue(FXCollections.observableArrayList(medsMapped));
        medList.itemsProperty().bind(medListProperty);
    }


    public void goToEdit() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorUpdateProfile");
            try {
                ScreenControl.addScreen("donorUpdateProfile", FXMLLoader.load(getClass().getResource("/scene/donorUpdateProfile.fxml")));
                ScreenControl.activate("donorUpdateProfile");
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorUpdateProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorProfilePane.getScene(), fxmlLoader, viewedDonor);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading update screen in popup",
                        "attempted to navigate from the profile page to the edit page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
    }


    public void goToDonations() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorDonations");
            try {
                ScreenControl.addScreen("donorDonations", FXMLLoader.load(getClass().getResource("/scene/donorUpdateDonations.fxml")));
                ScreenControl.activate("donorDonations");
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
                new Alert(Alert.AlertType.ERROR, "Error loading donation page", ButtonType.OK).show();
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorUpdateDonations.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorProfilePane.getScene(), fxmlLoader, viewedDonor);
            }
            catch (Exception e) {
                userActions.log(Level.SEVERE,
                        "Error loading donation screen in popup",
                        "attempted to navigate from the profile page to the donation page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
    }

    public void goToContactDetails() {
        if (ScreenControl.getLoggedInDonor() != null) {
            ScreenControl.removeScreen("donorContactDetails");
            try {
                ScreenControl.addScreen("donorContactDetails", FXMLLoader.load(getClass().getResource("/scene/donorUpdateContacts.fxml")));
                ScreenControl.activate("donorContactDetails");
            } catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading contact details screen",
                        "attempted to navigate from the profile page to the contact details page");
                new Alert(Alert.AlertType.ERROR, "Error loading contact details page", ButtonType.OK).show();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorUpdateContacts.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorProfilePane.getScene(), fxmlLoader, viewedDonor);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading contacts screen in popup",
                        "attempted to navigate from the profile page to the contacts page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading contacts page", ButtonType.OK).show();
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
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/donorMedications.fxml"));
            try {
                ScreenControl.loadPopUpPane(donorProfilePane.getScene(), fxmlLoader, viewedDonor);
            } catch (IOException e) {
                e.printStackTrace();
                userActions.log(Level.SEVERE, "Error loading medication screen in popup", "attempted to navigate from the profile page to the medication page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading medication page", ButtonType.OK).showAndWait();
            }
        }
    }

    public void goToDonorHome() {
        ScreenControl.activate("donorHome");
    }

}
