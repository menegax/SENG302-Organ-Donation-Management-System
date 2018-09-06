package controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

import static java.util.logging.Level.FINE;
import static utility.SystemLogger.systemLogger;
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

    private SortedList<PatientOrgan> sortedData;

    private IPatientDataService patientDataService = new PatientDataService();

    private ScreenControl screenControl = ScreenControl.getScreenControl();


    public void load() {
        masterData.clear();
        CachedThreadPool.getCachedThreadPool().getThreadService().shutdown();
        List<Patient> deadPatients = patientDataService.getDeadPatients();
        for (Patient patient : deadPatients) {
            if (patient.getDeathDate() != null) {
                for (Organ organ : patient.getDonations()) {
                    PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                    if (!masterData.contains(patientOrgan)) {
                        if (patientOrgan.timeRemaining() < 0) {
                            masterData.add(patientOrgan);
                        }
                    }
                }
            }
        }
        ExpiryObservable.getInstance().addObserver((o, arg) -> masterData.remove(arg));
        populateTable();
    }

    @FXML
    public void onSort(Event event) {
        Comparator<PatientOrgan> test = (o1, o2) -> Long.compare(o2.timeRemaining(), o1.timeRemaining());
        ObjectProperty<Comparator<? super PatientOrgan>> test1 = new SimpleObjectProperty<>(test);
        sortedData.comparatorProperty().unbind();
        if (availableOrgansTableView.getSortOrder().size() == 0) {
            sortedData.comparatorProperty().bind(test1);
            availableOrgansTableView.setSortPolicy(param -> true);
        } else {
            sortedData.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());
        }
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
        sortedData = new SortedList<>(filteredData);

        Comparator<PatientOrgan> test = (o1, o2) -> Long.compare(o2.timeRemaining(), o1.timeRemaining());
        ObjectProperty<Comparator<? super PatientOrgan>> test1 = new SimpleObjectProperty<>(test);

        sortedData.comparatorProperty().bind(test1);

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
        } else if (selected.getPatient().getBloodGroup() == null) {
            userActions.log(Level.WARNING, "Selected donor does not have a blood group set. Please set a blood group.", "Attempted to view available matches for a donor without a blood group");
        } else {
            GUIClinicianPotentialMatches controller = (GUIClinicianPotentialMatches) screenControl.show("/scene/clinicianPotentialMatches.fxml", false, null, selected.getPatient());
            controller.setTarget(selected.getPatient(), selected.getOrgan());
        }
    }


    /**
     * Refreshes the table when a profile opened by this controller
     */
    public void windowClosed() {
        load();
        availableOrgansTableView.refresh();
    }

    private UserControl userControl = UserControl.getUserControl();

    @FXML
    public void viewOnMap() throws DataFormatException {

        // todo rework

        List<Patient> patients = new ArrayList<>();

        for (int i = 0; i < masterData.size(); i++) {
            patients.add(masterData.get(i).getPatient());
            System.out.println(masterData.get(i).getPatient());
        }

        Alert alert;
        if (screenControl.getMapOpen()) {
            alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to repopulate the map?"
                    , ButtonType.OK, ButtonType.NO);
            alert.show();
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION, "Select 'View on Map' again after map is open to populate map"
                    , ButtonType.OK);
            alert.show();
        }

        alert.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            screenControl.setIsCustomSetMap(true);
            if (!screenControl.getMapOpen()) {
                screenControl.show("/scene/map.fxml", true, this, userControl.getLoggedInUser());
                screenControl.setMapOpen(true);
            }
            GUIMap.jsBridge.setMember("patients", patients);
            GUIMap.jsBridge.call("setPatients");
            screenControl.setMapOpen(true);
        });
    }
}
