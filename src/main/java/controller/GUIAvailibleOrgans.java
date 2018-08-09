package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Administrator;
import model.Clinician;
import model.DrugInteraction;
import org.apache.commons.lang3.StringUtils;
import service.Database;
import service.OrganWaitlist;
import utility.GlobalEnums;
import utility.GlobalEnums.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import utility.undoRedo.UndoableStage;
import model.Patient;

import java.io.IOException;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIAvailibleOrgans {

	@FXML
    private GridPane availableOrgans;
    @FXML
    private TableView<PatientOrgan> availibleOrgansTableView;
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

    private UserControl userControl = new UserControl();


    public void initialize() {
    	for (Patient patient : Database.getPatients()) {
    		if (patient.getDeath() != null) {
    			for (Organ organ : patient.getDonations()) {
        			masterData.add(new PatientOrgan(patient, organ));
    			}
    		}
    	}
    	
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
        locationCol.setCellValueFactory(r -> new SimpleStringProperty("Death Location"));
        deathCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getPatient().getDeath().toString()));
        //TODO add expiry countdown
        expiryCol.setCellValueFactory(r -> new SimpleStringProperty("Expiry"));

        // wrap ObservableList in a FilteredList
        FilteredList<PatientOrgan> filteredData = new FilteredList<>(masterData);

        // wrap the FilteredList in a SortedList.
        SortedList<PatientOrgan> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(availibleOrgansTableView.comparatorProperty());

        // add sorted (and filtered) data to the table.
        availibleOrgansTableView.setItems(sortedData);

    }
    
    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        availibleOrgansTableView.refresh();
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
    }
}
