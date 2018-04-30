package controller;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class GUIClinicianWaitingList {

    public AnchorPane clinicianWaitingListAnchorPane;
    public TableView waitingListTableView;
    public TableColumn dateCol;
    public TableColumn nameCol;
    public TableColumn organCol;
    public TableColumn regionCol;

    /**
     * Initializes waiting list screen by populatinf table and initializing a double click action
     * to view a patient's profile.
     */
    public void initialize() {
        populateTable();
        // TODO: 30/04/2018 add double click function to view donor 
    }

    /**
     * Populates waiting list table with all patients waiting to receive an organ
     */
    public void populateTable() {
        
    }
    
    /**
     * Returns the user to the clinician home page
     */
    public void goToClinicianHome() { ScreenControl.activate("clinicianHome"); }

}
