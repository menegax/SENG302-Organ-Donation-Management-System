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
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.io.InvalidObjectException;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;

public class GUIClinicianWaitingList {

    public AnchorPane clinicianWaitingListAnchorPane;
    public TableView<OrganWaitlist.OrganRequest> waitingListTableView;
    public TableColumn<OrganWaitlist.OrganRequest, String> dateCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> nameCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> organCol;
    public TableColumn<OrganWaitlist.OrganRequest, String> regionCol;
    
    private ObservableList<OrganWaitlist.OrganRequest> masterData = FXCollections.observableArrayList();

    /**
     * Initializes waiting list screen by populating table and initializing a double click action
     * to view a patient's profile.
     */
    public void initialize() throws InvalidObjectException {
    	OrganWaitlist waitingList = Database.getWaitingList();

        // TODO: 10/05/2018 remove test data after story 23 merged to development
    	waitingList.add(Database.getPatientByNhi("ABC1238"), Organ.LIVER);
        waitingList.add(Database.getPatientByNhi("ABC1238"), Organ.KIDNEY);
        waitingList.add(Database.getPatientByNhi("ABC1234"), Organ.CORNEA);
        waitingList.add(Database.getPatientByNhi("ABC1234"), Organ.KIDNEY);

        for (OrganWaitlist.OrganRequest request: waitingList) {
    		masterData.add(request);
    	}
        populateTable();
    	setupDoubleClickToPatientEdit();
    }

    /**
     * Sets up double-click functionality for each row to open a patient profile update
     */
    private void setupDoubleClickToPatientEdit() {

        // Add double-click event to rows
        waitingListTableView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && waitingListTableView.getSelectionModel()
                    .getSelectedItem() != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/patientProfile.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    GUIPatientProfile controller = fxmlLoader.getController();
                    controller.setViewedPatient(Database.getPatientByNhi(waitingListTableView.getSelectionModel()
                            .getSelectedItem().getReceiverNhi()));
                    DrugInteraction.setViewedPatient(Database.getPatientByNhi(waitingListTableView.getSelectionModel()
                            .getSelectedItem().getReceiverNhi()));
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
