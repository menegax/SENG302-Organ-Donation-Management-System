package controller;

import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static utility.UserActionHistory.userActions;

import data_access.factories.DAOFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import utility.SystemLogger;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private Set<GlobalEnums.Organ> initialRequirements = new HashSet<>();

    private Set<GlobalEnums.Organ> finalRequirements = new HashSet<>();

    private int totalRemoved;

    /**
     * Initializes the requirements screen by laoding in the current patient
     */
    public void initialize() {
        UserControl userControl = new UserControl();
        Object user = userControl.getLoggedInUser();
        if (user instanceof Patient) {
            loadProfile(((Patient) user).getNhiNumber());
        }
        if (userControl.getTargetUser() != null) {
            loadProfile(((Patient) userControl.getTargetUser()).getNhiNumber());
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
            } else {
                liverCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.KIDNEY)) {
                kidneyCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.KIDNEY);
            }
            else {
                kidneyCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.PANCREAS)) {
                pancreasCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.PANCREAS);
            }
            else {
                pancreasCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.HEART)) {
                heartCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.HEART);
            }
            else {
                heartCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.LUNG)) {
                lungCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.LUNG);
            }
            else {
                lungCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.INTESTINE)) {
                intestineCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.INTESTINE);
            }
            else {
                intestineCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.CORNEA)) {
                corneaCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.CORNEA);
            }
            if (organs.contains(GlobalEnums.Organ.MIDDLEEAR)) {
                middleearCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.MIDDLEEAR);
            }
            else {
                middleearCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.SKIN)) {
                skinCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.SKIN);
            }
            else {
                skinCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.BONE)) {
                boneCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.BONE);
            }
            else {
                boneCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.BONEMARROW)) {
                bonemarrowCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.BONEMARROW);
            }
            else {
                bonemarrowCB.setSelected(false);
            }
            if (organs.contains(GlobalEnums.Organ.CONNECTIVETISSUE)) {
                connectivetissueCB.setSelected(true);
                initialRequirements.add(GlobalEnums.Organ.CONNECTIVETISSUE);
            }
            else {
                connectivetissueCB.setSelected(false);
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
        SystemLogger.systemLogger.log(FINEST, "Patient had organ requirements deregistered. Asking for deregistration reason...");
        List<GlobalEnums.Organ> removedOrgans = new ArrayList<>(target.getRequiredOrgans());
        removedOrgans.removeAll(finalRequirements);
        if (removedOrgans.size() == 0) {
            Action action = new Action(target, after);
            statesHistoryScreen.addAction(action);
        }
        totalRemoved = 0;
        for (GlobalEnums.Organ organ : removedOrgans) {
            totalRemoved += 1;
            openReasonPopup(organ);
            after.removeRequired(organ);
        }
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
            controller.setTarget(after);
            UndoableStage popUpStage = new UndoableStage();
            screenControl.addStage(popUpStage.getUUID(), popUpStage);
            screenControl.show(popUpStage.getUUID(), root);
            popUpStage.setOnHiding(e -> {
                totalRemoved -= 1;
                if (totalRemoved == 0) {
                    if (after.getDeathDate() != null) {
                        after.clearRequiredOrgans();
                    }
                    Action action = new Action(target, after);
                    statesHistoryScreen.addAction(action);
                }
                populateForm(after);
            });
        } catch (IOException e) {
            userActions.log(Level.SEVERE,
                    "Failed to open deregistration of required organ scene from required organs update scene",
                    "attempted to open deregistration of required organ reason window from required organs update scene");
            new Alert(Alert.AlertType.ERROR, "Unable to open deregistration of required organ reason window", ButtonType.OK).show();
        }
    }

}
