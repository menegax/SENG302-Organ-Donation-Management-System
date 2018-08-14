package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.CachedThreadPool;
import utility.GlobalEnums.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import model.Patient;


import java.util.List;
import java.util.Random;

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
    private TableColumn<PatientOrgan, Double> organExpiryProgressCol;

    private ObservableList<PatientOrgan> masterData = FXCollections.observableArrayList();

    private IPatientDataService patientDataService = new PatientDataService();


    public void initialize() {
        List<Patient> deadPatients = patientDataService.getDeadPatients();
    	for (Patient patient : deadPatients) {
    		if (patient.getDeathDate() != null) {
    			for (Organ organ : patient.getDonations()) {
                    PatientOrgan patientOrgan = new PatientOrgan(patient, organ);
                    if (!masterData.contains(patientOrgan)) {
                        masterData.add(patientOrgan);
                    }
                }
            }
        }
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
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient().getDeathDate().toString()));
        //TODO add expiry countdown
        organExpiryProgressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        organExpiryProgressCol.setCellFactory(ProgressBarTableCell.forTableColumn());

        // wrap ObservableList in a FilteredList
        FilteredList<PatientOrgan> filteredData = new FilteredList<>(masterData);

        // wrap the FilteredList in a SortedList.
        SortedList<PatientOrgan> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
       // sortedData.comparatorProperty().bind(availableOrgansTableView.comparatorProperty());

        // add sorted (and filtered) data to the table.
        availableOrgansTableView.setItems(sortedData);
        availableOrgansTableView.setVisible(true);
        tableRefresh();

        for (PatientOrgan task : availableOrgansTableView.getItems()){
            CachedThreadPool.getCachedThreadPool().getThreadService().submit(task);
        }
    }
    
    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        availableOrgansTableView.refresh();
    }
    
    /**
     * Simple holder for patients and organ so that it is known which organ belongs to whom.
     */
    private class PatientOrgan extends Task<Void> {
    	private Patient patient;
    	private Organ organ;

        PatientOrgan(Patient patient, Organ organ) {
    		this.patient = patient;
    		this.organ = organ;
    	}

        @Override
        protected Void call() throws Exception {
            System.out.println("test");
            return null;
        }

    	public Patient getPatient() {
    		return patient;
    	}
    	
    	public Organ getOrgan() {
    		return organ;
    	}


    	@Override
    	public boolean equals(Object obj) {
    	    PatientOrgan patientOrgan = (PatientOrgan) obj;
            return patientOrgan.patient.getNhiNumber().equals(this.patient.getNhiNumber()) &&
                    patientOrgan.organ.equals(this.organ);

        }

    }
}
