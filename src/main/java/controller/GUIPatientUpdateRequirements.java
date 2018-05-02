package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import model.Patient;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIPatientUpdateRequirements implements IPopupable {

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
    private AnchorPane patientRequirementsAnchorPane;

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


    public void initialize() {
        if (ScreenControl.getLoggedInPatient() != null) {
            loadProfile(ScreenControl.getLoggedInPatient()
                    .getNhiNumber());
        }

        // Enter key triggers log in
        patientRequirementsAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveDonations();
            }
        });
    }

    public void setViewedPatient(Patient patient) {
        target = patient;
        loadProfile(patient.getNhiNumber());
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
        statesHistoryScreen = new StatesHistoryScreen(patientRequirementsAnchorPane, controls);
    }



    private void populateForm(Patient patient) {
        ArrayList<GlobalEnums.Organ> organs = patient.getRequiredOrgans();
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
            target.addRequired(GlobalEnums.Organ.LIVER);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.LIVER);
        }
        if (kidneyCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.KIDNEY);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.KIDNEY);
        }
        if (pancreasCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.PANCREAS);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.PANCREAS);
        }
        if (heartCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.HEART);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.HEART);
        }
        if (lungCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.LUNG);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.LUNG);
        }
        if (intestineCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.INTESTINE);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.INTESTINE);
        }
        if (corneaCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.CORNEA);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.CORNEA);
        }
        if (middleearCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.MIDDLEEAR);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.MIDDLEEAR);
        }
        if (skinCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.SKIN);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.SKIN);
        }
        if (boneCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.BONE);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.BONE);
        }
        if (bonemarrowCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.BONE_MARROW);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.BONE_MARROW);
        }
        if (connectivetissueCB.isSelected()) {
            target.addRequired(GlobalEnums.Organ.CONNECTIVETISSUE);
        }
        else {
            target.removeRequired(GlobalEnums.Organ.CONNECTIVETISSUE);
        }
        Database.saveToDisk();
        goToProfile();
    }


    public void goToProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianViewOfPatientProfile.fxml"));
        try {
            ScreenControl.loadPopUpPane(patientRequirementsAnchorPane.getScene(), fxmlLoader, target);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen in popup", "attempted to navigate from the donation page to the profile page in popup");
            new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
        }
    }
}
