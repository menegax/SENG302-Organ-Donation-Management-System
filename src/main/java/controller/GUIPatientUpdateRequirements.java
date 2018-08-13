package controller;

import data_access.factories.DAOFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import model.Patient;
import service.ClinicianDataService;
import service.OrganWaitlist;
import service.PatientDataService;
import service.interfaces.IClinicianDataService;
import service.interfaces.IPatientDataService;
import utility.SystemLogger;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.GlobalEnums;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.util.logging.Level;

import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

/**
 * This class is the controller for editing a patients required organs only accessible by the clinician
 */
public class GUIPatientUpdateRequirements extends UndoableController{

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

    private Patient target;

    private Patient after;

    private DAOFactory factory = DAOFactory.getDAOFactory(GlobalEnums.FactoryType.LOCAL);

    private UserControl userControl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Set<GlobalEnums.Organ> initialRequirements = new HashSet<>();

    private Set<GlobalEnums.Organ> finalRequirements = new HashSet<>();

    private int totalRemoved;

    private IClinicianDataService clinicianDataService = new ClinicianDataService();

    /**
     * Initializes the requirements screen by laoding in the current patient
     */
    public void initialize() {
        userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
        }
        if (userControl.getTargetUser() != null) {
            loadProfile(((Patient)userControl.getTargetUser()).getNhiNumber());
        }
        // Enter key triggers log in
        patientRequirementsPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveRequirements();
            }
        });
    }

    /**
     * Load the patients details
     *
     * @param nhi of the current patient being viewed
     */
    public void loadProfile(String nhi) {
        try {
            Patient patient = factory.getPatientDataAccess().getPatientByNhi(nhi);
            if (patient != null) {
                target = patient;
                after = (Patient) patient.deepClone();
                populateForm(after);
            }
        } catch (NullPointerException e) {
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
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEREQUIREMENTS);
    }

    /**
     * Loads the checkboxes with a tick if they already require them and the rest unchecked
     *
     * @param patient currently being viewed
     */
    private void populateForm(Patient patient) {
        List<GlobalEnums.Organ> organs = patient.getRequiredOrgans();
        if (organs != null) {
            if (organs.contains(GlobalEnums.Organ.LIVER)) {
                liverCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.LIVER);
            }
            if (organs.contains(GlobalEnums.Organ.KIDNEY)) {
                kidneyCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.KIDNEY);
            }
            if (organs.contains(GlobalEnums.Organ.PANCREAS)) {
                pancreasCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.PANCREAS);
            }
            if (organs.contains(GlobalEnums.Organ.HEART)) {
                heartCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.HEART);
            }
            if (organs.contains(GlobalEnums.Organ.LUNG)) {
                lungCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.LUNG);
            }
            if (organs.contains(GlobalEnums.Organ.INTESTINE)) {
                intestineCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.INTESTINE);
            }
            if (organs.contains(GlobalEnums.Organ.CORNEA)) {
                corneaCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.CORNEA);
            }
            if (organs.contains(GlobalEnums.Organ.MIDDLEEAR)) {
                middleearCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.MIDDLEEAR);
            }
            if (organs.contains(GlobalEnums.Organ.SKIN)) {
                skinCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.SKIN);
            }
            if (organs.contains(GlobalEnums.Organ.BONE)) {
                boneCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.BONE);
            }
            if (organs.contains(GlobalEnums.Organ.BONEMARROW)) {
                bonemarrowCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.BONEMARROW);
            }
            if (organs.contains(GlobalEnums.Organ.CONNECTIVETISSUE)) {
                connectivetissueCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.CONNECTIVETISSUE);
            }
        }
    }

    /**
     * Save button makes sure that the current session is saved with the changes made
     */
    public void saveRequirements() {
        finalRequirements.clear();
        if (liverCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.LIVER);
            finalRequirements.add(GlobalEnums.Organ.LIVER);
        } else {
            after.removeRequired(GlobalEnums.Organ.LIVER);
        }
        if (kidneyCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.KIDNEY);
            finalRequirements.add(GlobalEnums.Organ.KIDNEY);
        } else {
            after.removeRequired(GlobalEnums.Organ.KIDNEY);
        }
        if (pancreasCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.PANCREAS);
            finalRequirements.add(GlobalEnums.Organ.PANCREAS);
        } else {
            after.removeRequired(GlobalEnums.Organ.PANCREAS);
        }
        if (heartCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.HEART);
            finalRequirements.add(GlobalEnums.Organ.HEART);
        } else {
            after.removeRequired(GlobalEnums.Organ.HEART);
        }
        if (lungCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.LUNG);
            finalRequirements.add(GlobalEnums.Organ.LUNG);
        } else {
            after.removeRequired(GlobalEnums.Organ.LUNG);
        }
        if (intestineCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.INTESTINE);
            finalRequirements.add(GlobalEnums.Organ.INTESTINE);
        } else {
            after.removeRequired(GlobalEnums.Organ.INTESTINE);
        }
        if (corneaCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.CORNEA);
            finalRequirements.add(GlobalEnums.Organ.CORNEA);
        } else {
            after.removeRequired(GlobalEnums.Organ.CORNEA);
        }
        if (middleearCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.MIDDLEEAR);
            finalRequirements.add(GlobalEnums.Organ.MIDDLEEAR);
        } else {
            after.removeRequired(GlobalEnums.Organ.MIDDLEEAR);
        }
        if (skinCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.SKIN);
            finalRequirements.add(GlobalEnums.Organ.SKIN);
        } else {
            after.removeRequired(GlobalEnums.Organ.SKIN);
        }
        if (boneCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.BONE);
            finalRequirements.add(GlobalEnums.Organ.BONE);
        } else {
            after.removeRequired(GlobalEnums.Organ.BONE);
        }
        if (bonemarrowCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.BONEMARROW);
            finalRequirements.add(GlobalEnums.Organ.BONEMARROW);
        } else {
            after.removeRequired(GlobalEnums.Organ.BONEMARROW);
        }
        if (connectivetissueCB.isSelected()) {
            after.addRequired(GlobalEnums.Organ.CONNECTIVETISSUE);
            finalRequirements.add(GlobalEnums.Organ.CONNECTIVETISSUE);
        } else {
            after.removeRequired(GlobalEnums.Organ.CONNECTIVETISSUE);
        }
        deregistrationReason();

        IPatientDataService patientDataService = new PatientDataService();
        patientDataService.save(after);
    }

    /**
     * Creates a list of organs removed from the required organs for the patient, and opens a deregistration
     * reason popup for each deregistered organ
     */
    private void deregistrationReason() {
        SystemLogger.systemLogger.log(INFO, "Patient had organ requirements deregistered. Asking for deregistration reason...");
        List<GlobalEnums.Organ> removedOrgans = target.getRequiredOrgans();
        removedOrgans.removeAll(finalRequirements);
        System.out.println("removed organs: " + removedOrgans); //todo rm

        if (removedOrgans.size() == 0) {
            Action action = new Action(target, after);
            statesHistoryScreen.addAction(action);
        }
        totalRemoved = 0;
        for (GlobalEnums.Organ organ : removedOrgans) {
            totalRemoved += 1;
            openReasonPopup(organ);
//            after.removeRequired(organ);
//            loadProfile(target.getNhiNumber());
        }
        SystemLogger.systemLogger.log(Level.FINE, "Patient after launching deregistration reason:\n" + target);

    }

    public void removeAfterOrgans(Collection<GlobalEnums.Organ> organsToRemoveFromPatient) {
        for (GlobalEnums.Organ organ : organsToRemoveFromPatient) {
            after.removeRequired(organ);
        }
    }

    public void removeAfterOrgansAll() {
        after.getRequiredOrgans().clear();
    }




    /**
     * Opens the popup to getMedicationsByNhi a reason for organ deregistration
     * @param organ organ being validated for reason of deregistration
     */
    private void openReasonPopup(GlobalEnums.Organ organ) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/deregistrationReason.fxml"));
            Parent root = fxmlLoader.load();
            GUIRequiredOrganDeregistrationReason controller = fxmlLoader.getController();
            controller.setOrgan(organ);
            UndoableStage popUpStage = new UndoableStage();
            screenControl.addStage(popUpStage.getUUID(), popUpStage);
            screenControl.show(popUpStage.getUUID(), root);
            popUpStage.setOnHiding(e -> {
                totalRemoved -= 1;
                if (totalRemoved == 0) {
                    Action action = new Action(target, after);
                    statesHistoryScreen.addAction(action);
                }
            });
        } catch (IOException e) {
            userActions.log(Level.SEVERE,
                    "Failed to open deregistration of required organ scene from required organs update scene",
                    "attempted to open deregistration of required organ reason window from required organs update scene");
            new Alert(Alert.AlertType.ERROR, "Unable to open deregistration of required organ reason window", ButtonType.OK).show();
        }
    }

}
