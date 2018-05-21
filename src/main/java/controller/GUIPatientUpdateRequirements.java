package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import model.Patient;
import service.OrganWaitlist;
import utility.undoRedo.StatesHistoryScreen;
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * This class is the controller for editing a patients required organs only accessible by the clinician
 */
public class GUIPatientUpdateRequirements {

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

    Database database = Database.getDatabase();

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
        patientRequirementsAnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveRequirements();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Z").match(e)) {
                undo();
            }
            else if (KeyCodeCombination.keyCombination("Ctrl+Y").match(e)) {
                redo();
            }
        });
    }

    /**
     * Load the patients details
     * @param nhi of the current patient being viewed
     */
    private void loadProfile(String nhi) {
        try {
            Patient patient = database.getPatientByNhi(nhi);
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

    /**
     * Loads the checkboxes with a tick if they already require them and the rest unchecked
     * @param patient currently being viewed
     */
    private void populateForm(Patient patient) {
        ArrayList<GlobalEnums.Organ> organs = patient.getRequiredOrgans();
        if (organs != null) {
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
    }

    /**
     * Save button makes sure that the current session is saved with the changes made
     */
    public void saveRequirements() {
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
        createOrganRequests();
        database.saveToDisk();
        goToProfile();
    }

    private void createOrganRequests() {
        OrganWaitlist waitlist = database.getWaitingList();
        Iterator<OrganWaitlist.OrganRequest> iter = waitlist.iterator();
        while(iter.hasNext()) {
            OrganWaitlist.OrganRequest next = iter.next();
            if(next.getReceiverNhi().equals(target.getNhiNumber())) {
                iter.remove();
            }
        }
        for(GlobalEnums.Organ organ : target.getRequiredOrgans()) {
            waitlist.add(target, organ);
        }
    }

    /**
     * Goes back to the profile view
     */
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
                ScreenControl.loadPopUpPane(patientRequirementsAnchorPane.getScene(), fxmlLoader);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen in popup", "attempted to navigate from the requirements page to the profile page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading profile page", ButtonType.OK).show();
            }
        }
    }

}
