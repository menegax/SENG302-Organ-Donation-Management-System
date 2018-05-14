package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
public class GUIPatientUpdateDiagnosis implements IPopupable {

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

    /**
     * Diagnosis being updated
     */
    private static Disease target;

    /**
     * Patient who the target diagnosis belongs to
     */
    private static Patient currentPatient;

    private StatesHistoryScreen statesHistoryScreen;


    @Override
    public void setViewedPatient(Patient patient) {
        currentPatient = patient;
    }

    /**
     * Sets the diagnosis that is being updated
     * @param disease diagnosis to update
     */
    public static void setDisease(Disease disease) {
        target = disease;
    }

    /**
     * Initializes the popup window for updating a diagnosis.
     * Adds dropdown for disease states.
     * Populates all editable nodes with the current disease information
     */
    public void initialize() {
        populateDropdown();
        populateForm();
        ArrayList<Control> controls = new ArrayList<Control>() {{
            add(diseaseNameTextField);
            add(diagnosisDate);
            add(tagsDD);
        }};
        statesHistoryScreen = new StatesHistoryScreen(diagnosisUpdatePane, controls);
    }

    /**
     * Sets the diagnosis name text field to the current name.
     * Sets the diagnosis date picker to the current diagnosis date.
     * Sets the drop down state menu to the diagnosis' current state.
     */
    private void populateForm() {
        diseaseNameTextField.setText(target.getDiseaseName());
        diagnosisDate.setValue(target.getDateDiagnosed());
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
            ScreenControl.loadPopUpPane(diagnosisUpdatePane.getScene(), fxmlLoader, currentPatient);
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
     * Checks that the updated fields are valid (name, date and state of diagnosis.
     * Returns true if the update is valid and false otherwise.
     * If the update is valid, the node is reset to whatever is currently shown
     * @return boolean valid updates
     */
    private boolean isValidUpdate() {
        boolean valid = true;
        if(!diseaseNameTextField.getText().matches("[A-Z|a-z0-9.]{3,75}")) {
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

        return valid;
    }

    /**
     * Called when the done button is selected. If the update is valid, the diagnosis for the donor is updated
     * (but not saved) to the current updated diagnosis information and the popup stage is closed.
     * If the update is invalid, an alert is shown with the errors in question explained in the alert information.
     * The stage is not closed in this case.
     */
    public void completeUpdate() {
        if(isValidUpdate()) {
            if(!(target.getDiseaseName().equals(diseaseNameTextField.getText()) && target.getDateDiagnosed() ==
                    diagnosisDate.getValue() && target.getDiseaseState() ==
                    tagsDD.getSelectionModel().getSelectedItem())) {
                GUIClinicianDiagnosis.setChanged(true);
            }
            target.setDiseaseName(diseaseNameTextField.getText());
            try {
                target.setDateDiagnosed(diagnosisDate.getValue(), currentPatient);
            } catch (InvalidObjectException e) {
                userActions.log(Level.SEVERE, "The date is not valid. This should never be called.");
            }
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

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/clinicianDiagnosis.fxml"));
            try {
                ScreenControl.loadPopUpPane(diagnosisUpdatePane.getScene(), fxmlLoader, currentPatient);
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error returning to diagnoses screen in popup", "attempted to navigate from the update diagnosis page to the diagnoses page in popup");
                new Alert(Alert.AlertType.WARNING, "Error loading diagnoses page", ButtonType.OK).show();
            }
        } else {
            String errorString = "";
            if(diseaseNameTextField.getStyleClass().contains("invalid")) {
                errorString += "Disease names must be between 3 and 75 characters. " +
                        "Names must be comprised of lowercase letters, uppercase letters, digits or full stops.\n\n";
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


    public void redo() {
        statesHistoryScreen.redo();
    }

    public void undo() {
        statesHistoryScreen.undo();
    }

}
