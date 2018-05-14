package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Disease;
import model.Patient;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class for viewing a patient's diagnoses. Past and present lists are shown.
 */
public class GUIPatientDiagnosis {


    @FXML
    public AnchorPane patientDiagnosesPane;

    @FXML
    public TableView<Disease> pastDiagnosesView;

    @FXML
    public TableColumn<Disease, LocalDate> pastDateCol;

    @FXML
    public TableColumn<Disease, String> pastDiagnosisCol;

    @FXML
    public TableColumn<Disease, GlobalEnums.DiseaseState> pastTagsCol;

    @FXML
    public TableView<Disease> currentDiagnosesView;

    @FXML
    public TableColumn<Disease, LocalDate> currentDateCol;

    @FXML
    public TableColumn<Disease, String> currentDiagnosisCol;

    @FXML
    public TableColumn<Disease, GlobalEnums.DiseaseState> currentTagsCol;

    /**
     * Current disease list of the patient
     */
    private ArrayList<Disease> currentDiseases;

    /**
     * Past disease list of the patient
     */
    private ArrayList<Disease> pastDiseases;

    /**
     * Initializes the patient view of their own diseases (past and current).
     */
    @FXML
    public void initialize() {
        Patient currentPatient = ScreenControl.getLoggedInPatient();
        currentDiseases = currentPatient.getCurrentDiseases();
        pastDiseases = currentPatient.getPastDiseases();
        loadCurrentDiseases();
        loadPastDiseases();
    }

    /**
     * Loads current disease list from the target patient's current diseases
     */
    public void loadCurrentDiseases() {
        if(currentDiseases == null) currentDiseases = new ArrayList<>();
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        currentDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        currentDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        currentTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        currentDiagnosesView.setItems(observableCurrentDiseases);
    }

    /**
     * Loads past disease list from the target patient's past diseases
     */
    public void loadPastDiseases() {
        if(pastDiseases == null) pastDiseases = new ArrayList<>();
        ObservableList<Disease> observablePastDiseases = FXCollections.observableArrayList(pastDiseases);
        pastDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        pastDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        pastTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        pastDiagnosesView.setItems(observablePastDiseases);
    }

    /**
     * Returns to the patient profile page
     */
    @FXML
    public void goToProfile() {
        ScreenControl.removeScreen("patientProfile");
        try {
            ScreenControl.addScreen("patientProfile", FXMLLoader.load(getClass().getResource("/scene/patientProfile.fxml")));
            ScreenControl.activate("patientProfile");
        }catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the diagnoses page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
