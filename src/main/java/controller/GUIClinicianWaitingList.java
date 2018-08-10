package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.DrugInteraction;
import model.User;
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

import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

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

    private ObservableList<OrganWaitlist.OrganRequest> openProfiles = FXCollections.observableArrayList();
    private ObservableList<OrganWaitlist.OrganRequest> masterData = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> organSelection;

    @FXML
    private ComboBox<String> regionSelection;

    /**
     * Initializes waiting list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void load() {
        OrganWaitlist waitingList = Database.getWaitingList();
        for (OrganWaitlist.OrganRequest request : waitingList) {
            masterData.add(request);
        }
        populateTable();
        setupDoubleClickToPatientEdit();
        populateFilterChoiceBoxes();
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
                    .getSelectedItem() != null && !openProfiles.contains(waitingListTableView.getSelectionModel()
                    .getSelectedItem())) {
                try {
                    OrganWaitlist.OrganRequest request = waitingListTableView.getSelectionModel().getSelectedItem();
                    DrugInteraction.setViewedPatient(Database.getPatientByNhi(request.getReceiverNhi()));
                    User selectedUser = Database.getPatientByNhi(request.getReceiverNhi());
                    GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, this, selectedUser);
                    controller.setTarget(selectedUser);
                    openProfiles.add(request);
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
        SortedList<OrganWaitlist.OrganRequest> sortedData = new SortedList<>(filteredData);

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
            if (newValue.equals("")) {
                if (regionSelection.getValue() == null || regionSelection.getValue().equals("")) { //check if region selection is null or ""
                    return true;
                } else if (OrganRequest.getRequestRegion() == null) { //if region is not given in donor
                    return false;
                } else if (OrganRequest.getRequestRegion().getValue().equals(regionSelection.getValue())) {
                    return true;
                }
            }
            if (OrganRequest.getRequestedOrgan().getValue().toLowerCase().equals(newValue.toLowerCase())) {
                if (regionSelection.getValue() == null || regionSelection.getValue().equals("")) {
                    return true;
                } else if (OrganRequest.getRequestRegion() != null) {
                    return OrganRequest.getRequestRegion().getValue().toLowerCase().equals(regionSelection.getValue().toString().toLowerCase());
                }
            }
            return false;
        }));

        //add listener to organ choice box and add predicate
        regionSelection.valueProperty().addListener((organ, value, newValue) -> filteredData.setPredicate(OrganRequest -> {
            if (newValue.equals("")) {
                if (organSelection.getValue() == null ||
                        OrganRequest.getRequestedOrgan().getValue().toLowerCase().equals(organSelection.getValue().toLowerCase()) ||
                        organSelection.getValue().equals("")) {
                    return true;
                }
            }
            Region requestedRegion = OrganRequest.getRequestRegion();
            if (requestedRegion != null) {
                return requestedRegion.getValue().toLowerCase().equals(newValue.toLowerCase()) &&
                        (organSelection.getValue() == null || organSelection.getValue().equals("") ||
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

}
