package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Disease;
import model.Patient;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.MultiTouchHandler;
import utility.TouchDatePickerSkin;
import utility.undoRedo.IAction;
import utility.undoRedo.SingleAction;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static utility.GlobalEnums.UndoableScreen.CLINICIANDIAGNOSIS;
import static utility.UserActionHistory.userActions;

/**
 * Controller for diagnosis update popup window.
 */
public class GUIPatientUpdateDiagnosis extends TargetedController {

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
    private ChoiceBox tagsDD;

    /**
     * Diagnosis being updated
     */
    private Disease targetDisease;
    private Disease targetDiseaseClone;

    /**
     * Patient who the targetDisease diagnosis belongs to
     */
    private Patient patientClone;
    public Label titleLabel;

    private boolean isAdd;

    private ScreenControl screenControl = ScreenControl.getScreenControl();
    private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();

    private IPatientDataService patientDataService = new PatientDataService();

    private MultiTouchHandler touchHandler;

    /**
     * Sets the diagnosis that is being updated
     * @param disease diagnosis to update
     */
    public void setDisease(Disease disease) {
        targetDisease = disease;
    }

    /**
     * Sets isAdd to true if the operation is a disease addition, and false if the operation is an update
     * @param bool boolean
     */
    public void setIsAdd(boolean bool) {
        isAdd = bool;
    }

    /**
     * Initializes the popup window for updating a diagnosis.
     * Adds dropdown for disease states.
     * Populates all editable nodes with the current disease information
     */
    public void loadController() {
        patientClone = (Patient) target.deepClone();
        if(isAdd) {
            targetDiseaseClone = new Disease(null, null);
        } else {
            if (patientClone.getCurrentDiseases() != null) {
                for (Disease idisease : patientClone.getCurrentDiseases()) {
                    if (idisease.equals(targetDisease)) {
                        targetDiseaseClone = idisease;
                    }
                }
            }
            if (patientClone.getPastDiseases() != null) {
                for (Disease idisease : patientClone.getPastDiseases()) {
                    if (idisease.equals(targetDisease)) {
                        targetDiseaseClone = idisease;
                    }
                }
            }
        }
        populateDropdown();
        populateForm();
        if(screenControl.isTouch()) {
            touchHandler = new MultiTouchHandler();
            touchHandler.initialiseHandler(diagnosisUpdatePane);
        }
        TouchDatePickerSkin dateOfDiagnosisSkin = new TouchDatePickerSkin(diagnosisDate, diagnosisUpdatePane);
        diagnosisDate.setSkin(dateOfDiagnosisSkin);
    }

    /**
     * Sets the diagnosis name text field to the current name.
     * Sets the diagnosis date picker to the current diagnosis date.
     * Sets the drop down state menu to the diagnosis' current state.
     */
    private void populateForm() {
        if(targetDiseaseClone.getDiseaseName() != null) {
            diseaseNameTextField.setText(targetDiseaseClone.getDiseaseName());
        }

        if(targetDiseaseClone.getDateDiagnosed() != null) {
            diagnosisDate.setValue(targetDiseaseClone.getDateDiagnosed());
        }

        if(targetDiseaseClone.getDiseaseState() != null) {
            tagsDD.setValue(targetDiseaseClone.getDiseaseState().getValue());
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
        screenControl.closeWindow(diagnosisUpdatePane);
    }

    /***
     * Applies the invalid class to the targetDisease control
     * @param targetDisease The targetDisease to add the class to
     */
    private boolean setInvalid(Control targetDisease) {
        targetDisease.getStyleClass().add("invalid");
        return false;
    }


    /**
     * Removes the invalid class from the targetDisease control if it has it
     *
     * @param targetDisease The targetDisease to remove the class from
     */
    private void setValid(Control targetDisease) {
        if (targetDisease.getStyleClass().contains("invalid")) {
            targetDisease.getStyleClass().remove("invalid");
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
        if(!Pattern.matches(GlobalEnums.UIRegex.DISEASENAME.getValue(), diseaseNameTextField.getText())) {
            valid = false;
            setInvalid(diseaseNameTextField);
        } else {
            setValid(diseaseNameTextField);
            diseaseNameTextField.setText(diseaseNameTextField.getText());
        }
        try {
            if (targetDiseaseClone.isInvalidDiagnosisDate(diagnosisDate.getValue(), patientClone.getBirth())) {
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

        if(targetDiseaseClone.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC &&
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
            d.setDateDiagnosed(diagnosisDate.getValue(), patientClone.getBirth());
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "The diagnosis date is not valid.", new String[]{"Attempted to add an invalid diagnosis date", ((Patient) target).getNhiNumber()});
        }
        for (Disease disease : patientClone.getCurrentDiseases()) {
            if(disease != targetDiseaseClone && isDuplicate(disease, d)) {
                return false;
            }
        }

        for (Disease disease: patientClone.getPastDiseases()) {
            if(disease != targetDiseaseClone && isDuplicate(disease, d)) {
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
            targetDiseaseClone.setDiseaseName(diseaseNameTextField.getText());
            try {
                targetDiseaseClone.setDateDiagnosed(diagnosisDate.getValue(), ((Patient) target).getBirth());
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, "The date is not valid. This should never be called.");
            }
            try {
                switch (tagsDD.getSelectionModel().getSelectedItem().toString()) {
                    case "cured":
                        targetDiseaseClone.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                        break;
                    case "chronic":
                        targetDiseaseClone.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                        break;
                    default:
                        targetDiseaseClone.setDiseaseState(null);
                        break;
                }
            } catch (NullPointerException e) {
                targetDiseaseClone.setDiseaseState(null);
            }
            if(isAdd) {
                patientClone.getCurrentDiseases().add(targetDiseaseClone);
            }
            patientClone.sortDiseases();
            IAction action = new SingleAction(target, patientClone);
            undoRedoControl.addAction(action, CLINICIANDIAGNOSIS, target);

            screenControl.closeWindow(diagnosisUpdatePane);
        } else {
            String errorString = "Diseases must not have the same disease name and diagnosis date as another disease\n\n";
            if(diseaseNameTextField.getStyleClass().contains("invalid")) {
                errorString = "Disease names must be between 3 and 50 characters. " +
                        "Names must be comprised of lowercase letters, uppercase letters, digits or full stops, and be unique in current diseases.\n\n";
            }

            if(diagnosisDate.getStyleClass().contains("invalid")) {
                errorString = "Diagnosis date must be a valid date not set in the future or before the " +
                        "patient was born.\n\n";
            }

            if(tagsDD.getStyleClass().contains("invalid")) {
                errorString = "A chronic disease can not be cured. Please remove the chronic tag and save the diagnosis" +
                        " before marking this disease as cured.\n\n";
            }
            userActions.log(Level.WARNING, errorString, new String[]{"Attempted to update diagnosis with invalid fields", ((Patient) target).getNhiNumber()});
        }
    }

}
