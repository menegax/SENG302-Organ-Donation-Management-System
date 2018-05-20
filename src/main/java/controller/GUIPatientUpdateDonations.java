package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCodeCombination;
import model.Patient;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIPatientUpdateDonations {

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


    @FXML
    private void redo() {
        statesHistoryScreen.redo();
    }


    @FXML
    private void undo() {
        statesHistoryScreen.undo();
    }


    private Patient target;

    private StatesHistoryScreen statesHistoryScreen;

    private UserControl userControl;

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
            else if (KeyCodeCombination.keyCombination("Ctrl+Z").match(e)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(e)) {
                redo();
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
        ArrayList<Control> controls = new ArrayList<Control>() {{
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
        statesHistoryScreen = new StatesHistoryScreen(patientDonationsAnchorPane, controls);
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
        }
        else {
            target.removeDonation(GlobalEnums.Organ.LIVER);

        }
        if (kidneyCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.KIDNEY);

        }
        else {
            target.removeDonation(GlobalEnums.Organ.KIDNEY);

        }
        if (pancreasCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.PANCREAS);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.PANCREAS);
        }
        if (heartCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.HEART);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.HEART);
        }
        if (lungCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.LUNG);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.LUNG);
        }
        if (intestineCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.INTESTINE);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.INTESTINE);
        }
        if (corneaCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CORNEA);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.CORNEA);
        }
        if (middleearCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.MIDDLEEAR);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.MIDDLEEAR);
        }
        if (skinCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.SKIN);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.SKIN);
        }
        if (boneCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE);
        }
        if (bonemarrowCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.BONE_MARROW);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.BONE_MARROW);
        }
        if (connectivetissueCB.isSelected()) {
            target.addDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
        }
        else {
            target.removeDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
        }
        Database.saveToDisk();
        goToProfile();
    }


    public void goToProfile() {
        if (userControl.getLoggedInUser() instanceof Patient) {
            ScreenControl.removeScreen("patientProfile");
            try {
                ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
                ScreenControl.activate("patientProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the donation page to the profile page");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
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
