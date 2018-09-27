package controller;

import static utility.UserActionHistory.userActions;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import service.ClinicianDataService;
import service.OrganWaitlist;
import service.PatientDataService;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;
import utility.TouchComboBoxSkin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIClinicianWaitingList extends TargetedController implements IWindowObserver{

    public GridPane clinicianWaitingList;
    public TableView<OrganWaitlist.OrganRequest> waitingListTableView;
    public TableColumn<OrganWaitlist.OrganRequest, String> dateCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> nameCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> organCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> regionCol;

    private ObservableList<OrganWaitlist.OrganRequest> masterData = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> organSelection;

    @FXML
    private ComboBox<String> regionSelection;

    private PatientDataService patientDataService = new PatientDataService();

    private SortedList<OrganWaitlist.OrganRequest> sortedData;

    /**
     * Initializes waiting list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void loadController() {
        ClinicianDataService clinicianDataService = new ClinicianDataService();
        OrganWaitlist organRequests = clinicianDataService.getOrganWaitList();
        for (OrganWaitlist.OrganRequest request: organRequests) {
            if (request.getReceiver().getDeathDate() == null) {
                masterData.add(request);
            }
    	}
        populateTable();
        setupDoubleClickToPatientEdit();
        populateFilterChoiceBoxes();
        if (screenControl.isTouch()) {
            new TouchComboBoxSkin(organSelection, (Pane) screenControl.getTouchParent(clinicianWaitingList));
            new TouchComboBoxSkin(regionSelection, (Pane) screenControl.getTouchParent(clinicianWaitingList));
        }
    }


    /**
     * Populates the choice boxes for filter
     */
    private void populateFilterChoiceBoxes() {
        regionSelection.getItems().add(GlobalEnums.NONE_ID); //for empty selection
        for (Region region : Region.values()) { //add values to region choice box
            regionSelection.getItems().add(StringUtils.capitalize(region.getValue()));
        }
        organSelection.getItems().add(GlobalEnums.NONE_ID);
        for (Organ organ : Organ.values()) {
            organSelection.getItems().add(StringUtils.capitalize(organ.getValue()));
        }
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update, ensures no duplicate profiles
     */
    private void setupDoubleClickToPatientEdit() {
        ScreenControl screenControl = ScreenControl.getScreenControl();
        // Add double-click event to rows
        waitingListTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && waitingListTableView.getSelectionModel()
                    .getSelectedItem() != null) {
                    OrganWaitlist.OrganRequest request = waitingListTableView.getSelectionModel().getSelectedItem();
                    try {
                        Patient selectedUser = patientDataService.getPatientByNhi(request.getReceiverNhi());
                        patientDataService.save(selectedUser);
                        Parent parent = screenControl.getTouchParent(clinicianWaitingList);
                        GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selectedUser, parent);
                        controller.setTarget(selectedUser);
                    } catch (Exception e) {
                        userActions.log(Level.SEVERE, "Failed to retrieve selected patient from database", new String[]{"Attempted to retrieve selected patient from database", request.getReceiverNhi()});
                    }
            }
        });
    }

    /**
     * Called when a profile window created by this controller is closed
     */
    public void windowClosed() {
        tableRefresh();
    }

    /**
     * Populates waiting list table with all patients waiting to receive an organ
     */
    private void populateTable() {
        // initialize columns
        nameCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getReceiverName()));
        dateCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getRequestDate().toString()));
        organCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getRequestedOrgan().toString()));
        regionCol.setCellValueFactory(r -> {
            if (r.getValue().getRequestRegion() != null) {
                return new SimpleStringProperty(r.getValue()
                        .getRequestRegion().toString());
            }
            return null;
        });

        // wrap ObservableList in a FilteredList
        FilteredList<OrganWaitlist.OrganRequest> filteredData = filterMasterData();

        // wrap the FilteredList in a SortedList.
        sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(waitingListTableView.comparatorProperty());

        // add sorted (and filtered) data to the table.
        waitingListTableView.setItems(sortedData);

    }

    /**
     * Create and add predicates to filterList to filter master data
     *
     * @return - filter list containing data that is filtered based on selections
     */
    private FilteredList<OrganWaitlist.OrganRequest> filterMasterData() {
        FilteredList<OrganWaitlist.OrganRequest> filteredData = new FilteredList<>(masterData, d -> true);

        //add listener to organ choice box and add predicate
        organSelection.valueProperty().addListener((organ, value, newValue) -> filteredData.setPredicate(OrganRequest -> {

            if (newValue.equals(GlobalEnums.NONE_ID)) {
                if (regionSelection.getValue() == null || regionSelection.getValue().equals(GlobalEnums.NONE_ID)) { //check if region selection is null or ""
                    return true;
                } else if (OrganRequest.getRequestRegion() == null) { //if region is not given in donor
                    return false;
                } else if (OrganRequest.getRequestRegion().getValue().equals(regionSelection.getValue())) {
                    return true;
                }
            }
            if (OrganRequest.getRequestedOrgan().getValue().toLowerCase().equals(newValue.toLowerCase())) {
                if (regionSelection.getValue() == null || regionSelection.getValue().equals(GlobalEnums.NONE_ID)) {
                    return true;
                } else if (OrganRequest.getRequestRegion() != null) {
                    return OrganRequest.getRequestRegion().getValue().toLowerCase().equals(regionSelection.getValue().toLowerCase());
                }
            }
            return false;
        }));

        //add listener to organ choice box and add predicate
        regionSelection.valueProperty().addListener((organ, value, newValue) -> filteredData.setPredicate(OrganRequest -> {
            if (newValue.equals(GlobalEnums.NONE_ID)) {
                if (organSelection.getValue() == null ||
                        OrganRequest.getRequestedOrgan().getValue().toLowerCase().equals(organSelection.getValue().toLowerCase()) ||
                        organSelection.getValue().equals(GlobalEnums.NONE_ID)) {
                    return true;
                }
            }
            Region requestedRegion = OrganRequest.getRequestRegion();
            if (requestedRegion != null) {
                return requestedRegion.getValue().toLowerCase().equals(newValue.toLowerCase()) &&
                        (organSelection.getValue() == null || organSelection.getValue().equals(GlobalEnums.NONE_ID) ||
                                OrganRequest.getRequestedOrgan().getValue().toLowerCase().equals(organSelection.getValue().toLowerCase()));
            }
            return false;
        }));

        return filteredData;
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        waitingListTableView.refresh();
    }

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    private UserControl userControl = UserControl.getUserControl();

    /**
     * View patients from table on the map
     * Sets the patients list in the JavaScript to custom set
     * Opens the map and loads
     */
    @FXML
    public void viewOnMap() {
        List<Patient> patients = new ArrayList<>();
        boolean found = false;
        for (OrganWaitlist.OrganRequest aMasterData : sortedData) {
            for (Patient patient : patients) {
                found = false;
                if (patient.getNhiNumber().equals(aMasterData.getReceiverNhi())) {
                    found = true;
                    break;

                }
            }
            if (!found) {
                patients.add(patientDataService.getPatientByNhi(aMasterData.getReceiverNhi()));
            }
        }

        Alert alert;
        if (screenControl.getMapOpen()) {
            alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to repopulate the map?", ButtonType.OK, ButtonType.NO);
            alert.show();
            alert.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
                populateMap(patients);
            });
        } else {
            screenControl.show("/scene/map.fxml", true, this, userControl.getLoggedInUser(), null);
            populateMap(patients);
        }
    }

    /**
     * Populates the map with the provided collection of patients
     * @param patients the patients to populate the map with
     */
    private void populateMap(Collection<Patient> patients) {
        screenControl.setIsCustomSetMap(true);
        screenControl.getMapController().setPatients(patients);
        screenControl.setMapOpen(true);
    }

    /**
     * Displays the matching criteria in an info window for the user to read
     */
    @FXML
    public void openInfoWindow() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Information");

        StringBuilder infoText = new StringBuilder();
        infoText.append("Patients must have requested an organ in their profile to appear in this list.\n");

        alert.setContentText(infoText.toString());

        alert.show();

    }
}
