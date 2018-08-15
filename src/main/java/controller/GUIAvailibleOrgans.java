package controller;

import static utility.UserActionHistory.userActions;

import com.sun.xml.internal.bind.v2.runtime.property.ValueProperty;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import model.PatientOrgan;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
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
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIAvailibleOrgans {

	@FXML
    private GridPane availableOrgans;
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


    public void initialize() {
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
        ExpiryObservable.getInstance().addObserver((o, arg) -> {
            masterData.remove(arg);
        });
        populateTable();
    }
    
    /**
     * Populates waiting list table with all patients waiting to receive an organ
     */
    private void populateTable() {
        // initialize columns
        patientCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient().getNhiNumber()));
        organCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getOrgan().toString()));



        locationCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getPatient().getDeathLocationConcat()));
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getPatient().getDeathDate().toString()));
        //TODO add expiry countdown

        expiryCol.setCellValueFactory(r -> r.getValue().getProgressTask().messageProperty());
        organExpiryProgressCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getProgressTask()));
        organExpiryProgressCol.setCellFactory(cb -> ProgressBarCustomTableCell.getCell(organExpiryProgressCol));

        // wrap ObservableList in a FilteredList
        FilteredList<PatientOrgan> filteredData = new FilteredList<>(masterData);

        // wrap the FilteredList in a SortedList.
        SortedList<PatientOrgan> sortedData = new SortedList<>(filteredData);


        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());

        // add sorted (and filtered) data to the table.
        availableOrgansTableView.setItems(sortedData);
        availableOrgansTableView.setVisible(true);
        tableRefresh();
        setUpDoubleClickToPatientEdit();
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update. Opens the selected
     * patient's profile view screen in a new window.
     */
    private void setUpDoubleClickToPatientEdit() {
        // Add double-click event to rows
        availableOrgansTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && availableOrgansTableView.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    UserControl userControl = new UserControl();
                    userControl.setTargetUser(availableOrgansTableView.getSelectionModel().getSelectedItem().getPatient());
                    Patient patient = patientDataService.getPatientByNhi(availableOrgansTableView.getSelectionModel().getSelectedItem().getPatient().getNhiNumber());
                    patientDataService.save(patient); //save to local
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/home.fxml"));
                    UndoableStage popUpStage = new UndoableStage();

                    //Set initial popup dimensions
                    popUpStage.setWidth(1150);
                    popUpStage.setHeight(700);
                    ScreenControl.getScreenControl().addStage(popUpStage.getUUID(), popUpStage);
                    ScreenControl.getScreenControl().show(popUpStage.getUUID(), fxmlLoader.load());

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));
                } catch (IOException e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open patient profile scene from search patients table",
                            "attempted to open patient edit window from search patients table");
                    new Alert(Alert.AlertType.ERROR, "Unable to open patient edit window", ButtonType.OK).show();
                }
            }
        });
    }
    
    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        availableOrgansTableView.refresh();
    }
}
