package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.CachedThreadPool;
import utility.ExpiryObservable;
import utility.GlobalEnums.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import model.Patient;
import utility.ProgressBarCustomTableCell;
import utility.ProgressTask;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIAvailableOrgans extends UndoableController implements IWindowObserver {

    @FXML
    private TableView<PatientOrgan> availableOrgansTableView;

    @FXML
    private TableColumn<PatientOrgan, String> patientCol;

    @FXML
    private TableColumn<PatientOrgan, String> organCol;

    @FXML
    private TableColumn<PatientOrgan, String> locationCol;

    @FXML
    private TableColumn<PatientOrgan, String> deathCol;

    @FXML
    private TableColumn<PatientOrgan, String> expiryCol;

    @FXML
    private TableColumn<PatientOrgan, ProgressTask> organExpiryProgressCol;

    private ObservableList<PatientOrgan> masterData = FXCollections.observableArrayList();

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    public void load() {
        List<Patient> deadPatients = patientDataService.getDeadPatients();
        for (Patient patient : deadPatients) {
            if (patient.getDeathDate() != null) {
                for (Organ organ : patient.getDonations()) {
                    PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                    if (!masterData.contains(patientOrgan) && !patientOrgan.isExpired()) {
                        masterData.add(patientOrgan);
                    }
                }
            }
        }
        ExpiryObservable.getInstance()
                .addObserver((o, arg) -> masterData.remove(arg));
        populateTable();
    }


    /**
     * Populates waiting list table with all patients waiting to receive an organ
     */
    private void populateTable() {
        // initialize columns
        patientCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getNhiNumber()));
        organCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getOrgan()
                .toString()));

        locationCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getDeathLocationConcat()));
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient()
                .getDeathDate()
                .toString()));

        //expiry
        expiryCol.setCellValueFactory(r -> r.getValue()
                .getProgressTask()
                .messageProperty());
        organExpiryProgressCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()
                .getProgressTask()));
        organExpiryProgressCol.setCellFactory(cb -> ProgressBarCustomTableCell.getCell(organExpiryProgressCol));

        // wrap ObservableList in a FilteredList
        FilteredList<PatientOrgan> filteredData = new FilteredList<>(masterData);

        // wrap the FilteredList in a SortedList.
        SortedList<PatientOrgan> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to compare on expiry.
        sortedData.setComparator((patientOrgan1, patientOrgan2) -> {
            return patientOrgan2.timeRemaining()
                    .compareTo(patientOrgan1.timeRemaining());
        });

        // add sorted (and filtered) data to the table.
        availableOrgansTableView.setItems(sortedData);
        availableOrgansTableView.setVisible(true);
        setUpDoubleClickToPatientEdit();
    }


    /**
     * Sets up double-click functionality for each row to open a patient profile update. Opens the selected
     * patient's profile view screen in a new window.
     */
    private void setUpDoubleClickToPatientEdit() {
        availableOrgansTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && availableOrgansTableView.getSelectionModel()
                    .getSelectedItem() != null) {
                Patient selected = availableOrgansTableView.getSelectionModel()
                        .getSelectedItem()
                        .getPatient();
                GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selected);
                controller.setTarget(selected);
                patientDataService.save(patientDataService.getPatientByNhi(selected.getNhiNumber())); //save to local
            }
        });
    }


    /**
     * Opens the potential matches table for the selected organ
     */
    @FXML
    public void viewPotentialMatches() {
        PatientOrgan selected = availableOrgansTableView.getSelectionModel()
                .getSelectedItem();
        if (selected == null) {
            userActions.log(Level.WARNING, "Please select a organ to match", "Attempted to view available matches without selecting an organ");
        }
        else {
            GUIClinicianPotentialMatches controller =
                    (GUIClinicianPotentialMatches) screenControl.show("/scene/clinicianPotentialMatches.fxml", false, null, selected.getPatient());
            controller.setTarget(selected.getPatient(), selected.getOrgan());
        }
    }


    /**
     * Refreshes the table when a profile opened by this controller
     */
    public void windowClosed() {
        availableOrgansTableView.refresh();
    }
}
