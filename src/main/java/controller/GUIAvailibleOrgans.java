package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import model.Patient;

import java.util.List;

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
        //TODO add death location when merged
        //locationCol.setCellValueFactory(r -> new SimpleStringProperty("Death Location"));
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient().getDeathDate().toString()));
        //TODO add expiry countdown
        //expiryCol.setCellValueFactory(r -> new SimpleStringProperty("Expiry"));

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
    private class PatientOrgan {
    	private Patient patient;
    	private Organ organ;
    	
    	public PatientOrgan(Patient patient, Organ organ) {
    		this.patient = patient;
    		this.organ = organ;
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
