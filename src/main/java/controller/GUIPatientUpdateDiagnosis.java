package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Disease;
import model.Patient;
import model.User;
import utility.GlobalEnums;
import utility.StatusObservable;
import utility.undoRedo.Action;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller for diagnosis update popup window.
 */
public class GUIPatientUpdateDiagnosis extends UndoableController{

    @FXML
    private GridPane diagnosisUpdatePane;

    @FXML
    public Button doneButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField diseaseNameTextField;

    @FXML
    public DatePicker diagnosisDate;

    @FXML
    public ChoiceBox tagsDD;

    private UserControl userControl;

    // todo re-engineer to not be static
    /**
     * Diagnosis being updated
     */
    private static Disease target;
    private static Disease targetClone;

    /**
     * Patient who the target diagnosis belongs to
     */
    private static Patient currentPatient;
    private static Patient patientClone;
    public Label titleLabel;

    private static boolean isAdd;

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    /**
     * Sets the diagnosis that is being updated
     * @param disease diagnosis to update
     */
    public static void setDisease(Disease disease) {
        target = disease;
    }

    /**
     * Sets isAdd to true if the operation is a disease addition, and false if the operation is an update
     * @param bool boolean
     */
    public static void setIsAdd(boolean bool) {
        isAdd = bool;
    }

    /**
     * Initializes the popup window for updating a diagnosis.
     * Adds dropdown for disease states.
     * Populates all editable nodes with the current disease information
     */
    public void initialize() {
        userControl = new UserControl();
        currentPatient = (Patient) userControl.getTargetUser();
        patientClone = (Patient) currentPatient.deepClone();
        if(isAdd) {
            targetClone = new Disease(null, null);
        } else {
            if (patientClone.getCurrentDiseases() != null) {
                for (Disease idisease : patientClone.getCurrentDiseases()) {
                    // todo replace with .equals
                    if (idisease.getDiseaseName().equals(target.getDiseaseName()) && idisease.getDateDiagnosed().equals(target.getDateDiagnosed())) {
                        targetClone = idisease;
                    }
                }
            }
            if (patientClone.getPastDiseases() != null) {
                for (Disease idisease : patientClone.getPastDiseases()) {
                    // todo replace with .equals
                    if (idisease.getDiseaseName().equals(target.getDiseaseName()) && idisease.getDateDiagnosed().equals(target.getDateDiagnosed())) {
                        targetClone = idisease;
                    }
                }
            }
        }
        populateDropdown();
        populateForm();
        controls = new ArrayList<Control>() {{
            add(diseaseNameTextField);
            add(diagnosisDate);
            add(tagsDD);
        }};
        //statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEDIAGNOSIS); todo make work
    }

    /**
     * Sets the diagnosis name text field to the current name.
     * Sets the diagnosis date picker to the current diagnosis date.
     * Sets the drop down state menu to the diagnosis' current state.
     */
    private void populateForm() {
        if(targetClone.getDiseaseName() != null) {
            diseaseNameTextField.setText(targetClone.getDiseaseName());
        }

        if(targetClone.getDateDiagnosed() != null) {
            diagnosisDate.setValue(targetClone.getDateDiagnosed());
        }

        if(targetClone.getDiseaseState() != null) {
            tagsDD.setValue(targetClone.getDiseaseState().getValue());
        }
    }

    /**
     * Populates the dropdown menu with all possible disease states
     */
    private void populateDropdown() {
        List<String> tags = new ArrayList<>();
        tags.add("None");
        tags.add(GlobalEnums.DiseaseState.CHRONIC.getValue());
        tags.add(GlobalEnums.DiseaseState.CURED.getValue());
        ObservableList<String> tagsOL = FXCollections.observableList(tags);
        tagsDD.setItems(tagsOL);
    }

    /**
     * Closes the stage when the cancel button is clicked.
     * Gets the source of the click event, gets the window that the source is on and casts the window to a stage
     * before calling the stage close method.
     */
    public void cancelUpdate() {
        screenControl.closeStage(((UndoableStage)cancelButton.getScene().getWindow()).getUUID());
    }

    /***
     * Applies the invalid class to the target control
     * @param target The target to add the class to
     */
    private boolean setInvalid(Control target) {
        target.getStyleClass()
                .add("invalid");
        return false;
    }


    /**
     * Removes the invalid class from the target control if it has it
     *
     * @param target The target to remove the class from
     */
    private void setValid(Control target) {
        if (target.getStyleClass()
                .contains("invalid")) {
            target.getStyleClass()
                    .remove("invalid");
        }
    }

    /**
     * Checks if an added or updated disease is a duplicate of a disease the patient already has
     * @param disease patient's disease
     * @param d added or updated disease
     * @return boolean is duplicate
     */
    private boolean isDuplicate(Disease disease, Disease d) {
        return disease.equals(d);
    }

    /**
     * Checks that the updated fields are valid (name, date and state of diagnosis.
     * Returns true if the update is valid and false otherwise.
     * If the update is valid, the node is reset to whatever is currently shown
     * @return boolean valid updates
     */
    private boolean isValidUpdate() {
        boolean valid = true;
        if(!diseaseNameTextField.getText().matches("[A-Z|a-z0-9.]{3,50}")) {
            valid = false;
            setInvalid(diseaseNameTextField);
        } else {
            setValid(diseaseNameTextField);
            diseaseNameTextField.setText(diseaseNameTextField.getText());
        }
        try {
            if (targetClone.isInvalidDiagnosisDate(diagnosisDate.getValue(), patientClone)) {
                valid = false;
                setInvalid(diagnosisDate);
            } else {
                setValid(diagnosisDate);
                diagnosisDate.setValue(diagnosisDate.getValue());
            }
        } catch(NullPointerException e) {
            valid = false;
            setInvalid(diagnosisDate);
        }

        if(targetClone.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC &&
                tagsDD.getSelectionModel().getSelectedItem().equals("cured")) {
            valid = false;
            setInvalid(tagsDD);
        } else {
            setValid(tagsDD);
            tagsDD.setValue(tagsDD.getSelectionModel().getSelectedItem());
        }

        if(!isValidAdd()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Checks for duplicate diseases to ensure an add is valid
     * @return boolean valid add
     */
    private boolean isValidAdd() {
        Disease d = new Disease(diseaseNameTextField.getText(), null);
        if(tagsDD.getSelectionModel().getSelectedItem() != null) {
            switch (tagsDD.getSelectionModel().getSelectedItem().toString()) {
                case "cured":
                    d.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                    break;
                case "chronic":
                    d.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                    break;
            }
        }
        try {
            d.setDateDiagnosed(diagnosisDate.getValue(), patientClone);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "The diagnosis date is not valid.", "Attempted to add an invalid diagnosis date");
        }
        for (Disease disease : patientClone.getCurrentDiseases()) {
            if(disease != targetClone && isDuplicate(disease, d)) {
                return false;
            }
        }

        for (Disease disease: patientClone.getPastDiseases()) {
            if(disease != targetClone && isDuplicate(disease, d)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Called when the done button is selected. If the update is valid, the diagnosis for the donor is updated
     * (but not saved) to the current updated diagnosis information and the diagnoses view screen is returned to.
     *
     * If the operation is adding a diagnosis, the operation is checked for validity for adding. If the add is
     * valid, the new diagnosis is added to the patient's current diagnoses. Otherwise a message is shown and no new
     * dignosis
     *
     * If the update is invalid, a message is shown with the errors in question explained in the message information.
     * The stage is not closed in this case.
     */
    public void completeUpdate() {
        if(isValidUpdate()) {
            GUIClinicianDiagnosis.setChanged(true);
            targetClone.setDiseaseName(diseaseNameTextField.getText());
            try {
                targetClone.setDateDiagnosed(diagnosisDate.getValue(), currentPatient);
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, "The date is not valid. This should never be called.");
            }
            try {
                switch (tagsDD.getSelectionModel().getSelectedItem().toString()) {
                    case "cured":
                        targetClone.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                        break;
                    case "chronic":
                        targetClone.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                        break;
                    default:
                        targetClone.setDiseaseState(null);
                        break;
                }
            } catch (NullPointerException e) {
                targetClone.setDiseaseState(null);
            }
            if(isAdd) {
                patientClone.getCurrentDiseases().add(targetClone);
            }
            patientClone.sortDiseases();
            Action action = new Action(currentPatient, patientClone);
            for (Stage stage : screenControl.getUsersStages(userControl.getLoggedInUser())) {
                if (stage instanceof UndoableStage) {
                    for (StatesHistoryScreen statesHistoryScreen : ((UndoableStage) stage).getStatesHistoryScreens()) {
                        if (statesHistoryScreen.getUndoableScreen().equals(GlobalEnums.UndoableScreen.CLINICIANDIAGNOSIS)) {
                            statesHistoryScreen.addAction(action);
                        }
                    }
                }
            }

            screenControl.closeStage(((UndoableStage)doneButton.getScene().getWindow()).getUUID());
        } else {
            String errorString = "Diseases must not have the same disease name and diagnosis date as another disease\n\n";
            if(diseaseNameTextField.getStyleClass().contains("invalid")) {
                errorString += "Disease names must be between 3 and 50 characters. " +
                        "Names must be comprised of lowercase letters, uppercase letters, digits or full stops, and be unique in current diseases.\n\n";
            }

            if(diagnosisDate.getStyleClass().contains("invalid")) {
                errorString += "Diagnosis date must be a valid date not set in the future or before the " +
                        "patient was born.\n\n";
            }

            if(tagsDD.getStyleClass().contains("invalid")) {
                errorString += "A chronic disease can not be cured. Please remove the chronic tag and save the diagnosis" +
                        " before marking this disease as cured.\n\n";
            }
            userActions.log(Level.WARNING, errorString, "Attempted to update diagnosis with invalid fields");
        }
    }
}
