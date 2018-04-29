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
import model.Donor;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIDonorDiagnosis {


    public AnchorPane donorDiagnosesPane;
    public TableView<Disease> pastDiagnosesView;
    public TableColumn<Disease, LocalDate> pastDateCol;
    public TableColumn<Disease, String> pastDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> pastTagsCol;
    public TableView<Disease> currentDiagnosesView;
    public TableColumn<Disease, LocalDate> currentDateCol;
    public TableColumn<Disease, String> currentDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> currentTagsCol;

    private Donor currentDonor;
    private ArrayList<Disease> currentDiseases;
    private ArrayList<Disease> pastDiseases;

    @FXML
    public void initialize() {
        currentDonor = ScreenControl.getLoggedInDonor();
        currentDiseases = currentDonor.getCurrentDiseases();
        pastDiseases = currentDonor.getPastDiseases();
        loadCurrentDiseases();
        loadPastDiseases();
    }

    public void loadCurrentDiseases() {
        if(currentDiseases == null) currentDiseases = new ArrayList<>();
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        currentDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        currentDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        currentTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        currentDiagnosesView.setItems(observableCurrentDiseases);
    }

    public void loadPastDiseases() {
        if(pastDiseases == null) pastDiseases = new ArrayList<>();
        ObservableList<Disease> observablePastDiseases = FXCollections.observableArrayList(pastDiseases);
        pastDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        pastDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        pastTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        pastDiagnosesView.setItems(observablePastDiseases);
    }

    @FXML
    public void goToProfile() {
        ScreenControl.removeScreen("donorProfile");
        try {
            ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
            ScreenControl.activate("donorProfile");
        }catch (IOException e) {
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the diagnoses page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
