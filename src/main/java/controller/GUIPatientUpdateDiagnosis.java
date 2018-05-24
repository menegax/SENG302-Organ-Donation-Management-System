package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.Disease;
import model.Patient;
import utility.GlobalEnums;
import utility.undoRedo.StatesHistoryScreen;

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
    public AnchorPane diagnosisUpdatePane;

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

    /**
     * Diagnosis being updated
     */
    private static Disease target;

    /**
     * Patient who the target diagnosis belongs to
     */
    private static Patient currentPatient;
    public Label titleLabel;

    private static boolean isAdd;

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
        currentPatient = userControl.getTargetPatient();
        if(isAdd) {
            titleLabel.setText("Add Diagnosis");
            target = new Disease(null, null);
        } else {
            titleLabel.setText("Update Diagnosis");
        }
        populateDropdown();
        populateForm();
        controls = new ArrayList<Control>() {{
            add(diseaseNameTextField);
            add(diagnosisDate);
            add(tagsDD);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.PATIENTUPDATEDIAGNOSIS);
    }

    /**
     * Sets the diagnosis name text field to the current name.
     * Sets the diagnosis date picker to the current diagnosis date.
     * Sets the drop down state menu to the diagnosis' current state.
     */
    private void populateForm() {
        if(target.getDiseaseName() != null) {
            diseaseNameTextField.setText(target.getDiseaseName());
        }

        if(target.getDateDiagnosed() != null) {
            diagnosisDate.setValue(target.getDateDiagnosed());
        }

        if(target.getDiseaseState() != null) {
            tagsDD.setValue(target.getDiseaseState().getValue());
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianDiagnosis.fxml"));
        try {
            ScreenControl.loadPopUpPane(diagnosisUpdatePane.getScene(), fxmlLoader);
        } catch (IOException e) {
            userActions.log(Level.SEVERE, "Error returning to diagnoses screen in popup", "attempted to navigate from the update diagnosis page to the diagnoses page in popup");
            new Alert(Alert.AlertType.WARNING, "Error loading diagnoses page", ButtonType.OK).show();
        }
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
            if (target.isInvalidDiagnosisDate(diagnosisDate.getValue(), currentPatient)) {
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

        if(target.getDiseaseState() == GlobalEnums.DiseaseState.CHRONIC &&
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
            d.setDateDiagnosed(diagnosisDate.getValue(), currentPatient);
        } catch (InvalidObjectException e) {
            userActions.log(Level.SEVERE, "The diagnosis date is not valid.", "Attempted to add an invalid diagnosis date");
        }
        for (Disease disease : currentPatient.getCurrentDiseases()) {
            if(disease != target && isDuplicate(disease, d)) {
                return false;
            }
        }

        for (Disease disease: currentPatient.getPastDiseases()) {
            if(disease != target && isDuplicate(disease, d)) {
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
     * valid, the new diagnosis is added to the patient's current diagnoses. Otherwise an alert is shown and no new
     * dignosis
     *
     * If the update is invalid, an alert is shown with the errors in question explained in the alert information.
     * The stage is not closed in this case.
     */
    public void completeUpdate() {
        if(isValidUpdate()) {
            GUIClinicianDiagnosis.setChanged(true);
            target.setDiseaseName(diseaseNameTextField.getText());
            try {
                target.setDateDiagnosed(diagnosisDate.getValue(), currentPatient);
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, "The date is not valid. This should never be called.");
            }
            try {
                switch (tagsDD.getSelectionModel().getSelectedItem().toString()) {
                    case "cured":
                        target.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                        break;
                    case "chronic":
                        target.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                        break;
                    default:
                        target.setDiseaseState(null);
                        break;
                }
            } catch (NullPointerException e) {
                target.setDiseaseState(null);
            }

            if(isAdd) {
                currentPatient.getCurrentDiseases().add(target);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianDiagnosis.fxml"));
                try {
                    ScreenControl.loadPopUpPane(diagnosisUpdatePane.getScene(), fxmlLoader);
                } catch (IOException e) {
                    userActions.log(Level.SEVERE, "Error returning to diagnoses screen in popup", "attempted to navigate from the update diagnosis page to the diagnoses page in popup");
                    new Alert(Alert.AlertType.WARNING, "Error loading diagnoses page", ButtonType.OK).show();
                }
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianDiagnosis.fxml"));
                try {
                    ScreenControl.loadPopUpPane(diagnosisUpdatePane.getScene(), fxmlLoader);
                } catch (IOException e) {
                    userActions.log(Level.SEVERE, "Error returning to diagnoses screen in popup", "attempted to navigate from the update diagnosis page to the diagnoses page in popup");
                    new Alert(Alert.AlertType.WARNING, "Error loading diagnoses page", ButtonType.OK).show();
                }
            }

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not update diagnosis");
            alert.setContentText(errorString);
            alert.showAndWait();
        }
    }


    /**
     * Redoes an action
     */
    public void redo() {
        statesHistoryScreen.redo();
    }

    /**
     * Undoes the last action
     */
    public void undo() {
        statesHistoryScreen.undo();
    }

}
