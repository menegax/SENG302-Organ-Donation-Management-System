package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Administrator;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import service.Database;
import model.Clinician;
import model.Medication;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

/**
 * Patient profile page where in patient view, they may view their attributes, donating organs and required organs.
 * In clinician view, they can see the highlighted cell if the donating organ is also required by the patient.
 * This class loads and controls this view.
 */
public class GUIPatientProfile {

    @FXML
    private AnchorPane patientProfilePane;

    public Button editPatientButton;

    public Button contactButton;

    //public Button donationButton;

    @FXML
    public Button medicationBtn;

    @FXML
    public Button proceduresButton;

    @FXML
    public Button donationsButton;

    @FXML
    public Button requirementsButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label nhiLbl;

    @FXML
    private Label nameLbl;

    @FXML
    public Label vitalLbl1;

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
    private Label genderDeclaration;

    @FXML
    private Label genderStatus;

    @FXML
    private ListView receivingList;

    @FXML
    private Label receivingTitle;

    @FXML
    private Label donatingTitle;

    private ListProperty<String> donatingListProperty = new SimpleListProperty<>();

    private ListProperty<String> receivingListProperty = new SimpleListProperty<>();

    /**
     * A list for the organs a patient is donating
     */
    @FXML
    private ListView donationList;

    @FXML
    private ListView<String> medList;

    @FXML
    private Label back;

    private UserControl userControl;

    private ListProperty<String> medListProperty = new SimpleListProperty<>();

    private ScreenControl screenControl = ScreenControl.getScreenControl();



    /**
     * Initialize the controller depending on whether it is a clinician viewing the patient or a patient viewing itself
     * @throws InvalidObjectException throws exception when object is invalid
     */
    public void initialize() throws InvalidObjectException{
        userControl = new UserControl();
        Object user = null;
        if (userControl.getLoggedInUser() instanceof  Patient ) {
            requirementsButton.setDisable(true);
            requirementsButton.setVisible(false);
            medicationBtn.setDisable(true);
            medicationBtn.setVisible(false);
            if (Database.getPatientByNhi(((Patient) userControl.getLoggedInUser()).getNhiNumber()).getRequiredOrgans().size() == 0) {
                receivingList.setDisable(true);
                receivingList.setVisible(false);
                receivingTitle.setDisable(true);
                receivingTitle.setVisible(false);
            } if (Database.getPatientByNhi(((Patient) userControl.getLoggedInUser()).getNhiNumber()).getDonations().size() == 0) {
                donatingTitle.setDisable(true);
                donatingTitle.setVisible(false);
                donationList.setDisable(true);
                donationList.setVisible(false);
            }
            user = userControl.getLoggedInUser();
            proceduresButton.setText("View Procedures"); //Changing the button text for patients
            deleteButton.setVisible( false );
            deleteButton.setDisable( true );
        } else if (userControl.getLoggedInUser() instanceof Clinician) {
            removeBack();
            deleteButton.setVisible( false );
            deleteButton.setDisable( true );
            user = userControl.getTargetUser();
        } else {
            removeBack();
            user = userControl.getTargetUser();
        }
        try {
            assert user != null;
            loadProfile(((Patient)user).getNhiNumber());
        }
        catch (IOException e) {
            userActions.log(Level.SEVERE, "Cannot load patient profile");
        }
    }


    /**
     * Removes the back button from the scene
     */
    private void removeBack() {
        back.setDisable(true);
        back.setVisible(false);
    }

    /**
     * Sets the patient's attributes for the scene's labels
     * @param nhi the nhi of the patient to be viewed
     * @throws InvalidObjectException if the nhi of the patient does not exist in the database
     */
    private void loadProfile(String nhi) throws InvalidObjectException {
        Patient patient = Database.getPatientByNhi(nhi);
        nhiLbl.setText(patient.getNhiNumber());
        nameLbl.setText(patient.getNameConcatenated());
        if (userControl.getLoggedInUser() instanceof Clinician) {
            genderDeclaration.setText( "Gender assigned at birth: " );
            genderStatus.setText( patient.getBirthGender() == null ? "Not set" : patient.getBirthGender().getValue() );
        } else {
            genderDeclaration.setText( "Gender identity: " );
            genderStatus.setText(patient.getPreferredGender() == null ? "Not set" : patient.getPreferredGender()
                    .getValue());
        }
        vitalLbl1.setText(patient.getDeath() == null ? "Alive" : "Deceased");
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
        addLbl1.setText((patient.getStreet1() == null || patient.getStreet1().length() == 0) ? "Not set" : patient.getStreet1());
        addLbl2.setText((patient.getStreet2() == null || patient.getStreet2().length() == 0) ? "Not set" : patient.getStreet2());
        addLbl3.setText((patient.getSuburb() == null || patient.getStreet1().length() == 0) ? "Not set" : patient.getSuburb());
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

        if (patient.getRequiredOrgans() == null) { patient.setRequiredOrgans(new ArrayList<>()); }
        Collection<GlobalEnums.Organ> organsD = patient.getDonations();
        Collection<GlobalEnums.Organ> organsR = patient.getRequiredOrgans();
        List<String> organsMappedD = organsD.stream().map(e -> StringUtils.capitalize(e.getValue())).collect(Collectors.toList());
        List<String> organsMappedR = organsR.stream().map(e -> StringUtils.capitalize(e.getValue())).collect(Collectors.toList());
        donatingListProperty.setValue(FXCollections.observableArrayList(organsMappedD));
        receivingListProperty.setValue(FXCollections.observableArrayList(organsMappedR));
        donationList.itemsProperty().bind(donatingListProperty);
        receivingList.itemsProperty().bind(receivingListProperty);
        //Populate current medication listview
        Collection<Medication> meds = patient.getCurrentMedications();
        List<String> medsMapped = meds.stream().map(Medication::getMedicationName).collect(Collectors.toList());
        medListProperty.setValue(FXCollections.observableArrayList(medsMapped));
        medList.itemsProperty().bind(medListProperty);
//         list view styling/highlighting
        highlightListCell(donationList, true);
        highlightListCell(receivingList, false);
    }

    /**
     * Highlights the listview cell if the organ donating is also required by the patient in clinician view. If in
     * patient view, the listview cells are just styled.
     * @param listView The listView that the cells being highlighted are in
     * @param isDonorList boolean for if the receiving organ is also in the donating list
     */
    public void highlightListCell(ListView<String> listView, boolean isDonorList) {
        listView.setCellFactory(column -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (userControl.getLoggedInUser() instanceof Clinician) {
                    if (isDonorList) {
                        if (receivingListProperty.contains(item)) {
                            this.setStyle("-fx-background-color: #e6b3b3");
                            this.setText(item);
                        } else {
                            this.setStyle("-fx-background-color: WHITE");
                            this.setText(item);
                        }
                    } else {
                        if (donatingListProperty.contains(item)) {
                            this.setStyle("-fx-background-color: #e6b3b3");
                            this.setText(item);
                        } else {
                            this.setStyle("-fx-background-color: WHITE");
                            this.setText(item);
                        }
                    }
                } else {
                        this.setStyle("-fx-background-color: WHITE");
                        this.setText(item);
                }
            }
        });
    }

    /**
     * Takes the user to the edit patient profile scene and controller. The window is opened in the main patient
     * window if a patient is logged in or in the patient popup window if a clinician is logged in
     */
    public void goToEdit() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientProfilePane, "/scene/patientUpdateProfile.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to load update patient profile").show();
                userActions.log(SEVERE, "Failed to load update patient profile", "Attempted to load update patient profile");
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading update screen in popup",
                        "attempted to navigate from the profile page to the edit page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
    }

    /**
     * Goes to the patient donations scene
     */
    public void goToDonations() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientProfilePane,"/scene/patientUpdateDonations.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to load update patient donations").show();
                userActions.log(SEVERE, "Failed to load update patient donations", "Attempted to load update patient donations");
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateDonations.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            }
            catch (Exception e) {
                userActions.log(Level.SEVERE,
                        "Error loading donation screen in popup",
                        "attempted to navigate from the profile page to the donation page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
            }
        }
    }

    /**
     * Takes the user to the edit required organs scene and controller
     */
    public void goToRequirements() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateRequirements.fxml"));
        try {
            ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
        }
        catch (Exception e) {
            userActions.log(Level.SEVERE,
                    "Error loading required organs screen in popup",
                    "attempted to navigate from the profile page to the required organs page in popup");
            new Alert(Alert.AlertType.ERROR, "Error loading edit page", ButtonType.OK).show();
        }
    }

    /**
     * Takes the user to edit the patients contact details
     */
    public void goToContactDetails() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            if (userControl.getLoggedInUser() instanceof Patient) {
                try {
                    screenControl.show(patientProfilePane,"/scene/patientUpdateContacts.fxml");
                } catch (IOException e) {
                    new Alert((Alert.AlertType.ERROR), "Unable to load update patient contacts").show();
                    userActions.log(SEVERE, "Failed to load update patient contacts", "Attempted to load update patient contacts");
                }
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientUpdateContacts.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading contacts screen in popup",
                        "Attempted to navigate from the profile page to the contacts page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading contacts page", ButtonType.OK).show();
            }
        }
    }


    /**
     * Goes to medications edit scene
     */
    public void openMedication() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            if (userControl.getLoggedInUser() instanceof Patient) {
                try {
                    screenControl.show(patientProfilePane,"/scene/patientMedications.fxml");
                } catch (IOException e) {
                    new Alert((Alert.AlertType.ERROR), "Unable to load patient medications").show();
                    userActions.log(SEVERE, "Failed to load patient medications", "Attempted to load patient medications");
                }
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientMedications.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading medication screen in popup", "attempted to navigate from the profile page to the medication page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading medication page", ButtonType.OK).showAndWait();
            }
        }
    }

    /**
     * Navigates to the patient procedures screen
     */
    @FXML
    public void goToPatientProcedures() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientProfilePane, "/scene/patientProcedures.fxml");
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Unable to load patient procedures").show();
                userActions.log(SEVERE, "Failed to load patient procedures", "Attempted to load patient procedures");
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProcedures.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading procedures screen in popup", "attempted to navigate from the profile page to the procedures page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading procedures page", ButtonType.OK).showAndWait();
            }
        }
    }

    /**
     * Goes to the patient home scene
     */
    public void goToPatientHome() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientProfilePane, "/scene/patientHome.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to load patient home").show();
                userActions.log(SEVERE, "Failed to load patient home", "Attempted to load patient home");
            }
        }
    }

    /**
     * Deletes the current profile from the HashSet in Database, not from disk, not until saved
     */
    public void deleteProfile() {
        Patient patient = (Patient) userControl.getTargetUser();
        Database.deletePatient( patient );
        goToAdministratorHome();
    }

    /**
     * Opens the administrator home screen
     */
    private void goToAdministratorHome() {
        try {
            screenControl.show(patientProfilePane, "/scene/administratorHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load administrator home").show();
            userActions.log(SEVERE, "Failed to load administrator home", "Attempted to load administrator home");
        }
    }

    /**
     * Opens the patient's diagnoses screen.
     */
    public void openPatientDiagnoses() {
        if(userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientProfilePane, "/scene/clinicianDiagnosis.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "ERROR loading diagnoses page").show();
                userActions.log(SEVERE, "Error loading diagnoses screen", "attempted to navigate from the profile page to the diagnoses page");
            }
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianDiagnosis.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientProfilePane.getScene(), fxmlLoader);
            }
            catch (IOException e) {
                userActions.log(Level.SEVERE,
                        "Error loading diagnoses screen in popup",
                        "attempted to navigate from the profile page to the diagnoses page in popup");
                new Alert(Alert.AlertType.ERROR, "Error loading diagnoses page", ButtonType.OK).show();
            }
        }

    }
}
