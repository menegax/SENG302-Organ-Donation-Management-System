package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import model.Disease;
import model.Donor;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianDiagnosis {

    public AnchorPane clinicianDiagnosesPane;
    public TableView<Disease> pastDiagnosesView;
    public TableColumn<Disease, LocalDate> pastDateCol;
    public TableColumn<Disease, String> pastDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> pastTagsCol;
    public TableView<Disease> currentDiagnosesView;
    public TableColumn<Disease, LocalDate> currentDateCol;
    public TableColumn<Disease, String> currentDiagnosisCol;
    public TableColumn<Disease, GlobalEnums.DiseaseState> currentTagsCol;
    public Button saveButton;
    public Button deleteButton;
    public Button addDiagnosisButton;

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
        pastDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = pastDiagnosesView.getSelectionModel().getSelectedItem();
                ContextMenu rightClickPast = new ContextMenu();
                MenuItem makeChronicAction = new MenuItem("Mark as Chronic");
                makeChronicAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                    pastDiseases.remove(selected);
                    currentDiseases.add(selected);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                rightClickPast.getItems().addAll(makeChronicAction);
                rightClickPast.show(pastDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY());
            }
        });

        currentDiagnosesView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                Disease selected = currentDiagnosesView.getSelectionModel().getSelectedItem();
                ContextMenu rightClickCurrent = new ContextMenu();
                MenuItem makeCuredAction = new MenuItem("Mark as Cured");
                MenuItem makeChronicAction = new MenuItem("Mark as Chronic");
                makeCuredAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CURED);
                    currentDiseases.remove(selected);
                    pastDiseases.add(selected);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                makeChronicAction.setOnAction(event -> {
                    selected.setDiseaseState(GlobalEnums.DiseaseState.CHRONIC);
                    loadCurrentDiseases();
                    loadPastDiseases();
                });
                rightClickCurrent.getItems().addAll(makeChronicAction);
                rightClickCurrent.show(currentDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY());
            }
        });
    }

    private void loadCurrentDiseases() {
        if(currentDiseases == null) currentDiseases = new ArrayList<>();
        currentDiseases.add(new Disease("ChickenPox", currentDonor, null));
        currentDiseases.add(new Disease("Death Curse", currentDonor, GlobalEnums.DiseaseState.CHRONIC));
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        currentDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        currentDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        currentTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        currentDiagnosesView.setItems(observableCurrentDiseases);
    }

    private void loadPastDiseases() {
        if(pastDiseases == null) pastDiseases = new ArrayList<>();
        pastDiseases.add(new Disease("Influenza", currentDonor, null));
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
            userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the clinician diagnoses page to the profile page");
            new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void saveDiagnoses() {
        new Alert(Alert.AlertType.CONFIRMATION, "Diagnoses saved successfully", ButtonType.OK).show();
        goToProfile();
    }
}
