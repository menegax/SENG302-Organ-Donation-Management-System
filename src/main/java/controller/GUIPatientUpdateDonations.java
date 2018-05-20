package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.GridPane;
import model.Patient;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
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
    private GridPane patientDonationsAnchorPane;

    private Patient target;

    private UserControl userControl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

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

    private void loadProfile(String nhi) {
        try {
            Patient patient = Database.getPatientByNhi(nhi);
            target = patient;
            populateForm(patient);
        }
        catch (InvalidObjectException e) {
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


    public void saveDonations() {
        if (liverCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LIVER);
            userActions.log(Level.INFO, "Added liver to patient donations", "Attempted to add donation to a patient");
        }
        else {
            target.removeDonation(GlobalEnums.Organ.LIVER);
            userActions.log(Level.INFO, "Removed liver from patient donations", "Attempted to remove donation from a patient");

        }
        if (kidneyCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.KIDNEY);
            userActions.log(Level.INFO, "Added kidney to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.KIDNEY);
            userActions.log(Level.INFO, "Removed kidney from patient donations", "Attempted to remove donation from a patient");

        }
        if (pancreasCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.PANCREAS);
            userActions.log(Level.INFO, "Added pancreas to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.PANCREAS);
            userActions.log(Level.INFO, "Removed pancreas from patient donations", "Attempted to remove donation from a patient");

        }
        if (heartCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.HEART);
            userActions.log(Level.INFO, "Added heart to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.HEART);
            userActions.log(Level.INFO, "Removed heart from patient donations", "Attempted to remove donation from a patient");

        }
        if (lungCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LUNG);
            userActions.log(Level.INFO, "Added lung to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.LUNG);
            userActions.log(Level.INFO, "Removed lung from patient donations", "Attempted to remove donation from a patient");

        }
        if (intestineCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.INTESTINE);
            userActions.log(Level.INFO, "Added intestine to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.INTESTINE);
            userActions.log(Level.INFO, "Removed intestine from patient donations", "Attempted to remove donation from a patient");

        }
        if (corneaCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CORNEA);
            userActions.log(Level.INFO, "Added cornea to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.CORNEA);
            userActions.log(Level.INFO, "Removed cornea from patient donations", "Attempted to remove donation from a patient");

        }
        if (middleearCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.MIDDLEEAR);
            userActions.log(Level.INFO, "Added middle ear to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.MIDDLEEAR);
            userActions.log(Level.INFO, "Removed middle ear from patient donations", "Attempted to remove donation from a patient");

        }
        if (skinCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.SKIN);
            userActions.log(Level.INFO, "Added skin to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.SKIN);
            userActions.log(Level.INFO, "Removed skin from patient donations", "Attempted to remove donation from a patient");

        }
        if (boneCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE);
            userActions.log(Level.INFO, "Added bone to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE);
            userActions.log(Level.INFO, "Removed bone from patient donations", "Attempted to remove donation from a patient");

        }
        if (bonemarrowCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE_MARROW);
            userActions.log(Level.INFO, "Added bone marrow to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE_MARROW);
            userActions.log(Level.INFO, "Removed bone marrow from patient donations", "Attempted to remove donation from a patient");

        }
        if (connectivetissueCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            userActions.log(Level.INFO, "Added connective tissue to patient donations", "Attempted to add donation to a patient");

        }
        else {
            target.removeDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            userActions.log(Level.INFO, "Removed connective tissue from patient donations", "Attempted to remove donation from a patient");

        }
        Database.saveToDisk();
        goToProfile();
    }


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
