package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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

import java.io.InvalidObjectException;
import java.util.logging.Level;

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

    /**
     * Initializes waiting list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void initialize() throws InvalidObjectException {
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
        openProfiles.remove( index );
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
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    GUIPatientProfile controller = fxmlLoader.getController();
                    OrganWaitlist.OrganRequest request = waitingListTableView.getSelectionModel().getSelectedItem();
                    controller.setViewedPatient(Database.getPatientByNhi(request.getReceiverNhi()));
                    DrugInteraction.setViewedPatient(Database.getPatientByNhi(request.getReceiverNhi()));
                    Stage popUpStage = new Stage();
                    popUpStage.setX(ScreenControl.getMain()
                            .getX()); //offset popup
                    popUpStage.setScene(scene);
                    openProfiles.add(request);  // add the patient to a list so its profile can be opened once at a time

                    // When pop up is closed, refresh the table
                    popUpStage.setOnHiding(event -> closeProfile(openProfiles.indexOf( request )));

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
        regionCol.setCellValueFactory(r -> new SimpleStringProperty(r.getValue()
                .getRequestRegion().toString()));

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
    public void goToClinicianHome() { ScreenControl.activate("clinicianHome"); }

    /**
     * Refreshes the table data
     */
    private void tableRefresh() {
        waitingListTableView.refresh();
    }

}
