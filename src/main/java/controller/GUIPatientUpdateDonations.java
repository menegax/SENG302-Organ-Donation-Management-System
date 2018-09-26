package controller;

import data_access.factories.DAOFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import model.Patient;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.IAction;
import utility.undoRedo.MultiAction;
import utility.undoRedo.SingleAction;
import utility.undoRedo.StatesHistoryScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    private Map<GlobalEnums.Organ, CheckBox> controlMap = new HashMap<>();

    private IPatientDataService patientDataService = new PatientDataService();

    @FXML
    public void initialize() {
        controlMap.put(GlobalEnums.Organ.LIVER, liverCB);
        controlMap.put(GlobalEnums.Organ.KIDNEY, kidneyCB);
        controlMap.put(GlobalEnums.Organ.PANCREAS, pancreasCB);
        controlMap.put(GlobalEnums.Organ.HEART, heartCB);
        controlMap.put(GlobalEnums.Organ.LUNG, lungCB);
        controlMap.put(GlobalEnums.Organ.INTESTINE, intestineCB);
        controlMap.put(GlobalEnums.Organ.CORNEA, corneaCB);
        controlMap.put(GlobalEnums.Organ.MIDDLEEAR, middleearCB);
        controlMap.put(GlobalEnums.Organ.SKIN, skinCB);
        controlMap.put(GlobalEnums.Organ.BONE, boneCB);
        controlMap.put(GlobalEnums.Organ.BONEMARROW, bonemarrowCB);
        controlMap.put(GlobalEnums.Organ.CONNECTIVETISSUE, connectivetissueCB);
    }

    /**
     * Initializes the donations screen by loading the profile of the patient logged in or viewed.
     * Sets up enter key press event to save changes
     */
    public void loadController() {
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
        for (GlobalEnums.Organ organ : organs) {
            controlMap.get(organ).setSelected(true);
        }
    }

    /**
     * Checks if organ is promised or not to a patient already
     * @param patient the patient object
     * @param organ the organ to check
     * @return whether the organ is promised
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
        Patient receiver = null;
        Patient receiverAfter = null;
        for (GlobalEnums.Organ organ : controlMap.keySet()) {
            if (controlMap.get(organ).isSelected()) {
                after.addDonation(organ);
                newDonations.add(organ.toString());
            } else {
                if (promised(after, organ)) {
                    receiver = patientDataService.getPatientByNhi(after.getDonations().get(organ));
                    receiverAfter = (Patient) receiver.deepClone();
                    receiverAfter.getRequiredOrgans().get(organ).setDonorNhi(null);
                    promised.add(organ);
                }
                after.removeDonation(organ);
            }
        }
        IAction action = null;
        if (promised.size() > 0) {
            String alertMessage = "";
            for (int i = 0; i < promised.size(); i++) {
                if (i == promised.size() - 1) {
                    alertMessage += promised.get(i).getValue() + ".";
                } else {
                    alertMessage += promised.get(i).getValue() + ", ";
                }
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The following organs are already promised " +
                    "to other patients: " + alertMessage + "Please undo these changes if this was an error.", ButtonType.OK);
            alert.show();
            if (receiver != null) {
                action = new MultiAction((Patient) target, after, receiver, receiverAfter);
            }
        } else {
            action = new SingleAction(target, after);

        }
        if (action != null) {
            statesHistoryScreen.addAction(action);
        }

        userActions.log(INFO, "Updated user donations to: " + newDonations, new String[]{"Attempted to update donations", ((Patient) target).getNhiNumber()});
    }
}
