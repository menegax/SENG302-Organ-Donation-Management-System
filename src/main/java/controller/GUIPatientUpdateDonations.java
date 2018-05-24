package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import model.Patient;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

public class GUIPatientUpdateDonations extends UndoableController {

    @FXML
    private CheckBox liverCB;

    @FXML
    private CheckBox kidneyCB;

    @FXML
    private CheckBox pancreasCB;

    @FXML
    private CheckBox heartCB;

    @FXML
    private CheckBox lungCB;

    @FXML
    private CheckBox intestineCB;

    @FXML
    private CheckBox corneaCB;

    @FXML
    private CheckBox middleearCB;

    @FXML
    private CheckBox skinCB;

    @FXML
    private CheckBox boneCB;

    @FXML
    private CheckBox bonemarrowCB;

    @FXML
    private CheckBox connectivetissueCB;

    @FXML
    private AnchorPane patientDonationsAnchorPane;

    private Patient target;

    private UserControl userControl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes the donations screen by loading the profile of the patient logged in or viewed.
     * Sets up enter key press event to save changes
     */
    public void initialize() {
        userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
        }
        if (userControl.getTargetPatient() != null) {
            loadProfile((userControl.getTargetPatient()).getNhiNumber());
        }

        // Enter key triggers log in
        patientDonationsAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveDonations();
            }
        });
    }

    /**
     * Loads the patient's donations
     * @param nhi patient NHI
     */
    private void loadProfile(String nhi) {
        try {
            Patient patient = Database.getPatientByNhi(nhi);
            target = patient;
            populateForm(patient);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "Error loading logged in user", "attempted to manage the donations for logged in user");
        }
        controls = new ArrayList<Control>() {{
            add(liverCB);
            add(kidneyCB);
            add(pancreasCB);
            add(heartCB);
            add(lungCB);
            add(intestineCB);
            add(corneaCB);
            add(middleearCB);
            add(skinCB);
            add(boneCB);
            add(bonemarrowCB);
            add(connectivetissueCB);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEDONATIONS);
    }


    /**
     * Populates the donation checkboxes with the patient's donations
     * @param patient patient with viewed donation
     */
    private void populateForm(Patient patient) {
        ArrayList<GlobalEnums.Organ> organs = patient.getDonations();
        if (organs.contains(GlobalEnums.Organ.LIVER)) {
            liverCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.KIDNEY)) {
            kidneyCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.PANCREAS)) {
            pancreasCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.HEART)) {
            heartCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.LUNG)) {
            lungCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.INTESTINE)) {
            intestineCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.CORNEA)) {
            corneaCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.MIDDLEEAR)) {
            middleearCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.SKIN)) {
            skinCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.BONE)) {
            boneCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.BONE_MARROW)) {
            bonemarrowCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.CONNECTIVETISSUE)) {
            connectivetissueCB.setSelected(true);
        }
    }

    /**
     * Saves selected donations to the patient's profile, and saves the patient to the database
     */
    public void saveDonations() {

        ArrayList<String> newDonations = new ArrayList<>();

        if (liverCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LIVER);
            newDonations.add(GlobalEnums.Organ.LIVER.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.LIVER);
        }
        if (kidneyCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.KIDNEY);
            newDonations.add(GlobalEnums.Organ.KIDNEY.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.KIDNEY);
        }
        if (pancreasCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.PANCREAS);
            newDonations.add(GlobalEnums.Organ.PANCREAS.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.PANCREAS);

        }
        if (heartCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.HEART);
            newDonations.add(GlobalEnums.Organ.HEART.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.HEART);
        }
        if (lungCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LUNG);
            newDonations.add(GlobalEnums.Organ.LUNG.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.LUNG);

        }
        if (intestineCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.INTESTINE);
            newDonations.add(GlobalEnums.Organ.INTESTINE.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.INTESTINE);

        }
        if (corneaCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CORNEA);
            newDonations.add(GlobalEnums.Organ.CORNEA.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.CORNEA);

        }
        if (middleearCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.MIDDLEEAR);
            newDonations.add(GlobalEnums.Organ.MIDDLEEAR.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.MIDDLEEAR);

        }
        if (skinCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.SKIN);
            newDonations.add(GlobalEnums.Organ.SKIN.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.SKIN);

        }
        if (boneCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE);
            newDonations.add(GlobalEnums.Organ.BONE.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.BONE);

        }
        if (bonemarrowCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE_MARROW);
            newDonations.add(GlobalEnums.Organ.BONE_MARROW.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.BONE_MARROW);

        }
        if (connectivetissueCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            newDonations.add(GlobalEnums.Organ.CONNECTIVETISSUE.toString());
        } else {
            target.removeDonation(GlobalEnums.Organ.CONNECTIVETISSUE);

        }
        userActions.log(INFO, "Updated user donations to: " + newDonations, "Attempted to update donations");
        Database.saveToDisk();
        goToProfile();
    }


    /**
     * Navigates to the patient profile screen
     */
    public void goToProfile() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            try {
                screenControl.show(patientDonationsAnchorPane, "/scene/patientProfile.fxml");
            } catch (IOException e) {
                new Alert((Alert.AlertType.ERROR), "Unable to patient profile").show();
                userActions.log(SEVERE, "Failed to load patient profile", "Attempted to load patient profile");
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
            try {
                ScreenControl.loadPopUpPane(patientDonationsAnchorPane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen in popup", "attempted to navigate from the donation page to the profile page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        }
    }
}
