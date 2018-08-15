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
import model.User;
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


import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIAvailibleOrgans extends UndoableController implements IWindowObserver {

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

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

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
        availableOrgansTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && availableOrgansTableView.getSelectionModel().getSelectedItem() != null) {
                Patient selected = availableOrgansTableView.getSelectionModel().getSelectedItem().getPatient();
                GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selected);
                controller.setTarget(selected);
                patientDataService.save(patientDataService.getPatientByNhi(selected.getNhiNumber())); //save to local
            }
        });
    }
    
    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        availableOrgansTableView.refresh();
    }

    @Override
    public void windowClosed() {

    }

    @Override
    protected void load() {

    }
}
