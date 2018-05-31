package controller;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Patient;
import utility.GlobalEnums;
import utility.SearchPatients;
import utility.undoRedo.StatesHistoryScreen;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;

public class GUIClinicianSearchPatients extends UndoableController implements Initializable {

    private static final int X = 1; // Constant for the maximum default number of profiles returned in a search

    private static final int MAX_RESULTS = 3; // The maximum number of results possible to display

    @FXML
    private TableView<Patient> patientDataTable;

    @FXML
    private TableColumn<Patient, String> columnName;

    @FXML
    private TableColumn<Patient, String> columnStatus;

    @FXML
    private TableColumn<Patient, String> columnAge;

    @FXML
    private TableColumn<Patient, String> columnBirthGender;

    @FXML
    private TableColumn<Patient, String> columnRegion;

    @FXML
    private TextField searchEntry;

    @FXML
    private Button displayX;

    @FXML
    private Button displayY;

    private ObservableList<Patient> masterData = FXCollections.observableArrayList();
    private ObservableList<Patient> filteredList = FXCollections.observableArrayList();

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initialises the data within the table to all patients
     *
     * @param url URL not used
     * @param rb  Resource bundle not used
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        setDisplaySearchResultsButton( true, false );
        FilteredList<Patient> filteredData = setupTableColumnsAndData();
        setupSearchingListener(filteredData);
        setupDoubleClickToPatientEdit();
        setupRowHoverOverText();
        searchEntry.setPromptText( "There are " + getProfileCount() + " profiles" );
        setupUndoRedo();
    }

    /**
     * Sets up undo redo for this screen
     */
    private void setupUndoRedo() {
        controls = new ArrayList<Control>() {{
            add(searchEntry);
        }};
        statesHistoryScreen = new StatesHistoryScreen(controls, GlobalEnums.UndoableScreen.CLINICIANSEARCHPATIENTS);
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update. Opens the selected
     * patient's profile view screen in a new window.
     */
    private void setupDoubleClickToPatientEdit() {
        // Add double-click event to rows
        patientDataTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && patientDataTable.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    UserControl userControl = new UserControl();
                    userControl.setTargetPatient(patientDataTable.getSelectionModel()
                            .getSelectedItem());
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    UndoableStage popUpStage = new UndoableStage();
                    screenControl.addStage(popUpStage.getUUID(), popUpStage);
                    screenControl.show(popUpStage.getUUID(), fxmlLoader.load());

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> Platform.runLater(this::tableRefresh));
                }
                catch (IOException e) {
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
        columnStatus.setCellValueFactory( d -> d.getValue()
                .getStatus() != null ? new SimpleStringProperty(d.getValue()
                .getStatus()
                .toString()) : new SimpleStringProperty(""));
        columnAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue()
                .getAge())));
        columnBirthGender.setCellValueFactory(d -> d.getValue()
                .getBirthGender() != null ? new SimpleStringProperty(d.getValue()
                .getBirthGender()
                .toString()) : new SimpleStringProperty(""));
        columnRegion.setCellValueFactory(d -> d.getValue()
                .getRegion() != null ? new SimpleStringProperty(d.getValue()
                .getRegion()
                .toString()) : new SimpleStringProperty(""));

        // wrap ObservableList in a FilteredList
        FilteredList<Patient> filteredData = new FilteredList<>(masterData, new Predicate<Patient>() {
            @Override
            public boolean test(Patient d) {
                return true;
            }
        });

        // 2. Set the filter Predicate whenever the filter changes.
        searchEntry.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            masterData.clear();
            masterData.addAll(SearchPatients.searchByName(newValue));
            filteredData.setPredicate(patient -> true);

            if (filteredData.size() < X) {
                setDisplaySearchResultsButton( true, false );
            } else {
                setDisplaySearchResultsButton( false, true );
            }
        });

        // wrap the FilteredList in a SortedList.
        SortedList<Patient> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty()
                .bind(patientDataTable.comparatorProperty());

        // add sorted (and filtered) data to the table.
        patientDataTable.setItems(sortedData);

        if (masterData.size() < X) {
            setDisplaySearchResultsButton( true, false );
        } else {
            setDisplaySearchResultsButton( false, true );
        }
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
                    if (SearchPatients.filteredResults.size() < X) {
                        setDisplaySearchResultsButton( true, false );
                    } else {
                        setDisplaySearchResultsButton( false, true );
                    }
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    } else if (newValue.toLowerCase().equals( "male" ) || newValue.toLowerCase().equals("female")) {
                        //return SearchPatients.searchByGender(newValue).contains(patient);
                        return patient.getBirthGender().getValue().toLowerCase().equals( newValue.toLowerCase() ); // ------------------------------this is where it fails!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                    return SearchPatients.searchByName(newValue)
                            .contains(patient);
                }));
    }

    /**
     * Displays only the first X=30 profiles to the search patients table if more than X results from search
     */
    @FXML
    private void displayDefaultProfiles() {
        filteredList.clear();
        filteredList.addAll( SearchPatients.filterNumberSearchResults(X) );
        patientDataTable.setItems( filteredList );
    }

    /**
     * Displays only the first X=30 profiles to the search patients table if more than X results from search
     */
    @FXML
    private void displayCustomProfiles() {
        filteredList.clear();
        filteredList.addAll( SearchPatients.filterNumberSearchResults(MAX_RESULTS) );
        patientDataTable.setItems( filteredList );
    }



    /**
     * Sets disable/visible for the buttons displaying X or Y number of profiles from a search
     * @param a The boolean for whether the button is disabled
     * @param b The boolean for whether the button is visible
     */
    private void setDisplaySearchResultsButton(boolean a, boolean b) {
        displayX.setDisable( a );
        displayX.setVisible( b );
        displayY.setDisable( a );
        displayY.setVisible( b );
        if (SearchPatients.totalResults.size() < MAX_RESULTS) {
            displayY.setText( "Display total " + SearchPatients.totalResults.size() + " profiles" );
        } else {
            displayY.setText( "Display total " + MAX_RESULTS + " profiles" );
        }
    }

    /**
     * Gets the number of profiles that have been returned from a patient search by clinician
     * @return An integer value representing the total number of profiles returned from the search
     */
    private int getProfileCount() {
       return SearchPatients.totalResults.size();
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
        masterData.addAll(SearchPatients.getDefaultResults());
    }

    public void goToClinicianHome() {
        try {
            screenControl.show(patientDataTable, "/scene/clinicianHome.fxml");
        } catch (IOException e) {
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician home").show();
            userActions.log(SEVERE, "Failed to load clinician home", "Attempted to load clinician home");
        }
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        patientDataTable.refresh();
    }
}
