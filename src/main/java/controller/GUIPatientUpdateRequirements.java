package controller;

import static java.util.logging.Level.FINEST;
import static utility.UserActionHistory.userActions;

import data_access.factories.DAOFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import model.OrganReceival;
import model.Patient;
import service.ClinicianDataService;
import service.PatientDataService;
import service.interfaces.IClinicianDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.SystemLogger;
import utility.undoRedo.IAction;
import utility.undoRedo.MultiAction;
import utility.undoRedo.SingleAction;
import utility.undoRedo.StatesHistoryScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * This class is the controller for editing a globalPatients required organs only accessible by the clinician
 */
public class GUIPatientUpdateRequirements extends UndoableController implements IWindowObserver {

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
    private GridPane patientRequirementsPane;

    private Patient after;

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Set<GlobalEnums.Organ> initialRequirements = new HashSet<>();

    private Set<GlobalEnums.Organ> finalRequirements = new HashSet<>();

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    private IPatientDataService patientDataService = new PatientDataService();

    private int totalRemoved;

    private Map<GlobalEnums.Organ, CheckBox> controlMap = new HashMap<>();


    /**
     * Initializes the requirements screen by loading in the current patient
     */
    public void loadController() {
        loadProfile(((Patient) target).getNhiNumber());
        // Enter key triggers log in
        patientRequirementsPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveRequirements();
            }
        });
    }


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
     * Load the globalPatients details
     *
     * @param nhi of the current patient being viewed
     */
    public void loadProfile(String nhi) {
        try {
            Patient patient = factory.getPatientDataAccess()
                    .getPatientByNhi(nhi);
            if (patient != null) {
                target = patient;
                after = (Patient) patient.deepClone();
                populateForm(after);
            }
        }
        catch (NullPointerException e) {
            userActions.log(Level.SEVERE,
                    "Error loading logged in user",
                    new String[] { "attempted to manage the donations for logged in user", ((Patient) target).getNhiNumber() });
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
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEREQUIREMENTS, target);

        // disable boxes if dead
        if (((Patient) target).isDead()) {
            disableTickBoxes();
        }
    }


    /**
     * Loads the checkboxes with a tick if they already require them and the rest unchecked
     *
     * @param patient currently being viewed
     */
    private void populateForm(Patient patient) {
        List<GlobalEnums.Organ> organs = new ArrayList<>(patient.getRequiredOrgans()
                .keySet());
        for (GlobalEnums.Organ organ : controlMap.keySet()) {
            if (organs.contains(organ)) {
                controlMap.get(organ)
                        .setSelected(true);
                initialRequirements.add(organ);
            }
            else {
                controlMap.get(organ)
                        .setSelected(false);
            }
        }
    }


    /**
     * Checks if organ is promised or not to a patient already
     * @param patient the patient object
     * @param organ The organ to check
     * @return whether the organ is promised
     */
    public boolean promised(Patient patient, GlobalEnums.Organ organ) {
        boolean promise = false;
        if (patient.getRequiredOrgans()
                .containsKey(organ)) {
            if (patient.getRequiredOrgans()
                    .get(organ)
                    .getDonorNhi() != null) {
                promise = true;
            }
        }
        return promise;
    }


    /**
     * Save button makes sure that the current session is saved with the changes made
     */
    public void saveRequirements() {
        ArrayList<GlobalEnums.Organ> promised = new ArrayList<>();
        finalRequirements.clear();
        Patient donor = null;
        Patient donorAfter = null;
        for (GlobalEnums.Organ organ : controlMap.keySet()) {
            if (controlMap.get(organ)
                    .isSelected()) {
                after.addRequired(organ);
                finalRequirements.add(organ);
            }
            else {
                if (promised(after, organ)) {
                    donor = patientDataService.getPatientByNhi(after.getRequiredOrgans()
                            .get(organ)
                            .getDonorNhi());
                    donorAfter = (Patient) donor.deepClone();
                    donorAfter.getDonations()
                            .put(organ, null);
                    promised.add(organ);
                }
                after.removeRequired(organ);
            }
        }

        deregistrationReason();
        IAction action = null;
        if (promised.size() > 0) {
            String alertMessage = "";
            for (int i = 0; i < promised.size(); i++) {
                if (i == promised.size() - 1) {
                    alertMessage += promised.get(i)
                            .getValue() + ".";
                }
                else {
                    alertMessage += promised.get(i)
                            .getValue() + ", ";
                }
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "The following organs are already promised " + "to this patient: " + alertMessage
                            + " Please undo these changes if this was an error.",
                    ButtonType.OK);
            alert.show();
            if (donor != null) {
                action = new MultiAction((Patient) target, after, donor, donorAfter);
            }
        }
        else {
            action = new SingleAction(target, after);
        }

        if (action != null) {
            statesHistoryScreen.addAction(action);
        }

        IPatientDataService patientDataService = new PatientDataService();
        patientDataService.save(after);
    }


    /**
     * Creates a list of organs removed from the required organs for the patient, and opens a deregistration
     * reason popup for each deregistered organ
     */
    private void deregistrationReason() {
        SystemLogger.systemLogger.log(FINEST, "Patient had organ requirements deregistered. Asking for deregistration reason...");
        Map<GlobalEnums.Organ, OrganReceival> removedOrgans = new HashMap<>(((Patient) target).getRequiredOrgans());
        removedOrgans.keySet()
                .removeAll(finalRequirements);
        totalRemoved = 0;
        for (GlobalEnums.Organ organ : removedOrgans.keySet()) {
            totalRemoved += 1;
            openReasonPopup(organ);
            //after.removeRequired(organ);
        }
    }


    /**
     * Opens the popup to getMedicationsByNhi a reason for organ deregistration
     *
     * @param organ organ being validated for reason of deregistration
     */
    private void openReasonPopup(GlobalEnums.Organ organ) {
        Parent parent = screenControl.getTouchParent(patientRequirementsPane);
        GUIRequiredOrganDeregistrationReason controller =
                (GUIRequiredOrganDeregistrationReason) screenControl.show("/scene/deregistrationReason.fxml", false, this, target, parent);
        controller.setOrgan(organ);
        controller.setTarget(after);
    }


    public void windowClosed() {
        totalRemoved -= 1;
        if (totalRemoved == 0) {
            if (after.getDeathDate() != null) {
                after.clearRequiredOrgans();
                userActions.log(Level.INFO, "ALl required organs cleared due to patient death", "Deregistered an organ");
            }
        }
        populateForm(after);
    }


    private void disableTickBoxes() {
        liverCB.setDisable(true);
        kidneyCB.setDisable(true);
        pancreasCB.setDisable(true);
        heartCB.setDisable(true);
        lungCB.setDisable(true);
        intestineCB.setDisable(true);
        corneaCB.setDisable(true);
        middleearCB.setDisable(true);
        skinCB.setDisable(true);
        boneCB.setDisable(true);
        bonemarrowCB.setDisable(true);
        connectivetissueCB.setDisable(true);
    }

}
