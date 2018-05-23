package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.DrugInteraction;
import service.Database;
import service.OrganWaitlist;
import utility.GlobalEnums.Organ;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import utility.undoRedo.UndoableStage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.logging.Level;

import static java.util.logging.Level.SEVERE;
import static utility.UserActionHistory.userActions;

/**
 * Controller class to manage organ waiting list for patients who require an organ.
 */
public class GUIClinicianWaitingList {

    public AnchorPane clinicianWaitingListAnchorPane;
    public TableView<OrganWaitlist.OrganRequest> waitingListTableView;
    public TableColumn<OrganWaitlist.OrganRequest, String> dateCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> nameCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> organCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> regionCol;

    private ObservableList<OrganWaitlist.OrganRequest> openProfiles = FXCollections.observableArrayList();
    private ObservableList<OrganWaitlist.OrganRequest> masterData = FXCollections.observableArrayList();

    private UserControl userControl;

    private ScreenControl screenControl = ScreenControl.getScreenControl();

    /**
     * Initializes waiting list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void initialize() {
    	OrganWaitlist waitingList = Database.getWaitingList();
        for (OrganWaitlist.OrganRequest request: waitingList) {
    		masterData.add(request);
    	}
        populateTable();
    	setupDoubleClickToPatientEdit();
    }

    /**
     * Closes an opened profile, and removes patient from profile open list so profile can be reopened
     * @param index The index in the list of opened patient profiles
     */
    private void closeProfile(int index) {
        Platform.runLater(this::tableRefresh);
        //openProfiles.remove( index );
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update, ensures no duplicate profiles
     */
    private void setupDoubleClickToPatientEdit() {

        // Add double-click event to rows
        waitingListTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && waitingListTableView.getSelectionModel()
                    .getSelectedItem() != null && !openProfiles.contains(waitingListTableView.getSelectionModel()
                    .getSelectedItem())) {
                try {
                    userControl = new UserControl();
                    OrganWaitlist.OrganRequest request = waitingListTableView.getSelectionModel().getSelectedItem();
                    DrugInteraction.setViewedPatient(Database.getPatientByNhi(request.getReceiverNhi()));
                    userControl.setTargetPatient(Database.getPatientByNhi(request.getReceiverNhi()));
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    Parent root = fxmlLoader.load();
                    UndoableStage popUpStage = new UndoableStage();
                    screenControl.addStage(popUpStage.getUUID(), popUpStage);
                    screenControl.show(popUpStage.getUUID(), root);

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> closeProfile(openProfiles.indexOf( request )));
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
     * Populates waiting list table with all patients waiting to receive an organ
     */
    public void populateTable() {
        // initialize columns
        nameCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getReceiverName()));
        dateCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
        		.getRequestDate().toString()));
        organCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getRequestedOrgan().toString()));
        regionCol.setCellValueFactory(r -> {
            if(r.getValue().getRequestRegion() != null) {
                return new SimpleStringProperty(r.getValue()
                        .getRequestRegion().toString());
            }
            return null;
        });

        // wrap ObservableList in a FilteredList
        FilteredList<OrganWaitlist.OrganRequest> filteredData = new FilteredList<>(masterData, d -> true);

        // wrap the FilteredList in a SortedList.
        SortedList<OrganWaitlist.OrganRequest> sortedData = new SortedList<>(filteredData);

        // bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(waitingListTableView.comparatorProperty());

        // add sorted (and filtered) data to the table.
        waitingListTableView.setItems(sortedData);

    }
    
    /**
     * Returns the user to the clinician home page
     */
    public void goToClinicianHome() {
        try {
        screenControl.show(clinicianWaitingListAnchorPane, "/scene/clinicianHome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert((Alert.AlertType.ERROR), "Unable to load clinician home").show();
            userActions.log(SEVERE, "Failed to load clinician home", "Attempted to load clinician home");
        }
    }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        waitingListTableView.refresh();
    }

}
