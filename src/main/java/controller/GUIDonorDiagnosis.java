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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Disease;
import model.Donor;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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

    @FXML
    public void initialize() {
        currentDonor = ScreenControl.getLoggedInDonor();
        loadCurrentDiseases();
        loadPastDiseases();
    }

    public void loadCurrentDiseases() {
        ArrayList<Disease> currentDiseases = currentDonor.getCurrentDiseases();
        if(currentDiseases == null) currentDiseases = new ArrayList<>();
        currentDiseases.add(new Disease("Measles", currentDonor, null));
        currentDiseases.add(new Disease("Death Curse", currentDonor, GlobalEnums.DiseaseState.CHRONIC));
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        currentDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        currentDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        currentTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        currentDiagnosesView.setItems(observableCurrentDiseases);
    }

    public void loadPastDiseases() {
        ArrayList<Disease> pastDiseases = currentDonor.getPastDiseases();
        if(pastDiseases == null) pastDiseases = new ArrayList<>();
        pastDiseases.add(new Disease("Flu", currentDonor, null));
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
