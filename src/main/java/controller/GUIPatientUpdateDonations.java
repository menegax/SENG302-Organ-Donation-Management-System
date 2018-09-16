package controller;

import data_access.factories.DAOFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import model.Patient;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.GlobalEnums;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

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
    private GridPane patientDonationsAnchorPane;

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);

    /**
     * Initializes the donations screen by loading the profile of the patient logged in or viewed.
     * Sets up enter key press event to save changes
     */
    public void load() {
        loadProfile(((Patient) target).getNhiNumber());

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
        Patient patient = factory.getPatientDataAccess().getPatientByNhi(nhi);
        if (patient != null) {
            target = patient;
            populateForm(patient);
        } else {
            userActions.log(Level.SEVERE, "Error loading logged in user", new String[]{"attempted to manage the donations for logged in user", ((Patient) target).getNhiNumber()});
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
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEDONATIONS, target);
    }


    /**
     * Populates the donation checkboxes with the patient's donations
     * @param patient patient with viewed donation
     */
    private void populateForm(Patient patient) {
        Set<GlobalEnums.Organ> organs = patient.getDonations().keySet();
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
        if (organs.contains(GlobalEnums.Organ.BONEMARROW)) {
            bonemarrowCB.setSelected(true);
        }
        if (organs.contains(GlobalEnums.Organ.CONNECTIVETISSUE)) {
            connectivetissueCB.setSelected(true);
        }
    }

    /**
     * Checks if organ is promised or not to a patient already
     */
    public boolean promised(Patient patient, GlobalEnums.Organ organ) {
        boolean promise = false;
        if (patient.getDonations().get(organ) != null) {
            promise = true;
        }
        return promise;
    }

    /**
     * Saves selected donations to the patient's profile, and saves the patient to the database
     */
    public void saveDonations() {

        ArrayList<String> newDonations = new ArrayList<>();
        ArrayList<GlobalEnums.Organ> promised = new ArrayList<>();

        Patient after = (Patient) target.deepClone();

        if (liverCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.LIVER);
            newDonations.add(GlobalEnums.Organ.LIVER.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.LIVER)){
                promised.add(GlobalEnums.Organ.LIVER);
            }
            after.removeDonation(GlobalEnums.Organ.LIVER);
        }
        if (kidneyCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.KIDNEY);
            newDonations.add(GlobalEnums.Organ.KIDNEY.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.KIDNEY)){
                promised.add(GlobalEnums.Organ.KIDNEY);
            }
            after.removeDonation(GlobalEnums.Organ.KIDNEY);
        }
        if (pancreasCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.PANCREAS);
            newDonations.add(GlobalEnums.Organ.PANCREAS.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.PANCREAS)){
                promised.add(GlobalEnums.Organ.PANCREAS);
            }
            after.removeDonation(GlobalEnums.Organ.PANCREAS);

        }
        if (heartCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.HEART);
            newDonations.add(GlobalEnums.Organ.HEART.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.HEART)){
                promised.add(GlobalEnums.Organ.HEART);
            }
            after.removeDonation(GlobalEnums.Organ.HEART);
        }
        if (lungCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.LUNG);
            newDonations.add(GlobalEnums.Organ.LUNG.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.LUNG)){
                promised.add(GlobalEnums.Organ.LUNG);
            }
            after.removeDonation(GlobalEnums.Organ.LUNG);

        }
        if (intestineCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.INTESTINE);
            newDonations.add(GlobalEnums.Organ.INTESTINE.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.INTESTINE)){
                promised.add(GlobalEnums.Organ.INTESTINE);
            }
            after.removeDonation(GlobalEnums.Organ.INTESTINE);

        }
        if (corneaCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.CORNEA);
            newDonations.add(GlobalEnums.Organ.CORNEA.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.CORNEA)){
                promised.add(GlobalEnums.Organ.CORNEA);
            }
            after.removeDonation(GlobalEnums.Organ.CORNEA);

        }
        if (middleearCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.MIDDLEEAR);
            newDonations.add(GlobalEnums.Organ.MIDDLEEAR.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.MIDDLEEAR)){
                promised.add(GlobalEnums.Organ.MIDDLEEAR);
            }
            after.removeDonation(GlobalEnums.Organ.MIDDLEEAR);

        }
        if (skinCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.SKIN);
            newDonations.add(GlobalEnums.Organ.SKIN.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.SKIN)){
                promised.add(GlobalEnums.Organ.SKIN);
            }
            after.removeDonation(GlobalEnums.Organ.SKIN);

        }
        if (boneCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.BONE);
            newDonations.add(GlobalEnums.Organ.BONE.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.BONE)){
                promised.add(GlobalEnums.Organ.BONE);
            }
            after.removeDonation(GlobalEnums.Organ.BONE);

        }
        if (bonemarrowCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.BONEMARROW);
            newDonations.add(GlobalEnums.Organ.BONEMARROW.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.BONEMARROW)){
                promised.add(GlobalEnums.Organ.BONEMARROW);
            }
            after.removeDonation(GlobalEnums.Organ.BONEMARROW);

        }
        if (connectivetissueCB.isSelected()) {
            after.addDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
            newDonations.add(GlobalEnums.Organ.CONNECTIVETISSUE.toString());
        } else {
            if (promised(after, GlobalEnums.Organ.CONNECTIVETISSUE)){
                promised.add(GlobalEnums.Organ.CONNECTIVETISSUE);
            }
            after.removeDonation(GlobalEnums.Organ.CONNECTIVETISSUE);
        }

        if (promised.size() > 0) {
            String alertMessage = "";
            for (int i = 0; i <= promised.size(); i++) {
                if (i == promised.size() - 1) {
                    alertMessage += promised.get(i).getValue() + ".";
                } else {
                    alertMessage += promised.get(i).getValue() + ", ";
                }
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The following organs are already promised " +
                    "to other patients: " + alertMessage + "Please undo these changes if this was an error.", ButtonType.OK);
            alert.show();
        }

        Action action = new Action(target, after);
        statesHistoryScreen.addAction(action);

        userActions.log(INFO, "Updated user donations to: " + newDonations, new String[]{"Attempted to update donations", ((Patient) target).getNhiNumber()});
    }
}
