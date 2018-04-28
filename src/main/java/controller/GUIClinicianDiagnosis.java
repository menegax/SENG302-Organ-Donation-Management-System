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
import service.Database;
import utility.GlobalEnums;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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
    private ArrayList<Disease> deletedPast = new ArrayList<Disease>();
    private ArrayList<Disease> deletedCurrent = new ArrayList<Disease>();
    private ArrayList<Disease> currentDiseases;
    private ArrayList<Disease> pastDiseases;
    private Disease chosen;
    private boolean changed = false;


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
            } else if (click.getButton() == MouseButton.PRIMARY && pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = pastDiagnosesView.getSelectionModel().getSelectedItem();
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
                rightClickCurrent.getItems().addAll(makeChronicAction, makeCuredAction);
                rightClickCurrent.show(currentDiagnosesView.getSelectionModel().getTableView(), click.getScreenX(), click.getScreenY());
            } else if (click.getButton() == MouseButton.PRIMARY && currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
                chosen = currentDiagnosesView.getSelectionModel().getSelectedItem();
            }
        });
    }

    private void loadCurrentDiseases() {
        if(currentDiseases == null) currentDiseases = new ArrayList<>();
        ObservableList<Disease> observableCurrentDiseases = FXCollections.observableArrayList(currentDiseases);
        currentDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        currentDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        currentTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        currentDiagnosesView.setItems(observableCurrentDiseases);
    }

    private void loadPastDiseases() {
        if(pastDiseases == null) pastDiseases = new ArrayList<>();
        ObservableList<Disease> observablePastDiseases = FXCollections.observableArrayList(pastDiseases);
        pastDateCol.setCellValueFactory(new PropertyValueFactory<>("dateDiagnosed"));
        pastDiagnosisCol.setCellValueFactory(new PropertyValueFactory<>("diseaseName"));
        pastTagsCol.setCellValueFactory(new PropertyValueFactory<>("diseaseState"));
        pastDiagnosesView.setItems(observablePastDiseases);
    }

    @FXML
    public void goToProfile() {
        boolean back = false;
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have made some changes, are you sure you want to continue?", ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.get() == ButtonType.YES) {
                back = true;
                System.out.println(currentDiseases);
                currentDiseases.addAll(deletedCurrent);
                pastDiseases.addAll(deletedPast);
                System.out.println(currentDiseases);
            }
        } else {
            back = true;
        }
        if (back){
            ScreenControl.removeScreen("donorProfile");
            try {
                ScreenControl.addScreen("donorProfile", FXMLLoader.load(getClass().getResource("/scene/donorProfile.fxml")));
                ScreenControl.activate("donorProfile");
            } catch (IOException e) {
                userActions.log(Level.SEVERE, "Error loading profile screen", "attempted to navigate from the clinician diagnoses page to the profile page");
                new Alert(Alert.AlertType.WARNING, "ERROR loading profile page", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void saveDiagnoses() {
        currentDonor.setCurrentDiseases(currentDiseases);
        currentDonor.setPastDiseases(pastDiseases);
        Database.saveToDisk();
        new Alert(Alert.AlertType.CONFIRMATION, "Diagnosis saved successfully", ButtonType.OK).show();
        changed = false;
        goToProfile();
    }


    @FXML
    public void deleteDiagnoses() {
        if (pastDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            pastDiseases.remove(chosen);
            deletedPast.add(chosen);
            loadPastDiseases();
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnoses deleted successfully", ButtonType.OK).show();
        } else if (currentDiagnosesView.getSelectionModel().getSelectedItem() != null) {
            changed = true;
            currentDiseases.remove(chosen);
            deletedCurrent.add(chosen);
            loadCurrentDiseases();
            new Alert(Alert.AlertType.CONFIRMATION, "Diagnoses deleted successfully", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.WARNING, "No Diagnosis selected", ButtonType.OK).show();
        }
    }
}
