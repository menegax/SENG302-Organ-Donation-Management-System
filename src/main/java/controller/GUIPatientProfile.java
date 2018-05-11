package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import model.Medication;
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

public class GUIPatientProfile implements IPopupable {

    private UUID id = UUID.randomUUID();

    @FXML
    private AnchorPane patientProfilePane;

    public Button editPatientButton;

    public Button contactButton;

    public Button donationButton;

    @FXML
    public Button medicationBtn;

    @FXML
    private Label nhiLbl;

    @FXML
    private Label nameLbl;

    @FXML
    private Label genderIdentityLbl;

    @FXML
    private Label dobLbl;

    @FXML
    private Label dateOfDeathLabel;

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

    private Patient viewedPatient;

    private ListProperty<String> organListProperty = new SimpleListProperty<>();
    private ListProperty<String> medListProperty = new SimpleListProperty<>();
    
    private void removeBack() {
        back.setDisable(true);
        back.setVisible(false);
    }

    public UUID getId() {
        return id;
    }

    public void setViewedPatient(Patient patient) {
        this.viewedPatient = patient;
        removeBack();
        try {
            loadProfile(this.viewedPatient.getNhiNumber());
        }
        catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Failed to set the viewed patient", "Attempted to set the viewed patient");
        }
    }

    public void initialize() {
        if (ScreenControl.getLoggedInPatient() != null) {
            medicationBtn.setDisable(true);
            medicationBtn.setVisible(false);
            try {
                loadProfile(ScreenControl.getLoggedInPatient()
                        .getNhiNumber());
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Cannot load patient profile");
            }
        }
    }

    private void loadProfile(String nhi) throws InvalidObjectException {
        Patient patient = Database.getPatientByNhi(nhi);

        nhiLbl.setText(patient.getNhiNumber());
        nameLbl.setText(patient.getNameConcatenated());
        genderIdentityLbl.setText(patient.getPreferredGender() == null ? "Not set" : patient.getPreferredGender()
                .getValue());
        dobLbl.setText(patient.getBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateOfDeathLabel.setText(patient.getDeath() == null ? "Not set" : patient.getDeath()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
        if (patient.getZip() != 0) {
            addLbl5.setText(String.valueOf(patient.getZip()));
            while (addLbl5.getText()
                    .length() < 4) {
                addLbl5.setText("0" + addLbl5.getText());
            }
        }
        else {
            addLbl5.setText("Not set");
        }
        //Populate organ listview
        Collection<GlobalEnums.Organ> organs = patient.getDonations();
        List<String> organsMapped = organs.stream().map(e -> StringUtils.capitalize(e.getValue())).collect(Collectors.toList());
        organListProperty.setValue(FXCollections.observableArrayList(organsMapped));
        organList.itemsProperty().bind(organListProperty);
        //Populate current medication listview
        Collection<Medication> meds = patient.getCurrentMedications();
        List<String> medsMapped = meds.stream().map(Medication::getMedicationName).collect(Collectors.toList());
        medListProperty.setValue(FXCollections.observableArrayList(medsMapped));
        medList.itemsProperty().bind(medListProperty);
    }

    public void goToEdit() {
        if (ScreenControl.getLoggedInPatient() != null) {
            ScreenControl.removeScreen("patientUpdateProfile");
            try {
                ScreenControl.addScreen("patientUpdateProfile", FXMLLoader.load(getClass().getResource("/scene/patientUpdateProfile.fxml")));
                ScreenControl.activate("patientUpdateProfile");
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading update screen", "attempted to navigate from the profile page to the edit page");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader, viewedPatient);
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
        if (ScreenControl.getLoggedInPatient() != null) {
            ScreenControl.removeScreen("patientDonations");
            try {
                ScreenControl.addScreen("patientDonations", FXMLLoader.load(getClass().getResource("/scene/patientUpdateDonations.fxml")));
                ScreenControl.activate("patientDonations");
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading donation screen", "attempted to navigate from the profile page to the donation page");
                new Alert(Alert.AlertType.ERROR, "Error loading donation page", ButtonType.OK).show();
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateDonations.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader, viewedPatient);
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
        if (ScreenControl.getLoggedInPatient() != null) {
            ScreenControl.removeScreen("patientContactDetails");
            try {
                ScreenControl.addScreen("patientContactDetails", FXMLLoader.load(getClass().getResource("/scene/patientUpdateContacts.fxml")));
                ScreenControl.activate("patientContactDetails");
            } catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading contact details screen",
                        "attempted to navigate from the profile page to the contact details page");
                new Alert(Alert.AlertType.ERROR, "Error loading contact details page", ButtonType.OK).show();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateContacts.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader, viewedPatient);
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
        if (ScreenControl.getLoggedInPatient() != null) {
            ScreenControl.removeScreen("patientMedications");
            try {
                ScreenControl.addScreen("patientMedications", FXMLLoader.load(getClass().getResource("/scene/patientMedications.fxml")));
                ScreenControl.activate("patientMedications");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading medication screen", "attempted to navigate from the profile page to the medication page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading medication page", ButtonType.OK).showAndWait();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientMedications.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader, viewedPatient);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading medication screen in popup", "attempted to navigate from the profile page to the medication page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading medication page", ButtonType.OK).showAndWait();
            }
        }
    }

    public void goToPatientHome() {
        ScreenControl.activate("patientHome");
    }

}
