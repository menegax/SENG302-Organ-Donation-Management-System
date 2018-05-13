package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Disease;
import model.Patient;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class GUIPatientUpdateDiagnosis {



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

    private static Disease target;

    private static Patient currentPatient;


    public static void setPatient(Patient target) {
        currentPatient = target;
    }

    public static void setDisease(Disease disease) {
        target = disease;
    }

    public void initialize() {
        populateDropdown();
        populateForm();
    }

    private void populateForm() {
        diseaseNameTextField.setText(target.getDiseaseName());
        diagnosisDate.setValue(target.getDateDiagnosed());
        if(target.getDiseaseState() != null) {
            tagsDD.setValue(target.getDiseaseState().getValue());
        }
    }

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
    public void cancelUpdate(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
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

    public void completeUpdate(ActionEvent actionEvent) {
        if(isValidUpdate()) {
            target.setDiseaseName(diseaseNameTextField.getText());
            try {
                target.setDateDiagnosed(diagnosisDate.getValue(), currentPatient);
            } catch (InvalidObjectException e) {

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

            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
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

}
