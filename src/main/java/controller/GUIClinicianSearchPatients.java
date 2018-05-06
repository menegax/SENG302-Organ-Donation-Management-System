package controller;

import static utility.UserActionHistory.userActions;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Patient;
import model.DrugInteraction;
import service.Database;
import utility.GlobalEnums;
import utility.SearchPatients;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;


public class GUIClinicianSearchPatients implements Initializable {

    @FXML
    private TableView<Patient> patientDataTable;

    @FXML
    private TableColumn<Patient, String> columnName;

    @FXML
    private TableColumn<Patient, String> columnAge;

    @FXML
    private TableColumn<Patient, String> columnGender;

    @FXML
    private TableColumn<Patient, String> columnRegion;

    @FXML
    private TextField searchEntry;

    private ObservableList<Patient> masterData = FXCollections.observableArrayList();


    /**
     * Initialises the data within the table to all patients
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        FilteredList<Patient> filteredData = setupTableColumnsAndData();

        setupSearchingListener(filteredData);
        setupDoubleClickToPatientEdit();
        setupRowHoverOverText();

    }


    /**
     * Sets up double-click functionality for each row to open a patient profile update
     */
    private void setupDoubleClickToPatientEdit() {

        // Add double-click event to rows
        patientDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && patientDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    GUIPatientProfile controller = fxmlLoader.getController();
                    controller.setViewedPatient(patientDataTable.getSelectionModel()
                            .getSelectedItem());
                    DrugInteraction.setViewedPatient(patientDataTable.getSelectionModel()
                            .getSelectedItem());

                    Stage popUpStage = new Stage();
                    popUpStage.setX(ScreenControl.getMain()
                            .getX()); //offset popup
                    popUpStage.setScene(scene);

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));

                    //Add and show the popup
                    ScreenControl.addPopUp("searchPopup", popUpStage); //ADD to screen control
                    ScreenControl.displayPopUp("searchPopup"); //display the popup
                }
                catch (Exception e) {
                    userActions.log(Level.SEVERE,
                            "Failed to open patient profile scene from search patients table",
                            "attempted to open patient edit window from search patients table");
                    new Alert(Alert.AlertType.ERROR, "Unable to open patient edit window", ButtonType.OK).show();
                }
            }

        });
    }


    /**
     * Sets the table columns to pull the correct data from the patient objects
     *
     * @return a filtered list of patients
     */
    private FilteredList<Patient> setupTableColumnsAndData() {
        // initialize columns
        columnName.setCellValueFactory(d -> d.getValue()
                .getNameConcatenated() != null ? new SimpleStringProperty(d.getValue()
                .getNameConcatenated()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue()
                .getAge())));
        columnGender.setCellValueFactory(d -> d.getValue()
                .getGender() != null ? new SimpleStringProperty(d.getValue()
                .getGender()
                .toString()) : new SimpleStringProperty(""));
        columnRegion.setCellValueFactory(d -> d.getValue()
                .getRegion() != null ? new SimpleStringProperty(d.getValue()
                .getRegion()
                .toString()) : new SimpleStringProperty(""));

        // wrap ObservableList in a FilteredList
        FilteredList<Patient> filteredData = new FilteredList<>(masterData, d -> true);

        // wrap the FilteredList in a SortedList.
        SortedList<Patient> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(patientDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        patientDataTable.setItems(sortedData);
        return filteredData;
    }


    /**
     * Sets the search textfield to listen for any changes and search for the entry on change
     *
     * @param filteredData the patients to be filtered/searched through
     */
    private void setupSearchingListener(FilteredList<Patient> filteredData) {
        // set the filter Predicate whenever the filter changes.
        searchEntry.textProperty()
                .addListener((observable, oldValue, newValue) -> filteredData.setPredicate(patient -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    return SearchPatients.searchByName(newValue)
                            .contains(patient);

                }));
    }


    /**
     * Adds custom hover-over text to each row in the table
     */
    private void setupRowHoverOverText() {
        patientDataTable.setRowFactory(tv -> new TableRow<Patient>() {
            private Tooltip tooltip = new Tooltip();


            @Override
            public void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (patient == null) {
                    setTooltip(null);
                }
                else if (patient.getDonations().isEmpty()) {

                    tooltip.setText(patient.getNameConcatenated() + ". No donations.");
                    setTooltip(tooltip);
                }
                else {
                    StringBuilder tooltipText = new StringBuilder(patient.getNameConcatenated() + ". Donations: ");
                    for (GlobalEnums.Organ organ : patient.getDonations()) {
                        tooltipText.append(organ)
                                .append(", ");
                    }
                    tooltipText = new StringBuilder(tooltipText.substring(0, tooltipText.length() - 2));
                    tooltip.setText(tooltipText.toString());
                    setTooltip(tooltip);
                }
            }
        });
    }


    /**
     * Adds all db data via constructor
     */
    public GUIClinicianSearchPatients() {
        masterData.addAll(Database.getPatients());
    }


    public void goToClinicianHome() {
        ScreenControl.activate("clinicianHome");
    }


    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        patientDataTable.refresh();
    }
}

